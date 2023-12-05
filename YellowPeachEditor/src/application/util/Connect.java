package application.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.spec.RSAKeyGenParameterSpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.plaf.synth.SynthSplitPaneUI;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import application.Main;
import application.model.SystemInfo;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Connect {
	private PoolManager pool;
	private String host;
	private String clientId;

	public Connect() {
		pool = PoolManager.getInstance();
	}

	public PoolManager getPool() {
		return pool;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	/**
	 *  IoT시스템리스트 관련 명령어 get, insert, update, isExist
	 */
	// IoT시스템 리스트 테이블 검색
	public HashMap<String, Boolean> getIotSystemList(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HashMap<String, Boolean> data = new HashMap<String, Boolean>();
		
		try {
			conn = pool.getConnection();
			String sql = "select * from IoTSystemlist";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				data.put(rs.getString("name"), rs.getBoolean("isVirtual"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return data;
	}
	
	// IoT시스템 리스트 테이블 삽입
	public void insertIoTSystemList(String name, boolean isVirtual) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			conn = pool.getConnection();
			System.out.println("insert iotsystem");

			String sql = "insert into " + "IoTSystemlist" + " (name, isVirtual) values(?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setBoolean(2, isVirtual);

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.out.println("pstmt not closed");
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					/* ignore close errors */ }
			}
		}
	}
	
	public int updateIoTSystemList(String systemName, Boolean isVirtual) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = pool.getConnection();
			String sql = "update " + "IoTSystemlist" + " set isVirtual=? where name = '" + systemName + "'";

			pstmt = conn.prepareStatement(sql);
			pstmt.setBoolean(1, isVirtual);

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
		return result;
	}
	
	public boolean isIoTSystemExist(String systemName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			conn = pool.getConnection();
			String sql = "select * from " + "IoTSystemlist" + " where name = '" + systemName + "'";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				result = true;
			else
				result = false;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return result;
	}
	
	public Vector<String> getRealIoTSystemList(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<String> list = new Vector<String>();
		try {
			conn = pool.getConnection();
			String sql = "select * from "+ "IoTSystemlist where isVirtual = false";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				list.add(name);
				//System.out.println(name);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return list;
	}
	/**
	 * 토픽 관련 명령어들 isExistTable, create Table, insert vector, getAsc, search, update
	 * dropTable, get, insert one, isExistTopic, getCtrTopics
	 */
	public boolean isExistTable(String tableName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			conn = pool.getConnection();
			String sql = "SHOW TABLES LIKE '" + tableName + "'";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				result = true;
			else
				result = false;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return result;
	}
	public void createIoTTopicTable(String systemName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result;
		try {
			conn = pool.getConnection();
			String sql = "create table " + systemName + " (idx int AUTO_INCREMENT, topic nvarchar(100), "
					+ "ctrTopic nvarchar(100), constraint pk_topic primary key(topic))";
			pstmt = conn.prepareStatement(sql);
			result = pstmt.executeUpdate();
			//System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
	}
	
	public void insertTopicToIoTTable(String systemName, String column, Vector<String> topics) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			conn = pool.getConnection();
			System.out.println("insert IoTsystem Topics");
			
			for (int i = 0; i < topics.size(); i++) {
				System.out.println(topics.get(i));
				String sql = "insert into " + systemName + " (" + column + ") values(?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, topics.get(i));
				result = pstmt.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.out.println("pstmt not closed");
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					/* ignore close errors */ }
			}
		}
	}
	
	public void dropIoTTopicTable(String systemName) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = pool.getConnection();
			stmt = conn.createStatement();
			String sql = "DROP TABLE " + systemName;
			stmt.executeUpdate(sql);
			System.out.println("drop table complete!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, stmt);
		}
	}
	
	public Vector<Integer> getAsc(String systemName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<Integer> list = new Vector<Integer>();
		try {
			conn = pool.getConnection();
			String sql = "select * from "+ systemName +" order by idx asc";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				list.add(rs.getInt("idx"));
			}
			//System.out.println(list);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return list;
	}
	
	public void updateTopic(String systemName, String columnName, Vector<String> topics, Vector<Integer> asc) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			conn = pool.getConnection();
			for(int i = 0; i < topics.size(); i++) {
				String sql = "update "+ systemName +" set " + columnName + "=? where idx=" + asc.get(i);
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, topics.get(i));
				result = pstmt.executeUpdate();	
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
	}
	
	public Vector<String> getTopicList(String systemName){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<String> topics = new Vector<String>();
		try {
			conn = pool.getConnection();
			String sql = "select * from "+ systemName;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String t = rs.getString("topic");
				topics.add(t);
			}

		} catch (SQLException e) {
			System.out.println(systemName+" 테이블이 존재하지 않습니다.");
			//e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return topics;
	}
	
	public Vector<String> getCtrTopicList(String systemName){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<String> topics = new Vector<String>();
		try {
			conn = pool.getConnection();
			String sql = "select * from "+ systemName;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String t = rs.getString("ctrTopic");
				topics.add(t);
			}

		} catch (SQLException e) {
			System.out.println(systemName+" 테이블이 존재하지 않습니다.");
			//e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return topics;
	}
	
	// insert topic
	public boolean insertTopic(String table, String topic, String ctrTopic) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		int result = 0;

		try {
			conn = pool.getConnection();
			System.out.println("Database connection established");

			String sql = "insert into "+ table +" (topic, ctrTopic) values(?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, topic);
			pstmt.setString(2, ctrTopic);

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			return false;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.out.println("pstmt not closed");
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					/* ignore close errors */ }
			}
		}
		return true;
	}
	
	public boolean isTopicExist(String table, String topic) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			conn = pool.getConnection();
			String sql = "select * from "+ table +" where topic = '" + topic + "'";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				result = true;
			else
				result = false;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return result;
	}
	
	// topic과 일대일 매칭시킨 control topic 반환
	public Vector<String> getCtrTopics(Vector<String> topics) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<String> ctrTopics = new Vector<String>();
		try {
			conn = pool.getConnection();
			for (int i = 0; i < topics.size(); i++) {
				String to = topics.get(i);
				String sql = "select * from topicList where topic = '" + to + "'";
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				
				if (rs.next()) {
					ctrTopics.add(rs.getString("ctrtopic"));
				}
			}
			if(ctrTopics == null)
				return null;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return ctrTopics;
	}
	
	/**
	 * 대시보드 관련 명령어 get, insert, update, isExist
	 */
	// 데이터베이스에 있는 대시보드json가져오기
	public Vector<JSONObject> getDashboard(String version){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String versionTable = null;
		Vector<JSONObject> dashboardVector = new Vector<JSONObject>();
		
		if (version.equals("PC"))
			versionTable = "Json_pc";
		else
			versionTable = "Json_mobile";
		
		JSONParser parser = new JSONParser();
		try {
			conn = pool.getConnection();
			String sql = "select * from "+ versionTable;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				Object obj = parser.parse(rs.getString("content"));
    			JSONObject jsonObject = (JSONObject) obj;
				dashboardVector.add(jsonObject);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return dashboardVector;
	}

	// insert json file
	public void insertDashboard(String version, String dashBoardName, JSONObject obj, boolean isVirtual, String systemName) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		int result = 0;
		int num = 0;

		String versionTable;
		if (version.equals("PC"))
			versionTable = "Json_pc";
		else
			versionTable = "Json_mobile";
		
		try {
			conn = pool.getConnection();
			System.out.println("Database connection established");

			String sql = "insert into " + versionTable + " (name, content, isVirtual, IoTSysName, available) values(?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dashBoardName);
			pstmt.setString(2, obj.toString());
			pstmt.setBoolean(3, isVirtual);
			pstmt.setString(4, systemName);
			pstmt.setInt(5, 1);

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.out.println("pstmt not closed");
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					/* ignore close errors */ }
			}
		}
	}
	
	public int updateDashboard(String version, String dashboardName, JSONObject obj, boolean isVirtual, String systemName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		String versionTable;
		if (version.equals("PC"))
			versionTable = "Json_pc";
		else
			versionTable = "Json_mobile";

		try {
			conn = pool.getConnection();
			String sql = "update " + versionTable + " set content=? where name = '" + dashboardName + "'";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, obj.toString());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
		return result;
	}
	
	public boolean isDashBoardExist(String version, String dashboardName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;

		String versionTable;
		if (version.equals("PC"))
			versionTable = "Json_pc";
		else if(version.equals("Mobile"))
			versionTable = "Json_mobile";
		else
			versionTable = "Json_virtual";
		
		try {
			conn = pool.getConnection();
			String sql = "select * from " + versionTable + " where name = '" + dashboardName + "'";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				result = true;
			else
				result = false;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return result;
	}

	// 대시보드 테이블의 이름만 가져오기
	public Vector<String> getJsonNames(String version){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String versionTable = null;
		Vector<String> names = new Vector<String>();
		
		if (version.equals("PC"))
			versionTable = "Json_pc";
		else if(version.equals("Mobile"))
			versionTable = "Json_mobile";
		else
			versionTable = "Json_virtual";
			
		try {
			conn = pool.getConnection();
			String sql = "select * from "+ versionTable;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				String str = rs.getString("name");
    			names.add(str);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return names;
	}
	
	// 대시보드 이름으로 jsonData 한개 가져오기
	public JSONObject getJson(String version, String name) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String versionTable = null;
		JSONObject jsonObject = null;
		
		if (version.equals("PC"))
			versionTable = "Json_pc";
		else if(version.equals("Mobile"))
			versionTable = "Json_mobile";
		else
			versionTable = "Json_virtual";
		
		JSONParser parser = new JSONParser();
		try {
			conn = pool.getConnection();
			String sql = "select * from "+ versionTable + " where name = '" + name + "'";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Object obj = parser.parse(rs.getString("content"));
				jsonObject = (JSONObject) obj;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return jsonObject;
	}
	
	/**
	 * 에뮬레이터 관련 명령어 getSystem, insertSystem, updateSystem, isExist, createTable, insertTopic, dropTable
	 */
	// 데이터베이스에 있는 에뮬레이터 시스템 json가져오기
	public Vector<JSONObject> getEmulatorSystem(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String versionTable = null;
		Vector<JSONObject> emulatorVector = new Vector<JSONObject>();
		
		JSONParser parser = new JSONParser();
		try {
			conn = pool.getConnection();
			String sql = "select * from "+ "Json_virtual";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				Object obj = parser.parse(rs.getString("content"));
    			JSONObject jsonObject = (JSONObject) obj;
    			emulatorVector.add(jsonObject);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return emulatorVector;
	}
	
	// insert Emulator System json file
	public void insertEmulatorSystem(String emulatorSystemName, JSONObject obj) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		int result = 0;
		int num = 0;

		try {
			conn = pool.getConnection();
			System.out.println("Database connection established");

			String sql = "insert into " + "Json_virtual" + " (name, content) values(?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, emulatorSystemName);
			pstmt.setString(2, obj.toString());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.out.println("pstmt not closed");
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					/* ignore close errors */ }
			}
		}
	}
	
	public int updateEmulatorSystem(String systemName, JSONObject obj) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = pool.getConnection();
			String sql = "update " + "Json_virtual" + " set content=? where name = '" + systemName + "'";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, obj.toString());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
		return result;
	}
	
	public boolean isEmulatorSystemExist(String systemName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			conn = pool.getConnection();
			String sql = "select * from " + "Json_virtual" + " where name = '" + systemName + "'";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				result = true;
			else
				result = false;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return result;
	}
	
	public void createEmulatorTable(String systemName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result;
		try {
			conn = pool.getConnection();
			String sql = "create table " + systemName + " (topic nvarchar(100), constraint pk_topic primary key(topic))";
			pstmt = conn.prepareStatement(sql);
			result = pstmt.executeUpdate();
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
	}
	
	public void insertTopicToEmulatorTable(String systemName, Vector<String> topics) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			conn = pool.getConnection();
			System.out.println("insert IoTsystem Topics");

			for (int i = 0; i < topics.size(); i++) {
				System.out.println(topics.get(i));
				String sql = "insert into " + systemName + " (topic) values(?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, topics.get(i));
				result = pstmt.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.out.println("pstmt not closed");
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					/* ignore close errors */ }
			}
		}
	}
	
	public void dropEmulatorTopicTable(String systemName) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = pool.getConnection();
			stmt = conn.createStatement();
			String sql = "drop table " + systemName;
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, stmt);
		}
	}

}
