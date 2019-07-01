require(["mobile","jquery","jcl","common"],function(Mobile,$,Wade,Common){
	//保存
	var ue_reason = UE.getEditor('exReason');
	var ue_solution = UE.getEditor('exSolution');
	var saveSoled = $("#saveSoled");
	saveSoled.click(function(){
		var reason;
		var solution;
		ue_reason.ready(function() {
			reason = ue_reason.getContent();
		});
		ue_solution.ready(function() {
			solution = ue_solution.getContent();
		});	
		var oldExId = saveSoled.attr("oldExId");
		var oldExdesc = saveSoled.attr("oldExdesc");
		var oldExEeason = saveSoled.attr("oldExEeason");
		var oldExSolution = saveSoled.attr("oldExSolution");
		if("" == solution){
			solution = oldExSolution;
		}
		if("" == reason){
			reason = oldExEeason;
		}
		var param = Wade.DataMap(); 
		param.put("EX_ID",oldExId);
		param.put("EX_DESC",oldExdesc);
		param.put("EX_REASON",reason);
		param.put("EX_SOLUTION",solution);
		param.put("EX_STATE","1");
		Common.callSvc("BainiuSolvedErr.updateErr",param,function(data){
			Common.showSuccess("修改成功")
		});
	});
	//确认
		var confirmSoled = $("#confirmSoled");
		confirmSoled.click(function(){
		var reason;
		var solution;
		ue_reason.ready(function() {
			reason = ue_reason.getContent();
		});
		ue_solution.ready(function() {
			solution = ue_solution.getContent();
		});	
		var oldExId = saveSoled.attr("oldExId");
		var oldExdesc = saveSoled.attr("oldExdesc");
		var oldExEeason = saveSoled.attr("oldExEeason");
		var oldExSolution = saveSoled.attr("oldExSolution");
		if("" == solution){
			solution = oldExSolution;
		}
		if("" == reason){
			reason = oldExEeason;
		}
		var param = Wade.DataMap(); 
		param.put("EX_ID",oldExId);
		param.put("EX_DESC",oldExdesc);
		param.put("EX_REASON",reason);
		param.put("EX_SOLUTION",solution);
		param.put("EX_STATE","2");
		Common.callSvc("BainiuSolvedErr.updateErr",param,function(data){Common.showSuccess("确认成功");
			Common.openPage("UnsolvedErrQry");
		});
	});
	
});