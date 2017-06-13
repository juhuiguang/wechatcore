<%--
  Created by IntelliJ IDEA.
  User: 橘
  Date: 2016/7/12
  Time: 11:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String PATH = request.getContextPath();
    String BASE_URL  = request.getScheme()+"://"+request.getServerName();
    if(request.getServerPort()!=80){
        BASE_URL+=":"+request.getServerPort();
    }
    String BASE_PATH = BASE_URL + PATH +"/";
    request.setAttribute("BASE_PATH", BASE_PATH);
    String dpi=request.getParameter("dpi");
    if(dpi==null){
        dpi="2";
    }
    String User_Agent=request.getHeader("User-Agent");
%>
<jsp:include page="wxutil.jsp"></jsp:include>
<html>
<head>
    <base href="<%=BASE_PATH%>">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta name="format-detection" content="telephone=no" />
    <title>鲸鱼街-心晴海报</title>
    <link rel="stylesheet" href="css/weui.min.css" />
    <link rel="stylesheet" href="css/jquery-weui.css" />
    <script type="text/javascript" src="js/jquery-2.1.0.js"></script>
    <script type="text/javascript" src="js/jquery.dateformat.js"></script>
    <script type="text/javascript" src="js/jquery-weui.js"></script>
    <script type="text/javascript" src="js/angularjs/angular.min.js"></script>
    <script type="text/javascript" src="js/angularjs/ngTouch.min.js"></script>
    <script type="text/javascript" src="picbox/js/CanvasText-0.4.1.js"></script>
    <script src="http://api.map.baidu.com/api?v=2.0&ak=WaxXssCdjo3bQ8S9GWRI35VQ3FMbB8De" type="text/javascript"></script>
    <style>
        .weui-popup-overlay, .weui-popup-container{
            z-index: 200;
        }
        @font-face {
            font-family: macfont;
            src: url('picbox/resource/font/macfont.otf');
        }
        .greeting{
            position:absolute;
            font-weight:600;
            font-size:200%;
            left:1em;
            top:0.5em;
            font-family:macfont
        }
        .greeting.weather{
            position:absolute;
            font-weight:600;
            font-size:100%;
            left:2em;
            top:2em;
            font-family:macfont
        }
        .greeting.date,.greeting.time,.greeting.datetime{
            position:absolute;
            font-size:150%;
            font-weight:600;
            left:1em;
            top:1em;
        }

    </style>
