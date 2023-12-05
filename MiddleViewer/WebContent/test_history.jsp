<%@ page language="java" contentType="text/html; charset=EUC-KR"
   pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="chart.HistoryDAO"%>
<%@ page import="chart.HistoryDTO"%>
<% 
	ArrayList<HistoryDTO> topicList = new HistoryDAO().getTopicList();
	//ArrayList<HistoryDTO> historyList = new HistoryDAO().getHistory();
	int topicNum = topicList.size();
	String obj[] = new String[topicNum];
	
	for(int n=0; n<topicList.size(); n++){
		obj[n] = topicList.get(n).getTopicName().toString();
		System.out.println("this is in jsp");
		System.out.println(obj[n]);
		ArrayList<HistoryDTO> historyList = new HistoryDAO().getHistory(obj[n]);
		System.out.println(historyList);
		
	}
	
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="description" content="">
<meta name="author" content="">
<title>Yellow Peach</title>
	<!-- Bootstrap core CSS-->
	<link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	<!-- Custom fonts for this template-->
	<link href="vendor/font-awesome/css/font-awesome.min.css"
		rel="stylesheet" type="text/css">
	<!-- Page level plugin CSS-->
	<!-- Custom styles for this template-->
	<link href="css/startbootstrap.css" rel="stylesheet">
	<link href="css/PCViewer.css" rel="stylesheet">
	<link href="img/test_favicon.ico" rel="icon">
<title>Yellow Peach</title>

</head>

<script	src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script	src="//cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.js"></script>


<body class="fixed-nav sticky-footer bg-dark" id="page-top" >
	<!-- Navigation-->
	<nav class="navbar navbar-expand-lg navbar-#7f2412 bg-#7f2412 fixed-top"
		id="mainNav" style="background-color:#7f2412;">
		<a class="navbar-brand" href="index.html" style="color:white">Yellow Peach</a>
		<button class="navbar-toggler navbar-toggler-right" type="button"
			data-toggle="collapse" data-target="#navbarResponsive"
			aria-controls="navbarResponsive" aria-expanded="false"
			aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarResponsive" style="background-color:#22364e">
			<ul class="navbar-nav navbar-sidenav" id="exampleAccordion">
				<li class="nav-item" data-toggle="tooltip" data-placement="right"
					title="test_history"><a style="color:white" class="nav-link" href="history.jsp"> <i
						class="fa fa-fw fa-table"></i> <span style="color:white" class="nav-link-text">test_history</span>
				</a></li>
				
				<li class="nav-item" data-toggle="tooltip" data-placement="right"
					title="Dashboard"><a style="color:white" class="nav-link" href="PClist.jsp"> <i
						class="fa fa-fw fa-dashboard"></i> <span style="color:white"class="nav-link-text">Dashboard</span>
				</a></li>
				<li class="nav-item" data-toggle="tooltip" data-placement="right"
					title="Tables"><a style="color:white" class="nav-link" href="tables.html"> <i
						class="fa fa-fw fa-table"></i> <span style="color:white" class="nav-link-text">Tables</span>
				</a></li>
				<li class="nav-item" data-toggle="tooltip" data-placement="right"
					title="Components"><a style="color:white"
					class="nav-link nav-link-collapse collapsed" data-toggle="collapse"
					href="#collapseComponents" data-parent="#exampleAccordion"> <i
						class="fa fa-fw fa-wrench"></i> <span style="color:white" class="nav-link-text">Components</span>
				</a>
					<ul class="sidenav-second-level collapse" id="collapseComponents">
						<li ><a style="color:white" href="navbar.html">Font</a></li>
						<li ><a style="color:white" href="cards.html">Color</a></li>
					</ul></li>
			</ul>
			<ul class="navbar-nav sidenav-toggler">
				<li class="nav-item"><a class="nav-link text-center"
					id="sidenavToggler"> <i class="fa fa-fw fa-angle-left"></i>
					</a>
				</li>
			</ul>
		</div>
	</nav>
	<div class="content-wrapper" style="background-color: #eaeaea;">
		<div class="container-fluid">
			<!-- Breadcrumbs-->
			<ol class="breadcrumb" style="background-color: white; margin:0px;">
				<li class="breadcrumb-item active" id="dbname">My Dashboard</li>
			</ol>
			<div class="row" id="list" >
			</div>
		</div>
	</div>
	<footer class="sticky-footer">
		<div class="container">
			<div class="text-center">
				<small>Copyright © Yellow Peach 2018</small>
			</div>
		</div>
	</footer>
	<!-- Scroll to Top Button-->
	<a class="scroll-to-top rounded" href="#page-top"> <i
		class="fa fa-angle-up"></i>
	</a>
	<!-- Logout Modal-->
	<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLabel">Ready to Leave?</h5>
					<button class="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">Select "Logout" below if you are ready
					to end your current session.</div>
				<div class="modal-footer">
					<button class="btn btn-secondary" type="button"
						data-dismiss="modal">Cancel</button>
					<a class="btn btn-primary" href="login.html">Logout</a>
				</div>
			</div>
		</div>
	</div>
	<!-- Bootstrap core JavaScript-->
	<script src="vendor/jquery/jquery.min.js"></script>
	<script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
	<!-- Core plugin JavaScript-->
	<script src="vendor/jquery-easing/jquery.easing.min.js"></script>
	<!-- Page level plugin JavaScript-->
	<script src="vendor/chart.js/Chart.min.js"></script>
	<!-- Custom scripts for all pages-->
	<script src="js/startbootstrap.js"></script>
	<!-- Custom scripts for this page-->
	<script
		src="//cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.bundle.min.js "></script>
	<script src="//code.jquery.com/jquery-2.2.4.min.js"></script>
	<script src="//code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
	<script src="//code.jquery.com/ui/1.8.18/jquery-ui.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
<script src="//ajax.aspnetcdn.com/ajax/globalize/0.1.1/globalize.min.js"></script>
</body>
</html>