package com.ipu.server.dao;

import org.apache.log4j.Logger;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.util.Pagination;
 
public class RightDao extends SmartBaseDao
{
	private static transient Logger log = Logger.getLogger(RightDao.class);
	static String TABLE_IPU_MEMBER = "tf_f_user_right";

	public RightDao(String connName) throws Exception {
		super(connName);
		// TODO Auto-generated constructor stub
	}

	public IData queryRight(IData params,IData outParam,String keyList) throws Exception
	{	
		try {
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("select a.USER_ID,a.NAME, a.USER_ACCT,b.STATE as STATE_CODE,if(b.STATE=1,'1',NULL) as STATE_TAG ,b.RIGHT_CODE,");
			strBuf.append("(select d.ENUM_NAME from td_s_enumerate d ");
			strBuf.append("where d.COL_CODE='STATE' and d.TABLE_CODE='USER_RIGHT' ");
			strBuf.append("and d.SUBSYS_CODE='EX' AND d.ENUM_CODE=b.STATE) as STATE_NAME,");
			strBuf.append("(select e.ENUM_NAME from td_s_enumerate e"); 
		    strBuf.append(" where e.COL_CODE='RIGHT_TYPE' and e.TABLE_CODE='TD_M_RIGHT' and e.SUBSYS_CODE='SYS' AND e.ENUM_CODE=c.RIGHT_TYPE) as TYPE_NAME,");
			strBuf.append("	c.RIGHT_NAME                 ");
			strBuf.append("from tf_f_user a,tf_f_user_right b,td_m_role c ");
			strBuf.append("where  ");
			strBuf.append(" a.USER_ID=b.USER_ID and b.RIGHT_CODE=c.RIGHT_CODE");
			if(!(null==params.get("USER_ACCT")||params.getString("USER_ACCT").trim().isEmpty()))
			{
				strBuf.append(" and a.USER_ACCT='"+params.getString("USER_ACCT").trim()+"'");
			}
			
			if(!(null==params.get("RIGHT_NAME")||params.getString("RIGHT_NAME").trim().isEmpty())) 
			{
				strBuf.append(" and  c.RIGHT_NAME='"+((String) params.get("RIGHT_NAME")).trim()+"'");
			}
			strBuf.append("  ORDER BY b.STATE ");
			outParam = this.queryPaginationList(strBuf.toString(),params,outParam,keyList,new Pagination(8,5));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return outParam;
	}
	
	public IData authorize(IData params,IData outParam) throws Exception
	{	
		int count=0;
		try 
		{
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("UPDATE tf_f_user_right a SET a.STATE='1', a.USE_TAG='0' where a.USER_ID=? and a.RIGHT_CODE=?");
			count = this.executeUpdate(strBuf.toString(),new Object[]{params.get("USER_ID"),params.get("RIGHT_CODE")});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outParam.put("result", count);
		return outParam;
	}
	
	public IData cancelAuthorize(IData params,IData outParam) throws Exception
	{	
		int count=0;
		try 
		{
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("delete  from tf_f_user_right  where USER_ID=? and RIGHT_CODE=?");
			count = this.executeUpdate(strBuf.toString(),new Object[]{params.get("USER_ID"),params.get("RIGHT_CODE")});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outParam.put("result", count);
		return outParam;
	}
	
	
	public IData queryAvailableRight(IData params,IData outParam,String keyList) throws Exception
	{	
		 
	  StringBuffer strBuf = new StringBuffer();
	  strBuf.append("select *  from td_m_role a  WHERE a.RIGHT_CODE not in (select b.RIGHT_CODE from tf_f_user_right b where b.user_id=");
	  strBuf.append(params.get("USER_ID"));
	  strBuf.append(")");
	  IDataset rightList = this.queryList(strBuf.toString(),params);	 
	  outParam.put(keyList, rightList);
	  return outParam;
	}
	
	/**
	 * 新增成员信息
	 */
	public boolean insertUserRight(IData data) throws Exception 
	{
		boolean resultFlag = this.insert(TABLE_IPU_MEMBER, data);
		if (log.isDebugEnabled()) {
			log.debug(resultFlag ? "数据插入成功" : "数据插入失败");
		}
		return resultFlag;
	}

	public boolean queryUserRight(String userId, String rightCode) throws Exception {
		// TODO Auto-generated method stub
		IData buf = new DataMap();
		buf.put("USER_ID", userId);
		buf.put("MUM_CODE", rightCode);
		String sql = "SELECT M.* FROM TF_F_USER_RIGHT M,TD_M_ROLE_MUM N WHERE M.USER_ID = :USER_ID "
			+" AND M.RIGHT_CODE = N.ROLE_CODE AND N.MUM_CODE = :MUM_CODE";
		IDataset rightInfos = this.queryList(sql,buf);
		if(rightInfos.size()>0)
			return true;
		else
			return false;
	}

}
