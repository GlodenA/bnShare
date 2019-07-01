require(["mobile","jquery","jcl","common"],function(Mobile,$,Wade,Common)
{
	Common.pagination("RightMan");
	//分页查询
	$('#queryDetail').click(function()
	{
		var param = Wade.DataMap();
		
		param.put('USER_ACCT',$('#USER_ACCT').val());
		//param.put('STATE',$('#STATE').val());
		param.put('RIGHT_NAME',$('#RIGHT_NAME').val());

		Common.get(function(value)
		{
			Common.openPage("RightMan",param);	
		},"EX_ID");       
	});
		
	//授权切换按钮
    $('.auth_btn').click(function(){
        if($(this).hasClass('active')){
    	   $(this).bind("click",function()
          {
             var userID=$(this).parent().attr("user_id");
             var rightCode=$(this).parent().attr("right_code");         
             var param = Wade.DataMap();
 			
 			 param.put('USER_ID',userID);
 			 param.put('RIGHT_CODE',rightCode);
 			
 			 Common.callSvc("Right.cancelAuthorize",param,function(resultData){	 				 
 					if(resultData.get("result")!="0")
 					{
 						Common.showSuccess("取消权限成功");
   						Common.openPage("RightMan","");	
 					}
 					else
 					{
 						Common.showFail("取消权限失败");
 					}
 				});         	  
           });
        }else{
        	 $(this).bind("click",function()
             {
                var userID=$(this).parent().attr("user_id");
                var rightCode=$(this).parent().attr("right_code");
                var stateCode=$(this).parent().attr("state_code");

                if(stateCode=='1')
    			{
                	Common.showFail('该权限已被授权，不能重复授权');
    				return;
    			}
    			
                 var param = Wade.DataMap();
    			
    			  param.put('USER_ID',userID);
    			  param.put('RIGHT_CODE',rightCode);
    			
    			  Common.callSvc("Right.authorize",param,function(resultData){	
    				 
    					if(resultData.get("result")!="0")
    					{
    						Common.showSuccess("授权成功");
      						Common.openPage("RightMan","");	
    					}
    					else
    					{
    						Common.showFail("授权失败");
    					}
    				});     	  
              });
        }
    });

    $("#checkall").bind("click", function ()
    {		    	
    	//var isChecked=$("#checkall").attr("checked");
    	var isChecked=this.checked;
    	if(typeof(isChecked)=='undefined')
    	{
    		isChecked=false;
    	}
        $("[name = chkItem]:checkbox").each(function ()
        {
//          $(this).attr("checked", isChecked);
        	$(this).prop("checked", isChecked);
        });
    });
		    
	$('#authorizeAll').click(function()
	{
		var params = Wade.DataMap();
		var isValidate=false;
		var count=0;
		$("[name = chkItem]:checkbox").each(function (index)
		{
		  if($(this).is(':checked'))
          {
        	   var userID=$(this).attr("user_id");
               var rightCode=$(this).attr("right_code");
               var stateCode=$(this).attr("state_code");
               count++;
               if(stateCode=='1')
     			{
            	   Common.showFail('选择的用户中存在已被授权,请取消');
     				isValidate=false;
     				return false;
     			}
               
               var param = Wade.DataMap();   
               param.put('USER_ID',userID);
               param.put('RIGHT_CODE',rightCode);
               params.put(index,param);
               isValidate=true;
          }           
      });

	 if(count==0)
	 {
		 Common.showFail('请至少选择一个用户');
		 return;
	 }
	 
	 if(isValidate)
	 {
		Common.callSvc("Right.authorizeAll",params,function(resultData){	
		 
			if(resultData.get("result")!="0")
			{
				Common.showSuccess("审核成功");
				Common.openPage("RightMan","");	
			}
			else
			{
				Common.showFail("审核失败");
			}
		}); 
	 }
	}); 

	//授权按钮点击
	$('#authorizeRole').click(function(){
		var params = Wade.DataMap();
		var userID=$(this).parent().attr("user_id");
        var rightCode=$(this).parent().attr("right_code");
        var stateCode=$(this).parent().attr("state_code");
        if(stateCode=='1')
		{
        	Common.showFail('选择的用户已被授权');
			isValidate=false;
		}
       
       var param = Wade.DataMap();   
       param.put('USER_ID',userID);
       param.put('RIGHT_CODE',rightCode);
       params.put("HORIZE_ROLE",param);
       Common.callSvc("Right.authorizeAll",params,function(resultData){			 
			if(resultData.get("result")!="0")
			{
				Common.showSuccess("授权成功");
				Common.openPage("RightMan","");	
			}
			else
			{
				Common.showFail("授权失败");
			}
		}); 
	});
			
	$('#cancelAuthorizeRole').click(function(){
		var params = Wade.DataMap();
		var userID=$(this).parent().attr("user_id");
		var rightCode=$(this).parent().attr("right_code");
           
		var param = Wade.DataMap();   
		param.put('USER_ID',userID);
		param.put('RIGHT_CODE',rightCode);
		params.put("AUTHORIZE_ROLE",param);
		Common.callSvc("Right.cancelAuthorizeAll",params,function(resultData){	
			 
			if(resultData.get("result")!="0")
			{
				Common.showSuccess("删除成功");
				Common.openPage("RightMan","");	
			}
			else
			{
				Common.showFail("删除失败");
			}
		}); 
		
	});
			
	$('#cancelAuthorizeAll').click(function()
	{						
		var params = Wade.DataMap();
		var count=0;
		 $("[name = chkItem]:checkbox").each(function (index)
            {
              if($(this).is(':checked'))
              {
            	   var userID=$(this).attr("user_id");
	               var rightCode=$(this).attr("right_code");
	               
	               var param = Wade.DataMap();   
	               param.put('USER_ID',userID);
	               param.put('RIGHT_CODE',rightCode);
	               params.put(index,param);
	               count++;
              }	           
        });
		 
		 if(count==0)
		 {
			 Common.showFail('请至少选择一个用户');
			 return;
		 }
		 
		 Common.callSvc("Right.cancelAuthorizeAll",params,function(resultData){	     				 
				if(resultData.get("result")!="0")
				{
					Common.showSuccess("删除权限成功");
					Common.openPage("RightMan","");	
				}
				else
				{
					Common.showFail("删除权限失败");
				}
			}); 				
	});
			
	$('#apply').click(function()
	{				       
      Common.openDialog("ApplyRight","","","ROLE_CLASS");	
	});
	
	$('#sub').click(function()
	{
		 var param = Wade.DataMap();
		
		 param.put('RIGHT_CODE',$('#right').val());				
		 Common.callSvc("Right.addUserRight",param,function(resultData){	
			if(resultData.get("result")=="0")
			{
				Common.showSuccess("申请成功");
			    Common.closeDialog();
			}
			else
			{
				Common.showFail("申请失败");
			}
		}); 
  });
});