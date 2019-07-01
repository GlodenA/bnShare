require(["mobile","jquery","jcl","common"],function(Mobile,$,Wade,Common){	
	//返回首页
	var feedBackSub = $("#feedBackSub");
	feedBackSub.click(function(){
		var feedBack =  $("#feedBack").val();
		if(feedBack.length < 5){
			$("#feedBack").val('');
			$("#feedBack").attr("placeholder","字数有点少，请具体描述下");
			return ;
		};
		var param = Wade.DataMap();
		param.put("FEEDBACK_INFO",feedBack);
		Common.callSvc("Comment.submit",param,function(data){
//			$("#feedBack").val('');
//			$("#feedBack").attr("placeholder","请您留下意见或建议，我们将不断优化体验");
          Common.closeDialog();
		});
	});
	
	
});