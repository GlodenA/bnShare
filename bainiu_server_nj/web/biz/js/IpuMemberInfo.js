require(["mobile","zepto","jcl","common"],function(Mobile,$,Wade,Common){
	$("#header").children().click(function(){
		Mobile.back();
	});
	$("#updateInfos").click(function(){
		Common.callSvc("IpuMemberInfo.updateInfos",null,function(data){
			if(typeof data == "string"){
				data = new Wade.DataMap(data);
			}
			alert(data.get("MSG"));
			$("#password").html(data.get("PASSWORD"));
		});
	});
	
	$("#insertInfos").click(function(){
		Common.callSvc("IpuMemberInfo.insertInfos",null,function(data){
			if(typeof data == "string"){
				data = new Wade.DataMap(data);
			}
			alert(data.get("MSG"));
		});
	});
	$("#deleteInfos").click(function(){
		Common.callSvc("IpuMemberInfo.deleteInfos",null,function(data){
			if(typeof data == "string"){
				data = new Wade.DataMap(data);
			}
			alert(data.get("MSG"));
		});
	});
	
});