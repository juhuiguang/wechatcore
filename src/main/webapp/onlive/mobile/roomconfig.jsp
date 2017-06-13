<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <%@page import="com.alienlab.wechat.onlive.service.OnliveManager"%>
    <%@page import="com.alienlab.wechat.onlive.bean.OnliveRoom"%>
    <%@page import="com.alibaba.fastjson.JSONObject"%>
    <%@page import="com.alienlab.common.TypeUtils"%>
<!DOCTYPE html">
<html>
<%@ include file="wxutil.jsp"%>
<% 
	String roomno=request.getParameter("state");
	String roomjo="";
	OnliveRoom room=null;
	if(openid!=null&&!openid.equals("")){
		room=OnliveManager.getRoom(roomno);
		//验证直播间是否存在
		if(room!=null){
			//验证直播间是否由当前用户管理
			if(!room.getManager().getOpenid().equals(openid)){
				out.println("您不是该直播间管理员");
				return;
			}else{
				//roomjo=JSONObject.toJSONString(room);
			}
		}else{
			out.println("直播间不存在");
			return;
		}
	}else{
		out.println("未获得微信登录授权");
		return;
	}
	String speakmod=room.getSpeakMode();
	String joinmsg=room.getJoinmsg();
	String commentmsg=room.getCommentmsg();
	
	String currenttime=TypeUtils.getTime();
	String isover="0";
	if(currenttime.compareTo(room.getRawEndTime())>0){
		isover="1";
	}
%>
<style>
body{
	width:100%;
}
	.cover{
	    padding: 0;
	    overflow-y: hidden;
	}
	#roomcover{
		 width: 5.5em;
		 float:right;
    	display: block;
    	 box-sizing:border-box;
	}
	#roomname{
	    width: 11.5em;
	    padding: 10 20;
	    box-sizing:border-box;
	    color: #000;
	    float:left;
	    font-weight:600;
	    font-size:110%;
	}
	#roomname .time{
		font-weight:normal;
		font-size:100%;
	}
	#qrimg{
		width:100%;
	}
	
	.weui_btn+.weui_btn{
		margin-top:0;
		margin-left:2px;
	}
	
	.weui_btn_primary{
		    background-color: #ff0;
	}
	
	.weui_switch:checked {
	    border-color: #ff0;
	    background-color: #ff0;
	}
	#btnenter{
		    width: 11.5em;
		        
    border-radius: 0;
    background-color: #ffff25;
    font-weight: 600;
    color: #000;
	}
	
	#btnenter:after{
		border-radius:0;
	}
	
	#btndelete{
		width:5em;
		color:#fff;
		border-radius:0;
		background-color:#f00;
	}
	#btndelete:after{
		border-radius:0;
	}
	
	.clear{
		clear:both;
	}
	.pageheader{
		    margin-top: .77em;
		    margin-bottom: .3em;
		    padding-left: 15px;
		    padding-right: 15px;
		    color: #888;
		    font-size: 14px;
		    /* position: absolute; */
		    height: 2.5em;
	}
	
	.pageheader h2{
		position:absolute;
	}
	
	#btnmyroom{
		line-height:2.5em;
	}
	
	#membersclose{
		    position: absolute;
	    bottom: 0em;
	    /* left: 1em; */
	    z-index: 200;
	    width: 100%;
	    border-radius: 0;
	}
	
	#members .weui-popup-modal{
		padding-bottom:3em;
		box-sizing:border-box;
	}
	
	#members .weui_cell_switch{
		max-height: 3em;
    	height: 3em;
	}
	
	#members .mem_icon {
	    width: 2em;
	    height: 2em;
	    display: inline-block;
	    overflow-y:hidden;
	    float:left
	}
	
	#members .mem_nickname{
		    margin-left: 0.5em;
		    /* margin-bottom: 1em; */
		    float: left;
		    /* width: 12em; */
		    line-height: 2em;
	}
	
	#members .mem_icon img{
		width:100%;
	}
	
