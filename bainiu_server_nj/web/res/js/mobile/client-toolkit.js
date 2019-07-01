/**
 * Mobile扩展的tool对象,用于辅助Mobile对象中API的扩展. 
 */
define(["module","jcl"],function(module,Wade) {
	var clientTool = {};
	module.exports = clientTool;
	
	clientTool.tool = new function(){
		/*判断是否是DataMap类型*/
		this.isDataMap = function(key){
			return typeof (key) == "object" && (key instanceof Wade.DataMap);
		};
		/*判断是否是Array类型*/
		this.isArray = function(key){
			return typeof (key) == "object" && key.constructor == Array;
		};
		/*提取DatasetList中的json对象*/
		this.parseList = function(list){
			if(!(list instanceof Wade.DatasetList)){
				return list;
			}
			for(var i=0,len=list.items.length;i<len;i++){
				if(list.items[i] instanceof Wade.DataMap){
					list.items[i] = clientTool.tool.parseData(list.items[i]);
				}else if(list.items[i] instanceof Wade.DatasetList){
					list.items[i] = clientTool.tool.parseList(list.items[i]);
				}
			}
			return list.items;
		};
		/*提取DataMap中的json对象*/
		this.parseData = function (param){
			if(!(param instanceof Wade.DataMap)){
				return param;
			}
			for(var key in param.map){
				if(param.map[key] instanceof Wade.DataMap){
					param.map[key] = clientTool.tool.parseData(param.map[key]);
				}else if(param.map[key] instanceof Wade.DatasetList){
					param.map[key] = clientTool.tool.parseList(param.map[key]);
				}
			}
			return param.map;
		};
	};
});