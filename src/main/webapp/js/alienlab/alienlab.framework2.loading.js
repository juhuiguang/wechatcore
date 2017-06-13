(function($, window) {
	$.alienlab.loading=function(){
		//设置全屏遮罩
		var mask=$('<div class="alloading">系统加载中...请耐心等待...</div>');
		$("body").append(mask);
		$(".alloading").width($(window).width());
		$(".alloading").height($(window).height());
		$(".alloading").css("line-height",$(window).height()+"px");
		var wholeprocess=0;
		var ajaxs=[];
		var $this=this;
		this.setWholeProcess=function(number){
			wholeprocess=number;
			$(".alloading").css("display","block");
			if(number>=20){
				$(".alloading").stop().animate({opacity:0.8-number*0.01},200,null,function(){
					if(number==100){
						setTimeout(function(){$(".alloading").stop().fadeOut();},200);
					}
				});
			}
			
		}
		this.getWholeProcess=function(){
			return wholeprocess;
		}
	};
	$.fn.extend({
		alloading:function(){
			var $this=this;
			var loadingdom=$('<div class="ibMask" style="z-index: 10000;">'+
													'<div class="sk-spinner sk-spinner-wave ibLoader">'+
												'<div class="sk-rect1"></div>'+
												'<div class="sk-rect2"></div>'+
												'<div class="sk-rect3"></div>'+
												'<div class="sk-rect4"></div>'+
												'<div class="sk-rect5"></div>'+
											'</div>'+
										'</div>');
			this.loading=function(){
				$this.append(loadingdom);
			}
			this.unloading=function(){
				$this.find(".ibMask").remove();
			}
			return this;
		}
	});
	
	
})($,window);
$(function(){
	var isloading=true;
	var loading=new $.alienlab.loading();
	loading.setWholeProcess(10);
	setTimeout(function(){
		loading.setWholeProcess(75);
	},400)
	var interval=setInterval(function(){
		var p=loading.getWholeProcess();
		if(p>90){
			clearInterval(interval);
			isloading=false;
		}else{
			loading.setWholeProcess(p+10);
		}
		if(loading.getWholeProcess()>90){
			clearInterval(interval);
			isloading=false;
		}
	},200);
	$(window).load(function(){
		var interval=setInterval(function(){
			if(!isloading){
				loading.setWholeProcess(100);
				clearInterval(interval);
			}
		},100);
	});
});