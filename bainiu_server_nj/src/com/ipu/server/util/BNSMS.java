package com.ipu.server.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.dao.BaseDAO;
import com.ipu.server.dao.MsgDao;

public class BNSMS {
	public static int SMS_ADDNB_MOD = 0;
	public static int SMS_LEAVE_MYNOTICE_MOD = 1;
	public static int SMS_LEAVE_ADMINNOTICE_MOD = 2;
	public static int SMS_PUBLIC_NOTICE_MOD = 3;
	public static int SMS_ERR_PUB = 4;
	
	public static String SMS_LEAVE_CHKYES = "操作成功";
	public static String SMS_LEAVE_CHKNO  = "审核时效已过期";
	
	
	
	
	
	private String SMS_TITLE;
	private String SMS_ABSTRACT;
	private String SMS_CONTENT;
	private String SMS_ID;
	private String SMS_STATE = "0";
	private int SMS_TYPE;
	private int SMS_MOD;

	
	public int getSMS_MOD() {
		return SMS_MOD;
	}

	public void setSMS_MOD(int sms_mod) {
		SMS_MOD = sms_mod;
	}

	public BNSMS(int SMS_MOD,String msg){
		this.SMS_CONTENT=msg;
		flashSMSParam(SMS_MOD);
	}

	public Boolean send(String useid) throws Exception{
		if(SMS_TYPE==0){
			return sendSMStoPub(useid);
		}
		if(SMS_TYPE==1){
			return sendSMS(useid);
		}
		return false;
	}
	
	private void flashSMSParam(int SMS_MOD) {
		// TODO Auto-generated method stub
		switch(SMS_MOD){
		case 0: this.SMS_TITLE="系统积分奖励";
				this.SMS_ABSTRACT="系统积分奖励";
				this.SMS_TYPE=1;
				break;
		case 1: this.SMS_TITLE="请假申请提交成功通知";
				this.SMS_ABSTRACT="请假提交通知";
				this.SMS_TYPE=1;
				break;
		case 2: this.SMS_TITLE="您有待审核的请假通知";
				this.SMS_ABSTRACT="请假审核通知";
				this.SMS_TYPE=1;
				break;
		case 3: this.SMS_TITLE="公告："+SMS_CONTENT.substring(0, 20)+".....";
				this.SMS_ABSTRACT="公告通知";
				this.SMS_TYPE=0;
				break;
		case 4: this.SMS_TITLE="您提出的问题已经成功发布";
				this.SMS_ABSTRACT="问题发布";
				this.SMS_TYPE=1;
				break;
		case 5: this.SMS_TITLE="";
				this.SMS_ABSTRACT="";
				break;
		case 6: this.SMS_TITLE="";
				this.SMS_ABSTRACT="";
				break;
		}
	}

	public int getSMS_TYPE() {
		return SMS_TYPE;
	}

	public void setSMS_TYPE(int sms_type) {
		SMS_TYPE = sms_type;
	}

	public String getSMS_TITLE() {
		return SMS_TITLE;
	}
	public void setSMS_TITLE(String sms_title) {
		SMS_TITLE = sms_title;
	}
	public String getSMS_ABSTRACT() {
		return SMS_ABSTRACT;
	}
	public void setSMS_ABSTRACT(String sms_abstract) {
		SMS_ABSTRACT = sms_abstract;
	}
	public String getSMS_CONTENT() {
		return SMS_CONTENT;
	}
	public void setSMS_CONTENT(String sms_content) {
		SMS_CONTENT = sms_content;
	}
	public String getSMS_STATE() {
		return SMS_STATE;
	}
	public void setSMS_STATE(String sms_state) {
		SMS_STATE = sms_state;
	}
	
	public Boolean sendSMS(String useId) throws Exception{
		MsgDao dao = new MsgDao("bainiu");
		IData buf = new DataMap();
		buf.put("SMS_TITLE", SMS_TITLE);
		buf.put("SMS_ABSTRACT", SMS_ABSTRACT);
		buf.put("SMS_CONTENT", SMS_CONTENT);
		buf.put("SMS_STATE", SMS_STATE);
		buf.put("USER_ID", useId);
		if(SMS_ID==null){
			//buf.put("SMS_ID", StringUtil.getSequence(dao, StringUtil.LogIdSeq));
			buf.put("SMS_ID", StringUtil.getSequenceNew(dao, StringUtil.StrLogIdSeq));
		}else{
			buf.put("SMS_ID", SMS_ID);
		}
		buf.put("SMS_TYPE", SMS_TYPE);
		buf.put("INS_TIME", StringUtil.getSysTime());
		return dao.insert("tf_f_sms", buf);
	}
	
	public Boolean sendSMStoPub(String useId) throws Exception{
		return sendSMS("-1");
	}

	public String getSMS_ID() {
		return SMS_ID;
	}

	public void setSMS_ID(String sms_id) {
		SMS_ID = sms_id;
	}
	
}
