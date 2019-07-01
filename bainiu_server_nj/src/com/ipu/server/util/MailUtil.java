package com.ipu.server.util;

import java.util.List;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.dao.TempletManDao;
import com.ipu.server.dao.UtilDao;

/**
 * 发送邮件Util
 */
public class MailUtil {
	protected final Logger logger = Logger.getLogger(getClass());	
	
    //邮箱
    private  String mailServerHost = "mail.asiainfo.com";
    private  String mailSenderAddress = "bainiu6@asiainfo.com";
    private  String mailSenderNick = "摆牛管理员";
    private  String mailSenderUsername = "bainiu6";
    private  String mailSenderPassword = "aibn@333";
    private  String mailBack = "bainiu6@asiainfo.com";
    
    /**
     * 发送 邮件方法 (Html格式，支持附件)
     * @return void
     * @throws Exception 
     */
    public boolean send(Mail mailInfo) throws Exception {
    	
    	UtilDao utilDao = new UtilDao("bainiu");
    	TempletManDao templetDao = new TempletManDao("bainiu");
    	
    	IData codeDefine = new DataMap();
    	codeDefine=utilDao.getCodeDefine("SYSMAIL");
    	if(codeDefine.size()>0){
    		String[] str = codeDefine.get("CODE").toString().split("\\|");
    		mailSenderAddress = str[0];
    		mailSenderNick = str[1];
    		mailSenderUsername = str[2];
    		mailSenderPassword = str[3];
    	}
    	
    	String signatureType = "摆牛邮件签名";
    	
    	if(mailInfo.getFlag().equals("ISHARE")){
        	codeDefine=utilDao.getCodeDefine("ISHARE");
        	if(codeDefine.size()>0){
        		String[] str = codeDefine.get("CODE").toString().split("\\|");
        		mailSenderAddress = str[0];
        		mailSenderNick = str[1];
        		mailSenderUsername = str[2];
        		mailSenderPassword = str[3];
        		
        		signatureType = "爱分享签名";
        	}
    	}
    	
    	IData templ = new DataMap();
    	String signature = "";    	
    	templ = templetDao.getTempletDefine(signatureType);
    	if(templ.size()>0)
    		signature = templ.get("TEMPLET_CONTENT").toString();
         
        try {
            HtmlEmail email = new HtmlEmail();
            // 配置信息
            email.setHostName(mailServerHost);
            email.setFrom(mailSenderAddress,mailSenderNick);
            //如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码
            email.setAuthentication(mailSenderUsername,mailSenderPassword);
            email.setCharset(Mail.ENCODEING);
            //主题
            email.setSubject(mailInfo.getSubject());
            //内容，支持html
            email.setHtmlMsg(mailInfo.getContent()+signature);

            // 添加附件
            List<EmailAttachment> attachments = mailInfo.getAttachments();
            if (null != attachments && attachments.size() > 0) {
                for (int i = 0; i < attachments.size(); i++) {
                    email.attach(attachments.get(i));
                }
            }
            
            // 收件人            
            List<String> toAddress = mailInfo.getToAddress();
            if (null != toAddress && toAddress.size() > 0) {
                for (int i = 0; i < toAddress.size(); i++) {
                        email.addTo(toAddress.get(i));
                }
            }
            // 抄送人
            List<String> ccAddress = mailInfo.getCcAddress();
            if (null != ccAddress && ccAddress.size() > 0) {
                for (int i = 0; i < ccAddress.size(); i++) {
                        email.addCc(ccAddress.get(i));
                }
            }
            //邮件模板 密送人
            email.addBcc(mailBack);//邮件秘密备份
            List<String> bccAddress = mailInfo.getBccAddress();
            if (null != bccAddress && bccAddress.size() > 0) {
                for (int i = 0; i < bccAddress.size(); i++) {
                    email.addBcc(bccAddress.get(i));
                }
            }
            email.send();
            //System.out.println("邮件发送成功！");
            logger.info("###发送成功");
            return true;
        } catch (EmailException e) {
            e.printStackTrace();
            logger.info("###发送失败");
            return false;
        } 

    }	
 /*
    public class TestMail {

        public static void main(String[] args) {
            MailInfo mailInfo = new MailInfo();
            List<String> toList = new ArrayList<String>();
            toList.add("my@163.com");
            
            List<String> ccList = new ArrayList<String>();
            ccList.add("my@163.com");
            
            List<String> bccList = new ArrayList<String>();
            bccList.add("my@163.com");
            
            //添加附件
            EmailAttachment att = new EmailAttachment();
            att.setPath("g:\\测试.txt");
            att.setName("测试.txt");
            List<EmailAttachment> atts = new ArrayList<EmailAttachment>();
            atts.add(att);
            mailInfo.setAttachments(atts);
            
            mailInfo.setToAddress(toList);//收件人
            mailInfo.setCcAddress(ccList);//抄送人
            mailInfo.setBccAddress(bccList);//密送人
            
            mailInfo.setSubject("测试主题");
            mailInfo.setContent("内容：<h1>test,测试</h1>");
             
            MailUtil.sendEmail(mailInfo);

        }
    }*/
}
