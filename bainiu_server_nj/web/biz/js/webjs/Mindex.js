require(["mobile","jquery","jcl"],function(Mobile,$,Wade){
	Mobile.getMemoryCache(function(sessionId){
		redirectToByUrl("mobile?action=MLog&data={'SESSION_ID':"+sessionId+"}","log");
		redirectToByUrl("mobile?action=MMenu&data={'SESSION_ID':"+sessionId+"}","menu");
		redirectToByUrl("mobile?action=MInitContent&data={'SESSION_ID':"+sessionId+"}","content");
	}, "SESSION_ID");
	
});