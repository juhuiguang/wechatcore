<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="java.util.Calendar"%>
<%@page import="com.alienlab.wechat.onlive.service.OnliveManager"%>
    <%@page import="com.alienlab.wechat.onlive.bean.OnliveRoom"%>
    <%@page import="com.alienlab.wechat.onlive.bean.OnliveMember"%>
    <%@page import="com.alibaba.fastjson.JSONObject"%>
    <%@page import="com.alienlab.common.TypeUtils"%>
   
 <%
 	String roomno=request.getParameter("roomno");
 	int number=Integer.parseInt(request.getParameter("number"));
 	
 	if(roomno==null||roomno.equals("")){
 		out.println("请指定直播间编号");
 		return;
 	}

 	OnliveRoom room=OnliveManager.getRoom(roomno);
 	if(room==null){
 		out.println("直播间不存在");
 		return;
 	}
 	for(int i=0;i<number;i++){
 		OnliveMember member=new OnliveMember();
 		member.setOpenid("alienzoo_test"+Calendar.getInstance().getTimeInMillis());
 		member.setNick("机器人用户");
 		member.setLocalPic("http://www.bigercat.com/wechatsolution/img/azlogo.jpg");
 		member.setPic("http://www.bigercat.com/wechatsolution/img/azlogo.jpg");
 		member.setVip(false);
 		member.setJoinType("test");
 		member.setRoom_no(roomno);
 		room.joinRoom(member);
 	}
 	out.println("用户数据刷新完成，当前直播间已有用户："+room.getMemberCount());
 	
 %>