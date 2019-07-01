package com.ipu.server.dao;


import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ipu.server.util.StringUtil;

public class UserDao extends SmartBaseDao
{
	private static transient Logger log = Logger.getLogger(UserDao.class);
	static String TABLE_NAME = "TF_F_USER";


	public UserDao(String connName) throws Exception
	{
		super(connName);
	}
	
	/**
	 * 注册
	 */
	public boolean register(IData params) throws Exception 
	{
		  boolean isSuccess=false;
          isSuccess= this.insert(TABLE_NAME, params);
          if(isSuccess){
        	  //注册默认给游客权限 CUSTOMER
        	  this.executeUpdate("insert into tf_f_user_right(USER_ID,RIGHT_CODE,STATE,USE_TAG) values(:USER_ID,'CUSTOMER','1','0')", params);
        	  //公司人员直接赋权中级故障库权限 EX_MANAGE_LEVEL1
        	  if(!StringUtil.getStringNew(params,"HIRE_FLAG","").equals(""))
        		  this.executeUpdate("insert into tf_f_user_right(USER_ID,RIGHT_CODE,STATE,USE_TAG) values(:USER_ID,'EX_MANAGE_LEVEL1','1','0')", params);
          }
	     return isSuccess;
 	} 

	/**
	 * 校验注册帐号唯一性
	 * CHK_TYPE 0-亚信校验OK 1-亚信校验失败 2-其他邮箱 3-电话
	 */
	public IData checkUser(IData param,IData outParam) throws Exception 
	{ 	  
		  IDataset acct = new DatasetList();
		  StringBuffer strBuf = new StringBuffer();
		  int chkType = Integer.parseInt(param.getString("CHK_TYPE"));
		  String resp = "";
		  
		  if(chkType==0||chkType==1||chkType==2||chkType==3){
			  strBuf.append("select *  from tf_f_user t  where t.USER_ACCT='");
			  strBuf.append(param.get("USER_ACCT"));
			  strBuf.append("'");
			  acct = this.queryList(strBuf.toString(),param);
			  if(acct.size()!=0)
			  {
				  resp = "帐号重复(亚信邮箱为@前缀重复/其他邮箱为邮箱重复/手机号为手机号重复)|";
			  }
		  }
		  acct = new DatasetList();
		  strBuf = new StringBuffer();
		  if(chkType==0||chkType==1||chkType==2){
			  strBuf.append("select *  from tf_f_user t  where t.EMAIL='");
			  strBuf.append(param.get("EMAIL"));
			  strBuf.append("'");
			  acct = this.queryList(strBuf.toString(),param);
			  if(acct.size()!=0)
			  {
				  resp += "邮箱重复|";
			  }
		  }		
		  acct = new DatasetList();
		  strBuf = new StringBuffer();
		  if(chkType==0||chkType==3){
			  strBuf.append("select *  from tf_f_user t  where t.PHONE='");
			  strBuf.append(param.get("PHONE"));
			  strBuf.append("'");
			  acct = this.queryList(strBuf.toString(),param);
			  if(acct.size()!=0)
			  {
				  resp += "电话重复|";
			  }
		  }
		  outParam.put("CHK_RSP", resp);
		  
/*		  strBuf.append("select *  from tf_f_user t  where t.EMAIL='");
		  strBuf.append(param.get("EMAIL")+"@asiainfo.com");
		  strBuf.append("'");
		  IDataset email = this.queryList(strBuf.toString(),param);	 
		  if(email.size()!=0)
		  {
			  outParam.put("EMAIL_ERROR", "NT帐号已存在！");
		  }
		  else
		  {
			  outParam.put("EMAIL_ERROR", "");
		  }
		  
		  StringBuffer strBuf1 = new StringBuffer();
		  strBuf1.append("select *  from tf_f_user t  where t.USER_ACCT='");
		  strBuf1.append(param.get("USER_ACCT"));
		  strBuf1.append("'");
		  IDataset acct = this.queryList(strBuf1.toString(),param);	 
		  if(acct.size()!=0)
		  {
			  outParam.put("ACCT_ERROR", "帐号已存在！请重新输入");
		  }
		  else
		  {
			  outParam.put("ACCT_ERROR", "");
		  }*/
		  return outParam;
 	} 

