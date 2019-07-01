define(["wadeMobile","mobileClient","mobileBrower"],function(WadeMobile){
	var Mobile;
//	if(WadeMobile.isApp()){
//		Mobile =  require("mobileClient");
//	}else{
		Mobile = require("mobileBrower");
//	}
	return Mobile;
});