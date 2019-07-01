require(["mobile","jquery","jcl","common"],function(Mobile,$,Wade,Common){
	footerClass();
	var errList = $("#errList").children();
	var solvedErrList = $("#solvedErrList").children();
	var qryButton = $("#qry");
	var paginations = $("#paginations").children();
	
	//页面超链接
	var logLink = $("#loginLink");
	var errInsert = $("#errInsert");
	var solveErr = $("#solveErr");
	var solveErrDtl = $("#solveErrDtl");
	var userInsert = $("#userInsert");
	var rightMan = $("#rightMan");
	var feedBack = $("#feedBack");
	feedBack.click(function(){Common.openDialog("Comment");});
	logLink.click(function(){Common.openPage("Login");});
	errInsert.click(function(){Common.openPage("ErrInsert");});
	solveErr.click(function(){Common.openPage("UnsolvedErrQry");});
	solveErrDtl.click(function(){Common.openPage("SolvedErr");});
	userInsert.click(function(){Common.openPage("UserInsert");});
	rightMan.click(function(){Common.openPage("RightMan");});
	var goToMangerlink = $("#goToMangerlink");
	goToMangerlink.click(function(){Common.openPage("MBainiuIndex");});
	var regLink = $("#regLink");
	regLink.click(function(){Common.openPage("Reg");});
	
	
	//热词联想 
	$('.home_sh_form .sh_ipt input').keyup(function(){       	
                    var timer;
                    clearTimeout(timer);                  
                    timer = setTimeout(function(){
                        $.ajax({
                            url:'mobiledata?action=Index.getHotKey&data={"KEY_WORD":"'+$('.sh_ipt input').val()+'"}',
                            type:'POST',
                            dataType:'html',
                            data:'GBK',
                            success:function(res){
                                //将返回的内容填充到$('.async_area')里
                               /* $('.async_area').html('xxxxx');*/
                               var msg =  eval("(" + res + ")");;
                               if(0 == msg.X_RESULTCODE){
//                               		$('.async_area').empty();
                               		var list = msg.KEY_LIST;
                               		var lis = $('.bn_home .async_area ul li');
                               		if(list.length>0){
                               		//新增节点
//                               			var $ul = $('<ul></ul>');	
//                               			for(var i=0;i<list.length;i++){
//                               				var $li = $('<li>'+list[i].KEYWORD+'</li>');                               				
//                               				$ul.append($li);
//                              			}
//                               			$('.async_area').append($ul);
										for(var i=0;i<6;i++){
											lis[i].innerHTML='';
										}
										for(var i=0;i<list.length && i<6;i++){
											lis[i].innerHTML=list[i].KEYWORD;
										}
                               			
                               		}
                               		
                               }
                               
                            }
                        })
                    }, 500);
                $('.async_area').css('display','block');
                
            });
	
	//回车查询
	document.onkeydown =  function(e){
    	var ev = document.all ? window.event : e;
    	if(ev.keyCode ==13) {
				var keyWord = $("#keyWord").val();
				var sysDomain = $("#sysDomain").text();
				var param = Wade.DataMap();
	    		param.put("KEY_WORD",keyWord);
	    		param.put("CHOOSE_TYPE",sysDomain);
				Common.openPage("Qry",param);				
     		}
	}
	
	//查询
	qryButton.click(function(){
		var keyWord = $("#keyWord").val();
		var sysDomain = $("#sysDomain").text();
		var param = Wade.DataMap();
	    param.put("KEY_WORD",keyWord);
	    param.put("CHOOSE_TYPE",sysDomain);
	    Common.get(function(value){
			Common.openPage("Qry",param);	
		},"EX_ID");
	});
	
	//分页查询
	$.each(paginations,function(index,item){
		var obj = $(item);
	    var pagIdex = obj.children().attr("pagIdex");
	    if(pagIdex){
	    	obj.click(function(){
	    		var keyWord = $("#keyWord").val();
	    		var param = Wade.DataMap();
	    		param.put("KEY_WORD",keyWord);
	    		param.put("ROW_INDEX",pagIdex);
	    		Common.get(function(value){
					Common.openPage("Qry",param);	
				},"EX_ID");
	    	});
	    }
	});
	//详细查询
	$.each(errList,function(index,item){
		var obj = $(item);
	    var ex_id = obj.children()[0].children().attr("ex_id");
	    if(ex_id){
	    	obj.click(function(){
	    		var param = Wade.DataMap();
	    		param.put("EX_ID",ex_id);
	    		Common.get(function(value){
					Common.openPage("ErrDtl",param);	
				},"EX_ID");
	    	});
	    }
	});
	//未解答详细查询
	$.each(solvedErrList,function(index,item){
		var obj = $(item);
	    var ex_id = obj.children().attr("ex_id");
	    if(ex_id){
	    	obj.click(function(){
	    		var param = Wade.DataMap();
	    		param.put("EX_ID",ex_id);
	    		Common.get(function(value){
					Common.openPage("ErrDtl",param);	
				},"EX_ID");
	    	});
	    }
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
