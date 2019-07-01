require(["mobile","jquery","jcl","common","chart"],function(Mobile,$,Wade,Common,ChartPie){
	
	
	var errList = $("#errList").children();
	var solvedErrList = $("#solvedErrList").children();
	var qryButton = $("#qry");
	var paginations = $("#paginations").children();
	
	
	//报表
	var getChart =  function(pieObj,pieData){
		var char;
		var cl=[];
		var data= Wade.DatasetList(pieData);
		for(var i=0;i<data.items.length;i++){
			cl[i]=data.items[i].map;
			cl[i].value=parseInt(data.items[i].items[1]);
		}
		char=new Chart(pieObj).Doughnut(cl);
	}
	
	var errSolvePie = document.getElementById("errSolvePie").getContext("2d");
	var pieData= $("#errSolvePie").attr("pieData");
	getChart(errSolvePie,pieData);
	
	var errTypePie = document.getElementById("errTypePie").getContext("2d");
	var errTypePieData= $("#errTypePie").attr("pieData");
	getChart(errTypePie,errTypePieData);
	
	var errInsertPie = document.getElementById("errInsertPie").getContext("2d");
	var errInsertPieData= $("#errInsertPie").attr("pieData");
	getChart(errInsertPie,errInsertPieData);
	
	var errSolvedPie = document.getElementById("errSolvedPie").getContext("2d");
	var errSolvedPieData= $("#errSolvedPie").attr("pieData");
	getChart(errSolvedPie,errSolvedPieData);
	
	
	
	

	//页面超链接
	var logLink = $("#loginLink");
	logLink.click(function(){Common.openPage("Login");});
	
	//查询
	qryButton.click(function(){
		var keyWord = $("#keyWord").val();
		var param = Wade.DataMap();
	    param.put("KEY_WORD",keyWord);
	    Common.get(function(value){
			Common.openPage("BainiuQry",param);	
		},"EX_ID");
	});
	//分页查询
	Common.pagination("BainiuIndex",Wade.DataMap());


	
});