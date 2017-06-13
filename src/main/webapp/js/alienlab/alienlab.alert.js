(function($){
	$.extend({
		"alert" : function(type, msg, btn, callback){
			var object = {
				msg  : '提示信息',
				type : 'info',
				btnText : '确&nbsp;&nbsp;定',
				callback: function(){}
			};
			if($.isPlainObject(type)){
				object = $.extend({},object,type);
			}else if(arguments.length == 1 && $.type(type) === "string"){
				object.msg = type;
			}else if(arguments.length == 2 && $.type(type) === "string" && $.isFunction(msg)){
				object.msg = type;
				object.callback = msg;
			}else{
				object.type = type || object.type;
				object.msg = msg || object.msg;
				object.btnText = btn || object.btnText;
				object.callback = callback;
			}
			var dom = $('<div class="iBalert" style="display:none;">' +
				'<div class="mask"></div>' +
				'<div class="dialog">'  +
					'<div class="content">' + object.msg + '</div>' +
					'<div class="buttons">' +
						'<div class="btn sure">'+ object.btnText + '</div>' +
					'</div>' +
				'</div>' +
			'</div>');
			if($(".iBalert") && $(".iBalert").length > 0){
				$(".iBalert").hide().remove();
			}
			$("body").append(dom);
			
			if($("body").width() > 420){
				dom.addClass("max");
			}else{
				dom.removeClass("max");
			}
			//dom.css("top",($(window).height() - dom.height())/2 + $(window).scrollTop()+"px").show();
			//dom.css("top",($(window).height() - dom.height())/2 +"px").fadeIn(500);
			dom.animate({top:($(window).height() - dom.height())/2 +"px"},500).fadeIn(100);
			$(".sure",dom).unbind("click").click(function(){
				dom.hide().remove();
				object.callback && $.isFunction(object.callback) ? object.callback() : "";
			});
		},
		"confirm" : function(title,msg,sureDo,cancelDo){
			var object = {
				msg  : '提示信息',
				title: '操作确定',
				btnSure   : '确定',
				btnCancel : '取消',
				sureDo	  : function(){},
				cancelDo  : function(){}
			};
			if($.isPlainObject(title)){
				object = $.extend({},object,title);
			}else if(arguments.length == 1){
				object.msg = title;
			}else{
				object.title = title;
				object.msg = msg;
				object.sureDo = sureDo;
				object.cancelDo = cancelDo;
			}
			var dom = $('<div class="iBconfirm" style="display:none;">'+
				'<div class="mask"></div>' +
				'<div class="dialog">'  +
					'<div class="header"></div>'+
					'<div class="content">'+ object.msg + '</div>'+
					'<div class="buttons">'+
						'<div class="btn sure">' + object.btnSure + '</div>'+
						'<div class="btn cancel">' + object.btnCancel + '</div>'+
					'</div>'+
				'</div>'+
			'</div>');
			if($(".iBconfirm").length > 0){
				$(".iBconfirm").hide().remove();
			}
			$("body").append(dom);
			
			if($("body").width() > 420){
				dom.addClass("max");
			}else{
				dom.removeClass("max");
			}
			dom.animate({top:($(window).height() - dom.height())/2 +"px"},500).fadeIn(100);
			//dom.css("top",($(window).height() - dom.height())/2 +"px").fadeIn(500);
			$(".sure",dom).unbind("click").bind("click",function(){
				dom.hide().remove();
				object.sureDo && $.isFunction(object.sureDo) ? object.sureDo() : "";
			});
			$(".cancel",dom).unbind("click").bind("click",function(){
				dom.hide().remove();
				object.cancelDo && $.isFunction(object.cancelDo) ? object.cancelDo() : "";
			});
		}
	});
})(jQuery);
/*window.alert = function(type, msg, btn, callback){
	//$.alert.apply(this, arguments);
	$.alert.call(this,type, msg, btn, callback);
};
window.confirm = function(){
	//$.confirm.apply(this, arguments);
	$.confirm.call(this, title,msg,sureDo,cancelDo);
};*/