<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <%@page import="com.alienlab.wechat.onlive.service.NameList"%>
    <%@page import="com.alienlab.wechat.onlive.bean.NamelistItem"%>
<!DOCTYPE html">
<html>

<%@ include file="wxutil.jsp"%>
<% 
	String phone=request.getParameter("state");
	if(openid!=null&&!openid.equals("")){
		NamelistItem nameitem=NameList.getNameByOpenid(openid);
		if(nameitem!=null){
			nameitem.setHeaderimg(headerimg);
			nameitem.setNickname(nickname);
			nameitem.update2db();
			if(phone!=null&&!phone.equals("")&&!phone.equals("null")){
				if(nameitem.getPhone().equals(phone)){
					if(!nameitem.isValidate()){
						response.sendRedirect("noroom.jsp");
					}
				}else{
					//如果没有合法参数，跳转到电话号码验证
					response.sendRedirect("noroom.jsp");
				}
			}else{
				phone=nameitem.getPhone();
				
			}
		}else{
			//如果没有绑定手机号码，跳转到手机号码绑定
			response.sendRedirect("goto.jsp?target="+BASE_PATH+"/onlive/mobile/reg.jsp");
		}
	}else{
		//如果没有绑定手机号码，跳转到手机号码绑定
		response.sendRedirect("goto.jsp?target="+BASE_PATH+"/onlive/mobile/reg.jsp");
	}
	
	
