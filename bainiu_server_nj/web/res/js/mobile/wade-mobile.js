/**
 * 与客户端交互的基础API
 */
define(["jcl","base64","util","res/js/mobile/expand-mobile.js","biz/js/common/biz-mobile.js"],function(Wade,Base64) {
	//终端类型,a为android,i为ios
    var terminalType = (function(){
		var sUserAgent = navigator.userAgent.toLowerCase();
		var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";     
		var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
		var bIsAndroid = sUserAgent.match(/android/i) == "android";
		var bIsWinphone = sUserAgent.match(/windows phone /i) == "windows phone " || sUserAgent.match(/windows phone os /i) == "windows phone os ";
		if(bIsAndroid){
			return "a";
		}else if(bIsIpad||bIsIphoneOs){
			return "i";
		}else if(bIsWinphone){
			return "w";
		}else{
			return null;
		}
	})();
	
	WadeMobile = (function(){
        return{
        	isAndroid:function(){
        		return terminalType=='a';
        	},isIOS:function(){
        		return terminalType=='i';
        	},isWP:function(){
        		return terminalType=='w';
        	},isApp:function(){//判断是否是APP应用
				return terminalType!=null;
        	},getSysInfo:function(callback,key,err){//TELNUMBER|IMEI|IMSI|SDKVERSION|OSVERSION|PLATFORM|SIMNUMBER
				WadeMobile.callback.storageCallback("getSysInfo",callback);
				execute("getSysInfo", [key],err);
			},close:function(confirm,err){
				if(typeof(confirm)!="boolean"){
					confirm = true;
				}
				execute("close", [confirm],err);
			},httpRequest:function(callback,requestUrl,isEscape,encode,conTimeout,readTimeout,err){
				if (isEscape == undefined) {
					isEscape = true;
				}
				if(terminalType=="i"){
					requestUrl = encodeURIComponent(requestUrl);
				}
				WadeMobile.callback.storageCallback("httpRequest",callback,isEscape);
				execute("httpRequest", [requestUrl,isEscape,encode,conTimeout,readTimeout],err);
			},dataRequest:function(callback,dataAction,param,isEscape,encode,conTimeout,readTimeout,err){
				if (isEscape == undefined) {
					isEscape = true;
				}
				WadeMobile.callback.storageCallback("dataRequest",callback,isEscape);
				execute("dataRequest", [dataAction,param,isEscape,encode,conTimeout,readTimeout],err);
			},openUrl:function(url,err){
				execute("openUrl", [encodeURIComponent(url)],err);
			},openPage:function(action,data,err){
				execute("openPage", [action,data],err);
			},openTemplate:function(action,context,err){
				execute("openTemplate", [action,context],err);
			},back:function(err){
				execute("back", [],err);
			},storageDataByThread:function(dataAction,param,waitoutTime,err){
				execute("storageDataByThread", [dataAction,param,waitoutTime],err);
			},openDialog:function(callback,pageAction,param,isEscape,err){
				if (isEscape == undefined) {
					isEscape = true;
				}
				WadeMobile.callback.storageCallback("openDialog",callback,isEscape);
				execute("openDialog", [pageAction,param,isEscape],err);
			},closeDialog:function(data,err){
				execute("closeDialog", [data],err);
			},openWindow:function(callback,pageAction,param,isEscape,err){
				if (isEscape == undefined) {
					isEscape = true;
				}
				WadeMobile.callback.storageCallback("openWindow",callback,isEscape);
				execute("openWindow", [pageAction,param,isEscape],err);
			},closeWindow:function(data,err){
				execute("closeWindow", [data],err);
			}
		};
	})();
	
	//全局变量
	var callbackId = 0;
	var callbacks = {};//用来存放成功和失败的js回调函数
	var callbackDefine = {};//用来存放自定义的js回调函数
	
	/*绝大多数情况下,success回调函数是用不上的,有需要回调函数的时候异步方式传入取值*/
	var execute = function(action, args, error, success){
        args = stringify(args);
		if(terminalType=="a"){
			androidExecute(action, args, error, success);
		}else if(terminalType=="i"){
			iosExecute(action, args, error, success);
		}else if(terminalType=="w"){
			winphoneExecute(action, args, error, success);
		}else{
			//alert("无终端类型");
			//iosExecute(action, args, error, success);
		}
	};
	
	WadeMobile.execute = execute;
	
	var androidExecute = function(action, args, error, success){
		try {
	        var callbackKey = action+callbackId++;
	        if (success || error) {
        		callbacks[callbackKey] = {success:success, error:error};
	        }
	        if(WadeMobile.debug){
	        	//alert("准备调用"+action+" 参数:"+args);
	        	console.log("action:"+action+" param:"+args);
	        }
	        PluginManager.exec(action, callbackKey, args);
	    } catch (e) {
	    	printErr(e,"androidExecute");
	    }
	};
 
    var iosExecute = function(action, args, error, success){
        try {
            var callbackKey = action+callbackId++;
            if (success || error) {
                callbacks[callbackKey] = {success:success, error:error};
            }
            if(WadeMobile.debug){
                //alert("准备调用"+action+" 参数:"+args);
                console.log("action:"+action+" param:"+args);
            }
	        var WADE_SCHEME = "wade://";
            var url = WADE_SCHEME+action+"?param="+args+"&callback="+callbackKey;
//            var ifrmName = action;
            //一个动作请求客户端的最大数量，超过会造成请求覆盖
            var limitAction = 10;
            var ifrmName = "WADE_FRAME_"+(callbackId%limitAction);
            var ifrm = document.getElementById(ifrmName);
            if(!ifrm){
                var ifrm = document.createElement("iframe");
                ifrm.setAttribute("id",ifrmName);
                ifrm.setAttribute("width","0");
                ifrm.setAttribute("height","0");
                ifrm.setAttribute("border","0");
                ifrm.setAttribute("frameBorder","0");
                ifrm.setAttribute("name",ifrmName);
                document.body.appendChild(ifrm);
            }
            document.getElementById(ifrmName).src = encodeURI(url);
            //document.getElementById(ifrmName).contentWindow.location = encodeURI(invocation);
	    } catch (e) {
	    	printErr(e,"iosExecute");
	    }
	};
	
	var winphoneExecute = function(action, args, error, success){
		try {
	        var callbackKey = action+callbackId++;
	        if (success || error) {
        		callbacks[callbackKey] = {success:success, error:error};
	        }
	        if(WadeMobile.debug){
	        	//alert("准备调用"+action+" 参数:"+args);
	        	console.log("action:"+action+" param:"+args);
	        }
	      	window.external.Notify(stringify([action, callbackKey, args])); //[action, callbackKey, args]
	    } catch (e) {
	    	printErr(e,"winphoneExecute");
	    }
	};
	
	WadeMobile.callback = (function(){
		return{
			success:function(callbackKey, message) {
				if(typeof message == "undefined"){
					return;
				}
			    if (callbacks[callbackKey]) {
		            try {
		                if (callbacks[callbackKey].success) {
		                	if(typeof callbacks[callbackKey].success==="function"){
		                		var func = callbacks[callbackKey].success;
		                		func(message);
		                	}else{
		                		_eval(callbacks[callbackKey].success+"('"+message+"','"+callbackKey+"')");
		                	}
		                }
		            }catch (e) {
		                printErr(e,"WadeMobile.callback.success("+callbackKey+")");
		            }
			        if (callbacks[callbackKey]) {
			            delete callbacks[callbackKey];
			        }
			    }
			},error:function(callbackKey, message) {
				if(typeof message == "undefined"){
					return;
				}
			    if (callbacks[callbackKey]) {
			        try {
			            if (callbacks[callbackKey].error) {
			                if(typeof callbacks[callbackKey].error==="function"){
			                	var func = callbacks[callbackKey].error;
			                	func(message);
		                	}else{
		                		_eval(callbacks[callbackKey].error+"('"+message+"','"+callbackKey+"')");
		                	}
			            }
			        }catch(e) {
			        	printErr(e,"WadeMobile.callback.error("+callbackKey+")");
			        }
			        if (callbacks[callbackKey]) {
			            delete callbacks[callbackKey];
			        }
			    }else{
			    	alert(message);
			    }
			},storageCallback:function(action,callback,isEscape,isBase64){
				var callbackKey = action+callbackId;
				if (callback) {
		            callbackDefine[callbackKey] = {callback:callback,isEscape:isEscape,isBase64:isBase64};
		        }
			},execCallback:function(callbackKey, data){
				var callbackItem= callbackDefine[callbackKey];
				if (callbackItem) {
					data = data=="null"?null:data;
			        try {
			        	if(data){
			        		if(callbackItem.isEscape==true){
			        			data = unescape(data);
			        		}else if(callbackItem.isBase64==true){
			        			data = Base64.decode64(data);
			        		}
			        	}
			            if (callbackItem.callback) {   
			                if(typeof callbackItem.callback==="function"){
			                	var func = callbackItem.callback;
			                	func(data);
		                	}else{
		                		_eval(callbackItem.callback+"('"+data+"','"+callbackKey+"')");
		                	}
			            }
			        }catch(e) {
			        	printErr(e,"WadeMobile.callback.execCallback("+callbackKey+")");
			        }
			        if (callbackItem) {
			            delete callbackDefine[callbackKey];
			        }
			    }
			}
		};
	})();
	
	WadeMobile.event = (function(){
		if(WadeMobile.isApp()){
			var e = document.createEvent('Events');
			return {
				back:function(){
					e.initEvent('backKeyDown');
					document.dispatchEvent(e);
				},menu:function(){
					e.initEvent('menuKeyDown');
				    document.dispatchEvent(e);
				},search:function(){
					e.initEvent('searchKeyDown');
				    document.dispatchEvent(e);
				}
			};
		}
	})();
	
	/************公共方法**************/
	//打印js错误
	function printErr(e,funcName,msg){
		console.log(funcName+" execute error: "+e);
		var errStr = funcName+"调用错误提示:"+msg+"\t\n" ;
    	for(var p in e){
    		errStr+=p+":"+e[p]+"\n";
    	}
	    alert(errStr);
	};
	//动态执行js方法
	function _eval(code,action){
		if(WadeMobile.debug){
			alert(code);
		}
		try{
			var func = eval(code);
			if(typeof func==="function"){
				func();
			}
		}catch(e){
			printErr(e,"_eval","action:"+action);
		}
	}
	//格式转换方法
	function stringify(args) {
	    if (typeof JSON == "undefined") {
	        var s = "[";
	        for (var i=0; i<args.length; i++) {
	            if (i > 0) {
	                s = s + ",";
	            }
	            var type = typeof args[i];
	            if ((type == "number") || (type == "boolean")) {
	                s = s + args[i];
	            }
	            else if (args[i] instanceof Array) {
	            	s = s + "[" + args[i] + "]";
	            }
	            else if (args[i] instanceof Object) {
	            	var start = true;
	            	s = s + '{';
	            	for (var name in args[i]) {
	            		if (args[i][name] != null) {
		            		if (!start) {
		            			s = s + ',';
		            		}
		            		s = s + '"' + name + '":';
		            		var nameType = typeof args[i][name];
		            		if ((nameType == "number") || (nameType == "boolean")) {
		            			s = s + args[i][name];
		            		}
		            		else if ((typeof args[i][name]) == 'function') {
			           			// don't copy the functions
		            			s = s + '""'; 
		            		}
		            		else if (args[i][name] instanceof Object) {
		            			s = s + stringify(args[i][name]);
		            		}
		            		else {
		                        s = s + '"' + args[i][name] + '"';            			
		            		}
		                    start=false;
		                 }
	            	} 
	            	s = s + '}';
	            }else {
	                var a = args[i].replace(/\\/g, '\\\\');
	                a = a.replace(/"/g, '\\"');
	                s = s + '"' + a + '"';
	            }
	        }
	        s = s + "]";
	        return s;
	    }
	    else {
	        return JSON.stringify(args);
	    }
	};
	
	var ExpandMobile = require("res/js/mobile/expand-mobile.js");
	var BizMobile = require("biz/js/common/biz-mobile.js");
	Wade.extend(WadeMobile,ExpandMobile);//属性合并,ExpandMobile累加到WadeMobile中
	Wade.extend(WadeMobile,BizMobile);//属性合并,BizMobile累加到WadeMobile中
	return WadeMobile;
});