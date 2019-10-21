require(["mobile","jquery","jcl","layer","common"],function(Mobile,$,Wade,Layer,Common){
	
	Common.pagination("WebLink");
	// 跳转 按钮
	$('button[name=weburl]').bind("click", function () {
		var webURL = $(this).parent().attr("WEB_URL");
		window.open(webURL) ;

	});
	//意见反馈和批次号底部显示
    function footerClass(){    	
		var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
		if(windowFlow){
			$("#frame_footer").attr("style","position:absolute; bottom:10px; color:#999; width:100%; text-align:left; line-height:150%;");
		}else{
			$("#frame_footer").attr("style","bottom:5px;text-align: center; width:100%");
		}
    }
});