package com.ipu.server.dao.gtm;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ipu.server.dao.SmartBaseDao;
import com.ipu.server.util.Pagination;
import com.ipu.server.util.StringUtil;

public class VacationGtmDao extends SmartBaseDao{
	private static transient Logger log = Logger.getLogger(VacationGtmDao.class);
	static String TABLE_NAME = "TF_F_USER_ASKLEAVE";
	
	public VacationGtmDao(String connName) throws Exception 
	{
		super(connName);
		// TODO Auto-generated constructor stub
	}

	/*
	 * 捞取待提醒数据
	 * */
	public IData queryOwnLeaveCk(IData params, IData outParams, String keyList) throws Exception {
		// TODO Auto-generated method stub
		  StringBuffer strBuf1 = new StringBuffer();
		  strBuf1.append("SELECT ");
		  //strBuf1.append("(select name from tf_f_user where USER_ID=a.USER_ID) as USER_NAME,");
		  //strBuf1.append("(select name from tf_f_user where USER_ID=a.FIR_CHK) as FIR_CHK_NAME,");
		  //strBuf1.append("(select name from tf_f_user where USER_ID=a.SEC_CHK) as SEC_CHK_NAME,");
		  strBuf1.append("DATE_FORMAT(a.REQ_TIME,'%Y-%m-%d') as REQ_TIME,");
		  strBuf1.append("DATE_FORMAT(a.FIR_CHKTIME,'%Y-%m-%d') as FIR_CHKTIME,");
		  strBuf1.append("DATE_FORMAT(a.SEC_CHKTIME,'%Y-%m-%d') as SEC_CHKTIME,");
		  strBuf1.append("DATE_FORMAT(a.RETURN_TIME,'%Y-%m-%d') as RETURN_TIME,");
		  strBuf1.append("a.WORK_SUBGROUP,a.REQ_TYPE,a.REQ_MARK,a.OUT_PLACE,a.FIR_PS,a.SEC_PS,a.OUT_DATE,a.BACK_DATE,a.LEAVE_DAYS,");
		  strBuf1.append("CASE a.STATE WHEN '0' THEN '0' WHEN '-1' THEN '-1' WHEN '1' THEN '1' ELSE NULL  END as FIR_CHK,");
		  strBuf1.append("CASE a.STATE WHEN '-2' THEN '-2' WHEN '2' THEN '2'  WHEN '3' THEN '3' ELSE NULL  END as SEC_CHK,");
		  strBuf1.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE = 'TF_F_USER_ASKLEAVE' AND COL_CODE='STATE' AND ENUM_CODE = a.STATE ) STATE_NAME,");
		  strBuf1.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE = 'TF_F_USER_ASKLEAVE' AND COL_CODE='REQ_TYPE' AND ENUM_CODE = a.REQ_TYPE ) REQ_TYPENAME,");
		  strBuf1.append("USER_ID,FIR_CHK FIR_CHK_USERID,SEC_CHK SEC_CHK_USERID,BAK_USER_ID BAK_USERID,ID,STATE ");
		  strBuf1.append("FROM ");
		  strBuf1.append("tf_f_user_askleave a ");
		  strBuf1.append("WHERE ");
		  strBuf1.append("1 = 1 ");
		  if(!StringUtil.isNull(params.getString("USER_ID")))
			{
			  strBuf1.append("and a.USER_ID=:USER_ID ");
			}
		  if(!StringUtil.isNull(params.getString("REQ_TYPE")))
			{
			  strBuf1.append("and a.REQ_TYPE=:REQ_TYPE ");
			}
		  if(!StringUtil.isNull(params.getString("STATE")))
			{
			  strBuf1.append("and a.STATE=:STATE ");
			  if(params.getString("STATE").equals("0"))
				  strBuf1.append("and TIMESTAMPDIFF(DAY,DATE_FORMAT(a.REQ_TIME,'%Y-%m-%d'),CURDATE()) > 0 ");
			  else if(params.getString("STATE").equals("1"))
				  strBuf1.append("and TIMESTAMPDIFF(DAY,DATE_FORMAT(a.FIR_CHKTIME,'%Y-%m-%d'),CURDATE()) > 0 ");
			  else if(params.getString("STATE").equals("2"))
				  strBuf1.append("and TIMESTAMPDIFF(DAY,DATE_FORMAT(a.BACK_DATE,'%Y-%m-%d'),CURDATE()) > 0 ");
			}
		  strBuf1.append("ORDER BY a.REQ_TIME DESC");
		  return this.queryPaginationList(strBuf1.toString(), params, outParams, keyList, new Pagination(8, 6));
	}

	public IData queryIllegalLeave(IData params, IData outParams,String keyList) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf1 = new StringBuffer();
		strBuf1.append("SELECT ");
		strBuf1.append(" X.USER_ID USER_ID,X.`NAME` NAME,X.PHONE PHONE, ");
		strBuf1.append(" X.EMAIL EMAIL,X.ORG_NAME,A.REQ_MARK,");
		strBuf1.append(" (SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE = 'TF_F_USER_ASKLEAVE' AND COL_CODE='STATE' AND ENUM_CODE = A.STATE ) STATE_NAME, ");
		strBuf1.append(" A.REQ_TIME,A.OUT_DATE,A.FIR_CHKTIME,A.SEC_CHKTIME ");
		strBuf1.append("FROM TF_F_USER_ASKLEAVE A,TF_F_USER X ");
		strBuf1.append("WHERE A.REQ_TIME > DATE_ADD(NOW(),INTERVAL -:DAYS DAY)");
		strBuf1.append("AND (A.OUT_DATE <= DATE_FORMAT(A.FIR_CHKTIME,'%Y-%m-%d') ");
		strBuf1.append("OR A.OUT_DATE <= DATE_FORMAT(A.SEC_CHKTIME,'%Y-%m-%d') ");
		strBuf1.append("OR DATE_FORMAT(A.REQ_TIME,'%Y-%m-%d')>=A.OUT_DATE)");
		strBuf1.append("AND A.STATE IN ('0','1','2','3') ");
		strBuf1.append("AND A.USER_ID=X.USER_ID "); 		
		return this.queryPaginationList(strBuf1.toString(), params, outParams, keyList, new Pagination(8, 6));
	}
	
}
