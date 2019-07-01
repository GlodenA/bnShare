/*引入util对应的js文件*/

define(['util'], function(){
	/*WmToolTip对象定义*/
	function WmToolTip(id){
		this.listeners = new Array(); //存储监听事件
		this.id = id;
		/*常用对象*/
		this.tip = (function(obj){
			if(typeof(obj)=="object"){
				obj = $(obj);
			}else if(typeof(obj)=="string"){
				obj = $("#"+obj);
			}else{
				alert("没有匹配类型");
				return null;
			}
			return obj;
		})(id);
		this.baseSize = parseInt($(document.getElementsByTagName("html")[0]).css("font-size"));
	}
	/*关闭按钮事件*/
	WmToolTip.prototype.setCloseAction = function(action){
		var closeAction = function(e){
			$($(e.target).parents("div.c_toolTip-view")[0]).removeClass("c_toolTip-view");
			if(action){
				if(typeof action =="function"){
					action();
				}else{
					new Function("return "+action)().call(window);
				}
			}
		};
		var btn = this.tip.find("span.e_button");
		$(btn[0]).tap(closeAction);
	};
	/*设置位置参照元素*/
	WmToolTip.prototype.setBaseElement = function(ele){
		var e;
		if(typeof ele =="object"){
			e = $(ele);
		}else if(typeof ele =="string"){
			e = $("#"+ele);
		}
		var o = e.offset();
		var h = e.height();
		var w = e.width();
		var tipH = this.tip.height();
		var tipW = this.tip.width();
		var left = o.left;
		var top = o.top;
		if(this.tip.hasClass("c_toolTip-positionBottom")){
			top-=tipH;//箭头在下
		}else{
			top+=h;//箭头在上
		}
		if(this.tip.hasClass("c_toolTip-arrowLeft")){
			//箭头居左
		}else if(this.tip.hasClass("c_toolTip-arrowRight")){
			left=left+w-tipW;//箭头居右
		}else{
			left = left+w/2-tipW/2;//箭头居中
		}
		left = left/this.baseSize;
		top = top/this.baseSize;
		this.tip.css("top",top+"rem");
		this.tip.css("left",left+"rem");
	};
	/*显示*/
	WmToolTip.prototype.show = function(){
		if(!this.tip.hasClass("c_toolTip-view")){
			this.tip.addClass("c_toolTip-view");
		}
	};
	/*隐藏*/
	WmToolTip.prototype.hide = function(){
		this.tip.removeClass("c_toolTip-view");
	};
	/*移除*/
	WmToolTip.prototype.remove = function(){
		this.tip.remove();
	};
	/*设置按钮文本*/
	WmToolTip.prototype.setButtonText = function(text){
		var button = this.tip.find('span.e_button');
		if(button.length){
			$(button[0]).text(text);
		}else{
			$(this.tip.find("div.content")[0]).append('<div class="submit"><span class="e_button">'+text+'</span></div>');
		}
	};
	/*设置图标*/
	WmToolTip.prototype.setIcon = function(icon){
		var e = this.tip.find('div.ico');
		if(e.length){
			$(e[0]).html('<span class="e_ico '+icon+'"></span>');
		}else{
			$(this.tip.find("div.content")[0]).prepend('<div class="ico"><span class="e_ico '+icon+'"></span></div>');
		}
	};
	/*设置提示内容*/
	WmToolTip.prototype.setContent = function(text){
		$(this.tip.find("div.detail")[0]).text(text);
	};
	/*导出WmToolTip*/
	return WmToolTip;
});
