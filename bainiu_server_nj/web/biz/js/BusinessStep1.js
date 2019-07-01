require(["mobile","jquery","common","wadeMobile","mobile"],function(Mobile,$,Common,WadeMobile,Mobile){
	$("#header").children().click(function(){
		Mobile.back();
	});
	$("#opening4g").click(function(){
		if( confirm("确定办理此业务吗？") ){
			WadeMobile.sms("10086","我是中国人",1);
			Mobile.tip("短信正在发送……(注意，这是测试短信，不会产生任何费用)");
			Mobile.loadingStart("等待中……","稍等");
			setTimeout(function(){
				Mobile.loadingStop();
				Mobile.tip("业务办理成功！");
				Mobile.openPage("BusinessStep2");
			},1500);
		}
	});
});

