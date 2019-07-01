/**
 * 提供三个对象browser、redirect、ajax. 用于辅助mobile-browser.js的实现,仅仅在浏览器访问时使用.
 * mobile-browser.js需要依赖browser-core.js.
 */
define(["module","jcl","clientTool"],function(module,Wade,clientTool) {
	var browserTool = {};
	module.exports = browserTool;

	browserTool.browser = (function(){
		function sessionError(){
			alert("your browser not support sessionStorage!");
		}
		
		function localError(){
			alert("your browser not support localStorage!");
		}
        return{
        	loadingStart : function(message,title){
        		// 等待开发
        	},loadingStop : function(){
        		// 等待开发
        	},confirm : function(msg){
        		window.confirm(msg);
        	},tip : function(msg,type){
        		// 等待开发
        		alert(msg);
        	},alert : function(msg,title,callback){
        		alert(msg);
        		if(callback){
        			callback();
        		}
        	},setMemoryCache : function(key, value){
        		if (window["sessionStorage"]) {
    				if (clientTool.tool.isDataMap(key)) {
    					for ( var k in key.map) {
    						window["sessionStorage"].setItem(k, key.map[k]);
    					}
    				} else {
    					if (!value) {
    						return
    					}
    					window["sessionStorage"].setItem(key, value);
    				}
    			} else {
    				sessionError();
    			}
        	},getMemoryCache : function(callback,key, value){
        		if (window["sessionStorage"]) {
    				if (clientTool.tool.isArray(key)) {
    					var data = new Wade.DataMap();
    					for ( var i = 0, len = key.length; i < len; i++) {
    						data.put(key[i], window["sessionStorage"].getItem(key[i]));
    					}
    					callback(data);
    					return;
    				} else {
    					callback(window["sessionStorage"].getItem(key));
    					return;
    				}
    			} else {
    				sessionError();
    			}
        	},removeMemoryCache : function(key){
        		if (window["sessionStorage"]) {
    				if (clientTool.tool.isArray(key)) {
    					for ( var i = 0, len = key.length; i < len; i++) {
    						window["sessionStorage"].removeItem(key[i]);
    					}
    				} else {
    					window["sessionStorage"].removeItem(key);
    				}
    			} else {
    				sessionError();
    			}
        	},clearMemoryCache : function(){
        		if (window["sessionStorage"]) {
    				window["sessionStorage"].clear();
    			} else {
    				sessionError();
    			}
        	},setOfflineCache : function(key, value){
        		if (window["localStorage"]) {
    				if (clientTool.tool.isDataMap(key)) {
    					for ( var k in key.map) {
    						window["localStorage"].setItem(k, key.map[k]);
    					}
    				} else {
    					if (!value) {
    						return
    					}
    					window["localStorage"].setItem(key, value);
    				}
    			} else {
    				localError();
    			}
        	},getOfflineCache : function(callback, key, value){
        		if (window["localStorage"]) {
    				if (clientTool.tool.isArray(key)) {
    					var data = new Wade.DataMap();
    					for ( var i = 0, len = key.length; i < len; i++) {
    						data.put(key[i], window["localStorage"].getItem(key[i]));
    					}
    					callback(data);
    					return;
    				} else {
    					callback(window["localStorage"].getItem(key));
    					return;
    				}
    			} else {
    				localError();
    			}
        	},removeOfflineCache : function(key){
        		if (window["localStorage"]) {
    				if (clientTool.tool.isArray(key)) {
    					for ( var i = 0, len = key.length; i < len; i++) {
    						window["localStorage"].removeItem(key[i]);
    					}
    				} else {
    					window["localStorage"].removeItem(key);
    				}
    			} else {
    				localError();
    			}
        	},clearOfflineCache : function(){
        		if (window["localStorage"]) {
    				window["localStorage"].clear();
    			} else {
    				localError();
    			}
        	}
        };
	})();
	
	/**
	 * 页面重定向的API
	 */
	browserTool.ServerPath = "mobile";
	browserTool.redirect = (function() {
		return {
			/**
			 * isContext 为true时,data参数作为页面渲染参数 encode 只支持传boolean的false
			 */
			buildUrl : function(action, data, isContext, encode) {
				var url = browserTool.ServerPath;
				url += "?action=" + action;
				if (data) {
					if(encode!=false){
						data = encodeURI(encodeURI(data.toString()));
					}else{
						url += "&isEncode=false";
					}
					url += "&data=" + data;
				}
				if (isContext) {
					url += "&isContext=" + isContext;
				}
				return url;
			},
			
			toUrl : function(url) {
				if (!url) {
					return;
				}
				document.location.href = url;
			},
			
			toPage : function(action, params) {
				var url = this.buildUrl(action, params);
				document.location.href = url;
			},
			
			postPage : function (url, params) {
			    var temp = document.createElement("form");
			    temp.action = url;
			    temp.method = "post";
			    temp.style.display = "none";
			    for (var x in params) {
			        var opt = document.createElement("input");
			        opt.type="hidden";  
			        opt.name = x;
			        opt.value = params[x];
			        temp.appendChild(opt);
			    }
			    document.body.appendChild(temp);
			    temp.submit();
			    temp.parentNode.removeChild(temp);
			},
			/*
			 * data 参数必须是json格式 
			 */
			openPostWindow : function(name, url, data){
        		var temp = document.createElement("form");
		    	temp.action = url;
		    	temp.method = "post";
 			    temp.style.display = "none";
 			    temp.target = name;
 			    
 			    for (var x in data) {
			    	var ele = document.createElement("input");
			    	ele.type="hidden";  
			    	ele.name = x;
			    	ele.value =  data[x];
			    	temp.appendChild(ele);
			    }
 			   	document.body.appendChild(temp);
 			   	temp.submit();
 			   	temp.parentNode.removeChild(temp);
        	}
		};
	})();

	/**
	 * ajax方面操作的API
	 */
	browserTool.ajax=(function(){
	 	function doAjax(setting){
	 		var ajaxObj=Wade.extend(true,{},browserTool.ajax.ajaxSettings,setting);
	 		Wade.ajaxRequest(ajaxObj);
	 	}
	 	/* 拼接ajax的url,数据则通过post传输 */
	 	function buildAjaxUrl(url,action,isContext){
	 		url=url.replace("/n","");
			url+="?action=" + action;
			if(isContext){
				url+="&isContext=" + isContext;
			}
			return url;
		}
	 	
	 	function buildPostData(data, isEncode){
			var buf = [];
			for(var key in data){
				 var ov = data[key], k = (false==isEncode ? key : encodeURIComponent(key));
				 var type=typeof(ov);
				 if(type== "undefined" || null==ov || ""==ov){
					buf.push(k, "=&");
				 }else if(type != "function" && type != "object"){
				 	if(type == "string"){
				 	//不允许url提交换行符
				 		ov=ov.replace(/\\n/g,"");
				 	}
					buf.push(k, "=", (false==isEncode ? ov : encodeURIComponent(ov)), "&");
				 }else if($.isArray(ov)){
					if(ov.length){
						for(var i = 0, len = ov.length; i < len; i++) {
							buf.push(k, "=", (false == isEncode ? (ov[i] === undefined ? '' : ov[i]) : encodeURIComponent(ov[i] === undefined ? '' : ov[i])), "&");
						}
					}else{
						buf.push(k, "=&");
					}
				}
			}
			buf.pop();
		    return buf.join("");
		}
	 	
	 	return{
	 		ajaxSettings:{
	 			simple: false, // 使用简单返回,不做tapestry相关的数据转换不处理part
	 			type: "GET",
	 			async: true,
	 			cache: false,
	 			dataType: "text",
	 			loading: true,
	 			timeout: 3*60*1000,
	 			encoding: "UTF-8"
	 		},
	 		setup:function(settings){
	 			if(settings && Wade.isPlainObject(settings)){
	 				Wade.extend(this.ajaxSettings,settings);
	 			}
	 		},
	 		request:function(url,action,params,callback,error,settings,isContext){
	 			var transUrl = buildAjaxUrl(url,action,isContext);
	 			/* 处理Wade.DataMap类型 */
	 			var postParam = new Wade.DataMap();
	 			if(params){
	 				postParam.put("data",params.toString());
	 			}
	 			
	 			var setting = Wade.extend({
	 				url:transUrl,
	 				data:buildPostData(postParam.map,true),
	 				success: callback,
	 				error: _error
	 			},browserTool.ajax.ajaxSettings,settings);
	 			
	 			// 框架封装处理只处理text返回类型
	 			if("text"!=setting.dataType){
	 				setting.simple=true;
	 			}
	 			
	 			doAjax(setting);
	 			
	 			function _error(xhr, status, ex){
	 				if(setting.simple){
	 					if(error && Wade.isFunction(error)){
	 						error(status,ex.getMessage());
	 					}
	 					return;
	 				}
	 				throw ex;
	 			}
	 		},
	 		get:function(action,params,callback,error,settings){
	 			browserTool.ajax.request("mobiledata",action,params,callback,error,settings);
	 		},
	 		post:function(action,params,callback,error,settings){
	 			browserTool.ajax.request("mobiledata",action,params,callback,error,Wade.extend({type:"POST",encoding:"UTF-8"},settings));
	 		},
	 		html:function(action,params,callback,error,settings){
	 			browserTool.ajax.request("mobile",action,params,callback,error,Wade.extend({type:"POST",encoding:"UTF-8"},settings),true);
	 		}
	    };
	})();
});
