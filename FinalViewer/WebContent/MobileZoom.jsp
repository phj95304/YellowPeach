<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="chart.ChartDTO" %>
<%@ page import="chart.ChartDAO" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
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
	<style type="text/css">
	.chartName {
		width:95%;
		height: 50px;
		border-radius: 0.25rem;
		text-align: center;
		margin:5px auto;	
		background-color: white;	
		padding-top: 10px;
	}
	.size {
		position: relative;
		width: 340px;
		height: 420px;
		margin:0 auto;
		padding-top:30px;
		background-color: white;
	}
	.camera {
		width: 340px;
		height: 400px;
		background-color: white;
		margin:0 auto;
	}
	.camera_btn {
		width: 340px;
		height: 30px;
		margin:0 auto;
	}
	.button {
		width: 340px;
		height: 30px;
		text-align: center;
		background-color: pink;
		margin:0 auto;
	}
	#drawText {
		padding-left: 20px;
		padding-top: 20px;
	}
	#calBtn {
	  position: relative;
      width: 340px;
      height: 30px;
      text-align: right;
      margin:0px auto;   
      background-color: white;   
      padding-right: 10px;
      padding-top: 10px;
      border-top: 0.25rem;
	}
	</style>
</head>
<%
	String chartTopic1 = (String) request.getParameter("topic1");//토픽1
	String chartTopic2 = (String) request.getParameter("topic2");//토픽2
	String chartName = (String) request.getParameter("name");//차트 이름
	String chartChart = (String) request.getParameter("chart");//차트 종류
	String chartValue1 = (String) request.getParameter("value1");//Value이름1
	String chartValue2 = (String) request.getParameter("value2");//Value이름2
	String chartControl1 = (String) request.getParameter("cont1");//Control 토픽1
	String chartControl2 = (String) request.getParameter("cont2");//VControl 토픽2
