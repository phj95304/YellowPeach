package chart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.tomcat.dbcp.dbcp2.PStmtKey;

import pool.PoolManager;
/* Data Access Object
 * 테이블 당 한개의 DAO를 작성한다.
 * 
 * JSP_MEMBER 테이블과 연관된 DAO로
 * 회원 데이터를 처리하는 클래스이다.
 */   
public class ChartDAO {
   PoolManager pool;
   Connection conn = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   String result="";
   String query =null;
   ChartDTO dto = null;
   
   public ChartDAO() {
      pool=PoolManager.getInstance();
   }   


/*
//JSON 데이터 가져오는 메소드
   public String getChart() {
      try {
    	 // System.out.println("conn");
         conn=pool.getConnection();
         if( conn != null ){ System.out.println("데이터 베이스 접속 성공"); }
         if(conn == null) {System.out.println("conn == null");}
         int no =9;
         query = "select * from testJson"; //서버컴 테이블 이름으로 바꿀 것!, 
         pstmt = conn.prepareStatement(query);
         rs=pstmt.executeQuery();
         
         while(rs.next()) {
            dto = new ChartDTO();
            dto.setJson(rs.getString("content"));
            //result=dto.getName();
            result+=dto.getJson();
            System.out.println(result);

         }
      } catch (SQLException e) {
          e.printStackTrace();
       } catch (Exception e) {
         System.out.println("오류 in getChart");
         //e.printStackTrace();
      }finally {
         pool.freeConnection(conn,pstmt,rs);
         
      }
      return result;
   }
   */
   
   //PC 대시보드 리스트 가져오도록 하는 부분
   public ChartDTO getPCDashBoard(int dashboardPcNumber) {
	   //id에 해당하는 대시보드를 가져오도록 하는 SQL구문
	   String SQL = "select * from Json_pc where id = ?";
	   try {
		   conn=pool.getConnection();
		   pstmt = conn.prepareStatement(SQL);
		   pstmt.setInt(1, dashboardPcNumber);
		   rs = pstmt.executeQuery(); 
		   if(rs.next()) {
			   ChartDTO chartDTO = new ChartDTO();
			   chartDTO.setJson(rs.getString("content"));
			   return chartDTO;
		   }
	   } catch (Exception e) {
		// TODO: handle exception
	   }
	   return null;
   }
   
   //Mobile 대시보드 리스트 가져오도록 하는 부분
   public ChartDTO getMobileDashBoard(int dashboardMobileNumber) {
	   //id에 해당하는 대시보드를 가져오도록 하는 SQL구문
	   String SQL = "select * from Json_mobile where id = ?";
	   try {
		   conn=pool.getConnection();
		   pstmt = conn.prepareStatement(SQL);
		   pstmt.setInt(1, dashboardMobileNumber);
		   rs = pstmt.executeQuery(); 
		   if(rs.next()) {
			   ChartDTO chartDTO = new ChartDTO();
			   chartDTO.setJson(rs.getString("content"));
			   return chartDTO;
		   }
	   } catch (Exception e) {
		// TODO: handle exception
	   }
	   return null;
   }
   
   public int getMobileNext() {
//order by id desc
	   try {
		   conn=pool.getConnection();
		   String SQL = "select * from Json_mobile order by id desc";
		   pstmt = conn.prepareStatement(SQL);
		   rs = pstmt.executeQuery();	
		   if(rs.next()) {
			   return rs.getInt(1) + 1;
		   }
		   return 1; // 첫번째 대시보드인 경우
	} catch (Exception e) {
		// TODO: handle exception
	}
	   return -1; //데이터베이스 오류
   }
   
   public int getPcNext() {
//order by id desc
	   try {
		   conn=pool.getConnection();
		   String SQL = "select * from Json_pc order by id desc";
		   pstmt = conn.prepareStatement(SQL);
		   rs = pstmt.executeQuery();	
		   if(rs.next()) {
			   return rs.getInt(1) + 1;
		   }
		   return 1; // 첫번째 대시보드인 경우
	} catch (Exception e) {
		// TODO: handle exception
	}
	   return -1; //데이터베이스 오류
   }
   
