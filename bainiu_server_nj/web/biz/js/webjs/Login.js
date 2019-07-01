require(["domReady!","zepto","jcl","mobile","common"],function(Doc,$,Wade,Mobile,Common){
	footerClass();
	var gotoIndex = $("#gotoIndex");
	var regLink = $("#regLink");
	gotoIndex.click(function(){Common.openPage("Index");});
	regLink.click(function(){Common.openPage("Reg");});
	
	$("#findPwdBtn").click(function(){
	    if($('#user_acct').val()==''){
	    	Common.msg("请输入用户名，系统将发送您当前密码到您注册邮箱");
	        $('.acc').focus();
	        return false;
	    }
	    var user_acct = $("#user_acct").val();
	    var param = Wade.DataMap();
	    param.put("USER_ACCT",user_acct);
		Common.callSvc("Login.findPwd",param,function(returnBuf){
			if('true'==returnBuf.get("FIND_OK"))
			{
				Common.msg("密码找回邮件已发送");
			}
			else
			{
				Common.msg("密码找回失败，请确定账号是否正确");
			}
		});
	    
	});	
	
/*	$(".closemessage").click(function(){
		$(".messagebox").hide();
	});
*/	
	$("#loginBtn").click(function(){
		if(loginValid())
		{	
		setTimeout(function(){
			var explorer = window.navigator.userAgent;
			var user_acct = $("#user_acct").val();
			var password = $("#password").val();
			var param = Wade.DataMap();
			if(explorer.indexOf("MSIE") >= 0){
				param.put("BROWSER","Internet Explorer");
			}
			if(explorer.indexOf("Firefox") >= 0){
				param.put("BROWSER","Mozilla Firefox");
			}
			if(explorer.indexOf("Opera") >= 0){
				param.put("BROWSER","Opera");
			}	
			if(explorer.indexOf("Safari") >= 0){
				param.put("BROWSER","Safari");
			}
			if(explorer.indexOf("Chrome") >= 0){
				param.put("BROWSER","Google Chrome");
			}
			param.put("USER_ACCT",user_acct);
			param.put("PASSWORD",password);
			Common.callSvc("Login.doLogin",param,function(returnBuf){
				if('true'==returnBuf.get("IS_LOGIN_IN")){
					Common.clear();
					//登陆信息
					Common.put("NAME",returnBuf.get("NAME"));
					Common.put("ORG",returnBuf.get("ORG"));
					Common.put("NT_ACCT",returnBuf.get("NT_ACCT"));
					Common.put("USER_ID",returnBuf.get("USER_ID"));
					Common.put("RIGHTS",returnBuf.get("RIGHTS"));
					Common.put("RIGHT",returnBuf.get("RIGHT"));
					Common.put("FUNRIGHTS",returnBuf.get("FUNRIGHTS"));
					Common.put("SESSION_ID",returnBuf.get("SESSION_ID"));
					Common.put("USER_ACCT",user_acct);
					//全局展示信息
					Common.put("QUESTION_NAV",returnBuf.get("QUESTION_NAV"));
					Common.put("SYSMAN_NAV",returnBuf.get("SYSMAN_NAV"));
//					Common.msg(returnBuf.get("NAME")+",欢迎您回来!");
					Common.openPage("MBainiuIndex");
				}else{
					Common.msg(returnBuf.get("X_INFO"));
				}
			});
		},500);
		}
	});
});

function loginValid(){
    if($('#user_acct').val()==''){
		$(".messagebox").show();
        $('.valid_area').html("<b>*</b>"+'用户名不能为空！');
        $('.acc').focus();
        return false;
    }
    if($('#password').val()==''){
		$(".messagebox").show();
        $('.valid_area').html("<b>*</b>"+'密码不能为空！');
        $('.pass').focus();
        return false;
    }
    $('.valid_area').text('');
	$(".messagebox").hide();
    return true;
}

    function footerClass(){
    	//意见反馈和批次号底部显示
			var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
			if(windowFlow){
				$("#frame_footer").attr("style","position:absolute; bottom:0px; color:#999; width:100%; line-height:150%; border-top:0; padding-top:0px; padding-bottom:10px; clear:both;");
			}else{
				$("#frame_footer").attr("style","color:#999; width:100%; line-height:150%; border-top:0; padding-top:0px; padding-bottom:10px; clear:both;");
			}
    }