%>
<script	src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script	src="https://cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.js"></script>
<script type="text/javascript">
	var mqtt;
	var reconnectTimeout = 2000;
	var host = "113.198.86.190"; //change this
	var port = 9001;

	var drawChart="";
	
	var drawBar0;
	var drawBar1;
	
	var drawLine0;
	var drawLine1;
	
	var drawValue;
	
	var drawDoughnut0;
	var drawDoughnut1;

	var drawPie0;
	var drawPie1;

	var drawGaugeChart0;
	var drawGaugeChart1;

	//label
	var out_msgLabel = new Array();
	
	//line
	var out_msgLine = new Array();
	var out_msgLine1 = new Array();
	var vmsgLine = new Array();
	var vmsgLine1 = new Array();
	var valueline = new Array();
	var valueline1 = new Array();
	var timeline = new Array();
	
	//bar
	var out_msgBar = new Array();
	var out_msgBar1 = new Array();
	var vmsgBar = new Array();
	var vmsgBar1 = new Array();
	var valuebar = new Array();
	var valuebar1 = new Array();
	var timebar = new Array();

	//pie
	var out_msgPie;
	var out_msgPie1;
	var vmsgPie;
	var vmsgPie1;
	var newvaluepie;
	var newvaluepie1;	
	var valuepie;
	var valuepie1;
	
	//Doughnut
	var out_msgDough;
	var out_msgDough1;
	var vmsgDough;
	var vmsgDough1;
	var newvaluepie;
	var newvaluepie1;
	var valuedough;
	var valuedough1;
	
	//gauge
	var out_msgGauge;
	var out_msgGauge1;
	var valuegauge;
	var valuegauge1;
	var newValuegauge;
	var newValuegauge1;
	var vmsgGauge;
	var vmsgGauge1;
	
	//MobileViewer로 부터 넘어온 변수
	var topic = "<%=chartTopic1%>"+"-";
	var newTopic = topic.slice(0,-1);
	var topic1 = "<%=chartTopic2%>"+"-";
	var newTopic1 = topic1.slice(0,-1);
	
	var chartValue1 = "<%=chartValue1%>";
	var chartValue2 = "<%=chartValue2%>";

	var chartChart = "<%=chartChart%>";
	var chartName = "<%=chartName%>";
	
	var chartControl1 = "<%=chartControl1%>";
	var chartControl2 = "<%=chartControl2%>";

	var ctx = "";
	var valueLabel = [];

	function onFailure(message) { //
		console.log("Failed");
		setTimeout(MQTTconnect, reconnectTimeout);
	}
	function onMessageArrived(msg) {
		console.log("message Arrived");
		if(msg.destinationName) {
			if ((chartChart == "value") && 
					(msg.destinationName == newTopic)
					&& (msg.destinationName != "SmartHome/miraeb1/flame")) {
				drawValue = document.getElementById("drawValue");
				out_msgLabel = msg.payloadString;
				
				vmsgLabel = parseFloat(out_msgLabel);
				
				ctx = drawValue.getContext("2d");
				ctx.font = "50px malgun gothic";
				ctx.fillStyle = "rgba(255,0,255,1)";
				if(valueLabel.length == 2) {
					valueLabel.shift();
				    ctx.clearRect(0, 0, 200, 200);
				}
				ctx.fillText(valueLabel[0], 90,90);
			}
			if ((chartChart == "value")
					&& (msg.destinationName == "SmartHome/miraeb1/flame")) {
				 drawValue = document.getElementById("drawValue");
				 out_msgLabel = msg.payloadString;
                 
                 if(out_msgLabel<=300) {
					 valueLabel.push(out_msgLabel);
                	 //화재발생
                	 ctx = drawValue.getContext("2d");
                     ctx.font = "50px malgun gothic";
                     ctx.fillStyle = "rgba(255,0,64,1)";
                	 if(valueLabel.length == 2) {
	                     valueLabel.shift();
	                     ctx.clearRect(0, 0, 200, 200);
	                 }
                     ctx.fillText("화재 발생!!", 40,150);  
                 }
                 else {
					 valueLabel.push(out_msgLabel);
                	 //화재 감지중
                	 ctx = drawValue.getContext("2d");
                     ctx.font = "50px malgun gothic";
                     ctx.fillStyle = "rgba(0,0,0,1)";
                	 if(valueLabel.length == 2) {
	                      valueLabel.shift();
	                      ctx.clearRect(0, 0, 200, 200);
	                 }
                     ctx.fillText("화재 감지중", 40,150);  
                 }                    		                   		                                     
			}
			if((chartChart == "pie") && (newTopic != null)
					&& (msg.destinationName == newTopic)) {
				out_msgPie = msg.payloadString;
				vmsgPie = parseFloat(out_msgPie);
				
			}
			
			//pie1개 짜리
			if((chartChart == "pie") 
					&& (newTopic == "null")
					&& (msg.destinationName == newTopic)) {
				drawPie0 = document.getElementById("drawPieChart");

				new Chart(drawPie0,{
					type : 'pie',
					data : {
						labels : [chartValue1],
						datasets: [
					        {
					            data: [vmsgPie],
					            backgroundColor: [
					                "#ff9f40",
					                "#ffcd56"
					            ]
					        }]
					},
					options: {
					},
				});
			}
			// pie 2개 짜리
			if((chartChart == "pie")
					&& (msg.destinationName == newTopic1)) {
				drawPie1 = document.getElementById("drawPieChart");

				out_msgPie1 = msg.payloadString;
				vmsgPie1 = parseFloat(out_msgPie1);
				
				newvaluepie = vmsgPie1/(vmsgPie1+vmsgPie)*100;
				newvaluepie1 = vmsgPie/(vmsgPie1+vmsgPie)*100;
				
				new Chart(drawPie1,{
					type : 'pie',
					data : {
						labels : [chartValue1, chartValue2],
						datasets: [
					        {
					            data: [newvaluepie,newvaluepie1],
					            backgroundColor: [
					                "#ff9f40",
					                "#ffcd56"
					            ]
					        }]
					},
					options: {
					},
				});
			}
			
			//dounut그리기
			if ((chartChart == "doughnut") 
					&& (newTopic1 != "null")
					&& (msg.destinationName == newTopic)) {
				out_msgGauge = msg.payloadString;
				vmsgDough = parseFloat(out_msgGauge);
			}
			
			//doughnut 1개
			if((chartChart == "doughnut") 
					&& (newTopic == "null")
					&& (msg.destinationName == newTopic)) {
				drawDoughnut0 = document.getElementById("drawDoughnutChart");

				new Chart(drawDoughnut0,{
					type : 'doughnut',
					data : {
						labels : [chartValue1],
						datasets : [ {
							data : [vmsgDough],
							backgroundColor: [
				            	"#ff9f40"
				            ],
						}]
					},
					options: {
					},
				});
			}
			//doughut 2개
			if((chartChart == "doughnut") 
					&& (msg.destinationName == newTopic1)) {
				out_msgDough1 = msg.payloadString;
				
				drawDoughnut1 = document.getElementById("drawDoughnutChart");

				vmsgDough1 = parseFloat(out_msgDough1);
				
				newvaluedough = vmsgDough/(vmsgDough+vmsgDough1)*100;
				newvaluedough1 = vmsgDough1/(vmsgDough+vmsgDough1)*100;
				
				new Chart(drawDoughnut1,{
					type : 'doughnut',
					data : {
						labels : [chartValue1, chartValue2],
						datasets : [{
							data : [newvaluedough, newvaluedough1],
							backgroundColor: [
				            	"#ff9f40",
				                "#ffcd56"
				            ],
						}]
					},
					options: {
					},
				});
			}	
			
			//gauge그리기
			if ((chartChart == "gauge") 
					&& (newTopic1 != "null")
					&& (msg.destinationName == newTopic)) {
				out_msgGauge = msg.payloadString;
				vmsgGauge = parseFloat(out_msgGauge);
				console.log("gauge"+vmsgGauge);
			}
			
			
			//gauge value 2개일 경우
			if((chartChart == "gauge") 
					&& (msg.destinationName == newTopic1)) {
				out_msgGauge1 = msg.payloadString;
				
				drawGauge1 = document.getElementById("drawGaugeChart");
				
				vmsgGauge1 = parseFloat(out_msgGauge1);
				console.log("gauge1"+vmsgGauge1);
				
				//도넛차트 옵션 변경해서 게이지 차트로 옵션 설정
				var chartOptions = {
			  	  	  rotation: -Math.PI,
					  cutoutPercentage: 30,
					  circumference: Math.PI,
					  legend: {
					    position: 'left'
					  },
				};
				
				newValuegauge = vmsgGauge/(vmsgGauge+vmsgGauge1)*100;
				newValuegauge1 = vmsgGauge1/(vmsgGauge1+vmsgGauge1)*100;
				
				new Chart(drawGauge1,{
					type: 'doughnut',
					data : {
						labels: [
							chartValue1,chartValue2
					    ],
					    datasets: [
					        {
					            data: [newValuegauge, newValuegauge1],
					            backgroundColor: [
					            	"#ff9f40",
					                "#ff6384"],
					    }]
					},
					options: chartOptions
				});
			}			
			
			//Bar Chart
			if ((chartChart == "bar") 
					&& (newTopic1 != "null")
					&& (msg.destinationName == newTopic)) {
				out_msgBar = msg.payloadString;
				vmsgBar = parseFloat(out_msgBar);
			}

			//bar value 1개 일 때
			if ((chartChart == "bar") 
					&& (newTopic1 == "null")
					&& (msg.destinationName == newTopic)) {
				drawBar0 = document.getElementById("drawBarChart");

				out_msgBar = msg.payloadString;
				vmsgBar = parseFloat(out_msgBar);
												
                var now = new Date();
                var nowmin = now.getMinutes();
                var nowsecond = now.getSeconds();
                
                var time = nowmin + "m" + nowsecond+"s";
				
				valuebar.push(vmsgBar);
				timebar.push(time);
				
				if (timebar.length == 8) {
					timebar.shift();
				}
				if (valuebar.length == 8) {
					valuebar.shift();
				}
				
				new Chart(drawBar0,{
					type : 'bar',
					data : {
						labels : [ timebar[0],
							timebar[1],
							timebar[2],
							timebar[3],
							timebar[4],
							timebar[5],
							timebar[6]],
						datasets : [ {
							label : chartValue1,
							data : [ valuebar[0],
								valuebar[1],
								valuebar[2],
								valuebar[3],
								valuebar[4],
								valuebar[5],
								valuebar[6] ],
							backgroundColor : [
								'#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5' ],
							borderColor : [
									'#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5' ],
							borderWidth : 1
						}],
						option1 : {
							scales : {
								yAxes : [ {
									ticks : {
										beginAtZero : true
									}
								} ]
							}
						}
					}
				});
			}
			
			//bar value2개 일 때 sensorT1받아와서 추가로 그려주기
			if((chartChart == "bar") 
					&& (msg.destinationName == newTopic1)) {
				console.log("BBBar");
				drawBar1 = document.getElementById("drawBarChart");
				
				out_msgBar1 = msg.payloadString;

				vmsgBar1 = parseFloat(out_msgBar1);
				
                var now = new Date();
                var nowmin = now.getMinutes();
                var nowsecond = now.getSeconds();
                
                var time = nowmin + "m" + nowsecond+"s";

				valuebar.push(vmsgBar);
				valuebar1.push(vmsgBar1);

				timebar.push(time);
				
				if (timebar.length == 8) {
					timebar.shift();
				}
				if (valuebar.length == 8) {
					valuebar.shift();
				}
				if (valuebar1.length == 8) {
					valuebar1.shift();
				}
				
				new Chart(drawBar1,{
					type : 'bar',
					data : {
						labels : [ timebar[0],
							timebar[1],
							timebar[2],
							timebar[3],
							timebar[4],
							timebar[5],
							timebar[6]],
						datasets : [ {
							label : chartValue1,
							data : [valuebar[0],
								valuebar[1],
								valuebar[2],
								valuebar[3],
								valuebar[4],
								valuebar[5],
								valuebar[6]],
							backgroundColor : [
									'#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5' ],
							borderColor : [
									'#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5' ],
							borderWidth : 1
						},
						{
							label : chartValue2,
							data : [valuebar1[0],
								valuebar1[1],
								valuebar1[2],
								valuebar1[3],
								valuebar1[4],
								valuebar1[5],
								valuebar1[6]],
							backgroundColor : [
								'#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa' ],
							borderColor : [
									'#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa' ],
							borderWidth : 1
						}]
					},
					option1 : {
						scales : {
							yAxes : [ {
								ticks : {
									beginAtZero : true
								}
							} ]
						}
					}
				});
			}
			
			if ((chartChart == "line") 
					&& (newTopic1 != "null")
					&& (msg.destinationName == newTopic)) {
				out_msgLine = msg.payloadString;
				vmsgLine = parseFloat(out_msgLine);
			}
			
			//line value 1개 일 때 sensorT받아와서 그리기
			if ((chartChart == "line") 
					&& (newTopic1 == "null")
					&& (msg.destinationName == newTopic)) {
				out_msgLine = msg.payloadString;
				
				console.log("out_msgLine" + out_msgLine);
				
				drawLine0 = document.getElementById("drawLineChart");
				
				vmsgLine = parseFloat(out_msgLine);
				
                var now = new Date();
                var nowmin = now.getMinutes();
                var nowsecond = now.getSeconds();
                
                var time = nowmin + "m" + nowsecond+"s";

				valueline.push(vmsgLine);							
				timeline.push(time);
				
				if (timeline.length == 8) {
					timeline.shift();
				}
				if (valueline.length == 8) {
					valueline.shift();
				}
				
				new Chart(drawLine0,{
					type : 'line',
					data : {
						labels : [ timeline[0],
							timeline[1],
							timeline[2],
							timeline[3],
							timeline[4],
							timeline[5],
							timeline[6]],
						datasets : [ {
							label : chartValue1,
							data : [valueline[0],
								valueline[1],
								valueline[2],
								valueline[3],
								valueline[4],
								valueline[5],
								valueline[6]],
							borderColor : "#4bc0c0",
							fill : false
						}]
					},
					option0 : {
						title : {
							display : true,
							text : 'World population per region (in millions)'
						}
					}
				});
			}
			//line value2개 일 때 sensorT1받아와서 추가로 그려주기
			if((chartChart == "line") 
					&& (msg.destinationName == newTopic1)) {
				out_msgLine1 = msg.payloadString;
				
				drawLine1 = document.getElementById("drawLineChart");
				
				vmsgLine1 = parseFloat(out_msgLine1);
				
                var now = new Date();
                var nowmin = now.getMinutes();
                var nowsecond = now.getSeconds();
                
                var time = nowmin + "m" + nowsecond+"s";

				valueline.push(vmsgLine);
				valueline1.push(vmsgLine1);
				
				timeline.push(time);
				
				if (timeline.length == 8) {
					timeline.shift();
				}
				if (valueline.length == 8) {
					valueline.shift();
				}
				if (valueline1.length == 8) {
					valueline1.shift();
				}
				new Chart(drawLine1,{
					type : 'line',
					data : {
						labels : [ timeline[0],
							timeline[1],
							timeline[2],
							timeline[3],
							timeline[4],
							timeline[5],
							timeline[6]],
						datasets : [ {
							label : chartValue1,
							data : [valueline[0],
								valueline[1],
								valueline[2],
								valueline[3],
								valueline[4],
								valueline[5],
								valueline[6]],
							borderColor : "#ff6384",
							fill : false
						},
						{
							label : chartValue2,
							data : [valueline1[0],
								valueline1[1],
								valueline1[2],
								valueline1[3],
								valueline1[4],
								valueline1[5],
								valueline1[6]],
							borderColor : "#36a2eb",
							fill : false									
						}]
					},
					option0 : {
						title : {
							display : true,
							text : 'World population per region (in millions)'
						}
					}
				});
			}
		}
	}
	
	function onConnect() {
		console.log("connect topic : " + newTopic);
		if ((chartChart == "line")
				|| (chartChart == "value")
				|| (chartChart == "gauge")
				|| (chartChart == "bar")
				|| (chartChart == "doughnut")
				|| (chartChart == "pie")) {
			mqtt.subscribe(newTopic);
			mqtt.subscribe(newTopic1);
		}
	}
	
	function MQTTconnect() { //
		console.log("connecting to " + host + " " + port);
		mqtt = new Paho.MQTT.Client(host, port, "clientjs"); //client object생성
		//document.write("connecting to "+ host);
		var options = {
			timeout : 3,
			onSuccess : onConnect, //Callback function
			onFailure : onFailure,
		};
		mqtt.connect(options); //connect
		mqtt.onMessageArrived = onMessageArrived
	}
	
	function send_pic_message() {
		var $frame0;
		var Iurl;

		if(chartControl1 && (chartChart == "camera")) {
			message = new Paho.MQTT.Message(chartControl1);
			message.destinationName = topic;
			message.qos=0;
			mqtt.send(message);

			console.log("pub message : " + message);
			console.log("control[i][j] : " + chartControl1);
			
			Iurl = topic;
			$cframe = $("<iframe src='http://192.168.0.7:8000/image.jpg' width='340px' height='400px' name ='123'></iframe>");
			$(".camera").replaceWith($cframe);
		}
		else {
			console.log("nothing");
		}

	}
	
	function send_message() {
		var $frame0;
		var Iurl;
		if(chartControl1 && (chartChart != "camera")) {
			message = new Paho.MQTT.Message(chartControl1);
			message.destinationName = chartControl1;
			message.qos=0;
			mqtt.send(message);
			console.log("control[i][j] : " + chartControl1);
		}
		else {
			console.log("nothing");
		}
	}
	
	
	function chartDraw() {
		drawChart = document.getElementById("drawChart");
		var tag = "";
		if(chartChart == "bar") {
			if((chartControl1) != ("null" || null)) {
				tag = "<div id = 'calBtn'><a href = 'MobileHistory.jsp?name="
                     + newTopic+ "&type=" + chartChart + "&name2="+ newTopic1 +"'>"
                    + "<i class='fa fa-calendar'></i></a>"
                    + "</div>"     
					+"<div class='size'><canvas id = 'drawBarChart' width='300px' height='250px'/>"
					+"<input type='button' class='button' value ='control' onclick='send_message();'/></div>";
				drawChart.innerHTML = tag;
			}
			else {
				tag = "<div id = 'calBtn'><a href = 'MobileHistory.jsp?name="
                     + newTopic+ "&type=" + chartChart + "&name2="+ newTopic1 +"'>"
                    + "<i class='fa fa-calendar'></i></a>"
                    + "</div>"    
					+"<div class='size'><canvas id='drawBarChart' width='300px' height='250px'></canvas></div>";
				drawChart.innerHTML = tag;
			}
		}
		if(chartChart == "line") {
			if((chartControl1) != ("null" || null)) {
				tag = "<div id = 'calBtn'><a href = 'MobileHistory.jsp?name="
                     + newTopic+ "&type=" + chartChart + "&name2="+ newTopic1 +"'>"
                    + "<i class='fa fa-calendar'></i></a>"
                    + "</div>"     
					+"<div class='size'><canvas id = 'drawLineChart' width='300px' height='250px'/></div>"
						+"<input type='button' class='button' value ='control' onclick='send_message();'/>";
				drawChart.innerHTML = tag;
			}
			else {
				tag = "<div id = 'calBtn'><a href = 'MobileHistory.jsp?name="
                     + newTopic+ "&type=" + chartChart + "&name2="+ newTopic1 +"'>"
                    + "<i class='fa fa-calendar'></i></a>"
                    + "</div>"    
					+"<div class='size'><canvas id='drawLineChart' width='300px' height='300px'></canvas></div>";
				drawChart.innerHTML = tag;
			}
		}
		if(chartChart == "gauge") {
			if((chartControl1) != ("null" || null)) {
				tag = "<div id = 'calBtn'><a href = 'MobileHistory.jsp?name="
                     + newTopic+ "&type=" + chartChart + "&name2="+ newTopic1 +"'>"
                    + "<i class='fa fa-calendar'></i></a>"
                    + "</div>"     
					+"<div class='size'><canvas id = 'drawGaugeChart' width='300px' height='250px'/></div>"
						+"<input type='button' class='button' value ='control' onclick='send_message();'/>";
				drawChart.innerHTML = tag;
			}
			else {
				tag = "<div id = 'calBtn'><a href = 'MobileHistory.jsp?name="
                     + newTopic+ "&type=" + chartChart + "&name2="+ newTopic1 +"'>"
                    + "<i class='fa fa-calendar'></i></a>"
                    + "</div>"    
					+"<div class='size'><canvas id='drawGaugeChart' width='300px' height='300px'></canvas></div>";
				drawChart.innerHTML = tag;
			}
		}
		if(chartChart == "pie") {
			if((chartControl1) != ("null" || null)) {
				tag = "<div id = 'calBtn'><a href = 'MobileHistory.jsp?name="
                     + newTopic+ "&type=" + chartChart + "&name2="+ newTopic1 +"'>"
                    + "<i class='fa fa-calendar'></i></a>"
                    + "</div>"     
					+"<div class='size'><input type='button' class='button' value ='control' onclick='send_message();'/>"
						+"<canvas id = 'drawPieChart' width='300px' height='300px'/></div>";
				drawChart.innerHTML = tag;
			}
			else {
				tag = "<div id = 'calBtn'><a href = 'MobileHistory.jsp?name="
                     + newTopic+ "&type=" + chartChart + "&name2="+ newTopic1 +"'>"
                    + "<i class='fa fa-calendar'></i></a>"
                    + "</div>"    
					+"<div class='size'><canvas id='drawPieChart' width='300px' height='250px'></canvas></div>";
				drawChart.innerHTML = tag;
			}
		}
		if(chartChart == "doughnut") {
			if((chartControl1) != ("null" || null)) {
				tag ="<div id = 'calBtn'><a href = 'MobileHistory.jsp?name="
                     + newTopic+ "&type=" + chartChart + "&name2="+ newTopic1 +"'>"
                    + "<i class='fa fa-calendar'></i></a>"
                    + "</div>"      
					+"<div class='size'><canvas id = 'drawDoughnutChart' width='300px' height='300px'/></div>"
						+"<input type='button' class='button' value ='control' onclick='send_message();'/>";
				drawChart.innerHTML = tag;
			}
			else {
				tag = "<div id = 'calBtn'><a href = 'MobileHistory.jsp?name="
                     + newTopic+ "&type=" + chartChart + "&name2="+ newTopic1 +"'>"
                    + "<i class='fa fa-calendar'></i></a>"
                    + "</div>"    
					+"<div class='size'><canvas id='drawDoughnutChart' width='300px' height='250px'></canvas></div>";
				drawChart.innerHTML = tag;
			}
		}
		if(chartChart == "value") {
			if((chartControl1) != ("null" || null)) {
				tag =  "<div class='size'><canvas id = 'drawValue' width='300px' height='300px'/></div>"
						+"<input type='button' class='button' value ='control' onclick='send_message();'/>";
				drawChart.innerHTML = tag;
			}
			else {
				tag = "<div class='size'><canvas id='drawValue' width='340px' height='300px'></canvas></div>";
				drawChart.innerHTML = tag;
			}
		}
		if(chartChart == "text") {
			tag = "<div class='size'><label id='drawText' width='340px' height='300px'>"+ topic +"</label></div>";
			drawChart.innerHTML = tag;
			
		}
		if(chartChart == "image") {
			tag = "<div class='size'><img src = 'data:image/"+chartValue1+";base64,"+topic+"' width='340px' height='300px'/></div>";
			drawChart.innerHTML = tag;
		}
		if(chartChart == "camera") {
			tag = "<div class='size'><div class='camera'></div><input type='button' class='camera_btn' value ='take a picture' onclick='send_pic_message();'/></div>";
			drawChart.innerHTML = tag;
		}
		if(chartChart == "video") {
			tag = "<div class='size'><iframe src ='http://192.168.0.6:80/?action=stream' width='340px' height='300px'></iframe></div>";
			drawChart.innerHTML = tag;
		}
	}
</script>
<body class="fixed-nav sticky-footer bg-dark" id="page-top" onload="chartDraw()">
	<!-- Navigation-->
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top"
		id="mainNav">
		<a class="navbar-brand" href="index.html">Yellow Peach</a>
	</nav>
	<div class="content-wrapper" style="background-color: #eaeaea;">
		<div class='chartName'><%= chartName %></div>
			<div class="row" id="drawChart" ></div>
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
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.bundle.min.js "></script>
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
	<script type="text/javascript">
		MQTTconnect();
	</script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.bundle.min.js "></script>
	<script src="https://code.jquery.com/jquery-2.2.4.min.js"></script>
	<script src="https://code.highcharts.com/highcharts.js"></script>
	<script src="https://code.highcharts.com/modules/data.js"></script>
	<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
	<script src="http://code.jquery.com/ui/1.8.18/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
<script src="https://ajax.aspnetcdn.com/ajax/globalize/0.1.1/globalize.min.js"></script>
</body>

</html>