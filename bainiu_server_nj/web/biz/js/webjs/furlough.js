require(["jquery","pickmeup","jcl","common","util"],function($,$P,Wade,Common,Util)
{
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
    $('#submitLeave').click(function()
    {
    	 if(Util.isEmpty($("#BAK_NT").val()))
		 {
    		 Common.showFail('请填写交接人');
			 return;
		 }
		 if(Util.isEmpty($("#FIR_CHK_NT").val()))
		 {
			 Common.showFail('请填写所属组长');
			 return;
		 }
		 if(Util.isEmpty($("#OUT_PLACE").val()))
		 {
			 Common.showFail('请填写目的地');
			 return;
		 }
		 if(Util.isEmpty($("#WORK_GROUP").val()))
		 {
			 Common.showFail('请填写公司地');
			 return;
		 }
		 if(Util.isEmpty($("#WORK_SUBGROUP").val()))
		 {
			 Common.showFail('请填写模块');
			 return;
		 }
		 if(Util.isEmpty($("#REQ_MARK").val()))
		 {
			 Common.showFail('请填写请假事由');
			 return;
		 }
	      var param = Wade.DataMap();

	      //2019-10-08 - 2019-10-24
		  var leaveDays= $('#date-s-t').val();
	      param.put("leaveDays",leaveDays);
	      
	      var bakNt = $('#BAK_NT').val();
	      var firCkNt = $('#FIR_CHK_NT').val();
	      if(bakNt.indexOf("|")>0)
	    	  param.put("BAK_NT",bakNt.split("|")[1]);
	      else
	    	  param.put("BAK_NT",bakNt);

	      if(firCkNt.indexOf("|")>0)
	    	  param.put("FIR_CHK_NT",firCkNt.split("|")[1]);
	      else
	    	  param.put("FIR_CHK_NT",firCkNt);
	      
	      param.put("REQ_TYPE",$('#REQ_TYPE').val());
	      param.put("OUT_PLACE",$('#OUT_PLACE').val());
	      param.put("WORK_GROUP",$('WORK_GROUP').val());
	      param.put("WORK_SUBGROUP",$('#WORK_SUBGROUP').val());
	      param.put("REQ_MARK",$('#REQ_MARK').val());
	      $("#submitLeave").attr("disabled","false").removeClass("sub").addClass("cancel");
	      Common.callSvc("Leave.submitLeave",param,function(resultData){
				if(resultData.get("result")=="0")
				{
					Common.openPage("ForLeave");
				}
				else
				{
					Common.showFail(resultData.get("result"));
				}
				$("#submitLeave").removeAttr("disabled").removeClass("cancel").addClass("sub");
			});
    });
    
    $('.bn_home .link1 .async_area ul li').mouseenter(function(){
        $(this).addClass('active')
    }).mouseleave(function(){
        $(this).removeClass('active')
    });
    $('.bn_home .link2 .async_area ul li').mouseenter(function(){
        $(this).addClass('active')
    }).mouseleave(function(){
        $(this).removeClass('active')
    });    
    $('.bn_home .link1 .async_area ul li').click(function(){
    	$('.home_sh_form .sh_ipt .link1 input').val($(this).text());
		var lis = $('.link1 .async_area ul li');//选择后清空历史
		for(var i=0;i<5;i++){
			lis[i].innerHTML='';
		}
    	$('.async_area').css('display','none');
    });
    $('.bn_home .link2 .async_area ul li').click(function(){
    	$('.home_sh_form .sh_ipt .link2 input').val($(this).text());
		var lis = $('.link2 .async_area ul li');
		for(var i=0;i<5;i++){
			lis[i].innerHTML='';
		}
    	$('.async_area').css('display','none');
    });    
	//热词联想 
	$('.home_sh_form .sh_ipt .link2 input').keyup(function(){
		var nttext = $('.sh_ipt .link2 input').val();
		if(nttext.length<2) {
			$('.async_area').css('display','none');
			return;
		}
        var timer;
        clearTimeout(timer);                  
        timer = setTimeout(function(){
        	var param = Wade.DataMap();
        	Common.callSvc("Leave.queryResultData",param,function(resultData){});	//下载后参数未传递到界面上，查询前得先刷新下获取界面参数
        	param.put("USER_ACCT",nttext);
        	Common.callSvc("User.getLinkAcct",param,function(res){
                if(res.get("X_RESULTCODE")=="0"){
                		var list = res.get("KEY_LIST");
                		var lis = $('.link2 .async_area ul li');
						for(var i=0;i<5;i++){
							lis[i].innerHTML='';
						}
                		if(list.length>0){//新增节点
							for(var i=0;i<list.length && i<5;i++){
								var listsub = list.get(i);
								lis[i].innerHTML=listsub.get("NAME")+"|"+listsub.get("USER_ACCT");
							}
                		}
                		else
                			lis[0].innerHTML="查找的账号不存在";
                } 
    		});
        }, 500);
	    $('.link2 .async_area').css('display','block');
	});      
	$('.home_sh_form .sh_ipt .link1 input').keyup(function(){
		var nttext = $('.sh_ipt .link1 input').val();
		if(nttext.length<2) {
			$('.async_area').css('display','none');//太短不联想
			return;
		}
        var timer;
        clearTimeout(timer);                  
        timer = setTimeout(function(){
        	var param = Wade.DataMap();
        	Common.callSvc("Leave.queryResultData",param,function(resultData){});	//下载后参数未传递到界面上，查询前得先刷新下获取界面参数
        	param.put("USER_ACCT",nttext);
        	Common.callSvc("User.getLinkAcct",param,function(res){
                if(res.get("X_RESULTCODE")=="0"){
                		var list = res.get("KEY_LIST");
                		var lis = $('.link1 .async_area ul li');
						for(var i=0;i<5;i++){//只联想5个
							lis[i].innerHTML='';
						}
                		if(list.length>0){//新增节点
							for(var i=0;i<list.length && i<5;i++){
								var listsub = list.get(i);
								lis[i].innerHTML=listsub.get("NAME")+"|"+listsub.get("USER_ACCT");
							}
                		}
                		else
                			lis[0].innerHTML="查找的账号不存在";
                } 
    		});
        }, 500);
	    $('.link1 .async_area').css('display','block');
	});    
});
