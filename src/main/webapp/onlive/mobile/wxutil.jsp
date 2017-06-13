<%@page import="com.alienlab.wechat.core.util.WeixinUtil"%>
<%@page import="com.alienlab.wechat.bean.AccessToken"%>
<%@page import="com.alienlab.utils.PropertyConfig"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="header.jsp"%>
<% 
String cur_url= request.getScheme()+"://"+ request.getServerName()+request.getRequestURI();
String queryStr=request.getQueryString();
if(queryStr!=null&&!queryStr.equals("")){
	cur_url=cur_url+"?"+queryStr;
}
Map<String ,String> m=WeixinUtil.getJsapiSignature(cur_url);
if(m==null){
	WeixinUtil.getMenu();
	out.println("没有正确获取微信js-sdk");
	return;
}
String timestamp=m.get("timestamp");
String nonceStr=m.get("nonceStr");
String signature=m.get("signature");
String appid=m.get("appid");

//从session获得用户信息
String openid=(session.getAttribute("openid")==null)?"":session.getAttribute("openid").toString();
if(openid==null)openid="";
String nickname=(session.getAttribute("nickname")==null)?"":session.getAttribute("nickname").toString();
if(nickname==null)nickname="";
String headerimg=(session.getAttribute("headerimg")==null)?"":session.getAttribute("headerimg").toString();
if(headerimg==null)headerimg=BASE_PATH+"img/azlogo.jpg";

java.util.Map<String,String> user=null;

String code=request.getParameter("code");
//没有接到微信返回的授权码
if(code!=null){
	//通过code获得openid
	if(openid==null||openid.equals("")){//session中有值将直接使用
		com.alibaba.fastjson.JSONObject jo=WeixinUtil.getWxUserBase(code);
		//获取当前用户openid出错，有可能是用户点击浏览器返回按钮
		if(jo.containsKey("errcode")){
			openid=(session.getAttribute("openid")==null)?"":session.getAttribute("openid").toString();
			if(openid==null)openid="";
			nickname=(session.getAttribute("nickname")==null)?"":session.getAttribute("nickname").toString();
			if(nickname==null)nickname="";
			headerimg=(session.getAttribute("headerimg")==null)?"":session.getAttribute("headerimg").toString();
			if(headerimg==null)headerimg=BASE_PATH+"img/azlogo.jpg";
			System.out.println("code has timeout..");
			if(openid==null||openid.equals("")){
				out.println("获取微信授权失败，请关闭界面重新进入系统。");
				return;
			}
	  	}else{
	  		openid=jo.getString("openid");//使用接口获得到的openid；
	  		String token=jo.getString("access_token");
	  		//调取用户基本信息
	  		user=WeixinUtil.getWxinfo(openid, token);
	  		//获得用户昵称
	  		nickname=user.get("nickname");
	  		//获得用户头像地址
	  		headerimg=user.get("headimgurl");
	  		//用户信息获取正确，将信息存入session
	  		session.setAttribute("openid",openid);
	  		session.setAttribute("nickname",nickname);
	  		session.setAttribute("headerimg",headerimg);
	  	}
	}
}else{
	System.out.println("request is not from wechat..");
}



%>
<script type="text/javascript" charset="utf-8" src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script>
var wxdebug=false;
wx.config({
    debug: wxdebug,
    appId: '<%=appid%>',
    timestamp: <%=timestamp%>,
    nonceStr: '<%=nonceStr%>',
    signature: '<%=signature%>',
    jsApiList: [
      'checkJsApi',
      'onMenuShareTimeline',
      'onMenuShareAppMessage',
      'onMenuShareQQ',
      'onMenuShareWeibo',
      'hideMenuItems',
      'showMenuItems',
      'hideAllNonBaseMenuItem',
      'showAllNonBaseMenuItem',
      'translateVoice',
      'startRecord',
      'stopRecord',
      'onRecordEnd',
      'playVoice',
      'pauseVoice',
      'stopVoice',
      'uploadVoice',
      'downloadVoice',
      'chooseImage',
      'previewImage',
      'uploadImage',
      'downloadImage',
      'getNetworkType',
      'openLocation',
      'getLocation',
      'hideOptionMenu',
      'showOptionMenu',
      'closeWindow',
      'scanQRCode',
      'chooseWXPay',
      'openProductSpecificView',
      'addCard',
      'chooseCard',
      'openCard'
    ]
});

wx.ready(function (){ 	
	if(wxdebug){
		alert("微信JS onready");
	}
	wx.hideMenuItems({
	    menuList: ["menuItem:share:appMessage",
	    	"menuItem:share:timeline",
	    		"menuItem:share:qq",
	    		"menuItem:share:weiboApp",
	    		"menuItem:favorite",
	    		"menuItem:share:facebook",
	    		"menuItem:share:QZone"] // 要隐藏的菜单项，只能隐藏“传播类”和“保护类”按钮，所有menu项见附录3
	});
});
</script>