%>
<style>
.bc_cover{
	width:100%;
}
.bc_vip_cover{
	width:50%;
}
.weui_uploader_input_wrp .imgview{
	position:ablolute;
	width:100%;
	top:0;
	left:0%;
	z-index:300;
	display:none;
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
			<h2>创建直播间</h2>
		</div>
		
	<div class="weui_cells">
	    <div class="weui_cell weui_cell_bd">
	    	<input id="bc_name" class="weui_input"  type="text"  placeholder="请设置直播间的名称">
	    </div>
	 </div>

	 <div class="weui_cells">
	    <div class="weui_cell">
	         <textarea id="bc_abstract" class="weui_textarea"    placeholder="请设置直播间的摘要信息" rows=5></textarea>
	    </div>
	  </div>
	   <div class="weui_cells_title">直播开始时间(北京时间)</div>
	  <div class="weui_cells">
		    <div class="weui_cell">
		    	 <input class="weui_input"  id="starttime" type="datetime-local"  placeholder="设置直播开始时间">
	        </div>
	    </div>
		 <div class="weui_cells_title">直播结束时间(北京时间)</div>
	    <div class="weui_cells">
		    <div class="weui_cell">
		    	<input class="weui_input"  id="endtime" type="datetime-local"  placeholder="设置直播结束时间">
	        </div>
	    </div>
	  <div class="weui_cells_title">封面图片</div>
	  <div class="weui_cells">
	    <div class="weui_cell">
	    	<div class="weui_uploader_input_wrp bc_cover">
	    		<img id="bc_cover" class="weui_uploader_input" />
	    		<img class="imgview" id="bc_cover_view" src=""/>
	    	</div>
         </div>
	   </div>
	    <div class="weui_cells_title">直播头像</div>
	    <div class="weui_cells">
	     	<div class="weui_cell">
               <div class="weui_uploader_input_wrp bc_vip_cover">
		    		<img id="bc_vip_cover" class="weui_uploader_input" src=""/>
		    		<img class="imgview" id="bc_vip_cover_view" src=""/>
		    	</div>
             </div>
	    </div>
	    <div class="weui_cells_title">设置评论模式</div>
	    <div class="weui_cells weui_cells_radio">
	            <label class="weui_cell " for="speaker">
	                <div class="weui_cell_bd weui_cell_primary">
	                    <p>只有嘉宾发言，禁止评论</p>
	                </div>
	                <div class="weui_cell_ft">
	                    <input type="radio" class="weui_check" name="speakmode" id="speaker" checked="checked">
	                    <span class="weui_icon_checked"></span>
	                </div>
	            </label>
	            <label class="weui_cell "  for="allmember">
	                <div class="weui_cell_bd weui_cell_primary">
	                    <p>所有用户可以评论</p>
	                </div>
	                <div class="weui_cell_ft">
	                    <input type="radio" name="speakmode" class="weui_check" id="allmember" >
	                    <span class="weui_icon_checked"></span>
	                </div>
	            </label>
      </div>
      <div class="weui_cells">
      	 <div class="weui_cell">
	         <textarea id="bc_guest" class="weui_textarea"    placeholder="直播嘉宾介绍" rows=4></textarea>
	    </div>
      </div>
      <div class="weui_cells">
      	 <div class="weui_cell">
	         <textarea id="bc_project" class="weui_textarea"    placeholder="设置直播议程" rows=8></textarea>
	    </div>
      </div>
		<div class="weui_btn_area">
         	<a class="weui_btn weui_btn_primary" href="javascript:" id="btncreate">创建直播间</a>
     	</div>
</body>
<script>
	var openid="<%=openid%>";
	var phone="<%=phone%>";
	$(function(){
		//设置默认开始与结束时间
		var sdate=new Date(),edate=new Date();
		sdate.setTime(sdate.getTime()+7200*1000);
		edate.setTime(sdate.getTime()+7200*1000);
		$("#starttime").val(sdate.Format("yyyy-MM-ddThh:mm:ss"));
		$("#endtime").val(edate.Format("yyyy-MM-ddThh:mm:ss"));
		
		$(".bc_cover").height($(".bc_cover").width()/2);
		$(".bc_vip_cover").height($(".bc_vip_cover").width());
		
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
		
		var room={
				bc_name:"",
				bc_abstract:"",
				bc_project:"",
				bc_guest:"",
				bc_manager:openid,
				bc_manager_union:"",
				bc_status:"0",
				bc_starttime:"",
				bc_endtime:"",
				bc_speakmode:"",
				bc_cover:"",
				bc_vip_cover:"",
				bc_manager_phone:phone,
				bc_manage_headimg:"<%=headerimg%>",
				bc_manage_nickname:"<%=nickname%>"
		}
		
		function validate(obj){
			if(obj.bc_name==""){
				alert("请输入直播名称");
				return false;
			}
			var echar=0,cchar=0,totalchar=0;
			for(var i=0;i<obj.bc_name.length;i++){
				if (obj.bc_name.charCodeAt(i) > 0 && obj.bc_name.charCodeAt(i) < 128){
					echar++;
					totalchar+=1;
				}
                else{
                	cchar ++;
                	totalchar+=2;
                }
			}
			if(cchar>20 || echar>=40 ||totalchar>=40){
				alert("名称设置超长，中文20字以内，英文40字符以内");
				return false;
			}
			if(obj.bc_abstract==""){
				alert("请输入直播摘要");
				return false;
			}
			console.log(window.location.href);
			if(window.location.href.indexOf("localhost")<0&&window.location.href.indexOf("127.0.0.1")<0&&window.location.href.indexOf("192.168")<0){
				if(!obj.bc_cover||obj.bc_cover==""){
					alert("请设置直播封面");
					return false;
				}
				if(!obj.bc_vip_cover||obj.bc_vip_cover==""){
					alert("请设置直播头像");
					return false;
				}
			}
			if($("#starttime").val()>$("#endtime").val()){
				alert("直播时间区间设置错误。");
				return false;
			}
			return true;
		}
		
		$("#btncreate").bind("touchstart",function(){
			room.bc_name=$("#bc_name").val();
			room.bc_abstract=$("#bc_abstract").val();
			room.bc_starttime=$("#starttime").val();
			room.bc_endtime=$("#endtime").val();
			if($("#speaker")[0].checked){
				room.bc_speakmode="speaker";
			}else{
				room.bc_speakmode="all";
			}
			room.bc_project=$("#bc_project").val();
			room.bc_guest=$("#bc_guest").val();
			var cover_view=$("#bc_cover_view").attr("localid");
			var icon_view=$("#bc_vip_cover_view").attr("localid");
			room.bc_cover=cover_view;
			room.bc_vip_cover=icon_view;
			if(!validate(room))return;
			$.showLoading("正在提交...");
			//如果是本地运行直接提交
            if(!(window.location.href.indexOf("localhost")<0&&window.location.href.indexOf("127.0.0.1")<0&&window.location.href.indexOf("192.168")<0)){
            	 saveRoom(room,function(rep){
			        alertinfo("创建成功");
			        room.bc_no=rep.data.no;
                });
            }else{//非本地运行，上传图片
            	wx.uploadImage({
    	            localId: cover_view, // 需要上传的图片的本地ID，由chooseImage接口获得
    	            isShowProgressTips: 0, // 默认为1，显示进度提示
    	            success: function (res1) {
    	            	console.log(res1);
    	                var serverId = res1.serverId; // 返回图片的服务器端ID
    	                room.bc_cover=serverId;
    	                wx.uploadImage({
    	    	            localId:icon_view , // 需要上传的图片的本地ID，由chooseImage接口获得
    	    	            isShowProgressTips: 0, // 默认为1，显示进度提示
    	    	            success: function (res2) {
    	    	            	console.log(res2);
    	    	                var serverId = res2.serverId; // 返回图片的服务器端ID
    	    	                room.bc_vip_cover=serverId;
    	    	                saveRoom(room,function(rep){
    	    	                	$.toast("创建成功");
        	    			        room.bc_no=rep.data.no;
        	    			        var url="<%=BASE_PATH%>/onlive/mobile/roomconfig.jsp";
        	    			        window.location.href="onlive/mobile/goto.jsp?target="+url+"&state="+room.bc_no;
    	    	                });
    	    	            }
    	    	        });
    	            }
    	        });
            }
		});
		
		//保存直播间
		function saveRoom(obj,callback){
			console.log("saveRoom",obj);
			$.ajax({
				cache:false,
				url:"onlive/create.do",
				type:"POST",
				dataType:"JSON",
				data:obj,
				success:function(rep){
					$.hideLoading();
					if(rep.result>0){
						callback(rep);
					}else{
						$.alert(rep.message);
					}
					
				}
			});
		}
	});
</script>
</html>