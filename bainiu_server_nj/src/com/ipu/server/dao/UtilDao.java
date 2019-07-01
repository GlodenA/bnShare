package com.ipu.server.dao;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;

public class UtilDao extends SmartBaseDao  {

	private static transient Logger log = Logger.getLogger(UtilDao.class);
	
	public UtilDao(String connName) throws Exception {
		super(connName);
		// TODO Auto-generated constructor stub
	}

	/*
	 * 查询列定义表
	 * */
	public IData getCodeDefine(String ins) throws Exception {
		IData buf = new DataMap();
		buf.put("TRADE_TYPE", ins);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("select * from coldefine_ai where tradetype = :TRADE_TYPE limit 0,1");
		IDataset acct = this.queryList(strBuf.toString(), buf);
		return acct.first();	    
	}
	
	/*
	 * 获取系统关键参数
	 * */
	public IData getTag(String ins) throws Exception {
		IData buf = new DataMap();
		buf.put("TAG_CODE", ins);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SELECT * FROM TD_S_TAG WHERE TAG_CODE = :TAG_CODE AND STATE='0' AND END_TIME>NOW() LIMIT 0,1");
		IDataset acct = this.queryList(strBuf.toString(), buf);
		return acct.first();	    
	}	
}
