package com.ipu.server.bean;



import com.ailk.common.data.IData;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.RightDao;

public class RightBean extends AppBean
{
	public IData queryRight(IData params) throws Exception
	{	
		IData resultData = getResultData();
 
	    RightDao dao =new RightDao("bainiu");	 
	    resultData.putAll(dao.queryRight(params, resultData, "RIGHTLIST"));
			 		 
	   return resultData;
		 
	}
	
	public IData authorize(IData params) throws Exception
	{	
		IData resultData = getResultData();	 
	    RightDao dao =new RightDao("bainiu");	 
	    resultData.putAll( resultData=dao.authorize(params, resultData));	
	    doActionLog(params);	 
	    return resultData;	 
	}
	
	public IData cancelAuthorize(IData params) throws Exception
	{	
		IData resultData = getResultData();	 
	    RightDao dao =new RightDao("bainiu");	 
	    resultData.putAll( resultData=dao.cancelAuthorize(params, resultData));	
	    doActionLog(params);	 
	    return resultData;	 
	}
 
	public IData authorizeAll(IData params) throws Exception
	{	
		IData resultData = getResultData();	 
	    RightDao dao =new RightDao("bainiu");
	    
	    for (String key:params.keySet()) 
	    {
	    	if( params.get(key) instanceof IData)
	    	{
	    		resultData=dao.authorize((IData) params.get(key), resultData);	
	    	}
	
		}
	    
	    resultData.putAll(resultData);		 
	    doActionLog(params);	 
	    return resultData;	 
	}
	
	
	public IData cancelAuthorizeAll(IData params) throws Exception
	{	
		IData resultData = getResultData();	 
	    RightDao dao =new RightDao("bainiu");
	    
	    for (String key:params.keySet()) 
	    {
	    	
	    	if( params.get(key) instanceof IData)
	    	{
	    		resultData=dao.cancelAuthorize((IData) params.get(key), resultData);
	
	    	}
	    	
	    
		}
	    
	    resultData.putAll(resultData);
	    doActionLog(params);	 
	    return resultData;	 
	}
	
	public IData queryAvailableRight(IData params) throws Exception
	{	
		IData resultData = getResultData();	 
	    RightDao dao =new RightDao("bainiu");
	    params.put("USER_ID", getContextData().getUserID());
	    resultData=dao.queryAvailableRight(params, resultData,"availableRightList");
	    resultData.putAll(resultData);		
	    doActionLog(params);	 
	    return resultData;	 
	}
	
	public IData addUserRight(IData params) throws Exception
	{	
		boolean  isSuccess = false;
		IData resultData = getResultData();	 
	    RightDao dao =new RightDao("bainiu");
	    params.put("USER_ID", getContextData().getUserID());
	    params.put("STATE", "0");//申请状态
	    isSuccess=dao.insertUserRight(params);
		if(isSuccess)
		{
			
			resultData.put("result", "0");
			doActionLog(params);	 
			return resultData;
		}
		else
		{
			resultData.put("result", "1");
			return resultData;
		}
	}
 
}
