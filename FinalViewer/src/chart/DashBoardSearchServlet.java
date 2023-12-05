package chart;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
@WebServlet("/DashBoardSearchServlet")
public class DashBoardSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//매개변수 등을 처리할 때 
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html);charset=UTF-8");
		String dashboardName = request.getParameter("dashboardName"); 
		response.getWriter().write(getJSON(dashboardName));
	}
	//특정한 대시보드를 검색했을 때 그 결과 정보가 JSON형태로 출력됨으로써 응답이 온다
	// 다시 파싱해서 분석한 다음 사용자에게 보여주는 것
	//따라서 서버역할을 하는 서블릿은 JSON을 만들어주는 역할을 한다
	public String getJSON(String dashboardName) {
		if(dashboardName ==null) dashboardName = "";
		StringBuffer result = new StringBuffer("");
		result.append("{\"result\":[");
		ChartDAO cDao = new ChartDAO();
		ArrayList<ChartDTO> cList = cDao.search(dashboardName);
		for(int i=0; i<cList.size(); i++) {
			result.append("[{\"value\"\""+cList.get(i).getName()+"\"},");
			result.append("{\"value\"\""+cList.get(i).getName()+"\"}],");
		}
		result.append("]}");
		return result.toString();
	}

}
*/