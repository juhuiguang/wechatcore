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
	OnliveRoom room=OnliveManager.getRoom(roomno);
	String currenttime=TypeUtils.getTime();
	if(room==null){
		response.sendRedirect("noroom.jsp");
		return;
	}
	if(room.getManager()==null){
		response.sendRedirect("noroom.jsp");
		return;
	}
	if(room.getManager().getStatus().equals("0")){
		response.sendRedirect("noroom.jsp");
		return;
	}
	if(room.getStatus().equals("9")){
		response.sendRedirect("noroom.jsp");
		return;
	}
	String ismanager="0";//是否直播间管理员
	//是否是演讲者
	String isspeaker="0"; //是否演讲嘉宾
	//如果是直播间管理员，更新管理员信息
	if(openid.equals(room.getManager().getOpenid())){
		ismanager="1";
		isspeaker="1";
		room.getManager().setNickname(nickname);
		room.getManager().setHeaderimg(headerimg);
		room.getManager().update2db();
	}
	String autoload="1";//是否自动加载
	String sorttype="desc";//排序顺序
	String isover="0";//直播是否结束
	String roomstatus="即将开始";
	if(currenttime.compareTo(room.getRawEndTime())>0){ //直播间到期
		autoload="0";//不自动加载
		//如果直播间已到期，取消页面发布按钮
		//ismanager="0";
		sorttype="asc"; //正序排列
		isover="1";//结束
		roomstatus="已结束";
	}
	
	String endtime=room.getRawEndTime();
	String starttime=room.getRawStartTime();
	
	String isonlive="0"; //正在直播
	if(currenttime.compareTo(starttime)>=0 && currenttime.compareTo(endtime)<0){
		isonlive="1";
		roomstatus="直播中";
	}
	
	String speakmod=room.getSpeakMode(); //发言模式
	String joinmsg=room.getJoinmsg(); //是否发送成员进入消息
	String commentmsg=room.getCommentmsg(); //是否发送个评论消息
	
	if(room.getSpeakerOpenid().indexOf(openid)>=0){
		isspeaker="1";
	}
%>
<link rel="stylesheet" href="onlive/mobile/plugins/onliveroom.css" />
<body>
	<div class="page" id="page2">
		<div class="cover">
			<div class="coverimgdiv"><img class="coverimg" src="<%=room.getCover() %>"></div>
			<div class="headerimgdiv"><img class="headerimg" src="<%=room.getBrandcover() %>"></div>
			<div class="roomname">
				<div class="r_name"><%=room.getName() %></div>
				<div class="r_time"><%=room.getLayoutTime()%></div>
				<div class="line"></div>
				<div class="r_desc"><%=room.getDescript() %></div>
				<div class="weui_btn" id="btnenter">进入</div>
			</div>
		</div>
		<div class="stream_header">
			<div class="s_name">
				<div id="s_name_text"><%=room.getName()%></div>
				<div id="s_tags">
					<span id="s_status"><%=roomstatus%></span>
					<span id="membercount"><%=room.getMemberCount() %>人</span>
				</div>
			</div>
			<div class="s_h_icon">
				<img src="<%=room.getBrandcover() %>">
			</div>
			<div class="clear"></div>
		</div>
		<div id="pagecontent" class="weui_tab">
		  <div class="weui_navbar">
		    <a class="weui_navbar_item weui_bar_item_on"  href="#sdiv1">
		      直播
		    </a>
		    <a class="weui_navbar_item" href="#sdiv2">
		      介绍
		    </a>
		    <a class="weui_navbar_item" href="#sdiv3">
		      主办方
		    </a>
		  </div>
		  <div class="weui_tab_bd">
		  	<div class="weui-pull-to-refresh-layer">
				    <div class="pull-to-refresh-arrow"></div> <!-- 上下拉动的时候显示的箭头 -->
				    <div class="pull-to-refresh-preloader"></div> <!-- 正在刷新的菊花 -->
				    <div class="down">下拉刷新</div><!-- 下拉过程显示的文案 -->
				    <div class="up">释放加载新内容</div><!-- 下拉超过50px显示的文案 -->
				    <div class="refresh">加载中</div><!-- 正在刷新时显示的文案 -->
				  </div>	
		    <div id="sdiv1" class="streamdiv weui_tab_bd_item weui_tab_bd_item_active">
				
			</div>
			<div id="sdiv2" class="onlivedesc weui_tab_bd_item">
				2
			</div>
			<div id="sdiv3" class="onlivespeaker weui_tab_bd_item">
				3
			</div>
		  </div>
		</div>
		
		<div id="iwantbtn">
			我也要直播
		</div>
		<div class="gototop"></div>

	</div>
	<div id="settingbtn"></div>
	<div id="publishbtn">
	</div>
	
	<div class="weui_tab publish_panel">
	  <div class="weui_navbar">
	    <a class="weui_navbar_item weui_bar_item_on" href="#publish_text">
	      文字
	    </a>
	    <a class="weui_navbar_item" href="#publish_image">
	      图片
	    </a>
	    <a class="weui_navbar_item" href="#publish_voice">
	      语音
	    </a>
	  </div>
	  <div class="weui_tab_bd">
	  	<div class="weui_tab_bd_item weui_tab_bd_item_active" id="publish_text">
		    <textarea id="pub_text_stream" class="weui_textarea"    placeholder="输入您需要发布的内容" rows=4></textarea>
		  	<a href="javascript:;" class="weui_btn weui_btn_primary" id="textpubbtn">发布</a>
		  </div>
		  <div class="weui_tab_bd_item" id="publish_image">
		    <div class="weui_uploader_input_wrp">
	    		<img id="pub_img_stream" class="weui_uploader_input" />
	    		<img class="imgview" id="pub_img_stream_view" src=""/>
	    	</div>
	    	<a href="javascript:;" class="weui_btn weui_btn_primary" id="imgpubbtn">发布</a>
		  </div>
		  <div class="weui_tab_bd_item" id="publish_voice">
		    <div class="voicebtn">点击开始录音</div>
		  </div>
	  </div>
	  
	  
	</div>
	
