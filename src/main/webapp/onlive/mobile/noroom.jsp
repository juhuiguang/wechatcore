<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html">
<html>
<%@ include file="wxutil.jsp"%>
<style>
	
</style>
<body>
	<div class="weui_msg">
        <div class="weui_icon_area"><i class="weui_icon_warn weui_icon_msg"></i></div>
        <div class="weui_text_area">
            <p class="weui_msg_title">无法操作</p>
            <p class="weui_msg_desc">很抱歉，您的操作无法继续。看到此信息，可能是以下几种情况：</p>
            <p class="weui_msg_desc">您创建的直播间已达到数量上限。</p>
            <p class="weui_msg_desc">您的账户或该直播间管理员账户已超过有效期。</p>
            <p class="weui_msg_desc">您的账户或该直播间管理员账户被停用。</p>
            <p class="weui_msg_desc">该直播间被系统管理员关闭。</p>
            <p class="weui_msg_desc">您访问的直播间不存在。</p>
        </div>
        <div class="weui_opr_area">
            <p class="weui_btn_area"  id="btncancle">
                <a href="javascript:;" class="weui_btn weui_btn_default" >取消</a>
            </p>
        </div>
    </div>
</body>
<script>
	$("#btncancle").bind("touchstart",function(){
		window.close();
	});
</script>
</html>