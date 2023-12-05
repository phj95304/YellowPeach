package application.util;
/**
------------------------------------------------------
1.Ǯ �޴����� ��� ���� 
------------------------------------------------------
- �����ͺ��̽� "����"�̶�� �۾���ü�� ���� �ð��� ���ҽ��� �䱸�Ѵ�.
  ���� �ϴ� ����� Ŀ�ؼ��� �Ź� ������ ���� ���������ν� �ڿ��� ȿ������ �ش�ȭ �Ѵ�.

------------------------------------------------------
2.������ ��� �Ұ� !!
------------------------------------------------------
1.����̹� ��Ű�� ���� : private String _driver = "org.gjt.mm.mysql.Driver",
2.DB ����� ���� ����:  _url   = "jdbc:mysql://localhost:3306/study",
                                _user = "���̵�",
								_password = "�н�����";
------------------------------------------------------
3.���Ǵ� �޼���
------------------------------------------------------
getInstance() : Ŀ�ؼ� Ǯ ��ü�� ����
getConnection() : Ŀ�ؼ� ��ü ����
freeConnection() : ������ �°� �ڿ� �ݳ�
 */
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;

import application.Main;


public class PoolManager {

    private Vector connections = new Vector(10);
    
    /*
     *  "com.mysql.jdbc.Driver"
     *  "jdbc:mysql://113.198.86.190:3306/yellowpeach?useSSL=false"
     *  "root"
     *  "1234"
     */
    private String _driver = "com.mysql.jdbc.Driver";
    /*
    private String _url = "jdbc:mysql://113.198.86.190:3306/yellowpeach2?useSSL=false";
    private String _user = "root";
    private String _password = "1234";
    */
    private String _url, _user, _password;
    
    private boolean _traceOn = false;
    private boolean initialized = false;
    private int _openConnections = -1;
    private static PoolManager instance = null;

    public PoolManager() {
    	
    }

    public String get_url() {
		return _url;
	}
	public void set_url(String _url) {
		this._url = _url;
	}
	public String get_user() {
		return _user;
	}
	public void set_user(String _user) {
		this._user = _user;
	}
	public String get_password() {
		return _password;
	}
	public void set_password(String _password) {
		this._password = _password;
	}



	/** 
     1�ܰ�: Ŀ�ؼ� Ǯ ��ü�� ���ɴϴ�.
     */
    public static PoolManager getInstance() {
        if (instance == null) {
            synchronized (PoolManager.class) {
                if (instance == null) {
                    instance = new PoolManager();
                }                
            }
        }
        return instance;
    }

    public void setOpenConnectionCount(int count) {
        _openConnections = count;
    }


    public void setEnableTrace(boolean enable) {
        _traceOn = enable;
    }


    /** Returns a Vector of java.sql.Connection objects */
    public Vector getConnectionList() {
        return connections;
    }


    /** Opens specified "count" of connections and adds them to the existing pool */
    public synchronized void setInitOpenConnections(int count)
            throws SQLException {
        Connection c = null;
        ConnectionObject co = null;

        for (int i = 0; i < count; i++) {
            c = createConnection();
            co = new ConnectionObject(c, false);

            connections.addElement(co);
            trace("ConnectionPoolManager: Adding new DB connection to pool (" + connections.size() + ")");
        }
    }


    /** Returns a count of open connections */
    public int getConnectionCount() {
        return connections.size();
    }


