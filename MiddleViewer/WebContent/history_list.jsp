<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="chart.HistoryDAO"%>
<%@ page import="chart.HistoryDTO"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Viewer</title>
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<meta content="" name="keywords">
<meta content="" name="description">

<!-- Favicon -->
<link href="img/test_favicon.ico" rel="icon">

<!-- Bootstrap CSS File -->
<link href="lib/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<!-- Libraries CSS Files -->
<link href="lib/font-awesome/css/font-awesome.min.css" rel="stylesheet">

<link rel="stylesheet" href="css/pikaday.css">
<link rel="stylesheet" href="css/site.css">

<!-- Main Stylesheet File -->
<link href="css/style.css" rel="stylesheet">
<link rel="stylesheet"
	href="https://use.fontawesome.com/releases/v5.5.0/css/all.css"
	integrity="sha384-B4dIYHKNBt8Bc12p+WXckhzcICo0wtJAoU8YZTY5qE0Id1GSseTk6S+L3BlXeVIU"
	crossorigin="anonymous">

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
<script src="vendor/chart.js/Chart.min.js"></script>
<script src="js/moments.js"></script>
<script src="js/pikaday.js"></script>

<!-- Template Specisifc Custom Javascript File -->
<script src="js/custom.js"></script>
<script src="contactform/contactform.js"></script>
</head>
<script type="text/javascript">
///데이터베이스에서 토픽리스트를 가져온다.
	<%ArrayList<String> topicList = new HistoryDAO().getTopicList();
			int topicNum = topicList.size();
			String[] _topicName = new String[topicNum];//_로 구분된 토픽리스트 담기는 스트링 배열
			String[] topicName = new String[topicNum];// "/"로 구분된 토픽 
			for (int n = 0; n < topicNum; n++) {
				_topicName[n] = topicList.get(n).toString();
				topicName[n] = _topicName[n].replace("_", "/");
				System.out.println(topicList.get(n).toString());
		
				/*ArrayList<HistoryDTO> historyList = new HistoryDAO().getHistory(_topicName[n]);
				int historyNum = historyList.size();// 한 토픽에 대해 몇개의 히스토리가 저장되어 있는지
				System.out.println("=========");
				System.out.println(historyNum);
				for (int k = 0; k < historyNum; k++) {
					System.out.println(historyList.get(k).getTopicName());
					System.out.println(historyList.get(k).getsTime());
					System.out.println(historyList.get(k).getsData());
				}*/
			

			}%>
	var topicNum = '<%=topicNum%>';//토픽의 개수
	var topicNameArray = new Array();//토픽 이름의 배열
	var historyDateArray = new Array(topicNum);//historyArray[id(토픽이름)][날짜,시간]
	var historyDataArray = new Array(topicNum);//historyArray[id(토픽이름)][데이터]
	var historySize = new Array(topicNum);//history 개수 배열
	
	<%for (int num=0; num<topicNum; num++){%>
		topicNameArray[<%=num%>]='<%=topicName[num]%>';
		<%ArrayList<HistoryDTO> historyList = new HistoryDAO().getHistory(_topicName[num]);%>
		<%int historyNum[] = new int [num];%>
		historySize[<%=num%>] ='<%=historyList.size()%>';
		console.log(historySize[<%=num%>]);
		historyDateArray[<%=num%>] = new Array(historySize[<%=num%>]);
		historyDataArray[<%=num%>] = new Array(historySize[<%=num%>]);
		<%for (int h=0; h<historyList.size(); h++){%>
			historyDateArray[<%=num%>][<%=h%>] ='<%=historyList.get(h).getsTime()%>';
			historyDataArray[<%=num%>][<%=h%>] ='<%=historyList.get(h).getsData()%>';
		<%}%>
	<%}%>

	function drawChart(id, mData) {//차트를 캔버스 안에 그리는 함수
		var canvasId = "chartCanvas" + id;
		var topicName = topicNameArray[id];
		console.log(canvasId);
		console.log(topicName);
		var ctx = document.getElementById(canvasId).getContext('2d');
		var myChart = new Chart(ctx,{
           type : 'line',
           data : {
              labels : ["0 시", "1 시", "2시", "3시", "4시",
                  "5시", "6시", "7시", "8시", "9시", "10시",
                  "11시", "12시", "13시", "14시", "15시",
                  "16시", "17시", "18시", "19시", "20시",
                  "21시", "22시", "23시"
              ],
              datasets : [ {
                 label : topicName,
                 data : [mData[24], mData[1], mData[2],
                     mData[3], mData[4], mData[5],
                     mData[6], mData[7], mData[8],
                     mData[9], mData[10], mData[11],
                     mData[12], mData[13], mData[14],
                     mData[15], mData[16], mData[17],
                     mData[18], mData[19], mData[20],
                     mData[21], mData[22], mData[23]],
                 borderColor : "#4bc0c0",
                 fill : false
              }]
           },
           option0 : {
              title : {
                 display : true,
                 text : 'values'
              },
           }
        });
	}
	function meanTime(valueArray, timeArray){
		
		
		
	}
	
	function meanDay(valueArray){
		//시간 없을때 0으로 처리하기
		var meanD = new Array();
		var tmp=0;
		console.log("menaDay");
		console.log(valueArray.length);
		
		
		
		
			for(var k=0; k<valueArray.length; k++){
					tmp = tmp + valueArray[k];
			}
			meanD = (tmp/valueArray.length).toFixed(2);
		return meanD;
	}

	function inputDate(id, date){//입력받은 날짜와 저장된 날짜 비교
		
		var mData = new Array(25);

		
		for(var hour=1; hour<25; hour++){
			var valueArray = new Array();
			var dayArray = new Array();
			var count = 0;
			
			for(var j=0; j<historySize[id];j++ ){
				if(historyDateArray[id][j].split(';')[0]==date){//입력된 날짜가 같을 때 시간을 구분해서 평균을 구한다.
					if((historyDateArray[id][j].split(';')[1].split('-')[0])==hour||
						(historyDateArray[id][j].split(';')[1].split('-')[0])=="0"+hour){
						count++;
						dayArray[count] = historyDateArray[id][j].split(';')[1];
						valueArray[count] = historyDateArray[id][j];
					}
				}
				
			}
			mData[hour] = meanDay(valueArray);//24 시간 평균 구하기
			console.log(mData[hour]);
		}
		//drawChart(id, mData);	
	}
	
	function newPikaday(id) {//Pikaday 객체 생성
		var pikaday = "pikaday" + id;
		var datepickerId = "datepicker" + id;
		var selectedId = "selected" + id;
		pikaday = new Pikaday({
			field : document.getElementById(datepickerId),
			firstDay : 1,
			minDate : new Date(2000, 0, 1),
			maxDate : new Date(2020, 12, 31),
			yearRange : [ 2018, 2028 ],
			onSelect : function() {//날짜 선택
				var date = this.getMoment().format('YYYY-MM-DD');
				console.log(date);
				//document.getElementById(selectedId).appendChild(date);
				console.log(selectedId);
				inputDate(id, date);
			}
		});
	}
	
	
	function submit() {//next버튼을 눌렀을 때 클릭된 차트show
		for (var i = 0; i < topicNum; i++) {
			console.log($("#checkBox" + i).is(":checked"))
			if ($("#checkBox" + i).is(":checked") == true) {
				toggleBtn(i, true);
			}
		}
	}
	function remove() {//back버튼을 눌렀을 때 클릭된 차트hide
		for (var i = 0; i < topicNum; i++) {
			console.log($("#checkBox" + i).is(":checked"))
			if ($("#checkBox" + i).is(":checked") == true) {
				toggleBtn(i, false);
				$("#checkBox" + i).prop('checked', false);
			}
		}
	}

	function toggleBtn(number, isVisible) {
		//차트 테이블을 show 또는 hide 한다.
		console.log("tobbleBtn is called " + number, isVisible);

		if ($("#chartTable" + number).css("display") == "none"
				&& (isVisible == true)) {
			newPikaday(number);
			$("#chartTable" + number).css("display", "block");
		}

		if ($("#chartTable" + number).css("display") == "block"
				&& (isVisible == false)) {
			$("#chartTable" + number).css("display", "none");
		}
	}
	

