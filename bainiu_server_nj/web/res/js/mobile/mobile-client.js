/**
 * 此JS文件匹配mobile_browser.js,用于分离出终端调用和web调用的逻辑.
 * 终端访问应用时,动态引用mobile_client.js文件;浏览器访问应用时,动态引用mobile_browser.js文件.
 */
define(["wadeMobile","clientTool"],function(WadeMobile,clientTool) {
	var Mobile = new function() {
		/******************系统功能**********************/
		/*判断是否App*/
		this.isApp = function(){
			return WadeMobile.isApp();
		};
		/*关闭应用*/
		this.closeApp = function() {
			WadeMobile.close(false);
		};
		/******************数据请求**********************/
		/*调用服务*/
		this.dataRequest = function(action, param, callback, isEscape, err) {
			param = param ? param : "";
			WadeMobile.dataRequest(callback, action, param.toString(), isEscape, err);
		};
		/******************页面跳转**********************/
		/*页面跳转,url为跳转目标*/
		this.openUrl = function(url, err) {
			WadeMobile.openUrl(url, err);
		};
		/*页面跳转,param为打开页面时调用接口的参数*/
		this.openPage = function(pageAction, param, err) {
			param = param ? param : "";
			WadeMobile.openPage(pageAction, param.toString(), err);
		};
		/*页面跳转,param为打开页面的映射数据*/
		this.openTemplate = function(pageAction, param, err) {
			param = param ? param : "";
			WadeMobile.openTemplate(pageAction, param.toString(), err);
		};
		/*将模板转换成html源码*/
		this.getTemplate = function(action,param,callback,err){
			alert("getTemplate待开发……");
		};
		/*将Page转换成html源码*/
		this.getPage = function(action, param, callback, err){
			alert("getPage待开发……");
		};
		/*回退到前一个界面*/
		this.back = function(){
			WadeMobile.back();
		};
		/******************基础UI**********************/
		/*打开loading对话框*/
		this.loadingStart = function(message,title){
			WadeMobile.loadingStart(message,title);
		};
		/*关闭加载中对话框*/
		this.loadingStop = function(){
			WadeMobile.loadingStop();
		};
		/*弹出确认对话框*/
		this.confirm = function(){
			alert("confirm待开发……");
		};
		/*弹出提示气泡*/
		this.tip = function(msg,type){
			if(type==undefined){
				type = 1;
			}
			WadeMobile.tip(msg, type);
		};
		/*弹出提示框*/
		this.alert = function(msg, title, callback) {
			WadeMobile.alert(msg, title, callback);
		};
		/******************内存缓存**********************/
		this.setMemoryCache = function(key, value){
			if (clientTool.tool.isDataMap(key)) {
				WadeMobile.setMemoryCache(key.map);
			} else {
				WadeMobile.setMemoryCache(key, value);
			}
		};
		this.getMemoryCache = function(callback,key, value){
			WadeMobile.getMemoryCache(callback,key, value);
		};
		this.removeMemoryCache = function(key){
			WadeMobile.removeMemoryCache(key);
		};
		this.clearMemoryCache = function(){
			WadeMobile.clearMemoryCache();
		};
		/******************离线缓存**********************/
		this.setOfflineCache = function(key, value){
			if (clientTool.tool.isDataMap(key)) {
				WadeMobile.setOfflineCache(key.map);
			} else {
				WadeMobile.setOfflineCache(key, value);
			}
		};
		this.getOfflineCache = function(callback, key, value){
			WadeMobile.getOfflineCache(callback, key, value);
		};
		this.removeOfflineCache = function(key){
			WadeMobile.removeOfflineCache(key);
		};
		this.clearOfflineCache = function(){
			WadeMobile.clearOfflineCache();
		};
		/******************扩展UI**********************/
		this.openDialog = function(pageAction,param,callback){
			WadeMobile.openDialog(callback,pageAction,param.toString());
		};
		this.closeDialog = function(result){
			WadeMobile.closeDialog(result);
		};
		this.openWindow = function(pageAction,param,callback){
			WadeMobile.openWindow(callback,pageAction,param.toString());
		};
		this.closeWindow = function(result){
			WadeMobile.closeWindow(result);
		};
		/******************本地数据库操作**********************/
		this.execSQL = function(dbName,sql,bindArgs,callback,err){
			WadeMobile.execSQL(dbName,sql,bindArgs,callback,err);
		};
		this.insert = function(dbName,table,datas,callback,err){
			WadeMobile.insert(dbName,table,datas,callback,err);
		};
		this.delete = function(dbName,table,datas,callback,err){
			WadeMobile.delete(dbName,table,datas,callback,err);
		};
		this.update = function(dbName,table,datas,conds,callback,err){
			WadeMobile.update(sql,bindArgs,callback,err);
		};
		this.select = function(dbName,table,columns,conds,callback,err){
			WadeMobile.select(sql,bindArgs,callback,err);
		};
		//查询第一行数据,效率高
		this.selectFirst = function(dbName,table,columns,conds,callback,err){
			WadeMobile.selectFirst(sql,bindArgs,callback,err);
		};
	};
	
	return Mobile;
});