</head>
<body ng-app="imgapp" style="background:#eee;">
    <div id="content" ng-controller="imgcontroller" class="weui_tab_bd" style="overflow-x:hidden;">
        <div class="weui_uploader_input_wrp bc_cover" id="pic_panel" style="float: left;position: relative;border: 1px solid #d9d9d9;margin:0;overflow: hidden;">
            <input ng-if="!iswechat" id="picbox_pic" class="weui_uploader_input" type="file" imagepreview fileread="img.file" style="z-index:99;" />
            <div ng-if="iswechat"  class="weui_uploader_input" ng-click="loadwechatimg()" style="z-index:99;" ></div>
            <div imagediv ng-show="img.show" style="background-color:transparent;width:100%;height:100%;overflow-y:hidden;position:absolute;left:0;top:0;z-index:90;">

            </div>
            <div ng-if="!debug" class="weui_uploader_input" style="z-index:99;"></div>
            <%--<img ng-src="{{img.file}}" style="width:100%;overflow-y:hidden;position:absolute;left:0;top:0;z-index:97;" imageimg imgshow="img.show"/>--%>
            <div id="imgmask" ng-if="img.show" style="width:100%;height:100%;position:absolute;left:0;top:0;z-index:98;background-color:rgba(0,0,0,0.2);"></div>
            <div id="sentence2"
                 ng-if="img.show&&!sentence2control"
                 style="position:absolute;min-height:1em;top:40%;left:10%;width:80%;color:#fff;font-family:macfont;font-size:120%;z-index: 105;text-align:center"
                 ng-touchstart="sentencestart($event)"
                 ng-touchmove="sentencemove($event)"
                 ng-touchend="sentenceend($event)"
            >
                <div ng-repeat="text in img.sentence2.split('/')">{{text}}</div>
            </div>
            <input  ng-if="sentence2control" ng-model="img.sentence2" ng-blur="showsentence2control(false)" style="position:absolute;bottom:50%;left:10%;width:80%;color:#fff;background:transparent;font-size:120%;z-index: 102;"/>
            <div ng-if="!img.islogo&&img.show" style="width:100%;text-align:center;position:absolute;bottom:0.5em;left:0;z-index:102;">
                <img src="picbox/resource/logo1.png" style="width:4em;height:2em;"/>
            </div>
        </div>
        <div style="clear:both;"></div>
        <div id="textpanel" style="width:100%;height:80px;background:#fff;position:relative;">
            <div id="greeting" ng-click="changegreeting()" class="greeting" ng-class="{date:img.greetingtype=='date',time:img.greetingtype=='time',datetime:img.greetingtype=='datetime',weather:img.greetingtype=='weather'}" >{{img.greeting}}</div>
            <img src="picbox/resource/qrlogo.png" width="80px" height="80px" style="float:right;"/>
            <%--<div id="datetext" ng-if="!datecontrol" ng-click="showchangedate()" style="position:absolute;font-size:90%;right:1em;bottom:2.5em;color:#333;">{{img.datetext|date:'yyyy.MM.dd'}}</div>--%>
            <%--<input ng-if="datecontrol" ng-model="img.datetext" ng-blur="changedate()" ng-change="changedate()" style="position:absolute;font-size:90%;right:1em;bottom:2.5em;color:#333;"  type="date"/>--%>
            <%--<div id="sentence1" ng-click="showsentence1control(true)" ng-if="!sentence1control" style="position:absolute;font-size:100%;right:1em;bottom:1em;color:#000;">{{img.weather}}</div>--%>
            <%--<input ng-model="img.weather" ng-blur="showsentence1control(false)" ng-if="sentence1control" style="position:absolute;font-size:100%;right:1em;bottom:1em;color:#000;" />--%>
        </div>
        <div style="width:100%;padding:1em;">
            <a ng-click="drawing()" class="weui_btn  weui_btn_plain_primary form-control" style="float:left;width:90%">生成图片</a>
            <%--<span style="float:left;margin-left:1em;margin-top:5px;font-size:90%;"><input type="checkbox" ng-model="img.islogo"/>去除logo（Az星际券*1）</span>--%>
        </div>
        <canvas id="canvas" style="background:#000;color:#fff;"></canvas>
        <div id="imagepop" class="weui-popup-container">
            <div class="weui-popup-modal">
                <img style="width:100%;"/>
                <div style="width:100%;text-align:center;font-size:90%;margin-top:1em;">
                    长按图片，可将图片保存至手机。<a href="javascript:void(0)" class='close-popup'>点此关闭</a>
                </div>
            </div>
        </div>
    </div>
    <%--<div class="weui_tabbar">--%>
        <%--<a href="javascript:;" class="weui_tabbar_item weui_bar_item_on">--%>
            <%--<p class="weui_tabbar_label">图片<br/>合成</p>--%>
        <%--</a>--%>
        <%--<a href="javascript:;" class="weui_tabbar_item">--%>
            <%--<p class="weui_tabbar_label">我的<br/>图库</p>--%>
        <%--</a>--%>

    <%--</div>--%>

</body>
<script type="text/javascript" src="picbox/js/text.js"></script>