</style>
<body>
	<div class="weui_cells_title weui_cell_bd weui_cell_primary pageheader">
		<h2>直播间设置</h2>
		<div class="weui_cell_ft" id="btnmyroom">
	      我的直播间&nbsp;&nbsp;>
	    </div>
	</div>
	<div class="weui_cells weui_cell_bd weui_cell_primary">
		<div class="weui_cell cover">
		<div id="roomname"><%=room.getName() %><br/><span class="time"><%=room.getLayoutTime()%></span></div>
			<img id="roomcover" src="<%=room.getCover() %>">
			<div class="clear"></div>
		</div>
		<div class="weui_cell">
			<p class="weui_media_desc"><%=room.getDescript() %></p>
		</div>
		
	</div>
	<div class="weui_cells weui_cells_access">
	  <a class="weui_cell" href="javascript:;" id="btnmember">
	    <div class="weui_cell_bd weui_cell_primary">
	      <p>参与者</p>
	    </div>
	    <div id="membercount" class="weui_cell_ft" >
	      	<%=room.getMemberCount() %>人
	    </div>
	  </a>
	  <div class="weui_cell weui_cell_switch">
	    <div class="weui_cell_hd weui_cell_primary">允许评论</div>
	    <div class="weui_cell_ft">
	      <input class="weui_switch" type="checkbox" id="btnspeakmod">
	    </div>
	  </div>
	   <div class="weui_cell weui_cell_switch">
	    <div class="weui_cell_hd weui_cell_primary">接收成员进入提示</div>
	    <div class="weui_cell_ft">
	      <input class="weui_switch" type="checkbox" id="btnjoinmsg">
	    </div>
	  </div>
	  <div class="weui_cell weui_cell_switch">
	    <div class="weui_cell_hd weui_cell_primary">接收评论提示</div>
	    <div class="weui_cell_ft">
	      <input class="weui_switch" type="checkbox" id="btncommentmsg">
	    </div>
	  </div>
	  <a href="javascript:;" class="weui_cell" id="btnspeaker">
	  	<div class="weui_cell_bd weui_cell_primary">
	      <p>设置演讲人</p>
	    </div>
	    <div class="weui_cell_ft">
	      	
	    </div>
	  </a>
	  <a href="javascript:;" class="weui_cell" id="btnqr">
	  	<div class="weui_cell_bd weui_cell_primary">
	      <p>直播间二维码</p>
	    </div>
	    <div class="weui_cell_ft">
	      	
	    </div>
	  </a>
	   <div class="weui_cell">
	  	<a class="weui_btn weui_btn_primary" id="btnenter"> 进入>></a>
	  	<a class="weui_btn weui_btn_warn" id="btndelete"> 删除</a>
	  </div>
	</div> 
	
	
	  
	  <div class="weui_dialog_alert"  id="qrdialog">
        <div class="weui_mask"></div>
        <div class="weui_dialog">
            <div class="weui_dialog_hd"><strong class="weui_dialog_title"><%=room.getName() %></strong></div>
            <div class="weui_dialog_bd">
            	<img src="<%=room.getQrcode()%>"/>
            </div>
            <div class="weui_dialog_ft">
                <a href="javascript:;" class="weui_btn_dialog primary">确定</a>
            </div>
        </div>
    </div>
    
    <div id="members" class="weui-popup-container">
	  <div class="weui-popup-modal">
	  		<div class="weui_cell">
			    <div class="weui_cell_hd weui_cell_primary">
			    	已进入成员
			    </div>
			    <div class="weui_cell_ft">
			      	设为嘉宾
			    </div>
		  	</div>
	    	
	  </div>
	  <a id="membersclose" href="javascript:;" class="close-popup weui_btn weui_btn_warn" data-target="members">取消</a>
	</div>