    /** Returns an unused existing or new connection. 
    2�ܰ�: Ŀ�ؼ� ��ü�� ���ɴϴ�. 
    */
    public synchronized Connection getConnection()
            throws Exception {
        if (!initialized) {
            Class c = Class.forName(_driver);
            DriverManager.registerDriver((Driver) c.newInstance());

            initialized = true;
        }


        Connection c = null;
        ConnectionObject co = null;
        boolean badConnection = false;


        for (int i = 0; i < connections.size(); i++) {
            co = (ConnectionObject) connections.elementAt(i);

            // If connection is not in use, test to ensure it's still valid!
            if (!co.inUse) {
                try {
                    badConnection = co.connection.isClosed();
                    if (!badConnection)
                        badConnection = (co.connection.getWarnings() != null);
                } catch (Exception e) {
                    badConnection = true;
                    e.printStackTrace();
                }

                // Connection is bad, remove from pool
                if (badConnection) {
                    connections.removeElementAt(i);
                    trace("ConnectionPoolManager: Remove disconnected DB connection #" + i);
                    continue;
                }

                c = co.connection;
                co.inUse = true;

                trace("ConnectionPoolManager: Using existing DB connection #" + (i + 1));
                break;
            }
        }

        if (c == null) {
            c = createConnection();
            co = new ConnectionObject(c, true);
            connections.addElement(co);

            trace("ConnectionPoolManager: Creating new DB connection #" + connections.size());
        }

        return c;
    }


    /** Marks a flag in the ConnectionObject to indicate this connection is no longer in use 
     3�ܰ� : ������ �°� �ڿ��� �ݳ��մϴ�. 
     */
    public synchronized void freeConnection(Connection c) {
        if (c == null)
            return;

        ConnectionObject co = null;

        for (int i = 0; i < connections.size(); i++) {
            co = (ConnectionObject) connections.elementAt(i);
            if (c == co.connection) {
                co.inUse = false;
                break;
            }
        }

        for (int i = 0; i < connections.size(); i++) {
            co = (ConnectionObject) connections.elementAt(i);
            if ((i + 1) > _openConnections && !co.inUse)
                removeConnection(co.connection);
        }
    }

    public void freeConnection(Connection c, PreparedStatement p, ResultSet r) {
        try {
            if (r != null) r.close();
            if (p != null) p.close();
            freeConnection(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Connection c, Statement s, ResultSet r) {
        try {
            if (r != null) r.close();
            if (s != null) s.close();
            freeConnection(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Connection c, PreparedStatement p) {
        try {
            if (p != null) p.close();
            freeConnection(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Connection c, Statement s) {
        try {
            if (s != null) s.close();
            freeConnection(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /** Marks a flag in the ConnectionObject to indicate this connection is no longer in use */
    public synchronized void removeConnection(Connection c) {
        if (c == null)
            return;

        ConnectionObject co = null;
        for (int i = 0; i < connections.size(); i++) {
            co = (ConnectionObject) connections.elementAt(i);
            if (c == co.connection) {
                try {
                    c.close();
                    connections.removeElementAt(i);
                    trace("Removed " + c.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }


    public Connection createConnection()
            throws SQLException {
        Connection con = null;

        try {
            if (_user == null)
                _user = "";
            if (_password == null)
                _password = "";

            Properties props = new Properties();
            props.put("user", _user);
            props.put("password", _password);
            
            con = DriverManager.getConnection(_url, props);
            Main.isDbConnect = true;
        } catch (Exception e) {
			System.out.println("�������");
			Main.isDbConnect = false;
			//con.close();
		}
        return con;
    }


    /** Closes all connections and clears out the connection pool */
    public void releaseFreeConnections() {
        trace("ConnectionPoolManager.releaseFreeConnections()");

        Connection c = null;
        ConnectionObject co = null;

        for (int i = 0; i < connections.size(); i++) {
            co = (ConnectionObject) connections.elementAt(i);
            if (!co.inUse)
                removeConnection(co.connection);
        }
    }


    /** Closes all connections and clears out the connection pool */
    public void finalize() {
        trace("ConnectionPoolManager.finalize()");

        Connection c = null;
        ConnectionObject co = null;

        for (int i = 0; i < connections.size(); i++) {
            co = (ConnectionObject) connections.elementAt(i);
            try {
                co.connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            co = null;
        }

        connections.removeAllElements();
    }


    private void trace(String s) {
        if (_traceOn)
            System.err.println(s);
    }

}


class ConnectionObject {
    public java.sql.Connection connection = null;
    public boolean inUse = false;

    public ConnectionObject(Connection c, boolean useFlag) {
        connection = c;
        inUse = useFlag;
    }

}
