require(["mobile","jquery","jcl","common","pagination","placeholder"],function(Mobile,$,Wade,Common,pag,place){
	var errList = $("#errList").children();
	var qryButton = $("#qry");
	var goToMangerlink = $("#goToMangerlink");
	goToMangerlink.click(function(){Common.openPage("MBainiuIndex");});
	var logLink = $("#loginLink");
	logLink.click(function(){Common.openPage("Login");});
	var headLogo = $("#headLogo");
	headLogo.click(function(){Common.openPage("Index");});
	var regLink = $("#regLink");
	regLink.click(function(){Common.openPage("Reg");});

	footerClass();
	
	//页面超链接
	$("#keyWord").keyup(function(e){
    	var ev = document.all ? window.event : e;
    	if(ev.keyCode==13) {
				var keyWord = $("#keyWord").val();
				var sysDomain = $("#sysDomain").text();
				var param = Wade.DataMap();
	    		param.put("KEY_WORD",keyWord);
	    		param.put("CHOOSE_TYPE",sysDomain);
	    		Common.get(function(value){
				Common.openPage("Qry",param);	
				},"EX_ID");	
     		}
	});
	//查询
	qryButton.click(function(){
		var keyWord = $("#keyWord").val();
		var sysDomain = $("#sysDomain").text();
		var param = Wade.DataMap();
	    param.put("KEY_WORD",keyWord);
		param.put("CHOOSE_TYPE",sysDomain);
	    Common.get(function(value){
			Common.openPage("Qry",param,function(data){
				if(typeof data == "string"){
				data = new Wade.DataMap(data);
				}
				$("#keyWord").html(data.get("KEY_WORD"));
			});	
		},"EX_ID");
	});
	
	//分页
		var paginations =$("#paginations").children();
			$.each(paginations,function(index,item){
			var obj = $(item);
			var pagobj = obj.children();
	    	var pagIdex = pagobj.attr("pagIdex");
	    	if(pagIdex){
	    			obj.click(function(){
	    				var param = Wade.DataMap();
	    				var keyWord = $("#keyWord").val();
	    				var sysDomain = $("#sysDomain").text();
	    				param.put("CHOOSE_TYPE",sysDomain);
	    				param.put("KEY_WORD",keyWord);
	    				param.put("ROW_INDEX",pagIdex);
						Common.openPage("Qry",param);	
	    			});
	    		}
			});
			
	$("#gotopagesubmit").click(function(){
		var param = Wade.DataMap();
	    var keyWord = $("#keyWord").val();
	    var sysDomain = $("#sysDomain").text();
	    param.put("CHOOSE_TYPE",sysDomain);
	    param.put("KEY_WORD",keyWord);
	    param.put("ROW_INDEX",$("#gotopage").val());
	    Common.openPage("Qry",param);	
	});

	//详细查询
	$.each(errList,function(index,item){
		var obj = $(item);
	    var ex_id_div = obj.children();
	    var ex_id = ex_id_div.children().attr("ex_id")
	    var sysDomain = $("#sysDomain").text();
	    if(ex_id){
	    	ex_id_div.click(function(){
	    		var param = Wade.DataMap();
	    		param.put("EX_ID",ex_id);
	    		param.put("CHOOSE_TYPE",sysDomain);
	    		Common.get(function(value){
					Common.openPage("Details",param);	
				},"EX_ID");
	    	});
	    }
	});
	
 function footerClass(){
    	//意见反馈和批次号底部显示
	 $("#frame_footer").attr("style","color:#999; width:100%; line-height:150%; border-top:0; padding-top:0px; padding-bottom:10px; clear:both;");
    }
	  
});





