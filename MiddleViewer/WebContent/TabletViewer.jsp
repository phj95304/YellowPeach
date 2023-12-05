<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="chart.ChartDTO" %>
<%@ page import="chart.ChartDAO" %>

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
	<link href="css/TabletViewer.css" rel="stylesheet">
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
		cDto = cDao.getPCDashBoard(id); //구체적인 내용을 가지고 와서 인스턴스에 담을 수 있도록
		String json = cDto.getJson();
		
	%>
	<script	src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<script	src="//cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.js"></script>
	<script >	

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
		
		var newValuegauge;
		var newValuegauge1;
		var newvaluedough;
		var newvaluedough1;
		var newvaluepie;
		var newvaluepie1;

		var mqtt;
		var reconnectTimeout = 2000;
		var host = "192.168.0.54"; //change this
		var port = 9001;
		var out_msgLine = [];
		var out_msgLine1 = [];
		var out_msgBar = [];
		var out_msgBar1 = [];
		
		var out_msgGauge;
		var out_msgGauge1;
		
		var out_msgDough;
		var out_msgDough1;
		
		var out_msgPie;
		var out_msgPie1;
		
		var out_msgLabel = [];

		var timeline = [];
		var valueline = [];
		var valueline1 = [];
		
		var timebar = [];
		var valuebar = [];
		var valuebar1 = [];
		
		var valuegauge;
		var valuegauge1;
		
		var valuedough;
		var valuedough1;
		
		var valuepie;
		var valuepie1;

		var vmsgLabel = [];
		var vmsgLine = [];
		var vmsgLine1 = [];
		
		var vmsgBar = [];
		var vmsgBar1 = [];
		
		var vmsgGauge;
		var vmsgGauge1;
		
		var vmsgDough;
		var vmsgDough1;
		
		var vmsgPie;
		var vmsgPie1;			
		
		var valueLabel = [];
		var ctx="";

		var row = [];
		var draw = [];
		var num = [];
		var tag = [];
		var chart = [];
		

		$(document).ready(function() {		
			var jData = '<%=json%>';
			var data = jData.replace(/\n/gi,"\\n");

			var obj = JSON.parse(data);
			
//			console.log("json : " + jData);
			//json데이터 잘 넘어왔는지 확인하는 부분.
			var dashName = [];
			dashName.push(obj.dashboardName);
			console.log(dashName);
			console.log(obj);
			//dashboard name 화면에 출력하는 부분
			if(dashName != null) {
				document.getElementById('dbname').innerHTML=dashName;
			}

			objlen = obj.content.length;
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
					chartNamel[j] = viewList[i][j].chartName;
					
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
			for (i = 0; i < objlen; i++) {
				for (j = 0; j < numlen; j++) {
					if (msg.destinationName) {	
						if ((viewList[i][j].chart == "value")
								&& (msg.destinationName == sensorT[i][j])) {
							out_msgLabel[i][j] = msg.payloadString;
							num = "chart" + j;

							var unit = viewList[i][j].unit;
							console.log("unit : " + unit);
							if((unit == "null")||(unit == null)) {
								unit="걸음";
							}
							if(unit == "Â°C") {
								unit="°C";
							}
							draw[j] = document.getElementById(num);							
							vmsgLabel[i][j] = parseFloat(out_msgLabel[i][j]);
							
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
						
						//pie그리기
						if ((viewList[i][j].chart == "pie") 
								&& (viewList[i][j].secondTopic != "null")
								&& (msg.destinationName == sensorT[i][j])) {
							out_msgPie = msg.payloadString;
							vmsgPie = parseInt(out_msgPie);							
						}
						// pie 1개 짜리
						if((viewList[i][j].chart == "pie") 
								&& (viewList[i][j].secondTopic == "null")
								&& (msg.destinationName == sensorT[i][j])) {
							num = "chart" + j;
							draw[j] = document.getElementById(num);		
							
							draw[j] = $('#'+num).get(0).getContext('2d');
							
							out_msgPie = msg.payloadString;
							vmsgPie = parseInt(out_msgPie);

							var chart1 = new Chart(draw[j],{
								type : 'pie',
								data : {
									labels : [viewList[i][j].firstName],
									datasets: [
								        {
								            data: [vmsgPie],
								            backgroundColor: [
								                "#ff9f40"
								            ]
								        }]
								},
								options: {
								},
							});
						}
						
						// pie 2개 짜리
						if((viewList[i][j].chart == "pie")
								&& (msg.destinationName == sensorT1[i][j])
								) {
							num = "chart" + j;
							
							draw[j] = document.getElementById(num);		

							draw[j] = $('#'+num).get(0).getContext('2d');

							out_msgPie1 = msg.payloadString;

							vmsgPie1 = parseInt(out_msgPie1);

							newvaluepie = parseInt((vmsgPie1/(vmsgPie1+vmsgPie))*100);
							newvaluepie1 = parseInt((vmsgPie/(vmsgPie1+vmsgPie))*100);

							var chart1 = new Chart(draw[j],{
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
								},
							});
						}
						//dounut그리기
						if ((viewList[i][j].chart == "doughnut") 
								&& (viewList[i][j].secondTopic != "null")
								&& (msg.destinationName == sensorT[i][j])) {
							out_msgDough = msg.payloadString;
							vmsgDough = parseInt(out_msgDough);
							
						}
						
						//doughnut 1개
						if((viewList[i][j].chart == "doughnut") 
								&& (viewList[i][j].secondTopic == "null")
								&& (msg.destinationName == sensorT[i][j])) {
							num = "chart" + j;
							draw[j] = document.getElementById(num);		
							
							draw[j] = $('#'+num).get(0).getContext('2d');

							out_msgDough = msg.payloadString;
							vmsgDough = parseInt(out_msgDough);
							
							var chart1 = new Chart(draw[j],{
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
								},
							});
						}
						
						//doughut 2개
						if((viewList[i][j].chart == "doughnut")
								&& (msg.destinationName == sensorT1[i][j])) {
							out_msgDough1 = msg.payloadString;
							
							num = "chart" + j;
							draw[j] = document.getElementById(num);	
							
							draw[j] = $('#'+num).get(0).getContext('2d');

							vmsgDough1 = parseInt(out_msgDough1);
							
							newvaluedough = parseInt(vmsgDough/(vmsgDough+vmsgDough1)*100);
							newvaluedough1 = parseInt(vmsgDough1/(vmsgDough+vmsgDough1)*100);

							var chart1 = new Chart(draw[j],{
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
							vmsgGauge = parseInt(out_msgGauge);
						}
						
						
						//gauge value 2개일 경우
						if((viewList[i][j].chart == "gauge")
								&& (msg.destinationName == sensorT1[i][j])) {
							out_msgGauge1 = msg.payloadString;
							
							num = "chart" + j;
							draw[j] = document.getElementById(num);		
							
							draw[j] = $('#'+num).get(0).getContext('2d');

							vmsgGauge1 = parseInt(out_msgGauge1);
							
							//도넛차트 옵션 변경해서 게이지 차트로 옵션 설정
							var chartOptions = {
						  	  	  rotation: -Math.PI,
								  cutoutPercentage: 30,
								  circumference: Math.PI,
								  legend: {
								    position: 'left'
								  },
								 animation: {
		                               animateScale: false,
		                               animateRotate: false
		                         },						        
							};
							
							newValuegauge = parseInt(vmsgGauge/(vmsgGauge+vmsgGauge1)*100);
							newValuegauge1 = parseInt(vmsgGauge1/(vmsgGauge1+vmsgGauge1)*100);
							
							var chart1 = new Chart(draw[j],{
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
						}
						
		                  //Bar Chart
		                  if ((viewList[i][j].chart == "bar") 
		                        && (viewList[i][j].secondTopic != "null")
		                        && (msg.destinationName == sensorT[i][j])) {
		                     out_msgBar[i][j] = msg.payloadString;
		                     vmsgBar[i][j] = parseFloat(out_msgBar[i][j]);
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
		                     var chart1 = new Chart(draw[j],{
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
//		                              animation: {
//		                                           animateScale: false,
//		                                           animateRotate: false,
//		                                           duration: 0
//		                                     },
//		                                    scaleShowLabelBackdrop : false,
		                           }
		                        }
		                     });
//		                     chart1.update({
//		                         duration: 0,
//		                         easing: 'easeOutBounce'
//		                     });
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
		                        valuebar.shift();
		                     }
		                     
		                     var chart1 = new Chart(draw[j],{
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
		                        option1 : {/*
		                           animationEnabled: false,  //change to false
		                           responsive   : false,
		                           maintainAspectRatio   : false,
		                                scaleShowLabelBackdrop : false,*/
		                        }
		                     });
//		                     chart1.update({
//		                         duration: 0,
//		                         easing: 'easeOutBounce'
//		                     });
		                  }
		                  
		                  //Line Chart
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
		                     vmsgLine[i][j] = parseFloat(out_msgLine[i][j]);

		                     num = "chart" + j;

		                     draw[j] = document.getElementById(num);
		                                          
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
		                     
		                     var chart1 = new Chart(draw[j],{
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
		                           },
//		                           animation: {
//		                                     animateScale: false,
//		                                     animateRotate: false
//		                                  },
		                        }
		                     });
//		                     chart1.update({
//		                         duration: 0,
//		                         easing: 'easeOutBounce'
//		                     });
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
		   
		                     timeline.push(time);
		                     
		                     if (timeline.length == 6) {
		                        timeline.shift();
		                     }
		                     if (valueline.length == 6) {
		                        valueline.shift();
		                     }
		                     if (valueline1.length == 6) {
		                        valueline1.shift();
		                     }
		                     var chart1 = new Chart(draw[j],{
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
		                           },
                       
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
						mqtt.subscribe(sensorT[i][j]);
						mqtt.subscribe(sensorT1[i][j]);						
					}
				}
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
			for (i = 0; i < objlen; i++) {
				for (j = 0; j < numlen; j++) {
					if((control[i][j]) && (viewList[i][j].chart == "camera")) {
						message = new Paho.MQTT.Message(control[i][j]);
						message.destinationName = control[i][j];
						message.qos=0;
						mqtt.send(message);

						console.log("pub message : " + message);
						console.log("control[i][j] : " + control[i][j]);
						
						Iurl = viewList[i][j].firstTopic;
						console.log("URL : " + Iurl);
						//URL바꾸기
						$cframe = $("<div class='camera' > <iframe src='http://"+Iurl+"/image.jpg' width='390px' height='170px' name ='123'></iframe></div>");
						$("#draw"+j).replaceWith($cframe);
					}
					else {
						console.log("nothing");
					}
				}	
			}
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


	// 차트를 row에 따라서 그려주는 부분	
		function getList() {
			list = document.getElementById("list");
//			var tag="";
			var $frame;
			var $frame0;
			var $frame1;
			var $frame2;
			var $frame3;
			var $frame4;

			var input;
			var url;
			var t = 0;
			var newTag = "";

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
			console.log("row : " + row);
			console.log("i? : " + i);					

			for (i = 0; i < objlen; i++) {
				for(t = 0; t<= row.length-1; t++) {
					tag[t] = "<table style='margin:3px; border-spacing: 10px; border-collapse: separate; '><tr>";
					for(j=0; j<numlen; j++) {
						if(viewList[i][j].row == t) {
							console.log("viewList[i][j].row:"+viewList[i][j].row);
							if ((viewList[i][j].chart == "line")
									|| (viewList[i][j].chart == "bar")
									|| (viewList[i][j].chart == "value")
									|| (viewList[i][j].chart == "video")
									|| (viewList[i][j].chart == "camera")
									|| (viewList[i][j].chart == "gauge")
									|| (viewList[i][j].chart == "text")
									|| (viewList[i][j].chart == "doughnut")
									|| (viewList[i][j].chart == "image")
									|| (viewList[i][j].chart == "pie")){
								tag[t] = tag[t] + "<td>"
								+"<canvas id = 'chart"+j+ "' width='340px' height='200px'></canvas></td>"
							}
						}
					}
					tag[t] = tag[t]+"</tr>";
					tag[t] = tag[t]+"</table>";
										
					newTag += tag[t];
					
					console.log("tag : " + newTag);
					
					list.innerHTML=newTag;
				}
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
									+ "</div><div id='btn'><a href='PCZoom.jsp?topic1=" 
									+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
									+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
									+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
									+"<div id='size'><canvas id = 'chart"+j+ "' width='300px' height='160px'></canvas>"
									+"<input type='button' value ='control' class='camera_button' onclick='send_message();'/></div>");
							$("#chart"+j).replaceWith($frame);
						}
						else {
							$frame = $("<div id='size0'>"+ viewList[i][j].chartName
									+ "</div><div id='btn'>"
									+"<a href='PCZoom.jsp?topic1=" 
									+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
									+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
									+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a>"
									+"</div>"
									+"<div id='size'><canvas id = 'chart"+j+ "' width='300px' height='180px'></canvas></div>");
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
								+ "</div><div id='btn'><a href='PCZoom.jsp?topic1=" 
								+ strTopic1 + "&topic2" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
								+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
								+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
								+"<div id='size'> <img src ='http://"+url+"/?action=stream' width='390px' height='200px'/></div>");
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
						strUnit = viewList[i][j].unit;
						if((viewList[i][j].firstControl) != ("null" || null)) {
							$frame1 =  $("<div id='value0'>"+ viewList[i][j].chartName
									+ "</div><div id='label_zoom'><a href='PCZoom.jsp?topic1=" 
									+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
									+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
									+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
									+"<div id='value_btnLabel'><canvas id = 'chart" + j + "' width='170px' height='170px'/>"
									+"<input type='button' class='value_button' value ='control' onclick='send_message();'/></div>" );
							$("#chart"+j).replaceWith($frame1);
						}
						else {
							$frame1 =  $("<div id='value0'>"+ viewList[i][j].chartName
									+ "</div><div id='label_zoom'><a href='PCZoom.jsp?topic1="
									+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
									+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
									+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
									+"<div id='value'><canvas id = 'chart"+j+ "' width='170px' height='200px'></canvas></div>");
							$("#chart"+j).replaceWith($frame1);
						}
					}
					if(viewList[i][j].chart == "image") {
						strTopic1 = viewList[i][j].firstTopic; //첫 번째 토픽
						strValue1 = viewList[i][j].firstName;

						var image = strTopic1.replace('\\','');

						$frame2 = $("<div id='value0'>"+ viewList[i][j].chartName
								+ "</div><div id='label_zoom'><a href='TabletZoom.jsp?topic=" 
								+ strTopic1 + "," + strTopic2+"," +strName+ "," + strChart  + ","
								+ strValue1 + "," + strValue2 + "," + strControl1
								+ "," + strControl2 +"'></a></div>"
								+"<div id='value'><img src = 'data:image/"+strValue1+";base64," +image +"' width='190px' height='200px'/></div>");
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
						$frame3 =  $("<div id='value0'>"+ viewList[i][j].chartName
								+ "</div><div id='label_zoom'><a href='TabletZoom.jsp?topic1=" 
								+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
								+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
								+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
								+"<div id='value'><div id ='text' width='170px' height='200px'>"
								+ viewList[i][j].firstTopic + "</div></div>");
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
								+ "</div><div id='btn'><a href='PCZoom.jsp?topic1=" 
								+ strTopic1 + "&topic2=" + strTopic2+"&name=" +strName+ "&chart=" + strChart  + "&value1="
								+ strValue1 + "&value2=" + strValue2 + "&cont1=" + strControl1
								+ "&cont2=" + strControl2 +"'><i class='fa fa-fw fa-plus''></i></a></div>"
								+"<div class='camera' id = 'draw" + j+"' width='390px' height='170px'></div>"
								+"<input type='button' class='camera_button' value ='pic' onclick='send_pic_message();'/>");
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
		<a class="navbar-brand" href="TabletMain.jsp">Yellow Peach</a>
		<button class="navbar-toggler navbar-toggler-right" type="button"
			data-toggle="collapse" data-target="#navbarResponsive"
			aria-controls="navbarResponsive" aria-expanded="false"
			aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarResponsive">
			<ul class="navbar-nav navbar-sidenav" id="exampleAccordion">
				<li class="nav-item" data-toggle="tooltip" data-placement="right"
					title="Dashboard"><a style="color:white" class="nav-link" href="Tabletlist.jsp"> <i
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
			<ol class="breadcrumb" style="background-color: white;">
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
	<script>
		MQTTconnect();
	</script>
	<script
		src="//cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.bundle.min.js "></script>
	<script src="//code.jquery.com/jquery-2.2.4.min.js"></script>
	<script src="//code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
	<script src="//code.jquery.com/ui/1.8.18/jquery-ui.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
<script src="//ajax.aspnetcdn.com/ajax/globalize/0.1.1/globalize.min.js"></script>
</body>
</html>