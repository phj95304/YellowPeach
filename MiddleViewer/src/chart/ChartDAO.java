package chart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import pool.PoolManager;
/* Data Access Object
 * ���̺� �� �Ѱ��� DAO�� �ۼ��Ѵ�.
 * 
 * JSP_MEMBER ���̺�� ������ DAO��
 * ȸ�� �����͸� ó���ϴ� Ŭ�����̴�.
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
//JSON ������ �������� �޼ҵ�
   public String getChart() {
      try {
    	 // System.out.println("conn");
         conn=pool.getConnection();
         if( conn != null ){ System.out.println("������ ���̽� ���� ����"); }
         if(conn == null) {System.out.println("conn == null");}
         int no =9;
         query = "select * from testJson"; //������ ���̺� �̸����� �ٲ� ��!, 
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
         System.out.println("���� in getChart");
         //e.printStackTrace();
      }finally {
         pool.freeConnection(conn,pstmt,rs);
         
      }
      return result;
   }
   */
   
   //PC ��ú��� ����Ʈ ���������� �ϴ� �κ�
   public ChartDTO getPCDashBoard(int dashboardPcNumber) {
	   //id�� �ش��ϴ� ��ú��带 ���������� �ϴ� SQL����
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
   
   //Mobile ��ú��� ����Ʈ ���������� �ϴ� �κ�
   public ChartDTO getMobileDashBoard(int dashboardMobileNumber) {
	   //id�� �ش��ϴ� ��ú��带 ���������� �ϴ� SQL����
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
		   return 1; // ù��° ��ú����� ���
	} catch (Exception e) {
		// TODO: handle exception
	}
	   return -1; //�����ͺ��̽� ����
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
		   return 1; // ù��° ��ú����� ���
	} catch (Exception e) {
		// TODO: handle exception
	}
	   return -1; //�����ͺ��̽� ����
   }
   
   //Mobile��ú��� ����Ʈ �������� �κ�
   //Ư���� �������� ���� �� 5���� ��ú��带 ������ �� �ֵ��� �Ѵ�
   public ArrayList<ChartDTO> getMobileList(int dashboardMobileNumber) { 
	   String SQL = "select * from Json_mobile where id < ? and available = 1 order by id desc limit 5"; 
	   //id�� Ư���� ���ں��� ���� �� ������ ���� �ʾƼ� available�� 1�� �۵鸸 ���� �� �� �ֵ��� 
	   //id�� �������� ���� ������ 10�� ���� �����´�
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
	   return list; // 5�� �̾ƿ� ��ú��� ����Ʈ���� �������ش�
	   //�������� �� Ư�� �������� �´� ��ú��� ����Ʈ���� ����Ʈ�� ��ܼ� ��ȯ�Ǿ� ������ �� �ִ�
   }
   
   //PC��ú��� ����Ʈ �������� �κ�
   //Ư���� �������� ���� �� 5���� ��ú��带 ������ �� �ֵ��� �Ѵ�
   public ArrayList<ChartDTO> getPCList(int dashboardPcNumber) { 
	   String SQL = "select * from Json_pc where id < ? and available = 1 order by id desc limit 5"; 
	   //id�� Ư���� ���ں��� ���� �� ������ ���� �ʾƼ� available�� 1�� �۵鸸 ���� �� �� �ֵ��� 
	   //id�� �������� ���� ������ 10�� ���� �����´�
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
	   return list; // 5�� �̾ƿ� ��ú��� ����Ʈ���� �������ش�
	   //�������� �� Ư�� �������� �´� ��ú��� ����Ʈ���� ����Ʈ�� ��ܼ� ��ȯ�Ǿ� ������ �� �ִ�
   }
   public static void main(String[] args) {
	   ChartDAO a = new ChartDAO();
	   System.out.print(a.dto);
	   
   }
}