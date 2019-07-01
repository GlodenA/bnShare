require(["mobile", "jquery", "jcl", "common", "util"], function (Mobile, $, Wade, Common, Util) {
	//保存	
	var ue_reason = UE.getEditor("EX_REASON");
	var ue_solution = UE.getEditor("EX_SOLUTION");
	var saveSoled = $("#saveSoled");
	saveSoled.click(function () {
		var reason;
		var solution;
		ue_reason.ready(function () {
			reason = ue_reason.getContent();
		});
		ue_solution.ready(function () {
			solution = ue_solution.getContent();
		});
		var oldExId = saveSoled.attr("oldExId");
		var oldExdesc = saveSoled.attr("oldExdesc");
		var oldExEeason = saveSoled.attr("oldExEeason");
		var oldExSolution = saveSoled.attr("oldExSolution");
		if ("" == solution) {
			solution = oldExSolution;
		}
		if ("" == reason) {
			reason = oldExEeason;
		}
		var param = Wade.DataMap();
		param.put("EX_ID", oldExId);
		param.put("EX_DESC", oldExdesc);
		param.put("EX_REASON", reason);
		param.put("EX_SOLUTION", solution);
		param.put("EX_STATE", "1");
		Common.callSvc("BainiuSolvedErr.updateErr", param, function (data) {
			Common.showSuccess("问题已上传，请等待审批才能拿到悬赏金");
			Common.openPage("UnsolvedErrQry");
		});
	});
	
	//确认
	var confirmSoled = $("#confirmSoled");
	confirmSoled.click(function () {
		var reason;
		var solution;
		ue_reason.ready(function () {
			reason = ue_reason.getContent();
		});
		ue_solution.ready(function () {
			solution = ue_solution.getContent();
		});
		var oldExId = saveSoled.attr("oldExId");
		var oldExdesc = saveSoled.attr("oldExdesc");
		var oldExEeason = saveSoled.attr("oldExEeason");
		var oldExSolution = saveSoled.attr("oldExSolution");
		if ("" == solution) {
			solution = oldExSolution;
		}
		if ("" == reason) {
			reason = oldExEeason;
		}
		var param = Wade.DataMap();
		param.put("EX_ID", oldExId);
		param.put("EX_DESC", oldExdesc);
		param.put("EX_REASON", reason);
		param.put("EX_SOLUTION", solution);
		param.put("EX_STATE", "2");
		Common.callSvc("BainiuSolvedErr.updateErr", param, function (data) {
			Common.showSuccess("录入成功");
			Common.openPage("UnsolvedErrQry");
		});
	});
	var goback = $("#goback");
	goback.click(function () {
		Common.openPage("UnsolvedErrQry");
	});
	var delSoled = $("#delSoled");
	delSoled.click(function () {
		var param = Wade.DataMap();
		var oldLogId = delSoled.attr("oldLogId");
		param.put("LOG_ID", oldLogId);
		Common.callSvc("BainiuSolvedErr.deleteErr", param, function (data) {
			Common.showSuccess("删除成功");
			Common.openPage("UnsolvedErrQry");
		});		
	});	
});

