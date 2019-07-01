define(function($){
	if(!window["WmWebUI"]){
		var wmWebUI = window["WmWebUI"] = {};
		wmWebUI.tags = {}
	}
	
	wmWebUI.store = function(id,tag){
		wmWebUI.tags[id] = tag;
	}
	
	wmWebUI.select = function(id){
		return wmWebUI.tags[id];
	}
	
	return wmWebUI;
});
