require(["mobile","jquery","jcl","common"],function(Mobile,$,Wade,Common){
	
	var solvedErrList = $("#solvedErrList").children();
	var qryButton = $("#qry");
	var paginations = $("#paginations").children();
		
	//页面超链接
	var logLink = $("#loginLink");
	logLink.click(function(){Common.openPage("Login");});
	
	
	
	//查询
	qryButton.click(function(){
		var keyWord = $("#keyWord").val();
		var param = Wade.DataMap();
	    param.put("KEY_WORD",keyWord);
	    Common.get(function(value){
			Common.openPage("UnsolvedErrQry",param);	
		},"EX_ID");
	});
	//分页查询
	$.each(paginations,function(index,item){
		var obj = $(item);
	    var pagIdex = obj.children().attr("pagIdex");
	    if(pagIdex){
	    	obj.click(function(){
	    		var keyWord = $("#keyWord").val();
	    		var param = Wade.DataMap();
	    		param.put("KEY_WORD",keyWord);
	    		param.put("ROW_INDEX",pagIdex);
	    		Common.get(function(value){
					Common.openPage("BainiuQry",param);	
				},"EX_ID");
	    	});
	    }
	});
	//未解答详细查询
	$.each(solvedErrList,function(index,item){
		var obj = $(item);
	    var ex_id = obj.children().attr("ex_id");
	    if(ex_id){
	    	obj.click(function(){
	    		var param = Wade.DataMap();
	    		param.put("EX_ID",ex_id);
	    		Common.get(function(value){
					Common.openPage("SolvedErr",param);	
				},"EX_ID");
	    	});
	    }
	});
	
	
});