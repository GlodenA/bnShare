/**
 * util用于将模块化变量全局化,方便使用
 * 如果每个js都需要引入的相同模块,则绑定到window对象即可
 */
define(["jcl","tap","wmUI","wmBase","wmAnimate"],function($) {
	//window.$ = window.Wade = $;
	
	
	var Util =new function()
	{
		this.isEmpty=function(value)
		{
			var type;
            if(value == null) { // 等同于 value === undefined || value === null
                return true;
            }
            type = Object.prototype.toString.call(value).slice(8, -1);
            switch(type) {
            case 'String':
                return !$.trim(value);
            case 'Array':
                return !value.length;
            case 'Object':
                return $.isEmptyObject(value); // 普通对象使用 for...in 判断，有 key 即为 false
            default:
                return false; // 其他对象均视作非空
            }
		};
		
		this.checkMobile=function(mobile)
		{
			  if(mobile.length==0)
		        {
		           alert('请输入手机号码！');
		           return true;
		        }    
		        if(mobile.length!=11)
		        {
		            alert('请输入有效的手机号码！');
		            return true;
		        }
		        
		        var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/; 
		        if(!myreg.test(mobile))
		        {
		            alert('请输入有效的手机号码！');
		            return true;
		        }
		};
		 
		
	};
	

	
	return Util;
	
});