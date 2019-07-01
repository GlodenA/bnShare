require(["mobile","jquery","jcl","layer","common"],function(Mobile,$,Wade,Layer,Common)
{
	//分页查询
	Common.pagination("UserMan");		
	
    //用户创建/取消创建
    $('#newis_btn').click(function(){
    	var params = Wade.DataMap();
    	$("#create_user div input,select,textarea").each(function(){
    		if($(this).val()!="")
    			params.put($(this).attr("id"),$(this).val());
    	});
 	   	Common.callSvc("Reg.doRegister",params,function(resultData){
	 	   	if ("true" == resultData.get("IS_REG_OK")) {
	 	   		Common.showSuccess("新增成功");
	 	   		//清空
	 	    	$("#create_user div input,select,textarea").each(function(){
	 	    		$(this).val("");
	 	    	});
	 	    	$(".create_user").slideUp();
	 	   	}
	 	   	else
	 	   		Common.showFail(resultData.get("ERR_INFO"));
		});
    });
    $('#cancelnewis_btn').click(function(){
    	$(".create_user").slideUp();
    	return;
    });	

    $('#chgis_btn').click(function(){
    	var params = Wade.DataMap();
    	$("#chg_user div input").each(function(){
    		//时间框问题
    		params.put($(this).attr("id").substr(1),$(this).val());
    	});
    	
        var x=0,m=0;
        $("[name = chkItem]:checkbox").each(function (index){
  	      if($(this).is(':checked'))
  	      {
  	    	 if(x!=0||$(this).attr("STATE")!="0")
  	    	 {
    	       Common.showFail('只能选择一条生效中的用户修改');
    	       m = 1;
        	   return;
      	     }
  	         params.put('USER_ID',$(this).attr("USER_ID"));
  	         x++;
  	       }
        });
    	if(x==0)
    	{
	       Common.showFail('请选择一条生效用户修改');
    	   return;
  	    }
    	if(m==0)
  	  		modifyUserInfo(params);    	
    });
    
    $('#cancelchgis_btn').click(function(){
    	$(".chg_user").slideUp();
    	return;
    });

    $('#userQ').click(function()//查询
    {
		var param = Wade.DataMap();
 	    if($('#qaddkey').val()=="")
 	    {
 		   Common.showFail('关键字不可为空');
 		   return;
 	    }		
		param.put("KEY",$('#qaddkey').val());
   		qryUser(param);
    });  
    function qryUser(param)
    {
   		Common.callSvc("Group.queryUser",param,function(resultData){
   			var innerHtml = "";
   			var paramResp =resultData.get("USERLIST");
   			for(var i=0;i<resultData.get("USERLIST").length;i++)
   			{
   				var stateName = paramResp.get(i).get("STATE")=="0"?"生效":"无效";
				innerHtml = innerHtml + '<tr NAME="'+paramResp.get(i).get("NAME")+'" PHONE="'+paramResp.get(i).get("PHONE")+'" USER_ID="'+paramResp.get(i).get("USER_ID")+'">'
				+'<td><input  name="chkItem" type="checkbox" value="" STATE="'+paramResp.get(i).get("STATE") +'" USER_ID="'+paramResp.get(i).get("USER_ID")+'"></td>'
				+'<td>'+ paramResp.get(i).get("NAME")+'</td>'
				+'<td>'+ paramResp.get(i).get("PHONE")+'</td>'
				+'<td>'+ paramResp.get(i).get("EMAIL")+'</td>'
				+'<td>'+ paramResp.get(i).get("USER_ACCT")+'</td>'
				+'<td>'+ paramResp.get(i).get("ORG_NAME")+'</td>'
				+'<td>'+ stateName+'</td>'
				//+'<td>'+ paramResp.get(i).get("STATE")+'</td>'
				+'</tr>';  	
   			}
   			//innerHtml = "<tr><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
   			$('#queryforadd').html(innerHtml);
   		}); 
		footerClass();
    }
	
    function dealEnrolOut(paramN)
    {       
 	   if(paramN.length==0)
 	   {
 		   Common.showFail('请至少选择一个用户！');
 		   return;
 	   }
 	   
 	   var params = Wade.DataMap();
 	   params.put("CHK_LIST",paramN);
 	   Common.callSvc("User.delUser",params,function(resultData){
 			if(resultData.get("RETRUN_STR")!="")
 			{
 				Common.showSuccess(resultData.get("RETRUN_STR"));
				var paramQ = Wade.DataMap();
				paramQ.put("KEY",$('#qaddkey').val());
				qryUser(paramQ);
 			}
 		});
    }	
    
    $('#userNew').click(function()//创建
    {	
    	$(".chg_user").slideUp();
    	if($(".create_user").css('display')=='none')
    		$(".create_user").slideDown();
    	else
    		$(".create_user").slideUp();
		return;
    });   
    
    $('#userChg').click(function()//修改
    {
       $(".create_user").slideUp();
	   if($(".chg_user").css('display')=='none')
		   $(".chg_user").slideDown();
	   else
		   $(".chg_user").slideUp();	   
	   return;
    });    
    
    $('#userDel').click(function()//删除
    {	
        Layer.prompt({
            title: '取消需要原因',
            formType: 2 //prompt风格，支持0-2(0-明文密码 1-密文 2-文本)
        }, function(text){
        	$("#REMARK").val(text);
    		var paramN = getSelectedItemParam('2');
    		dealEnrolOut(paramN);
        });  
    }); 
    
    function modifyUserInfo(param){
		Common.callSvc("User.updUser",param,function(resultData){
			if(resultData.get("result")=="0")
			{
				    Common.showSuccess("操作成功");
		 	   		//清空
		 	    	$("#chg_user div input,select,textarea").each(function(){
		 	    		$(this).val("");
		 	    	});
				    $(".chg_user").slideUp();
				    
					var paramQ = Wade.DataMap();
					paramQ.put("KEY",$('#qaddkey').val());
					qryUser(paramQ);		
			}
			else
			{
				Common.showFail("操作失败:"+resultData.get("resultInfo"));
			}
		}); 
	}

    $("#checkall").bind("click", function ()//全选
    {
    	var isChecked=this.checked;
    	if(typeof(isChecked)=='undefined')
    	{
    		isChecked=false;
    	}
        $("[name = chkItem]:checkbox").each(function ()
        {
        	$(this).prop("checked", isChecked);
        });        
    });
    
    $("input[name = chkItem]").bind("click",function(){
    	var isChecked = false;
    	$("[name = chkItem]:checkbox").each(function ()
        {
    		if($(this).prop("checked")){
    			isChecked = true;
    		}
        });    	
    });

    function getSelectedItemParam(state)
    {
      var params= Wade.DatasetList();
      var remark=$("#REMARK").val();
      $("[name = chkItem]:checkbox").each(function (index){
	      if($(this).is(':checked'))
	      {
	    	   var user_id=$(this).attr("USER_ID");
	           var user_state=$(this).attr("STATE");
	           var param = Wade.DataMap();
	           param.put('USER_ID',user_id);
	           param.put('STATE',state);	          
	           param.put('REMARK',remark);
		       params.put(param);
	       }
       });
 	   return params;
    }
    //意见反馈和批次号底部显示
    function footerClass(){    	
		var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
		if(windowFlow){
			$("#frame_footer").attr("style","position:absolute; bottom:10px; color:#999; width:100%; text-align:center; line-height:150%;");
		}else{
			$("#frame_footer").attr("style","bottom:5px;text-align: center; width:100%");
		}
    }
});