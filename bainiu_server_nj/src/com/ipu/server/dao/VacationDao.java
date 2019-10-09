package com.ipu.server.dao;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.util.Pagination;
import com.ipu.server.util.StringUtil;

public class VacationDao extends SmartBaseDao
{

	private static transient Logger log = Logger.getLogger(VacationDao.class);
	static String TABLE_NAME = "TF_F_USER_ASKLEAVE";
	
	public VacationDao(String connName) throws Exception 
	{
		super(connName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 根据NT帐号查询用户信息
	 */
	public IData queryUserByNT(String NT) throws Exception 
	{
		  StringBuffer strBuf1 = new StringBuffer();
		  strBuf1.append("select *  from tf_f_user t  where t.USER_ACCT='");
		  strBuf1.append(NT);
		  strBuf1.append("'");
		  IDataset acct = this.queryList(strBuf1.toString(),new  DataMap());	 
		  return acct.first();
 	} 
	
	/**
	 * 根据USE_ID查询用户信息
	 */
	public IData queryUserByUserId(String userId) throws Exception 
	{
		IData buf = new DataMap();
		buf.put("USER_ID", userId);
		  StringBuffer strBuf1 = new StringBuffer();
		  strBuf1.append("select *  from tf_f_user t  where t.USER_ID=:USER_ID");
		  IDataset acct = this.queryList(strBuf1.toString(),buf);

		  return acct.first();
 	} 
	
	/**
	 * 根据user_id 查询用户的一级归属(GROUP_LEVEL=1)
	 */
	public String queryGroupID(String userId)  throws Exception 
	{
		StringBuffer strBuf1 = new StringBuffer();
		strBuf1.append(" SELECT A.GROUP_ID GROUP_ID ");
		strBuf1.append(" FROM TF_F_GROUP A,TF_F_GROUP B,TF_F_GROUP C,TF_F_GROUP D,TF_F_GROUP_SUB E,TF_F_USER F ");
		strBuf1.append(" WHERE A.GROUP_LEVEL='1' ");
		strBuf1.append(" AND B.GROUP_LEVEL='2' AND B.PARENT_GROUP_ID=A.GROUP_ID");
		strBuf1.append(" AND C.GROUP_LEVEL='3' AND C.PARENT_GROUP_ID=B.GROUP_ID ");
		strBuf1.append(" AND D.GROUP_LEVEL='4' AND D.PARENT_GROUP_ID=C.GROUP_ID ");
		strBuf1.append(" AND NOW() BETWEEN B.START_TIME AND B.END_TIME ");
		strBuf1.append(" AND NOW() BETWEEN C.START_TIME AND C.END_TIME ");
		strBuf1.append(" AND NOW() BETWEEN D.START_TIME AND D.END_TIME ");
		strBuf1.append(" AND E.GROUP_ID = D.GROUP_ID AND E.MEM_ID=F.USER_ID AND E.STATE='0'  ");
		strBuf1.append(" AND F.USER_ID = '"+userId+"'");
		IDataset acct = this.queryList(strBuf1.toString(),new  DataMap());	 
		String groupID = "";
		if(acct.size()>0){
			groupID = ((IData)acct.get(0)).getString("GROUP_ID");
		}
		return groupID;
	}
	
	/**
	 * 根据ID查询请假
	 */
	public IData queryLeaveByID(String id) throws Exception 
	{	
		IData buf = new DataMap();
		buf.put("ID", id);
		  StringBuffer strBuf1 = new StringBuffer();
		  strBuf1.append("select *  from tf_f_user_askleave t  where t.ID=:ID ");
		  IDataset acct = this.queryList(strBuf1.toString(), buf);
		  return acct.first();
 	}

	/**
	 * 查询用户的历史请假 litj@20191001
	 */
	public IData queryLeaveByUserOnline(String id) throws Exception
	{
		IData buf = new DataMap();
		buf.put("USER_ID", id);
		StringBuffer strBuf1 = new StringBuffer();
		strBuf1.append("select *  from tf_f_user_askleave t  where t.USER_ID=:USER_ID AND t.state in ('0','1','2')");
		IDataset acct = this.queryList(strBuf1.toString(), buf);
		return acct.first();
	}

	/**
	 * 请假申请
	 */
	public boolean submitLeave(IData params) throws Exception 
	{
		  boolean isSuccess=false;
          isSuccess= this.insert(TABLE_NAME, params);
           
	     return isSuccess;
 	} 
	
	/**
	 * 查询当前用户和请假B角是否在以为其他人的B角
	 * @param BakNtId
	 * @param UserId
	 * @return
	 * @throws Exception
	 */
	public IData queryBakNtList(String BakNtId,String UserId,String outTime,String backTime) throws Exception {
		StringBuffer strBuf1 = new StringBuffer();
		strBuf1.append("select *  from tf_f_user_askleave t  where t.BAK_USER_ID in ('");
		strBuf1.append(BakNtId);
		strBuf1.append("','");
		strBuf1.append(UserId);
		strBuf1.append("')");
		strBuf1.append(" and t.BACK_DATE BETWEEN '"+outTime+"' and '"+backTime+"'");
		strBuf1.append(" and t.OUT_DATE BETWEEN '"+outTime+"' and '"+backTime+"'");
		strBuf1.append(" and t.state>=0");
		IDataset acct = this.queryList(strBuf1.toString(),new  DataMap());	 
		return acct.first();
	}
	
	/**
	 * 根据用户id查询该用户的请假信息
	 */
	public IData queryLeaveByUser(IData params) throws Exception 
	{
		  StringBuffer strBuf1 = new StringBuffer();
		  strBuf1.append("SELECT ");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.USER_ID) as USER_NAME,");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.FIR_CHK) as FIR_CHK_NAME,");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.SEC_CHK) as SEC_CHK_NAME,");
		  strBuf1.append("DATE_FORMAT(a.REQ_TIME,'%Y-%m-%d') as REQ_TIME,");
		  strBuf1.append("DATE_FORMAT(a.FIR_CHKTIME,'%Y-%m-%d') as FIR_CHKTIME,");
		  strBuf1.append("DATE_FORMAT(a.SEC_CHKTIME,'%Y-%m-%d') as SEC_CHKTIME,");
		  strBuf1.append("DATE_FORMAT(a.RETURN_TIME,'%Y-%m-%d') as RETURN_TIME,");
		  strBuf1.append("a.REQ_TYPE,");
		  strBuf1.append("a.FIR_PS,");
		  strBuf1.append("a.SEC_PS,");
		  strBuf1.append("CASE a.STATE WHEN '0' THEN '0' ELSE NULL  END as FIR_CHK,");
		  strBuf1.append("CASE a.STATE WHEN '1' THEN '1' ELSE NULL END as SEC_CHK,");
		  strBuf1.append("CASE a.STATE WHEN '2' THEN '2' ELSE NULL END as SICK_LEAVE,");
		  strBuf1.append("CASE a.STATE WHEN '3' THEN '3' ELSE NULL END as ENDDING,");
		  strBuf1.append("CASE a.STATE WHEN '-1' THEN '-1' ELSE NULL END as FIR_CHK_NO_PASS,");
		  strBuf1.append("CASE a.STATE WHEN '-2' THEN '-2' ELSE NULL END as SEC_CHK_NO_PASS ");
		  strBuf1.append("FROM ");
		  strBuf1.append("tf_f_user_askleave a ");
		  strBuf1.append("WHERE ");
		  strBuf1.append("a.USER_ID = :USER_ID ");
		  strBuf1.append("ORDER BY a.REQ_TIME DESC limit 1 ");
		  return this.queryList(strBuf1.toString(),params).first();
	} 
	
	/**
	 * 根据用户id查询该用户的请假信息
	 */
	public IData queryOwnLeave(IData params,IData outParams,String keyList) throws Exception 
	{
		  StringBuffer strBuf1 = new StringBuffer();
		  strBuf1.append("SELECT ");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.USER_ID) as USER_NAME,");
		  strBuf1.append("(select PHONE from tf_f_user where USER_ID=a.USER_ID) as USER_PHONE,");	//电话号码
		  strBuf1.append("(select PHONE from tf_f_user where USER_ID=a.BAK_USER_ID) as BAK_USER_PHONE,");	//B角号码
		  strBuf1.append("a.WORK_SUBGROUP,a.OUT_PLACE,REQ_MARK,");
		  //查询组别
		  strBuf1.append("(select ll.GROUP_NAME GROUP3_NAME from tf_f_group ll, tf_f_group mm ,tf_f_group_sub nn where nn.MEM_ID=a.USER_ID and nn.STATE='0' and nn.GROUP_ID=mm.GROUP_ID  and mm.PARENT_GROUP_ID=ll.GROUP_ID and SYSDATE() BETWEEN ll.START_TIME and ll.END_TIME  and SYSDATE() BETWEEN mm.START_TIME and mm.END_TIME LIMIT 1) as GROUP_NAME,");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.FIR_CHK) as FIR_CHK_NAME,");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.SEC_CHK) as SEC_CHK_NAME,");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.BAK_USER_ID) as BAK_USER_NAME,");
		  strBuf1.append("DATE_FORMAT(a.REQ_TIME,'%Y-%m-%d') as REQ_TIME,");
		  strBuf1.append("DATE_FORMAT(a.FIR_CHKTIME,'%Y-%m-%d') as FIR_CHKTIME,");
		  strBuf1.append("DATE_FORMAT(a.SEC_CHKTIME,'%Y-%m-%d') as SEC_CHKTIME,");
		  strBuf1.append("DATE_FORMAT(a.RETURN_TIME,'%Y-%m-%d') as RETURN_TIME,");
		  strBuf1.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE = 'TF_F_USER_ASKLEAVE' AND COL_CODE='REQ_TYPE' AND ENUM_CODE = a.REQ_TYPE ) REQ_TYPENAME,");
		  strBuf1.append("a.REQ_TYPE,a.FIR_PS,a.SEC_PS,a.OUT_DATE,a.BACK_DATE,a.LEAVE_DAYS,");
		  strBuf1.append("CASE a.STATE WHEN '0' THEN '0' WHEN '-1' THEN '-1' WHEN '1' THEN '1' ELSE NULL  END as FIR_CHK,");
		  strBuf1.append("CASE a.STATE WHEN '-2' THEN '-2' WHEN '2' THEN '2'  WHEN '3' THEN '3' ELSE NULL  END as SEC_CHK,");
		  strBuf1.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE = 'TF_F_USER_ASKLEAVE' AND COL_CODE='STATE' AND ENUM_CODE = a.STATE ) STATE_NAME,");
