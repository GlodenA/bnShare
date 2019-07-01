require(["mobile","jquery","jcl","common"],function(Mobile,$,Wade,Common){	
	//返回首页
	var goToIndex = $("#goToIndex");
	goToIndex.click(function(){Common.openPage("Index");});
	//注销
	var signOut = $("#signOut");
	signOut.click(function(){
	 Common.logoutAccount();
	});
	//问题管理菜单
	var questionNav = $("#question-nav").children();
	$.each(questionNav,function(index,item){
		var obj = $(item);
	    var action = obj.children().attr("action");
	    if(action){
	    	obj.click(function(){
				Common.openPage(action);	
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
				Common.openPage(action);	
	    	});
	    }
	});
	
});