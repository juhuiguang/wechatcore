<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.alienlab.wechat.onlive.service.NameList"%>
    <%@page import="com.alienlab.wechat.onlive.bean.NamelistItem"%>
<!DOCTYPE html">
<html>
<%@ include file="wxutil.jsp"%>
<% 
if(openid==null||openid.equals("")){
//	out.println("未获得微信授权登录");
//	return ;
	openid="one5hs5IJ14OWi-xNUvFetRIhA1g";
}
NamelistItem myself=NameList.getNameByOpenid(openid);
if(myself==null){
	response.sendRedirect("goto.jsp?target="+BASE_PATH+"/onlive/mobile/reg.jsp");
	return;
}

%>
<style>
	#btncreate{
		width:100%;
		heigth:2.5em;
	}
	#roomlist{
		padding-bottom:3em;
	}
	.bottombar{
		width:100%;
		max-height:2.5em;
		position:fixed;
		bottom:0;
		left:0;
		text-align:center;
	}
	
	#btncreate{
		border-radius:0;
		background-color:#ffff25;
		color:#000;
		font-weight:600;
	}
	
	#btncreate:after{
		border-radius:0;
	}
</style>
<body>
	<div class="weui_cells_title weui_cell_bd weui_cell_primary">
		<h2>我的直播间</h2>
	</div>
	<div id="roomlist" class="weui_panel weui_panel_access">
	<template id="roomtmp">
		<a href="javascript:void(0);" class="weui_media_box weui_media_appmsg room" roomno="$(roomno)">
		      <div class="weui_media_hd">
		        <img class="cover weui_media_appmsg_thumb" src="$(cover)" alt="">
		      </div>
		      <div class="weui_media_bd">
		        <h4 class="roomname weui_media_title">$(roomname)</h4>
		        <p class="roomdesc weui_media_desc">$(desc)</p>
		      </div>
	    </a>
	   </template> 
	</div>
	<div class="bottombar">
		<a href="javascript:;" class="weui_btn weui_btn_primary"  id="btncreate">创建新直播间</a>
	</div>
</body>
<script>
	var openid="<%=openid%>";
	$(function(){
		//alert("page open:"+openid);
		loadRooms();
		function loadRooms(){
			$.ajax({
				cache:false,
				url:"onlive/getUserRoom.do",
				type:"POST",
				dataType:"JSON",
				data:{openid:openid},
				success:function(rep){
					//alert("getUserRoom:"+rep.result);
					if(rep.result>0){
						//alert("result>0:"+rep.data);
						if(rep.data.length>0){
							$("#roomlist .room").remove();
							for(var i=0;i<rep.data.length;i++){
								var dom=renderRoom(rep.data[i]);
								$("#roomlist").append(dom);
							}
							
							$("#roomlist .room").click(function(){
								var roomno=$(this).attr("roomno");
								if(roomno){
									var url="<%=BASE_PATH%>/onlive/mobile/roomconfig.jsp";
									window.location.href="onlive/mobile/goto.jsp?target="+url+"&state="+roomno;
								}
							});
						}else{
							//alert("noroom");
							$("#roomlist").append($("<p class='no room'>您还没有创建任何直播间。</p>"));
						}
					}else{
						
					}
				}
				
			});
		}
		
		function renderRoom(room){
			//var tmp=$("#roomtmp");
			//console.log(tmp);
			//var domstr=tmp.html();
			var domstr='<a href="javascript:void(0);" class="weui_media_box weui_media_appmsg room" roomno="$(roomno)">'+
											      '<div class="weui_media_hd">'+
									        '<img class="cover weui_media_appmsg_thumb" src="$(cover)" alt="">'+
									      '</div>'+
									      '<div class="weui_media_bd">'+
									        '<h4 class="roomname weui_media_title">$(roomname)</h4>'+
									        '<p class="roomdesc weui_media_desc">$(desc)</p>'+
									      '</div>'+
								    '</a>';
			domstr=domstr.replace("$(roomno)",room.roomno).replace("$(roomname)",room.roomname).replace("$(cover)",room.cover).replace("$(desc)",room.roomdesc);
			var dom=$(domstr);
			console.log(dom);
			return dom;
		}
		
		//创建新直播间
		$("#btncreate").click(function(){
			var url="<%=BASE_PATH%>/onlive/mobile/CreateRoom.jsp";
			window.location.href="onlive/mobile/goto.jsp?target="+url;
		});
	});
	
	 //微信分享代码块
    wx.ready(function (){ 	
		 wx.showMenuItems({
			 menuList: ["menuItem:share:appMessage",
				    	"menuItem:share:timeline",
				    		"menuItem:favorite"]  // 要显示的菜单项，所有menu项见附录3
			});
		 var shareObject={
		  	title: '我的直播间', // 分享标题
		    link: "<%=BASE_PATH%>/onlive/mobile/goto.jsp?target=<%=BASE_PATH%>/onlive/mobile/myroom.jsp", // 分享链接
		    imgUrl: '<%=BASE_PATH%>/img/azlogo.jpg', // 分享图标
		    desc: '直播间组件1.0试用版', // 分享描述
		    success: function () { 
		        // 用户确认分享后执行的回调函数
		    	$.toast("分享成功");
		    },
		    cancel: function () { 
		        // 用户取消分享后执行的回调函数
		    }
		 }
		 wx.onMenuShareAppMessage(shareObject);
		 wx.onMenuShareTimeline(shareObject);
	 });
</script>
</html>