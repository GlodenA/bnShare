/**
 * 将一些公共的业务处理放入此对象中 
 */
define(["jcl","mobile","clientTool","layer","jquery"],function(Wade,Mobile,ClientTool,Layer,$,iScroll) {
	var Common = new function(){
		Layer.config({
    			extend: 'extend/layer.ext.js'
		});
		/*调用服务*/
		this.callSvc = function(action,param,callback,isEscape,error){
			var lay=Layer.load();
			param = param ? param : new Wade.DataMap();
			error = error ? error : function(x_code, x_info) {
				
				if(x_code==-100){
					Mobile.openPage("SessionErr");
				}
				alert(action + "请求失败\n错误编码:[" + x_code + "]\n错误信息:" + x_info);
				if("Leave.submitLeave"==action){
					$("#submitLeave").removeAttr("disabled").removeClass("cancel").addClass("sub");
				}
			};
			
			Common.get(function(data){
				if(typeof data == "string"){
					data = new Wade.DataMap(data);
				}
				if(data.get(Common.Constant.SESSION_ID)){
					param.put(Common.Constant.SESSION_ID,data.get(Common.Constant.SESSION_ID));
				}
				if(data.get(Common.Constant.USER_NAME)){
					param.put(Common.Constant.USER_NAME,data.get(Common.Constant.USER_NAME));
				}
				callSvc();
			},[Common.Constant.SESSION_ID,Common.Constant.NAME]);
			
			function callSvc(){
				Mobile.dataRequest(action,param,function(data){
					Layer.close(lay);
					var x_resultcode, x_resultinfo;
					if (data.substring(0, 1) == "{") {
						data = new Wade.DataMap(data);
						x_resultcode = data.get(Common.Constant.X_RESULTCODE);
						x_resultinfo = data.get(Common.Constant.X_RESULTINFO);
					} else if (data.substring(0, 1) == "[") {
						data = new Wade.DatasetList(data);
						x_resultcode = data.get(0).get(Common.Constant.X_RESULTCODE);
						x_resultinfo = data.get(0).get(Common.Constant.X_RESULTINFO);
					}
					if (x_resultcode != "0") {
						error(x_resultcode, x_resultinfo);
						return;
					}
					callback(data);
				}, isEscape, error);
			}
		};
		

		this.openPage = function(action,param){
			var lay=Layer.load();
			param = param ? param : new Wade.DataMap();
			Common.get(function(data){
				if(typeof data == "string"){
					data = new Wade.DataMap(data);
				}
				param.put(Common.Constant.SESSION_ID,data.get(Common.Constant.SESSION_ID));
				
				param.eachKey(function(index,scope)
				{   
					if(typeof(scope)=="string")
					{
						param.put(index,scope.replace("%","\\%25"));
					}
				});
				
				Mobile.openPage(action,param,function(errInfo){
					Layer.close(lay);
					if(typeof errInfo == "string"){
						errInfo = new Wade.DataMap(errInfo);
					}
					if(errInfo.get(Common.Constant.X_RESULTCODE)==-100){
						Mobile.openPage("SessionErr");
					}
				});
			},[Common.Constant.SESSION_ID,Common.Constant.NAME]);
		};
		
        this.showSuccess=function(msg,callback){
            Layer.alert(msg, {
                icon: 1,
                title: false,
                closeBtn: 0,
                btn: ['确定'],
                skin: 'layer-ext-moon',
                yes:callback||function(index){
                	Layer.close(index);
                }
            });
        };
        this.showFail=function(msg){
        	Layer.alert(msg, {
                icon: 2,
                title: false,
                closeBtn: 0,
                btn: ['确定'],
                skin: 'layer-ext-moon'
            });
        };
        
        this.tips=function(content, follow, options){
        	Layer.tips(content, follow, options);
        };
        
        this.confirm=function(content, options, yes, cancel){
        	Layer.confirm(content, options, yes, cancel);
        };
        
        this.close=function(index){
        	Layer.close(index);
        };
		
        this.openWindow = function(content){
        	Layer.open({
                type: 1,
                shift: 2,
                title: false,
                offset: 'rb',
                closeBtn: 0, //不显示关闭按钮
                area: ['100%', '260px'], //宽高
                content: content
            });
        };
        
		//意见反馈和批次号底部显示
	    this.footerClass = function(){
			var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
			if(windowFlow){
				$("#frame_footer").attr("style","position:absolute; bottom:0px; color:#999; width:100%; line-height:150%; border-top:1px solid #ddd; padding-top:10px; padding-bottom:10px; clear:both;");
			}else{
				$("#frame_footer").attr("style","color:#999; width:100%; line-height:150%; border-top:1px solid #ddd; padding-top:10px; padding-bottom:10px; clear:both;");
			}
	    };        
		
		this.openDialog = function(action,param,name,roleClass){
			
			param = param ? param : new Wade.DataMap();
			Common.get(function(data){
				if(typeof data == "string"){
					data = new Wade.DataMap(data);
				}
				param.put(Common.Constant.SESSION_ID,data.get(Common.Constant.SESSION_ID));
				//param.put(Common.Constant.USER_ID,data.get(Common.Constant.USER_ID));
				var url = "mobile?action=" + action+"&data={";
				if(data.get(Common.Constant.SESSION_ID)){
					url+="('SESSION_ID':"+data.get(Common.Constant.SESSION_ID)+"),"
				}
				param.eachKey(function(index,scope)
						{   
					        url+=("'"+index+"':'"+scope+"',");
						});
				url+="}";
					var dialogObj = Layer.open({
						type: 2,
						title: '',
//						maxmin: true,
						maxmin: false,
						shadeClose: true,
						area:roleClass=="ROLE_CLASS"?['350px' , '490px']:['350px' , '300px'],
						content: url
					});
				
				Mobile.setMemoryCache("dialogObj", dialogObj);
				
//				
//				if(navigator.userAgent.indexOf("Chrome") >0 ){
//					var winOption = "height=500px,width=455px,top=50,left=50,toolbar=no,location=yes,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,fullscreen=0";
//					  return  window.open(url,window, winOption);
//					}
//					else
//					{
//					    return window.showModalDialog(url, window, 'dialogHeight:500px; dialogWidth:455px;edge: Raised; center: Yes; help: Yes;scroll:no; resizable: no; status: no;resizable:yes');
//					} 
				},[Common.Constant.SESSION_ID,Common.Constant.NAME]);
				
		};
		this.closeDialog=function(){
			Mobile.getMemoryCache(function(data){
				Layer.close(data)
			},"dialogObj");
		};
		/*分页查询*/
		/*
		  action server_data.xml 配置的action
		  templateAction server_page.xml 配置的action
		  paramsObj 页面的提交参数隐藏域id
		  paginationobj 分页html域id
		  flashareaObj 刷新域id
		  callback 重新给刷新后的域注册js事件
		*/
		this.pagination=function(action,templateAction,paramsObj,paginationobj,flashareaName,callback){
			var param;
			if(null==paramsObj){
				param = Wade.DataMap($("#params").text());
			}else{
				param = Wade.DataMap($("#"+paramsObj).text());
			}
			var paginations;
			if(null==paginationobj){
				paginations =$(".pagination").children();
			}else{
				paginations = $("#"+paginationobj).children();
			}
			var flashareaObj;
			if(flashareaName==null){
				flashareaObj=$("#reflashArea");
			}else{
				flashareaObj=$("#"+flashareaName);
			}
			$.each(paginations,function(index,item){
			var obj = $(item);
			var pagobj = obj.children('a');
	    	var pagIdex = pagobj.attr("pagIdex");
	    	
	    	if(pagIdex){
	    			obj.click(function(){
	    				param.put("ROW_INDEX",pagIdex);
	    				if(templateAction){
							Common.callSvc(action,param,function(data){
								Mobile.getPage(templateAction,data, function(html){
									flashareaObj.html(html);
									//必须重新注册事件
									Common.pagination(action,templateAction,paramsObj,paginationobj,flashareaName,callback);
									if(callback!=null){
										callback(data);
									}
																		
								});
							});
						}else{
							param.put("ROW_INDEX",pagIdex);
							Common.openPage(action,param);
						}
	    			});
	    		}
			});
		};
		this.closeApp = function(){
			if(confirm("确定要退出应用程序吗?")){
				var param = new Wade.DataMap();
				Common.callSvc("CloseApp",param,function(data){
					Mobile.closeApp();
				});
			}
		};
		this.msg=function(content, options, end){
			Layer.msg(content, options, end);
		};
		this.logoutAccount = function(){
			if(confirm("确定登出?")){
				var param = new Wade.DataMap();
				Common.callSvc("CloseApp",param,function(data){
					Common.remove(Common.Constant.SESSION_ID);
					top.location.href = "mobile?action=Index";
				});
			}
		};
		
		this.put = function(key, value) {
			if(!checkMapKey(key)){
				return;
			}
			Mobile.setMemoryCache(key, value);
		};
		this.get = function(callback, key, value) {
			if(!checkArrayKey(key)){
				return;
			}
			Mobile.getMemoryCache(callback, key, value);
		};
		this.remove = function(key) {
			if(!checkArrayKey(key)){
				return;
			}
			Mobile.removeMemoryCache(key);
		};
		this.clear = function() {
		    Mobile.clearMemoryCache();
		};
		this.putLocal = function(key, value) {
			if(!checkMapKey(key)){
				return;
			}
			Mobile.setOfflineCache(key, value);
		};
		this.getLocal = function(callback, key, value) {
			if(!checkArrayKey(key)){
				return;
			}
			Mobile.getOfflineCache(callback, key,value);
		};
		this.removeLocal = function(key) {
			if(!checkArrayKey(key)){
				return;
			}
			Mobile.removeOfflineCache(key);
		};
		this.clearLocal = function() {
			Mobile.clearOfflineCache();
		};
		
		function checkMapKey(key){
			if (!key || (typeof (key) != "string" && !ClientTool.tool.isDataMap(key))) {
				alert(key+"参数类型异常");
				return false;
			} else {
				return true;
			}
		}
		
		function checkArrayKey(key){
			if (!key || (typeof (key) != "string" && !ClientTool.tool.isArray(key))) {
				alert(key+"参数类型异常");
				return false;
			} else {
				return true;
			}
		}
	}
	
	Common.Constant = {
		NAME : "NAME",
		USER_ID : "USER_ID",
		SESSION_ID : "SESSION_ID",
		X_RECORDNUM : "X_RECORDNUM",
		X_RESULTCODE : "X_RESULTCODE",
		X_RESULTINFO : "X_RESULTINFO",
		X_RESULTCAUSE : "X_RESULTCAUSE"
	}
	
	Common.init = function(){
	}
	
	return Common;
});