require(["mobile","jquery","common","wadeMobile"],function(Mobile,$,Common,WadeMobile){
	var menus = $("#menuList").children();
	//WadeMobile.defineAlert("测试一个插件");
	$.each(menus,function(index,item){
		var obj = $(item);
		var action = obj.attr("action");
		if(action){
			if(action == "Login"){
				obj.click(function(){
					Mobile.openPage(action);
				});
				//WadeMobile.defineAlert("测试一个插件");
			}else{
				obj.click(function(){
					Common.openPage(action);
				});
			}
		}
	});
	$("#openTestPage").click(function(){
		Common.openPage("Test");
	});
});