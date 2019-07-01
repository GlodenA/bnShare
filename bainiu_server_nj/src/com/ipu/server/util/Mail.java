package com.ipu.server.util;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.mail.EmailAttachment;

@SuppressWarnings("serial")  
public class Mail  implements Serializable {
	public static final String ENCODEING = "UTF-8";
	
	private String subject; // 主题

	private String content; // 信息(支持HTML)	
	private String flag; //区分发送邮箱
	
    // 收件人
    private List<String> toAddress = null;
    // 抄送人地址
    private List<String> ccAddress = null;
    // 密送人
    private List<String> bccAddress = null;
    // 附件信息
    private List<EmailAttachment> attachments = null;	
	
    public List<String> getToAddress() {
        return toAddress;
    }

    public void addToAddress(String toAddress) {
        this.toAddress.add(toAddress);
    }

    public void addToAddress(List<String> toAddress) {
        this.toAddress.addAll(toAddress);
    }

    public void addCcAddress(List<String> ccAddress) {
        if (null != ccAddress && ccAddress.size() > 0)
            this.ccAddress.addAll(ccAddress);
    }

    public List<EmailAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<EmailAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<String> getBccAddress() {
        return bccAddress;
    }

    public void setBccAddress(List<String> bccAddress) {
        this.bccAddress = bccAddress;
    }
    
    public void setToAddress(List<String> toAddress) {
        this.toAddress = toAddress;
    }

    public List<String> getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(List<String> ccAddress) {
        this.ccAddress = ccAddress;
    }

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
