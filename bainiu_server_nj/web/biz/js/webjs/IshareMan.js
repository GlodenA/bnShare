require(["mobile","jquery","jcl","layer","common"],function(Mobile,$,Wade,Layer,Common)
{
	//分页查询
	Common.pagination("IshareMan");	
	
	//tab切换
    $('.nav-tabs a').click(function (e) {
        e.preventDefault();
    	var param = Wade.DataMap();
     	if($(this).attr("id")=="fur_apply")
    	{
    		Common.openPage("IshareMan",param);
    	}
    	if($(this).attr("id")=="apply_query")
    	{
    		Common.openPage("IshareMan",param) ;
    	}
    });
	
    //爱分享创建/取消创建
    $('#newis_btn').click(function(){
    	var params = Wade.DataMap();
    	$("#create_Ishare div input,select,textarea").each(function(){
    		params.put($(this).attr("id"),$(this).val());
    	});
 	  	params.put("IS_STATE","0");
 	   	Common.callSvc("Ishare.dealIshare",params,function(resultData){
			if(resultData.get("RETRUN_STR")!="")
			{
				Common.showSuccess(resultData.get("RETRUN_STR"),function(){
					//刷新
			    	var param = Wade.DataMap();
			    	param.put("IS_NAME",$("#IS_NAME").val());
			    	param.put("IS_STATE",$("#IS_STATE").val());
			    	Common.callSvc("Ishare.queryIshare",param,function(resultData){
						Common.openPage("IshareMan",param);
					});					
				});
			}
		});
    });
    $('#cancelnewis_btn').click(function(){
    	$(".create_Ishare").slideUp();
    	return;
    });	

    $('#chgis_btn').click(function(){
    	var params = Wade.DataMap();
    	$("#chg_Ishare div input").each(function(){
    		//时间框问题
    		params.put($(this).attr("id").substr(1),$(this).val());
    	});
    	
        var x=0,m=0;
        $("[name = chkItem]:checkbox").each(function (index){
  	      if($(this).is(':checked'))
  	      {
  	    	 if(x!=0||$(this).attr("IS_STATE")!="0")
  	    	 {
    	       Common.showFail('只能选择一条报名中的记录修改');
    	       m = 1;
        	   return;
      	     }
  	         params.put('IS_ID',$(this).attr("IS_ID"));
  	         x++;
  	       }
        });
    	if(x==0)
    	{
	       Common.showFail('请选择一条报名中的活动修改');
    	   return;
  	    }
    	if(m==0)
  	  		modifyIshareInfo(params);    	
    });
    
    $('#cancelchgis_btn').click(function(){
    	$(".chg_Ishare").slideUp();
    	return;
    });	    
	
    function dealEnrolOut(param)
    {
		Common.callSvc("Ishare.enrolIshare",param,function(resultData){
			if(resultData.get("result")!="0")
			{
				Common.showSuccess("操作成功",function(){
					Common.openPage("IshareMan","");
				});				
			}
			else
			{
				Common.showFail("操作失败:"+resultData.get("resultInfo"));
			}
		}); 
    }
	
	$('.ishare_btn').bind("click",function(){
		var param = Wade.DataMap();
		var is_id=$(this).parent().attr("IS_ID");
		param.put('IS_ID',is_id);

        if($(this).hasClass('active')){
     	   $(this).bind("click",function()
           {
     	        Layer.prompt({
     	            title: '取消需要原因',
     	            formType: 2 //prompt风格，支持0-2
     	        }, function(text){
                  	param.put('STATE','2');
          			param.put('CANCEL_REASON',text);
          			dealEnrolOut(param); 
     	        });
     	        
            });
         }else{
         	 $(this).bind("click",function()
              {
               	param.put('STATE','0');
      			param.put('CANCEL_REASON','');	
      			dealEnrolOut(param);  	  
              });
         }
	});	

    $('#ishareQ').click(function()//查询
    {
    	var param = Wade.DataMap();
    	Common.callSvc("Ishare.queryResultData",param,function(resultData){});	//下载后参数未传递到界面上，查询前得先刷新下获取界面参数
    	param.put("IS_NAME",$("#IS_NAME").val());
    	param.put("IS_STATE",$("#IS_STATE").val());
		Common.openPage("IshareMan",param);
		footerClass();
    });
    
    function dealIshareState(state)
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
	   Common.callSvc("Ishare.dealIshare",params,function(resultData){
			if(resultData.get("RETRUN_STR")!="")
			{
				Common.showSuccess(resultData.get("RETRUN_STR"),function(){
					//刷新
			    	var param = Wade.DataMap();
			    	param.put("IS_NAME",$("#IS_NAME").val());
			    	param.put("IS_STATE",$("#IS_STATE").val());
			    	Common.callSvc("Ishare.queryIshare",param,function(resultData){
						Common.openPage("IshareMan",param);
					});					
				});
			}
		});
    }
    
    $('#ishareNew').click(function()//创建
    {	
    	$(".chg_Ishare").slideUp();
    	if($(".create_Ishare").css('display')=='none')
    		$(".create_Ishare").slideDown();
    	else
    		$(".create_Ishare").slideUp();
		return;
    });   
    
    $('#ishareChg').click(function()//修改
    {
       $(".create_Ishare").slideUp();
	   if($(".chg_Ishare").css('display')=='none')
		   $(".chg_Ishare").slideDown();
	   else
		   $(".chg_Ishare").slideUp();	   
	   return;
    });    
    
    $('#ishareEnd').click(function()//结束
    {	
		dealIshareState('1');
    });
    
    $('#ishareCancel').click(function()//取消
    {	
        Layer.prompt({
            title: '取消需要原因',
            formType: 2 //prompt风格，支持0-2(0-明文密码 1-密文 2-文本)
        }, function(text){
        	$("#REMARK").val(text);
    		dealIshareState('2');
        });  
    }); 
    
    function modifyIshareInfo(param){
		Common.callSvc("Ishare.chgIshare",param,function(resultData){
			if(resultData.get("result")!="0")
			{
				Common.showSuccess("操作成功",function(){
					Common.openPage("IshareMan","");
				});				
			}
			else
			{
				Common.showFail("操作失败:"+resultData.get("resultInfo"));
			}
		}); 
	}
    
    $("#export").click(function()//导出
    {
        Common.confirm('确定导出爱分享信息吗？', {
            btn: ['确定','取消'],title: '导出确认' //按钮
        }, function(index){
        	document.location.href = "exportFile?ACTION=userIshareList&IS_NAME="+$("#IS_NAME").val()+"&IS_STATE="+$("#IS_STATE").val()+"&USER_ID="+$("#userID").val();
        	Common.close(index);
        }, function(){
        });    	
    });

    $("#checkall").bind("click", function ()//全选
    {
    	var isChecked=this.checked;
    	if(typeof(isChecked)=='undefined')
    	{
    		isChecked=false;
    	}
    	var userChecked = false;
        $("[name = chkItem]:checkbox").each(function ()
        {
        	$(this).prop("checked", isChecked);
        	if($(this).attr("USER_ID")!=$("#userID").val()){
        		userChecked = true;
        	}
        });
        queryUserType(isChecked ? userChecked:true);
    });
    
    $("input[name = chkItem]").bind("click",function(){
    	var userChecked = false;
    	var isChecked = false;
    	$("[name = chkItem]:checkbox").each(function ()
        {
    		if($(this).prop("checked")){
    			isChecked = true;
    			if($(this).attr("USER_ID")!=$("#userID").val()){
            		userChecked = true;
            	}
    		}
        });
    	queryUserType(isChecked?userChecked:true);
    });
    
    function queryUserType(userChecked){
    	if(false/*userChecked*/){
    		$("#userType").hide();
    	}else{
    		$("#userType").show();
    	}
    }
    
    $("#modal-close").bind("click", function (){
    	$(".modal-box").hide();
    	$("#bg").hide();
    });
    
    $("#example1 tbody tr td[name='itemCkecked']").bind("click", function (){//点击浮层
    	var innerHtml = "";
    	var user_name ="",state_name ="",enrol_time ="",enrol_order ="",listuser="",user_email="";
    	var is_id		= $(this).parent().attr("IS_ID");    	
	    var is_explain 	= $(this).parent().attr("IS_EXPLAIN");
	    var is_expresult = $(this).parent().attr("IS_EXPRESULT");
	    var is_limitnum = $(this).parent().attr("IS_LIMITNUM");
    	var in_sum		= $(this).parent().attr("IN_SUM");    	
	    var wait_sum 	= $(this).parent().attr("WAIT_SUM");
	    var cancel_sum = $(this).parent().attr("CANCEL_SUM");	 
	    var upd_user 	= $(this).parent().attr("UPD_USER");
	    var upd_time = $(this).parent().attr("UPD_TIME");	  
	    var is_no = $(this).parent().attr("IS_NO");
	    var is_linkuser = $(this).parent().attr("IS_LINKUSER");
    	
    	var param = Wade.DataMap();
    	param.put("IS_ID",is_id);
		Common.callSvc("Ishare.qIshareEntry",param,function(resultData){
			
			listuser='<table width="100%"><tr><th width="30%">时间</th><th width="10%">顺序</th><th width="10%">姓名</th> <th width="10%">状态</th><th width="35%">邮箱</th></tr>';
   			for(var i=0;i<resultData.get("ISENTRYLIST").length;i++)
   			{
   				user_name = resultData.get("ISENTRYLIST").get(i).get("NAME");
   				state_name = resultData.get("ISENTRYLIST").get(i).get("STATE_NAME");
   				enrol_time = resultData.get("ISENTRYLIST").get(i).get("ENROL_TIME");
   				enrol_order = resultData.get("ISENTRYLIST").get(i).get("ENROL_ORDER");
   				user_email = resultData.get("ISENTRYLIST").get(i).get("EMAIL");
   				//listuser = listuser+enrol_time+"&nbsp顺序"+enrol_order+"&nbsp"+user_name+"&nbsp"+state_name+"<br>";
   				listuser = listuser+"<tr><td>"+enrol_time+"</td><td>"+enrol_order+"</td><td>"+user_name+"</td><td>"+state_name+"</td><td>"+user_email+"</td><td></tr>";
   			}
   			if(listuser.length>0){
   				var countList = "申请成功数："+in_sum+"&nbsp排队总数："+wait_sum+"&nbsp取消总数："+cancel_sum;
   				listuser = countList+"<br><br>"+listuser+"</table>";
   			}

   	    	innerHtml = innerHtml
    		+'<tr>'
    		+'	<td class="active" width="12%">期数</td>'
    		+'	<td>'+is_no+'</td>'
    		+'	<td class="active" width="12%">联系人员</td>'
    		+'	<td>'+is_linkuser+'</td>'
    		+'</tr>'   	    	
    		+'<tr>'
    		+'	<td class="active" width="12%">维护人员</td>'
    		+'	<td>'+upd_user+'</td>'
    		+'	<td class="active" width="12%">维护时间</td>'
    		+'	<td>'+upd_time+'</td>'
    		+'</tr>'
    		+'<tr>'
    		+'	<td class="active" width="12%">分享编号</td>'
    		+'	<td>'+is_id+'</td>'
    		+'	<td class="active" width="12%">人数限制</td>'
    		+'	<td>'+is_limitnum+'</td>'
    		+'</tr>'    		
    		+'</tr>'
    		+'	<td class="active" width="12%">简介</td>'
    		+'	<td colspan="3"><div style="overflow-y:scroll; height:30px;">'+is_explain+'</div></td>'
    		+'</tr>'    		
    		+'</tr>'    		
    		+'	<td class="active" width="12%">预期效果</td>'
    		+'	<td colspan="3"><div style="overflow-y:scroll; height:30px;">'+is_expresult+'</div></td>'    		
    		+'</tr>'
    		+'</tr>'
    		+'	<td class="active" width="12%">报名列表</td>'
    		+'	<td colspan="3"><div style="overflow-y:scroll; height:100px;">'+listuser+'</div></td>'
    		+'</tr>';
	    	$('#querygroup').html(innerHtml);
	    	$(".modal-box").show();
	    	$("#bg").height(document.body.clientHeight);
	    	var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
	   		if(windowFlow){
	   			$("#bg").height($(window).height());
	   		}else{
	   			$("#bg").height(document.body.clientHeight);
	   		}
	    	$("#bg").show();   	    	
		});
    });

    function getSelectedItemParam(state)
    {
      var params= Wade.DatasetList();
      var remark=$("#REMARK").val();
      $("[name = chkItem]:checkbox").each(function (index){
	      if($(this).is(':checked'))
	      {
	    	   var is_id=$(this).attr("IS_ID");
	           var is_state=$(this).attr("IS_STATE");
	           var param = Wade.DataMap();
	           param.put('IS_ID',is_id);
	           param.put('IS_STATE',state);	          
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
			$("#frame_footer").attr("style","position:absolute; bottom:10px; color:#999; width:100%; text-align:left; line-height:150%;");
		}else{
			$("#frame_footer").attr("style","bottom:5px;text-align: center; width:100%");
		}
    }
});