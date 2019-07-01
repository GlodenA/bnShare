package com.ipu.server.dao;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ipu.server.util.StringUtil;

public class GroupDao extends SmartBaseDao
{
	private static transient Logger log = Logger.getLogger(GroupDao.class);
	static String TABLE_NAME = "TF_F_GROUP";
	static String TABLE_NAME_SUB = "TF_F_GROUP_SUB";
	static String NOT_FLIT_GROUPID = "10000";
	static String GROUP_END_TIME = "2099-12-31";


	public GroupDao(String connName) throws Exception
	{
		super(connName);
	}
	
	public IData queryGroupTree(IData param, IData resultData,String keyList) throws Exception{
		// TODO Auto-generated method stub

			StringBuffer strBuf = new StringBuffer();
			
			String groupL = "1";
			
			if(!(null==param.get("GROUP_LEVEL")||param.getString("GROUP_LEVEL").trim().isEmpty()))
			{
				groupL = param.getString("GROUP_LEVEL").trim();
			}
			if(groupL.equals("1")){
				strBuf.append("SELECT DISTINCT A.GROUP_ID GROUP1_ID,A.GROUP_NAME GROUP1_NAME ");
				strBuf.append("FROM TF_F_GROUP A ");
				strBuf.append("WHERE A.GROUP_LEVEL='1' ");
				strBuf.append("and now() between a.start_time and a.end_time ");
			}
			else if(groupL.equals("2") || groupL.equals("p2"))
			{			
				strBuf.append("SELECT DISTINCT A.GROUP_ID GROUP1_ID,A.GROUP_NAME GROUP1_NAME,B.GROUP_ID GROUP2_ID,B.GROUP_NAME GROUP2_NAME ");
				strBuf.append("FROM TF_F_GROUP A,TF_F_GROUP B ");
				strBuf.append("WHERE A.GROUP_LEVEL='1' ");
				strBuf.append("AND A.GROUP_ID='"+param.getString("GROUP1_ID").trim()+"' ");
				strBuf.append("AND B.GROUP_LEVEL='2' ");
				strBuf.append("AND B.PARENT_GROUP_ID=A.GROUP_ID ");
				strBuf.append("and now() between a.start_time and a.end_time ");
				strBuf.append("and now() between b.start_time and b.end_time ");
			}
			else if(groupL.equals("3") || groupL.equals("p3"))
			{			
				strBuf.append("SELECT DISTINCT A.GROUP_ID GROUP2_ID,A.GROUP_NAME GROUP2_NAME,B.GROUP_ID GROUP3_ID,B.GROUP_NAME GROUP3_NAME ");
				strBuf.append("FROM TF_F_GROUP A,TF_F_GROUP B ");
				strBuf.append("WHERE A.GROUP_LEVEL='2' ");
				strBuf.append("AND A.GROUP_ID='"+param.getString("GROUP2_ID").trim()+"' ");
				strBuf.append("AND B.GROUP_LEVEL='3' ");
				strBuf.append("AND B.PARENT_GROUP_ID=A.GROUP_ID ");
				strBuf.append("and now() between a.start_time and a.end_time ");
				strBuf.append("and now() between b.start_time and b.end_time ");				
			}	
			else if(groupL.equals("4") || groupL.equals("p4"))
			{
				strBuf.append("SELECT DISTINCT A.GROUP_ID GROUP3_ID,A.GROUP_NAME GROUP3_NAME,B.GROUP_ID GROUP4_ID,B.GROUP_NAME GROUP4_NAME ");
				strBuf.append("FROM TF_F_GROUP A,TF_F_GROUP B ");
				strBuf.append("WHERE A.GROUP_LEVEL='3' ");
				strBuf.append("AND A.GROUP_ID='"+param.getString("GROUP3_ID").trim()+"' ");
				strBuf.append("AND B.GROUP_LEVEL='4' ");
				strBuf.append("AND B.PARENT_GROUP_ID=A.GROUP_ID ");
				strBuf.append("and now() between a.start_time and a.end_time ");
				strBuf.append("and now() between b.start_time and b.end_time ");				
			}	
			log.info("##queryGroupTree="+strBuf.toString());
			
			IDataset rightList = this.queryList(strBuf.toString(),param);	 
			resultData.put(keyList, rightList);
			return resultData;			
	}

