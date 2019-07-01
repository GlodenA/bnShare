require(["mobile","jquery","jcl","layer","common"],function(Mobile,$,Wade,Layer,Common){
	
	Common.pagination("BookMan");
	//tab切换
    //dataTable显示隐藏切换错位重绘
    $('.nav-tabs a').click(function (e) {
        e.preventDefault();
    	var param = Wade.DataMap();
     	if($(this).attr("id")=="fur_apply")
    	{
    		Common.openPage("BookMan",param) ;
    	}
    	if($(this).attr("id")=="apply_query")
    	{
    		Common.openPage("BookMan",param) ;
    	}
    });
	//借阅按钮单击事件
	$("button[name='loanBook']") .bind("click",function(){
		var param = Wade.DataMap();
		var bookId = $(this).parent().parent().children().eq(1).text();
		var bookName = $(this).parent().parent().children().eq(2).text();
		var bookPub = $(this).parent().attr("BOOK_PUB");
        $("#bo_name").text(bookName);
        $("#bo_pub").text(bookPub);
        $("#bo_id").val(bookId);
    	$("#loanInfo").show();
    	var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
   		if(windowFlow){
   			$("#bg").height($(window).height());
   		}else{
   			$("#bg").height($(".frame_content").height()+100);
   		}
   		$("#bg").show();
		/*Common.callSvc("BookMan.queryBookByID",param,function(resultData){
			
			
		});*/
	});
	//提交按钮单击事件
	$("button[name='submit']").bind("click",function(){
		var preBackTime = $("#BACK_TIME").val();
		var bookName = $("#bo_name").text();
		if(""==preBackTime){
			Common.showFail("请您选择还书时间！");
			return;
		}else{
			var param = Wade.DataMap();
			param.put("BOOK_ID",$("#bo_id").val());
			param.put("OPER_TYPE","0");//0:借书  1：还书  2：删除
			param.put("PRE_BACK_TIME",preBackTime);
			param.put("BOOK_NAME",bookName);
			Common.callSvc("BookMan.loanBook",param,function(resultData){
				if(resultData.get("result")!="0"){
					Common.showSuccess("借阅成功!",function(){
						Common.openPage("BookMan","");
					});				
				}else{
					Common.showFail("操作失败:"+resultData.get("resultInfo"));
				}
			});
		}
		
	});
	/**
	 * 图书归还按钮事件
	 */
	$("#bookBack").bind("click",function(){
	   dealBookState('1');
	});
	/**
	 * 图书创建按钮事件
	 */
	$("#bookCreate").bind("click",function(){
    	if($(".create_Book").css('display')=='none')
    		$(".create_Book").slideDown();
    	else
    		$(".create_Book").slideUp();
		return;
	});
	$('#createCancle').click(function(){
    	$(".create_Book").slideUp();
    	return;
    });	
	/**
	 * 图书创建提交按钮事件
	 */
	$("#createSubmit").bind("click",function(){
		var bookName = $("#BO_NAME").val();
		var bookAuthor = $("#BO_AUTHOR").val();
		var bookPub = $("#BO_PUBLISH").val();
		var ownerTeam = $("#OW_TEAM").val();
		var isbn = $("#ISBN").val();
		var copy = $("#COPY").val();
		var price = $("#PRICE").val();
		var buycnl = $("#BUYCNL").val();
		var param = Wade.DataMap();
		param.put("BOOK_NAME",bookName);
		param.put("BOOK_AUTHOR",bookAuthor);
		param.put("BOOK_PUBLISH",bookPub);
		param.put("OWNER_TEAM",ownerTeam);
		param.put("ISBN",isbn);
		param.put("COPY",copy);
		param.put("PRICE",price);
		param.put("BUYCNL",buycnl);
		Common.callSvc("BookMan.createBook",param,function(resultData){
			if(resultData.get("result")!="0"){
				Common.showSuccess("图书上架成功!",function(){
					Common.openPage("BookMan","");
				});
			}else{
				Common.showFail("操作失败:"+resultData.get("resultInfo"));
			}
		});
	});
	/**
	 * 查询按钮事件
	 */
	$("#bookQuery").bind("click",function(){
		var param = Wade.DataMap();
		param.put("BOOK_NAME",$("#INPUT_BOOKNAME").val());
		param.put("BOOK_COPY",$("#BOOK_COPY").val());
		Common.openPage("BookMan",param);
		footerClass();
	});
	function dealBookState(type){
		var params = getSelectedItemParam();
		if(params.length == 0){
			Common.showFail('请至少选择一条记录！');
			return;
		}
		var inparam = Wade.DataMap();
		inparam.put("BOOK_LIST",params);
		inparam.put("OPER_TYPE",type);
		if("1" == type){
			operMethod = "BookMan.backBook";
		}else if("2" == type){
			operMethod = "BookMan.backBook";
		}
		Common.callSvc(operMethod,inparam,function(resultData){
			if(resultData.get("result")!="0"){
				var count = resultData.get("result");
				Common.showSuccess("共计【"+count+"】本图书操作成功！",function(){
					Common.openPage("BookMan","");
				});				
			}else{
				Common.showFail("操作失败:"+resultData.get("resultInfo"));
			}
		});
	}
	function getSelectedItemParam(){
		var params= Wade.DatasetList();
        $("[name = chkItem]:checkbox").each(function (index){
	      if($(this).is(':checked'))
	      {
	    	   var bookId=$(this).parent().parent().children().eq(1).text();
	           var state=$(this).attr("STATE");
	           var param = Wade.DataMap();
	           param.put("BOOK_ID",bookId);
	           param.put("STATE",state);	
		       params.put(param);
	       }
        });
	 	return params;
	}
	$("#modal-close").bind("click", function (){
    	$("#loanInfo").hide();
    	$("#bg").hide();
    });
	
	$("#modal-close-bk").bind("click", function (){
    	$("#loanHis").hide();
    	$("#bg").hide();
    });
	
	
    $("#bookstable tbody tr td[name='itemCkecked']").bind("click", function (){//点击浮层
    	var innerHtml = "";
    	var user_name ="",loan_time ="",pre_backtime ="",real_backtime="",listuser="",remark = "";
    	var book_id		= $(this).parent().attr("BOOK_ID");
    	var loan_sum	= $(this).parent().attr("LOAN_SUM");
    	
    	var param = Wade.DataMap();
    	param.put("BOOK_ID",book_id);
		Common.callSvc("BookMan.qryLoanHis",param,function(resultData){
			
			listuser='<table width="100%"><tr><th width="10%" style="text-align:center;">借书人</th><th width="20%" style="text-align:center;">借阅时间</th><th width="20%" style="text-align:center;">预约归还</th> <th width="20%" style="text-align:center;">实际还书</th> <th width="30%" style="text-align:center;">备注</th></tr>';
   			for(var i=0;i<resultData.get("LOANLIST").length;i++)
   			{
   				user_name = resultData.get("LOANLIST").get(i).get("NAME");
   				loan_time = resultData.get("LOANLIST").get(i).get("LOAN_TIME");
   				pre_backtime = resultData.get("LOANLIST").get(i).get("PRE_BACK_TIME");
   				real_backtime = resultData.get("LOANLIST").get(i).get("REAL_BACK_TIME");
   				remark = resultData.get("LOANLIST").get(i).get("REMARK");
   				listuser = listuser+"<tr><td style='text-align:center;'>"+user_name+"</td><td style='text-align:center;'>"+loan_time+"</td><td style='text-align:center;'>"+pre_backtime+"</td><td style='text-align:center;'>"+real_backtime+"</td><td style='text-align:center;'>"+remark+"</td></tr>";
   			}
   			
   			var countList = "总借阅次数："+loan_sum;
   			listuser = countList+"<br><br>"+listuser+"</table>";

   	    	innerHtml = innerHtml+'<tr><td colspan="5"><div style="overflow-y:scroll; height:100px;">'+listuser+'</div></td></tr>';
   	    	
	    	$('#querygroup').html(innerHtml);
	    	$("#loanHis").show();
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
	
	//全选
	$("#checkall").bind("click", function (){
    	var isChecked=this.checked;
    	if(typeof(isChecked)=='undefined')
    	{
    		isChecked=false;
    	}
    	var userChecked = false;
        $("[name = chkItem]:checkbox").each(function ()
        {
        	$(this).prop("checked", isChecked);
        });
	});
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