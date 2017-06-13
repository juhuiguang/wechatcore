/**
 * Alienlab studio 主框架js version 1.0
 */
(function($, window) {
	console.log("system start");
	$.alienlab = {
		modulemanage_ : {},
		masbox_ : {},
		framework : function(config) {
			// 界面传参存储变量
			// 默认参数值
			var _config = {
				systeminfo : {

				},
				user : {
					userFlag : true,
					loginname : "",
					username : "",
					usericon : "",
					userpurview : ""
				},
				msgbox : {
					msgFlag : true,
					allmsg : [],
					newmsg : []
				},
				modulemanage : {
					modules : [],
					currentModule : [],
					historyModules : []
				}
			}
			_config = $.extend(true, _config, config);
			var $fw = this;
			// 定义对象区---------------------------------------
			// 界面传参数存储变量
			var appdata = {}
			var framedom = {
				left : $("<div id='leftmenu'><div>"),
				top : $("<div id='topmenu'><div id='toptitle' ></div><ul id='ddbtn'></ul><div id='submenu'></div><div>"),
				content : $("<div id='maincontent'><div>"),
				cover : $('<div class="coverDiv"></div>')
			}
			
			var msgbox = function(msglistinfo) {
				var msglist = {
					msgFlag : true,
					allmsg : [],
					newmsg : []
				}
				msglist = $.extend(true, msglist, msglistinfo);
				this.msgFlag = msglist.msgFlag;
				this.length = msglist.allmsg.length;
				this.msglist = msglist.allmsg;
				this.newlength = msglist.newmsg.length;
				this.newlist = msglist.newmsg;
				this.addmsg = function(message) {

				}
				this.render = function() {
					return dom;
				}
				this.showMsg = function(msgcode) {
					dom = message.render();
					// 展示消息盒子
				}
			}
			
			var modulemanage = function(modulemanageinfo) {
				var _modulemanageinfo = {
					modules : [],
					currentModule : [],
					historyModules : []
				}
				_modulemanageinfo = $.extend(true, _modulemanageinfo,
						modulemanageinfo);
				//实时记录当前content打开的module.
				this.contentModule=null;
				this.modules=[];
				for(var i=0;i<_modulemanageinfo.modules.length;i++){
					var module=new $.alienlab.module(_modulemanageinfo.modules[i]);
					var submodules=_modulemanageinfo.modules[i].subModule;
					if(submodules!=null && submodules.length>0){
						var subarr=[];
						for(var j=0;j<submodules.length;j++){
							var submodule=new $.alienlab.module(submodules[j]);
							if(submodule.pid==null||submodule.pid==""){
								submodule.pid=module.id;
							}
							subarr.push(submodule);
							this.modules.push(submodule);
						}
						module.subModule=subarr;
					}
					this.modules.push(module);
				}
				this.currentModule = _modulemanageinfo.currentModule;
				this.historyModules = _modulemanageinfo.historyModules;
				// 给每个module添加code
				var $fm = this;
				var getMaxZ_index = function() {
					var z_index = 1000;
					for (var i = 0; i < $fm.currentModule.length; i++) {
						var div_index = parseInt($(
								'#contentDiv'
										+ $fm.currentModule[i].code
										+ '').css('z-index'))
						if (div_index > z_index) {
							z_index = div_index;
						}
					}
					return z_index;
				}
				
				// 隐藏相邻div
				this.hideNearModule = function(module, callback) {
					$('#contentDiv' + module.code + '').css('z-index',
							getMaxZ_index() + 1);
					$('#leftmenu').css('z-index', 32766);
					var currentModules = $fm.currentModule;  
					var removeModues = [];
					// 没有蒙版的出现 关闭所有相邻的 和有蒙版的
					if (module.allowMuilty) {
						if (module.rendertype == "right"
								|| module.rendertype == "left") {
							for (var i = 0; i < currentModules.length; i++) {
								if (currentModules[i].rendertype == "top"
										|| currentModules[i].rendertype == "bottom"
										|| !currentModules[i].allowMuilty) {
									removeModues.push(currentModules[i]);
									currentModules[i].closeModule(callback,true);
									callback = null;
								}
							}
						} else if (module.rendertype == "top"
								|| module.rendertype == "bottom") {
							for (var i = 0; i < currentModules.length; i++) {
								if (currentModules[i].rendertype == "left"
										|| currentModules[i].rendertype == "right"
										|| !currentModules[i].allowMuilty) {
									removeModues.push(currentModules[i]);
									currentModules[i].closeModule(callback,true);
									callback = null;
								}
							}
						} else {
							console.log(currentModules) 
							for (var i = 0; i < currentModules.length; i++) {
								if (module.code != currentModules[i].code) {
									removeModues.push(currentModules[i]);
									currentModules[i].closeModule(callback,true);
									callback = null;
								}

							}
						}
					} else { 
						// 点击有蒙版的 隐藏所有
						for (var i = 0; i < currentModules.length; i++) {
							if (module.code != currentModules[i].code) {
								removeModues.push(currentModules[i]);
								currentModules[i].closeModule(callback,true); 
								callback = null;
							} 

						}
					}
					
					if (callback != null) {
						callback();
					}
					// 从currentModules中删除Modules
					for (var i = 0; i < removeModues.length; i++) {
						$fm.removeCurrentModule(removeModues[i]);
					}
					// 从currentModules中添加Module 
					$fm.currentModule.push(module);
					// 添加到historyModule中
					$fm.historyModules.push(module);
				}
				
				// 根据code得到Module
				this.getModule = function(code) {
					for (var i = 0; i < $fm.modules.length; i++) {
						if ($fm.modules[i].code == code) {
							return $fm.modules[i];
						}
					}
					return null;
				}
				// 添加module到modules
				this.addModule = function(module) {
					// 给新增加的Module添加code
					if ($fm.modules.length > 0) {
						module.code = "_tmp_"+parseInt($fm.modules.length+ 1);
						module.id=module.code;
					} else {
						module.code = 0;
					}
					console.log('add module' + module.code)
					$fm.modules.push(module);
					return module;
				}

				// 删除Module
				this.removeModule = function(module) {
					for (var i = 0; i < $fm.modules.length; i++) {
						if ($fm.modules[i].src.trim() == module.src
								&& $fm.modules[i].code == module.code) {
							$('#contentDiv' + $fm.modules[i].code + '')
									.remove();
							$fm.modules.splice(i, 1);
							break;
						}
					}
				}
				// 得到所有正在运行的module
				this.getCurrentModule = function() {
					return $fm.currentModule;
				}
				// 得到历史访问module
				this.getHistoryModule = function() {
					return $fm.historyModules;
				}
				// 关闭Module后从currentmodule删除
				this.removeCurrentModule = function(module) {
					for (var i = 0; i < $fm.currentModule.length; i++) {
						if ($fm.currentModule[i].code == module.code) {
							$fm.currentModule.splice(i, 1);
							console.log('remove currentModues' + module.code)
						}
					}
				}

				// 是否存在相同src的module
				this.hasSameSrc = function(module) {
					if (module.src == "") {
						return false;
					}
					console.log($fm.modules);
					for (var i = 0; i < $fm.modules.length; i++) {
						if ($fm.modules[i].code == module.code || $fm.modules[i].src==module.src) {
							return true;
						}
					}
					return false;
				}
				// 界面得到自己Module
				this.getOwnModule = function(src,code) {
					for (var i = 0; i < $fm.modules.length; i++) {
						if(code==null){
							if ($fm.modules[i].src.trim() == src) {
								return $fm.modules[i];
							}
						}else{
							console.log("code>>>",$fm.modules[i].code,code);
							if ($fm.modules[i].src.trim() == src && $fm.modules[i].code==code) {
								return $fm.modules[i];
							}
						}
						
					}
					return null;
				}
				
			}
			
			//开放获取module方法
			this.getOwnModule=function(src,code){
				return modulemanage_.getOwnModule(src,code);
			}
			//开放获取module方法
			this.getModule=function(code){
				return modulemanage_.getModule(code);
			}
			
			// -------------------------------------------------
			var _init = function() {
				$("body").append(framedom.left);
				$("body").append(framedom.cover)
				$("body").append(framedom.top);
				$("body").append(framedom.content);

				$('#topmenu').css({
					'width' : $(window).width() - $('#leftmenu').width(),
					'margin-left' : $('#leftmenu').width(),
				});
				$('#maincontent').css({
					'width' : $(window).width() - $('#leftmenu').width(),
					'height' : $(window).height() - $('#topmenu').height(),
					'margin-left' : $('#leftmenu').width()
				});
				
				$(window).resize(function(){
					$('#topmenu').css({
						'width' : $(window).width() - $('#leftmenu').width(),
						'margin-left' : $('#leftmenu').width(),
					});
					$('#maincontent').css({
						'width' : $(window).width() - $('#leftmenu').width(),
						'height' : $(window).height() - $('#topmenu').height(),
						'margin-left' : $('#leftmenu').width()
					});
				});
			}
			_init();
			//获得当前content运行的module
			this.getContentModule=function(){
				return modulemanage_.contentModule;
			}
			
			//打开指定的module
			this.openDefaultModule=function(src,code){
				var module=modulemanage_.getOwnModule(src,code);
				module.openModule();
			}
			// 得到初始模型
			this.user_ = new $.alienlab.user(_config.user);
			
			msgbox_ = new msgbox(_config.msgbox);
			modulemanage_ = new modulemanage(_config.modulemanage);
			//程序启动默认出发模块点击
			setTimeout(function(){
				//如果参数指定了默认地址，则自动打开默认地址
				if(_config.defaultSrc&&_config.defaultSrc!=""){
					var defaultModule=modulemanage_.getOwnModule(_config.defaultSrc);
					console.log("defaultModule>>>>",defaultModule);
					if(defaultModule!=null){
						if(defaultModule.rendertype=="content"){
							if(defaultModule.type=="submodule"){//如果是二级菜单
								$("#menu"+defaultModule.pid).trigger("click");
								if(!$("#menu"+defaultModule.id).hasClass("selected")){
									$("#menu"+defaultModule.id).trigger("click");
								}
							}else{
								$("#menu"+defaultModule.id).trigger("click");
							}
						}else {
							$('.moduleitem:first').trigger("click");
						}
					}else{
						$('.moduleitem:first').trigger("click");
					}
				}else{
					$('.moduleitem:first').trigger("click");
				}
				
				
			},500);
			// 菜单点击事件
			var menu_item_click = function() {
				$('.moduleitem').unbind('click').click(function() {
					var selectCode = $(this).attr('code');
					var selectModule = modulemanage_.getModule(selectCode);
					//$fw.setTitle(selectModule.name);
					if(selectModule.subModule&&selectModule.subModule.length>0){
						//渲染子菜单
						$fw.renderSubmodules(selectModule);
						selectModule.subModule[0].openModule();
						$fw.setTitle(selectModule.subModule[0].name);
					}else{
						if (selectModule == null) {
							console.log("selectModule == null"); 
							return;
						}
						if ($(this).hasClass("selected")) {
							if (selectModule.rendertype == 'content') {
								selectModule.reloadModule();
								return;
							}
							$(this).removeClass("selected");
							selectModule.closeModule();
						} else {
							selectModule.openModule();
							$fw.setTitle(selectModule.name);
						}
					}
					
				});
			}
			// 用户头像点击事件
			var user_icon_click = function(user) {
				if(user.link){
					var userModule=new $.alienlab.module({
						name: '欢迎使用',
						icon: '',
						src: user.link,
						rendertype: 'left',
						disposeClose: true,
						allowMuilty: true
					});
					userModule.openModule();
				}
			}
			// 消息盒子点击事件
			var msgbox_click = function() {
				if ($fw.msgFlag) {

				}
			}

			// 蒙版点击事件
			$('.coverDiv').unbind('click').click(function() {
				modulemanage_.getModule($(this).attr('code')).closeModule();
			})
			var renderUser = function(user) { // 画出人物
				if (user.userFlag) {
					var dom = '<div class="left_menu_item" ><div id = "left_menu_user">' + user.usericon
							+ '</div></div>';
					$('#leftmenu').append($(dom));
					$("#left_menu_user").click(function(){
						user_icon_click(user);
					});
				}
			}
			var renderMsgbox = function(msgbox) { // 画出消息盒子
				if (msgbox.msgFlag == true) { // 判断消息盒子是否要存在
					var message_newlength = msgbox.newlength; // 获取最新的消息的长度
					var dom = '<div class="left_menu_item"><div id="left_menu_msgbox"><i class=" inverted   big mail outline icon"></i></div></div>';
					$('#leftmenu').append($(dom));
				}
			}
			// 绘制左侧菜单
			var renderModules = function(modulelist) {
				var modulelist_len = modulelist.length;
				var dom = '<ul id="menulist"></ul>';
				$('#leftmenu').append($(dom));
				var height = $(window).height() - 300; // 动态计算剩余的子菜单剩余的值
				var modulenumber = parseInt(height / 80);
				if (modulelist_len > modulenumber) { // 限制一排只显示五个菜单
					for (var i = 0; i < modulenumber; i++) {
						var module = modulelist[i]; // 取到所有的菜单
						$('#menulist').append($(module.getModuleDom()));
					}
					$('#menulist')
							.append(
									$('<li id="more_menu_item_btn" class="left_menu_item"><div class="menuicon"  data-variation="inverted"><i class=" circular inverted large  gray ellipsis horizontal icon"></i></div><div class="menuname white">更多</div></li>'));
					var dom_ = '<div tabindex="0" id="menu_more"  class=" ui vertical pointing menu">';
					for (var i = modulenumber; i < modulelist_len; i++) {
						var module = modulelist[i];
						dom_ += '<a class=" moduleitem  attach_menu item" id="menu'
								+ module.code
								+ '" code="'
								+ module.code
								+ '">'
								+ module.name.substring(0, 8) + '</a>'; // 剩余的菜单
					}
					dom_ += '</div>';
					$('#more_menu_item_btn').append($(dom_));
					// 计算menu_more位置
					if (parseInt($('#menu_more').height() / 3 * 2) > 150) {
						$('#menu_more').css({
							'bottom' : "-120px",
						})
					} else {
						$('#menu_more').css({
							'top' : -$('#menu_more').height() / 3,
						})
					}

					$('#menu_more').hide().blur(function(){
						$(this).hide();
					});

					$('#more_menu_item_btn').unbind('click').click(function() {
						if($('#menu_more').is(":hidden")){
							$('#menu_more').show().focus();
						}else{
							$('#menu_more').hide()
						}
					});
				} else {
					for (var i = 0; i < modulelist_len; i++) {
						var module = modulelist[i]; // 取到所有的子菜单
						$('#menulist').append($(module.getModuleDom()));
					}
				}
				menu_item_click();
			}

			renderUser(this.user_);
			renderMsgbox(msgbox_);
			var rendModules=[];
			for(var i=0;i<modulemanage_.modules.length;i++){
				if(modulemanage_.modules[i].type=="module"){
					rendModules.push(modulemanage_.modules[i]);
				}
			}
			renderModules(rendModules);
			this.setTitle=function(title,titleclick,titlecss){
				$("#toptitle").empty();
				$("#toptitle").html(title);
				if(titlecss){
					$("#toptitle").css(titlecss);
				}
				if(titleclick){
					$("#toptitle").unbind("click").click(function(e){
						titleclick(e);
					});
				}
			}
			
			/**
			 * 创建顶部下拉项
			 * @param text 下拉项名称
			 * @param onclick 下拉项点击
			 * @param issystem 是否系统级下拉项
			 */
			this.setDropDown=function(text,onclick,issystem){
				var flag="temp";
				if(issystem) flag="system";
				var ddItem=$("<li class='dditem "+flag+"'><span class='ddtext'>"+text+"</span> <i class='dropdown icon'></i></li>");
				if(onclick){
					ddItem.click(function(e){
						if(onclick){
							onclick($(this).find(".ddtext"),e);
						}
					});
				}else{//如果没有指定点击事件，就删除下拉箭头。
					ddItem.find(".dropdown").remove();
				}
				
				$("#ddbtn").append(ddItem);
			}
			
			// 副菜单render
			this.renderSubmodules = function(module) {
				console.log(module);
				$("#submenu").empty();
				if(module.subModule.length>0){
					for(var i=0;i<module.subModule.length;i++){
						var item=$('<a id="menu'+module.subModule[i].code+'" code="'+module.subModule[i].code+'" class="item" link="'+module.subModule[i].src+'">'+module.subModule[i].name+'</a>');
						$("#submenu").append(item);
					}
				}
				$("#submenu .item").unbind("click").click(function(){
					var link=$(this).attr("link");
					var code=$(this).attr("code");
					var module=modulemanage_.getOwnModule(link,code);
					console.log('+++++++++++++');
					console.log(module) ;
					console.log(link,module);
					module.openModule();
					$fw.setTitle(module.name);
				});
			}

			// 传入参数
			this.setAppdata = function(app, key, value) {
				if (!appdata[app]) {
					appdata[app] = {};
				}
				appdata[app][key] = value;
			}

			// 得到参数
			this.getAppdata = function(app, key) {
				if(appdata[app]!=null){
					return appdata[app][key];
				}else{
					return null;
				}
			}
		},
		user : function(userinfo) {
			var user = {
				userFlag : true,
				loginname : "",
				username : "",
				usericon : "",
				userpurview : "",
				link:"user.html"
			}

			user = $.extend(true, user, userinfo);
			this.userFlag = user.userFlag;
			this.loginname = user.loginname;
			this.username = user.username;
			this.usericon = user.usericon;
			this.userpurview = user.userpurview;
			this.link=user.link;
			var $user=this;
			
		},

		message : function(messageinfo) {
			var msginfo = {
				msgcode : "",
				msgtype : "",
				msgcontent : "",
				msgsrc : "",
				msgflag : "",
				msgtime : "",
				msgfrom : ""
			}
			msginfo = $.extend(true, msginfo, messageinfo);

			this.msgcode = msginfo.msgcode;
			this.msgtype = msginfo.msgtype;
			this.msgcontent = msginfo.msgcontent;
			this.msgsrc = msginfo.msgsrc;
			this.msgflag = msginfo.msgflag;
			this.msgtime = msginfo.msgtime;
			this.msgfrom = msginfo.msgfrom;

			this.render = function() {
				return false;
			}
		},
		module : function(moduleinfo) {
			var _moduleinfo = {
				id:"",
				pid:"",
				name : "",
				icon : "",
				src : "",
				type:"module",
				rendertype : "content",
				disposeClose : true, // 关闭时候是否销毁
				allowMuilty : false
			}
			_moduleinfo = $.extend(true, _moduleinfo, moduleinfo);
			var $this = this;
			this.code = _moduleinfo.id;
			this.id=_moduleinfo.id;
			this.pid=_moduleinfo.pid;
			this.type=_moduleinfo.type;
			this.name = _moduleinfo.name;
			this.icon = _moduleinfo.icon;
			this.src = _moduleinfo.src;
			this.rendertype = _moduleinfo.rendertype;
			this.disposeClose = _moduleinfo.disposeClose;
			this.subModule = _moduleinfo.subModule;
			this.allowMuilty = _moduleinfo.allowMuilty; 
			
			this.isActive=function(){
				var curmod=modulemanage_.getCurrentModule();
				if(curmod==null || curmod.length==0){
					return false;
				}else{
					for(var i=0;i<curmod.length;i++){
						if($this.code==curmod[i].code){
							return true;
						}
					}
					return false;
				}
			}
			
			this.getModuleDom = function() {
				var dom = '<li class="left_menu_item moduleitem main_menu"  id = "menu'
						+ $this.code + '" code = "' + $this.code + '">'
						+ '<div class="menuicon">' + $this.icon + '</div>'
						+ '<div class="menuname white">' + $this.name.substring(0, 4)
						+ '</div>' + '</li>';
				return dom;
			}
			this.render = function() {
				if ($this.rendertype == 'content') {
					$('#maincontent').empty();
					return;
				} else {
					// manage中不存在此 module ， 添加 并 分配code
					if (($this.code==null||$this.code=="")||modulemanage_.getModule($this.code)==null) {
						console.log('this code is ' + $this.code)
						$this=modulemanage_.addModule($this);
						console.log('set code ' + $this.code)
					}
					var dom = '<div code="'
							+ $this.code
							+ '" id="contentDiv'
							+ $this.code
							+ '" class="contentDiv '
							+ $this.rendertype
							+ '"><div class="contentDivManage"><label class="moduleName">'
							+ $this.name
							+ '</label><i class="icon large repeat"></i><i class="icon large remove"></i></div><div class="htmlcontent"></div></div>';
					$('body').append($(dom));
					var $div = $('#contentDiv' + $this.code + '')
					if ($this.rendertype == 'right') {
						$div.css({
							'height' : '100%',
							'width' : ($(window).width() - $("#leftmenu")
									.width()) * 0.5,
							'right' : -($(window).width() - $("#leftmenu")
									.width()) * 0.5,
							'top' : '0',
							'border-left' : '1px solid #eeece8'
						});
						$div.find(".htmlcontent").css({
							height:$(window).height()-40
						});
					} else if ($this.rendertype == 'top') {
						$div.css({
							'height' : $(window).height() * 0.8,
							'width' : $(window).width()
									- $("#leftmenu").width(),
							'top' : -$(window).height() * 0.8,
							'right' : '0',
							'border-bottom' : '1px solid #eeece8'
						});
						$div.find(".htmlcontent").css({
							height:$(window).height()*0.8-40
						});
					} else if ($this.rendertype == 'bottom') {
						$div.css({
							'height' : $(window).height() * 0.312,
							'width' : $(window).width()
									- $("#leftmenu").width(),
							'bottom' : -$(window).height() * 0.312,
							'right' : '0',
							'border-top' : '1px solid #eeece8'
						});
						$div.find(".htmlcontent").css({
							height:$(window).height()*0.312-40
						});
					} else {
						// 其他情况都从left出来
						$div.css({
							'height' : '100%',
							'width' : ($(window).width() - $("#leftmenu")
									.width()) * 0.312,
							'left' : -($(window).width() - $("#leftmenu")
									.width()) * 0.312,
							'top' : '0',
							'border-right' : '1px solid #eeece8'
						});
						$div.find(".htmlcontent").css({
							height:$(window).height()-40
						});
					}
					$('.contentDiv .contentDivManage .repeat').unbind('click')
							.click(
									function() {
										modulemanage_.getModule(
												$(this).parents('.contentDiv') 
														.attr('code')) 
												.reloadModule();
									})
					$('.contentDiv .contentDivManage .remove').unbind('click')
							.click(
									function() {
										modulemanage_.getModule($(this).parents('.contentDiv').attr('code')).closeModule();
										
									})
				}
			}

			// 判断是否存在dom
			this.hasDom = function() {
				if ($('#contentDiv' + $this.code + '').length > 0) {
					// 存在
					return true;
				}
				return false;
			}
			this.hideModule = function(callback) {
				if ($this.rendertype == null) {
					return;
				}
				if ($this.rendertype == 'content'
						&& (modulemanage_.currentModule.length>0&&modulemanage_.currentModule[0].code == $this.code)) {
					if(callback) callback();
					return;
				}
				console.log('remove selected ' + $this.code)
				// 消失效果
				var $div = $('#contentDiv' + $this.code + '')

				if ($this.rendertype == 'right') {
					$div.stop().animate(
							{
								right : -($(window).width() - $("#leftmenu")
										.width()) * 0.5
							}, 200, null, function() {
								$div.hide();
								if (callback != null) {
									callback();
								}

							});
				} else if ($this.rendertype == 'top') {
					$div.stop().animate({
						top : -$(window).height() * 0.8
					}, 200, null, function() {
						$div.hide();
						if (callback != null) {
							callback();
						}

					});
				} else if ($this.rendertype == 'bottom') {
					$div.stop().animate({
						bottom : -$(window).height() * 0.312
					}, 200, null, function() {
						$div.hide();
						if (callback != null) {
							callback();
						}

					});
				} else {
					// 其他情况都从left出来
					$div.stop().animate(
							{
								left : -($(window).width() - $("#leftmenu")
										.width()) * 0.312
							}, 200, null, function() {
								$div.hide();
								if (callback != null) {
									callback();
								}

							});
				}
				// 给left_menu_item取消被选中样式
				$('#menu' + $this.code + '').removeClass('selected');
			}
			this.showModule = function() {
				if($this.rendertype=="content"){
					$("#menulist .selected").removeClass("selected");
					$("#submenu .selected").removeClass("selected");
					$("#menu"+$this.code).addClass("selected");
					if($this.type=="submodule"){
						$("#menu"+$this.pid).addClass("selected");
					}
					
					
				}	
				// 异常判断
				if ($this.rendertype == null) {
					return;
				}
				// 给left_menu_item添加被选中样式
				// 隐藏相邻div
				modulemanage_.hideNearModule($this, function() {
					var $div = $('#contentDiv' + $this.code + '')
					// 出现效果
					if ($this.rendertype == 'right') {
						$div.stop().show().animate({
							right : "0px"
						}, 200);
					} else if ($this.rendertype == 'top') {
						$div.stop().show().animate({
							top : "0px"
						}, 200);
					} else if ($this.rendertype == 'bottom') {
						$div.stop().show().animate({
							bottom : "0px"
						}, 200);
					} else {
						// 其他情况都从left出来
						$div.stop().show().animate({
							left : $("#leftmenu").width()
						}, 200);
					}
					// 是否添加蒙版
					if (!$this.allowMuilty && $this.rendertype != 'content') {
						$('.coverDiv').attr('code', $this.code);
						$('.coverDiv').stop().fadeIn();
					}
				});

			}

			this.closeModule = function(callback,flag) {
				if (!$this.allowMuilty) {
					$('.coverDiv').stop().fadeOut();
				}
				// 隐藏div
				$this.hideModule(callback); 
				if(flag == null){ 
					console.log('only close module') 
					modulemanage_.removeCurrentModule($this);
				}
				// 回调函数 
				console.log("dispose>>>>",$this,$('#menu' +$this.code + ''))
				if ($this.disposeClose && $this.code.indexOf("_tmp_")==0) { 
					// 清除manage中的该module    
					console.log("dispose module"+$this.code);
					modulemanage_.removeModule($this);
				}
			}
			// get当前src html
			this.loadModuleContent = function() {
				if ($this.src == "") {
					return;
				}
				//add loading
				var dom=null;
				if ($this.rendertype != 'content') {
					dom=$('#contentDiv' + $this.code + ' .htmlcontent');
				}else{
					dom=$('#maincontent');
				}
				dom.alloading().loading();
				console.log(dom);
				//return;
				
				setTimeout(function(){
					$.ajax({
						url : $this.src,
						async : false,
						type : "get",
						cache:false,
						dataType : "text",
						success : function(rep) {
							dom.alloading().unloading();
							// success
							if ($this.rendertype != 'content') {
								console.log('load contentDiv' + $this.code);
								$('#contentDiv' + $this.code + ' .htmlcontent')
										.html(rep);
							} else {
								console.log("load maincontent" + $this.code)
								$('#maincontent').html(rep);
							}
						},
						error:function(){
							dom.alloading().unloading();
						}
					});
				},500);
				

			}
			// 重新加载当前html
			this.reloadModule = function() {
				// 清空div
				console.log('empty' + $this.code)
				$('#contentDiv' + $this.code + ' .htmlcontent').empty();
				if($this.rendertype=="content"){
					$("#ddbtn .dditem.temp").remove();
				}
				// 重新get html
				$this.loadModuleContent();
			}
			// 打开一个module
			this.openModule = function() {
				//如果是在content打开的，需要删除当前顶部选择项
				if($this.rendertype=="content"){
					$("#ddbtn .dditem.temp").remove();
					modulemanage_.contentModule=$this;
				}
				// 判断此module是否已经存在
				if (modulemanage_.hasSameSrc($this)) {
					var oldModule = modulemanage_.getOwnModule($this.src,$this.code);
					$this=oldModule;
					
//					if ($('#menu' + oldModule.code + '').length > 0) {
//						$this.disposeClose = true;
//						$this.closeModule();
//						oldModule.openModule();
//						// 将OldModule 赋值给 $this
//						$this = oldModule;
//						console.log('remove new same src module');
//						return;
//					} else {
//						console.log('remove old same src module');
//						// 销毁相同src module
//						if(oldModule.disposeClose){
//							oldModule.closeModule();  
//							$this.openModule(); 
//						}else{
//							$this.closeModule();  
//							oldModule.openModule();  
//						}
//						
//						return;
//					}
				} else {
					console.log('no same div'); 
				}
				
				console.log("$this",$this);
				// 判断dom是否存在
				if (!$this.hasDom()) {
					$this.render();
					$this.showModule();
					$this.loadModuleContent();
				} else {
					$this.showModule();
				}
			}
		},

	};
})(jQuery, window);