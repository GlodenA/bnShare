require(["mobile","jquery","jcl","common","util"],function(Mobile,$,Wade,Common,Util)
{   
	function initgroupin(){
		$("#qaddkey").val('');
		$('#operreason').val('');
		$('#queryforadd').html("");
		$('.groupin').css({'display':'none'})
	}
	
    function intaddgroup(){
		$('.addgroup').css({'display':'none'});
    	$("#grouplevel").val('');
    	$("#groupname").val('');
    	$("#groupremark").val('');
    }
    
	function initTable(ins){
		var innerHtml = "";
		var paramResp =ins.get("GROUPUSER");
		for(var i=0;i<ins.get("GROUPUSER").length;i++)
		{
			innerHtml = innerHtml + "<tr NAME = "+paramResp.get(i).get("NAME")+"&nbsp;"+" PHONE= "+paramResp.get(i).get("PHONE") + "&nbsp;"+" USERACCT="+paramResp.get(i).get("USER_ACCT")+"&nbsp;"+" EMALL="+paramResp.get(i).get("EMAIL")+" ORG_NAME="+paramResp.get(i).get("ORG_NAME").replace(" ","&nbsp;")+"&nbsp;"+" NTACCT="+paramResp.get(i).get("NTACCT")+"&nbsp;"+" USER_ID="+ paramResp.get(i).get("USER_ID")+">"
//			innerHtml = innerHtml + "<tr NAME = "+paramResp.get(i).get("NAME")+" PHONE="+paramResp.get(i).get("PHONE")+" EMALL="+paramResp.get(i).get("EMAIL")+" BORG="+"CUC PSQ-Product1"+" NTACCT="+"CUC&nbsp;PSQ-Product1"+" USERACCT="+paramResp.get(i).get("USER_ACCT")+">"
			+'<td><input  name="chkItem" type="checkbox" value="" GROUP4_ID="'+ paramResp.get(i).get("GROUP4_ID")+'" USER_ID="'+ paramResp.get(i).get("USER_ID")+'" STATE="'+ paramResp.get(i).get("STATE")+'"></td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("RowNo")+'</td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("NAME")+'</td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("GS")+'</td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("NTACCT")+'</td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("GROUP1_NAME")+'</td>'
			//+'<td name="groupChecked">'+ paramResp.get(i).get("GROUP2_NAME")+'</td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("GROUP3_NAME")+'</td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("GROUP4_NAME")+'</td>'
			//+'<td name="groupChecked">'+ paramResp.get(i).get("ORG_NAME")+'</td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("BORG")+'</td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("PHONE")+'</td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("EMAIL")+'</td>'
			+'<td name="groupChecked">'+ paramResp.get(i).get("STATE")+'</td>'
			//+'<td name="groupChecked">'+ paramResp.get(i).get("START_TIME")+'</td>'
			//+'<td name="groupChecked">'+ paramResp.get(i).get("END_TIME")+'</td>'
			//+'<td name="groupChecked">'+ paramResp.get(i).get("REMARK")+'</td>'
			//+'<td name="groupChecked">'+ paramResp.get(i).get("UPD")+'</td>'
			+'<td style="display: none;" name="groupChecked">'+ paramResp.get(i).get("GROUP1_ID")+'</td>'
			+'<td style="display: none;" name="groupChecked">'+ paramResp.get(i).get("GROUP2_ID")+'</td>'
			+'<td style="display: none;" name="groupChecked">'+ paramResp.get(i).get("GROUP3_ID")+'</td>'
			+'<td style="display: none;" name="groupChecked">'+ paramResp.get(i).get("GROUP4_ID")+'</td>'
			+'<td style="display: none;" name="groupChecked">'+ paramResp.get(i).get("USER_ID")+'</td>'
			+'</tr>';    	
		}
		$('#querygroup').html(innerHtml);
		$("#querygroup tr td[name='groupChecked']").bind("click", function (){
			var html = "";
			var param = Wade.DataMap();
			if($(this).parent().attr("EMALL")){
				var name = $(this).parent().attr("NAME");
				var phone = $(this).parent().attr("PHONE");
				var email = $(this).parent().attr("EMALL");
				var orgName = $(this).parent().attr("ORG_NAME");
				var ntAcct = $(this).parent().attr("NTACCT");
				var userAcct = $(this).parent().attr("USERACCT");
				param.put("USER_ID",$(this).parent().attr("USER_ID"));
				param.put("EMALL",email);
				//根据邮箱查询人员信息
				Common.callSvc("Group.QueryUserList",param,function(resultData){
					var resDataList = resultData.get("USERITEM");
					if(resultData.get("USERTYPE")){
						html +='<tr>';
						html +='	<td class="active">姓名</td>';
						html +='	<td>'+resDataList.get("姓名")+'</td>';
						html +='	<td class="active">电话号码</td>';
						html +='	<td>'+resDataList.get("手机号")+'</td>';
						html +='</tr>';
						html +='<tr>';
						html +='	<td class="active">邮箱</td>';
						html +='	<td>'+email+'</td>';
						html +='	<td class="active">工号</td>';
						html +='	<td>'+resDataList.get("工号")+'</td>';
						html +='</tr>';
						html +='<tr>';
						html +='	<td class="active">入职日期</td>';
						html +='	<td>'+resDataList.get("入职日期")+'</td>';
						html +='	<td class="active">上级员工(员工号)</td>';
						html +='	<td>'+resDataList.get("上级员工(员工号)")+'</td>';
						html +='</tr>';
						html +='<tr>';
						html +='	<td class="active">部门</td>';
						html +='	<td colspan="3">'+resDataList.get("部门")+'</td>';
						html +='</tr>';
					}else{
						
						html +='<tr>';
						html +='	<td class="active">姓名</td>';
						html +='	<td>'+name+'</td>';
						html +='	<td class="active">电话号码</td>';
						html +='	<td>'+phone+'</td>';
						html +='</tr>';
						html +='<tr>';
						html +='	<td class="active">邮箱</td>';
						html +='	<td>'+email+'</td>';
						html +='	<td class="active">部门</td>';
						html +='	<td>'+orgName+'</td>';
						html +='</tr>';
						html +='<tr>';
						html +='	<td class="active">账号</td>';
						html +='	<td>'+userAcct+'</td>';
						html +='	<td class="active">NT账号</td>';
						html +='	<td>'+ntAcct+'</td>';
						html +='</tr>';
					}
					$('#querygroupTab').html(html);
			    	$(".modal-box").show();
			    	var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
			   		if(windowFlow){
			   			$("#bg").height($(window).height());
			   		}else{
			   			$("#bg").height($(".frame_content").height()+100);
			   		}
			   		$("#bg").show();
				});
			}
		});
	}
	
	
	
    $("#modal-close").bind("click", function (){
    	$(".modal-box").hide();
    	$("#bg").hide();
    });
	
    function init(){
/*    	$("#group1").empty();
    	$("#group2").empty();
    	$("#group3").empty();
    	$("#group4").empty();*/
    	//$("#groupt1").empty();
    	$("#groupt1 option:first").prop("selected", 'selected');
    	$("#groupt2").empty();
    	$("#groupt3").empty();
    	$("#groupt4").empty();
    	intaddgroup();
    	initgroupin();
    	$('.btn_qgroup').click();
		footerClass();
    	
/*		var param = Wade.DataMap();
		Common.callSvc("Group.init",param,function(resultData){
   			for(var i=0;i<resultData.get("GROUP1TREE").length;i++)
   			{
   				$("#group1").append("<option value='"+resultData.get("GROUP1TREE").get(i).get('GROUP1_ID')+"'>"+resultData.get("GROUP1TREE").get(i).get('GROUP1_NAME')+"</option>");    	
   				
   				$("#groupt1").append("<option value='"+resultData.get("GROUP1TREE").get(i).get('GROUP1_ID')+"'>"+resultData.get("GROUP1TREE").get(i).get('GROUP1_NAME')+"</option>");
   			}
   			$("#groupt1").append("<option value='+'>--+--</option>");
   			initTable(resultData);
   			
		});*/
    }
	
    $('.groupoper').change(function(){
        if($(this).val()=='add'){
            $('.groupin').css({'display':''});
            footerClass();
        }else{
        	initgroupin();
            $('.groupin').css({'display':'none'});
            footerClass();
        }
    });
    
    $('.btn_man').bind("click",function(){
    	var oper = $('.groupoper').val();
    	switch(oper){
    		case "add":  
    		case "del": 
    		case "mov":
    		case "cop": joinGroup(oper);break;
    		default :Mobile.tip("请选择操作");break;
    	}
    });
    
    function getSelectedItemParam(type)
    {
    	var params= Wade.DatasetList();
 	    if(type!="add"){
			 $("[name = chkItem]:checkbox").each(function (index)
		                {
		                  if($(this).is(':checked'))
		                  {
				               var userId=$(this).attr("USER_ID");
				               var groupId=$(this).attr("GROUP4_ID");
				               var param = Wade.DataMap();   
				               param.put('USER_ID',userId);
				               param.put('GROUP4_ID',groupId);
				               params.add(param);
		                  }
		            });
 	    }
 	    else
 	    {
			 $("[name = chkUserItem]:checkbox").each(function (index)
		                {
		                  if($(this).is(':checked'))
		                  {
				               var userId=$(this).attr("USER_ID");
				               var param = Wade.DataMap();   
				               param.put('USER_ID',userId);
				               params.add(param);
		                  }
		            });
 	    }	
			 return params; 	   
    }
    
    function joinGroup(type){
    	
    	if($("#groupt4").val()==null & type!='del'){
    		Mobile.tip("请选择目标团队再操作");
    		return ;
    	}
    	
		var param = Wade.DataMap();
		var paramO = Wade.DataMap();
		var paramN = Wade.DataMap();
		param.put('GROUP_OPER',type);
		param.put('REMARK',$("#operreason").val());
		if(type=="add"){
			paramN = getSelectedItemParam(type);
			param.put('N_GROUP4_ID',$("#groupt4").val());
			param.put('N_USERLIST',paramN);
		}
		else if(type=="del"){
			paramO = getSelectedItemParam(type);
			param.put('O_USERLIST',paramO);	
		}
		else{
			paramN = getSelectedItemParam(type);
			param.put('N_GROUP4_ID',$("#groupt4").val());
			param.put('O_USERLIST',paramN);
		}
   		Common.callSvc("Group.modifyGroup",param,function(resultData){	
			if(resultData.get("result")!="0")
			{
				Mobile.tip("人员操作成功!");
				init();
			}
			else
			{
				Mobile.tip("人员操作失败!");
			}
		}); 
   		
    }
    
    $("#selectall").bind("click", function (){
//		    	var isChecked=$("#selectall").attr("checked");
    			var isChecked=this.checked;
		    	if(typeof(isChecked)=='undefined')
		    	{
		    		isChecked=false;
		    	}
                $("[name = chkItem]:checkbox").each(function ()
                {
                	if($(this).attr("STATE")=="新增"){
//                		$(this).attr("checked", isChecked);
                		$(this).prop("checked", isChecked);
                	}
                });
     });
    
    $("#newuser").bind("click", function (){
//    	var isChecked=$("#newuser").attr("checked");
    	var isChecked=this.checked;
    	if(typeof(isChecked)=='undefined')
    	{
    		isChecked=false;
    	}
        $("[name = chkUserItem]:checkbox").each(function ()
        {
//            $(this).attr("checked", isChecked);
        	$(this).prop("checked", isChecked);
        });
    });
    
    $(".btn_qadd").click(function(){
		var param = Wade.DataMap();
		param.put("KEY",$('#qaddkey').val());
   		Common.callSvc("Group.queryUser",param,function(resultData){	
   			var innerHtml = "";
   			var paramResp =resultData.get("USERLIST");
   			for(var i=0;i<resultData.get("USERLIST").length;i++)
   			{
				innerHtml = innerHtml + '<tr>'
				+'<td><input  name="chkUserItem" type="checkbox" value="" USER_ID="'+paramResp.get(i).get("USER_ID")+'"></td>'
				+'<td>'+ paramResp.get(i).get("NAME")+'</td>'
				+'<td>'+ paramResp.get(i).get("NTACCT")+'</td>'
				+'<td>'+ paramResp.get(i).get("PHONE")+'</td>'
				+'<td>'+ paramResp.get(i).get("EMAIL")+'</td>'
				+'<td>'+ paramResp.get(i).get("ORG_NAME")+'</td>'
				+'</tr>';    	
   			}
   			//innerHtml = "<tr><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
   			$('#queryforadd').html(innerHtml);
		}); 
    });

    $("#group1").change(function(){
    	grouptchange("1","");
    });
    $("#group2").change(function(){
    	grouptchange("2","");
    });
    $("#group3").change(function(){
    	grouptchange("3","");
    });
    $("#group4").change(function(){
    	$('.btn_qgroup').click();
    });
    $("#groupt1").change(function(){
    	grouptchange("1","p");
    });
    $("#groupt2").change(function(){
    	grouptchange("2","p");
    });
    $("#groupt3").change(function(){
    	grouptchange("3","p");
    });
    $("#groupt4").change(function(){
    	addgroup("4");
    });
    
    function addgroup(level){
    	if($("#groupt1").val()=='+'&level=="1" 
    		|| $("#groupt2").val()=='+' &level=="2" 
    			|| $("#groupt3").val()=='+' &level=="3" 
    				|| $("#groupt4").val()=='+'&level=="4" ){
            $('.addgroup').css({'display':''});
    		$("#grouplevel").val(level);
    	}
    	else{
    		intaddgroup();
    	}
    }
    
    function grouptchange(le,oper){
    	if(oper=="p") 
    		addgroup(le);
    	else
    		$('.btn_qgroup').click();
    	var stridpre = oper=="p"?"groupt":"group";
    	var id = stridpre+le;
    	var nextInt = parseInt(le)+1;
    	var nextStr = nextInt.toString();
    	var idNext = stridpre+nextStr;
    	
    	var respData = oper=="p"?"PGROUP"+nextStr+"LIST":"GROUP"+nextStr+"TREE";
    	var respId = "GROUP"+nextStr+"_ID";
    	var respName = "GROUP"+nextStr+"_NAME";
    	
		var param = Wade.DataMap();
		for(var i=1;i<nextInt;i++){
			param.put('GROUP'+i.toString()+'_ID',$("#"+stridpre+i.toString()).val());
		}
		
		param.put('GROUP_LEVEL',oper+nextStr);
   		Common.callSvc("Group.queryGroupTree",param,function(resultData){	
   			for(var i=nextInt;i<5;i++){
   				$("#"+stridpre+i.toString()).empty();
   			}
   			for(var i=0;i<resultData.get(respData).length;i++)
   			{
   				$("#"+idNext).append("<option value='"+resultData.get(respData).get(i).get(respId)+"'>"+resultData.get(respData).get(i).get(respName)+"</option>");    	
   			}
   			if(oper=="p")	
   				$("#"+idNext).append("<option value='+'>--+--</option>");
   			$("#"+idNext).get(0).selectedIndex = -1;
   			
   			footerClass();
		});
    }
    
    function footerClass(){
    	//意见反馈和批次号底部显示
			var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
			if(windowFlow){
				$("#frame_footer").attr("style","position:absolute; bottom:0px; color:#999; width:100%; line-height:150%; border-top:1px solid #ddd; padding-top:10px; padding-bottom:10px; clear:both;");
			}else{
				$("#frame_footer").attr("style","color:#999; width:100%; line-height:150%; border-top:1px solid #ddd; padding-top:10px; padding-bottom:10px; clear:both;");
			}
    }
    
    function backDeal(group,groupId,groupName){
    	$("#"+group+" option[value='+']").remove();
		$("#"+group).append("<option value='"+groupId+"'>"+groupName+"</option>");
		$("#"+group).append("<option value='+'>--+--</option>");
		$("#"+group).val(groupId);
		$("#"+group).change();
    }
    
    $('.btn_addgroupok').click(function(){
		var param = Wade.DataMap();
		var groupLevel = $("#grouplevel").val();
		var groupName = $("#groupname").val();
		param.put('GROUP1_ID',$("#groupt1").val());
		param.put('GROUP2_ID',$("#groupt2").val());
		param.put('GROUP3_ID',$("#groupt3").val());
		param.put('GROUP_LEVEL',groupLevel);
		param.put('GROUP_NAME',groupName);
		param.put('REMARK',$("#groupremark").val());
		param.put('GROUP_OPER','addgroup');
   		Common.callSvc("Group.modifyGroup",param,function(resultData){	
			if(resultData.get("result")!="0")
			{
				Mobile.tip("群组操作成功!");
				var groupId = resultData.get("GROUP_ID");
				backDeal("groupt"+groupLevel,groupId,groupName);
				intaddgroup();
			}
			else
			{
				Mobile.tip("群组操作失败!");
			}
		}); 
    });
    
    $('.btn_addgroupesc').click(function(){
    	intaddgroup();
    });
    
    $('.btn_qgroup').click(function(){
    	quertGroupList();
    });
    
    $('#memstate').change(function(){
    	quertGroupList();
    });
    
    function quertGroupList(){
    	var param = Wade.DataMap();
		param.put('GROUP1_ID',$("#group1").val());
		param.put('GROUP2_ID',$("#group2").val());
		param.put('GROUP3_ID',$("#group3").val());
		param.put('GROUP4_ID',$("#group4").val());
		param.put('QUERY_KEY',$("#qkeys").val());
		param.put('QUERY_STATE',$("#memstate").val());
		
   		Common.callSvc("Group.queryGroup",param,function(resultData){	
				initTable(resultData);
				footerClass();
		}); 
    }
    
    $("#export").click(function()
    {
    	var group1 = $("#group1").val()==null?"":$("#group1").val();
    	var group2 = $("#group2").val()==null?"":$("#group2").val();
    	var group3 = $("#group3").val()==null?"":$("#group3").val();
    	var group4 = $("#group4").val()==null?"":$("#group4").val();
    	var qkeys = $("#qkeys").val()==null?"":$("#qkeys").val();
    	var memstate = $("#memstate").val()==null?"":$("#memstate").val();
    	
        Common.confirm('确定导出人员信息吗？', {
            btn: ['确定','取消'],title: '导出确认'
        }, function(index){
        	document.location.href = "exportFile?ACTION=GroupUserList&GROUP1_ID="+group1+"&GROUP2_ID="+group2+"&GROUP3_ID="+group3+"&GROUP4_ID="+group4+"&QUERY_KEY="+qkeys+"&QUERY_STATE="+memstate;
        	Common.close(index);
        }, function(){
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
