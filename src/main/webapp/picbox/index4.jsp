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
%>
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
            src: url('picbox/resource/font/macfont3.otf');
        }

    </style>
</head>
<body ng-app="imgapp" style="background:#eee;">
    <div id="content" ng-controller="imgcontroller" class="weui_tab_bd" style="overflow-x:hidden;">
        <div class="weui_uploader_input_wrp bc_cover" id="pic_panel" style="float: left;position: relative;border: 1px solid #d9d9d9;margin:0;overflow: hidden;">
            <input ng-if="debug" id="picbox_pic" class="weui_uploader_input" type="file" imagepreview fileread="img.file" style="z-index:99;" />
            <div ng-if="!debug" class="weui_uploader_input" style="z-index:99;"></div>
            <img ng-src="{{img.file}}" style="width:100%;overflow-y:hidden;position:absolute;left:0;top:0;z-index:97;" imagediv imgshow="img.show"/>
            <div id="imgmask" ng-if="img.show" style="width:100%;height:100%;position:absolute;left:0;top:0;z-index:98;background-color:rgba(0,0,0,0.2);"></div>
            <div id="sentence2"
                 ng-touchstart="sentencestart($event)"
                 ng-touchmove="sentencemove($event)"
                 ng-touchend="sentenceend($event)"
                 ng-if="img.show&&!sentence2control"
                 ng-click="showsentence2control(true)"
                 style="position:absolute;min-height:1em;bottom:50%;left:10%;width:80%;color:#fff;font-family:macfont;font-size:120%;z-index: 101;text-align:center">
                <div ng-repeat="text in img.sentence2.split('/')">{{text}}</div>
            </div>
            <input  ng-if="sentence2control" ng-model="img.sentence2" ng-blur="showsentence2control(false)" style="position:absolute;bottom:50%;left:10%;width:80%;color:#fff;background:transparent;font-size:120%;z-index: 102;"/>
            <div ng-if="!img.islogo&&img.show" style="width:100%;text-align:center;position:absolute;bottom:0.5em;left:0;z-index:102;">
                <img src="picbox/resource/logo.png" style="width:3em;height:3em;"/>
            </div>
        </div>
        <div style="clear:both;"></div>
        <div id="textpanel" style="width:100%;height:80px;background:#fff;position:relative;">
            <div id="greeting" ng-click="changegreeting()" style="position:absolute;font-weight:600;font-size:200%;left:1em;top:0.5em;font-family:macfont">{{img.greeting}}</div>
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
    var dpi=<%=dpi%>;
    var imgapp=angular.module("imgapp",["ngTouch"]);
    imgapp.controller("imgcontroller",["$scope","$filter","$http",function($scope,$filter,$http){
        function gettext() {
            var len = window.textarr.length;
            var num = Math.random() * len;
            num = parseInt(num, 10);
            return window.textarr[num];
        }
        $scope.debug=true;
        $scope.img={
            show:false,
            greeting:"晚安",
            datetext:new Date(),
            sentence1:"每一天都是新鲜的",
            sentence2:gettext(),
            weather:""
        }
        $scope.changetext=function(){
            $scope.img.sentence2=gettext();
        }
        $scope.datecontrol=false;
        $scope.sentence1control=false;
        $scope.sentence2control=false;
        $scope.changegreeting=function(){
            if($scope.img.greeting=="晚安"){
                $scope.img.greeting="早安";
            }else{
                $scope.img.greeting="晚安";
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
            var element=$(event.currentTarget);
            var x=parseInt(element.css("left"));
            var y=parseInt(element.css("top"));
            $scope.sentencepos.offsetx=(touch.clientX-x);
            console.log(touch.clientX,x,touch.clientX-x);
            $scope.sentencepos.offsety=(touch.clientY-y);
            //element.css("left",touch.clientX).css("top",touch.clientY);
            //console.log("start>>",event,touch,element);
        }
        //移动过程
        $scope.sentencemove=function(event){
            var touches=event.originalEvent.targetTouches;
            var touch=touches[0];
            var element=$(event.currentTarget);
            element.css("left",touch.clientX-$scope.sentencepos.offsetx).css("top",touch.clientY-$scope.sentencepos.offsety);
            $scope.sentencepos.x=touch.clientX-$scope.sentencepos.offsetx;
            $scope.sentencepos.y=touch.clientY-$scope.sentencepos.offsety;
            //console.log("move>>",event,touch,element);
        }
        //触摸结束
        $scope.sentenceend=function(event){

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
            canvasText.drawText({
                text:"<class='greeting'>"+$scope.img.greeting+"</class>",
                x: $scope.canvas.width*0.03,
                y: $scope.canvas.height-(25*dpi)
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
                if(img.width>img.height){
                    ctx.drawImage(img, 0, 0, img.height, img.height, 0, 0, $scope.canvas.width, $scope.canvas.width);
                }else{
                    ctx.drawImage(img, 0, 0, img.width, img.width, 0, 0, $scope.canvas.width, $scope.canvas.width);
                }
                //为图片增加黑色透明蒙版
                ctx.fillStyle = 'rgba(0,0,0,0.2)';
                ctx.fillRect(0,0,$scope.canvas.width,$scope.canvas.width);
                //填充文字
//                ctx.font="normal normal normal "+(120*dpi)+"% 宋体";
//                ctx.fillStyle = "white";
//
//                ctx.textAlign="start";
                var textarr=$scope.img.sentence2.split("/");
//                var str='<class="normal">'+$scope.img.sentence2.replace(/\//,'</class><br/><class="normal">')+'</class>';
//                console.log(str);
//                canvasText.drawText({
//                    text:str,
//                    x: $scope.sentencepos.x*dpi,
//                    y: $scope.sentencepos.y*dpi
//                });

                for(var i=0;i<textarr.length;i++){
                    var length=textarr[i].length*20*dpi;
                    var start=$scope.canvas.width * 0.1
                    if(length<$scope.canvas.width*0.8){
                        start=($scope.canvas.width*0.8-length)/2+$scope.canvas.width * 0.1
                    }
                    //$scope.canvas.width*0.48
                    canvasText.drawText({
                        text:"<class='normal'>"+textarr[i]+"</class>",
                        x: start+($scope.sentencepos.x*dpi),
                        y: (($scope.sentencepos.y*dpi)+((22*dpi+10)*(i-textarr.length/2)))
                    });
                }

                if(!$scope.img.islogo){
                    var logoimg=new Image;
                    logoimg.src="picbox/resource/logo1.png";
                    logoimg.onload=function(){
                        ctx.drawImage(logoimg, $scope.canvas.width*0.45,$scope.canvas.width*0.89 , $scope.canvas.width*0.1, $scope.canvas.width*0.1);
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
                                $scope.img.weather = weather.weather+" "+weather.temperature+" "+weather.wind;
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

    imgapp.directive("imagediv",[function(){
        return {
            scope:{
                imgshow:"="
            },
            link: function (scope, element, attributes) {
                element.bind("load", function (changeEvent) {
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
    imgapp.directive("moveleft",[function(){
        function gettext() {
            var len = window.textarr.length;
            var num = Math.random() * len;
            num = parseInt(num, 10);
            return window.textarr[num];
        }
        return {
            scope:{
                text:"="
            },
            link: function (scope, element, attributes) {
                var touchflag=false;
                var x=0,x1=0;
                element.bind("touchstart", function (touchevent) {
                    x=touchevent.originalEvent.targetTouches[0].pageX;
                    x1=x;
                    console.log("starttouch")
                    touchflag=true;
                });
                element.bind("touchmove", function (touchevent) {
                    if(touchflag){
                        x1=touchevent.originalEvent.targetTouches[0].pageX;
                    }
                });
                element.bind("touchend", function (touchevent) {
                    touchflag=false;
                    console.log(x1,x);
                    if(Math.abs(x1-x)>10){
                        scope.$apply(function(){scope.text=gettext();});
                        console.log("change text>>"+scope.text);
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
