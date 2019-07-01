package com.ipu.server.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONArray;

import com.ai.MainQuery;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.ErrDao;
import com.ipu.server.dao.GroupDao;
import com.ipu.server.dao.UserDao;
import com.ipu.server.dao.VacationDao;
import com.ipu.server.util.StringUtil;

public class UserBean extends AppBean {
	public IData init(IData param) throws Exception
	{
		IData resultData = getResultData();
		resultData.putAll(param);		
		
		return resultData;
	}	

	public IData register(IData registerData) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserDao userDao = new UserDao("bainiu");
		registerData.put("REG_TIME", df.format(new Date()));
		registerData.put("HIRE_FLAG", "是");
		registerData.put("USER_ID", StringUtil.getSequenceNew(userDao,StringUtil.StrUserIdSeq));// TODO
		boolean isSuccess = userDao.register(registerData);

		IData resultData = getResultData();
		if (isSuccess) {

			resultData.put("result", "0");
			return resultData;
		} else {
			resultData.put("result", "1");
			return resultData;
		}
	}

	public IData doRegister(IData registerData) throws Exception {
		IData resultData = getResultData();
		MainQuery qry = new MainQuery();
		//支持后台注册 
		String acc_id =  "";
		if(registerData.containsKey("ACCID"))
			acc_id = registerData.getString("ACCID");
		else{
			if(registerData.containsKey("EMAIL"))
				acc_id = registerData.getString("EMAIL");
			else
				acc_id = registerData.getString("PHONE");
			registerData.put("PASSWORD", "123456");
		}
		String err_returnStr = "注册失败，可能已存在用户";
		String[] buf = acc_id.split("@");
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		registerData.put("REG_TIME", df.format(new Date()));
		//传入非亚信邮箱和手机，默认手机为账号
		if (buf.length > 1 && !buf[1].toLowerCase().equals("asiainfo.com") && registerData.get("PHONE")!=null) 
			registerData.put("USER_ACCT", registerData.getString("PHONE"));
		else
			registerData.put("USER_ACCT", acc_id);
		
		//支持后台注册
		if(registerData.get("NAME")!=null){
			registerData.put("NICK_NAME", registerData.getString("NAME"));
		}
		
		//支持后台注册
		if("".equals(registerData.getString("NICK_NAME",""))){
			registerData.put("NAME", registerData.getString("佚名"));
		}else{
			registerData.put("NAME", registerData.getString("NICK_NAME"));
		}
		//默认可以看到安全等级为0的
		registerData.put("SECURITY_LEVEL", 3);//默认0修改为3
		//内部已有不能注册
		IData outParam = new DataMap();
		//IData checkBuf = new DataMap();
		//checkBuf.put("USER_ACCT", buf[0]);
		//checkBuf.put("EMAIL", buf[0]);
		//userDao.checkUser(checkBuf, outParam);
		//boolean isSuccess  = "".equals(outParam.getString("ACCT_ERROR"))&&"".equals(outParam.getString("EMAIL_ERROR"));
		if (buf.length > 1) {// 有@认为是邮箱注册
			// 亚信外部员工
			registerData.put("EMAIL", acc_id);
			if ("asiainfo.com".equals(buf[1].toLowerCase())) {// 公司邮箱注册
				String str = qry.queryAiBook("NT=" + buf[0] + "=BAINIU6");
				
				IDataset infos = StringUtil.getTieStr2IDataset(str);
				if (infos.size() > 0) {
					// 亚信内部员工
					for (int i = 0; i < infos.size(); i++) {
						IData map = (IData) infos.get(i);
						if (buf[0].equals(map.getString("NT账号"))) {
							registerData.put("USER_ACCT", buf[0]);
							registerData.put("NAME", map.getString("姓名"));
							registerData.put("EMAIL", acc_id);
							registerData.put("PHONE", map.getString("手机号"));
							registerData.put("NTACCT", buf[0]);
							registerData.put("ORG_NAME", map.getString("部门"));
							registerData.put("HIRE_FLAG", "是");
							registerData.put("SECURITY_LEVEL", 3);
							registerData.put("CHK_TYPE", "0");
						}
					}				
				}else{
					//isSuccess= false;
					//err_returnStr="AI账号不存在";
					//只要用公司邮箱注册即认为单位人员
					registerData.put("USER_ACCT", buf[0]);
					registerData.put("EMAIL", acc_id);
					if(registerData.get("PHONE")==null)
						registerData.put("PHONE", "18812345678");
					registerData.put("NTACCT", buf[0]);
					registerData.put("ORG_NAME", "亚信");
					registerData.put("HIRE_FLAG", "是");
					registerData.put("SECURITY_LEVEL", 3);
					registerData.put("CHK_TYPE", "1");
				}
					
				
			} else {
				// 其它邮箱注册
				registerData.put("EMAIL", acc_id);
				if(registerData.get("PHONE")==null)
					registerData.put("PHONE", "18812345678");
				registerData.put("ORG_NAME", "非亚信");
				registerData.put("CHK_TYPE", "2");
			}
		} else {
			// 手机用户
			registerData.put("PHONE", acc_id);
			registerData.put("EMAIL", "default@bainiu.com");
			registerData.put("ORG_NAME", "非亚信");
			registerData.put("CHK_TYPE", "3");
		}

		UserDao userDao = new UserDao("bainiu");
		userDao.checkUser(registerData, outParam);
		err_returnStr = outParam.getString("CHK_RSP");
		boolean isSuccess  = "".equals(err_returnStr);
		if(isSuccess){//校验通过后插tf_f_user表
			registerData.put("USER_ID", StringUtil.getSequenceNew(userDao,StringUtil.StrUserIdSeq));
			isSuccess = userDao.register(registerData);
			resultData.put("IS_REG_OK", "true");
			resultData.putAll(registerData);
		} else {
			resultData.put("ERR_INFO", err_returnStr);
		}

		return resultData;
	}

	public IData checkUser(IData param) throws Exception {

		IData resultData = getResultData();
		UserDao userDao = new UserDao("bainiu");
		resultData = userDao.checkUser(param, resultData);
		return resultData;
	}

	
	public IData queryUserInfo(IData param) throws Exception {

		IData resultData = getResultData();
		UserDao userDao = new UserDao("bainiu");
		param.put("USER_ID",getContextData().getUserID());
		resultData = userDao.queryUserInfo(param);
		ErrDao errDao = new ErrDao("bainiu");
		resultData = errDao.queryErrByUser(param,resultData,"QUELIST","QRYMYERR");
		VacationDao vDao = new VacationDao("bainiu");
		resultData.put("LEAVELIST", vDao.queryLeaveByUser(param));
		param.remove("USER_ID");
		param.put("DEAL_USERID", getContextData().getUserID());
		resultData = errDao.queryErrByUser(param,resultData,"ANSLIST","QRYMYDEAL");
		param.remove("DEAL_USERID");
		return resultData;
	}
	
	public IData getLinkAcct(IData param) throws Exception {
			IData resultData = getResultData();
			IDataset keylist = new DatasetList();
			UserDao userDao = new UserDao("bainiu");
			param.put("USER_ID",getContextData().getUserID());
			
			//统一群组所有人参与联想
			IData da = new DataMap();
			IData userList = new DataMap();
			VacationDao vacationDao = new VacationDao("bainiu");
			String UserGroupID = vacationDao.queryGroupID(getContextData().getUserID());
			
			da.put("GROUP1_ID", UserGroupID);
			da.put("QUERY_STATE", "0");
			GroupDao groupDao = new GroupDao("bainiu");
			userList = groupDao.queryGroup(da, userList,"GROUPUSER");
			
			keylist=userDao.getLinkAcct(param,userList,"GROUPUSER");
			resultData.put("KEY_LIST", keylist);
			resultData.putAll(param);
			return resultData;
	}	
	
	public IData getMoreMyErr(IData param) throws Exception {
		IData resultData = new DataMap();
		param.put("USER_ID",getContextData().getUserID());
		
		ErrDao errDao = new ErrDao("bainiu");
		resultData = errDao.queryErrByUser(param,resultData,"QUELIST","QRYMYERR");
		param.remove("USER_ID");
		return resultData;
	}
	public IData getMoreMyDealErr(IData param) throws Exception {
		IData resultData = new DataMap();
		param.put("DEAL_USERID",getContextData().getUserID());
		ErrDao errDao = new ErrDao("bainiu");
		resultData = errDao.queryErrByUser(param,resultData,"ANSLIST","QRYMYDEAL");
		param.remove("DEAL_USERID");
		return resultData;
	}

	public IData modifyUser(IData param) throws Exception {

		IData resultData = getResultData();
		UserDao userDao = new UserDao("bainiu");
		param.put("USER_ID",getContextData().getUserID());
		try 
		{
		  int cnt = userDao.modifyUser(param);
		  
		 if(cnt==0)
		 {
			 resultData.put("ERROR", "没有记录被修改");
		 }
		}
		catch (Exception e) {
			resultData.put("ERROR", "异常");
		}
		
		return resultData;
	}
	
	public IData updUser(IData param) throws Exception {

		IData resultData = getResultData();
		UserDao userDao = new UserDao("bainiu");
		//param.put("USER_ID",getContextData().getUserID());
		try 
		{
		  userDao.updUser(param);
		  resultData.put("result", "0");
		}
		catch (Exception e) {
			resultData.put("result", "-1");
			resultData.put("resultinfo", "操作失败:"+e);
		}
		doActionLog(param);
		return resultData;
	}
	
	public IData delUser(IData param) throws Exception {

		IData resultData = getResultData();
		IDataset wa = (IDataset) param.get("CHK_LIST");
		
		UserDao userDao = new UserDao("bainiu");
		
		for(int i=0;i<wa.size();i++){
			IData signParam = wa.getData(i);    		
    		userDao.delUser(signParam);
		}		
		resultData.put("RETRUN_STR", "操作成功");

		doActionLog(param);
	    return  resultData; 
	}	
}
