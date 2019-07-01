/*引入util对应的js文件*/

define(['util'], function(){
	/*WmProgress对象定义*/
	function WmProgress(id,initAction){
		this.id = id;
		/*常用对象*/
		this.progress = (function(obj){
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
	}
	
	/* 设置进度条百分比，取值范围0-100 */
	WmProgress.prototype.setProgress = function(pro){
		if(pro < 0 || pro > 100) return ;
		$($(this.progress.find("div.e_progressBar")[0]).children()[0]).css("width",pro+"%");
		var use = this.progress.find("div.e_progressUseValue");
		if(use.length){
			$(use[0]).text(pro+"%");
		}
		var surplus = this.progress.find("div.e_progressSurplusValue");
		if(surplus.length){
			$(surplus[0]).text((100-pro)+"%");
		}
	};
	/*设置进度条警示级别 从低到高分为info、warn、error*/
	WmProgress.prototype.setLevel = function(level){
		if(level == "warn"){
			if(this.progress.hasClass("e_progress-error")){
				this.progress.removeClass("e_progress-error");
			}
			if(!this.progress.hasClass("e_progress-warn")){
				this.progress.addClass("e_progress-warn");
			}
		}else if(level == "error"){
			if(this.progress.hasClass("e_progress-warn")){
				this.progress.removeClass("e_progress-warn");
			}
			if(!this.progress.hasClass("e_progress-error")){
				this.progress.addClass("e_progress-error");
			}
		}else{
			if(this.progress.hasClass("e_progress-warn")){
				this.progress.removeClass("e_progress-warn");
			}
			if(this.progress.hasClass("e_progress-error")){
				this.progress.removeClass("e_progress-error");
			}
		}
	};
	/*返回进度0-100*/
	WmProgress.prototype.getProgress = function(){
		var width = $($(this.progress.find("div.e_progressBar")[0]).children()[0]).css("width");
		return width.substring(0,width.length-1);
	};

 	/*设置总量提示*/
	WmProgress.prototype.setTotalTip = function(tip){
		$(this.progress.find("div.e_progressTotal")[0]).text(tip);
	};
	/*设置已完成标题*/
	WmProgress.prototype.setPastTip = function(tip){
		$(this.progress.find("div.e_progressUseTip")[0]).text(tip);
	};
	/*设置未完成标题*/
	WmProgress.prototype.setSpaceTip = function(tip){
		$(this.progress.find("div.e_progressSurplusTip")[0]).text(tip);
	};
	/*显示总量提示 */
	WmProgress.prototype.showTotalTip = function(){
		$(this.progress.find("div.e_progressTotal")[0]).css("display","block");
	};
	/*隐藏总量提示*/
	WmProgress.prototype.hideTotaTip = function(){
		$(this.progress.find("div.e_progressTotal")[0]).css("display","none");
	};
	/*显示已完成提示 */
	WmProgress.prototype.showPastTip = function(){
		$(this.progress.find("div.e_progressUse")[0]).css("display","block");
	};
	/*隐藏已完成提示*/
	WmProgress.prototype.hidePastTip = function(){
		$(this.progress.find("div.e_progressUse")[0]).css("display","none");
	};
	/*显示未完成提示*/
	WmProgress.prototype.showSpaceTip = function(){
		$(this.progress.find("div.e_progressSurplus")[0]).css("display","block");
	};
	/*隐藏未完成提示*/
	WmProgress.prototype.hideSpaceTip = function(){
		$(this.progress.find("div.e_progressSurplus")[0]).css("display","none");
	};

	/*导出WmProgress*/
	return WmProgress;
});
