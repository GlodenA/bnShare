require(["mobile","jquery","jcl","common"],function(Mobile,$,Wade,Common)
{
	//分页查询
	Common.pagination("ForLeaveC");
	//tab切换
    //dataTable显示隐藏切换错位重绘
    $('.nav-tabs a').click(function (e) {
        e.preventDefault();
    	var param = Wade.DataMap();
     	if($(this).attr("id")=="fur_apply")
    	{
    		Common.openPage("ForLeave",param) ;
    	}
    	if($(this).attr("id")=="apply_query")
    	{
    		Common.openPage("ForLeaveC",param) ;
    	}
    });

    $('#sub').click(function()
    {
    	var reqType = $("#REQ_TYPE").val();
    	var ntName = $("#NT_NAME").val();
    	var reqState = $("#REQ_STATE").val();
    	var param = Wade.DataMap();
    	Common.callSvc("Leave.queryResultData",param,function(resultData){});	//下载后参数未传递到界面上，查询前得先刷新下获取界面参数
    	param.put("REQ_TYPE",reqType);
    	param.put("NT_NAME",ntName);
    	param.put("REQ_STATE",reqState);
		Common.callSvc("Leave.queryOwnLeave",param,function(resultData){	
			//回填
	    	$("#REQ_TYPE").val(reqType);
	    	$("#NT_NAME").val(ntName);
	    	$("#REQ_STATE").val(reqState);
	    	Common.openPage("ForLeaveC",param);
	    	Common.footerClass();
		});
    });
    
    $('#subEnd').click(function()
    {
    	dealLeave('3');
    });    
    
    $("#export").click(function()
    {
    	var param = Wade.DataMap();
        Common.confirm('确定导出请假信息吗？', {
            btn: ['确定','取消'],title: '导出确认'
        }, function(index){
        	document.location.href = "exportFile?ACTION=userAskleave&REQ_TYPE="+$("#REQ_TYPE").val()+"&NT_NAME="+$("#NT_NAME").val()+"&REQ_STATE="+$("#REQ_STATE").val()+"&USERIDS="+$("#userID").val();
        	Common.close(index);
        }, function(){        	
        });
    });

    $('#subCancel').click(function()
	{
		dealLeave('99');
	});     

    $("#checkall").bind("click", function ()
    {
//    	var isChecked=$("#checkall").attr("checked");
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
    	if(userChecked){
    		$("#userType").hide();
    	}else{
    		$("#userType").show();
    	}
    }
    
    $("#modal-close").bind("click", function (){
    	$(".modal-box").hide();
    	$("#bg").hide();
    });
    
    $("#example1 tbody tr td[name='itemCkecked']").bind("click", function (){
    	var innerHtml = "";
    	var user_name = $(this).parent().attr("USER_NAME");
    	var user_phone = $(this).parent().attr("USER_PHONE");
    	var bak_user_name = $(this).parent().attr("BAK_USER_NAME");
    	var bak_user_phone = $(this).parent().attr("BAK_USER_PHONE");
    	var work_subgroup = $(this).parent().attr("WORK_SUBGROUP");	//模块
    	var out_date = $(this).parent().attr("OUT_DATE");			//离开时间
    	var out_place = $(this).parent().attr("OUT_PLACE");			//目的地
    	var back_date = $(this).parent().attr("BACK_DATE");			//预计返回时间
    	var req_time = $(this).parent().attr("REQ_TIME");			//申请日期
    	var req_type = $(this).parent().attr("REQ_TYPENAME");		//休假类型
    	var req_mark = $(this).parent().attr("REQ_MARK");			//休假事由
    	var group_name = $(this).parent().attr("GROUP_NAME");		//组别
    	innerHtml = innerHtml
    		+'<tr>'
    		+'	<td class="active">姓名</td>'
    		+'	<td>'+user_name+'</td>'
    		+'	<td class="active">电话号码</td>'
    		+'	<td>'+user_phone+'</td>'
    		+'</tr>'
    		+'<tr>'
    		+'	<td class="active">B角联系人</td>'
    		+'	<td>'+bak_user_name+'</td>'
    		+'	<td class="active">B角联系电话</td>'
    		+'	<td>'+bak_user_phone+'</td>'
    		+'</tr>'
    		+'<tr>'
    		+'	<td class="active">公司</td>'
    		+'	<td>亚信</td>'
    		+'	<td class="active">组别</td>'
    		+'	<td>'+group_name+'</td>'
    		+'</tr>'
    		+'	<td class="active">模块</td>'
    		+'	<td colspan="3">'+work_subgroup+'</td>'
    		+'</tr>'
    		+'<tr>'
    		+'	<td class="active">离开时间</td>'
    		+'	<td>'+out_date+'</td>'
    		+'	<td class="active">目的地</td>'
    		+'	<td>'+out_place+'</td>'
    		+'</tr>'
    		+'<tr>'
    		+'	<td class="active">预计返回时间</td>'
    		+'	<td>'+back_date+'</td>'
    		+'	<td class="active">申请日期</td>'
    		+'	<td>'+req_time+'</td>'
    		+'</tr>'
    		+'</tr>'
    		+'	<td class="active">休假类型</td>'
    		+'	<td colspan="3">'+req_type+'</td>'
    		+'</tr>'
    		+'</tr>'
    		+'	<td class="active">休假事由</td>'
    		+'	<td colspan="3">'+req_mark+'</td>'
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

    function getSelectedItemParam(flag)
    {
      var params= Wade.DatasetList();
      var ps=$("#PS").val();
      $("[name = chkItem]:checkbox").each(function (index){
	      if($(this).is(':checked'))
	      {
	    	   var leaveId=$(this).attr("ID");
	           var userId=$(this).attr("USER_ID");
	           var state=$(this).attr("STATE");
	           var firChk=$(this).attr("FIR_CHK_USERID");
	           var param = Wade.DataMap();
	           param.put('USER_ID',userId);
	           param.put('ID',leaveId);
	           var stateN;
	           if(state=='0' && flag =='2'){
	        	   param.put('STATE','1');//新状态
	           }
	           else if(state=='0' && flag =='-2'){
	        	   param.put('STATE','-1');//新状态
	           }
	           else
	        	   param.put('STATE',flag);
	           
	           param.put('FIR_CHK_USERID',firChk);
	           param.put('PS',ps);
		       params.put(param);
	       }
       });
 	   return params;
    }

    function dealLeave(type){
    	var params = Wade.DataMap();
    	Common.callSvc("Leave.queryResultData",params,function(resultData){});	//下载后参数未传递到界面上，查询前得先刷新下获取界面参数
    	
		   var paramN = getSelectedItemParam(type);//传值-审核 注意区分一级还是二级
		   if(paramN.length==0)
		   {
			   Common.showFail('请至少选择一条记录！');
			   return;
		   }
		   params.put("CHK_LIST",paramN);
		   Common.callSvc("Leave.chkLeave",params,function(resultData){
				if(resultData.get("RETRUN_STR")!="")
				{
					Common.showSuccess(resultData.get("RETRUN_STR"));
					//刷新
			    	var param = Wade.DataMap();
			    	param.put("REQ_TYPE",$("#REQ_TYPE").val());
			    	param.put("NT_NAME",$("#NT_NAME").val());
			    	param.put("REQ_STATE",$("#REQ_STATE").val());
			    	Common.callSvc("Leave.queryOwnLeave",param,function(resultData){
						Common.openPage("ForLeaveC",param);
					});
				}
			});
    }

    $('#subChkNo').click(function(){
    	if($("#PS").val()=='')
		{
    		Common.showFail('审批不通过需要填写原因！');
		   return;
		}
    	dealLeave('-2');
    });

    $('#subChkYes').click(function(){
    	dealLeave('2');
	});
    
});