	public IData queryGroup(IData param, IData resultData,String keyList) throws Exception{
		// TODO Auto-generated method stub
		String group1 = "",group2 = "",group3 = "",group4 = "",queryKeys = "",queryState = "";
		if(!(null==param.get("GROUP1_ID")||param.getString("GROUP1_ID").trim().isEmpty()))
		{
			group1 = param.getString("GROUP1_ID").trim();
		}
		if(!(null==param.get("GROUP2_ID")||param.getString("GROUP2_ID").trim().isEmpty()))
		{
			group2 = param.getString("GROUP2_ID").trim();
		}
		if(!(null==param.get("GROUP3_ID")||param.getString("GROUP3_ID").trim().isEmpty()))
		{
			group3 = param.getString("GROUP3_ID").trim();
		}
		if(!(null==param.get("GROUP4_ID")||param.getString("GROUP4_ID").trim().isEmpty()))
		{
			group4 = param.getString("GROUP4_ID").trim();
		}
		if(!(null==param.get("QUERY_KEY")||param.getString("QUERY_KEY").trim().isEmpty()))
		{
			queryKeys = param.getString("QUERY_KEY").trim();
		}
		if(!(null==param.get("QUERY_STATE")||param.getString("QUERY_STATE").trim().isEmpty()))
		{
			queryState = param.getString("QUERY_STATE").trim();
		}
		
		try {
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("SELECT F.NAME NAME,F.USER_ID,'' GS,F.NTACCT NTACCT,USER_ACCT, ");
			strBuf.append("A.GROUP_ID GROUP1_ID,A.GROUP_NAME GROUP1_NAME,B.GROUP_ID GROUP2_ID,B.GROUP_NAME GROUP2_NAME, ");
			strBuf.append("C.GROUP_ID GROUP3_ID,C.GROUP_NAME GROUP3_NAME,D.GROUP_ID GROUP4_ID,D.GROUP_NAME GROUP4_NAME, ");
			strBuf.append("F.ORG_NAME ORG_NAME,substring_index(ORG_NAME,'-',2) BORG,F.PHONE PHONE,F.EMAIL EMAIL, ");
			strBuf.append("(SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE = 'TF_F_GROUP_SUB' AND COL_CODE='STATE' AND ENUM_CODE =  E.STATE ) STATE, ");
			strBuf.append("E.START_TIME START_TIME,E.END_TIME END_TIME,E.REMARK REMARK,(SELECT NAME FROM TF_F_USER WHERE USER_ID = E.UPD_USER_ID) UPD ");
			strBuf.append("FROM TF_F_GROUP A,TF_F_GROUP B,TF_F_GROUP C,TF_F_GROUP D,TF_F_GROUP_SUB E,TF_F_USER F ");
			strBuf.append("WHERE A.GROUP_LEVEL='1' ");
			strBuf.append("AND B.GROUP_LEVEL='2' ");
			strBuf.append("AND B.PARENT_GROUP_ID=A.GROUP_ID ");
			strBuf.append("AND C.GROUP_LEVEL='3' ");
			strBuf.append("AND C.PARENT_GROUP_ID=B.GROUP_ID ");
			strBuf.append("AND D.GROUP_LEVEL='4' ");
			strBuf.append("AND D.PARENT_GROUP_ID=C.GROUP_ID ");
			strBuf.append("AND E.GROUP_ID = D.GROUP_ID ");
			strBuf.append("and now() between a.start_time and a.end_time ");
			strBuf.append("and now() between b.start_time and b.end_time ");
			strBuf.append("and now() between c.start_time and c.end_time ");
			strBuf.append("and now() between d.start_time and d.end_time ");			
			strBuf.append("AND E.MEM_ID=F.USER_ID ");
			if(!queryKeys.equals(""))
				strBuf.append("AND (F.NAME like '%"+queryKeys+"%' or F.PHONE like '%"+queryKeys+"%' or F.EMAIL like '%"+queryKeys+"%') ");
			if(!queryState.equals("") && !queryState.equals("all"))
				strBuf.append("AND E.STATE='"+queryState+"' ");
			IDataset rightList = this.queryList(strBuf.toString(),param);
			if(!group1.equals("")&&!group1.equals(NOT_FLIT_GROUPID)) rightList = filter(rightList,"GROUP1_ID",group1);
			if(!group2.equals("")) rightList = filter(rightList,"GROUP2_ID",group2);
			if(!group3.equals("")) rightList = filter(rightList,"GROUP3_ID",group3);
			if(!group4.equals("")) rightList = filter(rightList,"GROUP4_ID",group4);
			
			resultData.put(keyList, rightList);
			
			if(rightList.size()>0){
				resultData.put(keyList, StringUtil.increaseRowNo((DatasetList)resultData.get(keyList)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return  resultData;
	}
	
	public IDataset filter(IDataset rightList,String keyStr,String value){
		IDataset newData = new DatasetList();
		log.debug("##"+rightList.size());
		log.debug("##"+rightList.toString());		
		for(int i =0;i<rightList.size();i++){
			IData temp = new DataMap();
			temp = rightList.getData(i);
/*			log.debug("##"+keyStr.toString());
			log.debug("##"+temp.toString());*/
			if(temp.get(keyStr).equals(value)){
				newData.add(temp);
				/*log.info("##"+newData.size());		*/		
			}
		}
		log.debug("##"+newData.size());
		log.debug("##"+newData.toString());
		return newData;
	}

	public IData queryUser(IData param, IData resultData, String keyList) throws Exception {
		// TODO Auto-generated method stub
		  StringBuffer strBuf = new StringBuffer();
		  strBuf.append("select *  from tf_f_user a  WHERE a.name like '%");
		  strBuf.append(param.get("KEY")+"%' ");
		  strBuf.append("or a.ntacct like '%");
		  strBuf.append(param.get("KEY")+"%' ");
		  strBuf.append("or a.phone like '%");
		  strBuf.append(param.get("KEY")+"%' ");
		  strBuf.append("or a.email like '%");
		  strBuf.append(param.get("KEY")+"%' ");		  
		  log.info("##queryUser="+strBuf.toString());
		  IDataset rightList = this.queryList(strBuf.toString(),param);	 
		  resultData.put(keyList, rightList);
		  return resultData;
	}
	
	/**
	 * 查询人员信息是否存在
	 * @return
	 * @throws Exception
	 */
	public IDataset queryItemNum(IData param) throws Exception {
		String sql = "select * from tf_f_user_item t where user_id =:USER_ID";
		return this.queryList(sql,param);
		
	}
	
	/**
	 * 添加人员基础信息
	 * @param set
	 * @return
	 * @throws Exception
	 */
	public void insertUserItem(IDataset set) throws Exception {
		for(int i= 0;i<set.size();i++){
			IData data = set.getData(i);
			this.executeUpdate("insert into tf_f_user_item(user_id,attr_code,attr_value,start_time) VALUES(:USER_ID,:ATTR_CODE,:ATTR_VALUE,NOW())", data);
		}
	}
	
	/*排重*/
	public IData modifyGroup(IData param, IData resultData) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf = new StringBuffer();
		String oper = "";
			
		if(!(null==param.get("GROUP_OPER")||param.getString("GROUP_OPER").trim().isEmpty()))
		{
			oper = param.getString("GROUP_OPER").trim();
		}
		if(oper.equals("add")) {
			int count=0;
			IDataset wa = (IDataset) param.get("N_USERLIST");
			String newGroup = param.getString("N_GROUP4_ID");
			String remark = param.getString("REMARK");
			log.info("##add "+wa.size());
			for(int i=0;i<wa.size();i++){
				IData paramO = wa.getData(i);
				strBuf.setLength(0);
				strBuf.append("insert into tf_f_group_sub (GROUP_ID, MEM_ID, MEM_ROLE, DEV_ROLE, WORK_PLACE, STATE, START_TIME, END_TIME, UPD_TIME, UPD_USER_ID, REMARK) ");
				strBuf.append("select ?,?,1,1,'','0',now(),null,now(),'',? from dual");
				count += this.executeUpdate(strBuf.toString(),new Object[]{newGroup,paramO.get("USER_ID"),remark});
			}
			resultData.put("result", count);
		}
		else if(oper.equals("mov")) {
			int count=0;
			IDataset wa = (IDataset) param.get("O_USERLIST");
			String newGroup = param.getString("N_GROUP4_ID");
			String remark = param.getString("REMARK");
			log.info("##move "+wa.size());
			for(int i=0;i<wa.size();i++){
				IData paramO = wa.getData(i);
				strBuf.setLength(0);
				strBuf.append("update tf_f_group_sub set end_time = CURDATE(),state='1',remark=? where group_id = ? and mem_id = ? ");
				count += this.executeUpdate(strBuf.toString(),new Object[]{remark,paramO.get("GROUP4_ID"),paramO.get("USER_ID")});
				strBuf.setLength(0);
				strBuf.append("insert into tf_f_group_sub (GROUP_ID, MEM_ID, MEM_ROLE, DEV_ROLE, WORK_PLACE, STATE, START_TIME, END_TIME, UPD_TIME, UPD_USER_ID, REMARK) ");
				strBuf.append("select ?,?,1,1,'','0',CURDATE(),null,now(),'',? from dual");
				count += this.executeUpdate(strBuf.toString(),new Object[]{newGroup,paramO.get("USER_ID"),remark});
			}
			resultData.put("result", count);
		}
		else if(oper.equals("cop")){
			int count=0;
			IDataset wa = (IDataset) param.get("O_USERLIST");
			String newGroup = param.getString("N_GROUP4_ID");
			String remark = param.getString("REMARK");
			log.info("##copy "+wa.size());
			for(int i=0;i<wa.size();i++){
				IData paramO = wa.getData(i);
				strBuf.setLength(0);
				strBuf.append("insert into tf_f_group_sub (GROUP_ID, MEM_ID, MEM_ROLE, DEV_ROLE, WORK_PLACE, STATE, START_TIME, END_TIME, UPD_TIME, UPD_USER_ID, REMARK) ");
				strBuf.append("select ?,?,1,1,'','0',CURDATE(),null,now(),'',? from dual");
				count += this.executeUpdate(strBuf.toString(),new Object[]{newGroup,paramO.get("USER_ID"),remark});
			}
			resultData.put("result", count);
		}
		else if(oper.equals("del")){
			int count=0;
			IDataset wa = (IDataset) param.get("O_USERLIST");
			String remark = param.getString("REMARK");
			log.info("##del "+wa.size());
			for(int i=0;i<wa.size();i++){
				IData paramO = wa.getData(i);
				strBuf.setLength(0);
				strBuf.append("update tf_f_group_sub set end_time = CURDATE(),upd_time=now(),state='1',remark=? where group_id = ? and mem_id = ? ");
				count += this.executeUpdate(strBuf.toString(),new Object[]{remark,paramO.get("GROUP4_ID"),paramO.get("USER_ID")});
			}
			resultData.put("result", count);
		}
		else if(oper.endsWith("addgroup")){
			int count=0;
			String remark = param.getString("REMARK");
			String groupLevel = param.getString("GROUP_LEVEL");
			String groupName = param.getString("GROUP_NAME");
			String pregroupId = "";
			if(groupLevel.equals("1")) pregroupId="";
			else if(groupLevel.equals("2")) pregroupId = param.getString("GROUP1_ID");
			else if(groupLevel.equals("3")) pregroupId = param.getString("GROUP2_ID");
			else if(groupLevel.equals("4")) pregroupId = param.getString("GROUP3_ID");
			
			String groupId = StringUtil.getSequenceNew(this,StringUtil.StrGroupIdSeq);
			
			strBuf.setLength(0);
			strBuf.append("INSERT INTO tf_f_group (GROUP_ID, GROUP_TYPE, GROUP_LEVEL, GROUP_NAME, PARENT_GROUP_ID, CREATE_USER_ID, START_TIME, END_TIME, UPD_TIME, UPD_USER_ID, REMARK) ");
			strBuf.append("select ?,'1',?,?,?,'',now(),?,now(),'',? from dual");
			count += this.executeUpdate(strBuf.toString(),new Object[]{groupId,groupLevel,groupName,pregroupId,GROUP_END_TIME,remark});
			resultData.put("GROUP_ID",groupId);
			resultData.put("result", count);
		}
	    return resultData;
	}

	public IData init(IData param, IData resultData) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IDataset qryQjManagerRight(IData params,String rightCode) throws Exception{
		IData paramsin = params;
		//查找用户当前所在群组，查找归属一级群组，一级群组下所有分组内成员具有 DATA_LEAVE_CHK 权限的
		paramsin.put("MUM_CODE", rightCode);
		String sql = "SELECT E.MEM_ID USER_ID											"
		+"FROM TF_F_GROUP B1,TF_F_GROUP B2,TF_F_GROUP B3,TF_F_GROUP B4,TF_F_GROUP_SUB E "
		+"WHERE B1.GROUP_ID IN (														"
		+"	SELECT A1.GROUP_ID															"
		+"	FROM TF_F_GROUP_SUB A,TF_F_GROUP A4,TF_F_GROUP A3,TF_F_GROUP A2,TF_F_GROUP A1"
		+"	WHERE A.MEM_ID= :USER_ID AND A.STATE='0' AND A.GROUP_ID = A4.GROUP_ID		"
		+"	AND A3.GROUP_ID=A4.PARENT_GROUP_ID AND A2.GROUP_ID=A3.PARENT_GROUP_ID		"
		+"	AND A1.GROUP_ID=A2.PARENT_GROUP_ID)											"		
		+"	AND B2.PARENT_GROUP_ID=B1.GROUP_ID AND B3.PARENT_GROUP_ID=B2.GROUP_ID		"
		+"	AND B4.PARENT_GROUP_ID=B3.GROUP_ID AND E.GROUP_ID = B4.GROUP_ID	AND E.STATE='0'	"
		+"	AND EXISTS (SELECT 1 FROM TF_F_USER_RIGHT M,TD_M_ROLE_MUM N WHERE M.USER_ID = E.MEM_ID " 
		+"	AND M.RIGHT_CODE = N.ROLE_CODE AND N.MUM_CODE = :MUM_CODE "
		+"	AND M.STATE='1' AND M.USE_TAG='0' )";
		return this.queryList(sql, paramsin);
	}
	//查询分组信息
	public IData qryUserGroup(String userid) throws Exception{
		IData buf = new DataMap();
		buf.put("USER_ID", userid);
		String sql = "select A.GROUP_NAME GROUP1_NAME,A.GROUP_ID GROUP1_ID,B.GROUP_NAME GROUP2_NAME,B.GROUP_ID GROUP2_ID,"
		        +"l.GROUP_NAME GROUP3_NAME,l.GROUP_ID GROUP3_ID,M.GROUP_NAME GROUP4_NAME,M.GROUP_ID GROUP4_ID from tf_f_group a,tf_f_group b,tf_f_group l, tf_f_group m ,tf_f_group_sub n "
				+"where n.MEM_ID=:USER_ID and n.STATE='0' and n.GROUP_ID=m.GROUP_ID "
				+"and b.PARENT_GROUP_ID=a.GROUP_ID and SYSDATE() BETWEEN a.START_TIME and a.END_TIME "
				+"and l.PARENT_GROUP_ID=b.GROUP_ID and SYSDATE() BETWEEN b.START_TIME and b.END_TIME "
				+"and m.PARENT_GROUP_ID=l.GROUP_ID and SYSDATE() BETWEEN l.START_TIME and l.END_TIME "
				+"and SYSDATE() BETWEEN m.START_TIME and m.END_TIME";
		IDataset groupInfos = this.queryList(sql,buf);
		IData groupName = null;
		if(groupInfos.size()>0)
			groupName = ((IData)groupInfos.get(0));
		return  groupName;
	}
	
	public IData qryGroupInfoById(IData params,String groupLevel)throws Exception{
		IData buf = new DataMap();
		buf.put("GROUP_ID", params.get(groupLevel));
		String sql = "select  MAN_USERLIST from tf_f_group where group_id = :GROUP_ID " ;
		IDataset groupInfos = this.queryList(sql,buf);
		IData groupName = null;
		if(groupInfos.size()>0)
			groupName = ((IData)groupInfos.get(0));
		return  groupName;
	}

	public IDataset qryGroupLevel1User(IData params) throws Exception {
		// TODO Auto-generated method stub
		String sql = "SELECT DISTINCT f.* "
				+" from tf_f_group a,tf_f_group b,tf_f_group c,tf_f_group d,tf_f_group_sub e,tf_f_user f"
				+" where INSTR(:IS_TARGROUPID, a.GROUP_ID)>0 and a.END_TIME>now()"
				+" and b.PARENT_GROUP_ID = a.GROUP_ID and b.END_TIME>now()"
				+" and c.PARENT_GROUP_ID = b.GROUP_ID and c.END_TIME>now()"
				+" and d.PARENT_GROUP_ID = c.GROUP_ID and d.END_TIME>now()"
				+" and e.GROUP_ID = d.GROUP_ID"
				+" and (e.END_TIME>now() or e.END_TIME is null)"
				+" and e.STATE='0'"
				+" and f.USER_ID = e.MEM_ID";
		return this.queryList(sql, params);
	}
}
