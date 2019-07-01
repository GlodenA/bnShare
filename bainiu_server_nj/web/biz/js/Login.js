require(["domReady!","zepto","jcl","mobile","common"],function(Doc,$,Wade,Mobile,Common){
	
	//刷新页面以后存放图片验证码的sessionId
	var sessionId = $("#sessionId").val();
	if(sessionId){
		Common.put("SESSION_ID", sessionId);
	}else{
		alert("验证码丢失,重新获取图片验证码~");
	}
	
	$("#loginBtn").click(function(){
		Mobile.loadingStart("登陆中,请稍等……","等待");
		setTimeout(function(){
			var userName = $("#userName").val();
			var passWord = $("#passWord").val();
			var verifyCode = $("#verifyCode").val();
			var param = Wade.DataMap();
			param.put("USER_NAME",userName);
			param.put("PASS_WORD",passWord);
			param.put("VARIFY_CODE",verifyCode);
			Common.callSvc("Login.doLogin",param,function(resultData){
				Mobile.loadingStop();
				Common.put("SESSION_ID",resultData.get("SESSION_ID"));
				Common.put("USER_NAME",param.get("USER_NAME"));
				Mobile.tip(userName+"登陆成功!");
				Mobile.openPage("Index");
			});
		},500);
	});
	
	$("#verifyIMG").click(function(){
		Common.get(function(sessionId){
			var param = Wade.DataMap();
			param.put("SESSION_ID", sessionId);
			Common.callSvc("Login.refreshVerifyCode", param, function(resultData){
				$("#verifyIMG").attr("src", "data:image/png;base64, " + resultData.get("VERIFY_IMG"));
				$("#sessionId").val(resultData.get("SESSION_ID"));
				Common.put("SESSION_ID", resultData.get("SESSION_ID"));
			});
		},"SESSION_ID");
	});
});
