<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="chart.ChartDTO" %>
<%@ page import="chart.ChartDAO" %>

<%
		int id = 0;
		//매개변수로 넘어온 id라는 매개 변수가 존재한다면
		// id에 매개변수 id를 담은 다음에 처리할 수 있도록 한다
		//번호가 반드시 존재해야지 특정한 대시보드를 볼 수 있도록 구현한다
		if(request.getParameter("id") != null) {
			id = Integer.parseInt(request.getParameter("id"));
		}
		
		ChartDAO cDao = new ChartDAO();
		ChartDTO cDto = new ChartDTO();
		cDto = cDao.getPCDashBoard(id); //구체적인 내용을 가지고 와서 인스턴스에 담을 수 있도록
		String json = cDto.getJson();
		
		out.print(json);
%>
<script>location.href = "PCViewer2.jsp"; </script>
