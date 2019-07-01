
require(["mobile", "jquery", "jcl", "common"], function (Mobile, $, Wade, Common) {	
	//注销
	var signOut = $("#signOut");
	signOut.click(function(){
	 Common.logoutAccount();
	});



	//返回首页
	var sysmailink = $("#sysmailink");
	sysmailink.click(function () {
		Mobile.getMemoryCache(function (sessionId) {
			redirectToByUrl("mobile?action=MsgBox&data={'SESSION_ID':" + sessionId + "}", "content");
		}, "SESSION_ID");
	});
	
	var personalCenter = $("#personalCenter");
	personalCenter.click(function () {
		Mobile.getMemoryCache(function (sessionId) {
			redirectToByUrl("mobile?action=Personal&data={'SESSION_ID':" + sessionId + "}", "content");
		}, "SESSION_ID");
	});
	
	//展开搜索
	var formwrapper = $(".form_wrapper");
	formwrapper.click(function(){
		this.attr("style","width:600px;");
	});
	//
	
    
    if(navigator && navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
    	try {
    		var isTocuh = "ontouchstart" in window;
        	var navTag = 0;
            var clickFn = function(e){
            	if(!e || e.target.tagName == 'A' || e.target.className == 'left-nav-ctrl') {
            		var cols;
            		if (navTag == 0) {
            			cols = "0,*";
            			navTag = 1;
            		} else {
            			cols = "176,*";
            			navTag = 0;
            		}
            		window.parent.document.getElementsByTagName("frameset")[1].cols = cols;            		
            	}

        	};
        	$("#leftNavCtrl").bind(isTocuh?"touchend":"click", clickFn);
        	clickFn();
        	window.parent.document.getElementsByTagName("frameset")[1].childNodes[1].contentWindow.onclick = clickFn;      		
    	} catch(e){
    		console.log(e);
    	}
  	
    }

	

});

