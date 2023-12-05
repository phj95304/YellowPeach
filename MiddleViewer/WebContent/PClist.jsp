<%@ page language="java" contentType="text/html; charset=EUC-KR"
     pageEncoding="UTF-8"%>
<%@ page import="chart.ChartDTO" %>
<%@ page import="chart.ChartDAO" %>
<%@ page import="java.util.ArrayList" %> <!-- 게시판의 목록을 띄우기 위해 import -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta charset="utf-8">  <title>Viewer</title>
  <meta content="width=device-width, initial-scale=1.0" name="viewport">
  <meta content="" name="keywords">
  <meta content="" name="description">

  <!-- Favicon -->
  <link href="img/test_favicon.ico" rel="icon">

  <!-- Bootstrap CSS File -->
  <link href="lib/bootstrap/css/bootstrap.min.css" rel="stylesheet">

  <!-- Libraries CSS Files -->
  <link href="lib/font-awesome/css/font-awesome.min.css" rel="stylesheet">

  <!-- Main Stylesheet File -->
  <link href="css/style.css" rel="stylesheet">
  <script type="text/javascript">
  </script>
<body>
  <section class="about" id="about">
    <div class="container text-center">
      <h2>
          Choose DashBoard
       </h2>
	<%
		int dashboardPcNumber = 1; //기본 페이지 의미
		if(request.getParameter("dashboardPcNumber") !=null) {
			dashboardPcNumber = Integer.parseInt(request.getParameter("dashboardPcNumber"));
		}
	%>
		<div class="row">
		<!--  
			<div class="form-group row pull-right">
				<div class="col-xs-8">
					<input class="form-control" id="dashName" onkeyup="searchFunction()" type="text" size="20">
				</div>
				<div class="col-xs-2">
					<button type="button" onclick="searchFunction();">검색</button>
				</div>
			</div>
			-->
			<table class="table" style="text-align: center">
				<thead class="thead-light">
					<tr>
						<th scope="col">DashBoard</th>				
					</tr>
				</thead>
				<tbody id = "ajaxTable">
					<%
						ChartDAO cDao = new ChartDAO();
						ArrayList<ChartDTO> list = cDao.getPCList(dashboardPcNumber); //현재의 페이지에서 가져온 대시보드 목록
						//가져온 목록 하나씩 출력
						for(int i=0; i<list.size(); i++) { 
					%>
					<!-- 그 안의 내용으로 현재 게시글에 대한 정보가 들어가도록 한다 -->
					<tr>
					<!-- 이때 해당 대시보드에 맞는 곳으로 이동하기 위하여-->
						<td><a href="PCViewer2.jsp?id=<%=list.get(i).getId()%>"><%= list.get(i).getName() %></a></td>										
					</tr>
					<%		
						}
					%>
				</tbody>
			</table>
		</div>
    
    </div>
  </section>
  <!-- /Call to Action -->
  <!-- Portfolio -->
  <section id="contact">
    <div class="container">
      <div class="row">
        <div class="col-md-12 text-center">
          <h2 class="section-title">Contact Us</h2>
        </div>
      </div>
      <div class="row justify-content-center">
        <div class="col-lg-3 col-md-4">
          <div class="info">
            <div>
              <i class="fa fa-map-marker"></i>
              <p> 116, Samseongyo-ro 16-gil, Seongbuk-gu<br> Seoul, Republic of Korea </p>
            </div>
            <div>
              <i class="fa fa-envelope"></i>
              <p>info@hansung.ac.kr</p>
            </div>

            <div>
              <i class="fa fa-phone"></i>
              <p>02-760-4438</p>
            </div>

          </div>
        </div>

        <div class="col-lg-5 col-md-8">
          <div class="form">
            <div id="sendmessage">Your message has been sent. Thank you!</div>
            <div id="errormessage"></div>
            <form action="" method="post" role="form" class="contactForm">
              <div class="form-group">
                <input type="text" name="name" class="form-control" id="name" placeholder="Your Name" data-rule="minlen:4" data-msg="Please enter at least 4 chars" />
                <div class="validation"></div>
              </div>
              <div class="form-group">
                <input type="email" class="form-control" name="email" id="email" placeholder="Your Email" data-rule="email" data-msg="Please enter a valid email" />
                <div class="validation"></div>
              </div>
              <div class="form-group">
                <input type="text" class="form-control" name="subject" id="subject" placeholder="Subject" data-rule="minlen:4" data-msg="Please enter at least 8 chars of subject" />
                <div class="validation"></div>
              </div>
              <div class="form-group">
                <textarea class="form-control" name="message" rows="5" data-rule="required" data-msg="Please write something for us" placeholder="Message"></textarea>
                <div class="validation"></div>
              </div>
              <div class="text-center"><button type="submit">Send Message</button></div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </section>
  <footer class="site-footer">
    <div class="bottom">
      <div class="container">
        <div class="row">

          <div class="col-lg-6 col-xs-12 text-lg-left text-center">
            <p class="copyright-text">
              Yellow Peach
            </p>
            <div class="credits">
              <a href="http://cse.hansung.ac.kr//">Hansung University</a> Computer Engineering
            </div>
          </div>
        </div>
      </div>
    </div>
  </footer>
  <a class="scrolltop" href="#"><span class="fa fa-angle-up"></span></a>
	<!-- Required JavaScript Libraries -->
  <script src="lib/jquery/jquery.min.js"></script>
  <script src="lib/jquery/jquery-migrate.min.js"></script>
  <script src="lib/superfish/hoverIntent.js"></script>
  <script src="lib/superfish/superfish.min.js"></script>
  <script src="lib/tether/js/tether.min.js"></script>
  <script src="lib/stellar/stellar.min.js"></script>
  <script src="lib/bootstrap/js/bootstrap.bundle.min.js"></script>
  <script src="lib/counterup/counterup.min.js"></script>
  <script src="lib/waypoints/waypoints.min.js"></script>
  <script src="lib/easing/easing.js"></script>
  <script src="lib/stickyjs/sticky.js"></script>
  <script src="lib/parallax/parallax.js"></script>
  <script src="lib/lockfixed/lockfixed.min.js"></script>

  <!-- Template Specisifc Custom Javascript File -->
  <script src="js/custom.js"></script>
  <script src="contactform/contactform.js"></script>
</body>
</html>