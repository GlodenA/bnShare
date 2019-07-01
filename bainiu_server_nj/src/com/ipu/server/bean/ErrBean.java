package com.ipu.server.bean;




import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.mobile.util.MobileUtility;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.ErrDao;
import com.ipu.server.util.StringUtil;

public class ErrBean extends AppBean {
	public IData init(IData param) throws Exception{
		IData resultData = getResultData();
		resultData.putAll(param);
		return resultData;
	}
	public IData getDomain(IData param) throws Exception{
		IData resultData = getResultData();
		resultData.putAll(param);
		ErrDao dao = new ErrDao("bainiu");
		IDataset infos= dao.getDomain(param);
		resultData.put("TYPE_LIST", infos);
		return resultData;
	}	
	

	public IData getHotKey(IData param) throws Exception{
		IData resultData = getResultData();
		IDataset keylist = new DatasetList();
		ErrDao dao = new ErrDao("bainiu");
		keylist=dao.getHotKey(param);
		resultData.put("KEY_LIST", keylist);
		resultData.putAll(param);
		return resultData;
	}
	
	public IData getErrInfos(IData param) throws Exception{
		IData resultData = getResultData();
		ErrDao dao = new ErrDao("bainiu");

		resultData.putAll(dao.selectInfos(resultData,resultData,"ERRLIST"));
		resultData.putAll(param);
		doActionLog(param);
		return resultData;
	}
	
	
	public IData qryUnsolved(IData param) throws Exception{
		IData resultData = getResultData();
		ErrDao dao = new ErrDao("bainiu");
		param.put("EX_STATE", "1");
		resultData.putAll(dao.goQryUnsolved(param,resultData,"ERRLIST"));
		resultData.putAll(param);
		IDataset errs = (IDataset)resultData.get("ERRLIST");
		if(errs.size()>0){
			resultData.put("SHOW_RES", "YES");
		}else{
			resultData.put("SHOW_RES", null);
		}
		doActionLog(param);
		return resultData;
	}
	
	public IData updateErr(IData param) throws Exception{
		IData resultData = getResultData();
		ErrDao dao = new ErrDao("bainiu");
		if("1".equals(param.getString("EX_STATE"))){
			param.put("REVIEW_USERID",getContextData().getUserID());
			param.put("REVIEW_PSCODE","1");
			if(dao.updateErr(param)<1){
				MobileUtility.error("对不起，没有保存成功~");
			}
			
		}
		if("2".equals(param.getString("EX_STATE"))){
			param.put("DEAL_USERID",getContextData().getUserID());
			if(dao.updateErr(param)<1){
				MobileUtility.error("对不起，没有保存成功~");
			}
			dao.insertL2Err(param);
		}

		doActionLog(param);
		return resultData;
	}
	
	public IData goQry(IData param) throws Exception{
		IData resultData = getResultData();
		ErrDao dao = new ErrDao("bainiu");
		IDataset infos= dao.getDomain(param);
		resultData.put("TYPE_LIST", infos);
		param.put("EX_STATE", "2");
		if(StringUtil.isNull(param.getString("CHOOSE_TYPE"))){
			if(null==getContextData().getDomain()){
				//默认北六
				param.put("CHOOSE_TYPE","N6");
				
			}else{
				
				param.put("CHOOSE_TYPE",getContextData().getDomain());
			}
		}
		if(null==getContextData()){
			param.put("SECURITY_LEVEL","0");
		}else{
			param.put("SECURITY_LEVEL",getContextData().getSecurityLevel());
		}
		
		resultData.putAll(dao.goQry(param,resultData,"ERRLIST"));
		resultData.putAll(param);
		IDataset errs = (IDataset)resultData.get("ERRLIST");
		if(errs.size()>0){
			resultData.put("SHOW_RES", "YES");
		}else{
			resultData.put("SHOW_RES", null);
		}
		doActionLog(param);
		return resultData;
	}
	
	public IData getErrDtl(IData param) throws Exception{
		IData resultData = getResultData();
		IDataset errInfos = new DatasetList();
		ErrDao dao = new ErrDao("bainiu");
		IDataset infos= dao.getDomain(param);
		resultData.put("TYPE_LIST", infos);
		errInfos=dao.selectDelInfos(param);
		resultData.put("ERRDTL", errInfos.first());
		resultData.putAll(param);
		doActionLog(param);
		return resultData;
	}
	
	
	public IData getUnDealErrDtl(IData param) throws Exception{
		IData resultData = getResultData();
		IDataset errInfos = new DatasetList();
		ErrDao dao = new ErrDao("bainiu");
		IDataset infos= dao.getDomain(param);
		resultData.put("TYPE_LIST", infos);
		errInfos=dao.getUnDealErrDtl(param);
		IData errinfo = errInfos.first();
		//如果为提出人本人，展示删除按钮
		if(getContextData().getUserID().equals(errinfo.getString("INS_USERID"))){
			errinfo.put("ERR_DEL", "yes");
		}
		
		resultData.put("ERRDTL", errInfos.first());
		resultData.putAll(param);
		doActionLog(param);
		return resultData;
	}
	
