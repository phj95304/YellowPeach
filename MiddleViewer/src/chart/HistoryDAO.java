package chart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import pool.PoolManager;


public class HistoryDAO {
	PoolManager pool;

	public HistoryDAO() {
		pool = PoolManager.getInstance();
	}

	//get topic list from DB 
	 public ArrayList<String> getTopicList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSetMetaData rsmd = null;
		ResultSet rs = null;
		String query = null;
		String sName = null;
		ArrayList<String> topicList = null;
		
		try {
			conn = pool.getConnection();
			if (conn != null) {
				System.out.println("DB connection success");
			}
			if (conn == null) {
				System.out.println("conn == null");
			}

			query = "select * from Topic";//query

			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			rsmd= rs.getMetaData();
			int columnNum = rsmd.getColumnCount();//the number of Topic
			topicList = new ArrayList<String>();// topic ArrayList
			
			for (int i=0; i< columnNum; i++) {
				
				String dto = new String();			
				if(rsmd.getColumnName(i+1).toString().equals("idx")){
					continue;
				}
				System.out.println(rsmd.getColumnName(i+1));
				topicList.add(rsmd.getColumnName(i+1));
		
			}
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("����");
			// e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}

		return topicList;

	} 
	 
	 public ArrayList<HistoryDTO> getHistory(String sensorlist) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String query = null;
			HistoryDTO dto = null;
			
			ArrayList<HistoryDTO> historyList = new ArrayList<HistoryDTO>();

			try {
				conn = pool.getConnection();
				if (conn != null) {
					System.out.println("connection ");
				}
				if (conn == null) {
					System.out.println("conn == null");
				}

				query = "select * from Topic";//
				pstmt = conn.prepareStatement(query);
				rs = pstmt.executeQuery();
				
				while (rs.next()) {
					String rData[] = null;//raw 
					String tTime = null;//시간
					String sData = null;//데이터
					dto = new HistoryDTO();
					
					rData = rs.getString(sensorlist).split(";");
					tTime = rData[0].replace(".", "-")+";"+rData[1].replace(".", "-");//시간
					sData = rData[2];//데이터
					
					
					dto.setTopicName(sensorlist);
					dto.setsData(sData);
					dto.setsTime(tTime);
					historyList.add(dto);
				
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("error");
				// e.printStackTrace();
			} finally {
				pool.freeConnection(conn, pstmt, rs);

			}

			return historyList;
		}
	 
	 
	/* 
	public static void main(String[] args) { 
		 HistoryDAO a = new HistoryDAO();
		 HistoryDTO t = new HistoryDTO();
		 ArrayList<String> b = new HistoryDAO().getTopicList();
		 System.out.println(b);
		 ArrayList<HistoryDTO> hi = new HistoryDAO().getHistory("SmartHome_miraeb1_dust");
		 int size = hi.size();
		 for(int i=0; i<size; i++) {
			 System.out.println(hi.get(i).getTopicName());
			 System.out.println(hi.get(i).getsTime());
			 System.out.println(hi.get(i).getsData());
		 }
		
	}*/

}

