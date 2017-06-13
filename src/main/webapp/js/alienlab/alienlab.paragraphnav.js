/**
 * Alienlab studio
 * 段落内容导航
 * version 1.0
 */
(function($) {
	$.fn.extend({
		paragraphnav:function(config){
			console.log(window);
			var $this=this;
			/**
			 * 初始化参数
			 * {
			 * 	 data:[{title:"导航标题",id:"段落id",children:[{title:"子导航标题",id:"子段落id"}]}],
			 * 	navdiv:$(this),//导航容器，默认当前选中元素
			 * containerdiv:"paragraph",//段落内容容器id
			 * }
			 */
			var _config={
				data:[],
				navdiv:$(this),
				containerdiv:""
			}
			_config = $.extend(true, _config, config);
			
			var level=["first","second","third","fourth","fifth","sixth","seventh","eighth"];
			
			var navdom=$("<div id='alpnav'></div>");
			this.empty();
			//渲染导航
			renderNav(_config.data,0);
			//将导航添加到容器
			this.append(navdom);
			//绑定段落容器滚动事件
			$("#"+_config.containerdiv).scroll(function(){
				$(this).find(".paragraph").each(function(){
					//接近顶部的元素激活
					if(parseInt($(this).offset().top)>0&&parseInt($(this).offset().top)<100){
						setActive($(this));
					}
					//超出顶部的元素，激活下一个
					if(parseInt($(this).offset().top)<-$(this).height()/2 && parseInt($(this).offset().top) > -$(this).height()/2 ){
						setActive($(this).next());
					}
				});
			});
			
			//绑定导航点击事件
			$("#alpnav .item").click(function(){
				setActive($($(this).attr("href")));
			});
			
			//设置导航选中
			function setActive(item){
				$("#alpnav .active").removeClass("active");
				$("#alpnav .item[href='#"+item.attr("id")+"']").addClass("active");
			}
			
			//递归渲染导航
			function renderNav(data,lev){
				if(data&&data.length>0){
					for(var i=0;i<data.length;i++){
						var item=data[i];
						var navitem=$('<a class="item " href="#'+item.id+'">'+item.title+'</a>');
						navitem.addClass(level[lev]);
						navdom.append(navitem);
						$("#"+item.id).removeClass("paragraph").addClass("paragraph");
						if(item.children&&item.children.length>0){
							renderNav(item.children,(lev+1));
						}
					}
				}
			}
			return this;
		}
	});
})($);