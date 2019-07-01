/**
 * 提供业务的AppMobile插件
 */
define(["require"],function(require) {
	var BizMobile = (function(){
		return{
			login:function(loginParam,err){ //加载进度条
				execute("login", [loginParam],err);
			},openAnimation:function(err){ //打开动画
				execute("openAnimation", [],err);
			},shutAnimation:function(err){ //关闭动画
				execute("shutAnimation", [],err);
			},defineAlert:function(msg,callback,err){
				execute("defineAlert", [msg],err);
			}
		};
	})();
	
	var WadeMobile;
	function execute(action, args, error, success) {
		/*循环依赖,懒加载*/
		if(!WadeMobile){
			WadeMobile = require("wadeMobile")
		}
        return WadeMobile.execute(action, args, error, success)
	}
	function storageCallback(action,callback,isEscape,isBase64) {
		/*循环依赖,懒加载*/
		if(!WadeMobile){
			WadeMobile = require("wadeMobile")
		}
		WadeMobile.callback.storageCallback(action,callback,isEscape,isBase64)
	}
	return BizMobile;
});