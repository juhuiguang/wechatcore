<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String target=request.getParameter("target");
	String state=request.getParameter("state");
	if(target==null||target.equals("")){
		return;
	}
	String gotolink=com.alienlab.wechat.core.util.WeixinUtil.getAuthUrl(target, state, "info");
	System.out.println("go to link:"+gotolink);
	response.sendRedirect(gotolink);
%>