/*		  strBuf1.append("CASE a.STATE WHEN '0' THEN '0' WHEN '1' THEN '1' ELSE NULL  END as AUDIT,");
		  strBuf1.append("CASE a.STATE WHEN '2' THEN '2' ELSE NULL  END as SICK_LEAVE,");
		  strBuf1.append("CASE a.STATE WHEN '3' THEN '3' ELSE NULL  END as ENDDING,");
		  strBuf1.append("CASE a.STATE WHEN '-2' THEN '-2' WHEN '-1' THEN '-1'  ELSE NULL  END as NO_PASS, ");*/
		  strBuf1.append("USER_ID,FIR_CHK FIR_CHK_USERID,SEC_CHK SEC_CHK_USERID,ID,STATE ");
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
		  if(!StringUtil.isNull(params.getString("REQ_STATE"))){
			  if("3".equals(params.getString("REQ_STATE")) || "-1".equals(params.getString("REQ_STATE")) || "-2".equals(params.getString("REQ_STATE"))){
				  strBuf1.append("and a.OUT_DATE > DATE_SUB(CURDATE(), INTERVAL 4 MONTH) ");
			  }
			  strBuf1.append("and a.STATE=:REQ_STATE ");	//状态
		  }
		  if(!StringUtil.isNull(params.getString("NT_NAME"))){
			  strBuf1.append("and a.user_id in (select u.user_id from tf_f_user u where u.name like '%"+params.getString("NT_NAME")+"%') ");
		  }
		  //***** 20160902 添加归属团队判断，只能查询当前归属团队下的人员*********
		  strBuf1.append(" and (select distinct(DD.GROUP_ID) from TF_F_GROUP_SUB SS,TF_F_GROUP AA,TF_F_GROUP BB,TF_F_GROUP CC,TF_F_GROUP DD ");
		  strBuf1.append(" where SS.GROUP_ID = AA.GROUP_ID AND SS.STATE = 0 ");
		  strBuf1.append(" and AA.GROUP_LEVEL = 4 and AA.PARENT_GROUP_ID = BB.GROUP_ID and now() between AA.start_time and AA.end_time ");
		  strBuf1.append(" and BB.GROUP_LEVEL = 3 and BB.PARENT_GROUP_ID = CC.GROUP_ID and now() between BB.start_time and BB.end_time ");
		  strBuf1.append(" and CC.GROUP_LEVEL = 2 and CC.PARENT_GROUP_ID = DD.GROUP_ID and now() between CC.start_time and CC.end_time ");
		  strBuf1.append(" and DD.GROUP_LEVEL = 1 and now() between DD.start_time and DD.end_time   ");
		  strBuf1.append(" and SS.MEM_ID =:USERIDS) IN  ");
		  strBuf1.append(" (select DDD.GROUP_ID from TF_F_GROUP_SUB SSS,TF_F_GROUP AAA,TF_F_GROUP BBB,TF_F_GROUP CCC,TF_F_GROUP DDD ");
		  strBuf1.append(" where SSS.GROUP_ID = AAA.GROUP_ID ");
		  strBuf1.append(" and AAA.GROUP_LEVEL = 4 and AAA.PARENT_GROUP_ID = BBB.GROUP_ID and now() between AAA.start_time and AAA.end_time  ");
		  strBuf1.append(" and BBB.GROUP_LEVEL = 3 and BBB.PARENT_GROUP_ID = CCC.GROUP_ID and now() between BBB.start_time and BBB.end_time ");
		  strBuf1.append(" and CCC.GROUP_LEVEL = 2 and CCC.PARENT_GROUP_ID = DDD.GROUP_ID and now() between CCC.start_time and CCC.end_time ");
		  strBuf1.append(" and DDD.GROUP_LEVEL = 1 and now() between DDD.start_time and DDD.end_time  ");
		  strBuf1.append(" and A.USER_ID = SSS.mem_id) ");
		  //***** 20160902***************************************************** 
		  strBuf1.append("ORDER BY a.REQ_TIME DESC");
		  return this.queryPaginationList(strBuf1.toString(), params, outParams, keyList, new Pagination(8, 6));
	}
	
	/**
	 * 导出请假信息
	 * @param params
	 * @param
	 * @param
	 * @return
	 * @throws Exception
	 */
	public IDataset exportOwnLeave(IData params) throws Exception 
	{
		  StringBuffer strBuf1 = new StringBuffer();
		  strBuf1.append("SELECT ");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.USER_ID) as USER_NAME,");
		  strBuf1.append("a.WORK_SUBGROUP,a.OUT_PLACE,REQ_MARK,");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.FIR_CHK) as FIR_CHK_NAME,");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.SEC_CHK) as SEC_CHK_NAME,");
		  strBuf1.append("(select name from tf_f_user where USER_ID=a.BAK_USER_ID) as BAK_USER_NAME,");
		  strBuf1.append("DATE_FORMAT(a.REQ_TIME,'%Y-%m-%d') as REQ_TIME,");
		  strBuf1.append("DATE_FORMAT(a.RETURN_TIME,'%Y-%m-%d') as RETURN_TIME,");
		  strBuf1.append("a.FIR_PS,a.SEC_PS,a.OUT_DATE,a.BACK_DATE,a.LEAVE_DAYS,");
		  strBuf1.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE = 'TF_F_USER_ASKLEAVE' AND COL_CODE='STATE' AND ENUM_CODE = a.STATE ) STATE_NAME,");
		  strBuf1.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE = 'TF_F_USER_ASKLEAVE' AND COL_CODE='REQ_TYPE' AND ENUM_CODE = a.REQ_TYPE ) REQ_TYPENAME,");
		  strBuf1.append("USER_ID,FIR_CHK FIR_CHK_USERID,SEC_CHK SEC_CHK_USERID,ID,STATE ");
		  strBuf1.append("FROM ");
		  strBuf1.append("TF_F_USER_ASKLEAVE a ");
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
		  if(!StringUtil.isNull(params.getString("REQ_STATE"))){
			  strBuf1.append("and a.STATE=:REQ_STATE ");	//状态
		  }
		  if(!StringUtil.isNull(params.getString("NT_NAME"))){
			  strBuf1.append("and a.user_id in (select u.user_id from tf_f_user u where u.name like '%"+params.getString("NT_NAME")+"%') ");
		  }
		  
		  //***** 20160902 添加归属团队判断，只能查询当前归属团队下的人员*********
		  strBuf1.append(" and (select distinct(DD.GROUP_ID) from TF_F_GROUP_SUB SS,TF_F_GROUP AA,TF_F_GROUP BB,TF_F_GROUP CC,TF_F_GROUP DD ");
		  strBuf1.append(" where SS.GROUP_ID = AA.GROUP_ID AND SS.STATE = 0 ");
		  strBuf1.append(" and AA.GROUP_LEVEL = 4 and AA.PARENT_GROUP_ID = BB.GROUP_ID and now() between AA.start_time and AA.end_time ");
		  strBuf1.append(" and BB.GROUP_LEVEL = 3 and BB.PARENT_GROUP_ID = CC.GROUP_ID and now() between BB.start_time and BB.end_time ");
		  strBuf1.append(" and CC.GROUP_LEVEL = 2 and CC.PARENT_GROUP_ID = DD.GROUP_ID and now() between CC.start_time and CC.end_time ");
		  strBuf1.append(" and DD.GROUP_LEVEL = 1 and now() between DD.start_time and DD.end_time   ");
		  strBuf1.append(" and SS.MEM_ID =:USERIDS) IN  ");
		  strBuf1.append(" (select DDD.GROUP_ID from TF_F_GROUP_SUB SSS,TF_F_GROUP AAA,TF_F_GROUP BBB,TF_F_GROUP CCC,TF_F_GROUP DDD ");
		  strBuf1.append(" where SSS.GROUP_ID = AAA.GROUP_ID ");
		  strBuf1.append(" and AAA.GROUP_LEVEL = 4 and AAA.PARENT_GROUP_ID = BBB.GROUP_ID and now() between AAA.start_time and AAA.end_time  ");
		  strBuf1.append(" and BBB.GROUP_LEVEL = 3 and BBB.PARENT_GROUP_ID = CCC.GROUP_ID and now() between BBB.start_time and BBB.end_time ");
		  strBuf1.append(" and CCC.GROUP_LEVEL = 2 and CCC.PARENT_GROUP_ID = DDD.GROUP_ID and now() between CCC.start_time and CCC.end_time ");
		  strBuf1.append(" and DDD.GROUP_LEVEL = 1 and now() between DDD.start_time and DDD.end_time  ");
		  strBuf1.append(" and A.USER_ID = SSS.mem_id) ");
		  //***** 20160902***************************************************** 
		  
		  strBuf1.append("ORDER BY a.REQ_TIME DESC");
		  return this.queryList(strBuf1.toString(), params);
	}

	/*
	 * 废弃不再使用
	 * */
	public boolean queryUserPm(String userId) throws Exception {
		StringBuffer  sqlbuf = new StringBuffer();
		sqlbuf.append("	select * from tf_f_group_sub t ");
		sqlbuf.append("	where t.MEM_ROLE in ('P','M') ");
		sqlbuf.append(" and mem_id =  '");
		sqlbuf.append(userId);
		sqlbuf.append("'");
		IDataset acct = this.queryList(sqlbuf.toString(),new  DataMap());
		return acct.size()>0?true:false;
	}

	public void auditLeava(IData params)  throws Exception {
		// TODO Auto-generated method stub
		StringBuffer  sqlbuf = new StringBuffer();
		sqlbuf.append("update tf_f_user_askleave set STATE=:STATE,");
		if(1==Math.abs(params.getInt("STATE"))){
			sqlbuf.append(" FIR_CHKTIME= now() ");
			if(params.containsKey("FIR_PS")){
				sqlbuf.append(", FIR_PS=:FIR_PS ");
			}
		}
		if(2==Math.abs(params.getInt("STATE"))){
			sqlbuf.append(" SEC_CHK= :SEC_CHK ,");
			sqlbuf.append(" SEC_CHKTIME = now() ");
			if(params.containsKey("SEC_PS")){
				sqlbuf.append(", SEC_PS = :SEC_PS ");
			}
		}
		if(3==Math.abs(params.getInt("STATE"))){
			sqlbuf.append(" RETURN_TIME = now() ");		
		}
		
		sqlbuf.append(" WHERE ID=:ID ");

		if(99==Math.abs(params.getInt("STATE"))){
			this.executeUpdate("delete from tf_f_user_askleave where ID=:ID",params);
		}
		else
			this.executeUpdate(sqlbuf.toString(),params);
	}

	public boolean validateAudit(IData params) throws Exception {
		String sql = "select count(1) SUM from  tf_f_user_askleave  where STATE>=ABS(:STATE) and ID=:ID";
		return this.queryList(sql,params).first().getInt("SUM")==0;
	}

	public void sendMailToSec(IData params) throws Exception{
		// TODO Auto-generated method stub
	}

	public String chkState(int oldState, int newState) throws Exception {
		// TODO Auto-generated method stub

		VacationDao dao = new VacationDao("bainiu");
		String oldName0 = StringUtil.getCodeName(dao,TABLE_NAME,"STATE","0");
		String oldName1 = StringUtil.getCodeName(dao,TABLE_NAME,"STATE","1");
		String oldName2 = StringUtil.getCodeName(dao,TABLE_NAME,"STATE","2");
		String newName = StringUtil.getCodeName(dao,TABLE_NAME,"STATE",String.valueOf(newState));
		String respModel = "";
		if(oldState==3)
			return "【已销假】不允许操作！";
		switch(newState)
		{
			case -1: ;
			case 1:  respModel = "只有状态为【"+oldName0+"】的请假记录才能进入下一个【"+newName+"】状态！";break;
			case 99:  respModel = "只有状态为【"+oldName0+"】即未审批的请假记录才能【"+newName+"】！";break;
			case 2:  ;
			case -2: respModel = "只有状态为【"+oldName1+"】的请假记录才能进入下一个【"+newName+"】状态！";break;
			case 3:  respModel = "只有状态为【"+oldName2+"】的请假记录才能进入下一个【"+newName+"】状态！";break;
			default : respModel="";
		}
		
		if(oldState==0 && (newState==-1||newState==1)) respModel = "";
		else if(oldState==0 && newState==99) respModel = "";
		else if(oldState==1 && (newState==-2||newState==2)) respModel = "";
		else if(oldState==2 && (newState==3)) respModel = "";
		
		return respModel;
	}

	public String chkPerson(String userID,int newState,IData params,IDataset pmInfos) throws Exception {
		// TODO Auto-generated method stub
		String strResp = "";
		VacationDao adaoN = new VacationDao("bainiu");
		GroupDao gdao = new GroupDao("bainiu");
		IData firChkBuf  = adaoN.queryUserByUserId(params.getString("FIR_CHK"));
		IData userInfo 	= adaoN.queryUserByUserId(params.getString("USER_ID"));
		if((newState==1||newState==-1) && (!userID.equals(params.getString("FIR_CHK")))){						
			strResp = userInfo.getString("NAME")+"的申请只有第一审批人【"+firChkBuf.getString("NAME")+"】可以审批";
		}
		
		int days = StringUtil.compDateDif(params.getString("BACK_DATE"),params.getString("OUT_DATE"))-1;
		System.out.println("###days="+days);
		if(days >= 10){
			IDataset pmInfos10 = gdao.qryQjManagerRight(params,"DATA_LEAVE_CHK10");
			if(pmInfos10.size()>0)
				pmInfos = pmInfos10;
		}
		else if(days >= 7){
			IDataset pmInfos7 = gdao.qryQjManagerRight(params,"DATA_LEAVE_CHK7");
			if(pmInfos7.size()>0)
				pmInfos = pmInfos7;
		}
		
		if((newState==99) && (!userID.equals(params.getString("USER_ID"))))
			strResp = "只有请假人【"+userInfo.getString("NAME")+"】才能撤销请假";
		if((newState==3) && (!userID.equals(params.getString("USER_ID"))))
			strResp = "只有请假人【"+userInfo.getString("NAME")+"】才能销假";
		if(newState==2||newState==-2){
			boolean bChk = true;
			String pmName = "";
			for(int i =0;i<pmInfos.size();i++){
				IData PmUserbuf = (IData)pmInfos.get(i);
				IData pmUserInfo= adaoN.queryUserByUserId(PmUserbuf.getString("USER_ID"));
				if(PmUserbuf.getString("USER_ID").equals(userID)){
					bChk = false;
				}
				if(pmUserInfo!=null) {
					pmName = pmName + "|" + pmUserInfo.getString("NAME");
				}
			}
			if(bChk) strResp=userInfo.getString("NAME")+"的申请只有第二审批人(PM管理人员【"+pmName+"】)可以审批";
		}
		return strResp;
	}
		
	public IDataset queryExportTab(String key) throws Exception 
	{
		IData buf = new DataMap();
		buf.put("TAB_KEY", key);
		StringBuffer strBuf1 = new StringBuffer();
		strBuf1.append("select *  from tf_f_export_tab t  where t.TAB_KEY=:TAB_KEY");
		return this.queryList(strBuf1.toString(),buf);	 
 	}
	
	/*
	  * 获取模板状态、类型参数
	  * */
	public IData querySelectType(IData param,IData outParams) throws Exception
	{
		IDataset BTypeList=this.queryList("select a.ENUM_CODE,a.ENUM_NAME  from td_s_enumerate a where a.COL_CODE='STATE' and a.SUBSYS_CODE='SYS' and a.TABLE_CODE='TF_F_USER_ASKLEAVE' ORDER BY SHOW_ORDER ASC", param);
		outParams.put("LEAVE_STATELIST", BTypeList);
		IDataset CTypeList=this.queryList("select a.ENUM_CODE,a.ENUM_NAME  from td_s_enumerate a where a.COL_CODE='REQ_TYPE' and a.SUBSYS_CODE='SYS' and a.TABLE_CODE='TF_F_USER_ASKLEAVE' ORDER BY SHOW_ORDER ASC", param);
		outParams.put("LEAVE_TYPELIST", CTypeList);		
		return outParams;
	}
}
