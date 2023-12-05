package chart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import pool.PoolManager;

/* Data Access Object
 * 테이블 당 한개의 DAO를 작성한다.
 * 
 * JSP_MEMBER 테이블과 연관된 DAO로
 * 회원 데이터를 처리하는 클래스이다.
 */
public class HistoryDAO {
	PoolManager pool;

	public HistoryDAO() {
		pool = PoolManager.getInstance();
	}

	// 센서 토픽 리스트 데이터 가져오는 메소드
	public ArrayList<HistoryDTO> getTopicList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		String sName = null;
		ArrayList<HistoryDTO> topicList = null;
		//

		try {
			// System.out.println("conn");
			conn = pool.getConnection();
			if (conn != null) {
				System.out.println("데이터 베이스 접속 성공");
			}
			if (conn == null) {
				System.out.println("conn == null");
			}
			query = "select * from topicList";// 서버컴 테이블 이름으로 바꿀 것! + 센서 토픽 리스트의 테이블 이름

			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			topicList = new ArrayList<HistoryDTO>();

			while (rs.next()) {
				HistoryDTO dto = new HistoryDTO();
				dto.setTopicName(rs.getString("topicName"));// 토픽리스트
				sName = rs.getString("topicName");
				topicList.add(dto);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("오류");
			// e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);

		}

		return topicList;

	}

	// sensor 데이터 가져오는 메소드
	public ArrayList<HistoryDTO> getHistory(String sensorlist) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		String sTime = null;
		String sData = null;
		HistoryDTO dto = null;
		ArrayList<HistoryDTO> history = new ArrayList<HistoryDTO>();

		try {
			// System.out.println("conn");
			conn = pool.getConnection();
			if (conn != null) {
				System.out.println("데이터 베이스 접속 성공");
			}
			if (conn == null) {
				System.out.println("conn == null");
			}

			query = "select * from " + sensorlist;// 서버컴 테이블 이름으로 바꿀 것! + 센서 토픽 리스트의 테이블 이름
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				dto = new HistoryDTO();
				dto.setsDatetime(rs.getString("time"));// 센서 테이블 날짜
				dto.setsData(rs.getString("data"));// 센서 테이블 데이터

				sTime = dto.getsDatetime();
				sData = dto.getsData();
				history.add(dto);
				System.out.println(dto.getsData());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("오류");
			// e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);

		}

		return history;
	}

	/*
	 * public static void main(String[] args) { HistoryDAO a = new HistoryDAO();
	 * HistoryDTO t = new HistoryDTO(); a.getHi("HSFarm_mirae_cds"); }
	 */

}