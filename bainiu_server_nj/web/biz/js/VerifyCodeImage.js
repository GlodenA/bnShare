require(["mobile","zepto","common"],function(Mobile,$,Common){
	$("#verifyIMG").click(function(){
		Common.callSvc("VerifyCodeImage.generateImage", null, function(data){
			var resultData = new Wade.DataMap(data);
			$("#verifyIMG").attr("src", "data:image/png;base64, " + resultData.get("VERIFY_IMG"));
		});
	});
});
