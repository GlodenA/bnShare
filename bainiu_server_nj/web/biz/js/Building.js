require(["mobile","zepto","wadeMobile"],function(Mobile,$,WadeMobile){
	$("#header").children().click(function(){
		Mobile.back();
	});
});