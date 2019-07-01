require(["mobile","jquery","jcl","common","util"],function(Mobile,$,Wade,Common,Util)
{
	var isPass=false;
	$('#account-name').blur(function(){
     var checkParam = Wade.DataMap();	
     checkParam.put("USER_ACCT",$.trim($("#account-name").val()));
	 Common.callSvc("User.checkUser",checkParam,function(resultData){
			if(resultData.get("ACCT_ERROR")!="")
			{
				Mobile.tip(resultData.get("ACCT_ERROR"));
				$("#account-name").val('');				
				isPass=false;
			}
			else
			{
				isPass=true;
			}
		});
	});
	
	$('#user-email').blur(function(){
     var checkParam = Wade.DataMap();	
     checkParam.put("EMAIL",$.trim($("#user-email").val()));
	 Common.callSvc("User.checkUser",checkParam,function(resultData){			 
			if(resultData.get("EMAIL_ERROR")!="")
			{
				Mobile.tip(resultData.get("EMAIL_ERROR"));
				$("#user-email").val('');
				isPass=false;
			}
			else
			{
				isPass=true;
			}
		});
	});

	$("#register").click(function()
	{
		if(!isPass)
		{
			 return;
		}
		if(Util.isEmpty($("#account-name").val()))
		{
			Common.showFail('帐号不能为空');
			 return;
		}
		 
		if(Util.isEmpty($("#user-email").val()))
		{
			Common.showFail('NT帐号不能为空');
			 return;
		}

		 if(Util.isEmpty($("#user-password").val()))
		 {
			 Common.showFail('密码不能为空');
			 return;
		 }
		 
		 if(!($("#user-password").val()==$("#user-password-confirm").val()))
		 {
			 Common.showFail('输入的密码不相同');
			 return;
		 }
		 
		 if(Util.checkMobile($("#user-iphone").val()))
		 {
			 return;
		 }
		 
		 var checkParam = Wade.DataMap();			
		 checkParam.put("USER_ACCT",$.trim($("#account-name").val()));
		 checkParam.put("EMAIL",$.trim($("#user-email").val()));
	
		 var param = Wade.DataMap();
		 param.put("USER_ACCT",$.trim($("#account-name").val()));
		 param.put("NAME",$.trim($("#user-name").val()));
		 param.put("PASSWORD",$.trim($("#user-password").val()));
		 param.put("EMAIL",$.trim($("#user-email").val()));
		 param.put("PHONE",$.trim($("#user-iphone").val()));
		 param.put("NTACCT",$.trim($("#user-email").val()));
		 param.put("ORG_NAME",$.trim($("#org").val()));
		 
		 Common.callSvc("User.register",param,function(resultData){							 
				if(resultData.get("result")=="0")
				{
					Common.showSuccess("注册成功!");
					Common.openPage("UserInsert");
				}
				else
				{
					Common.showFail("注册失败!");
				}
		});
	});
});