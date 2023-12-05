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
<style>
   .main {
      background-color: #343a40;
      width: 100%;
      height: 60px;
      color: white;
      padding-left: 15px;
      padding-top: 15px;
      font-size: 25px;
   }
      .hist {
      position: relative;
      width: 700px;
      height: 50px;
      margin:0 auto;
      background-color: white;
      border-radius: 0.25rem;
      padding-top: 10px;
      margin-top : 25px;
   }
   .canv {
      position: relative;
      width: 700px;
      height: 550px;
      margin:0 auto;
      background-color: white;
      border-radius: 0.25rem;
      margin-top: 15px;
   }
</style>
<%
   String name = null;
   String name2 = null;
   String topic = null;
   String topic2 = null;
   String type = null;
   if (request.getParameter("name") != null) {
      topic = (String) request.getParameter("name");
      name = (String) request.getParameter("name").replace("/", "_");
      
   }

   if (request.getParameter("type") != null) {
      type = (String) request.getParameter("type");
      System.out.println(type);
      //type ="pie";
   }
   
   if (request.getParameter("name2") != null) {
      topic2 = (String) request.getParameter("name2");
      name2 = (String) request.getParameter("name2").replace("/", "_");
      System.out.println(name2);
   }
   if (request.getParameter("name2") == null) {
      name2=null;
   }

   HistoryDAO historyDAO = new HistoryDAO();
   HistoryDTO history = new HistoryDTO();
   System.out.println(name);

   ArrayList<HistoryDTO> historyList = new HistoryDAO().getHistory(name);
   ArrayList<HistoryDTO> historyList2 = new HistoryDAO().getHistory(name2);
   System.out.println(historyList);
   System.out.println("-----------------------------");
   System.out.println(historyList2);
   int size = historyList.size();
   int size2 = historyList2.size();
%>
<head>
<title>PC history</title>
<link rel="stylesheet" href="css/pikaday.css">
<link rel="stylesheet" href="css/site.css">
<!-- Bootstrap core CSS-->
<link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<!-- Custom fonts for this template-->
<link href="vendor/font-awesome/css/font-awesome.min.css"
   rel="stylesheet" type="text/css">
<!-- Page level plugin CSS-->
<!-- Custom styles for this template-->
<link href="css/startbootstrap.css" rel="stylesheet">
<style>
	.copyright {
		margin-top: 30px;
		text-align: center;
	}
</style>
</head>
<body style="background-color: #eaeaea;" >
      <div class="main">
		<a href='javascript:history.back()' style="color: white">DashBoard Viewer</a>
      </div>
         
         <div class="hist" id="list" >
         <label for="datepicker"> &nbsp;&nbsp;&nbsp; Select date: &nbsp; &nbsp;</label>
         <input type="text"id="datepicker" placeholder="yyyy-mm-dd">
         </div>
         <div class="canv">
            <canvas id="hChart" width="200" height="150"></canvas>
      </div>
      <footer>
		<div class="copyright">
			<small>Copyright © Yellow Peach 2018</small>
		</div>
	</footer>
</body>



<!-- load moments.js before pikaday.js  -->
<script
   src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script
   src="https://cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.js"></script>
<script src="js/moments.js"></script>
<script src="js/pikaday.js"></script>

<script
   src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
<script
   src="https://ajax.aspnetcdn.com/ajax/globalize/0.1.1/globalize.min.js"></script>
<script
   src="//cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
<script src="//ajax.aspnetcdn.com/ajax/globalize/0.1.1/globalize.min.js"></script>

