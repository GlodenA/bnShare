require(["mobile","jquery","jcl","common","util","iScroll"],function(Mobile,$,Wade,Common,Util,iScroll)
{
	Common.pagination("Personal.qryMyErr","Personal.qryMyErrTemp","qryMyErrParames","qryMyErrPagination","flashArea1");
	Common.pagination("Personal.qryMyDeal","Personal.qryMyDealTemp","qryMyDealParames","qryMyDealPagination","flashArea2");
	
	$('.per_profile .mod').on('click',function()
	{
	    if($(this).hasClass('btn-warning'))
	    {
	    	var phone = $("input[name = 'PHONE']").val();
	    	var reg = /^1\d{10}$/;
	    	if (!reg.test(phone)) {
	    		Common.showFail("号码格式有误");
	    		return false;
	    	};
	    	var email = $("input[name = 'EMAIL']").val();
	    	var myreg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	    	if(!myreg.test(email)){
	    		Common.showFail("邮箱格式有误");
	    		return false;
	    	}
	    	var postParam = Wade.DataMap();
	        $('.per_profile .edit').each(function(){
	            var cell = $(this);
	            var attrName=cell.children('input').eq(0).attr("name");
	            postParam.put(attrName,cell.children('input').eq(0).val());
	            if(attrName=="PASSWORD")
	            {
	            	cell.closest(".clearfix").css("display","none");
	            }
	            else
	            {
	            	var txt = cell.children('input').eq(0).val();
	  	            cell.text(txt);
	            }
	            
	            $(this).removeClass('active');
	        });
	        
	        $(this).text('修改');
	        $(this).removeClass('btn-warning');
	        Common.callSvc("Personal.modifyUser",postParam,function(resultData){					 
				if(resultData.containsKey("ERROR"))
				{
					Mobile.tip(resultData.get("ERROR"));
				}
			});      
	    } 	    
	    else
	    {	    	 
		        $('.per_profile .edit').each(function(){
		            if($(this).hasClass('active')){
		               return false;
		            }else{
		                var cell = $(this);
		                var txt = cell.text();
		                var input='';
		                var spanName=cell.attr("name");
		                if(spanName=="PASSWORD")
		                {
		                   input=cell.closest(".clearfix");
		                   input.css("display","block");
		                }
		                else
		                {
		                 input = $("<input placeholder='如实填写' name='"+spanName+"' type='text' value='" +txt+ "'/>");
		                  cell.html(input);
		                }
		                input.click(function () {
		                    return false;
		                });
		                input.trigger("focus");
		                $(this).addClass('active');
		            }
		        });
		    $(this).text('保存');
		    $(this).addClass('btn-warning');		  
	    }
	});
	 
});