</script>
<body>
	<section class="about" id="about">
	<div class="container text-center">
		<h2>Choose Topic</h2>


		<div class="row">
			<table class="table" style="text-align: center">
				<thead class="thead-light">
					<tr>
						<th scope="col">Topic List</th>
					</tr>
				</thead>
				<tbody id="ajaxTable" width="1645px">
					<%for(int i = 0; i < topicNum; i++) {%>
					<tr>
						<td>
							<input type="checkbox" id="checkBox<%=i%>"
							value=<%=topicName[i]%>><%=topicName[i]%>
						</td>
					</tr>
					<%}%>
			
				</tbody>
			</table>
			<button onclick="submit()"> NEXT </button>
			<button onclick="remove()"> BACK </button>
		</div>
	</div>
	</section>


	<div>
		<%for (int i = 0; i < topicNum; i++) {%>
		<table id="chartTable<%=i%>"
			style='margin: 5px; border-spacing: 5px; border-collapse: separate; display: none;'>
			<tr>
				<td style='margin: 6px; width: 1654px;' >
					<div id="size0" style='background-color: coral;'>
					<p><%=topicName[i]%><p>
						<label for="datepicker<%=i%>">Date:</label>
						<input type="text" id="datepicker<%=i%>">
						<div id="selected<%=i%>"></div>
					</div>
					<div id="size">
						<canvas width="1654px" id="chartCanvas<%=i%>" >
						</canvas>
					</div>
				</td>
			</tr>
		</table>
		<%
			}
		%>

	</div>

	<!-- /Call to Action -->
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
						<p>
							116, Samseongyo-ro 16-gil, Seongbuk-gu<br> Seoul, Republic
							of Korea
						</p>
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
							<input type="text" name="name" class="form-control" id="name"
								placeholder="Your Name" data-rule="minlen:4"
								data-msg="Please enter at least 4 chars" />
							<div class="validation"></div>
						</div>
						<div class="form-group">
							<input type="email" class="form-control" name="email" id="email"
								placeholder="Your Email" data-rule="email"
								data-msg="Please enter a valid email" />
							<div class="validation"></div>
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="subject"
								id="subject" placeholder="Subject" data-rule="minlen:4"
								data-msg="Please enter at least 8 chars of subject" />
							<div class="validation"></div>
						</div>
						<div class="form-group">
							<textarea class="form-control" name="message" rows="5"
								data-rule="required" data-msg="Please write something for us"
								placeholder="Message"></textarea>
							<div class="validation"></div>
						</div>
						<div class="text-center">
							<button type="submit">Send Message</button>
						</div>
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
					<p class="copyright-text">Yellow Peach</p>
					<div class="credits">
						<a href="http://cse.hansung.ac.kr//">Hansung University</a>
						Computer Engineering
					</div>
				</div>
			</div>
		</div>
	</div>
	</footer>
	<a class="scrolltop" href="#"><span class="fa fa-angle-up"></span></a>


</body>

</html>