<script language=javascript>
   var name = '<%=name%>';//strTopic
   var name2 = '<%=name2%>';//strTopic2
   var topic ='<%=topic%>';//토픽
   var topic2 ='<%=topic2%>';//토픽2
   var type = '<%=type%>';
   var size = '<%=size%>'; //현재 테이블에 저장된 데이터 개수
   var size2 = '<%=size2%>';
   var time = new Array();//DB에서 불러온 데이터 저장할 변수
   var time2 = new Array();//DB에서 불러온 데이터 저장할 변수
   var data = new Array();//DB에서 불러온 데이터 저장할 변수
   var data2 = new Array();//DB에서 불러온 데이터 저장할 변수2
   var drawD = new Array(24);//차트 y축 24크기의 배열
   var drawD2 = new Array(24);//차트 y축 24크기의 배열
   var cdata = new Array();
   var cdata2 = new Array();
   var sdy ;
   var year;//
   var month;
   var day;
   var hour = 0;
   var h = 0;
   var count;
   var count2;
   var c = document.getElementById("hChart").getContext('2d');
   var myChart = new Chart(c);
   
   
   console.log("현재 선택한 토픽 과거 저장된 history size" + size);
   console.log("현재 선택한 토픽 과거 저장된 history size" + size2);
   //time을 2차원 배열 만들기 
//   for (var i = 0; i < size; i++) {
//      time[i] = new Array(4);
//   }

      for (var i = 0; i < size+1; i++) {
         cdata[i]=0;
         cdata[i] = new Array();
   }
      
      for (var i = 0; i < size2+1; i++) {
         cdata2[i]=0;
         cdata2[i] = new Array();
   }
   

<%for (int i = 0; i < historyList.size(); i++) {%>
         data[<%=i%>]='<%=historyList.get(i).getsData()%>';
         console.log("db에서 불러온 " + <%=i%>+"번째 data : "+ data[<%=i%>]);
         time[<%=i%>]='<%=historyList.get(i).getsDatetime().toString()%>';
       console.log("db에서 불러온 " +<%=i%>+ "번째 time : " + time[<%=i%>]);<%}%>
