/**
 * Alienlab studio
 * 信息流展示组件
 * version 1.0
 */
(function($,window) {
	$.fn.extend({
		infostream:function(config){
			console.log(config);
			this.latestid="0";
			this.timefield=config.timefield?config.timefield:"time";
			this.itemidfield=config.itemidfield?config.itemidfield:"itemno";
			this.loadfunc=config.loadfunc;
			this.itemloadfunc=config.itemloadfunc;
			this.items={};
			this.autoload=config.autoload;
			this.currentAudio=null;
			this.currentVideo=null;
			this.sorttype="desc";
			this.latestinfo={
					latestpic:null,
					latesttext:null
			}
			if(config.sorttype!=null&&config.sorttype!=""){
				this.sorttype=config.sorttype;
			}
			var stream=this;
			var dom=$(this);
			
			
			/**
			 * 刷新数据，获取最近20条
			 * 数据返回数组，并调用方法回调
			 */
			this.refresh=function(callback){
				if(!stream.loadfunc)return;
				var time="0";
				//调用去数据方法并回调
				if(stream.latestid!="0"){
					time=$("#stream_"+stream.latestid).attr("time");
				}
				stream.loadfunc(time,"head",stream.sorttype,function (items){
					if(callback)callback();
					processdata(items,"head",stream.latestid);
					dom.find(".blanktip").remove();
					if(dom.find(".stream.item").length<=0){
						dom.append($("<div class='blanktip'>房主目前还没有发布内容，敬请期待~。</div>"));
					}
				});
			}
			
			/**
			 * 向后加载数据
			 */
			this.loadAfter=function(itemid){
				if(!stream.loadfunc)return;
				if(!itemid)return;
				var time=$("#stream_"+itemid).attr("time");
				stream.loadfunc(time,"tail",stream.sorttype,function(items){
					processdata(items,"tail",itemid);
				});
				
			}
			/**
			 * 重新加载某一个内容
			 */
			this.reloadItem=function(itemid){
				if(!stream.itemloadfunc)return;
				if(stream.items[itemid]==null)return;
				var item=stream.items[itemid];
				stream.itemloadfunc(itemid,function(newitem){
					if(newitem.link!=null&&newitem.link!=""){
						item.link=newitem.link;
						item.piclink=newitem.content_piclink;
						stream.items[itemid]=item;
						if($("#stream_"+itemid+" .itemcontent").hasClass("needload")){
							$("#stream_"+itemid+" .itemcontent").removeClass("needload");
							$("#stream_"+itemid+" .itemcontent .loadmask").hide();
							if(item.type=="image"){
								$("#stream_"+itemid+" .itemcontent .media img").attr("src",item.link);
							}else if(item.type=="voice"){
								$("#stream_"+itemid+" .itemcontent .media audio").attr("src",item.link);
							}else if(item.type=="video"||item.type=="shortvideo"){
								$("#stream_"+itemid+" .itemcontent .media img").attr("src",item.piclink);
								$("#stream_"+itemid+" .itemcontent .media video").attr("src",item.link);
							}
							$("#stream_"+itemid+" .itemcontent .media").show();
						}
					}else{
						$("#stream_"+itemid+" .itemcontent .loadmask").text("内容未下载完全，请点击重新加载内容");
					}
				});
			}
			
			function processdata(items,direction,targetid){
				if(items==null||items.length==0){
					console.log("no data refresh");
					return;
				}
				//根据获取到的数据，初始化成为streamItem对象，并且存入数组
				var dataitems=[];
				for(var i=0;i<items.length;i++){
					var item=new streamItem(items[i]);
					if(stream.items[item[stream.itemidfield]]!=null){
						break;
					}else{
						stream.items[item[stream.itemidfield]]=item;
						dataitems.push(item);
					}
				}
				if(direction=="head"){
					stream.latestid=items[0]["no"];
				}
				if(dataitems.length>0){
					//将数组中stream对象添加到页面中
					stream.addItem(dataitems,direction,targetid);
				}
				
				
			}
			
			/**
			 * 向界面添加stream
			 */
			this.addItem=function(items,direction,targetid){
				if(direction=="head"){
					for(var i=items.length-1;i>=0;i--){
						var itemdom=items[i].getDom();
						//每次刷新的最后一个元素，添加loadafter标记，当滚动操作发现此标记，进行向后加载。
						if(i==items.length-1){
							itemdom.addClass("loadafter");
						}
						dom.prepend(itemdom);
					}
				}else{
					var targetdom=$("#stream_"+targetid);
					for(var i=items.length-1;i>=0;i--){
						var itemdom=items[i].getDom();
						if(i==items.length-1){
							itemdom.addClass("loadafter");
						}
						targetdom.after(itemdom);
					}
				}
				stream.bindEvent();
			}
			
			this.bindEvent=function(){
				var scrolltimeout=0;
				$(window).scroll(function(){
					//console.log("scrolling");
					if($(window).scrollTop()>$(".stream_header").height()){
						clearTimeout(scrolltimeout);
						$(".stream_header").hide();
						//$("#iwantbtn").hide();
						$(".stream_header").addClass("fixed");
						scrolltimeout=setTimeout(function(){
							$(".stream_header").show();
							//$("#iwantbtn").show();
						},800);
					
						//$(".stream_header").css("top",$(window).scrollTop());
					}else{
						$(".stream_header").removeClass("fixed");
						$(".stream_header").css("top",0);
						$(".stream_header").show();
					}
					$(".loadafter").each(function(){
						if($(this).hasClass("loading")) return;
						var top=parseInt($(this).offset().top);
						if($(window).scrollTop()>window.screen.height){
							//show goto top button
							$(".gototop").show();
						}else{
							$(".gototop").hide();
						}
						//当元素滚动到距离底部20的时候，启动加载
						if($(window).height()-$(window).scrollTop()-window.screen.height<200){
							$(this).addClass("loading");
							stream.loadAfter($(this).attr("id").split("_")[1]);
						}
					});
				});
				
				$("#pagecontent .weui_tab_bd").scroll(function(){
					$(".loadafter").each(function(){
						if($(this).hasClass("loading")) return;
						var top=parseInt($(this).offset().top);
						if($("#pagecontent .weui_tab_bd").scrollTop()>window.screen.height){
							//show goto top button
							$(".gototop").show();
						}else{
							$(".gototop").hide();
						}
						//当元素滚动到距离底部20的时候，启动加载
						if($("#pagecontent .weui_tab_bd").height()-$("#pagecontent .weui_tab_bd").scrollTop()-window.screen.height<100){
							$(this).addClass("loading");
							stream.loadAfter($(this).attr("id").split("_")[1]);
						}
					});
				});
				
				dom.find(".audioinfo").unbind("click").click(function(){
					var audio=$(this).find("audio");
					if(stream.currentAudio!=null){
						stream.currentAudio[0].pause();
						stream.currentAudio.parent().removeClass("play");
					}
					stream.currentAudio=audio;
					audio[0].play();
					$(this).addClass("play");
				});
				
				dom.find(".videoinfo").unbind("click").click(function(){
					$(this).find("img").hide();
					$(this).find(".videoiconmask").hide();
					var video=$(this).find("video");
					if(stream.currentVideo!=null&&stream.currentVideo.attr("src")!=video.attr("src")){
						stream.currentVideo[0].pause();
						stream.currentVideo.hide();
						stream.currentVideo.parent().find("img").show();
						stream.currentVideo.parent().find(".videoiconmask").show();
					}
					stream.currentVideo=video;
					video.show();
					video[0].play();
				});
				
				dom.find(".imageinfo").unbind("click").click(function(){
					var currentimg=$(this).find("img").attr("src");
					var images=[];
					dom.find(".imageinfo").each(function(){
						images.push($(this).find("img").attr("src"));
					});
					wx.previewImage({
					    current: currentimg, // 当前显示图片的http链接
					    urls: images // 需要预览的图片http链接列表
					});
				});
				
				//未加载出的内容，点击后重新加载
				dom.find(".needload").unbind("click").click(function(){
					$(this).find(".loadmask").text("正在加载中...");
					stream.reloadItem($(this).parent().attr("id").split("_")[1]);
				});
			}
			
			function formatTime(timestr){
				var time="";
				time=timestr.substring(0,4)+"年"+timestr.substring(4,6)+"月"+timestr.substring(6,8)+"日 "
					+timestr.substring(8,10)+":"+timestr.substring(10,12)+":"+timestr.substring(12,14);
				return time;
			}
			
			var streamItem=function(item){
				this.usericon=item.usericon;//用户头像
				this.username=item.nick;//用户昵称
				this.time=item.time;//
				this.type=item.contenttype;
				this.content=item.content;
				this.link=item.link;
				this.piclink=item.content_piclink;
				this.itemno=item.no;
				var _this=this;
				this.getDom=function(){
					var itemdiv=$("<div class='stream item' id='stream_"+_this.itemno+"' time='"+_this.time+"'></div>");
					var header=$("<div class='headericon'><img src="+_this.usericon+"/></div>");
					var nick=$("<div class='nick'>"+_this.username+"</div>");
					var time=$("<div class='time'>"+formatTime(_this.time)+"</div>");
					var itemcontent=$("<div class='itemcontent'></div>");
					itemcontent.append(nick);
					var clear=$("<div class='clear'></div>");
					if(_this.type=="text"){//文本消息dom
						itemcontent.append($("<div class='textinfo'>"+_this.content+"</div>"));
					}else if(_this.type=="image"){//图片消息dom
						itemcontent.append($("<div class='imageinfo media'><img src='"+_this.link+"'/></div>"));
					}else if(_this.type=="voice"){//语音消息dom
						itemcontent.append($("<div class='audioinfo media'><audio src='"+_this.link+"' style='display:none;'></audio></div>"));
					}else{//视频消息dom
						itemcontent.append($("<div class='videoinfo media'><img class='videoiconmask' src='onlive/mobile/image/play.png'/><img src='"+_this.piclink+"'/><video src='"+_this.link+"'></video></div>"));
					}
					if(_this.type!="text"&&(_this.link==null||_this.link=="")){//如果媒体消息没有下载完成，显示点击重新下载
						itemcontent.addClass("needload");
						itemcontent.append($("<div class='loadmask'>内容未下载完全，请点击重新加载内容</div>"));
						itemcontent.find(".media").hide();
					}
					//评论区
					var comment=$("<div class='comment'></div>");
					//点赞、评论按钮
					var btnop=$("<div class='btnop' streamno="+_this.itemno+"><span  class='commenticon icon'></span><span class='commentnum'></span><span class='praiseicon icon'></span><span class='praisenum'></span></div>");
					itemcontent.append(time).append(comment).append(btnop);
					var commentdom=$("<div class='commdiv'></div>");
					var commentpnl='<div class="commentpnl">'+
													'<input type="text" class="txtcomment" id="txtcomment_'+_this.itemno+'" placeholder="我也说两句"></textarea>'+
													'<a href="javascript:;" class="weui_btn commentbtn" streamno="'+_this.itemno+'">发送</a>'+
												'</div>';
					commentdom.append(commentpnl);
					itemdiv.append(header).append(itemcontent).append(clear).append(commentdom);
					return itemdiv;
				}
				
			}
			
			
			//如果开启自动加载，30秒加载一次
			if(this.autoload){
				stream.refresh();
				setInterval(function(){
					stream.refresh();
				},(1000*30));
			}else{
				stream.refresh();
			}
			return this;
		}
	});
})($,window);