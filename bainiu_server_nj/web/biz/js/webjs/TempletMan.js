require(["mobile","jquery","jcl","layer","common","util","ZeroClipboard"],function(Mobile,$,Wade,Layer,Common,Util,ZeroClipboard)
{
	window['ZeroClipboard']=ZeroClipboard;
	var ue_temp	 =  UE.getEditor('TEMPLET_CONTENT');
	var ue_tempnew	 =  UE.getEditor('NTEMPLET_CONTENT');
	
	$(function(){  
		Common.footerClass();
	});  
	
    //创建/取消创建
    $('#newtem_btn').click(function(){
    	var params = Wade.DataMap();
    	$("#create_Templet div input,select,textarea").each(function(){
    		params.put($(this).attr("id"),$(this).val());
    	});
    	
		 var desc;
		 ue_temp.ready(function() {
		 	desc = ue_temp.getContent();
		 });
		 params.put("TEMPLET_CONTENT",desc);
    	
 	  	params.put("STATE","0");
 	   	Common.callSvc("Templet.dealTemplet",params,function(resultData){
			if(resultData.get("RETRUN_STR")!="")
			{
				Common.showSuccess(resultData.get("RETRUN_STR"),function(){
					//刷新
			    	var param = Wade.DataMap();
			    	param.put("TEMPLET_NAME",$("#TEMPLET_NAME").val());
			    	param.put("STATE",$("#STATE").val());
			    	Common.callSvc("Templet.querySelectType",param,function(resultData){
						Common.openPage("TempletMan",param);
					});					
				});
			}
		});
    });
    $('#cancelnewtem_btn').click(function(){
    	$(".create_Templet").slideUp();
    	return;
    });	

    $('#chgtem_btn').click(function(){
    	var params = Wade.DataMap();
    	$("#chg_Templet div input").each(function(){
    		//时间框问题
    		params.put($(this).attr("id").substr(1),$(this).val());
    	});
    	
		 var desc;
		 ue_tempnew.ready(function() {
		 	desc = ue_tempnew.getContent();
		 });
		 params.put("TEMPLET_CONTENT",desc);    	
    	
        var x=0,m=0;
        $("[name = chkItem]:checkbox").each(function (index){
  	      if($(this).is(':checked'))
  	      {
  	    	 if(x!=0||$(this).attr("STATE")!="0")
  	    	 {
    	       Common.showFail('只能选择一条生效的记录修改');
    	       m = 1;
        	   return;
      	     }
  	         params.put('TEMPLET_ID',$(this).attr("TEMPLET_ID"));
  	         x++;
  	       }
        });
    	if(x==0)
    	{
	       Common.showFail('请选择一条生效记录修改');
    	   return;
  	    }
    	if(m==0)
  	  		modifyIshareInfo(params);    	
    });
    
    $('#cancelchgtem_btn').click(function(){
    	$(".chg_Templet").slideUp();
    	return;
    });	    

    $('#templetQ').click(function()//查询
    {
    	var param = Wade.DataMap();
    	Common.callSvc("Templet.queryResultData",param,function(resultData){});	//下载后参数未传递到界面上，查询前得先刷新下获取界面参数
    	param.put("TEMPLET_NAME",$("#TEMPLET_NAME").val());
    	param.put("STATE",$("#STATE").val());
    	Common.callSvc("Templet.queryTemplet",param,function(resultData){
    		
    	});
    	Common.footerClass();
    });
    
    function dealTempletState(state)
    {
       var params = Wade.DataMap();
       //Common.callSvc("Ishare.queryResultData",params,function(resultData){});	//下载后参数未传递到界面上，查询前得先刷新下获取界面参数
       var paramN = getSelectedItemParam(state);
	   if(paramN.length==0)
	   {
		   Common.showFail('请至少选择一条记录！');
		   return;
	   }
	   params.put("CHK_LIST",paramN);
	   Common.callSvc("Templet.dealTemplet",params,function(resultData){
			if(resultData.get("RETRUN_STR")!="")
			{
				Common.showSuccess(resultData.get("RETRUN_STR"),function(){
					//刷新
			    	var param = Wade.DataMap();
			    	param.put("TEMPLET_NAME",$("#TEMPLET_NAME").val());
			    	param.put("STATE",$("#STATE").val());
			    	Common.callSvc("Templet.querySelectType",param,function(resultData){
						Common.openPage("TempletMan",param);
					});
				});
			}
		});
    }
    
    $('#templetNew').click(function()//创建
    {	
    	$(".chg_Templet").slideUp();
    	if($(".create_Templet").css('display')=='none')
    		$(".create_Templet").slideDown();
    	else
    		$(".create_Templet").slideUp();
		return;
    });   
    
    $('#templetChg').click(function()//修改
    {
       $(".create_Templet").slideUp();
	   if($(".chg_Templet").css('display')=='none')
		   $(".chg_Templet").slideDown();
	   else
		   $(".chg_Templet").slideUp();	   
	   return;
    });    
    
    $('#templetEnd').click(function()//结束
    {	
        Layer.prompt({
            title: '结束模板需要原因',
            formType: 2 //prompt风格，支持0-2(0-明文密码 1-密文 2-文本)
        }, function(text){
        	$("#REMARK").val(text);
    		dealTempletState('1');
        });  
    }); 
    
    function modifyIshareInfo(param){
		Common.callSvc("Templet.chgTemplet",param,function(resultData){
			if(resultData.get("result")!="0")
			{
				Common.showSuccess("操作成功",function(){
					Common.openPage("TempletMan","");
				});				
			}
			else
			{
				Common.showFail("操作失败:"+resultData.get("resultInfo"));
			}
		}); 
	}
    
 });