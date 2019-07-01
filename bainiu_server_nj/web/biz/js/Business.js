require(["mobile","jquery","common"],function(Mobile,$,Common){
	var userName = $("#userName").val();
	$("#header").children().click(function(){
		Mobile.openPage("Index");
	});
	$("#business4g").click(function(){
		Mobile.openPage("BusinessStep1");
	});
});