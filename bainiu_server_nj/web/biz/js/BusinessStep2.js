require(["mobile","jquery","common"],function(Mobile,$,Common){
	$("#header").children().click(function(){
		Mobile.back();
	});
	$("#continue").click(function(){
		Common.openPage("Business");
	});
});
