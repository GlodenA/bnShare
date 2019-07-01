define(function() {
	var O = {};
	/**通过id获取对象*/
	O.$ = function(id){
		return document.getElementById(id);
	}
	/**通过name获取对象*/
	O.$N = function(name){
		return document.getElementsByName(name);
	}
	/**通过id获取对象的值或者文本*/
	O.$F = function(id){
		var obj = document.getElementById(id);
		if(obj){
			if(obj.value){
				return obj.value;
			}else if(obj.innerHTML){
				return obj.innerHTML;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}

	/**
	 * 获取对象的所有属性
	 */
	O.getAttrValue = function(obj){
		var s = new Array();
		for(var attr in obj){
			s.push(attr+"——"+obj[attr]);
		}
		return s.join(" ; ");
	};
	
	/**
	 * 改良版的setTimeout,可以传递参数
	 */
	O.setTimeout = function(fn, delaySec) {
		if (arguments.length > 2) {
			var params = new Array()
			for ( var i = 2; i < arguments.length; i++) {
				params[i - 2] = arguments[i];
			}
			setTimeout((function() {
				fn.apply(null, params)
			}), delaySec);
		} else {
			setTimeout(fn, delaySec);
		}
	}

	/**
	 * 获取浏览器所在终端类型
	 */
	O.terminalType = (function() {
		var sUserAgent = navigator.userAgent.toLowerCase();
		var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
		var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
		var bIsAndroid = sUserAgent.match(/android/i) == "android";
		var bIsWinphone = sUserAgent.match(/windows phone /i) == "windows phone "
				|| sUserAgent.match(/windows phone os /i) == "windows phone os ";
		if (bIsAndroid) {
			return "a";
		} else if (bIsIpad || bIsIphoneOs) {
			return "i";
		} else if (bIsWinphone) {
			return "w";
		} else {
			return null;
		}
	})();
	
	O.go = function(url) {
		window.location.href = url;
	}
	
	return O;
});