	public IData insertErr(IData param) throws Exception{
		IData resultData = getResultData();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		boolean  isSuccess = false;
		//FileUpload.getInstance().upload(this.getRequest());
		ErrDao dao = new ErrDao("bainiu");
		//param.put("LOG_ID", StringUtil.getSequence(dao,StringUtil.LogIdSeq));
		//param.put("EX_ID", StringUtil.getSequence(dao,StringUtil.ExIdSeq));
		param.put("LOG_ID", StringUtil.getSequenceNew(dao,StringUtil.StrLogIdSeq));
		param.put("EX_ID", StringUtil.getSequenceNew(dao,StringUtil.StrExIdSeq));
		param.put("INS_USERID",getContextData().getUserID());
		param.put("SYS","N6");
		param.put("SECURITY_LEVEL","1");
		param.put("INS_TIME",df.format(new Date()));
		param.put("EX_STATE", "1");
		
	
		DataMap keyList=(DataMap)param.getData("KEY_LIST_MAP");
		
		DataMap paramData = new DataMap();
		
		StringBuffer keyListString = new StringBuffer();
				
		for (Object v : keyList.values()) 
		{
			paramData.put("KEYWORD", v);
			boolean isSucc=dao.addHotkey(paramData);
			
			if(!isSucc)
			{
				resultData.put("result", "关键词插入失败");
				return resultData;
			}
			keyListString.append(v+",");
	    }
	     
		param.put("KEY_LIST", keyListString.toString());
		isSuccess=dao.insertInfos(param);
		 
		if(isSuccess)
		{
			
			resultData.put("result", "0");
			doActionLog(param);
			return resultData;
		}
		else
		{
			resultData.put("result", "1");
			return resultData;
		}
	}
	
	public IData querySelectType(IData param) throws Exception
	{	
		IData resultData = getResultData();	 
		ErrDao dao = new ErrDao("bainiu");
		resultData.putAll(dao.querySelectType(param));
		return resultData;
	}
	public IData deleteErr(IData param) throws Exception
	{
		IData resultData = getResultData();	 
		ErrDao dao = new ErrDao("bainiu");
		dao.deleteErr(param);
		doActionLog(param);
		return resultData;
	}
	
	public IData getUnsolvedInfo(IData param) throws Exception
	{	
		IData resultData = getResultData();	 
		ErrDao dao = new ErrDao("bainiu");
		param.put("SYS", getContextData().getDomain());
		IDataset unsolvedTypeList = new DatasetList();
		unsolvedTypeList = dao.getUnsolvedTypeList(param);
		IDataset unsolvedTypeList1 = new DatasetList();
		IDataset unsolvedTypeList2 = new DatasetList();
		IDataset unsolvedTypeList3 = new DatasetList();
		
		if(unsolvedTypeList.size()>0){
			for(int i =0;i<unsolvedTypeList.size();i++){
				IData unsolvedType = (IData) unsolvedTypeList.get(i);
				IDataset unsolvedInfos = dao.getUnsolvedInfosByType(unsolvedType);
				IData sessionBuf = new DataMap();
				sessionBuf.put("SESSION_ID", param.getString("SESSION_ID"));
				unsolvedInfos =StringUtil.addMap2IDataset(unsolvedInfos,sessionBuf);
				unsolvedType.put("UNSOLVED_INFOS", unsolvedInfos);
				switch(i%3){
				case 0:unsolvedTypeList1.add(unsolvedType);break;
				case 1:unsolvedTypeList2.add(unsolvedType);break;
				case 2:unsolvedTypeList3.add(unsolvedType);break;
				}
			}
		}
		resultData.put("UNSOLVED_TYPE_INFOS_1", unsolvedTypeList1);
		resultData.put("UNSOLVED_TYPE_INFOS_2", unsolvedTypeList2);
		resultData.put("UNSOLVED_TYPE_INFOS_3", unsolvedTypeList3);
		doActionLog(param);
		
		return resultData;
	}
	
 
	
}
