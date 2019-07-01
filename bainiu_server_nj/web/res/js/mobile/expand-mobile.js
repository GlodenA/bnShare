/**
 * 提供给外围自行扩展和终端交互的js API。 
 */
define(["require"],function(require) {
	
	var ExpandMobile = (function(){
		return{
			loadingStart:function(message,title,cancelable,err){ //加载进度条
				execute("loadingStart", [message,title,cancelable],err);
			},loadingStop:function(err){ //结束进度条
				execute("loadingStop", [],err);
			},tip:function(msg,type,err){
				if(type==undefined){
					type = 0;//0-短提示,-1长提示
				}
				execute("tip", [msg,type],err);
			},getDate:function(callback,date,format,err){
				if(format==undefined){
					format = "yyyy-MM-dd";
				}
				storageCallback("getDate",callback);
				execute("getDate", [date,format],err);
			},getPhoto:function(callback,type,err){//获取照片
				if(type==undefined){
					type = 1;//0-Base64编码的字符串 1- 文件路径
				}
				storageCallback("getPhoto",callback);
				execute("getPhoto", [type],err);
			},getPicture:function(callback,type,err){//获取照片
				if(type==undefined){
					type = 1;//0-Base64编码的字符串 1- 文件路径
				}
				storageCallback("getPicture",callback);
				execute("getPicture", [type],err);
			},getBase64Picture:function(callback,path,err){
				storageCallback("getBase64Picture",callback);
				execute("getBase64Picture", [path],err);
			},getCompressPicture:function(callback,path,fileSize,quality,err){
				if(fileSize==undefined){
					fileSize = 10;//压缩到10K左右大小
				}
				if(quality==undefined){
					quality = 30;//图片质量30
				}
				storageCallback("getCompressPicture",callback);
				execute("getCompressPicture", [path,fileSize,quality],err);
			},beep:function(count,err){
				execute("beep", [count],err);
			},shock:function(time,err){
				execute("shock", [time],err);
			},call:function(sn,autoCall,err){
				if(autoCall==undefined){
					autoCall=1;//0-跳转至拨打界面,1-直接拨打
				}
				execute("call", [sn,autoCall],err);
			},sms:function(sn,msg,autoSms,err){
				if(autoSms==undefined){
					autoSms=0;//0-跳转至短信界面,1-直接短信
				}
				execute("sms", [sn,msg,autoSms],err);
			},openApp:function(appId,urlParams,installUrl,err){
				execute("openApp", [appId,urlParams,installUrl],err);
			},showKeyBoard:function(type,err){
				execute("showKeyBoard", [type],err);
			},hideKeyBoard:function(err){
				execute("hideKeyBoard", [],err);
			},setTitleView:function(title,err){
				execute("setTitleText", [title],err);
			},getSysInfo:function(callback,key,err){//TELNUMBER|IMEI|IMSI|SDKVERSION|OSVERSION|PLATFORM|SIMNUMBER
				storageCallback("getSysInfo",callback);
				execute("getSysInfo", [key],err);
			},getNetInfo:function(callback,key,err){//MAC|IP
				storageCallback("getNetInfo",callback);
				execute("getNetInfo", [key],err);
			},getDirection:function(callback,type,err){//APP_FILES|APP_CACHE|SDCARD_APP_PATH|SDCARD_ROOT|SDCARD_APP_FILES
				storageCallback("getDirection",callback);
				execute("getDirection", [type],err);
			},explorer:function(callback,fileType,initPath){
				storageCallback("explorer",callback);
				execute("explorer",[callback,fileType,initPath]);
			},httpDownloadFile : function(targetFilePath,fileName,callback,suc,err){//客户端直接访问服务端进行下载
				storageCallback("httpDownloadFile",callback);
				execute("httpDownloadFile",[targetFilePath,fileName],suc,err);
			},location:function(callback,err){
				storageCallback("location",callback);
				execute("location",[],err);
			},markMap:function(callback,markParam,isSelect,isJump,isEscape,err){
				if (typeof(markParam)=="object" && (markParam instanceof Wade.DataMap)) {
					var markParams = new Wade.DatasetList();
					markParams.add(markParam);
					markParam = markParams;
				}
				storageCallback("markMap", callback, isEscape);
				execute("markMap", [markParam.toString(),isSelect,isJump,isEscape,err]);
			},selectLocation:function(callback,isLocation,longitude,latitude,scale){
				storageCallback("selectLocation",callback);
				execute("selectLocation",[isLocation, longitude, latitude, scale]);
			},scanQrCode:function(callback){
				storageCallback("scanQrCode",callback);
				execute("scanQrCode",[]);
			},createQrCode:function(callback,content){
				storageCallback("createQrCode",callback);
				execute("createQrCode",[content]);
			},httpGet:function(callback,url,isEscape,encode){
				storageCallback("httpGet",callback,isEscape);
				execute("httpGet",[url,isEscape,encode]);
			},removeMemoryCache:function(key,err){
				execute("removeMemoryCache",[key],err);
			},clearMemoryCache:function(err){
				execute("clearMemoryCache",[],err);
			},setMemoryCache:function(key,value,err){
				execute("setMemoryCache",[key,value],err);
			},getMemoryCache:function(callback,key,defValue,err){
				storageCallback("getMemoryCache",callback);
				execute("getMemoryCache",[key,defValue],err);
			},setOfflineCache:function(key,value,err){
				execute("setOfflineCache", [key,value],err);
			},getOfflineCache:function(callback,key,defValue,err){
				storageCallback("getOfflineCache",callback);
				return execute("getOfflineCache", [key,defValue],err);
			},removeOfflineCache:function(key,err){
				execute("removeOfflineCache", [key],err);
			},clearOfflineCache:function(err){
				execute("clearOfflineCache", [],err);
			},writeFile:function(content,fileName,type,err){
				if(type==undefined ){
					type =1;
				}
				execute("writeFile",[content,fileName,type],err);
			},appendFile:function(content,fileName,type,err){
				if(type==undefined){
					type =1;
				}
				execute("appendFile",[content,fileName,type],err);
			},readFile:function(callback,fileName,type,isEscape,err){
				if(type==undefined){
					type =1;
				}
				if(isEscape==undefined){
					isEscape=true;
				}
				storageCallback("readFile",callback,isEscape);
				execute("readFile",[fileName,type,isEscape],err);
			},openFile:function(filename,flag,err){
				if(flag==undefined){
					flag =1;
				}
				execute("openFile", [filename,flag],err);
			},deleteFile:function(filename,flag,err){
				if(flag==undefined){
					flag =1;
				}
				execute("deleteFile", [filename,flag],err);
			},getAllFile:function(callback,type,err){
				if(type==undefined){
					type=1;
				}
				storageCallback("getAllFile", callback);
				execute("getAllFile", [type],err);
			},cleanResource:function(type,err){
				execute("cleanResource",[type],err);
			},shareByBluetooth:function(err){
				execute("shareByBluetooth", [],err);
			},audioRecord:function(callback,auto,err){
				if(auto == undefined) {
					auto = false; //false-按住录音,true-自动录音
				}
				storageCallback("audioRecord",callback);
				execute("audioRecord", [auto],err);
			},audioPlay:function(audioPath,hasRipple,err){
				if (hasRipple == undefined) {
					hasRipple = true; //true-弹出波纹,false-无效果
				}
				execute("audioPlay",[audioPath,hasRipple],err);
			},logCat:function(msg,title,err){
				//将日志输出至LogCat控制台（异步）
				execute("logCat",[msg,title],err);
			},execSQL:function(dbName,sql,bindArgs,callback,err){
				storageCallback("execSQL",callback);
				execute("execSQL",[dbName,sql,bindArgs],err);
			},insert:function(dbName,table,datas,callback,err){
				storageCallback("insert",callback);
				execute("insert",[dbName,table,datas],err);
			},delete:function(dbName,table,datas,callback,err){
				storageCallback("delete",callback);
				execute("delete",[dbName,table,datas],err);
			},update:function(dbName,table,datas,conds,callback,err){
				storageCallback("update",callback);
				execute("update",[dbName,table,datas,conds],err);
			},select:function(dbName,table,columns,conds,callback,err){
				storageCallback("select",callback);
				execute("select",[dbName,table,columns,conds],err);
			},selectFirst:function(dbName,table,columns,conds,callback,err){
				storageCallback("selectFirst",callback);
				execute("selectFirst",[dbName,table,columns,conds],err);
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
	
	return ExpandMobile;
});