<script>
    var User_Agent="<%=User_Agent%>";
    var dpi=<%=dpi%>;
    var imgapp=angular.module("imgapp",["ngTouch"]);
    imgapp.controller("imgcontroller",["$scope","$filter","$http",function($scope,$filter,$http){
        function gettext() {
            var len = window.textarr.length;
            var num = Math.random() * len;
            num = parseInt(num, 10);
            var text=window.textarr[num];
            while(!text||text==""){
                text=gettext();
            }
            return text;
        }

        $scope.debug=true;
        $scope.datecontrol=false;
        $scope.sentence1control=false;
        $scope.sentence2control=false;
        $scope.greetingtypes=["morning","noon","night","date","time","datetime","weather"];
        $scope.img={
            show:false,
            greeting:"",
            datetext:new Date(),
            sentence1:"每一天都是新鲜的",
            sentence2:gettext(),
            weather:""
        }
        getgreeting("weather");
        $scope.changetext=function(){
            $scope.img.sentence2=gettext();
        }

        function getgreeting(greeting){
            var pos=0;
            for(var i=0;i<$scope.greetingtypes.length;i++){
                if($scope.greetingtypes[i]==greeting){
                    if(i<$scope.greetingtypes.length-1){
                        pos=i+1;
                    }else{
                        pos=0;
                    }
                    break;
                }
            }
            $scope.img.greetingtype=$scope.greetingtypes[pos];
            switch($scope.img.greetingtype){
                case "morning":{
                    $scope.img.greeting="早安";
                    break;
                }
                case "noon":{
                    $scope.img.greeting="午安";
                    break;
                }
                case "night":{
                    $scope.img.greeting="晚安";
                    break;
                }
                case "date":{
                    $scope.img.greeting=$filter("date")(new Date(),"yyyy-MM-dd");
                    break;
                }
                case "time":{
                    $scope.img.greeting=$filter("date")(new Date(),"HH:mm");;
                    break;
                }
                case "datetime":{
                    $scope.img.greeting=$filter("date")(new Date(),"yyyy-MM-dd HH:mm");;
                    break;
                }
                case "weather":{
                    $scope.img.greeting=$scope.img.weather;
                    break;
                }
            }
        }
        $scope.changegreeting=function(){
            getgreeting($scope.img.greetingtype);
        }

        $scope.showchangedate=function(){
            $scope.datecontrol=true;
        }

        $scope.showsentence1control=function(flag){
            $scope.sentence1control=flag;
        }
        $scope.showsentence2control=function(flag){
            $scope.sentence2control=flag;
        }

        $scope.changedate=function(date){
            //$scope.img.datetext=date.toLocaleString;
            $scope.datecontrol=false;
        }

        function isWeixinBrowser(){
            return /micromessenger/.test(navigator.userAgent.toLowerCase())
        }

        if(false){
            $scope.iswechat=true;
        }else{
            $scope.iswechat=false;
        }
        $scope.loadwechatimg=function(){
            wx.chooseImage({
                count: 1, // 默认9
                sizeType: ['compressed'], // 可以指定是原图还是压缩图，默认二者都有
                sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
                success: function (res) {
                    var img = res.localIds[0]; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                    var reader = new FileReader();
                    reader.onload = function (loadEvent) {
                        $scope.$apply(function () {
                            $scope.img.file = loadEvent.target.result;
                        });
                    }
                    reader.readAsDataURL(img);
                }
            });
        }

        $scope.thouch={
            start:{
                time:0,
                x:0,
                y:0
            },
            end:{
                time:0,
                x:0,
                y:0
            }
        }
        $scope.sentencepos={
            offsetx:0,
            offsety:0
        }
        //触摸开始
        $scope.sentencestart=function(event){
            var touches=event.originalEvent.targetTouches;
            var touch=touches[0];
            var x=touch.screenX;
            var y=touch.screenY;
            var element=$(event.currentTarget);
            var sx=parseInt(element.css("left"));
            var sy=parseInt(element.css("top"));
            console.log("1>>",sx,sy,touch);
            $scope.sentencepos.offsetx=(touch.clientX-sx);
            $scope.sentencepos.offsety=(touch.clientY-sy);
            //element.css("left",touch.clientX).css("top",touch.clientY);
            $scope.thouch.start.time=new Date().getTime();
            $scope.thouch.end.time=new Date().getTime();
            $scope.thouch.start.x=x;
            $scope.thouch.start.y=y;
            $scope.thouch.end.x=x;
            $scope.thouch.end.y=y;
        }
        //移动过程
        $scope.sentencemove=function(event){
            var touches=event.originalEvent.targetTouches;
            var touch=touches[0];
            var x=touch.screenX;
            var y=touch.screenY;
            $scope.thouch.end.x=x;
            $scope.thouch.end.y=y;
            var xdiff=$scope.thouch.end.x- $scope.thouch.start.x;
            var ydiff=$scope.thouch.end.y- $scope.thouch.start.y;
            var distance=Math.round(Math.sqrt(xdiff*xdiff+ydiff*ydiff));
            if(distance>10){
                var element=$(event.currentTarget);
                console.log("1>>",touch.clientX,$scope.sentencepos.offsetx,touch.clientX-$scope.sentencepos.offsetx);
                if((touch.clientX-$scope.sentencepos.offsetx)<(-$scope.sentencepos.offsetx)){
                    element.css("left",(-$scope.sentencepos.offsetx))
                }else if(touch.clientX>($(window).width()*0.95)){
                    element.css("left",($(window).width()*0.95)-$scope.sentencepos.offsetx);
                }else{
                    element.css("left",touch.clientX-$scope.sentencepos.offsetx);
                }
                console.log("2>>",touch.clientY,$scope.sentencepos.offsety,touch.clientY-$scope.sentencepos.offsety);
                if((touch.clientY-$scope.sentencepos.offsety)<(-$scope.sentencepos.offsety)){
                    element.css("top",(-$scope.sentencepos.offsety))
                }else if(touch.clientY>($(window).width()*0.95)){
                    element.css("top",($(window).width()*0.95)-$scope.sentencepos.offsety);
                }else{
                    element.css("top",touch.clientY-$scope.sentencepos.offsety);
                }

//                $scope.sentencepos.x=touch.clientX-$scope.sentencepos.offsetx;
//                $scope.sentencepos.y=touch.clientY-$scope.sentencepos.offsety;

                $scope.sentencepos.x=parseInt(element.css("left"));
                $scope.sentencepos.y=parseInt(element.css("top"));
            }
        }
        //触摸结束
        $scope.sentenceend=function(event){
            $scope.thouch.end.time=new Date().getTime();
            //判断触摸行为
            var timediff= $scope.thouch.end.time- $scope.thouch.start.time;
            var xdiff=$scope.thouch.end.x- $scope.thouch.start.x;
            var ydiff=$scope.thouch.end.y- $scope.thouch.start.y;
            var distance=Math.round(Math.sqrt(xdiff*xdiff+ydiff*ydiff));
            console.log(distance);
            if(timediff>800){//大于800毫秒，认定为长按
                if(distance<10){//如果移动小于10个像素，认定为长按
                    console.log("long touch");
                    $scope.showsentence2control(true);
                }
            }else{//视为单击
                if(distance<10){
                    $scope.img.sentence2=gettext();
                }

            }

        }

        $scope.drawing=function(){
            if($scope.img.file==null){
                return;
            }
            $.showLoading("正在生成图片...");
            var c = document.getElementById("canvas");
            c.width=$scope.canvas.width;
            c.height=$scope.canvas.height;
            var ctx = c.getContext('2d');
            ctx.clearRect(0,0,$scope.canvas.width,$scope.canvas.height);
            var canvasText = new CanvasText;
            canvasText.config({
                canvas: c,
                context: ctx
            });
            canvasText.defineClass("date",{
                fontSize: (14*1.5*dpi)+"px",
                fontColor: "#000",
                fontFamily: "macfont",
                fontWeight: "normal"
            });
            canvasText.defineClass("weather",{
                fontSize: (14*1.3*dpi)+"px",
                fontColor: "#000",
                fontFamily: "macfont",
                fontWeight: "normal"
            });
            canvasText.defineClass("greeting",{
                fontSize: (14*2.5*dpi)+"px",
                fontColor: "#000",
                fontFamily: "macfont",
                fontWeight: "normal"
            });
            canvasText.defineClass("normal",{
                fontSize: (20*dpi)+"px",
                fontColor: "#fff",
                fontFamily: "macfont",
                fontWeight: "normal"
            });
            //绘制早安
            ctx.fillStyle = 'rgba(255,255,255,1)';
            ctx.fillRect(0,$scope.canvas.width,$scope.canvas.width,$scope.canvas.height);
//            ctx.font = "normal normal bold "+(250*dpi)+"% 宋体";
//            ctx.fillStyle = "black";
            var text="";
            var yoffset=0;
            switch($scope.img.greetingtype){
                case "morning":
                case "noon":
                case "night":{
                    text="<class='greeting'>"+$scope.img.greeting+"</class>";
                    break;
                }
                case "weather":{
                    text="<class='weather'>"+$scope.img.greeting+"</class>";
                    yoffset=5*dpi;
                    break;
                }
                default:{
                    yoffset=5*dpi;
                    text="<class='date'>"+$scope.img.greeting+"</class>";
                }
            }
            canvasText.drawText({
                text:text,
                x: $scope.canvas.width*0.03,
                y: $scope.canvas.height-(25*dpi)-yoffset
            });


//            ctx.font = "normal normal normal "+(90*dpi)+"% 宋体";
//            ctx.fillStyle = "#333333";
//            ctx.textAlign="end";
//            ctx.fillText($filter('date')($scope.img.datetext,"yyyy.MM.dd"), $scope.canvas.width*0.97,$scope.canvas.height-50*dpi);
//
//            ctx.font = "normal normal normal "+(100*dpi)+"% 宋体";
//            ctx.fillStyle = "#333333";
//            ctx.textAlign="end";
//            ctx.fillText($scope.img.weather, $scope.canvas.width*0.97,$scope.canvas.height-30*dpi);
            var img = new Image;
            img.src = $scope.img.file;
            //图片加载完成
            img.onload = function() {//图片上的操作都在图片加载完成后进行
                console.log(img.width,img.height);
                if(img.width>img.height){
                    var wpos=(img.width-img.height)/2;
                    ctx.drawImage(img, wpos, 0, img.height, img.height, 0, 0, $scope.canvas.width, $scope.canvas.width);
                    //console.log(wpos, 0, img.height+wpos, img.height, 0, 0, $scope.canvas.width, $scope.canvas.width);
                }else{
                    var hpos=(img.height-img.width)/2;
                    ctx.drawImage(img, 0, hpos, img.width, img.width, 0, 0, $scope.canvas.width, $scope.canvas.width);
                    //console.log(0, hpos, img.width, img.width+hpos, 0, 0, $scope.canvas.width, $scope.canvas.width);
                }
                //为图片增加黑色透明蒙版
                ctx.fillStyle = 'rgba(0,0,0,0.2)';
                ctx.fillRect(0,0,$scope.canvas.width,$scope.canvas.width);
                //填充文字
//                ctx.font="normal normal normal "+(120*dpi)+"% 宋体";
//                ctx.fillStyle = "white";
//
//                ctx.textAlign="start";
                //填写图片上的文字
                var textarr=$scope.img.sentence2.split("/");
                for(var i=0;i<textarr.length;i++){
//                    var length=textarr[i].length*20*dpi;
//                    var start=$scope.canvas.width * 0.1
//                    if(length<$scope.canvas.width*0.8){
//                        start=($scope.canvas.width*0.8-length)/2+$scope.canvas.width * 0.1
//                    }
//                    canvasText.drawText({
//                        text:"<class='normal'>"+textarr[i]+"</class>",
//                        x: start,
//                        y: ($scope.canvas.width*0.48+((22*dpi+10)*(i-textarr.length/2)))
//                    });
//                    $scope.sentencepos.x=touch.clientX-$scope.sentencepos.offsetx;
//                    $scope.sentencepos.y=touch.clientY-$scope.sentencepos.offsety;
                    if(!$scope.sentencepos.x){
                        $scope.sentencepos.x=0;
                    }
                    if(!$scope.sentencepos.y){
                        $scope.sentencepos.y=0;
                    }
                    var length=textarr[i].length*20*dpi;
                    var start=(Math.abs($scope.sentencepos.x)>0)?$scope.sentencepos.x*dpi:$scope.canvas.width*0.1;
                    if(length<$scope.canvas.width*0.8){
                        start=($scope.canvas.width*0.8-length)/2+((Math.abs($scope.sentencepos.x)>0)?($scope.sentencepos.x*dpi):($scope.canvas.width*0.1));
                    }
                    canvasText.drawText({
                        text:"<class='normal'>"+textarr[i]+"</class>",
                        x: start,
                        y: (((Math.abs($scope.sentencepos.y)>0)?$scope.sentencepos.y*dpi:$scope.canvas.width*0.48)+((22*dpi+10)*(($scope.sentencepos.y==0)?(i-textarr.length/2):(i+1))))
                    });

                }

                if(!$scope.img.islogo){
                    var logoimg=new Image;
                    logoimg.src="picbox/resource/logo1.png";
                    logoimg.onload=function(){
                        ctx.drawImage(logoimg, $scope.canvas.width*0.42,$scope.canvas.width*0.89 , $scope.canvas.width*0.16, $scope.canvas.width*0.08);
                        //绘制二维码
                        var qrcode = new Image;
                        qrcode.src = "picbox/resource/qrlogo.png";
                        qrcode.onload=function(){
                            ctx.drawImage(qrcode,c.width-75*dpi,c.height-80*dpi+2.5*dpi,75*dpi,75*dpi);
                            $http({
                                url:"picboxservlet/upload",
                                method:"POST",
                                data:encodeURIComponent(c.toDataURL('image/jpeg',1))
                            }).then(function(rep){
                                $.hideLoading();
                                $("#imagepop img").attr("src","./"+rep.data);
                                $("#imagepop").popup();
                            });
                        }


                    }
                }

                //$("#canvas").show();
            }




        }
        $(function(){
            var img_width=$(window).width();
            var img_height=img_width;
            $("#pic_panel").width(img_width).height(img_height);
            $("#canvas").attr("width",img_width*dpi).attr("height",(img_height*dpi+80*dpi)).hide();
            $scope.canvas={
                width:img_width*dpi,
                height:img_height*dpi+80*dpi
            }

            //获取天气
            var local = new BMap.LocalCity();
            local.get(function(city) {
                console.log(city);
                var url = "http://api.map.baidu.com/telematics/v3/weather?location=" + city.name + "&output=json&ak=WaxXssCdjo3bQ8S9GWRI35VQ3FMbB8De";
                $.ajax({
                    type: "get",
                    url: url,
                    dataType: "jsonp",
                    jsonp: "callback",
                    jsonpCallback: "weathercallback",
                    async: true,
                    success: function(rep) {
                        if (rep.error == 0) {
                            var weather=rep.results[0].weather_data[0];
                            $scope.$apply(function(){
                                $scope.img.weather = weather.weather+" "+weather.temperature;
                            });
                        }
                    }
                });
            });
        });
    }]);

    imgapp.directive("imagepreview",[function(){
        return {
            scope: {
                fileread: "="
            },
            link: function (scope, element, attributes) {
                element.bind("change", function (changeEvent) {
                    if(changeEvent.target.files.length>0){
                        $.showLoading("正在渲染图片...");
                    }
                    var reader = new FileReader();
                    reader.onload = function (loadEvent) {
                        scope.$apply(function () {
                            scope.fileread = loadEvent.target.result;
                        });
                        $.hideLoading();
                    }
                    reader.readAsDataURL(changeEvent.target.files[0]);
                });
            }
        }
    }]);

    imgapp.directive("imageimg",[function(){
        return {
            scope:{
                imgshow:"="
            },
            link: function (scope, element, attributes) {
                element.bind("load",function(){
                    var w=this.width;
                    var h=this.height;
                    if(w>h){
                        element.css("height","100%");
                        element.css("width","auto");
                    }else{
                        element.css("width","100%");
                        element.css("height","auto");
                    }
                    scope.$apply(scope.imgshow=true);
                });
            }
        }
    }]);

    imgapp.directive("imagediv",[function(){
        return {
            link: function (scope, element, attributes) {
                if(!scope.img.file){
                    scope.img.show=false;
                }
                scope.$watch("img.file",function(newvalue,oldvalue){
                    if(!scope.img.file){
                        scope.img.show=false;
                    }else{
                        var pic=new Image();
                        pic.src=scope.img.file;
                        pic.onload=function(){
                            console.log("pic load");
                            element.css("background-image","url("+pic.src+")");
                            if(pic.width>pic.height){
                                element.css("background-size","auto 100%");
                                element.css("background-position","center center");
                            }else{
                                element.css("background-size","100% auto");
                                element.css("background-position","center center");
                            }

                            scope.$apply(scope.img.show=true);
                        }

                    }
                });
            }
        }
    }]);

</script>
<script>
    var _hmt = _hmt || [];
    (function() {
        var hm = document.createElement("script");
        hm.src = "//hm.baidu.com/hm.js?7de71fe36c929112d615950877f61b0b";
        var s = document.getElementsByTagName("script")[0];
        s.parentNode.insertBefore(hm, s);
    })();
</script>

</html>
