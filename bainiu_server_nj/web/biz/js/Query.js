require(["mobile","zepto","wadeMobile"],function(Mobile,$,WadeMobile){
	var alreadyBound = false;
	$("#header").children().click(function(){
		Mobile.back();
	});
	$("#linkMe").click(function(){
		if( confirm("确定要呼叫人工客户吗？") ){
			WadeMobile.call("10086",1);
		}
	});
	$("#shareApp").click(function(){
		WadeMobile.shareByBluetooth();
	});
	
	var listSite = $("#listSite").children();
	
	$("#listSiteBtn").click(function(){
		WadeMobile.loadingStart("正在定位,请稍等……","定位中");
		WadeMobile.location(function(info){
			WadeMobile.loadingStop();
			var data=new Wade.DataMap(info);
			Mobile.tip("我的位置："+data.get("LocationDesc"));
			var latitude=parseFloat(data.get("Latitude"));
			var longitude=parseFloat(data.get("Longitude"));
			latitude = isNaN(latitude)?28.20:latitude;
			longitude = isNaN(longitude)?112.96:longitude;
			var la1 = latitude + 0.05;
			var lo1 = longitude + 0.1;
			var la2 = latitude + 0.05;
			var lo2 = longitude +0.03;
			var la3 = latitude - 0.04;
			var lo3 = longitude - 0.05;
			if(!alreadyBound){
				alreadyBound = true;
				$.each(listSite,function(index,item){
					if(index<3){
						$(item).click(function(){
							var data=new Wade.DataMap();
							data.put("Latitude",eval("la"+(index+1)));
							data.put("Longitude",eval("lo"+(index+1)));
							WadeMobile.markMap(function(info){
								var data = new Wade.DataMap(info);
								alert("选择的营业厅信息是："+info);
							},data,false,true,true);
						});
					}else{
						$(item).click(function(){
							var list=new Wade.DatasetList();
							/*不设置Icon则使用默认图标*/
							/*不设置Title和Snippet则使用默认位置信息*/
							var data1=new Wade.DataMap();
							data1.put("Latitude",la1);
							data1.put("Longitude", lo1);
							data2.put('Title',"县营业厅");
							list.add(data1);
							var data2=new Wade.DataMap();
							data2.put("Latitude",la2);
							data2.put("Longitude", lo2);
							data2.put('Title',"省营业厅");
							data2.put("Snippet", "服务好，速度快，环境好，快点来吧!");
							list.add(data2);
							var data3=new Wade.DataMap();
							data3.put("Latitude", la3);
							data3.put("Longitude", lo3);
							data2.put('Title',"市营业厅");
							list.add(data3);
							WadeMobile.markMap(function(info){
								var data = new Wade.DataMap(info);
								alert("选择的营业厅信息是："+data);
							},list,true,false,true);
						});
					}
				});
			}
			
		},function(error){
			WadeMobile.loadingStop();
			WadeMobile.tip(error);
		});
	});
	
});