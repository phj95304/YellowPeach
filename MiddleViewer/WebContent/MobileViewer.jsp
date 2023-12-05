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
	<link href="css/MobileViewer.css" rel="stylesheet">
</head>
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
		
		cDto = cDao.getMobileDashBoard(id); //구체적인 내용을 가지고 와서 인스턴스에 담을 수 있도록
		String json = cDto.getJson();
	%>
	<script	src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<script	src="https://cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.js"></script>
	<script type="text/javascript">
		var list=""; // 테이블이 출력될 div영역
		var testBarName = document.getElementById("chart-1");
		var number = [];
		var numlen;
		var objlen;
		var viewList = new Array();
		var chartNamel = new Array();
		var chartName = new Array();
		var sensorT = new Array();
		var sensorT1 = new Array();
		var control = new Array(); //컨트롤 할 센서 토픽 저장
		var control1 = new Array(); //컨트롤 할 센서 토픽 저장
		
		var j;
		var i = 0;
		var k = 0;
		var l = 0; //row 배열 만들때 필요한 변수
		var count = 0; // topic의 개수 구하는 것.

		var mqtt;
		var reconnectTimeout = 2000;
		var host = "192.168.0.54"; //change this
		var clientId="clientMobile";
		var port = 9001;
		var out_msgLine = new Array();
		var out_msgLine1 = new Array();
		var out_msgBar = new Array();
		var out_msgBar1 = new Array();
		var out_msgGauge;
		var out_msgGauge1;
		var out_msgDough;
		var out_msgDough1;
		var out_msgPie;
		var out_msgPie1;
		var out_msgLabel = new Array();
		
		var timeline = new Array();
		var valueline = new Array();
		var valueline1 = new Array();
		var timebar = new Array();
		var valuebar = new Array();
		var valuebar1 = new Array();
		var valuegauge;
		var valuegauge1;
		var valuedough;
		var valuedough1;
		var valuepie;
		var valuepie1;

		var newValuegauge;
		var newValuegauge1;
		var newvaluedough;
		var newvaluedough1;
		var newvaluepie;
		var newvaluepie1;
		
		var vmsgLabel = new Array();
		var vmsgLine = new Array();
		var vmsgLine1 = new Array();
		var vmsgBar = new Array();
		var vmsgBar1 = new Array();
		var vmsgGauge;
		var vmsgGauge1;
		var vmsgDough;
		var vmsgDough1;
		var vmsgPie;
		var vmsgPie1;

		var row = new Array();
		var draw = new Array();
		var num = new Array();
		var valueLabel = [];
		var ctx = "";

		$(document).ready(function() {
			var jData = '<%=json%>';
			var data = jData.replace(/\n/gi,"\\n");
			//json데이터 잘 넘어왔는지 확인하는 부분.
			var obj = JSON.parse(data);
			
			
			var isVirtual = [];//선택한 대시보드가 가상 IoT 시스템의 대시보드인지 확인
			isVirtual.push(obj.isVirtual);
			console.log("isVirtual : " +isVirtual);
			
			var virtualName =[];//가상 IoT 시스템의 이름, 가상 IoT가 아닐경우에는 null
			virtualName.push(obj.systemName);
			console.log("virtualName: " +virtualName);
			if(isVirtual==true || isVirtual=="true"){
				pubVirtual = new Paho.MQTT.Message(virtualName);
				pubVirtual.destinationName = "isVirtual";//change this 
				message.qos=0;
				mqtt.send(pubVirtual);
				console.log("you choose virtual IoT System");
			}
			
			//dashboard name 화면에 출력하는 부분
			var dashName = [];
			dashName.push(obj.dashboardName);
			console.log(dashName);

			
			if(dashName != null) {
				document.getElementById('dbname').innerHTML=dashName;
			}
			//json데이터 중 content 배열 출력
			console.log(obj.content);

			objlen = obj.content.length;
			console.log("objlen : " + objlen);
			//chart의 개수를 구하는 부분
			for ( var i in obj.content[0]) {
				var x = parseInt(i);
				number.push(x);
			}

			numlen = number.length;

			//viewList 2차원 배열 만드는 부분.
			for (i = 0; i < objlen; i++) {
				viewList[i] = new Array(numlen);
				sensorT[i] = new Array(numlen);
				sensorT1[i] = new Array(numlen);
				control[i] = new Array(numlen);
				control1[i] = new Array(numlen);

				out_msgLine[i] = new Array(numlen);
				out_msgLine1[i] = new Array(numlen);
				out_msgBar[i] = new Array(numlen);
				out_msgBar1[i] = new Array(numlen);
				out_msgLabel[i] = new Array(numlen);
				
				vmsgLine[i] = new Array(numlen);
				vmsgLine1[i] = new Array(numlen);
				vmsgBar[i] = new Array(numlen);
				vmsgBar1[i] = new Array(numlen);
				vmsgLabel[i] = new Array(numlen);
			}

			//viewList로부터 content Array를 받아서 viewList배열에 저장하는 부분.
			//firstTopic 저장하는 부분.
			for (i = 0; i < objlen; i++) {
				for (j = 0; j < numlen; j++) {
					viewList[i][j] = obj.content[i][j];
					console.log(viewList[i][j]);
					console.log(viewList[i][j].chart); //content중 chart종류 출력
					chartNamel[j] = viewList[i][j].chartName;
					//console.log(chartNamel[j]);
					
					if ((viewList[i][j].chart == "line")
							|| (viewList[i][j].chart == "value")
							|| (viewList[i][j].chart == "bar")
							|| (viewList[i][j].chart == "gauge")
							|| (viewList[i][j].chart == "pie")
							|| (viewList[i][j].chart == "doughnut")
							|| (viewList[i][j].chart == "camera")) {
						sensorT[i][j] = viewList[i][j].firstTopic;
						sensorT1[i][j] = viewList[i][j].secondTopic;
						control[i][j] = viewList[i][j].firstControl;
						control1[i][j] = viewList[i][j].secondControl;
					}
				}
			}
		});
		function onFailure(message) { //
			console.log("Failed");
			setTimeout(MQTTconnect, reconnectTimeout);
		}

		function onMessageArrived(msg) {
			console.log("message Arrived");
			for (i = 0; i < objlen; i++) {
				for (j = 0; j < numlen; j++) {
					if (msg.destinationName) {						
						if ((viewList[i][j].chart == "value")
								&& (msg.destinationName == sensorT[i][j])
								&& (msg.destinationName != "SmartHome/miraeb1/flame")) {
							 out_msgLabel[i][j] = msg.payloadString;
		                     num = "chart" + j;
		                     
		                     var unit = viewList[i][j].unit;
		                     
		                     draw[j] = document.getElementById(num);                     
		                     vmsgLabel[i][j] = parseFloat(out_msgLabel[i][j]);
		                    // if((unit == "null")||(unit == null)) {
									//unit="걸음";
								//}
								if(unit == "Â°C") {
									unit="°C";
								}
		                     valueLabel.push(vmsgLabel[i][j]);
		                     
		                     ctx = draw[j].getContext("2d");
		                     ctx.font = "25px malgun gothic";
		                     ctx.fillStyle = "rgba(0,0,0,1)";
		                     if(valueLabel.length == 2) {
		                         valueLabel.shift();
		                         ctx.clearRect(0, 0, 200, 200);
		                     }
		                     ctx.fillText(valueLabel[0] + unit, 17,90);  
						}
						if ((viewList[i][j].chart == "value")
								&& (msg.destinationName == "SmartHome/miraeb1/flame")) {
							 out_msgLabel[i][j] = msg.payloadString;
		                     num = "chart" + j;
		                     draw[j] = document.getElementById(num);
		                     

		                     if(out_msgLabel[i][j]<=20) {
								 valueLabel.push(out_msgLabel[i][j]);

		                    	 //화재발생
		                    	 ctx = draw[j].getContext("2d");
			                     ctx.font = "25px malgun gothic";
			                     ctx.fillStyle = "rgba(255,0,64,1)";
		                    	 if(valueLabel.length == 2) {
				                     valueLabel.shift();
				                     ctx.clearRect(0, 0, 300, 300);
				                 }
			                     ctx.fillText("화재 발생!!", 110,110);  
		                     }
		                     else {
								 valueLabel.push(out_msgLabel[i][j]);

		                    	 //화재 감지중
		                    	 ctx = draw[j].getContext("2d");
			                     ctx.font = "25px malgun gothic";
			                     ctx.fillStyle = "rgba(0,0,0,1)";
			                     if(valueLabel.length == 2) {
				                     valueLabel.shift();
				                     ctx.clearRect(0, 0, 300, 300);
				                 }
			                     ctx.fillText("화재 감지중", 110,110);
		                     }                    		                   		                     
		                     
						}
						
						//pie그리기
						if ((viewList[i][j].chart == "pie") 
								&& (viewList[i][j].secondTopic != "null")
								&& (msg.destinationName == sensorT[i][j])) {
							out_msgPie = msg.payloadString;
							vmsgPie = parseFloat(out_msgPie);
						}
						
						// pie 1개 짜리
						if((viewList[i][j].chart == "pie") 
								&& (viewList[i][j].secondTopic == "null")
								&& (msg.destinationName == sensorT[i][j])) {
							num = "chart" + j;
							draw[j] = document.getElementById(num);		

							new Chart(draw[j],{
								type : 'pie',
								data : {
									labels : [viewList[i][j].firstName],
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
							        showAllTooltips: true
								},
							});
							chart1.update({
							    duration: 0,
							    easing: 'easeOutBounce'
							});
						}
						
						// pie 2개 짜리
						if((viewList[i][j].chart == "pie")
								&& (msg.destinationName == sensorT1[i][j])) {
							num = "chart" + j;
							draw[j] = document.getElementById(num);		

							out_msgPie1 = msg.payloadString;
							vmsgPie1 = parseFloat(out_msgPie1);
							
							newvaluepie = vmsgPie1/(vmsgPie1+vmsgPie)*100;
							newvaluepie1 = vmsgPie/(vmsgPie1+vmsgPie)*100;
							
							new Chart(draw[j],{
								type : 'pie',
								data : {
									labels : [viewList[i][j].firstName,viewList[i][j].secondName],
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
							        showAllTooltips: true
								},
							});
							chart1.update({
							    duration: 0,
							    easing: 'easeOutBounce'
							});
						}

						//dounut그리기
						if ((viewList[i][j].chart == "doughnut") 
								&& (viewList[i][j].secondTopic != "null")
								&& (msg.destinationName == sensorT[i][j])) {
							out_msgGauge = msg.payloadString;
							vmsgDough = parseFloat(out_msgGauge);
						}
						//doughnut 1개
						if((viewList[i][j].chart == "doughnut") 
								&& (viewList[i][j].secondTopic == "null")
								&& (msg.destinationName == sensorT[i][j])) {
							num = "chart" + j;
							draw[j] = document.getElementById(num);		

							new Chart(draw[j],{
								type : 'doughnut',
								data : {
									labels : [viewList[i][j].firstName],
									datasets : [ {
										data : [vmsgDough],
										backgroundColor: [
							            	"#ff9f40"
							            ],
									}]
								},
								options: {
							        showAllTooltips: true
								},
							});
							chart1.update({
							    duration: 0,
							    easing: 'easeOutBounce'
							});
						}
						
						//doughut 2개
						if((viewList[i][j].chart == "doughnut")
								&& (msg.destinationName == sensorT1[i][j])) {
							out_msgDough1 = msg.payloadString;
							
							num = "chart" + j;
							draw[j] = document.getElementById(num);		

							vmsgDough1 = parseFloat(out_msgDough1);
							
							newvaluedough = vmsgDough/(vmsgDough+vmsgDough1)*100;
							newvaluedough1 = vmsgDough1/(vmsgDough+vmsgDough1)*100;
							new Chart(draw[j],{
								type : 'doughnut',
								data : {
									labels : [viewList[i][j].firstName, viewList[i][j].secondName],
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
						if ((viewList[i][j].chart == "gauge") 
								&& (viewList[i][j].secondTopic != "null")
								&& (msg.destinationName == sensorT[i][j])) {
							out_msgGauge = msg.payloadString;
							vmsgGauge = parseFloat(out_msgGauge);
							console.log("gauge"+vmsgGauge);
						}
						
						
						//gauge value 2개일 경우
						if((viewList[i][j].chart == "gauge")
								&& (msg.destinationName == sensorT1[i][j])) {
							out_msgGauge1 = msg.payloadString;
							
							num = "chart" + j;
							draw[j] = document.getElementById(num);		
							
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
							
							new Chart(draw[j],{
								type: 'doughnut',
								data : {
									labels: [
										viewList[i][j].firstName, viewList[i][j].secondName
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
							chart1.update({
							    duration: 0,
							    easing: 'easeOutBounce'
							});
						}
		                  if ((viewList[i][j].chart == "bar") 
		                          && (viewList[i][j].secondTopic != "null")
		                          && (msg.destinationName == sensorT[i][j])) {
		                       out_msgBar[i][j] = msg.payloadString;
		                       vmsgBar[i][j] = parseFloat(out_msgBar[i][j]);
		                       console.log("bar : "+vmsgBar[i][j]);
		                    }

		                    //bar value 1개 일 때 sensorT받아와서 그리기
		                    if ((viewList[i][j].chart == "bar") 
		                          && (viewList[i][j].secondTopic == "null")
		                          && (msg.destinationName == sensorT[i][j])) {
		                       out_msgBar[i][j] = msg.payloadString;
		                       vmsgBar[i][j] = parseFloat(out_msgBar[i][j]);
		                       
		                       num = "chart" + j;

		                       draw[j] = document.getElementById(num);
		                       
		                       vmsgBar[i][j] = parseFloat(out_msgBar[i][j]);
		                       
		                       var now = new Date();
		                       var nowmin = now.getMinutes();
		                       var nowsecond = now.getSeconds();
		                       
		                       var time = nowmin + "m" + nowsecond+"s";

		                       valuebar.push(vmsgBar[i][j]);                     
		                       timebar.push(time);
		                       
		                       if (timebar.length == 6) {
		                          timebar.shift();
		                       }
		                       if (valuebar.length == 6) {
		                          valuebar.shift();
		                       }
		                       
		                       new Chart(draw[j],{
		                          type : 'bar',
		                          data : {
		                             labels : [ timebar[0],
		                                timebar[1],
		                                timebar[2],
		                                timebar[3],
		                                timebar[4]],
		                             datasets : [ {
		                                label : viewList[i][j].firstName,
		                                data : [valuebar[0],
		                                      valuebar[1],
		                                      valuebar[2],
		                                      valuebar[3],
		                                      valuebar[4]],
		                                backgroundColor : [
		                                   '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5' ],
		                                borderColor : [
		                                      '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5' ],
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
		                    if((viewList[i][j].chart == "bar") 
		                          && (msg.destinationName == sensorT1[i][j])) {
		                       out_msgBar1[i][j] = msg.payloadString;
		                       
		                       num = "chart" + j;

		                       draw[j] = document.getElementById(num);

		                       vmsgBar1[i][j] = parseFloat(out_msgBar1[i][j]);
		                       
		                       var now = new Date();
		                       var nowmin = now.getMinutes();
		                       var nowsecond = now.getSeconds();
		                       
		                       var time = nowmin + "m" + nowsecond+"s";

		                       valuebar.push(vmsgBar[i][j]);
		                       
		                       valuebar1.push(vmsgBar1[i][j]);

		                       timebar.push(time);
		                       
		                       if (timebar.length == 6) {
		                          timebar.shift();
		                       }
		                       if (valuebar.length == 6) {
		                          valuebar.shift();
		                       }
		                       if (valuebar1.length == 6) {
		                          valuebar1.shift();
		                       }
		                       new Chart(draw[j],{
		                          type : 'bar',
		                          data : {
		                             labels : [ timebar[0],
		                                timebar[1],
		                                timebar[2],
		                                timebar[3],
		                                timebar[4]],
		                             datasets : [ {
		                                label : viewList[i][j].firstName,
		                                data : [valuebar[0],
		                                      valuebar[1],
		                                      valuebar[2],
		                                      valuebar[3],
		                                      valuebar[4]],
		                                backgroundColor : [
		                                      '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5' ],
		                                borderColor : [
		                                      '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5' ],
		                                borderWidth : 1
		                             },
		                             {
		                                label : viewList[i][j].secondName,
		                                data : [valuebar1[0],
		                                      valuebar1[1],
		                                      valuebar1[2],
		                                      valuebar1[3],
		                                      valuebar1[4]],
		                                backgroundColor : [
		                                   '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa' ],
		                                borderColor : [
		                                      '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa' ],
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
		                    
		                    if ((viewList[i][j].chart == "line") 
		                          && (viewList[i][j].secondTopic != "null")
		                          && (msg.destinationName == sensorT[i][j])) {
		                       out_msgLine[i][j] = msg.payloadString;
		                       vmsgLine[i][j] = parseFloat(out_msgLine[i][j]);
		                    }
		                    
		                    //line value 1개 일 때 sensorT받아와서 그리기
		                    if ((viewList[i][j].chart == "line") 
		                          && (viewList[i][j].secondTopic == "null")
		                          && (msg.destinationName == sensorT[i][j])) {
		                       out_msgLine[i][j] = msg.payloadString;
		                       
		                       num = "chart" + j;

		                       draw[j] = document.getElementById(num);
		                       
		                       vmsgLine[i][j] = parseFloat(out_msgLine[i][j]);
		                       
		                       var now = new Date();
		                       var nowmin = now.getMinutes();
		                       var nowsecond = now.getSeconds();
		                       
		                       var time = nowmin + "m" + nowsecond+"s";

		                       valueline.push(vmsgLine[i][j]);                     
		                       timeline.push(time);
		                       
		                       if (timeline.length == 6) {
		                          timeline.shift();
		                       }
		                       if (valueline.length == 6) {
		                          valueline.shift();
		                       }
		                       
		                       new Chart(draw[j],{
		                          type : 'line',
		                          data : {
		                             labels : [ timeline[0],
		                                timeline[1],
		                                timeline[2],
		                                timeline[3],
		                                timeline[4]],
		                             datasets : [ {
		                                label : viewList[i][j].firstName,
		                                data : [valueline[0],
		                                      valueline[1],
		                                      valueline[2],
		                                      valueline[3],
		                                      valueline[4]],
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
		                    if((viewList[i][j].chart == "line") 
		                          && (msg.destinationName == sensorT1[i][j])) {
		                       out_msgLine1[i][j] = msg.payloadString;
		                       
		                       num = "chart" + j;

		                       draw[j] = document.getElementById(num);
		                       
		                       vmsgLine1[i][j] = parseFloat(out_msgLine1[i][j]);
		                       
		                       var now = new Date();
		                       var nowmin = now.getMinutes();
		                       var nowsecond = now.getSeconds();
		                       
		                       var time = nowmin + "m" + nowsecond+"s";

		                       valueline.push(vmsgLine[i][j]);
		                       valueline1.push(vmsgLine1[i][j]);
		                       
		                       timeline.unshift(time);
		                       
		                       if (timeline.length == 6) {
		                          timeline.shift();
		                       }
		                       if (valueline.length == 6) {
		                          valueline.shift();
		                       }
		                       if (valueline1.length == 6) {
		                          valueline1.shift();
		                       }
		                       
		                       new Chart(draw[j],{
		                          type : 'line',
		                          data : {
		                             labels : [ timeline[0],
		                                timeline[1],
		                                timeline[2],
		                                timeline[3],
		                                timeline[4]],
		                             datasets : [ {
		                                label : viewList[i][j].firstName,
		                                data : [valueline[0],
		                                      valueline[1],
		                                      valueline[2],
		                                      valueline[3],
		                                      valueline[4]],
		                                borderColor : "#ff6384",
		                                fill : false
		                             },
		                             {
		                                label : viewList[i][j].secondName,
		                                data : [valueline1[0],
		                                      valueline1[1],
		                                      valueline1[2],
		                                      valueline1[3],
		                                      valueline1[4]],
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
			}
		}

		function onConnect() { //
			// Once a connection has been made, make a subscription and send a message.
			console.log("Connected ");
			for (i = 0; i < objlen; i++) {
				for (j = 0; j < numlen; j++) {
					if ((viewList[i][j].chart == "line")
							|| (viewList[i][j].chart == "value")
							|| (viewList[i][j].chart == "gauge")
							|| (viewList[i][j].chart == "bar")
							|| (viewList[i][j].chart == "doughnut")
							|| (viewList[i][j].chart == "pie")) {
						mqtt.subscribe(sensorT[i][j]); //
						mqtt.subscribe(sensorT1[i][j]);
					}
				}
			}
		}

		function MQTTconnect() { //
			clientId= clientId + Math.floor(Math.random() * 10000) + 1;
			console.log("connecting to " + host + " " + port+ " clientId: "+ clientId);
			mqtt = new Paho.MQTT.Client(host, port, clientId); //client object생성
			var options = {
				timeout : 3,
				onSuccess : onConnect, //Callback function
				onFailure : onFailure,
			};
			mqtt.connect(options); //connect
			mqtt.onMessageArrived = onMessageArrived
		}
		
		function load_pic(Iurl){
			setTimeout(function(){ 
				$('#camera_iframe').attr('src',Iurl);
				console.log("Iurl " + Iurl);     
				},1000);
			
		}
		function send_pic_message(strTopic) {
			var $frame0;
			var Iurl;
			var desTopic = "SmartFarm/miraeb1/IMG";//change this
			console.log("strTopic is : "+strTopic);
			//for (i = 0; i < objlen; i++) {
				//for (j = 0; j < numlen; j++) {
					//if((viewList[i][j].chart == "camera")) {
						message = new Paho.MQTT.Message(desTopic);
						message.destinationName = desTopic;
						message.qos=0;
						mqtt.send(message);

						console.log("pub message : " + message);

						Iurl = "http://" +strTopic+"/image.jpg";
						load_pic(Iurl);
					
						//$cframe = $("<div class='camera' > <iframe src='http://113.198.85.5:8000/image.jpg' width='340px' height='170px' name ='123'></iframe></div>");
						
					//	$("#draw"+j).replaceWith($cframe);
					//}
				//	else {
				//		console.log("nothing");
				//	}
				
					
				//}	
			//}
			
		}
		function send_message() {
			var $frame0;
			var Iurl;
			for (i = 0; i < objlen; i++) {
				for (j = 0; j < numlen; j++) {
					if((control[i][j]) && (viewList[i][j].chart != "camera")) {
						message = new Paho.MQTT.Message(control[i][j]);
						message.destinationName = control[i][j];
						message.qos=0;
						mqtt.send(message);
						console.log("control[i][j] : " + control[i][j]);
					}
					else {
						console.log("nothing");
					}
				}	
			}
		}
		//배열의 요소 중복 제거
		function uniqueArray(arr) {
	        var a = {};
	        for (var i = 0; i < arr.length; i++) {
	            if (typeof a[arr[i]] == 'undefined') {
	                a[arr[i]] = 1;
	            }
	        }
	        arr.length = 0;
	        for (var i in a) {
	            arr[arr.length] = i;
	        }
	        return arr;
	    }


		function getList() {
			list = document.getElementById("list");
			var tag="";
			var $frame0;
			var $frame1;
			var $frame2;
			var $frame3;
			var $frame4;	
			
			var input;
			var url;
			var strTopic1; //차트 토픽1, text내용, url주소
			var strTopic2; //차트 토픽2
			var strName; //차트 이름
			var strChart; //차트 종류
			var strValue1; // value이름
			var strValue2; // value이름2
			var strControl1; //control토픽
			var strControl2; //control토픽2

			//나중에 한번에 list를 리턴해 줌으로써 테이블을 그린다
			for (i = 0; i < objlen; i++) {
				for(j=0; j<numlen; j++) {
					row[l] = viewList[i][j].row;
					l++;
				}
			}
			uniqueArray(row);	
			
			tag = "<table style='margin:3px; border-spacing: 10px; border-collapse: separate; width: 100%;'>";
		
			for (i = 0; i < objlen; i++) {
				for(j=0; j<numlen; j++) {
					if ((viewList[i][j].chart == "line")
							|| (viewList[i][j].chart == "bar")
							|| (viewList[i][j].chart == "value")
							|| (viewList[i][j].chart == "video")
							|| (viewList[i][j].chart == "camera")
							|| (viewList[i][j].chart == "gauge")
							|| (viewList[i][j].chart == "text")
							|| (viewList[i][j].chart == "doughnut")
							|| (viewList[i][j].chart == "image")
							|| (viewList[i][j].chart == "pie")) {
						tag = tag + "<tr><td>"
						
						+ "<canvas id = 'chart"+j+ "' width='300px' height='300px'/>"
						+ "</td></tr>";	
					}
				}
				tag =tag +"</table>";
				console.log("tag : " + tag);
				list.innerHTML=tag;
			}
			for (i = 0; i < objlen; i++) {
				for(j=0; j<numlen; j++) {
					if((viewList[i][j].chart == "line")
							|| (viewList[i][j].chart == "bar")
							|| (viewList[i][j].chart == "doughnut")
							|| (viewList[i][j].chart == "gauge")
							|| (viewList[i][j].chart == "pie")) {
						strTopic1 = viewList[i][j].firstTopic; //첫 번째 토픽
						strTopic2 = viewList[i][j].secondTopic; //두 번째 토픽
						strName = viewList[i][j].chartName; //차트 이름
						strChart = viewList[i][j].chart; // 차트 종류
						strValue1 = viewList[i][j].firstName;
						strValue2 = viewList[i][j].secondName;
						strControl1 = viewList[i][j].firstControl;
						strControl2 = viewList[i][j].secondControl;
						if((viewList[i][j].firstControl) != ("null" || null)) {
							$frame = $("<div id='size0'>"+ viewList[i][j].chartName
									+ "</div><div id='btn'><a href='MobileZoom.jsp?topic1="
									+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
									+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
									+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
									+"<div id='size'><canvas id = 'chart"+j+ "' width='200px' height='150px'></canvas>"
									+"<input type='button' value ='control' class='camera_button' onclick='send_message();'/></div>");
							$("#chart"+j).replaceWith($frame);
						}
						else {
							$frame = $("<div id='size0'>"+ viewList[i][j].chartName
									+ "</div><div id='btn'><a href='MobileZoom.jsp?topic1="
									+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
									+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
									+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
									+"<div id='size'><canvas id = 'chart"+j+ "' width='300px' height='250px'></canvas></div>");
							$("#chart"+j).replaceWith($frame);
						}
					}
					if(viewList[i][j].chart == "video") {						
						strTopic1 = viewList[i][j].firstTopic; //첫 번째 토픽
						strTopic2 = viewList[i][j].secondTopic; //두 번째 토픽
						strName = viewList[i][j].chartName; //차트 이름
						strChart = viewList[i][j].chart; // 차트 종류
						strValue1 = viewList[i][j].firstName;
						strValue2 = viewList[i][j].secondName;
						strControl1 = viewList[i][j].firstControl;
						strControl2 = viewList[i][j].secondControl;
						url = viewList[i][j].firstTopic;
						$frame0 = $("<div id='size0'>"+ viewList[i][j].chartName
								+ "</div><div id='btn'><a href='MobileZoom.jsp?topic1="
								+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
								+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
								+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
								+"<div id='size'> <img src ='http://"+url+"/?action=stream' width='300px' height='300px'/></div>");
						//<img src="/?action=stream" />
						$("#chart"+j).replaceWith($frame0);
					}
					if(viewList[i][j].chart == "value") {
						strTopic1 = viewList[i][j].firstTopic; //첫 번째 토픽
						strTopic2 = viewList[i][j].secondTopic; //두 번째 토픽
						strName = viewList[i][j].chartName; //차트 이름
						strChart = viewList[i][j].chart; // 차트 종류
						strValue1 = viewList[i][j].firstName;
						strValue2 = viewList[i][j].secondName;
						strControl1 = viewList[i][j].firstControl;
						strControl2 = viewList[i][j].secondControl;
						if((viewList[i][j].firstControl) != ("null" || null)) {
							$frame1 =  $("<div id='size0'>"+ viewList[i][j].chartName
									+ "</div><div id='btn'><a href='MobileZoom.jsp?topic1="
									+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
									+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
									+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
									+"<div id='size'><canvas id = 'chart"+j+ "' width='300px' height='260px'></canvas>"
									+"<input type='button' value ='control' class='camera_button' onclick='send_message();'/></div>");
							$("#chart"+j).replaceWith($frame1);
						}
						else {
							$frame1 =  $("<div id='size0'>"+ viewList[i][j].chartName
									+ "</div><div id='btn'><a href='MobileZoom.jsp?topic1="
									+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
									+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
									+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
									+"<div id='size'><canvas id = 'chart"+j+ "' width='300px' height='300px'></canvas></div>" );
							$("#chart"+j).replaceWith($frame1);
						}
					}
					if(viewList[i][j].chart == "image") {
						strTopic1 = viewList[i][j].firstTopic; //첫 번째 토픽
						strTopic2 = viewList[i][j].secondTopic; //두 번째 토픽
						strName = viewList[i][j].chartName; //차트 이름
						strChart = viewList[i][j].chart; // 차트 종류
						strValue1 = viewList[i][j].firstName;
						strValue2 = viewList[i][j].secondName;
						strControl1 = viewList[i][j].firstControl;
						strControl2 = viewList[i][j].secondControl;

						var image = strTopic1.replace('\\','');
						//src="data:image/png;base64,
						$frame2 = $("<div id='size0'>"+ viewList[i][j].chartName
								+ "</div><div id='btn'><a href='MobileZoom.jsp?topic=" 
								+ strTopic1 + "," + strTopic2+"," +strName+ "," + strChart  + ","
								+ strValue1 + "," + strValue2 + "," + strControl1
								+ "," + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
								+"<div id='size'><img src = 'data:image/"+strValue1+";base64," +image +"' width='300px' height='300px'/></div>");
						$("#chart"+j).replaceWith($frame2);
					}
					
					if(viewList[i][j].chart == "text") {
						strTopic1 = viewList[i][j].firstTopic; //첫 번째 토픽
						strTopic2 = viewList[i][j].secondTopic; //두 번째 토픽
						strName = viewList[i][j].chartName; //차트 이름
						strChart = viewList[i][j].chart; // 차트 종류
						strValue1 = viewList[i][j].firstName;
						strValue2 = viewList[i][j].secondName;
						strControl1 = viewList[i][j].firstControl;
						strControl2 = viewList[i][j].secondControl;
						
						$frame3 =  $("<div id='size0'>"+ viewList[i][j].chartName
								+ "</div><div id='btn'><a href='MobileZoom.jsp?topic1="
								+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
								+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
								+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
								+"<div id='size'><label id ='text' width='300px' height='300px'>"
								+ viewList[i][j].firstTopic + "</label></div>");
						$("#chart"+j).replaceWith($frame3);
						
					}
					if(viewList[i][j].chart == "camera") {
						strTopic1 = viewList[i][j].firstTopic; //첫 번째 토픽
						strTopic2 = viewList[i][j].secondTopic; //두 번째 토픽
						strName = viewList[i][j].chartName; //차트 이름
						strChart = viewList[i][j].chart; // 차트 종류
						strValue1 = viewList[i][j].firstName;
						strValue2 = viewList[i][j].secondName;
						strControl1 = viewList[i][j].firstControl;
						strControl2 = viewList[i][j].secondControl;

						$frame4 = $("<div id='size0'>"+ viewList[i][j].chartName
								+ "</div><div id='btn'><a href='MobileZoom.jsp?topic1="
								+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
								+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
								+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
								+"<div class='camera' id = 'draw" + j+"' width='340px' height='300px'>"
								+"<iframe id='camera_iframe' src='http://"+strTopic1+"/image.jpg' width='340' height='300'  name ='123'></iframe>"
								+"</div>"
								+"<input type='button' class='camera_button' style='width:340px;' value ='pic' onclick='send_pic_message(\""+strTopic1+"\");'/>");
						$("#chart"+j).replaceWith($frame4);
					}
				}
			}
		}
	</script>
<body class="fixed-nav sticky-footer bg-dark" id="page-top" onload="getList()">
	<!-- Navigation-->
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top"
		id="mainNav">
		<a class="navbar-brand" href="index.html">Yellow Peach</a>
		<button class="navbar-toggler navbar-toggler-right" type="button"
			data-toggle="collapse" data-target="#navbarResponsive"
			aria-controls="navbarResponsive" aria-expanded="false"
			aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarResponsive">
			<ul class="navbar-nav navbar-sidenav" id="exampleAccordion">
				<li class="nav-item" data-toggle="tooltip" data-placement="right"
					title="Dashboard"><a style="color:white" class="nav-link" href="Mobilelist.jsp"> <i
						class="fa fa-fw fa-dashboard"></i> <span style="color:white" class="nav-link-text">Dashboard</span>
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
			<ol class="breadcrumb"  style="background-color: white;">
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