	/**
	 * 查询用户信息
	 */
	public IData queryUserInfo(IData param) throws Exception 
	{
 	     
		return this.queryList("SELECT * FROM TF_F_USER WHERE USER_ID=:USER_ID", param).first();
 	} 
	
	/**
	 * 查询用户信息
	 */
	public int modifyUser(IData param) throws Exception 
	{
		StringBuffer sql=new StringBuffer();
		
		sql.append("update tf_f_user t set t.NICK_NAME =:NICK_NAME ,t.EMAIL=:EMAIL,t.PHONE=:PHONE,t.PASSWORD=:PASSWORD ");
		sql.append(",NAME=:NAME,NTACCT=:NTACCT,ORG_NAME=:ORG_NAME ");
		sql.append("where t.USER_ID=:USER_ID ");
		return this.executeUpdate(sql.toString(), param);
 	}
	
	public void updUser(IData param) throws Exception {
		// TODO Auto-generated method stub
		String email = "",phone="",orgname="",name="";
		if(!(null==param.get("EMAIL")||param.getString("EMAIL").trim().isEmpty()))
		{
			email = param.getString("EMAIL").trim();
		}
		if(!(null==param.get("PHONE")||param.getString("PHONE").trim().isEmpty()))
		{
			phone = param.getString("PHONE").trim();
		}
		if(!(null==param.get("ORG_NAME")||param.getString("ORG_NAME").trim().isEmpty()))
		{
			orgname = param.getString("ORG_NAME").trim();
		}	
		if(!(null==param.get("NAME")||param.getString("NAME").trim().isEmpty()))
		{
			name = param.getString("NAME").trim();
		}		
		
		StringBuffer  sqlbuf = new StringBuffer();
		sqlbuf.append("UPDATE tf_f_user a SET a.USER_ACCT=a.USER_ACCT");
		if(!email.equals(""))
			sqlbuf.append(",a.EMAIL='"+email+"' ");
		if(!phone.equals(""))
			sqlbuf.append(",a.phone='"+phone+"' ");
		if(!orgname.equals(""))
			sqlbuf.append(",a.org_name='"+orgname+"' ");
		if(!name.equals(""))
			sqlbuf.append(",a.name='"+name+"' ");
		sqlbuf.append(" WHERE USER_ID=:USER_ID ");
		
		log.debug("##"+sqlbuf.toString());
		
		this.executeUpdate(sqlbuf.toString(),param);
	}

	public void delUser(IData param) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer  sqlbuf = new StringBuffer();
		sqlbuf.append("UPDATE tf_f_user a SET a.STATE= :STATE");
		sqlbuf.append(" WHERE USER_ID=:USER_ID ");
		this.executeUpdate(sqlbuf.toString(),param);
	}	

	/*
	 * 模糊联想用户信息,同一个群组的才返回
	 * */
	public IDataset getLinkAcct(IData param,IData userList,String key) throws Exception {
		  StringBuffer strBuf = new StringBuffer();
		  strBuf.append("select USER_ACCT,NAME,USER_ID  from tf_f_user a  WHERE a.NAME like '");
		  strBuf.append(param.get("USER_ACCT")+"%' ");
		  strBuf.append("or a.USER_ACCT like '");
		  strBuf.append(param.get("USER_ACCT")+"%' ");
		  log.info("##queryUser="+strBuf.toString());
		  
		  IDataset respList = new DatasetList();
		  
		  IDataset rightList = this.queryList(strBuf.toString(),param);
		  
		  IDataset allList = (IDataset) userList.get(key);
		  for(int i=0;i<rightList.size();i++){
			  IData wa1 = (IData) rightList.get(i);
			  for(int j=0;j<allList.size();j++){
				  IData wa2 = (IData) allList.get(j);
				  if(wa1.get("USER_ID").equals(wa2.get("USER_ID"))){
					  respList.add(wa1);
					  break;
				  }
			  }
		  }
		  
		  return respList;
	}
}
