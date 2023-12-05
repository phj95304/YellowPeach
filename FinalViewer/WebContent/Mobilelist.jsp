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

  <!-- Facebook Opengraph integration: https://developers.facebook.com/docs/sharing/opengraph -->
  <meta property="og:title" content="">
  <meta property="og:image" content="">
  <meta property="og:url" content="">
  <meta property="og:site_name" content="">
  <meta property="og:description" content="">

  <!-- Twitter Cards integration: https://dev.twitter.com/cards/  -->
  <meta name="twitter:card" content="summary">
  <meta name="twitter:site" content="">
  <meta name="twitter:title" content="">
  <meta name="twitter:description" content="">
  <meta name="twitter:image" content="">

  <!-- Favicon -->
  <link href="img/favicon.ico" rel="icon">

  <!-- Google Fonts -->
  <link href="https://fonts.googleapis.com/css?family=Raleway:400,500,700|Roboto:400,900" rel="stylesheet">

  <!-- Bootstrap CSS File -->
  <link href="lib/bootstrap/css/bootstrap.min.css" rel="stylesheet">

  <!-- Libraries CSS Files -->
  <link href="lib/font-awesome/css/font-awesome.min.css" rel="stylesheet">

  <!-- Main Stylesheet File -->
  <link href="css/style.css" rel="stylesheet">
  <script type="text/javascript">
  /*
  	var request = new XMLHttpRequest();
  	function searchFunction() {
  		request.open("Post","./DashBoardSearchServlet?dashboardName" + encodeURIComponent(document.getElementById("dashName").value),true ); //dashName을 적은 값이 UTF-8로 인코딩 돼서 실제 파라미터로 넘어감 
  		//이제 넘어온 JSON을 받아서 처리를 해 주어야 한다
  		//성공적으로 요청하는 작업이 들어왔다면 searchProcess를 실행하라
  		request.onreadystatechange = searchProcess;
  		request.send(null);
  	}
  	function searchProcess() {
  		//ajax에 해당하는 tbody부분을 가져온다
  		var table = document.getElementById("ajaxTable");
  		table.innerHTML = "";
  		//성공적으로 통신이 이루어졌을 경우
  		if(request.readyState == 4 && request.status == 200) {
  			var object = eval('('+request.responseText + ')'); //
  			var result = object.result;
  			//처음에 result라는 이름으로 변수를 담기 때문에 result(대시보드 배열)를 가져오겠다
  			for(var i=0; i<result.length; i++) {
  				var row = table.insertRow(0);
  				for(var j=0; j<result[i].length ; j++) {
  					var cell = row.insertCell(j);
  					cell.innerHTML = result[i][j].value;
  					//각각의 행에 해당되는 대시보드의 번호와 이름이 출력
  				}
  			}
  		}
  	}
  	window.load = function() {
  		searchFunction();
  	}
  	*/
  </script>
<body>
  <section class="about" id="about">
    <div class="container text-center">
      <h2>
          Choose DashBoard
       </h2>
	<%
		int dashboardMobileNumber = 1; //기본 페이지 의미
		if(request.getParameter("dashboardMobileNumber") !=null) {
			dashboardMobileNumber = Integer.parseInt(request.getParameter("dashboardMobileNumber"));
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
						<th scope="col">대시보드</th>				
					</tr>
				</thead>
				<tbody id = "ajaxTable">
					<%
						ChartDAO cDao = new ChartDAO();
						ArrayList<ChartDTO> list = cDao.getMobileList(dashboardMobileNumber); //현재의 페이지에서 가져온 대시보드 목록
						//가져온 목록 하나씩 출력
						for(int i=0; i<list.size(); i++) { 
					%>
					<!-- 그 안의 내용으로 현재 게시글에 대한 정보가 들어가도록 한다 -->
					<tr>
					<!-- 이때 해당 대시보드에 맞는 곳으로 이동하기 위하여-->
						<td><a href="MobileViewer.jsp?id=<%=list.get(i).getId()%>"><%= list.get(i).getName() %></a></td>										
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
              <p> 서울특별시 성북구 삼선교로 16길 116<br>한성대학교 공학관 B동 119호</p>
            </div>
            <div>
              <i class="fa fa-envelope"></i>
              <p>info@example.com</p>
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