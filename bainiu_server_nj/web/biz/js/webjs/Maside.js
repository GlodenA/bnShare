require(["mobile","jquery","jcl","common"],function(Mobile,$,Wade,Common){	
	//返回首页
	var goToIndex = $("#goToIndex");
	goToIndex.click(function(){
		Mobile.getMemoryCache(function(sessionId){
			top.location.href = "mobile?action=Index&data={'SESSION_ID':"+sessionId+"}";
		}, "SESSION_ID");
	});
	
	//问题管理菜单
	var questionNav = $("#question-nav").children();
	$.each(questionNav,function(index,item){
		var obj = $(item);
	    var action = obj.children().attr("action");
	    if(action){
	    	obj.click(function(){
	    		Mobile.getMemoryCache(function(sessionId){
					redirectToByUrl("mobile?action="+action+"&data={'SESSION_ID':"+sessionId+"}","content");
				}, "SESSION_ID");
	    	});
	    }
	});
	//系统管理菜单
	var sysmanNav = $("#sysman-nav").children();
	$.each(sysmanNav,function(index,item){
		var obj = $(item);
	    var action = obj.children().attr("action");
	    if(action){
	    	obj.click(function(){
				Mobile.getMemoryCache(function(sessionId){
					redirectToByUrl("mobile?action="+action+"&data={'SESSION_ID':"+sessionId+"}","content");
				}, "SESSION_ID");
	    	});
	    }
	});
	//文件管理菜单
	var toolsNav = $("#tools-nav").children();
	$.each(toolsNav,function(index,item){
		var obj = $(item);
	    var action = obj.children().attr("action");
	    if(action){
	    	obj.click(function(){
				Mobile.getMemoryCache(function(sessionId){
					redirectToByUrl("mobile?action="+action+"&data={'SESSION_ID':"+sessionId+"}","content");
				}, "SESSION_ID");
	    	});
	    }
	});
	//项目管理菜单
	var pmNav = $("#pm-nav").children();
	$.each(pmNav,function(index,item){
		var obj = $(item);
	    var action = obj.children().attr("action");
	    if(action){
	    	obj.click(function(){
				Mobile.getMemoryCache(function(sessionId){
					redirectToByUrl("mobile?action="+action+"&data={'SESSION_ID':"+sessionId+"}","content");
				}, "SESSION_ID");
	    	});
	    }
	});
	//智享库菜单
	var bpsNav = $("#bps-nav").children();
	$.each(bpsNav,function(index,item){
		var obj = $(item);
		var action = obj.children().attr("action");
		if(action){
			obj.click(function(){
				Mobile.getMemoryCache(function(sessionId){
					redirectToByUrl("mobile?action="+action+"&data={'SESSION_ID':"+sessionId+"}","content");
				}, "SESSION_ID");
			});
		}
	});
});