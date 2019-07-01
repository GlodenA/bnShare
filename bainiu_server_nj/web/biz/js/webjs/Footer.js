require(["mobile","jquery","jcl","common"],function(Mobile,$,Wade,Common){
	var feedBack = $("#feedBack");
	feedBack.click(function(){Common.openDialog("Comment");});
});

