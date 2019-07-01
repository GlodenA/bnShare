/*!
 * WADE JavaScript Library v2.0 for zepto
 * http://www.wadecn.com/
 * auth:xiedx@asiainfo-linkage.com
 * Copyright 2014, WADE
 */
define(["zepto"],function($){
	var rtrim = /^(\s|\u00A0)+|(\s|\u00A0)+$/g,
	push=Array.prototype.push;
	/**
	* 数据总线相关
	**/	 
	var escapeRe=function (d){
		return d.replace(/([-.*+?^${}()|[\]\/\\])/g,"\\$1");
	};
	var support = {
		scriptEval: false
	};
	var toString = Object.prototype.toString;

	$.Collection = function(allowFunctions, keyFn){
	    this.items = [];
	    this.map = {};
	    this.keys = [];
	    this.length = 0; 
	    this.allowFunctions = allowFunctions === true;
	    if(keyFn){
	        this.getKey = keyFn;
	    }
	};
	
	$.Collection.prototype={
	    allowFunctions : false,
	    add : function(key, o){
	        if(arguments.length == 1){
	            o = arguments[0];
	            key = this.getKey(o);
	        }
	        if(typeof key != 'undefined' && key !== null){
	            var old = this.map[key];
	            if(typeof old != 'undefined'){
	                return this.replace(key, o);
	            }
	            this.map[key] = o;
	        }
	        this.length++;
	        this.items.push(o);
	        this.keys.push(key);
	        return o;
	    },
	    getKey : function(o){
	         return o.id;
	    },
	    replace : function(key, o){
	        if(arguments.length == 1){
	            o = arguments[0];
	            key = this.getKey(o);
	        }
	        var old = this.map[key];
	        if(typeof key == 'undefined' || key === null || typeof old == 'undefined'){
	             return this.add(key, o);
	        }
	        var index = this.indexOfKey(key);
	        this.items[index] = o;
	        this.map[key] = o;
	        return o;
	    },
	    addAll : function(objs){
	        if(arguments.length > 1 || $.isArray(objs)){
	            var args = arguments.length > 1 ? arguments : objs;
	            for(var i = 0, len = args.length; i < len; i++){
	                this.add(args[i]);
	            }
	        }else{
	            for(var key in objs){
	                if(this.allowFunctions || typeof objs[key] != 'function'){
	                    this.add(key, objs[key]);
	                }
	            }
	        }
	    },
	    each : function(fn, scope){
	        var items = [].concat(this.items); // each safe for removal
	        for(var i = 0, len = items.length; i < len; i++){
	            if(fn.call(scope || items[i], items[i], i, len) === false){
	                break;
	            }
	        }
	    },
	    eachKey : function(fn, scope){
	        for(var i = 0, len = this.keys.length; i < len; i++){
	            fn.call(scope || window, this.keys[i], this.items[i], i, len);
	        }
	    },
	    find : function(fn, scope){
	        for(var i = 0, len = this.items.length; i < len; i++){
	            if(fn.call(scope || window, this.items[i], this.keys[i])){
	                return this.items[i];
	            }
	        }
	        return null;
	    },
	    insert : function(index, key, o){
	        if(arguments.length == 2){
	            o = arguments[1];
	            key = this.getKey(o);
	        }
	        if(this.containsKey(key)){
	            this.suspendEvents();
	            this.removeKey(key);
	            this.resumeEvents();
	        }
	        if(index >= this.length){
	            return this.add(key, o);
	        }
	        this.length++;
	        this.items.splice(index, 0, o);
	        if(typeof key != 'undefined' && key !== null){
	            this.map[key] = o;
	        }
	        this.keys.splice(index, 0, key);
	        return o;
	    },
	    remove : function(o){
	        return this.removeAt(this.indexOf(o));
	    },
	    removeAt : function(index){
	        if(index < this.length && index >= 0){
	            this.length--;
	            var o = this.items[index];
	            this.items.splice(index, 1);
	            var key = this.keys[index];
	            if(typeof key != 'undefined'){
	                delete this.map[key];
	            }
	            this.keys.splice(index, 1);
	            return o;
	        }
	        return false;
	    },
	    removeKey : function(key){
	        return this.removeAt(this.indexOfKey(key));
	    },
	    getCount : function(){
	        return this.length;
	    },
	    indexOf : function(o){
	        return $.inArray(o,this.items);
	    },
	    indexOfKey : function(key){
	        return $.inArray(key,this.keys);
	    },
	    item : function(key){
	        var mk = this.map[key],
	            item = mk !== undefined ? mk : (typeof key == 'number') ? this.items[key] : undefined;
	        return typeof item != 'function' || this.allowFunctions ? item : null; // for prototype!
	    },
	    itemAt : function(index){
	        return this.items[index];
	    },
	    key : function(key){
	        return this.map[key];
	    },
	    contains : function(o){
	        return this.indexOf(o) != -1;
	    },
	    containsKey : function(key){
	        return typeof this.map[key] != 'undefined';
	    },
	    clear : function(){
	        this.length = 0;
	        this.items = [];
	        this.keys = [];
	        this.map = {};
	    },
	    first : function(){
	        return this.items[0];
	    },
	    last : function(){
	        return this.items[this.length-1];
	    },
	    _sort : function(property, dir, fn){
	        var i, len,
	            dsc   = String(dir).toUpperCase() == 'DESC' ? -1 : 1,
	
	            //this is a temporary array used to apply the sorting function
	            c     = [],
	            keys  = this.keys,
	            items = this.items;
	
	        //default to a simple sorter function if one is not provided
	        fn = fn || function(a, b) {
	            return a - b;
	        };
	
	        //copy all the items into a temporary array, which we will sort
	        for(i = 0, len = items.length; i < len; i++){
	            c[c.length] = {
	                key  : keys[i],
	                value: items[i],
	                index: i
	            };
	        }
	
	        //sort the temporary array
	        c.sort(function(a, b){
	            var v = fn(a[property], b[property]) * dsc;
	            if(v === 0){
	                v = (a.index < b.index ? -1 : 1);
	            }
	            return v;
	        });
	
	        //copy the temporary array back into the main this.items and this.keys objects
	        for(i = 0, len = c.length; i < len; i++){
	            items[i] = c[i].value;
	            keys[i]  = c[i].key;
	        }
	    },
	    sort : function(dir, fn){
	        this._sort('value', dir, fn);
	    },
	    reorder: function(mapping) {
	        this.suspendEvents();
	
	        var items     = this.items,
	            index     = 0,
	            length    = items.length,
	            order     = [],
	            remaining = [];
	
	        //object of {oldPosition: newPosition} reversed to {newPosition: oldPosition}
	        for (oldIndex in mapping) {
	            order[mapping[oldIndex]] = items[oldIndex];
	        }
	
	        for (index = 0; index < length; index++) {
	            if (mapping[index] == undefined) {
	                remaining.push(items[index]);
	            }
	        }
	
	        for (index = 0; index < length; index++) {
	            if (order[index] == undefined) {
	                order[index] = remaining.shift();
	            }
	        }
	
	        this.clear();
	        this.addAll(order);
	
	        this.resumeEvents();
	    },
	    keySort : function(dir, fn){
	        this._sort('key', dir, fn || function(a, b){
	            var v1 = String(a).toUpperCase(), v2 = String(b).toUpperCase();
	            return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
	        });
	    },
	    getRange : function(start, end){
	        var items = this.items;
	        if(items.length < 1){
	            return [];
	        }
	        start = start || 0;
	        end = Math.min(typeof end == 'undefined' ? this.length-1 : end, this.length-1);
	        var i, r = [];
	        if(start <= end){
	            for(i = start; i <= end; i++) {
	                r[r.length] = items[i];
	            }
	        }else{
	            for(i = start; i >= end; i--) {
	                r[r.length] = items[i];
	            }
	        }
	        return r;
	    },
	    filter : function(property, value, anyMatch, caseSensitive){
	        if(!value){
	            return this.clone();
	        }
	        value = this.createValueMatcher(value, anyMatch, caseSensitive);
	        return this.filterBy(function(o){
	            return o && value.test(o[property]);
	        });
	    },
	    filterBy : function(fn, scope){
	        var r = new $.Collection();
	        r.getKey = this.getKey;
	        var k = this.keys, it = this.items;
	        for(var i = 0, len = it.length; i < len; i++){
	            if(fn.call(scope||this, it[i], k[i])){
	                r.add(k[i], it[i]);
	            }
	        }
	        return r;
	    },
	    findIndex : function(property, value, start, anyMatch, caseSensitive){
	        if(!value){
	            return -1;
	        }
	        value = this.createValueMatcher(value, anyMatch, caseSensitive);
	        return this.findIndexBy(function(o){
	            return o && value.test(o[property]);
	        }, null, start);
	    },
	    findIndexBy : function(fn, scope, start){
	        var k = this.keys, it = this.items;
	        for(var i = (start||0), len = it.length; i < len; i++){
	            if(fn.call(scope||this, it[i], k[i])){
	                return i;
	            }
	        }
	        return -1;
	    },
	    createValueMatcher : function(value, anyMatch, caseSensitive, exactMatch) {
	        if (!value.exec) { // not a regex
	            value = String(value);
	
	            if (anyMatch === true) {
	                value = escapeRe(value);
	            } else {
	                value = '^' + escapeRe(value);
	                if (exactMatch === true) {
	                    value += '$';
	                }
	            }
	            value = new RegExp(value, caseSensitive ? '' : 'i');
	         }
	         return value;
	    },
	    clone : function(){
	        var r = new $.Collection();
	        var k = this.keys, it = this.items;
	        for(var i = 0, len = it.length; i < len; i++){
	            r.add(k[i], it[i]);
	        }
	        r.getKey = this.getKey;
	        return r;
	    }
	};
	
	/*
	* 扩展常用方法
	* isArray|isPlainObject|isEmptyObject,三个方法zepto已经存在
	*/
	$.extend($,{
		noop:function(){},
		error:function( msg ) {
			throw msg;
		},
		isNumber:function(value) {
            return typeof value === 'number' && isFinite(value);
        },
        isNumeric: function(value) {
            return !isNaN(parseFloat(value)) && isFinite(value);
        },
        isString: function(value) {
            return typeof value === 'string';
        },
        isBoolean: function(value) {
            return typeof value === 'boolean';
        },
		isFunction:function(obj){
			return toString.call(obj) === "[object Function]";
		},
		/*isArray:function(obj) {
			return toString.call(obj) === "[object Array]";
		},*/
		//判断是否为object类型的对象
		isObject:function(obj){
			return toString.call(obj) === "[object Object]";
		},
		//判断是否为JSON对象，而不是function的实例
		/*isPlainObject:function(obj){
			//先对数据类型进行判断，并排除DOM类型的对象
			if (!obj || toString.call(obj)!=="[object Object]" || obj.nodeType || obj.setInterval){
				return false;
			}
			//如果有构造方法的属性，那么则是function的实例
			if (obj.constructor
				&& !hasOwnProperty.call(obj, "constructor")
				&& !hasOwnProperty.call(obj.constructor.prototype, "isPrototypeOf") ) {
				return false;
			}
			//判断最后一个属性是否是OwnProperty，而不是prototype里的属性
			var key;
			for(key in obj){}
			//返回判断结果
			return key === undefined || hasOwnProperty.call(obj, key);
		},*/
		//判断是否为没有任何属性的对象
		isEmptyObject:function(obj){
			for (var name in obj) {
				return false;
			}
			return true;
		},
        isElement: function(value) {
            return value ? value.nodeType !== undefined : false;
        },
        isNodeName:function(elem,name){
        	return $.nodeName(elem,name);
        },		
		//判定nodeName
		nodeName:function(elem, name){
			return elem.nodeName && elem.nodeName.toUpperCase()=== name.toUpperCase();
		},
		isWindow: function( obj ) {
			return obj && typeof obj === "object" && "setInterval" in obj;
		},
		trim: function(text) {
			return (text || "").replace(rtrim,"");
		},
		//判断item是否在数组array中
		inArray: function(item, array){
			if(array.indexOf){
				return array.indexOf(item);
			}
			//遍历array进行判定
			for(var i=0,length=array.length;i<length;i++){
				if(array[i]===item){
					return i;
				}
			}
			return -1;
		},
		makeArray:function(array, results){
			var ret = results || [];
			if ( array != null ) {
				//如果arry不是数组类型,则直接将array push到ret中
				if(array.length == null || typeof array === "string" || $.isFunction(array) || (typeof array !== "function" && array.setInterval)){
					push.call(ret, array);
				}else{
					$.merge(ret, array);
				}
			}
			return ret;
		},
		//复制数组对象
		merge:function(first, second) {
			var i = first.length, j = 0;
			if(typeof second.length === "number"){
				for(var l = second.length; j<l; j++) {
					first[i++]=second[j];
				}
			}else{
				while(second[j]!==undefined){
					first[i++]=second[j++];
				}
			}
			first.length = i;
			return first;
		},
		grep: function( elems, callback, inv ) {
			var ret = [];
			//遍历元素数组，在每个元素上执行callback，并返回执行结果有返回值不为false的结果集
			for ( var i = 0, length = elems.length; i < length; i++ ) {
				if ( !inv !== !callback( elems[ i ], i ) ) {
					ret.push( elems[ i ] );
				}
			}
			return ret;
		}, 
		map: function( elems, callback, arg) {
			var ret = [], value;
	
			//遍历元素数组， 在每个元素上执行callback，并返回所有的执行结果
			for ( var i = 0, length = elems.length; i < length; i++ ) {
				value = callback( elems[ i ], i, arg );
	
				if ( value != null ) {
					ret[ ret.length ] = value;
				}
			}
			return ret.concat.apply( [], ret );
		},		
		each:function( object, callback, args ) {
			var name, i = 0,
				length = object.length,
				isObj = length === undefined || $.isFunction(object);

			if (args){
				if (isObj) {
					for (name in object){
						if (callback.apply(object[name], args)===false){
							break;
						}
					}
				}else{
					for(;i<length;){
						if(callback.apply(object[i++],args)===false){
							break;
						}
					}
				}
			}else{
				if(isObj){
					for(name in object){
						if (callback.call(object[name],name,object[name])===false){
							break;
						}
					}
				}else{
					for(var value=object[0];
						i<length && callback.call(value,i,value)!==false;value=object[++i]){}
				}
			}
			return object;
		},
		parseJSON: function( data ) {
			if ( typeof data !== "string" || !data ) {
				return null;
			}
			//去除首尾空格
			data = $.trim(data);
			//使用正则表达式确保传入的字符串是json
			if ( /^[\],:{}\s]*$/.test(data.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, "@")
				.replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, "]")
				.replace(/(?:^|:|,)(?:\s*\[)+/g, "")) ) {

				//先尝试使用浏览器内部JSON解析器
				return window.JSON && window.JSON.parse ?
					window.JSON.parse( data ) :
					(new Function("return " + data))();
			} else {
				$.error( "Invalid JSON: " + data );
			}
		},
		globalEval: function(data){
			if (data && rnotwhite.test(data)){
				var head = document.getElementsByTagName("head")[0] || document.documentElement,
					script = document.createElement("script");

				script.type = "text/javascript";

				if (support.scriptEval ) {
					script.appendChild( document.createTextNode( data ) );
				} else {
					script.text = data;
				}
				head.insertBefore( script, head.firstChild );
				head.removeChild( script );
			}
		},
		//UserAgent检测
		uaMatch: function(ua) {
			ua = ua.toLowerCase();
			var match = /(webkit)[ \/]([\w.]+)/.exec(ua) ||
				/(opera)(?:.*version)?[ \/]([\w.]+)/.exec(ua) ||
				/(msie) ([\w.]+)/.exec(ua) ||
				!/compatible/.test(ua) && /(mozilla)(?:.*? rv:([\w.]+))?/.exec(ua) ||
				/(gecko)[ \/]([^\s]*)/i.exec(ua) || 
				[];

			return {browser: match[1] || "", version: match[2] || "0" };
		},	
		browser: {}
	});

	$.extend($,{
		parseJsonString:function(str){ //过滤Json串中特殊字符以构造Json
			if(!str || !typeof(str)=="string"){return "";}
			
			str=str.replace(/([:{}\[\]\"])[\s|\u00A0]+/g,"$1"); //替换 :{}[]" 符号的右侧空格
			str=str.replace(/[\s|\u00A0]+([:{}\[\]\"])/g,"$1"); //替换 :{}[]" 符号的左侧空格

			//str=str.replace(/,([^\":{}\[\]]+):([\"{\[]{1})/g,",\"$1\":$2");   //把JSON串中无引号的键名加上双引号(1:处理,xxx:("|{|[))
			//str=str.replace(/\{([^\":{}\[\]]+):([\"{\[]{1})/g,"{\"$1\":$2");  //把JSON串中无引号的键名加上双引号(2:处理{xxx:("|{|[))
		   
			str=str.replace(/,([^\":{}\[\]]+):\"/g,",\"$1\":\"");  //把JSON串中无引号的键名加上双引号(1:处理 ,xxx:")
			str=str.replace(/,([^\":{}\[\]]+):\{/g,",\"$1\":{");   //把JSON串中无引号的键名加上双引号(1:处理 ,xxx:{)
			str=str.replace(/,([^\":{}\[\]]+):\[/g,",\"$1\":[");   //把JSON串中无引号的键名加上双引号(1:处理 ,xxx:[)
		
			str=str.replace(/\{([^\":{}\[\]]+):\"/g,"{\"$1\":\""); //把JSON串中无引号的键名加上双引号(1:处理 {xxx:")
			str=str.replace(/\{([^\":{}\[\]]+):\{/g,"{\"$1\":{");  //把JSON串中无引号的键名加上双引号(1:处理 {xxx:{)
			str=str.replace(/\{([^\":{}\[\]]+):\[/g,"{\"$1\":[");  //把JSON串中无引号的键名加上双引号(1:处理 {xxx:[)
		
			str=str.replace(/\\\":(null|undefined)(,|})/g,"\\\":\\\"\\\"$2"); //处理KEY值里放IData或IDataset的toString串里的空值
			str=str.replace(/\\\":(true|false)(,|})/g,"\\\":\\\"$1\\\"$2"); //处理KEY值里放IData或IDataset的toString串里的布尔值
			str=str.replace(/\\\":(-)?([0-9\.]+)(,|})/g,"\\\":\\\"$1$2\\\"$3"); //处理KEY值里放IData或IDataset的toString串里的数值
			str=str.replace(/\\\"/g,"!~b~!"); //把字符串中原有的 \" 替换，处理KEY值里放IData或IDataset的toString串的情况
			
			str=str.replace(/:(null|undefined)(,|})/g,":\"\"$2");    //将null或undefined替换为空字符
			str=str.replace(/:(true|false)(,|})/g,":\"$1\"$2");      //将true|false替换为字符串
			str=str.replace(/:(-)?([0-9\.]+)(,|})/g,":\"$1$2\"$3");  //将数字替换为字符串
			
			//str=str.replace(datafilter,"");    //过滤允许字符之外的字符
			//str=str.replace(datareplacer,"$1:$3"); //处理bude串的=号替换为:号
			
			/*
			str=str.replace(/\r|\t|\n|\"/g,function(m){
					switch(m){
						case "\r":m="\u005Cr";break;
						case "\t":m="\u005Ct";break;
						case "\n":m="\u005Cn";break;
						case "\"":m="!~a~!";break;
					}return m;});  //处理回车换行制表符
			*/
			var out = "";
			for(var i=0;i<str.length;i++){
				var chr = str.charAt(i);
				switch (chr){
					case "\b":
						out +="\u005Cb";
						break;
					case "\f":
						out +="\u005Cf";
						break;
					case "\r":
						out +="\u005Cr";
						break;
					case "\t":
						out +="\u005Ct";
						break;
					case "\n":
						out +="\u005Cn";
						break;
					case "\"":
						out +="!~a~!";
						break;
					default:
						out += chr;
					break;
				}
			}
			str = out;
		
			//第一次替换处理JSON格式的双引号字符
			str=str.replace(/{!~a~!/g,"{\""); //处理 {"
			str=str.replace(/!~a~!}/g,"\"}");  //处理 "}
			str=str.replace(/!~a~!,!~a~!/g,"\",\"");   //处理 ","
			str=str.replace(/!~a~!:!~a~!/g,"\":\"");   //处理 ":"
			str=str.replace(/!~a~!:\[/g,"\":[");   //处理 ":[
			str=str.replace(/\],!~a~!/g,"],\"");   //处理 ],"
			str=str.replace(/!~a~!:{/g,"\":{");    //处理 ":{
			str=str.replace(/},!~a~!/g,"},\"");    //处理 },"
		
			//第二次替换处理其它的双引号
			str=str.replace(/\u005C!~a~!/g,"\u005C\""); //本身就有\的还原
			str=str.replace(/!~a~!/g,"\u005C\"");    //其它的加上\符号
			str=str.replace(/!~b~!/g,"\u005C\"");   //恢复 !~b~! 为 \"
			
			//str=str.replace(/\r/g,"\u005Cr");		
			//str=str.replace(/\n/g,"\u005Cn");	
			//str=str.replace(/([^,:\{\[])(\")([^,:\}\]])/g,"$1\u005C$2$3"); //处理json串值中的双引号
			//str=str.replace(/([^,\\:\{\[])(\")([^,\\:\}\]])/g,"$1\u005C$2$3"); //处理json串值中的双引号(两个双引号连一起的特殊情况)
			//str=str.replace(/\"(\")([^,:\}\]])/g,"\"\u005C$1$2"); //xiedx 2010-7-14 //处理json串值中的双引号(多个双引号连一起的特殊情况)
			//str=str.replace(/\"\,([^\"{\[])/g,"\u005C\"\,$1");   //处理字符串json值中有双引号和逗号在一起的情况(",)
			//str=str.replace(/([^\"}:\]])\,\"/g,"$1\,\u005C\"");  //处理字符串json值中有双引号和逗号在一起的情况(,")
			//str=str.replace(/\":\u005C\",/g,"\":\",");	//处理与值结束引号连在一起的双引号
			//str=str.replace(/,\u005C\"([\}\]]|(,\"))/g,",\"$1"); //处理与值结束引号连在一起的双引号
			return str;
		},
		parseJsonValue:function(str){ //替换json对象值中的特殊字符
			if(!str || !typeof(str)=="string"){return str;}
		
			var out = "";
			for(var i=0;i<str.length;i++){
				var chr = str.charAt(i);
				switch (chr){
					case "\b":
						out +="\u005Cb";
						break;
					case "\f":
						out +="\u005Cf";
						break;
					case "\r":
						out +="\u005Cr";
						break;
					case "\t":
						out +="\u005Ct";
						break;
					case "\n":
						out +="\u005Cn";
						break;
					case "\"":
						out +="\u005C\"";
						break;
					default:
						out += chr;
					break;
				}
			}
			return out;
		}
	});	
	
	$.DataMap=function(data){
		//支持不使用new来构造
		if(!this.parseString) {
			return new $.DataMap(data);
		}
		$.Collection.call(this);	
		if(data){
			if($.isString(data)){
				this.parseString(data);
			}else if(typeof(data)=="object"){
				this.parseObject(data);
			}
		}
	};
	$.DataMap.prototype=new $.Collection();
	$.extend($.DataMap.prototype,{
		get:function(key,defaultValue){
			var r=this.item(key);
			if(arguments.length>1 && (typeof(r)=="undefined" || r==null))
				return arguments[1];
			return r;
		},
		parseString:function(str){
			str=$.parseJsonString(str);
			(new Function("this.parseObject(" +str+")")).apply(this);
			//if(typeof(o)=="object")this.parseObject(o);
		},
		parseObject:function(obj){
			for(var p in obj){
				if(obj[p] && $.isArray(obj[p])){
					this.add(p,new $.DatasetList(obj[p]));
				}else if(obj[p] && $.isObject(obj[p])){
					this.add(p,new $.DataMap(obj[p]));
				}else{
					this.add(p,(obj[p]==undefined || obj[p]==null)?"":obj[p]);
				} 
			}
		}
	});

	$.DataMap.prototype.toString=function(){
			var cl=[],is="";
			for(var key in this.map){
				is="\"" + key + "\":";
				if(typeof(this.map[key])=="undefined" || this.map[key]==null){
					is+="\"\"";
				}else if(typeof(this.map[key])=="string" || !isNaN(this.map[key])){
					is+="\"" + $.parseJsonValue("" + this.map[key]) + "\"";
				}else{
					is+=this.map[key].toString();
				}
				cl.push(is);
			}
			return "{" + cl.join(",") + "}";
	};
	$.DataMap.prototype.put=$.DataMap.prototype.add;

	$.DatasetList=function(o){
		//支持不使用new来构造
		if(!this.parseString) {
			return new $.DatasetList(o);
		}
		this.items = [];
		this.length=0;
		if(typeof(o)=="string" && o!="")this.parseString(o);
		if(typeof(o)=="object" && (o instanceof Array) && o.length)this.parseArray(o);
	};
	$.extend($.DatasetList.prototype,{
		add:function(o){
			this.length=(this.length+1);
			this.items.push(o);
		},
		item:function(index,key,defaultValue){
			if(index < this.length && index >= 0){
				var r= this.items[index];
				if((typeof(r)!="undefined") && (r instanceof $.DataMap) 
				&& arguments.length>1 && typeof(arguments[1])=="string" 
				&& arguments[1]!="" ){return r.get(key,defaultValue);}
				return r;
			}return;
		},	
		each:function(fn, scope){
			var items = [].concat(this.items); 
			for(var i = 0, len = items.length; i < len; i=i+1){
				if(fn.call(scope || items[i], items[i], i, len) === false){
					break;
				}
			}
		},   
		remove:function(o){
			return this.removeAt(this.indexOf(o));
		},	
		removeAt:function(index){
			if(index < this.length && index >= 0){
				this.length=(this.length-1);
				this.items.splice(index, 1);
			}		
		},
		indexOf:function(o){
			if(!this.items.indexOf){
				for(var i = 0, len = this.items.length; i < len; i=i+1){
					if(this.items[i] == o){ return i;}
				}
				return -1;
			}else{
				return this.items.indexOf(o);
			}
		},	
		getCount:function(){
			return this.length; 
		},	
		parseString:function(str){
			str=$.parseJsonString(str);
			(new Function("this.parseArray(" +str+")")).apply(this);	
		},
		parseArray:function(o){
			for(var i=0;i<o.length;i++){
				if(o[i] && $.isArray(o[i])){
					this.add(new $.DatasetList(o[i]));
				}else if(o[i] && $.isObject(o[i])){
					this.add(new $.DataMap(o[i]));
				}else{
					if(o[i]!=undefined && o[i]!=null)this.add(o[i]);
				} 
			}
		},
		clear:function(){
			this.items =[];
			this.length=0;
			//System.CG();
		}
	});
	$.DatasetList.prototype.toString=function(){
		var cl=[],is="";
		for(var i=0;i<this.items.length;i++){
			is="";
			if(typeof(this.items[i])=="undefined" || this.items[i]==null){
				is+="\"\"";
			}else if(typeof(this.items[i])=="string"){
				is="\"" + $.parseJsonValue(this.items[i]) + "\"";
			}else{
				is=this.items[i].toString();
			}
			cl.push(is);
		}
		return "[" + cl.join(",") + "]";
	};
	$.DatasetList.prototype.get=$.DatasetList.prototype.item;
	$.DatasetList.prototype.put=$.DatasetList.prototype.add;

	//AJAX 事件的快速绑定
	/*$.each("ajaxStart ajaxStop ajaxComplete ajaxError ajaxSuccess ajaxSend".split(" "), function(i, o){
		$[o] = function(f) {
			return this.bind(o, f);
		};
	});*/
	var r20 = /%20/g,
	rurl = /^(\w+:)?\/\/([^\/?#]+)/;
	//Ajax 
	$.extend($,{
		//ajax活动请求数目计数器
		active: 0,
		//将object转换为URL参数字符串
		param:function(a) {
			var s = [];
			if(!a || !$.isObject(a)) return ""; 
			for (var prefix in a) {
				buildParams(prefix, a[prefix]);
			}
			//返回序列化后的字符串
			return s.join("&").replace(r20, "+");

			function buildParams(prefix, obj){
				if ($.isArray(obj)) {
					//序列化数组项
					$.each(obj, function(i, v){
						if (/\[\]$/.test(prefix)) {
							//处理子项中 例如 prefix[key]的 name，直接插入v
							add(prefix, v);
						} else {
							//处理子数组
							buildParams(prefix + "[" + (typeof v==="object" || $.isArray(v)? i : "") + "]", v);
						}
					});
						
				} else if(obj != null && typeof obj === "object"){
					//序列化Object对象
					$.each(obj, function(k, v){
						//递归处理Object对象
						buildParams(prefix + "[" + k + "]", v);
					});
				}else{
					//插入Array和Object之外的数据类型值
					add(prefix, obj);
				}
			}

			function add(key, value){
				//如果value是一个function，那么执行它，返回它的值
				value = $.isFunction(value) ? value() : value;
				//拼入URI编码后的字符串
				s[s.length] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
			}
		},
		//ajax get 调用
		get:function(url, data, callback, type){
			//当 data 参数是 Function 类型时，对参数进行互换
			if($.isFunction(data)){
				type = type || callback;
				callback = data;
				data = null;
			}
			return $.ajaxRequest({
				type: "GET",
				url: url,
				data: data,
				success: callback,
				dataType: type
			});
		},
		//ajax执行远端js
		getScript:function(url, callback){
			return $.get(url, null, callback, "script");
		},
		//ajax请求JSON数据
		getJSON: function(url, data, callback){
			return $.get(url, data, callback, "json");
		},
		//ajax post 调用
		post:function(url, data, callback, type) {
			//当 data 参数是 Function 类型时，对参数进行互换
			if ($.isFunction(data)) {
				type = type || callback;
				callback = data;
				data = {};
			}

			return $.ajaxRequest({
				type: "POST",
				url: url,
				data: data,
				success: callback,
				dataType: type
			});
		},
		//写入或覆盖默认的ajax参数
		ajaxSetup:function(settings) {
			$.extend($.ajaxSettings, settings);
		},
		//默认的ajax配置参数
		ajaxSettings:{
			//要请求的url地址 
			url: location.href,
			//是否全局的ajax 请求
			global: true,
			//XHR 请求类型
			type: "GET",
			//发送的数据格式
			contentType: "application/x-www-form-urlencoded",
			//是否有数据
			processData: true,
			//是否异步的ajax请求
			async: true,
			/*
			//编码
			//encoding:"GBK",
			//超时时间
			timeout: 0,
			//XHR请求中要发送的数据
			data: null,
			//HTTP请求需要的用户名
			username: null,
			//HTTP请求需要的密码
			password: null,
			traditional: false,
			*/
			//获取XMLHttpRequest对象 xhr();
			xhr: window.XMLHttpRequest && (window.location.protocol !== "file:" || !window.ActiveXObject) ?
				function() {
					return new window.XMLHttpRequest();
				} :
				function() {
					try {
						return new window.ActiveXObject("Microsoft.XMLHTTP");
					} catch(e) {}
				},
			//从服务器端响应返回的数据格式
			accepts: {
				xml: "application/xml, text/xml",
				html: "text/html",
				script: "text/javascript, application/javascript",
				json: "application/json, text/javascript",
				text: "text/plain",
				_default: "*/" + "*"
			}
		},
		//Last-Modified header cache for next request
		lastModified:{},
		etag:{},
		//ajax调用方法
		ajaxRequest:function(origSettings){
			//将默认参数 ajaxSettings 和入参 origSettings应用到{}上，取得调用所需的ajax参数
			var s = $.extend(true,{}, $.ajaxSettings, origSettings);
			//定义变量
			var jsonp, status, data,
				callbackContext = origSettings && origSettings.context || s,
				type = s.type.toUpperCase();

			//将要发送的data数据转换为字符串
			if (s.data && s.processData && typeof s.data !=="string") {
				s.data = $.param(s.data, s.traditional);
			}

			//处理JSONP类型的回调
			if (s.dataType === "jsonp"){
				//如果请求的类型是GET
				if (type === "GET"){
					//jsonp 请求需要在url中加入 callback=? 其中?在后面用回调函数名替换
					if (!jsre.test(s.url)){ //判断url中是否有 “=?”的字符串
						s.url +=(rquery.test( s.url )?"&" : "?") + (s.jsonp || "callback") + "=?";
					}
				}else if(!s.data || !jsre.test(s.data)){ 
					//jsonp 请求需要在post 数据中加入 callback=?
					s.data = (s.data ? s.data + "&" : "") + (s.jsonp || "callback") + "=?";
				}
				//请求数据类型改为json
				s.dataType = "json";
			}

			//创建临时性的jsonp回调函数
			//如果请求的数据类型是json,且url或data中包含 "=?" 则判定为是 jsonp请求
			if(s.dataType === "json" && (s.data && jsre.test(s.data) || jsre.test(s.url))){
				jsonp = s.jsonpCallback || ("jsonp" + jsc++);

				//将url和data中的 "=?"用 "=jsonpcallback" 替换
				if(s.data){
					s.data=(s.data + "").replace(jsre, "=" + jsonp + "$1");
				}
				s.url = s.url.replace(jsre, "=" + jsonp + "$1");

				//使用script的数据类型
				s.dataType = "script";

				//处理jsonp的载入和执行
				window[jsonp] = window[jsonp] || function(tmp){
					data = tmp;
					success();
					complete();
					//临时函数的内存回收
					window[jsonp] = undefined;
					try {
						delete window[jsonp];
					} catch(e) {}
					
					//从head中移除临时创建的script元素
					if (head){
						head.removeChild(script);
					}
				};
			}
			//如果请求的数据类型是script，则默认的缓存模式为false
			if(s.dataType === "script" && s.cache === null){
				s.cache = false;
			}
			
			//如果缓存模式为false，则拼入随机参数
			if (s.cache === false && type === "GET"){
				var ts = now();
				//尝试替换"_="，即原有的随机参数
				var ret = s.url.replace(rts, "$1_=" + ts + "$2");
				//如果替换失败，则拼入随机参数
				s.url = ret + ((ret === s.url) ? (rquery.test(s.url) ? "&" : "?") + "_=" + ts : "");
			}

			//如果有data数据且请求模式为GET，则将data拼入到url后面
			if (s.data && type === "GET") {
				s.url += (rquery.test(s.url) ? "&" : "?") + s.data;
			}

			//如果是第一个全局的ajax请求
			/*if(s.global && !$.active++){
				//执行ajaxStart时间
				$.event.trigger("ajaxStart");
			}*/

			//解析url，判断是否跨域访问
			var parts=rurl.exec(s.url),
				remote = parts && (parts[1] && parts[1] !== location.protocol || parts[2] !== location.host);

			//如果是跨域访问，则尝试使用脚本注入GET方式来加载JSON或Script
			if (s.dataType === "script" && type === "GET" && remote){
				//在head中创建script元素
				var head = document.getElementsByTagName("head")[0] || document.documentElement;
				var script = document.createElement("script");
				script.src = s.url;
				if(s.scriptCharset ){
					script.charset = s.scriptCharset;
				}

				//处理script载入
				if ( !jsonp ) {
					var done = false;
					//为script元素添加加载事件
					script.onload = script.onreadystatechange = function(){
						if (!done && (!this.readyState ||
								this.readyState === "loaded" || this.readyState === "complete")){
							done = true;
							success();
							complete();

							//在IE中，释放内存
							script.onload = script.onreadystatechange = null;
							if (head && script.parentNode) {
								head.removeChild(script);
							}
						}
					};
				}

				//使用insertBefore而不是appendChild，避免在ie6中的bug
				head.insertBefore(script, head.firstChild);

				//跳出ajax整体方法，在回调中做后续处理
				return undefined;
			}
			//XMLHttpRequest请求逻辑开始
			var requestDone = false;
			//创建XMLHttpRequest对象
			var xhr = s.xhr();
			if(!xhr ){
				return;
			}

			//打开连接
			if (s.username){
				xhr.open(type, s.url, s.async, s.username, s.password);
			} else {
				xhr.open(type, s.url, s.async);
			}

			//使用try/catch 来避免Firefox3中的bug
			try{
				//设置http头，contentType
				if (s.data || origSettings && origSettings.contentType) {
					xhr.setRequestHeader("Content-Type", s.contentType+ (s.encoding ?"; charset=" + s.encoding : ""));
				}

				// Set the If-Modified-Since and/or If-None-Match header, if in ifModified mode.
				if (s.ifModified) {
					if($.lastModified[s.url]) {
						xhr.setRequestHeader("If-Modified-Since", $.lastModified[s.url]);
					}

					if ($.etag[s.url] ) {
						xhr.setRequestHeader("If-None-Match", $.etag[s.url]);
					}
				}

				//设置一个requestHeader，以告知服务端是一个XMLHttpRequest
				//只有当它不是一个远程XHR请求时拼入
				if (!remote) {
					xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
				}

				//设置接收的数据类型
				xhr.setRequestHeader("Accept", s.dataType && s.accepts[s.dataType] ?
					s.accepts[s.dataType] + ", */" + "*" :
					s.accepts._default );
			} catch(e) {}

			//调用ajax调用入参中的beforeSend方法(如果有的话)，可以在方法里设置header/mime
			//且如果该方法返回false,则取消XMLHttpRequest调用
			if(s.beforeSend && s.beforeSend.call(callbackContext, xhr, s) === false) {
				//如果是最后一个ajax活动请求
				/*if(s.global && !--$.active){
					$.event.trigger("ajaxStop");
				}*/

				//关闭打开的连接
				xhr.abort();
				return false;
			}
			/*if(s.global){
				trigger("ajaxSend", [xhr, s]);
			}*/
			//等待XHR响应事件
			var onreadystatechange = xhr.onreadystatechange = function(isTimeout){
				//请求被取消
				if (!xhr || xhr.readyState === 0 || isTimeout === "abort") {
					// Opera 在readyState==0时不能响应onreadystatechange方法
					// 所以我们手动处理
					if (!requestDone){
						complete();
					}
					requestDone = true;
					if (xhr){
						//设置onreadystatechange 事件为空函数
						xhr.onreadystatechange = $.noop;
					}

				//请求完成，并且返回数据可用，或者请求超时 The transfer is complete and the data is available, or the request timed out
				}else if (!requestDone && xhr && (xhr.readyState === 4 || isTimeout === "timeout")){
					requestDone = true;
					xhr.onreadystatechange = $.noop;

					status = isTimeout === "timeout" ?
						"timeout" :
						!$.httpSuccess(xhr) ?
							"error" :
							s.ifModified && $.httpNotModified(xhr, s.url)?
								"notmodified" :
								"success";
					var errMsg;
					if (status === "success") {
						//处理XML解析，并获取解析错误信息
						try {
							//处理返回的数据
							data = $.httpData(xhr, s.dataType, s);
						} catch(err) {
							status = "parsererror";
							errMsg = err;
						}
					}

					//确保请求成功，且无更改
					if (status === "success" || status === "notmodified"){
						//jsonp调用它自己的回调函数
						if (!jsonp){
							success();
						}
					}else{
						//处理错误信息
						$.handleError(s, xhr, status, errMsg);
					}

					//运行complete回调
					complete();
					//如果超时，则取消request
					if (isTimeout === "timeout"){
						xhr.abort();
					}
					//释放XMLHttpRequest对象
					if (s.async) {
						xhr = null;
					}
				}
			};

			// Override the abort handler, if we can (IE doesn't allow it, but that's OK)
			// Opera doesn't fire onreadystatechange at all on abort
			try {
				var oldAbort = xhr.abort;
				xhr.abort = function(){
					if ( xhr ) {
						oldAbort.call(xhr);
					}

					onreadystatechange("abort");
				};
			} catch(e) { }

			//超时检查
			if (s.async && s.timeout > 0){
				setTimeout(function(){
					//检查请求是否正常
					if (xhr && !requestDone) {
						onreadystatechange("timeout");
					}
				}, s.timeout);
			}

			//发送data
			try {
				xhr.send(type === "POST" || type === "PUT" || type === "DELETE"?s.data:null);
			} catch(e) {
				$.handleError(s, xhr, null, e);
				// 执行complete回调
				complete();
			}

			//firefox 1.5不能在异步的XHR请求中触发statechagne事件
			if (!s.async) {
				onreadystatechange();
			}

			function success(){
				//执行success回调
				if(s.success){
					s.success.call(callbackContext, data, status, xhr);
				}

				/*if(s.global){
					trigger("ajaxSuccess", [xhr, s]);
				}*/
			}

			function complete(){
				//执行complete回调
				if (s.complete){
					s.complete.call( callbackContext, xhr, status);
				}

				//请求完成
				/*if (s.global){
					//执行全局或context上绑定的ajaxComplete事件
					trigger("ajaxComplete", [xhr, s]);
				}*/

				//进行ajax请求计数处理
				/*if (s.global && !--$.active){
					//执行全局ajaxStop事件
					$.event.trigger("ajaxStop");
				}*/
			}
			
			/*function trigger(type, args) {
				(s.context ? $(s.context) : $.event).trigger(type, args);
			}*/

			//将XMLHttpRequest对象返回，以在必须时可以取消这个XHR请求
			return xhr;
		},
		handleError: function(s, xhr, status, e){
			//处理error回调
			if(s.error){
				s.error.call( s.context || s, xhr, status, e );
			}
			//执行全局回调
			/*if (s.global){
				//触发s.content或全局的ajaxError事件
				(s.context ? $(s.context) : $.event).trigger("ajaxError",[xhr, s, e]);
			}*/
		},
		//检查XMLHttpRequest请求是否成功
		httpSuccess: function(xhr){
			try {
				// IE error sometimes returns 1223 when it should be 204 so treat it as success, see #1450
				return !xhr.status && location.protocol === "file:" ||
					//当其它浏览器中status是304时候，Opera则是0
					(xhr.status >= 200 && xhr.status < 300 ) ||
					xhr.status === 304 || xhr.status === 1223 || xhr.status === 0;
			} catch(e) {}

			return false;
		},
		//通过响应的ResponseHeader内容检查XMLHttpRequest响应是否有更改
		httpNotModified: function( xhr, url ) {
			var lastModified = xhr.getResponseHeader("Last-Modified"),
				etag = xhr.getResponseHeader("Etag");
			if (lastModified) {
				$.lastModified[url]= lastModified;
			}
			if (etag){
				$.etag[url] = etag;
			}
			//当其它浏览器中status是304时候，Opera则是0
			return xhr.status === 304 || xhr.status === 0;
		},
		httpData: function(xhr, type, s){
			var ct = xhr.getResponseHeader("content-type") || "",
				xml = type === "xml" || !type && ct.indexOf("xml") >= 0,
				data = xml ? xhr.responseXML : xhr.responseText;

			if(xml && data.documentElement.nodeName === "parsererror"){
				$.error("parsererror");
			}

			//如果ajax调用参数中有dataFilter函数，则使用dataFilter函数对返回的数据进行处理
			if(s && s.dataFilter){
				data = s.dataFilter(data, type);
			}

			//解析服务器返回的string数据
			if (typeof data === "string"){
				//如果请求的数据类型是json,则解析json数据
				if (type === "json" || !type && ct.indexOf("json") >= 0){
					data = $.parseJSON(data);
				// 如果请求的数据类型是script，那么执行script
				}else if( type === "script" || !type && ct.indexOf("javascript") >= 0 ) {
					$.globalEval(data);
				}
			}
			return data;
		}		
	});
	
	return $;
});


