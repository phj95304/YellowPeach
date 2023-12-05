$(document).ready(function() {
	$('side-nav').css('height', $(window).height());
	$(window).resize(function() {
		$('side-nav').css('height', $(window).height());
	});
});

/*
$( function() {
    $( "#datepicker" ).datepicker();
  } );
*/



$(function() {
	$("#datepicker").datepicker({
		showOn : "both",
		changeMonth : true,
		nextText : '다음 달',
		prevText : '이전 달',
		altFormat : 'yyyy-mm-dd',
		onSelect : function() {
			var day = $(this).datepicker('getDate').getDate();
			var month = $(this).datepicker('getDate').getMonth() + 1;
			var year = $(this).datepicker('getDate').getFullYear();
			var fullDate = year + ' 년 ' + month + ' 월 ' + day + ' 일 ';
			var firstTopic = 
			alert(fullDate);
		}
	});
});



/*
$(function() {
	var x = new Date(2018, 04, 02);
	$("#datepicker").datepicker("setDate", x);
});
*/
$(document).ready(function() {
	$("#button-popup").click(function() {
		//$(".window-popup").fadeIn('slow');
		alert("buttonpop 클릭");
		location.href ='readHistory.html';
	});
	
	$("#button-popup-close").click(function() {
		$("window-popup").fadeOut('slow');
	});
});