console.log("-------------start load data2-----------------------");
<%if(name2!=null){%>
   <%for (int i = 0; i < historyList2.size(); i++) {%>
       data2[<%=i%>]='<%=historyList2.get(i).getsData()%>';
      console.log("db에서 불러온 " + <%=i%>+"번째 data : "+ data2[<%=i%>]);
       time2[<%=i%>]='<%=historyList2.get(i).getsDatetime().toString()%>';
      console.log("db에서 불러온 " +<%=i%>+ "번째 time : " + time2[<%=i%>]);<%}}%>   
   
   
   var picker = new Pikaday(
         {
            field : document.getElementById('datepicker'),
            firstDay : 1,
            minDate : new Date(2018, 1, 1),
            maxDate : new Date(2020, 12, 31),
            yearRange : [ 2000, 2020 ],
            format : 'YYYY-MM-DD',
            onSelect : function() {
               //canvas 초기화
               myChart.destroy();

               //달력의 날짜 선택되었을 때 실행
               console.log("방금 선택한 날짜"
                     + this.getMoment().format('YYYY-MM-DD'));
               for (var i = 0; i < 4; i++) {
                  sdy = this.getMoment().format('YYYY-MM-DD');
               }

               for (h = 1, hour = 1; h < 25, hour < 25; h++, hour++) {//24시,1시~23시
                  count = 0;
                  count2 = 0;
                  if (hour < 10)
                     hour = "0" + hour;//db에 저장된 시간

                  for (var j = 0; j < size; j++) {//db에 저장된 시간 데이터만큼 반복 topic1
                     if (time[j] == sdy + "-" + hour) {
                        cdata[h][count] = data[j];
                        //console.log("cdata:  " + h + "  배열에 넣음 " + cdata[h][count]);
                        count++;
                     }
                     //console.log("hour db 한거" + hour);
                  }
                  if(name2!=null){   
                  for (var j = 0; j < size2; j++) {//db에 저장된 시간 데이터만큼 반복
                     if (time[j] == sdy + "-" + hour) {
                        cdata2[h][count2] = data2[j];
                        //console.log("cdata:  " + h + "  배열에 넣음 " + cdata[h][count]);
                        count2++;
                     }
                     //console.log("hour db 한거" + hour);
                  }   
                  }   
                  drawD[h] = mean(cdata[h], count);
                  if(name2!=null){
                  drawD2[h] = mean(cdata2[h], count2);}
                  console.log("topic1:  "+h + "시 의 평균: " + drawD[h]);
                  console.log("topic2:  "+h + "시 의 평균: " + drawD2[h]);
                  console.log("------------ ");
               }
               //하루동안의 평균
               draw1 = meanDay(drawD);
               if(name2!=null){
               draw12 = meanDay(drawD2);}

               //value
               if (type == "value") {

               }

               //bar chart topic1 일때 
               if (type == "bar") {
                  var ctx = document.getElementById("hChart").getContext(
                        '2d');
                  new Chart(ctx, {
                     type : 'bar',
                     data : {
                        labels : [ "0 시", "1 시", "2시", "3시", "4시",
                              "5시", "6시", "7시", "8시", "9시", "10시",
                              "11시", "12시", "13시", "14시", "15시",
                              "16시", "17시", "18시", "19시", "20시",
                              "21시", "22시", "23시" ],
                        datasets : [ {
                           label : topic,
                           data : [ drawD[24], drawD[1], drawD[2],
                                 drawD[3], drawD[4], drawD[5],
                                 drawD[6], drawD[7], drawD[8],
                                 drawD[9], drawD[10], drawD[11],
                                 drawD[12], drawD[13], drawD[14],
                                 drawD[15], drawD[16], drawD[17],
                                 drawD[18], drawD[19], drawD[20],
                                 drawD[21], drawD[22], drawD[23] ],
                           backgroundColor : [
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5'
                              ],
                           borderColor : [
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                              '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5' ],
                           borderWidth : 1
                        } ]
                     },
                     options : {
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
               
               //bar chart topic1, topic2 일때 
               if (type == "bar" && name2!="null") {
                  var ctx = document.getElementById("hChart").getContext(
                        '2d');
                  new Chart(ctx, {
                     type : 'bar',
                     data : {
                        labels : [ "0 시", "1 시", "2시", "3시", "4시",
                              "5시", "6시", "7시", "8시", "9시", "10시",
                              "11시", "12시", "13시", "14시", "15시",
                              "16시", "17시", "18시", "19시", "20시",
                              "21시", "22시", "23시" ],
                        datasets : [ {
                           label : topic,
                           data : [ drawD[24], drawD[1], drawD[2],
                                 drawD[3], drawD[4], drawD[5],
                                 drawD[6], drawD[7], drawD[8],
                                 drawD[9], drawD[10], drawD[11],
                                 drawD[12], drawD[13], drawD[14],
                                 drawD[15], drawD[16], drawD[17],
                                 drawD[18], drawD[19], drawD[20],
                                 drawD[21], drawD[22], drawD[23] ],
                                 backgroundColor : [
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5'
                                    ],
                                 borderColor : [
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5',
                                    '#9ad0f5','#9ad0f5','#9ad0f5','#9ad0f5' ],
                           borderWidth : 1
                        },{
                           label : topic2,
                           data : [ drawD2[24], drawD2[1], drawD2[2],
                                 drawD2[3], drawD2[4], drawD2[5],
                                 drawD2[6], drawD2[7], drawD2[8],
                                 drawD2[9], drawD2[10], drawD2[11],
                                 drawD2[12], drawD2[13], drawD2[14],
                                 drawD2[15], drawD2[16], drawD2[17],
                                 drawD2[18], drawD2[19], drawD2[20],
                                 drawD2[21], drawD2[22], drawD2[23] ],
                           backgroundColor : [
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa'
                              ],
                           borderColor : [ '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa',
                              '#ffe6aa','#ffe6aa','#ffe6aa','#ffe6aa'],
                           borderWidth : 1}]
                     },
                     options : {
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
               
               

               //line chart topic1 일때 
               if (type == "line") {
                  var ctx = document.getElementById("hChart").getContext(
                        '2d');
                  new Chart(ctx, {
                     type : 'line',
                     data : {
                        labels : [ "0 시", "1 시", "2시", "3시", "4시",
                              "5시", "6시", "7시", "8시", "9시", "10시",
                              "11시", "12시", "13시", "14시", "15시",
                              "16시", "17시", "18시", "19시", "20시",
                              "21시", "22시", "23시" ],
                        datasets : [ {
                           data : [ drawD[24], drawD[1], drawD[2],
                                 drawD[3], drawD[4], drawD[5],
                                 drawD[6], drawD[7], drawD[8],
                                 drawD[9], drawD[10], drawD[11],
                                 drawD[12], drawD[13], drawD[14],
                                 drawD[15], drawD[16], drawD[17],
                                 drawD[18], drawD[19], drawD[20],
                                 drawD[21], drawD[22], drawD[23] ],
                           label : topic,
                           borderColor : "#4bc0c0",
                           fill : false
                        } ]
                     },
                     option0 : {
                        title : {
                           display : true,
                           text : name
                        }
                     }
                  });

               }
               
               //line chart topic1, topic2 일때 
               if (type == "line") {
                  var ctx = document.getElementById("hChart").getContext(
                        '2d');

                  new Chart(ctx, {
                     type : 'line',
                     data : {
                        labels : [ "0 시", "1 시", "2시", "3시", "4시",
                              "5시", "6시", "7시", "8시", "9시", "10시",
                              "11시", "12시", "13시", "14시", "15시",
                              "16시", "17시", "18시", "19시", "20시",
                              "21시", "22시", "23시" ],
                        datasets : [ {
                           data : [ drawD[24], drawD[1], drawD[2],
                                 drawD[3], drawD[4], drawD[5],
                                 drawD[6], drawD[7], drawD[8],
                                 drawD[9], drawD[10], drawD[11],
                                 drawD[12], drawD[13], drawD[14],
                                 drawD[15], drawD[16], drawD[17],
                                 drawD[18], drawD[19], drawD[20],
                                 drawD[21], drawD[22], drawD[23] ],
                           label : topic,
                           borderColor : "#ff6384",
                           fill : false
                        },
                        {
                           data : [ drawD2[24], drawD2[1], drawD2[2],
                                 drawD2[3], drawD2[4], drawD2[5],
                                 drawD2[6], drawD2[7], drawD2[8],
                                 drawD2[9], drawD2[10], drawD2[11],
                                 drawD2[12], drawD2[13], drawD2[14],
                                 drawD2[15], drawD2[16], drawD2[17],
                                 drawD2[18], drawD2[19], drawD2[20],
                                 drawD2[21], drawD2[22], drawD2[23] ],
                           label : topic2,
                           borderColor : "#36a2eb",
                           fill : false
                        } ]
                     },
                     option0 : {
                        title : {
                           display : true,
                           text : name
                        }
                     }
                  });

               }

               //pie chart topic1 일때 
               if (type == "pie") {
                  var hChart = document.getElementById("hChart")
                        .getContext('2d');

                  var oilData = {
                     labels : [ sdy ],
                     datasets : [ {
                        data : [ draw1 ],
                        backgroundColor : [ "#ff9f40" ]
                     } ]
                  };
                  new Chart(hChart, {
                     type : 'pie',
                     data : oilData,
                  });

               }
               
               //pie chart topic1 ,topic2일때 
               if (type == "pie"&& name2!=null) {
                  var hChart = document.getElementById("hChart")
                        .getContext('2d');

                  var oilData = {
                     labels : [ topic,topic2 ],
                     datasets : [ {
                        data : [ draw1, draw12 ],
                        backgroundColor : [ "#ff9f40",
                                       "#ffcd56"]
                     } ]
                  };
                  new Chart(hChart, {
                     type : 'pie',
                     data : oilData,
                  });

               }
               
               
               //doughnut chart topic1 일때 
               if (type == "doughnut") {
                  var hChart = document.getElementById("hChart")
                        .getContext('2d');

                  var oilData1 = {
                     labels : [ sdy ],
                     datasets : [ {
                        data : [ draw1 ],
                        backgroundColor : [ "#ff9f40" ],
                     } ]
                  };

                  new Chart(hChart, {
                     type : 'doughnut',
                     data : oilData1,
                  });

               }

               //doughnut chart topic1, topic2 일때 
               if (type == "doughnut" && name2!=null) {
                  var hChart = document.getElementById("hChart")
                        .getContext('2d');

                  var oilData1 = {
                     labels : [ topic, topic2 ],
                     datasets : [ {
                        data : [ draw1,draw12 ],
                        backgroundColor : [ "#ff9f40","#ffcd56" ],
                     } ]
                  };

                  new Chart(hChart, {
                     type : 'doughnut',
                     data : oilData1,
                  });

               }

               //gauge chart topic1 일때 
               if (type == "gauge") {
                  var hChart = document.getElementById("hChart")
                        .getContext('2d');

                  //도넛차트 옵션 변경해서 게이지 차트로 옵션 설정
                  var chartOptions = {
                     rotation : -Math.PI,
                     cutoutPercentage : 30,
                     circumference : Math.PI,
                     legend : {
                        position : 'left'
                     },
                     animation : {
                        animateRotate : false,
                        animateScale : true
                     }
                  };

                  var oilData2 = {
                     labels : [ sdy ],
                     datasets : [ {
                        data : [ draw1 ],
                        backgroundColor : [ "#ff9f40" ],
                     } ]
                  };

                  myChart = new Chart(hChart, {
                     type : 'doughnut',
                     data : oilData2,
                     options : chartOptions
                  })
               }

   
               
               //gauge chart topic1,topic2 일때 
               if (type == "gauge"&&name2!=null) {
                  var hChart = document.getElementById("hChart")
                        .getContext('2d');

                  //도넛차트 옵션 변경해서 게이지 차트로 옵션 설정
                  var chartOptions = {
                     rotation : -Math.PI,
                     cutoutPercentage : 30,
                     circumference : Math.PI,
                     legend : {
                        position : 'left'
                     },
                     animation : {
                        animateRotate : false,
                        animateScale : true
                     }
                  };

                  var oilData2 = {
                     labels : [ topic, topic2 ],
                     datasets : [ {
                        data : [ draw1, draw12 ],
                        backgroundColor : [ "#ff9f40","#ff6384" ],
                     } ]
                  };

                  myChart = new Chart(hChart, {
                     type : 'doughnut',
                     data : oilData2,
                     options : chartOptions
                  })
               }

            }

         });

   function meanDay(y) {
      var mD = new Array();
      mD = y;
      var meanm = 0;
      var cnt = 24;
      //console.log("mean day 불림" + mD);

      for (var z = 1; z < 25; z++) {
         if (Number(mD[z]) == 0) {
            cnt = cnt - 1;
         }
         meanm = meanm + Number(mD[z]);
         //console.log("meanm?" + meanm);
      }
      //console.log("count 의 값" + cnt);
      mD = (meanm / cnt).toFixed(2);
      return mD;

   }

   function mean(x, count) {
      var m = x;
      var meanm = 0;
      var cnt = count;
      
      if (typeof x == "undefined" || x == "") {
         //console.log("매개변수 정의안됨");
         m = 0;
      } else {
         console.log("m있을 때 길이 " + m.length);
         for (var k = 0; k < m.length; k++) {
            meanm += Number(m[k]);
            console.log(meanm);
         }
         m = (meanm / cnt).toFixed(2);
      }
      return m;
   }
</script>

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
</html>