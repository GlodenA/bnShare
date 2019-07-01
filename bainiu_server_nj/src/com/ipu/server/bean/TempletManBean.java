package com.ipu.server.bean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.RightDao;
import com.ipu.server.dao.TempletManDao;
import com.ipu.server.util.BNSMS;
import com.ipu.server.util.StringUtil;

public class TempletManBean extends AppBean {
	public IData init(IData param) throws Exception{
		IData resultData = getResultData();
		resultData.putAll(param);
		return resultData;
	}
	
	public IData queryTemplet(IData param) throws Exception{
		IData resultData = getResultData();
		TempletManDao dao = new TempletManDao("bainiu");
		String userId= getContextData().getUserID();

		resultData = dao.queryTemplet(param, resultData, "TEMPLETLIST");
		
		//具有模板修改权限
		RightDao rightDao = new RightDao("bainiu");
		if(rightDao.queryUserRight(userId,"DATA_TEMPLET_OPER")){
			resultData.put("TEMPLETRIGHT","1");
		}
		
		dao.querySelectType(param,resultData);
		
		doActionLog(param);
		return resultData;
	}
		
	public IData chgTemplet(IData params) throws Exception 
	{
		IData resultData = getResultData();
		TempletManDao dao = new TempletManDao("bainiu");
		
		IData templetInfo = dao.queryTempletByID(params.getString("IS_ID"));	
		if(!templetInfo.getString("STATE").equals("0")){
			resultData.put("result","0");
			resultData.put("resultInfo","只有生效中的才需要修改");
		}
		
		int rsp = 0;
		try {
			rsp = dao.chgTemplet(params);
			doActionLog(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultData.put("result","0");
			resultData.put("resultInfo","系统异常:"+e);			
		}		
		resultData.put("result",rsp);				
		return resultData;
	}
	
	/*
	 * 模板创建取消
	 * */
	public IData dealTemplet(IData params) throws Exception
	{
		IData resultData = getResultData();
		IDataset wa = (IDataset) params.get("CHK_LIST");
		
		TempletManDao dao = new TempletManDao("bainiu");
		
		//创建
		if(wa==null){		
			params.put("TEMPLET_ID", StringUtil.getSequenceNew(dao,StringUtil.StrTempletIdSeq));
			params.put("UPD_USER_ID",getContextData().getUserID());
			params.put("START_TIME",StringUtil.getSysTime());
			params.put("END_TIME","2099-12-31 00:00:00");
			params.put("UPD_TIME",StringUtil.getSysTime());
			params.remove("SESSION_ID");
			boolean isSuccess = dao.createTemplet(params);
			if(isSuccess)
			{				
				resultData.put("RETRUN_STR", "新增活动成功");
				doActionLog(params);
				return resultData;
			}
			else
			{
				resultData.put("RETRUN_STR", "新增活动失败");
				doActionLog(params);
				return resultData;
			}
		}
		
		for(int i=0;i<wa.size();i++){
			IData signParam = wa.getData(i);    		
			dao.dealTempletState(signParam);
		}//更新

		resultData.put("RETRUN_STR", BNSMS.SMS_LEAVE_CHKYES);
		
		doActionLog(params);
	    return  resultData; 
	}
	
	public IData queryResultData(IData param)throws Exception
	{
		return getResultData();
	}		
	
	public IData querySelectType(IData param) throws Exception
	{	
		IData resultData = getResultData();	 
		TempletManDao dao = new TempletManDao("bainiu");
		dao.querySelectType(param,resultData);
		doActionLog(param);
		return resultData;
	}
}
