/**
 * 输入工具类
 * 提供输入输出等数据交互的API,用于提升web开发的效率.
 */
define(["o","zepto"],function(O,$) {
	var oInput = {};
	/*根据前缀批量获取dom元素的value*/
	oInput.getPrefixValues = function(prefix, isPrefix) {
		var values = new Wade.DataMap();
		$("[id^="+prefix+"]").each(function(index, item){
			if(isPrefix){
				values.put(item.id,item.value);
			}else{
				values.put(item.id.replace(prefix,""),item.value);
			}
		});
		return values;
	};
	/*获取指定表单的的所有数据，参数格式 :"#id"，当然也可以是其它css选择器，不过要求返回的为唯一的zepto的form元素。*/
	oInput.getFormValues = function(part) {
		if(typeof part=="string"){
			var arr=$(part).serializeArray();
			var values=new Wade.DataMap();
			$.each(arr,function(index,item){
				values.put(item.name,item.value);
			});
			return values;
		}else
			return "";
	}
	
	/*
	 * require用于判断输入框是否允许为空,区别html5自带的required.值:true|false
	 * datatype用于校验输入框value的数据类型.值:number\account\pspt\email
	 * title用于字段描述,用于错误信息时候的字段提示,如设置placeholder属性,title可以省略
	 */
	oInput.check = function(){
		var msg = null;
		var element = null;
		$("[require],[datatype]").each(function(index, item){
			element = item;
			var value = item.value.trim();
			var title = item.getAttribute("title")||item.placeholder||item.id||item.name;
			var require = item.getAttribute("require");
			if(value==""&&require=="true"){
				msg = title + "不能为空";
				item.focus();
				return false;
			}
			
			var datatype = item.getAttribute("datatype");
			if(!datatype){
				return true;
			}else if(datatype.toLowerCase()=="number"&&checkNumber(value)){
				msg = title + "需要是数字";
				item.focus();
				return false;
			}else if(datatype.toLowerCase()=="account"&&checkAccount(value)){
				msg = title + "需要字母开头,长度5-16位";
				item.focus();
				return false;
			}else if(datatype.toLowerCase()=="pspt"&&checkiDentity(value)){
				msg = title + "身份证号码格出错";
				item.focus();
				return false;
			}else if(datatype.toLowerCase()=="email"&&checkEmail(value)){
				msg = title + "邮箱格式出错";
				item.focus();
				return false;
			}else if((datatype.toLowerCase()=="phonenumber")&&checkPhoneNumber(value)){
				msg = title + "手机号码格式出错";
				item.focus();
				return false;
			}
		});
		if(msg){
			return {getMessage:function(){
				return msg;
			},element:element};
		}else{
			return;
		}
	};
	
	//检查是否全是数字 
	function checkNumber(value){
		  return value.match(/\D/)!=null;
	}
	
	//检查Email地址
	function checkEmail(value){
		var regx=/\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/;
		return !regx.test(value);
	}
	
	//匹配帐号是否合法
	function checkAccount(value){
		 var regx=/^[a-zA-Z][a-zA-Z0-9_]{4,15}$/;
		 return !regx.test(value);
	}
	
	//检查身份证格式
	function checkiDentity(id,isblank){
		var regx=/d{15}|d{18}/;
		return !regx.test(value);
	}
	
	//检查手机号码
	function checkPhoneNumber(value) {
		var regx =  /^([0-9]{11})?$/; 
		return !regx.test(value);
	}
	
	return oInput;
});