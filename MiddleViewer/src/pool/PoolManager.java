package pool;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Driver;
import java.sql.DriverManager;
import javax.sql.DataSource;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.PreparedStatement;



public class PoolManager {
   //�����Ŀ��� �ٲܰ�
   //�Ʒ� DataSource db name
   private static final String DB_USERNAME="root";
   private static final String DB_PASSWORD="1234";
   private static final String DB_URL="jdbc:mysql://192.168.0.12:3306/yellowpeach?useSSL=true&verifyServerCertificate=false";//������ url�� �ٲٱ�
   //"jdbc:mysql://113.198.86.190:3306/yellowpeach?useSSL=true&verifyServerCertificate=false"
   //222 111 115 100
   private static final String DB_DRIVER_CLASS="com.mysql.jdbc.Driver";
   

   
   private static PoolManager instance = null;

   public static PoolManager getInstance() {
      if(instance == null) {
         synchronized (PoolManager.class) {
            if(instance == null) {
               instance = new PoolManager();
            }
         }
      }
      return instance;
   }
   
   public static Connection getConnection() throws Exception{
      Connection conn = null;
   /*   Context initContext = new InitialContext();
      Context envContext = (Context)initContext.lookup("javac:/comp/env");
      DataSource ds = (DataSource)envContext.lookup("jdbc/mydbtest");
      conn = ds.getConnection();
   */
      try {
         Properties props = new Properties();
         Class.forName(DB_DRIVER_CLASS);
         props.put("user",DB_USERNAME);
         props.put("password",DB_PASSWORD);
         conn = DriverManager.getConnection(DB_URL, props);
         System.out.println("DBCP connection");
      } catch (Exception e) {
            e.printStackTrace();
      }
   
      return conn;
      
   }

   public void freeConnection(Connection c, PreparedStatement p, ResultSet r) {
      try {
         if(r!=null)r.close();
         if(p!=null)p.close();
         if(c!=null)c.close();
         
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
}