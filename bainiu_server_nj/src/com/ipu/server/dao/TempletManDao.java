package com.ipu.server.dao;

import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.util.StringUtil; 
import com.ipu.server.util.Pagination;

public class TempletManDao extends SmartBaseDao {
	private static transient Logger log = Logger.getLogger(TempletManDao.class);
	static String TABLE_NAME = "TD_S_TEMPLET";
	public TempletManDao(String connName) throws Exception {
		super(connName);
	}
	
	/*
	 * 查询模板定义表
	 * */
	public IData getTempletDefine(String ins) throws Exception {
		IData buf = new DataMap();
		buf.put("TEMPLET_NAME", ins);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SELECT * from TD_S_TEMPLET where TEMPLET_NAME = :TEMPLET_NAME AND STATE='0' AND END_TIME>NOW() limit 0,1");
		IDataset acct = this.queryList(strBuf.toString(), buf);
		return acct.first();	    
	}	

   /*
    * 获取模板状态、类型参数
    * */
   public IData querySelectType(IData param,IData outParams) throws Exception
   {
		IDataset BTypeList=this.queryList("select a.ENUM_CODE,a.ENUM_NAME  from td_s_enumerate a where a.COL_CODE='STATE' and a.SUBSYS_CODE='SYS' and a.TABLE_CODE='TD_S_TEMPLET'", param);
		outParams.put("TEMPLETSTATELIST", BTypeList);
		IDataset SEypeList=this.queryList("select a.ENUM_CODE,a.ENUM_NAME  from td_s_enumerate a where a.COL_CODE='TEMPLET_TYPE' and a.SUBSYS_CODE='SYS' and a.TABLE_CODE='TD_S_TEMPLET'", param);
		outParams.put("TEMPLETTYPELIST", SEypeList);
		return outParams;
   }

	public boolean createTemplet(IData params) throws Exception {
		// TODO Auto-generated method stub
		boolean resultFlag = this.insert(TABLE_NAME, params);
		if (log.isDebugEnabled()) {
			log.debug(resultFlag ? "数据插入成功" : "数据插入失败");
		}
		return resultFlag;
	}

	public void dealTempletState(IData params) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer  sqlbuf = new StringBuffer();
		sqlbuf.append("UPDATE TD_S_TEMPLET SET STATE=:STATE,UPD_TIME=NOW(),REMARK=:REMARK");
		sqlbuf.append(" WHERE TEMPLET_ID=:TEMPLET_ID ");
		this.executeUpdate(sqlbuf.toString(),params);
	}

	public IData queryTempletByID(String id) throws Exception {
		IData buf = new DataMap();
		buf.put("TEMPLET_ID", id);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("select *  from TD_S_TEMPLET t  where t.TEMPLET_ID=:TEMPLET_ID ");
		IDataset acct = this.queryList(strBuf.toString(), buf);
		return acct.first();	
	}

	public int chgTemplet(IData params) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer  sqlbuf = new StringBuffer();
		sqlbuf.append("UPDATE TD_S_TEMPLET SET UPD_TIME=NOW()");
		if(!StringUtil.isNull(params.getString("TEMPLET_CONTENT"))) 
			sqlbuf.append(" ,TEMPLET_CONTENT=:TEMPLET_CONTENT ");
		if(!StringUtil.isNull(params.getString("TEMPLET_PARADESC"))) 
			sqlbuf.append(" ,TEMPLET_PARADESC=:TEMPLET_PARADESC ");
		if(!StringUtil.isNull(params.getString("REMARK"))) 
			sqlbuf.append(" ,REMARK=:REMARK ");
		sqlbuf.append(" WHERE TEMPLET_ID=:TEMPLET_ID ");
		return this.executeUpdate(sqlbuf.toString(),params);
	}
	
	public IData queryTemplet(IData params, IData outParams, String keyList) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf = new StringBuffer();
		
		strBuf.append("SELECT TEMPLET_ID,TEMPLET_NAME,TEMPLET_TYPE,TEMPLET_CONTENT,TEMPLET_PARADESC,STATE,");
		strBuf.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE='TD_S_TEMPLET' AND COL_CODE='STATE' AND ENUM_CODE=A.STATE) STATE_NAME,");
		strBuf.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE='TD_S_TEMPLET' AND COL_CODE='TEMPLET_TYPE' AND ENUM_CODE=A.TEMPLET_TYPE) TEMPLET_TYPENAME,");
		strBuf.append("START_TIME,END_TIME,A.UPD_TIME,A.UPD_USER_ID,A.REMARK,");
		strBuf.append("(select name from tf_f_user where user_id=a.UPD_USER_ID) UPD_USER ");
		strBuf.append(" FROM TD_S_TEMPLET A");
		strBuf.append(" WHERE 1=1 ");
		if(!StringUtil.isNull(params.getString("TEMPLET_ID")))
		{
			strBuf.append(" AND A.TEMPLET_ID=:TEMPLET_ID ");
		}
		if(!StringUtil.isNull(params.getString("TEMPLET_NAME")))
		{
			strBuf.append(" AND A.TEMPLET_NAME like '%"+params.getString("TEMPLET_NAME")+"%' ");
		}
		if(!StringUtil.isNull(params.getString("STATE")))
		{
			strBuf.append(" AND A.STATE=:STATE ");
		}		
		strBuf.append(" ORDER BY A.STATE ASC,A.UPD_TIME DESC ");
		
		return this.queryPaginationList(strBuf.toString(), params, outParams, keyList, new Pagination(8, 6));
	}
}
