define(["jcl"],function($) {
	"use strict";
	var toString = Object.prototype.toString;
	var expando = "tap" + (new Date()).valueOf(), uuid = 0;

	var tapHanldersCache = {};
	function addTapHandler(elem, callback){
		var id = elem[expando];
		if(!id){
			id = ++ uuid;
			elem[expando] = id;
		}
		var handlers = tapHanldersCache[id];
		if(!handlers){
			handlers = tapHanldersCache[id] = [];
		}
		handlers.push(callback);
	}

	function getTapHandler(elem){
		var id = elem[expando];
		if(id && tapHanldersCache[id]){
			return tapHanldersCache[id];
		}
		return [];
	}

	var parentByTap = function(node){
		if(!node || !node.nodeType) return;
		var i = 0;
		while((!("tagName" in node) || (!("ontap" in node.attributes) && !getTapHandler(node).length)) && i < 10){
			if(!node.parentNode || 1 != node.parentNode.nodeType)
				break;
			node = node.parentNode;
			i ++;
		}
		
		if("ontap" in node.attributes || getTapHandler(node).length)
			return node;
	};

    var preventAll = function (e) {
        e.preventDefault();
        e.stopPropagation();
    };

	var getXY = function(e){
		var x = e.touchs ? e.touchs[0].pageX : e.clientX;
		var y = e.touchs ? e.touchs[0].pageX : e.clientY;
		return [x, y];
	};

	//event
	var hasTouch     = "ontouchstart" in window, 
		START_EVENT  = hasTouch ? 'touchstart': 'mousedown',
		MOVE_EVENT   = hasTouch ? 'touchmove' : 'mousemove', 
		END_EVENT    = hasTouch ? 'touchend' : 'mouseup',
		CANCEL_EVENT = hasTouch ? 'touchcancel' : 'mouseup';
	
	var distanceAllow = 10;
	var startXY,tapEl,activeTimer;
	
	$(document).ready(function(){
		
		$(document.body).bind(START_EVENT, function(e){
			if (e.originalEvent)
                e = e.originalEvent;
			
			startXY = getXY(e);
			tapEl = parentByTap(e.target);
			if(tapEl){
				activeTimer = setTimeout(function(){
					$(tapEl).addClass("active");
				}, 100);
			}
		});

		$(document.body).bind(MOVE_EVENT, function(e){
			if (e.originalEvent)
                e = e.originalEvent;
			
			var xy = getXY(e);
			if(startXY && (Math.abs(xy[0] - startXY[0]) > distanceAllow 
				|| Math.abs(xy[1] - startXY[1]) > distanceAllow)){
				clearTimeout(activeTimer);
				if(tapEl){
					$(tapEl).removeClass("active");
				}
				startXY = null;
				tapEl = null;
			}
		});

		$(document.body).bind(END_EVENT, function(e){
			if (e.originalEvent)
                e = e.originalEvent;
			
			setTimeout(function(){
				if(tapEl){
					$(tapEl).removeClass("active");
					var strFn = $(tapEl).attr("ontap");
					if(strFn){
						(new Function(strFn)).call(tapEl);
					}
					var handlers = getTapHandler(tapEl);
					for(var i=0;i<handlers.length;i++){
						handlers[i].call(tapEl);
					}
				}
				clearTimeout(activeTimer);
				startXY = null;
				tapEl = null;
			}, 120);
			
		});
	});

	$.fn["tap"] = function(callback) {
		if(typeof(callback) == "string"){
			this.attr("ontap", callback);
		}else if(toString.call(callback) === "[object Function]"){
			this.each(function(){
				addTapHandler(this, callback);
			});
		}
	};
});