package com.ipu.server.bean;


import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.core.context.IpuContextData;
import com.ipu.server.core.session.IpuSessionManager;
import com.ipu.server.dao.LoginDao;
import com.ipu.server.dao.UtilDao;
import com.ipu.server.util.BNSMS;
import com.ipu.server.util.StringUtil;


public class Login extends AppBean{
		
	/**
	 * 登陆逻辑
	 */
	public IData doLogin(IData param) throws Exception{
		IpuContextData contextData = getContextData();
		
		IData resultData = new DataMap();

		/*账号密码校验*/
		LoginDao dao= new LoginDao("bainiu");
		IData userInfo = dao.getUserInfo(param);
		if(null == userInfo){
			resultData.put("IS_LOGIN_IN", false);
			resultData.put("X_INFO", StringUtil.USER_LOGIN_ERRPWD);
			return resultData;
		}
		
		//升级校验
		UtilDao utildao = new UtilDao("bainiu");
		IData chkLock = utildao.getTag("UPD_CHK_LOCK");
		if(null == chkLock){
		}
		else{
			IData chkUser = utildao.getTag("UPD_CHK_USER");
			if(null == chkUser){
				resultData.put("IS_LOGIN_IN", false);
				resultData.put("X_INFO", StringUtil.UPD_CHK_LOCKINFO);
				return resultData;	
			}else{
				String tagInfo = chkUser.get("TAG_INFO").toString();
				if(tagInfo.indexOf(param.get("USER_ACCT").toString()+"|")<0)
				{
					resultData.put("IS_LOGIN_IN", false);
					resultData.put("X_INFO", StringUtil.UPD_CHK_LOCKINFO);
					return resultData;	
				}
			}
				
		}
		
		resultData.put("IS_LOGIN_IN", true);
		param.put("USER_ID", userInfo.getString("USER_ID"));

		//登陆准备,加载context
		IDataset rightInfos = dao.getRightInfos(param);
		IpuSessionManager.getInstance().destorySession();
		contextData = new IpuContextData();
		contextData.setEmail(userInfo.getString("EMAIL"));
		contextData.setLoginInFlag(true);
		if("".equals(userInfo.getString("NAME",""))){
			if("".equals(userInfo.getString("NICK_NAME",""))){
				contextData.setName(userInfo.getString("USER_ACCT",""));
			}else{
				contextData.setName(userInfo.getString("NICK_NAME",""));
			}
		}else{
			contextData.setName(userInfo.getString("NAME"));
		}
		contextData.setNickName(userInfo.getString("NICK_NAME"));
		contextData.setNTacct(userInfo.getString("NTACCT"));
		contextData.setOrg(userInfo.getString("ORG"));
		contextData.setPhone(userInfo.getString("PHONE"));
		contextData.setUserID(userInfo.getString("USER_ID"));
		contextData.setUserAcc(userInfo.getString("USER_ACCT"));
		contextData.setRights(rightInfos);
		contextData.setDomain(userInfo.getString("SYS_DOMAIN"));
		contextData.setSecurityLevel(userInfo.getInt("SECURITY_LEVEL"));
		
		//加载页面参数
		IDataset questionRights = dao.getQuestionRightInfos(param);
		contextData.getData().put("QUESTION_NAV", questionRights);
		
		IDataset sysmanmRights = dao.getSysmanmRightInfos(param);
		contextData.getData().put("SYSMAN_NAV", sysmanmRights);
		
		IDataset toolsRights = dao.getToolsRightInfos(param);
		contextData.getData().put("TOOLS_NAV", toolsRights);
		
		IDataset pmRights = dao.getPMRightInfos(param);
		contextData.getData().put("PM_NAV", pmRights);

		IDataset bpsRights = dao.getBPSRightInfos(param);
		contextData.getData().put("BPS_NAV", bpsRights);

		String sessionId = IpuSessionManager.getInstance().createSession(contextData);
		resultData.put("SESSION_ID", sessionId);
		resultData.put("QUESTION_NAV", questionRights);
		resultData.put("SYSMAN_NAV", sysmanmRights);
		resultData.put("TOOLS_NAV", toolsRights);

		resultData.put("BPS_NAV", bpsRights);
		
		resultData.putAll(userInfo);
		resultData.put("RIGHTS", rightInfos);
		resultData.put("RIGHT", contextData.getRight());
		/*记录浏览器信息*/
		LoginDao dao1= new LoginDao("bainiu");
		dao1.insertVistBrower(param);
		param.put("SESSION_ID", sessionId);
		
		if(dao1.isThisDayfristLogin(param) && addNB(userInfo.getString("USER_ID"),10)){
			StringBuffer msgBuf = new StringBuffer();
			msgBuf.append("<p>                                                                                     ");
			msgBuf.append("    &nbsp; 尊敬的["+contextData.getName()+"] 您好：                                                        ");
			msgBuf.append("</p>                                                                                    ");
			msgBuf.append("<p>                                                                                     ");
			msgBuf.append("   			 &nbsp; &nbsp; &nbsp; &nbsp;此次登陆为每日首次登入系统，                       ");
			msgBuf.append("   			 获得10<img src='template/webapp/bainiu/bainiuMan/images/bonus.png'/>奖励，    ");
			msgBuf.append("   			 请在个人资料查看您获得的牛币。感谢您对我们的支持~                             ");
			msgBuf.append("</p>                                                                                    ");
			sendSMS(msgBuf.toString(),BNSMS.SMS_ADDNB_MOD);
		}
		doActionLog(param);	
		return resultData;
	}
	public IData CloseApp(IData param) throws Exception{
		IData data = new DataMap();
		LoginDao dao= new LoginDao("bainiu");
		param.put("OUT_TIME", StringUtil.getSysTime());
		dao.loginOut(param);
		getContextData();
		return data;
	}
	
	/**
	 * 登陆逻辑
	 */
	public IData findPwd(IData param) throws Exception{	
		IData resultData = new DataMap();
		/*账号密码校验*/
		LoginDao dao= new LoginDao("bainiu");
		IData userInfo = dao.getUserInfo(param);
		if(null == userInfo){
			resultData.put("FIND_OK", false);
			return resultData;
		}
		//发送邮件
		String strPwd = userInfo.getString("PASSWORD"); 
		String email = userInfo.getString("EMAIL"); 
		String contentName = userInfo.getString("NAME");
		StringBuffer smslBufMail =  new StringBuffer();
		smslBufMail.append("<html><body>");
		smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
		smslBufMail.append("   <tbody>");
		smslBufMail.append("    <tr><td>"+contentName+"，您好！</td></tr>");					
		smslBufMail.append("    <tr>");			
		smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;您当前密码为"+strPwd+"，请您及时登录修改 </td>");			
		smslBufMail.append("    </tr>");
		smslBufMail.append("    <tr><td></td></tr>");
		smslBufMail.append("    <tr><td></td></tr>");
		smslBufMail.append("    <tr><td></td></tr>");
		smslBufMail.append("   </tbody>");
		smslBufMail.append("</table>");
		smslBufMail.append("</body></html>");		
		
		List<String> listMail = new ArrayList<String>();
		listMail.add(email);
		sendMailSimple(listMail,"【摆牛系统】找回密码",smslBufMail.toString());
		resultData.put("FIND_OK", true);
		doActionLog(param);	
		return resultData;
	}
	
}
