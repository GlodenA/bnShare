require(["mobile","jquery","jcl","common","util"],function(Mobile,$,Wade,Common,Util)
{
	    footerClass();
		Common.pagination("MsgBox");

        $("#selectall").bind("click", function ()
		    {
//		    	var isChecked=$("#selectall").attr("checked");
        		var isChecked=this.checked;
		    	if(typeof(isChecked)=='undefined')
		    	{
		    		isChecked=false;
		    	}
                $("[name = chkItem]:checkbox").each(function ()
                {
//                    $(this).attr("checked", isChecked);
                	$(this).prop("checked", isChecked);
                });
            });
       
        $("a[name = go2Detail]").each(function ()
         {
        	$(this).click(function()
            {
        		 var smsId=$(this).attr("S_ID");
                 var userId=$(this).attr("U_ID");
                 var state=$(this).attr("STATE");
                 var param = Wade.DataMap();  
              
                 param.put('USER_ID',userId);
                 param.put('SMS_ID',smsId);
                 param.put('SMS_STATE',state);
     		     Common.openPage("MsgDetail",param);
            });
         });
        
        $(".dropdown-toggle").click(function(){
        	$(".dropdown-menu").show();
        });

       function getSelectedItemParam(flag)
       {
    	    var params = Wade.DataMap();
  			 $("[name = chkItem]:checkbox").each(function (index)
		                {
		                  if($(this).is(':checked'))
		                  {
		                	   var smsId=$(this).attr("S_ID");
				               var userId=$(this).attr("U_ID");
				               var state=$(this).attr("STATE");
 				               var param = Wade.DataMap();   
				               param.put('USER_ID',userId);
				               param.put('SMS_ID',smsId);
				               param.put('SMS_STATE',state);
				               if(typeof(flag)!='undefined')
				               {
				            	   param.put('READ_FALG',flag); 
				               }
				               params.put(index,param);
		                  }		           
		            });
			 return params;
       }
	
	       $('#deleteMsg').click(function()
			{
	    	   var params = getSelectedItemParam('2');	    	   
	    	   if(params.length==0)
	    	   {
	    		   Common.showFail('请至少选择一个消息');
	    		   return;
	    	   }
	    	   
	    	   Common.callSvc("Msg.signMsg",params,function(resultData){	
   				 
					if(resultData.get("result")!="0")
					{
						Common.showSuccess("删除成功");
  						Common.openPage("MsgBox","");	
					}
					else
					{
						Common.showFail("删除失败");
					}
				}); 
			});
 
	       
	       $('#read').click(function()
	   			{
	   	    	   var params = getSelectedItemParam('1');
	   	    	   
	   	    	   if(params.length==0)
	   	    	   {
	   	    		   Common.showFail('请至少选择一个消息');	   	    		   
	   	    		   return;
	   	    	   }
	   	    	   
	   	    	   Common.callSvc("Msg.signMsg",params,function(resultData){	
	      				 
	   					if(resultData.get("result")!="0")
	   					{
	   						Common.showSuccess("标记已读成功");
	     					Common.openPage("MsgBox","");	
	   					}
	   					else
	   					{
	   						Common.showFail("标记已读失败");
	   					}
	   				}); 
	   			});

	       $('#noRead').click(function()
		   			{
		   	    	   var params = getSelectedItemParam('0');
		   	    	   
		   	    	   if(params.length==0)
		   	    	   {
		   	    		   Common.showFail('请至少选择一个消息');		   	    		   
		   	    		   return;
		   	    	   }
		   	    	   
		   	    	   Common.callSvc("Msg.signMsg",params,function(resultData){	
		      				 
		   					if(resultData.get("result")!="0")
		   					{
		   						Common.showSuccess("标记未读成功");
		     					Common.openPage("MsgBox","");	
		   					}
		   					else
		   					{
		   						Common.showFail("标记未读失败");
		   					}
		   				}); 
		   			});
	       
	       $('#deleteDetailMsg').click(function()
		   	{
	    	   var smsId=$(this).attr("S_ID");
               var userId=$(this).attr("U_ID");
               
	           var param = Wade.DataMap();   
               param.put('USER_ID',userId);
               param.put('SMS_ID',smsId);
               param.put('READ_FALG',"2"); 
               var params = Wade.DataMap();   
               params.put("1",param); 	   
		   	    	   Common.callSvc("Msg.signMsg",params,function(resultData){	
		      				 
		   					if(resultData.get("result")!="0")
		   					{
		   						Common.showSuccess("删除成功");
		     					Common.openPage("MsgBox","");	
		   					}
		   					else
		   					{
		   						Common.showFail("删除失败");
		   					}
		   				}); 
		   			});
	       $('#refreshMsg').click(function()
	   	    {
	   	    	 Common.openPage("MsgBox","");	
            });
            $('#agreeSub').click(function()
	   	    {
	   	    	var params = Wade.DataMap();
	   	    	params.put("STATE",$('#agreeSub').attr("state"));
	   	    	params.put("ID",$('#agreeSub').attr("leaveId"));
	   	    	params.put("SMS_ID",$('#agreeSub').attr("mailId"));
	   	    	params.put("PS",$('#you_idle').val());
	   	    	Common.callSvc("MsgBox.auditLeava",params,function(resultData){
	   	    		Common.msg(resultData.get("RETRUN_STR"));
	   	    		$('#agreeSub').hide();
	   	    		$('#disAgreeSub').hide();
	   	    		$('#you_idle').hide();
	   	    	});		
            });
            $('#disAgreeSub').click(function()
	   	    {	   	    
	   	    		var params = Wade.DataMap();
	   	    			params.put("STATE",$('#disAgreeSub').attr("state"));
	   	    			params.put("ID",$('#disAgreeSub').attr("leaveId"));
	   	    			params.put("SMS_ID",$('#disAgreeSub').attr("mailId"));
	   	    			params.put("PS",$('#you_idle').val());
	   	    	 	Common.callSvc("MsgBox.auditLeava",params,function(resultData){
	   	    		Common.msg(resultData.get("RETRUN_STR"));
	   	    		$('#agreeSub').hide();
	   	    		$('#disAgreeSub').hide();
	   	    		$('#you_idle').hide();
	   	    	});		
            });
			
	   
 });
	 function footerClass(){
    	//意见反馈和批次号底部显示
			var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
			if(windowFlow){
				$("#frame_footer").attr("style","position:absolute; bottom:0px; color:#999; width:100%; line-height:150%; border-top:0; padding-top:0px; padding-bottom:10px; clear:both;");
			}else{
				$("#frame_footer").attr("style","color:#999; width:100%; line-height:150%; border-top:0; padding-top:0px; padding-bottom:10px; clear:both;");
			}
    }