</body>
<script type="text/javascript" src="onlive/mobile/plugins/onlive.infostream.js?i=11331"></script>
<script>
	var roomno=<%=roomno%>;//直播间编号
	var ismanager=<%=ismanager%>;//是否直播间管理员
	var autoload='<%=autoload%>';//是否自动刷新内容
	var openid='<%=openid%>';//当前登录者
	var nick='<%=nickname%>';//当前登录人昵称
	var headerimg='<%=headerimg%>'//当前登录人头像地址
	var starttime='<%=starttime%>';//直播间开始时间
	var endtime='<%=endtime%>';//直播结束时间
	var sorttype='<%=sorttype%>';//直播排序方式
	var isonlive='<%=isonlive%>';//直播是否进行中
	var isover='<%=isover%>';//直播是否结束
	var speakmod='<%=speakmod%>';//直播间发言方式，是否允许评论
	var isspeaker='<%=isspeaker%>';//是否演讲嘉宾
	$(function(){
		var stream=null;
		var w_width=$(window).width();
		var w_height=$(window).height();
		//alert($(window).height()+">>"+$("body").height()+">>"+$(document).height()+">>"+);
		if(w_height<w_width){
			$("body").css("max-height",$(window).height());
			$("body").css("max-width",($(window).height()*640/1007));
		}
		
		$("#page2 .headerimgdiv").height($("#page2 .headerimgdiv").width());
		
		$("#pagecontent").height(window.screen.height-$(".stream_header").height());
		
		$("#pagecontent").bind("touchstart",function(){
			$(".publish_panel").hide();
		});
		
		$("#iwantbtn").click(function(){
			var url="<%=BASE_PATH%>/onlive/mobile/myroom.jsp";
			window.location.href="onlive/mobile/goto.jsp?target="+url+"&state="+roomno;
		});
		
		function showeditor(){
			if(isonlive!="1"){
				//$("#iwantbtn").show();
				return;
			}
			if(isspeaker!="1"){
				return ;
			}
			$("#iwantbtn").remove();
			$("#publishbtn").show().click(function(){
				$(".publish_panel").toggle();
			});
			
			$("#publishbtn").on("touchmove",function(e){
				var x=e.originalEvent.touches[0].pageX;
				var y=e.originalEvent.touches[0].pageY;
				$(this).css("left",(x-$(this).width()/2));
				$(this).css("top",(y-$(this).height()/2));
			});
			
			//文字发布
			$("#textpubbtn").click(function(){
				if($("#pub_text_stream").val()==""){
					$.toast("您没有输入任何内容。");
					return;
				}else{
					publishStream("text",$("#pub_text_stream").val());
				}
			});
			
			//图片选择
			//图片上传
			$(".weui_uploader_input,.imgview").bind("click",function(){
				var content=$(this).next();
				if($(this).hasClass("imgview")){
					content=$(this);
				}
				wx.chooseImage({
				    count: 1, // 默认9
				    sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
				    sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
				    success: function (res) {
				        var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
				        content.show();
				        content.attr("src",localIds[0]);
				        content.attr("localid",localIds[0]);
				    }
				});
			});
			
			//图片发布
			$("#imgpubbtn").click(function(){
				var localid=$("#pub_img_stream_view").attr("localid");
				if(localid==null||localid==""){
					$.toast("请选择图片发布。");
					return ;
				}else{
					wx.uploadImage({
	    	            localId: localid, // 需要上传的图片的本地ID，由chooseImage接口获得
	    	            isShowProgressTips: 1, // 默认为1，显示进度提示
	    	            success: function (res1) {
	    	                var serverId = res1.serverId; // 返回图片的服务器端ID
	    	                publishStream("image",serverId);
	    	            }
					});
				}
			});
			var voiceflag=false;//默认为非录音状态
			var starttime,endtime;
			var timeout=null;
			$(".voicebtn").on("touchstart",function(e){
				e.preventDefault();
				e.stopPropagation();
				if(!voiceflag){//非录音状态下点击，转为录音状态
					starttime=new Date().getTime();
					$(".voicebtn").text("停止录音");
					$(".voicebtn").css("background-color","red");
			        wx.startRecord();
			        voiceflag=true;
				}else{//录音状态下点击，转为非录音状态
					endtime=new Date().getTime();
					if(endtime-starttime<1000){//如果录音很短（小于1秒），不发送
						wx.stopRecord();
						voiceflag=false;
						$.toast("录音过短");
						$(".voicebtn").text("开始录音");
						$(".voicebtn").css("background-color","#04be02");
					}else{//录音超过1秒，发送录音
						var localId=null;
						wx.stopRecord({
						    success: function (res) {
						    	voiceflag=false;
						        localId = res.localId;
						    }
						});
						$.confirm("语音录制完成，立刻发送吗？", function() {
							 wx.uploadVoice({
						            localId: localId, // 需要上传的音频的本地ID，由stopRecord接口获得
						            isShowProgressTips: 1, // 默认为1，显示进度提示
						                success: function (res) {
						                var serverId = res.serverId; // 返回音频的服务器端ID
						                publishStream("voice",serverId);
						                $(".voicebtn").text("开始录音");
						                $(".voicebtn").css("background-color","#04be02");
						            }
						        });
						  }, function() {
							  $(".voicebtn").text("开始录音");
				               $(".voicebtn").css("background-color","#04be02");
						  });
					}
				}
			});
			wx.onVoiceRecordEnd({
			    // 录音时间超过一分钟没有停止的时候会执行 complete 回调
			    complete: function (res) {
			        var localId = res.localId; 
			        wx.uploadVoice({
			            localId: localId, // 需要上传的音频的本地ID，由stopRecord接口获得
			            isShowProgressTips: 1, // 默认为1，显示进度提示
			                success: function (res) {
		                	voiceflag=false;
			                var serverId = res.serverId; // 返回音频的服务器端ID
			                publishStream("voice",serverId);
			                $(this).text("开始录音");
			                $(".voicebtn").css("background-color","#04be02");
			            }
			        });
			    }
			});
			
			//发布内容
			function publishStream(type,content){
				var param={
						roomno:roomno,
						openid:openid,
						type:type,
						nick:nick,
						content:"",
						mediaid:""
				}
				if(type=="text"){
					param.content=content;
					param.mediaid="";
				}else{
					param.content="";
					param.mediaid=content;
				}
				$.showLoading("正在发送...");
				$.ajax({
					cache:false,
					url:"onlive/publishStream.do",
					type:"POST",
					dataType:"JSON",
					data:param,
					success:function(rep){
						if(rep.result>0){
							$.hideLoading();
							$.toast("发布成功");
							//清空发布框
							$("#pub_text_stream").val();
							$("#pub_img_stream_view").hide().attr("src","").attr("localid","");
							stream.refresh();
						}else{
							$.toast("发布失败");
						}
					}
				});
			}
		}
		
		//直播尚未开始
		if((isonlive=="0"||isonlive==0)&&(isover=="0"||isover==0)){
			//$("#btnenter").attr("disabled",true);
			$("#btnenter").text("即将开始");
		}else{//直播正在进行或已结束
			//点击进入直播间
			$("#btnenter").click(function(){
				//如果不是管理员进入
				if(ismanager!="1"){
					joinroom(function(){
						setTimeout(function(){
							enterRoom();
						},1000);
					});
				}else{
					setTimeout(function(){
						enterRoom();
					},1000);
				}
				
				$(this).hide();
				$("#page2 .cover").animate({
					"left":"-100%",
					"opcity":0
				},1000,null,function(){
					$("#page2 .cover").hide();
				});
				/**
				$("#page2 .coverimgdiv .coverimg").animate({
					"width":"100%"
				},1000).css("height","auto");
				$("#page2 .headerimgdiv").animate({
					"bottom":"-3em",
					"right":"2em"
				},1000);
				$("#page2 .roomname").animate({
					"bottom":"0"
				},1000).css("text-align","left");
				*/
				$(".gototop").click(function(){
					$(window).scrollTop(0);
				});
				
			});
		}
		
		
		
		
		//
		function joinroom(callback){
			$.ajax({
				cache:false,
				url:"onlive/joinRoom.do",
				type:"POST",
				dataType:"JSON",
				data:{
					roomno:roomno,
					openid:openid,
					isvip:false,
					nick:nick,
					headerimg:headerimg,
					jointype:'page'
					
				},
				success:function(rep){
					callback();
				}
			});
		}
		
		function enterRoom(){
			//1、调用controller，进入直播间
			
			//2、进入直播间后，显示内容
			showeditor();
			if(ismanager=="1"){
				$("#settingbtn").show().click(function(){
					var url="<%=BASE_PATH%>/onlive/mobile/roomconfig.jsp";
					window.location.href="onlive/mobile/goto.jsp?target="+url+"&state="+roomno;
				});
			}
			$("#membercount").show();
			$(".stream_header").show();
			//$(".streamdiv").show();
			$(".weui-pull-to-refresh-layer").show();
			$("#pagecontent .weui_tab_bd").pullToRefresh();
			var isautoload=true;
			if(isonlive=='0'||isonlive==0){
				isautoload=false;
			}
			stream=$("#page2 .streamdiv").infostream({
				loadfunc:loaditems,
				itemloadfunc:reloaditem,
				autoload:isautoload,
				sorttype:sorttype
			});
			
			
			$("#pagecontent .weui_tab_bd").on("pull-to-refresh", function() {
				stream.refresh(function(){
					$("#pagecontent .weui_tab_bd").pullToRefreshDone();
					
				});
			});
			
			if(isonlive=="1"||isonlive==1){
				var iv=setInterval(function(){
					loadMemberCount();
					if(isonlive==0||isonlive=="0"){
						clearInterval(iv);
					}
				},1000*20);
			}
			
			
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
			
			function loaditems(time,direction,sorttype,callback){
				if(sorttype=="asc"&&time=="0"){
					time=endtime;
				}
				$.ajax({
					cache:false,
					url:"onlive/loadstream.do",
					type:"POST",
					dataType:"JSON",
					data:{
						time:time,
						roomno:roomno,
						direction:direction,
						sorttype:sorttype
					},
					success:function(rep){
						if(ismanager!="1"&&isspeaker!="1"){
							$("#iwantbtn").show();
						}
						if(rep.result>0){
							callback(rep.data);
							
							$("#page2 .praiseicon").unbind("click").click(function(){
								var sno=$(this).parent().attr("streamno");
								praiseandcomment(sno,"praise",null);
							});
							
							reloadComment();
							
							if(speakmod=="speaker"){
								$("#page2 .commentpnl").hide();
							}
							$("#page2 .commenticon,#page2 .commentnum").unbind("click").click(function(){
								var streamno=$(this).parent().attr("streamno");
								$("#stream_"+streamno+" .commdiv").toggle();
							});
							
							$(".commentbtn").unbind("click").click(function(){
								var sno=$(this).attr("streamno");
								var comm=$("#txtcomment_"+sno).val();
								if(comm=="")return;
								praiseandcomment(sno,"comment",comm);
							});
							
						}else{
							$.alert(rep.message);
						}
					}
				});
			}
			
			function reloaditem(itemid,callback){
				$.ajax({
					cache:false,
					url:"onlive/reloaditem.do",
					type:"POST",
					dataType:"JSON",
					data:{
						streamno:itemid
					},
					success:function(rep){
						callback(rep);
					}
				});
			}
		}
		
		//发送评论
		function praiseandcomment(streamno,type,content){
			var url="",data={};
			if(type=="comment"){
				if(content=="")return;
				url="onlive/publishComment.do";
				data={
						roomno:roomno,
						openid:openid,
						streamno:streamno,
						comment:content
					}
			}else{
				url="onlive/publishPraise.do";
				data={
						roomno:roomno,
						openid:openid,
						streamno:streamno,
					}
			}
			
			//发送评论
			$.ajax({
				cache:false,
				url:url,
				type:"POST",
				dataType:"JSON",
				data:data,
				success:function(rep){
					if(rep.result>0){
						if(type=="comment"){
							$("#txtcomment_"+streamno).val("");
							reloadComment(streamno);
						}
						else{//评论更新人数
							var ptext=$("#stream_"+streamno+" .praisenum").text();
							if(ptext=="")ptext=0;
							var num=parseInt(ptext)+1;
							$("#stream_"+streamno+" .praisenum").text(num);
						}
					}
					
				}
			});
		}
		
		//每分钟刷新评论
		setInterval(function(){
			reloadComment();
		},60000);
		
		//加载点赞与评论
		function reloadComment(streamno){
			var data={};
			data.roomno=roomno;
			if(streamno){
				data.streamno=streamno;
			}
			$.ajax({
				cache:false,
				url:"onlive/getPraiseComment.do",
				type:"POST",
				dataType:"JSON",
				data:data,
				success:function(rep){
					console.log(rep);
					if(rep.result>0){
						var results=rep.data;
						var praise=results.praise;
						var comment=results.comment;
						processPraise(praise);
						processComment(comment);
					}else{
						
					}
				}
				
			});
		}
		
		//更新界面点赞dom
		function processPraise(praises){
			if(!praises)return;
			var stat={};
			for(var i=0;i<praises.length;i++){
				var sno=praises[i].stream_no;
				if(openid==praises[i].openid){//如果是本人发布,加颜色
					var icon=$("#stream_"+sno).find(".praiseicon")
					if(!icon.hasClass("done")){
						icon.addClass("done");	
					}
				}
				if(!stat[sno]){
					stat[sno]=1;
				}else{
					stat[sno]=parseInt(stat[sno])+1;
				}
			}
			
			$(".stream.item").each(function(){
				var sno=$(this).find(".btnop").attr("streamno");
				if(stat[sno]){
					$(this).find(".praisenum").text(stat[sno]);
				}
			});
		}
		
		//更新评论
		function processComment(comments){
			if(!comments)return;
			for(var i=0;i<comments.length;i++){
				var stream_no=comments[i].stream_no;
				if(openid==comments[i].openid){//如果是本人发布,加颜色
					var icon=$("#stream_"+stream_no).find(".commenticon")
					if(!icon.hasClass("done")){
						icon.addClass("done");	
					}
				}
				if($("#stream_"+stream_no).length>0){
					var comment_no=comments[i].comment_no;
					if($("#comment_"+comment_no).length>0){//如果评论已被添加
						continue;
					}else{
						var commitem=$("<div class='commitem' id='comment_"+comments[i].comment_no+"'></div>");
						var commicon=$("<div class='commicon'><img src='"+comments[i].member_pic+"'></div>");
						var commcontent=$("<div class='commcontent'></div>");
						commcontent.append($("<div class='comm_name'>"+comments[i].member_nick+"</div>"))
											.append($("<div class='comm_memo'>"+comments[i].content+"</div>"));
						commitem.append(commicon).append(commcontent).append($("<div class='clear'></div>"));
						$("#stream_"+stream_no+" .commdiv").append(commitem);
					}
				}
			}
			$(".stream.item").each(function(){
				$(this).find(".itemcontent .btnop .commentnum").text($(this).find(".commitem").length);
			});
		}
		
	});
	
	 //微信分享代码块
    wx.ready(function (){ 	
		 wx.showMenuItems({
			 menuList: ["menuItem:share:appMessage",
				    	"menuItem:share:timeline",
				    		"menuItem:favorite"]  // 要显示的菜单项，所有menu项见附录3
			});
		 var shareObject={
		  	title: '[直播]<%=room.getName()%>', // 分享标题
		    link: '<%=room.getShareLink()%>', // 分享链接
		    imgUrl: '<%=room.getBrandcover()%>', // 分享图标
		    desc: '<%=room.getDescript().replaceAll("\r|\n", "")%>', // 分享描述
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
		 if(isonlive=="1" || isonlive==1){//如果正在直播，每分钟刷新最新内容至分享
			 var iv=setInterval(function(){
				 var d=new Date();
				var now=d.Format("yyyyMMddhhmmss");
				if(now>endtime){
					isonlive="0";
					clearInterval(iv);
				}
				 $.ajax({
						cache:false,
						url:"onlive/getRoomLatestInfo.do",
						type:"POST",
						dataType:"JSON",
						data:{
							roomno:roomno
						},
						success:function(rep){
							if(rep.result>0){
								var data=rep.data;
								if(data){
									if(data.latesttext){
										shareObject.desc=data.latesttext;
									}
									if(data.latestpic){
										shareObject.imgUrl=data.latestpic;
									}
									wx.onMenuShareAppMessage(shareObject);
									wx.onMenuShareTimeline(shareObject);
								}
							}else{
								
							}
						}
					});
			 },60000)
		 }
		 
		
	 });
</script>
</html>