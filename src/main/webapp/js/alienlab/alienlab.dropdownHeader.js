/**
 * 一、二级选择组件
 * 
 */
(function($, window) {
	$.fn.extend({
		dropdownHeader:function(config){
			var $this=this;
			var containerid=new Date();
			if(config.id){
				containerid=config.id;
			}
			var _config={
				dom:$('<div tabindex="0" class="dph_container" id="'+containerid+'"></div>'),
				firstDom:$('<ul class="dph_first_div"></ul>'),
				secDom:$('<ul class="dph_second_div"></ul>'),
				closeOnSelect:true,
				idfield:"item_id",
				namefield:"item_name",
				childrenfield:"children",
				defaultitem:{
					item_id:"",
					item_name:""
				},
				data:[
					{
						item_id:"",
						item_name:"",
						selected:false,
						children:[]
					}
				],
				onSelect:function(){}
			};
			_config=$.extend(true,_config,config);
			var selItem=null;
			//插件初始化
			var init=function(){
				//组装dom
				_config.dom.append(_config.firstDom).append(_config.secDom);
				for(var i=0;i<_config.data.length;i++){
					var itemdata=_config.data[i];
					var firstitem=$('<li class="dph_first_div_item" itemid="'+itemdata[_config.idfield]+'" id="'+containerid+'_dfi_item_'+itemdata[_config.idfield]+'">'+itemdata[_config.namefield]+'</li>');
					if(itemdata.selected){
						firstitem.addClass("selected");
					}
					_config.firstDom.append(firstitem);
					if(itemdata[_config.childrenfield]&&itemdata[_config.childrenfield].length>0){
						for(var j=0;j<itemdata[_config.childrenfield].length;j++){
							var subitemdata=itemdata[_config.childrenfield][j];
							var subitem=$('<li class="dph_second_div_item sub_'+itemdata[_config.idfield]+'" itempid="'+itemdata[_config.idfield]+'" itemid="'+subitemdata[_config.idfield]+'" id="'+containerid+'_dfi_item_'+subitemdata[_config.idfield]+'"><a>'+subitemdata[_config.namefield]+'</a></li>');
							if(subitemdata.selected){
								subitem.addClass("selected");
							}
							_config.secDom.append(subitem);
						}
						_config.secDom.append($('<div style="clear:both;"></div>'));
					}
				}
				_config.firstDom.append($('<div style="clear:both;"></div>'));
				//加载到dom中。
				if($("#"+containerid).length>0){
					$("#"+containerid).remove();
				}
				$("body").append(_config.dom);
				
				//选中默认项
				if(_config.defaultitem&&_config.defaultitem[_config.idfield]&&_config.defaultitem[_config.idfield]!=""){
					$this.setSelect(_config.defaultitem[_config.idfield]);
				}else{
					//默认选中第一个
					if(_config.data.length>0){
						$this.setSelect(_config.data[0][_config.idfield]);
					}
				}
				//如果只有一个一级，自动隐藏一级，只显示二级
				if($("#"+containerid+" .dph_first_div_item").length==1){
					$("#"+containerid+" .dph_first_div_item").hide();
				}
				//一级菜单选中
				$("#"+containerid+" .dph_first_div_item").click(function(){
					var item_id=$(this).attr("itemid");
					var item=$this.findItem(item_id);
					selectFirstItem(item,$(this));
				});
				
				//二级菜单选中
				$("#"+containerid+" .dph_second_div_item").click(function(){
					var item_id=$(this).attr("itemid");
					var item=$this.findItem(item_id);
					selectSecItem(item,$(this));
				});
				
				$("#"+containerid).blur(function(){
					$this.close();
				});
			}
			
			//一层菜单点击
			function selectFirstItem(fitem,dom){
				$("#"+containerid+" .dph_first_div_item.selected").removeClass("selected");
				dom.addClass("selected");
				//隐藏已有子菜单
				$("#"+containerid+" .dph_second_div_item").hide();
				if(fitem[_config.childrenfield]&&fitem[_config.childrenfield].length>0){
					//显示子菜单
					$("#"+containerid+" .sub_"+fitem[_config.idfield]).show();
				}else{
					selItem=fitem;
					if(_config.closeOnSelect){
						$this.close();
					}
					//没有子菜单，触发选择事件
					_config.onSelect(fitem,dom);
					
				}
			}
			
			function selectSecItem(sitem,dom){
				selectFirstItem($this.findItem(dom.attr("itempid")),$("#"+containerid+"_dfi_item_"+dom.attr("itempid")));
				$("#"+containerid+" .dph_second_div_item.selected").removeClass("selected");
				//选中2级
				dom.addClass("selected");
				selItem=sitem;
				if(_config.closeOnSelect){
					$this.close();
				}
				_config.onSelect(sitem,dom);
			}
			
			
			this.findItem=function(item_id){
				for(var i=0;i<_config.data.length;i++){
					if(_config.data[i][_config.idfield]==item_id){
						return _config.data[i];
					}else{
						if(_config.data[i][_config.childrenfield]&&_config.data[i][_config.childrenfield].length>0){
							for(var j=0;j<_config.data[i][_config.childrenfield].length;j++){
								if(_config.data[i][_config.childrenfield][j][_config.idfield]==item_id){
									return _config.data[i][_config.childrenfield][j];
								}
							}
						}
					}
				}
				return null;
			}
			
			//显示下拉
			this.dropdown=function(dom,onselect){
				var x=$(dom).offset().left;
				var y=$(dom).offset().top+$(dom).height()+10;
				//_config.dom.css("display","block");
				if(x==null) x=300;
				if(y==null) y=100;
				if(y<0){
					y=10;
				}
				if(y+_config.dom.height()>$(window).height()){
					y=$(window).height()-_config.dom.height()-10;
				}
				if(x<0){
					x=10;
				}
				if(x+_config.dom.width()>$(window).width()){
					x=$(window).width()-_config.dom.width()-10;
				}
				_config.dom.css({
					top:y,
					left:x
				})
				_config.dom.slideDown(300,null,function(){
					$("#"+containerid).focus();
				});
				if(onselect){
					_config.onSelect=onselect;
				}
			}
			
			this.close=function(){
				_config.dom.fadeOut();
			}
			
			//获得当前选择项
			this.getSelected=function(){
				return selItem;
			}
			
			//设置选择项
			this.setSelect=function(item_id){
				var item=$this.findItem(item_id);
				var dom=$("#"+containerid+"_dfi_item_"+item[_config.idfield]);
				if(dom.hasClass("dph_second_div_item")){
					selectSecItem(item,dom);
				}else{
					selectFirstItem(item,dom);
				}
			}
			
			init();
			return this;
		}
	});
		
})($,window);
