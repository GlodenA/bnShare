require(["mobile","jquery","jcl","common","util","ZeroClipboard"],function(Mobile,$,Wade,Common,Util,ZeroClipboard)
{
		window['ZeroClipboard']=ZeroClipboard;
    	var ue_desc	 =  UE.getEditor('EX_DESC');
		var ue_reason =  UE.getEditor('EX_REASON');
		var ue_solution =  UE.getEditor('EX_SOLUTION');
	
	$("#sub").click(function()
	  {
			 var param = Wade.DataMap();
			 var desc;
			 var reason;
			 var solution;
			 ue_desc.ready(function() {
			 	desc = ue_desc.getContent();
			 });
			 ue_reason.ready(function() {
			 	reason = ue_reason.getContent();
			 });
			 ue_solution.ready(function() {
			 	solution = ue_solution.getContent();
			 });
			 if(Util.isEmpty($("#EX_ABSTRACT").val()))
			 {
				 Common.showFail('请填写问题概述');
				 return;
			 }
			 
			 if(Util.isEmpty(desc))
			 {
				 Common.showFail('请填写详细描述');
				 return;
			 }
			 param.put("EX_ABSTRACT",$("#EX_ABSTRACT").val());
			 param.put("EX_DESC",desc);
			 param.put("B_TYPE",$("#B_TYPE").val());
			 param.put("S_TYPE",$("#S_TYPE").val());
			 param.put("EX_REASON",reason);
			 param.put("EX_SOLUTION",solution);
			 			 
			 var keyParam = Wade.DataMap();
			 
			 $("div.plus-tag a").each(function(index)
			  {
				 keyParam.put(index,$.trim($(this).attr("title")));
			  });
			 param.put("KEY_LIST_MAP",keyParam);
			 
			 //param.put("ORG_NAME",$("#org").val());
			 Common.callSvc("Err.insertErr",param,function(resultData){	
				 
					if(resultData.get("result")=="0")
					{
						Common.showSuccess("录入成功");
						Common.openPage("ErrInsert");
					}
					else
					{
						Common.showFail("录入失败");
					}
				});
	 }); 
 });