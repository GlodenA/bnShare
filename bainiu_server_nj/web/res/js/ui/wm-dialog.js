/*引入util对应的js文件*/
define(['util'], function(){
	/*WmNavBar对象定义*/
	function WmDialog(id){
		this.listeners = new Array(); //存储监听事件
		this.id = id;
		/*常用对象*/
		this.box = (function(obj){
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
		this.title        = this.box.find(".wrapper > .title > .text");
		this.closeButton  = this.box.find(".wrapper > .title > a.close");
		this.contentDiv   = this.box.find(".wrapper > .content");
		this.submitDiv    = this.box.find(".wrapper > .submit");
		this.submitButton = this.box.find(".wrapper > .submit .e_button-ok");
		this.cancelButton = this.box.find(".wrapper > .submit .e_button-cancel");
		/*设置默认按钮事件*/
		var that = this;
		this.closeButton.tap(function(){that.hide()});
	}
	/*开关事件定义*/
	WmDialog.prototype.hide = function(){
		this.box.removeClass("c_dialog-view");
		if(typeof(this.hideAction) == "function") {
			this.hideAction();
		}else{
			new Function("return "+action)();
		}
	}
	WmDialog.prototype.show = function(){
		this.box.addClass("c_dialog-view");
		if(typeof(this.setHideAction) == "function") {
			this.showAction();
		}
	}
	WmDialog.prototype.setHideAction = function(action){
		if(typeof(action) == "function") {
			this.hideAction = action;
		}
	};
	WmDialog.prototype.setShowAction = function(action){
		if(typeof(action) == "function") {
			this.showAction = action;
		}
	};
	/*
	  提交/取消事件绑定
	  action： 执行的事件（必选）
	  isClose：执行后是否关闭对话框（可选，默认为 true）
	*/
	WmDialog.prototype.submitAction = function(action,isHide){
		var that = this;
		if(this.submitButton.length != 0) {
			this.submitButton.tap(function(){
				action();
				if(isClose != false) { that.hide();}
			});
		} else {
			alert("设置错误：未设置提交按钮");
		}
	}
	WmDialog.prototype.cancelAction = function(action,isHide){
		var that = this;
		if(this.cancelButton.length != 0) {
			this.cancelButton.tap(function(){
				action();
				if(isClose != false) { that.hide();}
			});
		} else {
			alert("设置错误：未设置取消按钮");
		}
	}
	/*设置标题*/
	WmDialog.prototype.setTitle = function(str) {
		this.title.text(str)
	}
	/*设置内容*/
	WmDialog.prototype.setContent = function(str,setBy) {
		if (setBy == "html") {
			this.contentDiv.html($("#" + str).html());
		} else {
			this.contentDiv.html("<div class='c_article'>" + str + "</div>");
		}
	}
	/*导出WmDialog*/
	return WmDialog;
});