</body>
<script>
	var roomno="<%=roomno%>";
	var openid="<%=openid%>";
	var speakmod="<%=speakmod%>";
	var joinmsg="<%=joinmsg%>";
	var commentmsg="<%=commentmsg%>";
	var isover='<%=isover%>';
	$(function(){
		//alert("window>>"+$(window).width());
		//alert("body>>"+$("body").width());
		//alert("document>>"+$(document).width());
		//alert("document client width>>>"+document.documentElement.clientWidth);
		
		$("#roomname").width($("body").width()*0.6);
		$("#roomcover").width($("body").width()*0.239);
		$("#btnenter").width($("body").width()*0.6);
		$("#btndelete").width($("body").width()*0.4);
		$("#roomcover,#roomname").height($("#roomcover").width());
		if(speakmod=="all"){
			$("#btnspeakmod").attr("checked",true);
		}
		if(joinmsg=="1"||joinmsg==1){
			$("#btnjoinmsg").attr("checked",true);
		}
		if(commentmsg=="1"||commentmsg==1){
			$("#btncommentmsg").attr("checked",true);
		}
		
		$("#btnspeakmod").change(function(e){
			var v="speaker";
			if(e.target.checked){
				v="all";
			}
			$.ajax({
		 		cache:false,
				url:"onlive/setSwitch.do",
				type:"POST",
				dataType:"JSON",
				data:{roomno:roomno,value:v,config:"speakmod"},
				success:function(rep){
					if(rep.result>0){
						$.toast("更新成功");
					}else{
						$.toast(rep.message);
					}
				}
		 	});
		});
		
		$("#btnjoinmsg").change(function(e){
			var v=0;
			if(e.target.checked){
				v=1;
			}
			$.ajax({
		 		cache:false,
				url:"onlive/setSwitch.do",
				type:"POST",
				dataType:"JSON",
				data:{roomno:roomno,value:v,config:"joinmsg"},
				success:function(rep){
					if(rep.result>0){
						$.toast("更新成功");
					}else{
						$.toast(rep.message);
					}
				}
		 	});
		});
		
		$("#btncommentmsg").change(function(e){
			var v=0;
			if(e.target.checked){
				v=1;
			}
			$.ajax({
		 		cache:false,
				url:"onlive/setSwitch.do",
				type:"POST",
				dataType:"JSON",
				data:{roomno:roomno,value:v,config:"commentmsg"},
				success:function(rep){
					if(rep.result>0){
						$.toast("更新成功");
					}else{
						$.toast(rep.message);
					}
				}
		 	});
		});
		
		//修改名称
		$("#roomname").click(function(){
			$.prompt("请输入新名称", function(text) {
					
				 	$.ajax({
				 		cache:false,
						url:"onlive/renameRoom.do",
						type:"POST",
						dataType:"JSON",
						data:{roomno:roomno,name:text},
						success:function(rep){
							if(rep.result>0){
								$.toast("更新成功");
								$("#roomname").text(text);
							}else{
								$.toast(rep.message);
							}
						}
				 	});
				  }, function() {
				  //点击取消后的回调函数
				  });
		});
		
		
		
		//显示二维码
		$("#btnqr").click(function(){
			$.alert('<img id="qrimg" src="<%=room.getQrcode()%>"/>', "扫描二维码进入直播间");
		});
		
		//参与者、设置演讲者
		$("#btnmember,#btnspeaker").click(function(){
			$("#members").popup();
			setTimeout(function(){
				$.showLoading("正在加载...");
				$.ajax({
					cache:false,
					url:"onlive/getMembers.do",
					type:"POST",
					dataType:"JSON",
					data:{roomno:roomno,openid:openid},
					success:function(rep){
						if(rep.result>0){
							renderMembers(rep.data);
							$.hideLoading();
						}else{
							$.toast(rep.message);
						}
					}
				});
			},800);
			
			
		});
		
		function renderMembers(members){
			var len=members.length;
			if(len>100){
				len=100;
			}
			$("#members .weui-popup-modal .memberdom").remove();
			for(var i=0;i<len;i++){
				var member=members[i];
				var mdom='<div class="weui_cell weui_cell_switch  memberdom">'+
							    '<div class="weui_cell_hd weui_cell_primary">'+
					    	'<span class="mem_icon"><img src="'+member.localPic+'"/></span>'+
					    	'<span class="mem_nickname">'+member.nick+'</span>'+
					    '</div>'+
					    '<div class="weui_cell_ft">'+
					      	'<input id="member_'+member.openid+'"'+((member.vip==true)?"checked":"")+'  openid="'+member.openid+'"  class="weui_switch mem_isvip"  type="checkbox">'+
					    '</div>'+
				  	'</div>';
				  	
				  	$("#members .weui-popup-modal").append($(mdom));
			}
			
			if(isover=="1"){
				$(".mem_isvip").attr("disabled",true);
			}
			
			$(".mem_isvip").change(function(e){
				if(isover=="1"){
					$.toast("直播已结束");
					return false;
				}
				var v=0,url="onlive/removeVip.do";
				if(e.target.checked){
					v=1;
					url="onlive/setVip.do"
				}
				$.ajax({
					cache:false,
					url:url,
					type:"POST",
					dataType:"JSON",
					data:{roomno:roomno,openid:$(this).attr("openid")},
					success:function(rep){
						$.hideLoading();
						$.toast(rep.message);
					}
				});
			});
		}
		
		//我的直播间
		$("#btnmyroom").click(function(){
			var url="<%=BASE_PATH%>/onlive/mobile/myroom.jsp";
			window.location.href="onlive/mobile/goto.jsp?target="+url;
		});
		
		//进入直播间
		$("#btnenter").click(function(){
			var url="<%=BASE_PATH%>/onlive/mobile/onliveroom.jsp";
			window.location.href="onlive/mobile/goto.jsp?target="+url+"&state=<%=roomno%>";
		});
		
		//删除直播间
		$("#btndelete").click(function(){
			$.confirm("确定要删除直播间吗？", function() {
				$.showLoading("正在删除");
			  	$.ajax({
			  		cache:false,
					url:"onlive/deleteRoom.do",
					type:"POST",
					dataType:"JSON",
					data:{roomno:roomno},
					success:function(rep){
						$.hideLoading();
						if(rep.result>0){
							$.toast(rep.message);
							//删除完毕后，跳转到我的直播间
							var url="<%=BASE_PATH%>/onlive/mobile/myroom.jsp";
							window.location.href="onlive/mobile/goto.jsp?target="+url;
						}else{
							$.toast(rep.message,"cancel");
						}
					}
			  	});
			  }, function() {
			  //点击取消后的回调函数
			  });
		});
		
		setInterval(function(){
			loadMemberCount();
		},1000*20);
		
		//加载直播间人数
		function loadMemberCount(){
			$("#membercount").text("获取中...");
			$.ajax({
		  		cache:false,
				url:"onlive/loadRoomMemberCount.do",
				type:"POST",
				dataType:"JSON",
				data:{roomno:roomno},
				success:function(rep){
					if(rep){
						$("#membercount").text(rep.data.count+"人");
					}else{
						$("#membercount").text("0人");
					}
				}
			});
		}
	});
</script>
</html>