   //Mobile대시보드 리스트 가져오는 부분
   //특정한 페이지에 따른 총 5개의 대시보드를 가져올 수 있도록 한다
   public ArrayList<ChartDTO> getMobileList(int dashboardMobileNumber) { 
	   String SQL = "select * from Json_mobile where id < ? and available = 1 order by id desc limit 5"; 
	   //id가 특정한 숫자보다 작을 때 삭제가 되지 않아서 available이 1인 글들만 가져 올 수 있도록 
	   //id로 내림차순 정렬 위에서 10개 까지 가져온다
	   //int dashboardNumber = 10;
	   ArrayList<ChartDTO> list = new ArrayList<ChartDTO>();
	   try {
		   conn=pool.getConnection();
		   pstmt = conn.prepareStatement(SQL);
		   pstmt.setInt(1, getMobileNext() - (dashboardMobileNumber - 1)*5);
		   rs = pstmt.executeQuery();
		   
		   while(rs.next()) {
			   ChartDTO chartDTO = new ChartDTO();
			   chartDTO.setId(rs.getInt("id"));
			   chartDTO.setName(rs.getString("name"));
			   chartDTO.setAvailable(rs.getInt("available"));
			   list.add(chartDTO);
		   }
	   } catch (Exception e) {
		// TODO: handle exception
		   System.out.println("null");
	   }
	   System.out.println(list);
	   return list; // 5개 뽑아온 대시보드 리스트들을 리턴해준다
	   //실행했을 때 특정 페이지에 맞는 대시보드 리스트들이 리스트에 담겨서 반환되어 가져올 수 있다
   }
   
   //PC대시보드 리스트 가져오는 부분
   //특정한 페이지에 따른 총 5개의 대시보드를 가져올 수 있도록 한다
   public ArrayList<ChartDTO> getPCList(int dashboardPcNumber) { 
	   String SQL = "select * from Json_pc where id < ? and available = 1 order by id desc limit 5"; 
	   //id가 특정한 숫자보다 작을 때 삭제가 되지 않아서 available이 1인 글들만 가져 올 수 있도록 
	   //id로 내림차순 정렬 위에서 10개 까지 가져온다
	   //int dashboardNumber = 10;
	   ArrayList<ChartDTO> list = new ArrayList<ChartDTO>();
	   try {
		   conn=pool.getConnection();
		   pstmt = conn.prepareStatement(SQL);
		   pstmt.setInt(1, getPcNext() - (dashboardPcNumber - 1)*5);
		   rs = pstmt.executeQuery();
		   
		   while(rs.next()) {
			   ChartDTO chartDTO = new ChartDTO();
			   chartDTO.setId(rs.getInt("id"));
			   chartDTO.setName(rs.getString("name"));
			   chartDTO.setAvailable(rs.getInt("available"));
			   list.add(chartDTO);
		   }
	   } catch (Exception e) {
		// TODO: handle exception
		   System.out.println("null");
	   }
	   System.out.println(list);
	   return list; // 5개 뽑아온 대시보드 리스트들을 리턴해준다
	   //실행했을 때 특정 페이지에 맞는 대시보드 리스트들이 리스트에 담겨서 반환되어 가져올 수 있다
   }
   
   
   /*
   //대시보드 리스트들을 5개씩 끊어서 다음 화면 화살표가 나타나 지도록 하는 함수 구현
   //5개이하일 경우 nextPage없다는 것을 알려줌 -> 페이징 처리를 하기 위하여 구현
   public boolean nextPage(int dashboardNumber) {
	   String SQL = "select * from testJson where id < ? and available = 1 order by id desc limit 5"; 
	   //id가 특정한 숫자보다 작을 때 삭제가 되지 않아서 available이 1인 글들만 가져 올 수 있도록 
	   //id로 내림차순 정렬 위에서 5개 까지 가져온다
	   try {
		   pstmt = conn.prepareStatement(SQL);
		   pstmt.setInt(1, getNext() - (dashboardNumber - 1)*5);
		   rs = pstmt.executeQuery();
		   if(rs.next()) {
			   return true; //다음페이지로 넘어 갈 수 있다는 것을 알려줌.
		   }
	   } catch (Exception e) {
		// TODO: handle exception
	   } 
	   return false; // 그렇지 않다면 flase
   }
   
   //대시보드 리스트 검색 기능 구현
   public ArrayList<ChartDTO> search(String dashboardName) { 
	   String SQL = "select * from testJson where dashboardName"; 
	   ArrayList<ChartDTO> list = new ArrayList<ChartDTO>();
	   try {
		   conn=pool.getConnection();
		   pstmt = conn.prepareStatement(SQL);
		   pstmt.setString(1,"%"+ dashboardName+"%");
		   rs = pstmt.executeQuery();
		   
		   while(rs.next()) {
			   ChartDTO chartDTO = new ChartDTO();
			   chartDTO.setId(rs.getInt("id"));
			   chartDTO.setName(rs.getString("dashboardName"));
			   list.add(chartDTO);
		   }
	   } catch (Exception e) {
		// TODO: handle exception
		   System.out.println("null");
	   }
	   System.out.println(list);
	   return list; // 5개 뽑아온 대시보드 리스트들을 리턴해준다
	   //실행했을 때 특정 페이지에 맞는 대시보드 리스트들이 리스트에 담겨서 반환되어 가져올 수 있다
   }
   */
   public static void main(String[] args) {
	ChartDAO a = new ChartDAO();
   }
}