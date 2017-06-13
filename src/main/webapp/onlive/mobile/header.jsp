<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <% 
    String PATH = request.getContextPath();
	String BASE_URL  = request.getScheme()+"://"+request.getServerName();
	if(request.getServerPort()!=80){
		BASE_URL+=":"+request.getServerPort();
	}
	String BASE_PATH = BASE_URL + PATH +"/";
	request.setAttribute("BASE_PATH", BASE_PATH);
    %>
<head>

<base href="<%=BASE_PATH%>"> 
<meta charset="UTF-8">
   <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
  <meta name="apple-mobile-web-app-capable" content="yes" />
  <meta name="format-detection"content="telephone=no" />
  <title>直播间</title>
<link rel="stylesheet" href="css/weui.min.css" />
<link rel="stylesheet" href="css/jquery-weui.css" />
<script type="text/javascript" src="js/jquery-2.1.0.js"></script>
<script type="text/javascript" src="js/jquery.dateformat.js"></script>
<script type="text/javascript" src="js/jquery-weui.js"></script>
<script>
$.toast.prototype.defaults.duration=1000;
</script>
<style>

</style>
</head>