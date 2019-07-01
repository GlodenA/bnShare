require(["mobile","jquery","jcl","common"],function(Mobile,$,Wade,Common){
	var linkWin = 5;
	function timeOut(){
		if(linkWin==0){
			Mobile.getMemoryCache(function(sessionId){
				top.location.href = "mobile?action=Index";
			});
		}else{
			setTimeout(function(){ 
				linkWin--;  
				$("#outTime").html(linkWin);
				timeOut();
	        },1000);
		}
	}
	timeOut();	
});