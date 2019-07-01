package com.ipu.server.core.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.mail.EmailAttachment;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.mobile.frame.bean.AbstractBean;
import com.ipu.server.core.context.IpuContextData;
import com.ipu.server.dao.LoginDao;
import com.ipu.server.dao.MsgDao;
import com.ipu.server.util.BNSMS;
import com.ipu.server.util.Mail;
import com.ipu.server.util.MailUtil;
import com.ipu.server.util.StringUtil;

public class AppBean extends AbstractBean {

	
	
	@Override
	protected IpuContextData getContextData() throws Exception {
		return (IpuContextData)(getContext().getContextData());
	}
	protected IData getResultData()throws Exception {
		IData resultData = new DataMap();
		if(null != this.getContextData()){
			log.debug("页面加入参数：Context="+getContextData());
			resultData.put("QUESTION_NAV", ((IpuContextData)getContext().getContextData()).getData().get("QUESTION_NAV"));
			resultData.put("SYSMAN_NAV", ((IpuContextData)getContext().getContextData()).getData().get("SYSMAN_NAV"));
			resultData.put("TOOLS_NAV", ((IpuContextData)getContext().getContextData()).getData().get("TOOLS_NAV"));
			resultData.put("PM_NAV", ((IpuContextData)getContext().getContextData()).getData().get("PM_NAV"));
			resultData.put("BPS_NAV", ((IpuContextData)getContext().getContextData()).getData().get("BPS_NAV"));
			resultData.put("RIGHT", ((IpuContextData)getContext().getContextData()).getRight());
			resultData.put("NAME", ((IpuContextData)getContext().getContextData()).getName());
		}	
		return resultData;
	}
	
	protected IData doActionLog(IData param)throws Exception {
		IData logBuf = new DataMap();
		LoginDao dao= new LoginDao("bainiu");
		if(null!=getContextData()){
			logBuf.put("USER_ID",getContextData().getUserID());
		}
		logBuf.put("IN_TIME", StringUtil.getSysTime());
		logBuf.put("ACTION_TIME", StringUtil.getSysTime());
		//logBuf.put("LOG_ID", StringUtil.getSequence(dao, StringUtil.LogIdSeq));
		logBuf.put("LOG_ID", StringUtil.getSequenceNew(dao, StringUtil.StrLogIdSeq));
		logBuf.put("IN_IP",StringUtil.getLocalIp(getRequest()));
		logBuf.put("IN_TERMINAL",getRequest().getHeader("User-Agent"));
		logBuf.put("IN_BROWSER",param.getString("BROWSER"));
		logBuf.put("SESSION_ID",param.getString("SESSION_ID"));
		logBuf.put("ACTION_CODE",getDataAction());
		logBuf.put("PARAM_CODE",param.toString());
		dao.insert("tf_l_userlog", logBuf);
		return logBuf;
	}
	protected Boolean addNB(String userId,int num)throws Exception {
		LoginDao dao= new LoginDao("bainiu");
		IData buf = new DataMap();
		buf.put("USER_ID", userId);
		buf.put("NB", num);
		
		return dao.addNB(buf);
		
	}
	protected Boolean sendSMS(String msg,int mod)throws Exception {
		BNSMS sms = new BNSMS(mod,msg);
		return sms.send(getContextData().getUserID());
	}
	protected Boolean sendSMStoSomeone(String msg,String user_id,int mod)throws Exception {
		BNSMS sms = new BNSMS(mod,msg);
		return sms.send(user_id);
	}
	protected Boolean sendSMStoSomeone(String msg,String user_id,String mail_id,int mod)throws Exception {
		BNSMS sms = new BNSMS(mod,msg);
		sms.setSMS_ID(mail_id);
		return sms.send(user_id);
	}
	
	/* 
	 * 邮件发送
	 * */
	
	protected Boolean sendMail(List<String> addto,List<String> addCc,List<String> addBcc,List<EmailAttachment> addAtta,String subject,String msg) throws Exception {
		return sendMail(addto,addCc,addBcc,addAtta,subject,msg,"");
	}
	protected Boolean sendMail(List<String> addto,List<String> addCc,List<String> addBcc,List<EmailAttachment> addAtta,String subject,String msg,String flag) throws Exception {
		Mail mail = new Mail();
		mail.setToAddress(addto); // 接收人
		mail.setCcAddress(addCc); // 抄送人
		mail.setBccAddress(addBcc); // 密送人
		mail.setAttachments(addAtta); // 附件
		mail.setSubject(subject);
		mail.setContent(msg);
		mail.setFlag(flag);
		new MailUtil().send(mail);
		
		addto.clear();
		addCc.clear();
		addBcc.clear();
		addAtta.clear();
		
		return true;
	}
	/* 
	 * 邮件发送
	 * */
	protected Boolean sendMailSimple(List<String> addto,String subject,String msg) throws Exception{
		return sendMailSimple(addto,subject,msg,"");
	}
	
	protected Boolean sendMailSimple(List<String> addto,String subject,String msg,String flag) throws Exception {
		List<String> addCc = new ArrayList<String>();
		List<String> addBcc = new ArrayList<String>();
		List<EmailAttachment> addAtta = new ArrayList<EmailAttachment>();
		
		Mail mail = new Mail();
		mail.setToAddress(addto); // 接收人
		mail.setCcAddress(addCc); // 抄送人
		mail.setBccAddress(addBcc); // 密送人
		mail.setAttachments(addAtta); // 附件
		mail.setSubject(subject);
		mail.setContent(msg);
		mail.setFlag(flag);
		new MailUtil().send(mail);
		addto.clear();
		return true;
	}	
}
