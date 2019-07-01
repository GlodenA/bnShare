define(["domReady!","o","zepto"],function(doc,O,$){
	var oEvent = {};
	
	var isMoved = false;
	var isIn = false; 
	var touchX = 0;
	var touchY = 0;
	var distanceX;
	var distanceY;
	var distanceAllow = 10;
	var currElement;
	var isSwitchIn = false;
	var isEnd = true;
	//event
	hasTouch     = "ontouchstart" in window, 
	START_EVENT  = hasTouch ? 'touchstart': 'mousedown',
	MOVE_EVENT   = hasTouch ? 'touchmove' : 'mousemove', 
	END_EVENT    = hasTouch ? 'touchend' : 'mouseup',
	CANCEL_EVENT = hasTouch ? 'touchcancel' : 'mouseup';
	
	var classEvent = {
		start:function(obj,cn){
			obj.className = cn + " active";
		},move:function(obj,cn){
			obj.className = cn;
		},end:function(obj,cn){
			obj.className = cn;
		},cancel:function(obj,cn){
			obj.className = cn;
		}
	}
	//点击事件
	oEvent.tap = function(obj, fn) {
		obj = typeof(obj)=="string"?doc.getElementById(obj):obj;
		var iClass = obj.className;
		var objMoveEventFunc = function() {
			if(isIn){
				if(classEvent&&classEvent["move"]){
					if (Math.abs(distanceX) > distanceAllow || Math.abs(distanceY) > distanceAllow) {
						classEvent.move(obj,iClass);
					}
				}
			}
		};
		
		var params;
		if (arguments.length > 2) {
			params = new Array()
			for ( var i = 2; i < arguments.length; i++) {
				params[i - 2] = arguments[i];
			}
		}
		
		var objEndEventFunc = function (){
			setTimeout(function() {
				if(classEvent&&classEvent["end"]){
					classEvent.end(obj,iClass);
				}
			}, 120)
			var delaySec = 120;
			if (!isMoved && fn) {
				fn.eventSrc=event.srcElement;
				if(params){
					setTimeout((function() {
						fn.apply(null, params);
						iClass = obj.className;
					}), delaySec);
				} else {
					setTimeout((function(){
						fn();
						iClass = obj.className;
					}), delaySec);
				}
			}
		};
		
		obj.addEventListener(START_EVENT, (function() {
			if(isEnd) {
				currElement = obj;
				iClass = obj.className;
				isEnd = false;
				setTimeout(function(){
					isEnd = true;
				}, 200)
				setTimeout(function() {
					if (!isMoved) {
						if(classEvent&&classEvent["start"]){
							classEvent.start(obj,iClass);
						}
					};
				}, 100)
				document.body.addEventListener(MOVE_EVENT, objMoveEventFunc);
				obj.addEventListener(END_EVENT, objEndEventFunc);
				obj.resetEvent = function(){
					if(event.srcElement != obj) { obj.className = iClass;}//防止移出了 obj 但是不弹起
					document.body.removeEventListener(MOVE_EVENT,objMoveEventFunc);
					obj.removeEventListener(END_EVENT, objEndEventFunc);
				}
			}
		}));
	}
	
	// 公共事件绑定
	var docMoveEventFunc = function(){
		window.event.preventDefault();
		distanceX = (event.clientX || event.touches[0].clientX) - touchX;
		distanceY = (event.clientY || event.touches[0].clientY) - touchY;
		if ( Math.abs(distanceX)> distanceAllow || Math.abs(distanceY)> distanceAllow) {
			isIn = true;
			isMoved = true;
		}
	};
	if(document.readyState=="complete") {
		document.body.addEventListener(START_EVENT,(function(){
			touchX = event.clientX || event.touches[0].clientX;
			touchY = event.clientY || event.touches[0].clientY;
			document.body.addEventListener(MOVE_EVENT,docMoveEventFunc);
		}), false);
		document.body.addEventListener(END_EVENT,  (function(){
			setTimeout(function() {
				isIn = false;
				isMoved = false;
			}, 100)
			if(currElement&&currElement.resetEvent){currElement.resetEvent();}
			document.body.removeEventListener(MOVE_EVENT,docMoveEventFunc);
		}), false);
	}
	
	/**************tap事件初始化****************/
	/*
	 * ontap触发事件
	 * tapfor聚焦输入框焦点
	 */
	oEvent.initTap = function(){
		$("[ontap],[tapfor]").each(function(index, item){
			ontap = item.getAttribute("ontap");
			if(ontap){
				oEvent.tap(item,(function(fnStr){
					(new Function(fnStr))();
				}),ontap);
			}
			tapfor = item.getAttribute("tapfor");
			if(tapfor){
				oEvent.tap(item,(function(id){
					O.$(id).focus();
				}),tapfor);
			}
		});
	};
	oEvent.initHref = function(){
		$("[href]").each(function(index, item){
			oEvent.tap(item,O.go,item.getAttribute("href"));
		});
	};
	
	$.fn.tap = function(fn){
		alert("tap方法已经废弃");
		return oEvent.tap(this.get(0),fn);
	};
	return oEvent;
});
