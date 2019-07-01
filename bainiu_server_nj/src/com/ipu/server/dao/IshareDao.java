package com.ipu.server.dao;

import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.util.Pagination;
import com.ipu.server.util.StringUtil;

public class IshareDao extends SmartBaseDao  
{

	private static transient Logger log = Logger.getLogger(IshareDao.class);
	static String TABLE_NAME = "TD_B_ISHARE";
	
	public IshareDao(String connName) throws Exception {
		super(connName);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 根据ID查询
	 */
	public IData queryIshareByID(String id) throws Exception 
	{
		IData buf = new DataMap();
		buf.put("IS_ID", id);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("select *  from TD_B_ISHARE t  where t.IS_ID=:IS_ID ");
		IDataset acct = this.queryList(strBuf.toString(), buf);
		return acct.first();
 	} 

	public IData queryIshare(IData params, IData outParams, String keyList) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SELECT (select max(state) from TF_F_ISHARE_ENTRY where user_id = :USER_ID AND is_id = a.is_id and state in ('0','1')) ADD_TAG,");
		strBuf.append("A.IS_ID,A.IS_NO,A.IS_NAME,A.IS_EXPLAIN,A.IS_EXPRESULT,A.IS_TIME,A.IS_PLACE,A.IS_TARGROUP,");	
		strBuf.append("A.IS_LECTURER,A.IS_LINKUSER,A.IS_LIMITNUM,A.IS_ENDENROL,");
		strBuf.append("A.IS_TARGROUPID,A.IS_STATE,");
		strBuf.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE='TD_B_ISHARE' AND COL_CODE='IS_STATE' AND ENUM_CODE=A.IS_STATE) IS_STATENAME,");
		strBuf.append("A.UPD_USERID,A.UPD_TIME,A.REMARK,");
		strBuf.append("(select name from tf_f_user where user_id=a.UPD_USERID) UPD_USER,");
		strBuf.append("(select count(1) from TF_F_ISHARE_ENTRY where is_id=a.is_id and state ='0') IN_SUM,");
		strBuf.append("(select count(1) from TF_F_ISHARE_ENTRY where is_id=a.is_id and state ='1') WAIT_SUM,");
		strBuf.append("(select count(1) from TF_F_ISHARE_ENTRY where is_id=a.is_id and state ='2') CANCEL_SUM,");
		strBuf.append("(select count(1) from TF_F_ISHARE_ENTRY where is_id=a.is_id) ALL_SUM ");
		strBuf.append(" FROM TD_B_ISHARE A");
		strBuf.append(" WHERE 1=1 ");
		if(!StringUtil.isNull(params.getString("IS_ID")))
		{
			strBuf.append(" AND A.IS_ID=:IS_ID ");
		}
		if(!StringUtil.isNull(params.getString("IS_NAME")))
		{
			strBuf.append(" AND A.IS_NAME like '%"+params.getString("IS_NAME")+"%' ");
		}
		if(!StringUtil.isNull(params.getString("IS_STATE")))
		{
			strBuf.append(" AND A.IS_STATE=:IS_STATE ");
		}		
		strBuf.append(" ORDER BY A.IS_TIME DESC,A.UPD_TIME DESC ");
		return this.queryPaginationList(strBuf.toString(), params, outParams, keyList, new Pagination(8, 6));
	}
	
	/*
	 * 导出不分页
	 * */
	public IDataset queryIshareExport(IData params) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SELECT (select max(state) from TF_F_ISHARE_ENTRY where user_id = :USER_ID AND is_id = a.is_id and state in ('0','1')) ADD_TAG,");
		strBuf.append("A.IS_ID,A.IS_NO,A.IS_NAME,A.IS_EXPLAIN,A.IS_EXPRESULT,A.IS_TIME,A.IS_PLACE,A.IS_TARGROUP,");	
		strBuf.append("A.IS_LECTURER,A.IS_LINKUSER,A.IS_LIMITNUM,A.IS_ENDENROL,");
		strBuf.append("A.IS_TARGROUPID,A.IS_STATE,");
		strBuf.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE='TD_B_ISHARE' AND COL_CODE='IS_STATE' AND ENUM_CODE=A.IS_STATE) IS_STATENAME,");
		strBuf.append("A.UPD_USERID,A.UPD_TIME,A.REMARK,");
		strBuf.append("(select name from tf_f_user where user_id=a.UPD_USERID) UPD_USER,");
		strBuf.append("(select count(1) from TF_F_ISHARE_ENTRY where is_id=a.is_id and state ='0') IN_SUM,");
		strBuf.append("(select count(1) from TF_F_ISHARE_ENTRY where is_id=a.is_id and state ='1') WAIT_SUM,");
		strBuf.append("(select count(1) from TF_F_ISHARE_ENTRY where is_id=a.is_id and state ='2') CANCEL_SUM,");
		strBuf.append("(select count(1) from TF_F_ISHARE_ENTRY where is_id=a.is_id) ALL_SUM ");
		strBuf.append(" FROM TD_B_ISHARE A");
		strBuf.append(" WHERE 1=1 ");
		if(!StringUtil.isNull(params.getString("IS_ID")))
		{
			strBuf.append(" AND A.IS_ID=:IS_ID ");
		}
		if(!StringUtil.isNull(params.getString("IS_NAME")))
		{
			strBuf.append(" AND A.IS_NAME like '%"+params.getString("IS_NAME")+"%' ");
		}
		if(!StringUtil.isNull(params.getString("IS_STATE")))
		{
			strBuf.append(" AND A.IS_STATE=:IS_STATE ");
		}		
		strBuf.append(" ORDER BY A.IS_TIME DESC,A.UPD_TIME DESC ");
		
		return this.queryList(strBuf.toString(), params);
	}	

	public IData qIshareEntry(IData params, IData outParams, String keyList) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SELECT A.IS_ID,C.IS_NAME,C.IS_TIME,A.USER_ID,A.ENROL_TIME,A.ENROL_ORDER,A.STATE,");
		strBuf.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE='TF_F_ISHARE_ENTRY' AND COL_CODE='STATE' AND ENUM_CODE=A.STATE) STATE_NAME,");
		strBuf.append("A.CANCEL_TIME,A.CANCEL_REASON,A.UPD_TIME,A.REMARK,");
		strBuf.append("B.NAME NAME,B.EMAIL EMAIL,B.PHONE PHONE ");
		strBuf.append(" FROM TF_F_ISHARE_ENTRY A,TF_F_USER B,TD_B_ISHARE C");
		strBuf.append(" WHERE 1=1 AND A.USER_ID = B.USER_ID AND C.IS_ID = A.IS_ID ");
		if(!StringUtil.isNull(params.getString("IS_ID")))
		{
			strBuf.append("AND A.IS_ID=:IS_ID ");
		}
		if(!StringUtil.isNull(params.getString("IS_NAME")))
		{
			strBuf.append(" AND C.IS_NAME like '%"+params.getString("IS_NAME")+"%' ");
		}
		if(!StringUtil.isNull(params.getString("IS_STATE")))
		{
			strBuf.append(" AND C.IS_STATE=:IS_STATE ");
		}			
		strBuf.append(" ORDER BY C.IS_TIME DESC,A.ENROL_TIME DESC ");
		IDataset rightList = this.queryList(strBuf.toString(),params);
		outParams.put(keyList, rightList);
		return outParams;
	}
	
	public IData qIshareEntryFirstWait(IData param) throws Exception 
	{ 	     
		return this.queryList("SELECT * FROM TF_F_ISHARE_ENTRY A WHERE A.IS_ID=:IS_ID AND A.STATE=:STATE ORDER BY A.ENROL_ORDER ASC LIMIT 1", param).first();
 	} 
	
	public IData qIshareFixEmail(IData param) throws Exception 
	{ 	     
		return this.queryList("SELECT * FROM COLDEFINE_AI A WHERE A.TRADETYPE=:TRADETYPE  LIMIT 1", param).first();
 	} 
	
   /*
    * 获取模板状态、类型参数
    * */
   public IData querySelectType(IData param,IData outParams,String key) throws Exception
   {
		IDataset BTypeList=this.queryList("select a.ENUM_CODE,a.ENUM_NAME  from td_s_enumerate a where a.COL_CODE='IS_STATE' and a.SUBSYS_CODE='SYS' and a.TABLE_CODE='TD_B_ISHARE'", param);
		outParams.put(key, BTypeList);
		return outParams;
	}	

	public IData addiShareEntry(IData params, IData outParam) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf = new StringBuffer();
		int count=0;
		
		strBuf.append("INSERT INTO TF_F_ISHARE_ENTRY (IS_ID,USER_ID,ENROL_TIME,ENROL_ORDER,STATE,CANCEL_TIME,CANCEL_REASON,UPD_TIME,REMARK) ");
		strBuf.append("SELECT ?,?,NOW(),?,?,NULL,'',NOW(),'' FROM DUAL");
		
		count = this.executeUpdate(strBuf.toString(),new Object[]{params.get("IS_ID"),params.get("USER_ID"),params.get("NEW_ORDER"),params.get("NEW_STATE")});
		
		outParam.put("result", count);
		return outParam;
	}
	
	/*
	 * 报名和排队中的更新为取消
	 * */
	public IData updiShareEntry(IData params, IData outParam) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf = new StringBuffer();
		int count=0;
		strBuf.append("UPDATE TF_F_ISHARE_ENTRY A SET A.STATE=?,A.CANCEL_REASON=?,A.CANCEL_TIME=NOW() WHERE A.USER_ID = ? AND A.IS_ID = ? AND A.STATE<>?");
		count = this.executeUpdate(strBuf.toString(),new Object[]{params.get("STATE"),params.get("CANCEL_REASON"),params.get("USER_ID"),params.get("IS_ID"),params.get("STATE")});
		outParam.put("result", count);
		return outParam;
	}	
	
	public IData updiShareEntry1(IData params, IData outParam) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf = new StringBuffer();
		int count=0;
		strBuf.append("UPDATE TF_F_ISHARE_ENTRY A SET A.STATE='0',A.REMARK='排队进入',A.UPD_TIME=NOW() WHERE A.IS_ID=? AND A.STATE='1' ORDER BY A.ENROL_ORDER ASC LIMIT 1");
		count = this.executeUpdate(strBuf.toString(),new Object[]{params.get("IS_ID")});		
		outParam.put("result", count);
		return outParam;
	}	

	public String chkState(int oldState, int newState) throws Exception {
		// TODO Auto-generated method stub

		IshareDao dao = new IshareDao("bainiu");
		String oldName0 = StringUtil.getCodeName(dao,TABLE_NAME,"IS_STATE","0");
		String newName = StringUtil.getCodeName(dao,TABLE_NAME,"IS_STATE",String.valueOf(newState));
		String respModel = "";
		switch(newState)
		{
			case 1:  respModel = "只有状态为【"+oldName0+"】的记录才能【"+newName+"】！";break;
			case 2:  respModel = "只有状态为【"+oldName0+"】的记录才能【"+newName+"】！";break;
			default : respModel="";
		}
		
		if(oldState==0 && (newState==1||newState==2)) 
			respModel = "";

		return respModel;
	}

	public void dealIshareState(IData params) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer  sqlbuf = new StringBuffer();
		sqlbuf.append("UPDATE TD_B_ISHARE SET IS_STATE=:IS_STATE,UPD_TIME=NOW(),REMARK=:REMARK");
		sqlbuf.append(" WHERE IS_ID=:IS_ID ");
		this.executeUpdate(sqlbuf.toString(),params);
	}

	public boolean createIshare(IData params) throws Exception {
		// TODO Auto-generated method stub
		boolean resultFlag = this.insert(TABLE_NAME, params);
		if (log.isDebugEnabled()) {
			log.debug(resultFlag ? "数据插入成功" : "数据插入失败");
		}
		return resultFlag;
	}

	/*
	 * 修改信息
	 * */
	public int chgIshare(IData params) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer  sqlbuf = new StringBuffer();
		sqlbuf.append("UPDATE TD_B_ISHARE SET UPD_TIME=NOW()");
		if(!StringUtil.isNull(params.getString("IS_TIME"))) sqlbuf.append(" ,IS_TIME=:IS_TIME ");
		if(!StringUtil.isNull(params.getString("IS_PLACE"))) sqlbuf.append(" ,IS_PLACE=:IS_PLACE ");
		if(!StringUtil.isNull(params.getString("IS_ENDENROL"))) sqlbuf.append(" ,IS_ENDENROL=:IS_ENDENROL ");
		if(!StringUtil.isNull(params.getString("REMARK"))) sqlbuf.append(" ,REMARK=:REMARK ");
		sqlbuf.append(" WHERE IS_ID=:IS_ID ");
		return this.executeUpdate(sqlbuf.toString(),params);
	}

}
