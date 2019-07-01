/**
 * 此JS文件匹配mobile_client.js,用于分离出终端调用和web调用的逻辑.
 * 浏览器访问应用时,动态引用mobile_browser.js文件;终端访问应用时,动态引用mobile_client.js文件.
 */
define(["browserTool"],function(browserTool) {
	var Mobile = new function() {
		/******************系统功能**********************/
		/*判断是否App*/
		this.isApp = function(){
			return false;
		};
		/*关闭应用 */
		this.closeApp = function() {
			window.opener=null;
			window.open('','_self');
	        window.close();
		};
		/******************数据请求**********************/
		/*调用服务*/
		this.dataRequest = function(action, param, callback, isEscape, err) {
			browserTool.ajax.post(action, param, callback, isEscape, err);
		};
		/******************页面跳转**********************/
		/*页面跳转,url为跳转目标*/
		this.openUrl = function(url, err) {
			browserTool.redirect.toUrl(url);
		};
		/*页面跳转,param为打开页面时调用接口的参数*/
		this.openPage = function(pageName, param, err) {
			var url = browserTool.ServerPath;
			url += "?action=" + pageName;
			var params;
			if (param) {
				params = {data:param};
			}
			browserTool.redirect.postPage(url, params);
			/* 
			 * get方式不适用了
			 * var url = browserTool.redirect.buildUrl(pageName,
			 * param.toString()); browserTool.redirect.toUrl(url);
			 */
		};
		
		/*页面跳转,param为打开页面的映射数据*/
		this.openTemplate = function(pageName, param, err) {
			var url = browserTool.ServerPath;
			url += "?action=" + pageName;
			var params = null;
			if (param) {
				params = {context:param};
			}
			browserTool.redirect.postPage(url, params);
		};
		/*将模板转换成html源码*/
		this.getTemplate = function(action, param, callback, err) {
			browserTool.ajax.html(action, param, callback, err);
		};
		/*将Page转换成html源码*/
		this.getPage = function(action, param, callback, err) {
			browserTool.ajax.html(action, param, callback, err);
		};
		/*回退到前一个界面*/
		this.back = function(){
			history.go(-1);
		};
		/******************基础UI**********************/
		/*打开loading对话框*/
		this.loadingStart = function(message,title){
			browserTool.browser.loadingStart(message,title);
		};
		/*关闭加载中对话框*/
		this.loadingStop = function(){
			browserTool.browser.loadingStop();
		};
		this.confirm = function(j, k, h, i){
			browserTool.browser.confirm(j, k, h, i);
		};
		//context:显示内容; id:显示位置，即输入框id; time:显示时间 时间默认为0.5秒
		/*弹出提示气泡*/
		this.tip = function(msg,type){
			browserTool.browser.tip(msg,type);
		};
		this.alert = function(msg,title,callback){
			browserTool.browser.alert(msg,title,callback);
		};
		/******************内存缓存**********************/
		this.setMemoryCache = function(key, value){
			browserTool.browser.setMemoryCache(key, value);
		};
		this.getMemoryCache = function(callback,key, value){
			browserTool.browser.getMemoryCache(callback,key, value);
		};
		this.removeMemoryCache = function(key){
			browserTool.browser.removeMemoryCache(key);
		};
		this.clearMemoryCache = function(){
			browserTool.browser.clearMemoryCache();
		};
		/******************离线缓存**********************/
		this.setOfflineCache = function(key, value){
			browserTool.browser.setOfflineCache(key, value);
		};
		this.getOfflineCache = function(callback, key, value){
			browserTool.browser.getOfflineCache(callback, key, value);
		};
		this.removeOfflineCache = function(key){
			browserTool.browser.removeOfflineCache(key);
		};
		this.clearOfflineCache = function(){
			browserTool.browser.clearOfflineCache();
		};
		/******************扩展UI**********************/
		var windowCallback;
		var windowFlag = false;
		this.openWindow = function(pageAction, param, callback, isContext) {
			/*var url = browserTool.redirect.buildUrl(pageAction, param, isContext);
			window.open(url, pageAction + new Date());*/
			var url = browserTool.redirect.buildUrl(pageAction, null, isContext);
			if(param){
				param = {data:param} //转换json格式
			}
			browserTool.redirect.openPostWindow(pageAction, url, param);
			windowCallback = callback;
			windowFlag = true;
		};
		this.closeWindow = function(result) {
			if (window.opener) {
				if (windowFlag) {
					windowCallback(result);
					windowFlag = false;
				} else {
					window.opener.closeWindow(result);
					window.close();
				}
			} else if (windowCallback) {
				windowCallback(result);
			}
		};
		window.closeWindow = this.closeWindow;

		var dialogCallback;
		window.dialogFlag = false;
		this.openDialog = function(pageAction, param, callback, isContext) {
			if(window.opener)
			if(window.opener&&window.opener.dialogFlag){
				var err = "存在已打开的窗口";
				alert(err);
				throw err;
			}
			dialogFlag = true;
			/*var url = browserTool.redirect.buildUrl(pageAction, param, isContext);
			window.open(url, pageAction);*/
			if(param){
				param = {data:param} //转换json格式
			}
			var url = browserTool.redirect.buildUrl(pageAction, null, isContext);
			browserTool.redirect.openPostWindow(pageAction, url, param);
			dialogCallback = callback;
		};
		
		this.closeDialog = function(result) {
			if (window.opener) {
				window.opener.closeDialog(result);
				window.close();
			} else if(dialogCallback) {
				dialogFlag = false;
				dialogCallback(result);
			}
		};
		window.closeDialog = this.closeDialog;
		
		/******************本地数据库操作**********************/
		this.execSQL = function(dbName,sql,bindArgs,callback,err){
			alert("等待实现");
		};
		this.insert = function(dbName,table,datas,callback,err){
			alert("等待实现");
		};
		this.delete = function(dbName,table,datas,callback,err){
			alert("等待实现");
		};
		this.update = function(dbName,table,datas,conds,callback,err){
			alert("等待实现");
		};
		this.select = function(dbName,table,columns,conds,callback,err){
			alert("等待实现");
		};
		//查询第一行数据,效率高
		this.selectFirst = function(dbName,table,columns,conds,callback,err){
			alert("等待实现");
		};
	}
	
	return Mobile;
});
