
/** eFrame  */
function getFrame(frame) {
	var target = null;
	if (isString(frame)) {
		var array = frame.split(".");
		for (var i=0; i<array.length; i++) {
			if (array[i] == "parent") {
				target = target == null ? parent : target.parent;
			}
		}
		if (target != null) return target;
	}
	
	var tgtrange = window.top;
	if (isArray(frame)) {
		tgtrange = frame[1];
		frame = frame[0];
	}
	var ifrms = tgtrange.frames;
	for (var i=0; i<ifrms.length; i++) {
		var jfrms = ifrms[i].frames;
		if (jfrms.name == frame) return jfrms;
		if (jfrms.length > 0) {
			for (var j=0; j<jfrms.length; j++) {
				var kfrms = jfrms[j].frames;
				if (kfrms.name == frame) return kfrms;
				if (kfrms.length > 0) {
					for (var k=0; k<kfrms.length; k++) {
						if (kfrms[k].name == frame) return kfrms[k];
					}
				}
			}
		}
	}
}
function redirectToByUrl(url, target) {
	getFrame(target).window.location.href = url;
}
/** is array */
function isArray(obj) {
	return (typeof obj == 'object') && obj.constructor == Array; 
}
/** is string */
function isString(str) {
	return (typeof str == 'string') && str.constructor == String;
}
/** is number */
function isNumber(obj) {
	return (typeof obj == 'number') && obj.constructor == Number;
}
/** is date */
function isDate(obj) {
	return (typeof obj == 'object') && obj.constructor == Date;
}
/** is function */
function isFunction(obj) {
	return (typeof obj == 'function') && obj.constructor == Function;
}



/** is object */
function isObject(obj) {
	return (typeof obj == 'object') && obj.constructor == Object;
}




