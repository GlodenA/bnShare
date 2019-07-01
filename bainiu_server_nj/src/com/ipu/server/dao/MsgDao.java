package com.ipu.server.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ipu.server.util.Pagination;
import com.ipu.server.util.StringUtil;

public class MsgDao extends SmartBaseDao
{
	
	private static transient Logger log = Logger.getLogger(MsgDao.class);
	static String TABLE_IPU_MEMBER = "tf_f_sms";
	public MsgDao(String connName) throws Exception
    {
		super(connName);
 	}

	
	public IData queryMsg(IData params,IData outParam,String keyList) throws Exception
	{	
		try {
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("SELECT  c.USER_ID,c.SMS_ID,c.SMS_TYPE,if(c.SMS_STATE=0,'1',NULL) as SMS_STATE_FALG, 	c.SMS_TITLE,	c.SMS_ABSTRACT,	c.SMS_CONTENT,c.SMS_STATE,c.INS_TIME,c.UPD_TIME,");
			strBuf.append("	(SELECT d.ENUM_NAME FROM td_s_enumerate d WHERE c.SMS_TYPE = d.ENUM_CODE AND d.SUBSYS_CODE = 'SYS' AND d.TABLE_CODE = 'TF_F_SMS' AND d.COL_CODE = 'SMS_TYPE' ) AS SMS_TYPE_NAME,");
			strBuf.append("	(SELECT d.ENUM_NAME FROM td_s_enumerate d WHERE c.SMS_STATE = d.ENUM_CODE AND d.SUBSYS_CODE = 'SYS' AND d.TABLE_CODE = 'TF_F_SMS' AND d.COL_CODE = 'STATE' ) AS SMS_STATE_NAME");
			strBuf.append(" from (select * from tf_f_sms a where a.USER_ID='" );
			strBuf.append(params.getString("USER_ID").trim());
			strBuf.append("' and a.SMS_STATE<>'2'");
			strBuf.append(" UNION ");
			strBuf.append("select * from tf_f_sms a where a.USER_ID='-1' and a.SMS_STATE<>'2' ");
			strBuf.append("and not exists (select 1 from tf_f_sms b where USER_ID='");
			strBuf.append(params.getString("USER_ID").trim());
			strBuf.append("' and b.sms_id = a.sms_id)"); 
		    strBuf.append("  ) c order by INS_TIME DESC");

			outParam = this.queryPaginationList(strBuf.toString(),params,outParam,keyList,new Pagination(8,5));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		outParam.put(keyList, StringUtil.increaseRowNo((DatasetList)outParam.get(keyList)));
		return  outParam;
	}
	
	
	
	public int queryNoReadMsg(IData params) throws Exception
	{	
		
		IDataset outParam  = new DatasetList();
		try {
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("select * from tf_f_sms a where a.USER_ID=:USER_ID ");
			strBuf.append("and a.SMS_STATE=0 ");
			strBuf.append(" UNION ");
			strBuf.append("select * from tf_f_sms a where a.USER_ID='-1' and a.SMS_STATE=0 ");
			strBuf.append("and not exists (select 1 from tf_f_sms b where USER_ID=:USER_ID and b.sms_id = a.sms_id)");
		    outParam = this.queryList(strBuf.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		 
		return  outParam.size();
	}
	
	public IData signMsg(IData param,IData outParam) throws Exception
	{	
		 
		int count=0;
		try 
		{
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("UPDATE tf_f_sms t SET t.SMS_STATE = ? , t.UPD_TIME=? WHERE t.SMS_ID = ?  and t.USER_ID=?");
			count = this.executeUpdate(strBuf.toString(),new Object[]{param.get("SMS_STATE"),param.get("UPD_TIME"),param.get("SMS_ID"),param.get("USER_ID")});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outParam.put("result", count);
		return outParam;
	}
	
	public IData signMsg2PubMsg(IData param,IData outParam) throws Exception
	{	
 		int count=0;
		try 
		{
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("insert into tf_f_sms(USER_ID,SMS_ID,SMS_TYPE,SMS_TITLE,SMS_ABSTRACT,SMS_CONTENT,SMS_STATE,INS_TIME,UPD_TIME)  select  ?,t.SMS_ID,t.SMS_TYPE,t.SMS_TITLE,t.SMS_ABSTRACT,t.SMS_CONTENT,?,t.INS_TIME,?  from tf_f_sms t where t.SMS_ID=?  and t.USER_ID='-1'");
			count = this.executeUpdate(strBuf.toString(),new Object[]{param.get("USER_ID"),param.get("SMS_STATE"),param.get("UPD_TIME"),param.get("SMS_ID")});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outParam.put("result", count);
		return outParam;
	}
		
	
	public void leave2Msg(IData param) throws Exception
	{	
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		param.put("INS_TIME", df.format(new Date()));
		param.put("SMS_STATE", "0");
		param.put("SMS_TYPE", "0");
 		try 
		{
 	
		    this.insert(TABLE_IPU_MEMBER, param);
		} 
 		catch (Exception e) 
 		{
			e.printStackTrace();
		}
		
  	}
	
	public IData queryDetailMsg(IData param,IData outParam) throws Exception
	{	
		try {
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("SELECT  c.USER_ID,c.SMS_ID ,	c.SMS_TITLE,	c.SMS_ABSTRACT,	c.SMS_CONTENT,c.SMS_STATE,c.INS_TIME,c.UPD_TIME,");
			strBuf.append("	(SELECT d.ENUM_NAME FROM td_s_enumerate d WHERE c.SMS_TYPE = d.ENUM_CODE AND d.SUBSYS_CODE = 'SYS' AND d.TABLE_CODE = 'TF_F_SMS' AND d.COL_CODE = 'SMS_TYPE' ) AS SMS_TYPE_NAME,");
			strBuf.append("	(SELECT d.ENUM_NAME FROM td_s_enumerate d WHERE c.SMS_STATE = d.ENUM_CODE AND d.SUBSYS_CODE = 'SYS' AND d.TABLE_CODE = 'TF_F_SMS' AND d.COL_CODE = 'STATE' ) AS SMS_STATE_NAME");
			strBuf.append(" from (select * from tf_f_sms a where a.USER_ID='" );
			strBuf.append(param.getString("USER_ID").trim());
			strBuf.append("' and a.SMS_STATE<>'2'"); 
			strBuf.append(" and a.SMS_ID='"); 
			strBuf.append(param.getString("SMS_ID").trim());
			strBuf.append("'"); 
		    strBuf.append("  ) c");

		    outParam = this.queryList(strBuf.toString(), outParam).first();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
 
 		return  outParam;
	}

	
	
	
}
