require(["mobile","jquery","jcl","chart","layer","common"],function(Mobile,$,Wade,ChartPie,Layer,Common){	

	//反馈分页
	Common.pagination("MInitContent");

	//报表
	var getChart =  function(pieObj,pieData,mode){		
		if("Line"==mode){
			var piedataObj = getLineObj(pieData);
			char=new Chart(pieObj).Line(piedataObj);
		}
		if("Doughnut"==mode){
			var piedataObj = getDoughnutObj(pieData);
			char=new Chart(pieObj).Doughnut(piedataObj);
		}
		if("Radar"==mode){
			var piedataObj = getRadarObj(pieData);
			char=new Chart(pieObj).Radar(piedataObj);
		}
		if("Bar"==mode){
			var piedataObj = getBarObj(pieData);
			char=new Chart(pieObj).Bar(piedataObj);
		}
	}
	
	var getRadarObj =  function(pieData){
		var pieObj={};
		var piedatasObj={};
		var pieDataMap = new Wade.DataMap(pieData);
		var list = pieDataMap.get("LINEDATA");
		var labels = new Array();
		var data = new Array();
		for(var i=0;i<list.length;i++){
			labels[i]=list.get(i).get("NAME");
			data[i]=parseInt(list.get(i).get("COU"));
		}
   		pieObj.labels=labels;
   		var datasets =new Array();
   		piedatasObj.fillColor="#e2f5fd";
   		piedatasObj.strokeColor="#31b5ee";
   		piedatasObj.pointColor="rgba(151,187,205,1)";
   		piedatasObj.pointStrokeColor="#fff";
   		piedatasObj.data=data;
   		datasets[0]=piedatasObj;
   		pieObj.datasets=datasets;
		return pieObj;		
	}
	
	var getDoughnutObj =  function(pieData){
		var datas = new Array();
		var pieDataMap = new Wade.DataMap(pieData);
		var list = pieDataMap.get("LINEDATA");
		for(var i=0;i<list.length;i++){
			datas[i]={};
			datas[i].color=list.get(i).get("COLOR");
			datas[i].value=parseInt(list.get(i).get("NUM"));
			
		}	
		return datas;		
	}
	var getLineObj =  function(pieData){
		var pieObj={};
		var piedatasObj={};
		var pieDataMap = new Wade.DataMap(pieData);
		var list = pieDataMap.get("LINEDATA");
		var labels = new Array();
		var data = new Array();
		for(var i=0;i<list.length;i++){
			labels[i]=list.get(i).get("NAME");
			data[i]=parseInt(list.get(i).get("COU"));
		}
   		pieObj.labels=labels;
   		var datasets =new Array();
   		piedatasObj.fillColor="#faa819";
   		piedatasObj.strokeColor="#faa819";
   		piedatasObj.data=data;
   		datasets[0]=piedatasObj;
   		pieObj.datasets=datasets;
		return pieObj;		
	}
	var getBarObj =  function(pieData){
		var pieObj={};
		var piedatasObj={};
		var pieDataMap = new Wade.DataMap(pieData);
		var list = pieDataMap.get("LINEDATA");
		var labels = new Array();
		var data = new Array();
		for(var i=0;i<list.length;i++){
			labels[i]=list.get(i).get("NAME");
			data[i]=list.get(i).get("COU");
		}
   		pieObj.labels=labels;
   		var datasets =new Array();
   		piedatasObj.fillColor="#faa819";
   		piedatasObj.strokeColor="#faa819";
   		piedatasObj.data=data;
   		datasets[0]=piedatasObj;
   		pieObj.datasets=datasets;
		return pieObj;
	}
	
	
	var dealRank = document.getElementById("dealRank").getContext("2d");
	var pieData= $("#dealRank").attr("chartDate");
	getChart(dealRank,pieData,"Bar");
	var troubleRank = document.getElementById("troubleRank").getContext("2d");
	var pieData1= $("#troubleRank").attr("chartDate");
	getChart(troubleRank,pieData1,"Bar");
	var activerRank = document.getElementById("activerRank").getContext("2d");
	var pieData2= $("#activerRank").attr("chartDate");
	getChart(activerRank,pieData2,"Bar");
	var hotRank = document.getElementById("hotRank").getContext("2d");
	var pieData3= $("#hotRank").attr("chartDate");
	getChart(hotRank,pieData3,"Line");
	var scale = document.getElementById("scale").getContext("2d");
	var pieData4= $("#scale").attr("chartDate");
	getChart(scale,pieData4,"Doughnut");
	var per_data_chart = document.getElementById("per_data_chart").getContext("2d");
	var pieData5= $("#per_data_chart").attr("chartDate");
	getChart(per_data_chart,pieData5,"Radar");
	          	          
	/*tab切换*/
     $('.data_analysis_panel .header_title .title li').on('click',function(){
     var index=$(this).index();
     $(this).addClass('active')
     $(this).siblings().removeClass('active')
        console.info($(this).parents('.data_analysis_panel').find('.con'))
     $(this).parents('.data_analysis_panel').find('.con').children('.con_item').css('display','none')
     $(this).parents('.data_analysis_panel').find('.con').children('.con_item').eq(index).show();
    });
    //回复留言
    $("#bro_stat tr").slice(1).each(function(g){
         $(this).on('click',function(ev){
             console.info(this)
             var sug=$(this).find('.sugDat').text()
             var eamil_to_who=$(this).find('.email').text()
             var reqp=$(this).find('.reqp').text()
             var keyId=$(this).find('.keyId').text()
             $('.reply_wrap .email_to').text(eamil_to_who)
             $('.reply_wrap .sugmsg .sugmsg_details').text(sug)
             $('#replyText').text(reqp);
             var layerId=Layer.tips($('.reply_temp').html(), this,{tips:1,area: ['400px','auto'],move:true,time:0});
             $('.reply_sub').on('click',function(){
               //提交
               Mobile.getMemoryCache(function(sessionId){
               		var param = Wade.DataMap();
               		param.put("SESSION_ID",sessionId);
               		param.put("KEYID", keyId);
               		var resq =$('.reply_wrap .reply_con textarea').eq(1).val();
               		param.put("CONTENT_REQP",resq);
					Mobile.dataRequest("BainiuIndex.insReply",param,function(data){
						Common.showSuccess("修改成功");
						redirectToByUrl("mobile?action=MInitContent&data={'SESSION_ID':"+sessionId+"}","content");
					});
				}, "SESSION_ID");
				
             });
             if('true'== $('#replyRight').text()){
             $('.reply_wrap .reply_con textarea').focus(function(){
                 $(this).css({'color':'#000','background':'#fff','border':'1px solid #a9a9a9'})
             });
             
             };
             $('.reply_can').on('click',function(){
                 Layer.close(layerId);//关闭
             });
             ev.stopPropagation();
             closeTips(layerId);
             function closeTips(layerIndex){
                 $('.layui-layer-tips').click(function(ev){
                     ev.stopPropagation();
                 })
                 $(document).click(function(){
                     Layer.close(layerIndex);//关闭
                 });
             }
         })
    });
});