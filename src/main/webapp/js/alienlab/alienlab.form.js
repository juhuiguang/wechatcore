/**
 * Alienlab studio 动态表单控件 version 1.0
 */

(function($) {
	$.fn.extend({
				dynamicForm:function() {
					// 拿到当前调用的选择器
					var $this = this;
					//表单dom
					var dom = '<div class="abf_parent_content">>';
					var item = '<div class="abf_item ui fields">';
					
					//将填写表单dom画到指定的选择器上
					this.renderForm=function(thisdom) {
						if ($.trim(dom) == '<div class="abf_parent_content">') {
							return;
						}
						thisdom.append(dom)
						dom = '<div class="abf_parent_content">';
					}
					//将展示dom画到指定的选择器上
					this.renderItem=function(thisdom) {
						if ($.trim(item) == '<div class="abf_item ui fields">') {
							return;
						}
						thisdom.append(item)
						domitem = '<div class="abf_item ui fields">';
					}
					
					 //根据传入的JSON生成表单，表单的json数据和是否自动将dom画到选择器上
					this.generateForm=function(config,renderflag) {
						var _config = {
							data:[],
							onSelect : function() {}
						};
						_config = $.extend(true,_config, config);
						if($.trim(dom)!='<div class="abf_parent_content">'){
							dom='<div class="abf_parent_content">';
						}
						for (var i = 0; i <_config.data.length; i++) {
							if(isJson(_config.data[i])){
								dom += '<div class="abf_content"><div class="abf_span_content" >'+ _config.data[i].name+ ':</div><input  class="abf_input_content recordfield" input_name="'+_config.data[i].name+'"  type="text" name="'+ _config.data[i].inputname + '"></div>';
							}
						}	
						dom+='</div>';
						// 判断renderflag如果是true,则自动将DOM画好
						if (renderflag) {  
							$this.renderForm($this)
							return ;
						}else{
							return dom;
						}
					}

					/**
					 * 根据传入的JSON生成查看的label
					 */
                    this.lookJSON=function(obj,flag){
                    	var _obj={
    							data:[],
    						};
                    	_obj = $.extend(true,_obj, obj);
                    	item='<div class="abf_item ">';
                    	for(var i =0;i<_obj.data.length;i++){
                    		if(isJson(_obj.data[i])){
                    			item+='<div class="abf_item_content ui field">'+
                    			'<div class="abf_span_second ui item" data-content="'+_obj.data[i].value+'" data-variation="mini"><span class="abf_span_first">'+_obj.data[i].name+':</span>'+_obj.data[i].value+'</div>'+
                    			'</div>';
                    		}
                    	}
                    	item+='</div>';
                    	if(flag){
                    		$this.renderItem($this)
                    	}
                    	return item;
                    }
                    
                	/**
					 * 判断传入的参数是否是JSON格式
					 */
                    var isJson = function(obj){
                    	var isjson = typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length; 
                    	return isjson;                	
                    }
					
					/**
					 * 根据传入的表单生成JSON
					 * 会自动找到带有类名recordfield的input
					 * 每一个这样的input上面定义一个input_name的属性就是存放在数据库中的这个字段的名称
					 */
					this.generateJson=function(domitem){
						var data=[];
						var item =  $(domitem).find('input');
						for(var i = 0;i<item.length;i++){
							if($(item[i]).hasClass('recordfield')){
								var o={
										name:"",
										value:""
								}
								var name=$(item[i]).attr('input_name');
								var value= $(item[i]).val();
								o.name=name;
								o.value=value;
								data[i]=o;
							}
						}
						return data;
					}
					return this;
				}
			});
})($);
