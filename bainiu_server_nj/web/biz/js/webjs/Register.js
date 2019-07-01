
require(["jquery", "common","mobile","jcl"], function ($,Common,Mobile,Wade) {
    footerClass();
	var gotoIndex = $("#gotoIndex");
	var logLink = $("#loginLink");
	gotoIndex.click(function(){Common.openPage("Index");});
	logLink.click(function(){Common.openPage("Login");});
	

	function regValid() {
		var acc_value = $(".acc input").val();
		if (!/.+@.+\.[a-zA-Z]{2,4}$/.test(acc_value) && !acc_value.match(/^(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/)) {
			$(".messagebox").show();
			$(".valid_area").html("<b>*</b>" + "手机号或邮箱格式不正确！");
			$(".acc input").focus();
			return false;
		}
		var pass_value = $(".pass input").val();
		if (!/^\w{6,14}$/.test(pass_value)) {
			$(".messagebox").show();
			$(".valid_area").html("<b>*</b>" + "密码支持6-14位数字、字母、下划线！");
			$(".pass input").focus();
			return false;
		}
		$(".valid_area").text("");
		$(".messagebox").hide();
		return true;
	}
	function sub() {
		if (regValid()) {
			var param = Wade.DataMap();
			param.put("ACCID", $.trim($("#acc_id").val()));
			param.put("PASSWORD", $.trim($("#password").val()));
			param.put("NICK_NAME", $.trim($("#name").val()));
			Common.callSvc("Reg.doRegister", param, function (returnBuf) {
				if ("true" == returnBuf.get("IS_REG_OK")) {
					setTimeout(function () {
						var explorer = window.navigator.userAgent;
						var user_acct = returnBuf.get("USER_ACCT");
						var password = returnBuf.get("PASSWORD");;
						var param = Wade.DataMap();
						if (explorer.indexOf("MSIE") >= 0) {
							param.put("BROWSER", "Internet Explorer");
						}
						if (explorer.indexOf("Firefox") >= 0) {
							param.put("BROWSER", "Mozilla Firefox");
						}
						if (explorer.indexOf("Opera") >= 0) {
							param.put("BROWSER", "Opera");
						}
						if (explorer.indexOf("Safari") >= 0) {
							param.put("BROWSER", "Safari");
						}
						if (explorer.indexOf("Chrome") >= 0) {
							param.put("BROWSER", "Google Chrome");
						}
						param.put("USER_ACCT", user_acct);
						param.put("PASSWORD", password);
						Common.callSvc("Login.doLogin", param, function (returnBuf) {
							if ("true" == returnBuf.get("IS_LOGIN_IN")) {
								Common.clear();
					//登陆信息
								Common.put("NAME", returnBuf.get("NAME"));
								Common.put("ORG", returnBuf.get("ORG"));
								Common.put("NT_ACCT", returnBuf.get("NT_ACCT"));
								Common.put("USER_ID", returnBuf.get("USER_ID"));
								Common.put("RIGHTS", returnBuf.get("RIGHTS"));
								Common.put("RIGHT", returnBuf.get("RIGHT"));
								Common.put("FUNRIGHTS", returnBuf.get("FUNRIGHTS"));
								Common.put("SESSION_ID", returnBuf.get("SESSION_ID"));
								Common.put("USER_ACCT", user_acct);
								//全局展示信息
								Common.put("QUESTION_NAV", returnBuf.get("QUESTION_NAV"));
								Common.put("SYSMAN_NAV", returnBuf.get("SYSMAN_NAV"));
								Mobile.tip(returnBuf.get("NAME") + ",欢迎首次进入!");
								Common.openPage("MBainiuIndex");
							} 
						});
					}, 500);
				} else {
					$(".valid_area").html("<b>*</b>" + returnBuf.get("ERR_INFO"));
				}
			});
		}
	}
	$("#regBtn").click(function () {
		sub();
	});
	$(document).keyup(function (event) {
		if (event.keyCode == 13) {
			$("#regBtn").trigger("click");
		}
	});
});

function footerClass(){
    	//意见反馈和批次号底部显示
			var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
			if(windowFlow){
				$("#frame_footer").attr("style","position:absolute; bottom:0px; color:#999; width:100%; line-height:150%; border-top:1px solid #ddd; padding-top:10px; padding-bottom:10px; clear:both;");
			}else{
				$("#frame_footer").attr("style","color:#999; width:100%; line-height:150%; border-top:1px solid #ddd; padding-top:10px; padding-bottom:10px; clear:both;");
			}
    }

