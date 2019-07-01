package com.ipu.server.bean;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.mail.EmailAttachment;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.GroupDao;
import com.ipu.server.dao.IshareDao;
import com.ipu.server.dao.RightDao;
import com.ipu.server.dao.UserDao;
import com.ipu.server.dao.VacationDao;
import com.ipu.server.util.BNSMS;
import com.ipu.server.util.ExportFile;
import com.ipu.server.util.StringUtil;

public class IshareBean extends AppBean 
{
	public IData init(IData param) throws Exception
	{
		IData resultData = getResultData();
		resultData.putAll(param);
		return resultData;
	}
	
	public IData queryIshare(IData param) throws Exception
	{
		IData resultData = getResultData();
		IshareDao ishareDao = new IshareDao("bainiu");		
		
		String userId = "";
		if(param.getBoolean("MAIL_CLK"))
			userId = param.getString("USER_ID");
		else
			userId= getContextData().getUserID();
		
		param.put("USER_ID", userId);
		
		resultData=ishareDao.queryIshare(param, resultData, "ISHARELIST");
		//具有爱分享管理员权限
		RightDao rightDao = new RightDao("bainiu");
		if(rightDao.queryUserRight(userId,"DATA_ISHARE_OPER")){
			resultData.put("ISHARERIGHT","1");
		}
		resultData.put("USER_ID", userId);
		
		//返回状态选框
		ishareDao.querySelectType(param,resultData,"ISHARESTATE");
		
		doActionLog(param);
		return resultData;
	}

	/*
	 * 根据编号查询参加活动的人员清单以及参加信息
	 * */
	public IData qIshareEntry(IData param) throws Exception
	{
		IData resultData = getResultData();
		IshareDao ishareDao = new IshareDao("bainiu");
		param.put("IS_ID", param.get("IS_ID"));
		resultData=ishareDao.qIshareEntry(param, resultData, "ISENTRYLIST");
		
		doActionLog(param);
		return resultData;
	}
	/**
【i-Share通知】核心产品部第四十期爱分享活动之docker入门培训报名通知
【i-Share通知】活动报名结果-核心产品部第四十期爱分享活动之docker入门-李铁记
【i-Share通知】活动取消报名-核心产品部第四十期爱分享活动之docker入门-李铁记
【i-Share通知】活动进入报名-核心产品部第四十期爱分享活动之docker入门-李铁记
	 * */
	/*
	 * 人员报名活动：若活动报名人数超出规定限制数，则自动排队
	 * 人员退出活动：人员退出活动，若存在有人排队，则队列最前的人员进入报名状态
	 * 本函数因为有来自邮件点击，不能随便获取系统数据
	 * */
	public IData enrolIshare(IData params) throws Exception
	{	
		IData resultData = getResultData();	 
		IshareDao ishareDao = new IshareDao("bainiu"); 
		boolean mailClick = false;
		
		if("mail".equals(params.getString("MAILFLAG"))){
			String data =StringUtil.praseStrByDES(params.getString("SUBDATA"), "mamashuomiyaoyidingyaochang", StringUtil.DES_DECIPHER);
			String[] infos=data.split("#");
			params.put("IS_ID", infos[0]);
			params.put("STATE", infos[1]);
			params.put("USER_ID", infos[2]);
			mailClick = true;
		}else{
			params.put("USER_ID", getContextData().getUserID());
		}
		
		List<String> listMail = new ArrayList<String>();
		List<String> listMailCc = new ArrayList<String>();
		List<String> listMailBcc = new ArrayList<String>();
		List<EmailAttachment> listMailAtta = new ArrayList<EmailAttachment>();
		String mailContent = "";
		
		UserDao userDao = new UserDao("bainiu");
		IData userInfo 	= userDao.queryUserInfo(params);
		String iName = userInfo.getString("NAME");
		String iEmail = userInfo.getString("EMAIL");
		
		//判断活动是否可加入
		IData tempResp = ishareDao.queryIshareByID(params.get("IS_ID").toString());
		String stateIs = tempResp.getString("IS_STATE");
		String iShareNo = tempResp.getString("IS_NO");
		String iShareName = tempResp.getString("IS_NAME");
		
		if(!stateIs.equals("0")){
			resultData.put("result","0");
			resultData.put("resultInfo","只有报名中的才允许报名或取消");
			return resultData;
		}
		if(StringUtil.compareTime(StringUtil.getSysTime(),tempResp.getString("IS_ENDENROL"))){//判断是否已经终止报名
			resultData.put("result","0");
			resultData.put("resultInfo","报名失败，超过最晚报名时间"+tempResp.getString("IS_ENDENROL"));
			return resultData;
		}
		StringBuffer ishareContent =  new StringBuffer();
		ishareContent.append("</br></br>&nbsp;&nbsp;&nbsp;&nbsp;本次爱分享活动信息如下");		
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;主题："+tempResp.getString("IS_NAME"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;爱分享人："+tempResp.getString("IS_LECTURER"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;简介："+tempResp.getString("IS_EXPLAIN"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;时间："+tempResp.getString("IS_TIME"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;地点："+tempResp.getString("IS_PLACE"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;对象："+tempResp.getString("IS_TARGROUP"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;联系人："+tempResp.getString("IS_LINKUSER"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;最晚报名："+tempResp.getString("IS_ENDENROL"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;备注信息："+tempResp.getString("REMARK"));
		String mailtitle = "";
		try
		{			
			if(params.get("STATE").equals("0"))	{
				
				IData temp = new DataMap(); 
				temp.put("IS_ID", params.get("IS_ID"));
				temp.put("USER_ID", params.get("USER_ID"));
				temp.put("MAIL_CLK", mailClick);
				IDataset tempDataS  = (IDataset)queryIshare(temp).get("ISHARELIST");//重新查防止前台未刷新
				IData tempData = (IData) tempDataS.get(0);
				//ADD_TAG 判断不可重复加入
				if(!StringUtil.isNull(tempData.getString("ADD_TAG")))
				{
					resultData.put("result","0");
					resultData.put("resultInfo","您已报名，请勿重新报名");
					return resultData;
				}
				
				int allSum = tempData.getInt("ALL_SUM");
				int inSum = tempData.getInt("IN_SUM");
				int isLimitnum = tempData.getInt("IS_LIMITNUM");
				int state = 0,order=0;
				if(inSum >= isLimitnum) 
					state = 1;//如果人数已经满了，处理排队
				order = allSum +1;
				
				params.put("NEW_ORDER", order);
				params.put("NEW_STATE", state);
				resultData = ishareDao.addiShareEntry(params, resultData);
				doActionLog(params);
				
				listMail.add(iEmail);
				listMailCc.add(StringUtil.ISHARE_EMAIL);
				
				String stateName = StringUtil.getCodeName(ishareDao,"TF_F_ISHARE_ENTRY","STATE",String.valueOf(state));
				
				mailContent = geneMailContent("爱分享报名成功，报名状态："+stateName+ishareContent.toString());
				
				resultData.put("ENROL_NAME",stateName);
				
				//邮件通知
				mailtitle = "【i-Share通知】活动报名结果-北方技术中心"+iShareNo+"爱分享活动之"+iShareName+"-"+iName;
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,mailtitle,mailContent,"ISHARE");
			}
			else{//取消的时候增加处理，将排队中靠前的置为报名
				resultData = ishareDao.updiShareEntry(params, resultData);
				doActionLog(params);
				
				listMail.add(iEmail);
				listMailCc.add(StringUtil.ISHARE_EMAIL);
				mailContent = geneMailContent("爱分享报名取消。取消原因:"+params.getString("CANCEL_REASON")+ishareContent.toString());
				
				mailtitle = "【i-Share通知】活动取消报名-北方技术中心"+iShareNo+"爱分享活动之"+iShareName+"-"+iName;
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,mailtitle,mailContent,"ISHARE");
				
				IData temp = new DataMap(); 
				IData tempRsp = new DataMap(); 
				temp.put("IS_ID", params.get("IS_ID"));
				temp.put("STATE", "1");
				tempRsp = ishareDao.qIshareEntryFirstWait(temp);
				doActionLog(temp);
				if(tempRsp!=null && tempRsp.size()>0){
				//没有排队不需要处理
					temp.clear();
					temp.put("USER_ID", tempRsp.get("USER_ID"));
					IData userInfoN = userDao.queryUserInfo(temp);
					doActionLog(temp);
					
					resultData = ishareDao.updiShareEntry1(params, resultData);
					doActionLog(params);
					
					listMail.add(userInfoN.getString("EMAIL"));
					listMailCc.add(StringUtil.ISHARE_EMAIL);
					mailContent = geneMailContent("排队进入爱分享，具体可查看摆牛爱分享页面"+ishareContent.toString());
					
					mailtitle = "【i-Share通知】活动进入报名-北方技术中心"+iShareNo+"爱分享活动之"+iShareName+"-"+userInfoN.getString("NAME");
					sendMail(listMail,listMailCc,listMailBcc,listMailAtta,mailtitle,mailContent,"ISHARE");
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			resultData.put("result","0");
			resultData.put("resultInfo","系统异常:"+e);
			return resultData;			
		}
		
	    resultData.putAll( resultData);
	    //doActionLog(params);	 
	    return resultData;	 
	}
	
	/*
	 * 爱分享邮件模板
	 * */
	private String geneMailContent(String content){
		StringBuffer smslBufMail =  new StringBuffer();
		smslBufMail.append("<html><body>");
		smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
		smslBufMail.append("   <tbody>");
		smslBufMail.append("    <tr><td>您好！</td></tr>");
		smslBufMail.append("    <tr>");
		smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;"+content+"</td>");
		smslBufMail.append("    </tr>");
		smslBufMail.append("    <tr><td></td></tr>");
		smslBufMail.append("    <tr><td></td></tr>");
		smslBufMail.append("    <tr><td></td></tr>");
		smslBufMail.append("    <tr><td></td></tr>");
		smslBufMail.append("    <tr><td></td></tr>");
		smslBufMail.append("   </tbody>");
		smslBufMail.append("</table>");
		smslBufMail.append("</body></html>");	
		return smslBufMail.toString();
	}
	
	public IData queryResultData(IData param)throws Exception
	{
		return getResultData();
	}	
	
	/*
	 * 修改爱分享信息
	 * 修改后发邮件
	 * */
	public IData chgIshare(IData params) throws Exception 
	{
		IData resultData = getResultData();
		IshareDao ishareDao = new IshareDao("bainiu");
	
		IData ishareInfo = ishareDao.queryIshareByID(params.getString("IS_ID"));	
		if(!ishareInfo.getString("IS_STATE").equals("0")){
			resultData.put("result","0");
			resultData.put("resultInfo","只有报名中的才允许修改");
		}
		
		int rsp = 0;
		try {
			rsp = ishareDao.chgIshare(params);
			doActionLog(params);
			
			ishareInfo.clear();
			ishareInfo = ishareDao.queryIshareByID(params.getString("IS_ID"));
			//修改成功发邮件
			if(rsp!=0) email(ishareInfo,"修改");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultData.put("result","0");
			resultData.put("resultInfo","系统异常:"+e);			
		}		
		resultData.put("result",rsp);				
		return resultData;
	}
	
	private String mailCont(IData params){
		StringBuffer ishareContent =  new StringBuffer();
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;主题："+params.getString("IS_NAME"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;爱分享人："+params.getString("IS_LECTURER"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;简介："+params.getString("IS_EXPLAIN"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;时间：<font color=\"red\"><b>"+params.getString("IS_TIME")+"</b></font>");
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;地点：<font color=\"red\"><b>"+params.getString("IS_PLACE")+"</b></font>");
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;对象："+params.getString("IS_TARGROUP"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;联系人："+params.getString("IS_LINKUSER"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;截止时间："+params.getString("IS_ENDENROL"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;人数限制："+params.getString("IS_LIMITNUM"));
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;备注信息："+params.getString("REMARK"));
		ishareContent.append("</br></br>&nbsp;&nbsp;&nbsp;&nbsp;参与原则：");
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;1.请大家<font color=\"red\"><b>报名参加</b></font>爱分享活动");
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;2.请提前10分钟到场");
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;3.分享来之不易，切勿随意离场，同学们一定要保证培训的完整性");
		ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;如有问题及时沟通！");
		
		return ishareContent.toString();
	}
	
	private void email(IData params,String type) throws Exception{
		
		List<String> listMail = new ArrayList<String>();
		List<String> listMailCc = new ArrayList<String>();
		List<String> listMailBcc = new ArrayList<String>();
		List<EmailAttachment> listMailAtta = new ArrayList<EmailAttachment>();	
		
		List<String> fixMail = new ArrayList<String>();
		
		copyList(fixMail,addFixEmail());
		copyList(listMailCc,fixMail);
		copyList(listMailCc,getLectEmail(params.getString("IS_LECTURER")));
		
		//邮件通知
		listMailCc.add(StringUtil.ISHARE_EMAIL);
		String iShareName = params.getString("IS_NAME");
		String emailContent = "爱分享活动【"+iShareName+"】"+type+"成功，请您知晓</br>";
		
		if(type.equals("创建")&&params.getString("IS_LECTURER").indexOf("xieliang@")>0)
			emailContent += "</br>&nbsp; &nbsp;&nbsp;<img src=\"http://bainiu6.com/elfinder-servlet/connector?cmd=file&target=B_L7Cut9bP7S_SNvMasLzIwMTYxMDE3MTU0NzM2LmpwZw_E_E\" border=\"0\">";
		
		int emailGroupL = 0;//是否有邮件组,有邮件组收件人只给邮件组
		if(!StringUtil.isNull(params.getString("IS_EMAILGROUP"))){
			String[] emailGroup = params.getString("IS_EMAILGROUP").split("\\|");
			emailGroupL = emailGroup.length;
			for(int i=0;i<emailGroupL;i++){
				listMail.add(emailGroup[i]);
			}
		}
		
		//查找群组所有人
		GroupDao groupDao = new GroupDao("bainiu");
		RightDao rightDao = new RightDao("bainiu");
		IDataset allUser = groupDao.qryGroupLevel1User(params);
		for(int i=0;i<allUser.size();i++){
			IData tem = (IData) allUser.get(i);
			//判断管理层抄送
			if(rightDao.queryUserRight(tem.getString("USER_ID"),"DATA_PMMANAGER")){
				if(!fixMail.contains(tem.getString("EMAIL")))//不在固定抄送列表
					listMailCc.add(tem.getString("EMAIL"));			
			}
			else{
				if(emailGroupL==0)//非邮件组才单独发送
					listMail.add(tem.getString("EMAIL"));
			}				
		}		
		
		emailContent += mailCont(params);
		String mailtitle = "【i-Share通知】北方技术中心"+params.getString("IS_NO")+"爱分享活动之"+iShareName+"培训"+type+"通知";
		sendMail(listMail,listMailCc,listMailBcc,listMailAtta,mailtitle,geneMailContent(emailContent),"ISHARE");
	}
	
	public static void copyList(List<String> dest,List<String> source )
	{
		//dest.clear();
		for( int i = 0 ; i < source.size() ; i++ )         
		{             
			dest.add(source.get(i));      
		}     
	}
	
	/*
	 * 获取爱分享人
	 * */
	private List<String> getLectEmail(String list) throws Exception{
		List<String> listMail = new ArrayList<String>();
		String[] slect = list.split("\\|");
		for(int i=0;i<slect.length;i++){
			String[] temp = slect[i].split(",");
			if(temp.length>1)//如果没写邮箱不抄送
				listMail.add(temp[1]);
		}
		
		return listMail;		
	}
	
	/*
	 *获取固定抄送人员
	 * */
	private List<String> addFixEmail() throws Exception{
		List<String> listMail = new ArrayList<String>();
		
		IshareDao dao = new IshareDao("bainiu");
		IData tempQry = new DataMap();
		tempQry.put("TRADETYPE", "ISHAREFIXMAIL");
		
		IData tempRsp = new DataMap(); 
		tempRsp = dao.qIshareFixEmail(tempQry);
		doActionLog(tempQry);
		
		if(tempRsp!=null && tempRsp.size()>0){
			String email = tempRsp.get("CODE").toString();
			String[] list = email.split("\\|");
			for(int i=0;i<list.length;i++){
				listMail.add(list[i]);
			}			
		}
		return listMail;
	}
	
	/*
	 * 爱分享活动创建/结束/取消
	 * 均需要邮件通知爱分享管理员
	 * 活动创建需要通知所选群组人员
	 * 【i-Share通知】核心产品部第四十期爱分享活动之docker入门培训创建通知
【i-Share通知】核心产品部第四十期爱分享活动之docker入门培训修改通知
【i-Share通知】核心产品部第四十期爱分享活动之docker入门培训圆满结束
【i-Share通知】核心产品部第四十期爱分享活动之docker入门培训取消通知
	 * */
	public IData dealIshare(IData params) throws Exception
	{
		IData resultData = getResultData();
		IData resultDataTemp = getResultData();		
		IDataset wa = (IDataset) params.get("CHK_LIST");
		
		List<String> listMail = new ArrayList<String>();
/*		List<String> listMailCc = new ArrayList<String>();
		List<String> listMailBcc = new ArrayList<String>();
		List<EmailAttachment> listMailAtta = new ArrayList<EmailAttachment>();	*/
		
		//创建
		if(wa==null){
			IshareDao dao = new IshareDao("bainiu");			
			params.put("IS_ID", StringUtil.getSequenceNew(dao,StringUtil.StrIsIdSeq));
			params.put("UPD_USERID",getContextData().getUserID());
			params.put("UPD_TIME",StringUtil.getSysTime());
			params.remove("SESSION_ID");
			boolean isSuccess = dao.createIshare(params);
			if(isSuccess)
			{				
				resultData.put("RETRUN_STR", "新增活动成功");
				doActionLog(params);
				
				//邮件通知
				email(params,"创建");	
				
				String mailContent = "";
				String iShareName = params.getString("IS_NAME");
				String iShareNo = params.getString("IS_NO");
				
				//有邮件组则不再发送群组邮件
				if(!StringUtil.isNull(params.getString("IS_EMAILGROUP"))){
					mailContent = "爱分享活动已经录入，您可以登录摆牛系统在爱分享页面报名↓↓↓。</br>";
					
					String[] list = params.getString("IS_EMAILGROUP").split("\\|");
					for(int i=0;i<list.length;i++){
						listMail.add(list[i]);
					}
					
					StringBuffer smslBuf1 =  new StringBuffer();
					smslBuf1.append("</br><tr><td>&nbsp;&nbsp;&nbsp;&nbsp;地址：<a href=\""+StringUtil.domain+"\"><font color=\"green\"><b>www.bainiu6.com</b></font></a>");
					/*smslBuf1.append("</br><tr><td>&nbsp;&nbsp;&nbsp;&nbsp;内网：<a href=\""+StringUtil.innerdomain+"\"><font color=\"green\"><b>www.bainiu6.com</b></font></a>");*/
					smslBuf1.append("</br><tr><td>&nbsp;&nbsp;&nbsp;&nbsp;指导链接:摆牛-->知识库管理-->爱分享");
						
					mailContent += mailCont(params);
					mailContent += smslBuf1.toString();
					String mailtitle="【i-Share通知】北方技术中心"+iShareNo+"爱分享活动之"+iShareName+"培训报名通知";
					sendMailSimple(listMail,mailtitle,geneMailContent(mailContent),"ISHARE");	
				}
				else{
					GroupDao groupDao = new GroupDao("bainiu");
					RightDao rightDao = new RightDao("bainiu");
					IDataset allUser = groupDao.qryGroupLevel1User(params);								
					for(int i=0;i<allUser.size();i++){
						mailContent = "爱分享活动已经录入，您可以登录摆牛系统在爱分享页面报名或则点击邮件下面的报名链接报名↓↓↓。</br>";
						IData tem = (IData) allUser.get(i);
						if(rightDao.queryUserRight(tem.getString("USER_ID"),"DATA_PMMANAGER"))
							continue;
						listMail.add(tem.getString("EMAIL"));
						StringBuffer smslBuf1 =  new StringBuffer();
						//IS_ID|STATE|USER_ID
						smslBuf1.append("</br><tr><td>&nbsp;&nbsp;&nbsp;&nbsp;报名链接：<a href=\""+StringUtil.domain+"/mobile?action=IshareMailEnrol&data={'MAILFLAG':'mail','SUBDATA':'"
									+StringUtil.praseStrByDES(params.getString("IS_ID")+"#0#"+tem.getString("USER_ID"), "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"green\"><b>点击报名</b></font></a>");
						
						/*smslBuf1.append("</br><tr><td>&nbsp;&nbsp;&nbsp;&nbsp;内网报名链接：<a href=\""+StringUtil.innerdomain+"/mobile?action=IshareMailEnrol&data={'MAILFLAG':'mail','SUBDATA':'"
								+StringUtil.praseStrByDES(params.getString("IS_ID")+"#0#"+tem.getString("USER_ID"), "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"green\"><b>点击报名</b></font></a>");*/
										
						if(params.getString("IS_LECTURER").indexOf("xieliang@")>0)
							mailContent += "</br>&nbsp; &nbsp;&nbsp;<img src=\"http://bainiu6.com/elfinder-servlet/connector?cmd=file&target=B_L7Cut9bP7S_SNvMasLzIwMTYxMDE3MTU0NzM2LmpwZw_E_E\" border=\"0\">";
											
						mailContent += mailCont(params);
						//报名链接后移
						mailContent += smslBuf1.toString();
						
						String mailtitle="【i-Share通知】北方技术中心"+iShareNo+"爱分享活动之"+iShareName+"培训报名通知";
						sendMailSimple(listMail,mailtitle,geneMailContent(mailContent),"ISHARE");	
					}
				}
				return resultData;				
			}
			else
			{
				resultData.put("RETRUN_STR", "新增活动失败");
				doActionLog(params);
				return resultData;
			}			
		}
		
		int m=0,n=0;
		String errorInfo = "";
		for(int i=0;i<wa.size();i++){
			IData signParam = wa.getData(i);    		
    		resultDataTemp = auditIshare(signParam);
    		String re  =resultDataTemp.getString("RETRUN_STR");
    		String re2 = resultDataTemp.getString("DEAL_TAG");
    		if(re.equals(BNSMS.SMS_LEAVE_CHKYES)||re2.equals("YES"))
    			m++;
    		else if(re.equals(BNSMS.SMS_LEAVE_CHKNO)||re2.equals("NO")){
    			n++;
    			errorInfo += re+"|";
    		}
		}//更新
		
		if(wa.size()==1)
			resultData.put("RETRUN_STR", resultDataTemp.get("RETRUN_STR"));
		else{
			if(n>0)
				resultData.put("RETRUN_STR", "处理结果："+m+"成功,"+n+"失败。 失败原因："+errorInfo);
			else
				resultData.put("RETRUN_STR", BNSMS.SMS_LEAVE_CHKYES);
		}
		doActionLog(params);
	    return  resultData; 
	}

	/*
	 * 活动结束或则取消处理
	 * 判断状态是否允许，只有报名中的才能取消或则结束
	 * 取消需要通知已报名人员，结束需要通知参与人员填写反馈表
	 * */
	private IData auditIshare(IData params) throws Exception {
		// TODO Auto-generated method stub
		IData resultData = getResultData();
		IshareDao dao = new IshareDao("bainiu");
		IData ishareInfo = dao.queryIshareByID(params.getString("IS_ID"));
		
		UserDao userDao = new UserDao("bainiu");
		IData tempQry = new DataMap();
		tempQry.put("USER_ID", ishareInfo.get("UPD_USERID"));
		IData updUser = userDao.queryUserInfo(tempQry);

		//状态判断，旧与新对比
		String chk = dao.chkState(ishareInfo.getInt("IS_STATE"),params.getInt("IS_STATE"));
		if(!chk.equals("")){
			resultData.put("RETRUN_STR", chk);
			resultData.put("DEAL_TAG", "NO");
			resultData.put("RETRUN_ERR", chk);
			return resultData;
		}
		if(true){
			dao.dealIshareState(params);
			doActionLog(params);		
			
			String stateName = StringUtil.getCodeName(dao,"TD_B_ISHARE","IS_STATE",String.valueOf(params.getInt("IS_STATE")));
			String iShareName = ishareInfo.getString("IS_NAME");
			String iShareNo = ishareInfo.getString("IS_NO");
			
			//邮件通知
			List<String> listMail = new ArrayList<String>();
			List<String> listMailCc = new ArrayList<String>();
			List<String> listMailBcc = new ArrayList<String>();
			List<EmailAttachment> listMailAtta = new ArrayList<EmailAttachment>();
			
			List<String> fixMail = new ArrayList<String>();
			
			StringBuffer ishareContent =  new StringBuffer();
			ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;主题："+ishareInfo.getString("IS_NAME"));
			ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;爱分享人："+ishareInfo.getString("IS_LECTURER"));
			ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;简介："+ishareInfo.getString("IS_EXPLAIN"));
			ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;时间："+ishareInfo.getString("IS_TIME"));
			ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;地点："+ishareInfo.getString("IS_PLACE"));
			ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;对象："+ishareInfo.getString("IS_TARGROUP"));
			ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;联系人："+ishareInfo.getString("IS_LINKUSER"));
			ishareContent.append("</br>&nbsp;&nbsp;&nbsp;&nbsp;备注："+ishareInfo.getString("REMARK"));

			String mailContent = "",mailtitle="";
			GroupDao groupDao = new GroupDao("bainiu");
			if(params.getInt("IS_STATE")==2){//取消
				
				copyList(fixMail,addFixEmail());
				copyList(listMailCc,fixMail);
				copyList(listMailCc,getLectEmail(ishareInfo.getString("IS_LECTURER")));
				
				listMailCc.add(StringUtil.ISHARE_EMAIL);
				
				int emailGroupL = 0;//是否有邮件组,有邮件组收件人只给邮件组
				if(!StringUtil.isNull(ishareInfo.getString("IS_EMAILGROUP"))){
					String[] emailGroup = ishareInfo.getString("IS_EMAILGROUP").split("\\|");
					emailGroupL = emailGroup.length;
					for(int i=0;i<emailGroupL;i++){
						listMail.add(emailGroup[i]);
					}
				}

				//查找群组所有人				
				RightDao rightDao = new RightDao("bainiu");
				IDataset allUser = groupDao.qryGroupLevel1User(ishareInfo);
				for(int i=0;i<allUser.size();i++){
					IData tem = (IData) allUser.get(i);
					//判断管理层抄送
					if(rightDao.queryUserRight(tem.getString("USER_ID"),"DATA_PMMANAGER")){
						if(!fixMail.contains(tem.getString("EMAIL")))
							listMailCc.add(tem.getString("EMAIL"));
					}
					else{
						if(emailGroupL==0)//非邮件组才单独发送
							listMail.add(tem.getString("EMAIL"));
					}
				}
				
				mailContent = "很抱歉的通知大家，因"+params.getString("REMARK")+
						"，故本期的爱分享活动延期举行，本期主题重新开课时会及时通知大家参加，感谢大家的支持。</br></br>&nbsp;&nbsp;&nbsp;&nbsp;以下为本期主题初始信息</br>";
				mailContent += ishareContent.toString();
				
				mailtitle = "【i-Share通知】北方技术中心"+iShareNo+"爱分享活动之"+iShareName+stateName+"通知";
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,mailtitle,geneMailContent(mailContent),"ISHARE");
			}
			else{//1结束  结束邮件指发给参与人		
				copyList(fixMail,addFixEmail());
				copyList(listMailCc,fixMail);
				copyList(listMailCc,getLectEmail(ishareInfo.getString("IS_LECTURER")));
				
				listMailCc.add(StringUtil.ISHARE_EMAIL);
				
				IData entryRsp = new DataMap();
				entryRsp=dao.qIshareEntry(params, entryRsp, "ISENTRYLIST");
				IDataset entryAll = (IDataset)entryRsp.get("ISENTRYLIST");
				for(int i=0;i<entryAll.size();i++){
					IData tem = (IData) entryAll.get(i);
					if(tem.getString("STATE").equals("0"))
						listMail.add(tem.getString("EMAIL"));
				}
				
				mailContent = "本期活动结束，感谢大家的支持，请大家按要求填写爱分享活动反馈表，如无表格请找爱分享管理员【"
							+updUser.getString("NAME")+"】领取。</br></br>&nbsp;&nbsp;&nbsp;&nbsp;以下为本期主题信息";
				mailContent += ishareContent.toString();
				
				mailtitle = "【i-Share通知】北方技术中心"+iShareNo+"爱分享活动之"+iShareName+"圆满"+stateName;
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,mailtitle,geneMailContent(mailContent),"ISHARE");
			
			}
			resultData.put("RETRUN_STR", BNSMS.SMS_LEAVE_CHKYES);
			resultData.put("DEAL_TAG", "YES");
		}
		return resultData;
	}	
	
	/*
	 * 重新导出多个shee页的
	 * */
	public void exportIshareList(IData param,HttpServletResponse response)throws Exception
	{
		IData resultData = new DataMap();
		IshareDao ishareDao = new IshareDao("bainiu");
		IDataset set = (IDataset)ishareDao.queryIshareExport(param);
		IDataset set1 = (IDataset)ishareDao.qIshareEntry(param, resultData, "ISENTRYLIST").get("ISENTRYLIST");
		
		VacationDao vacationDao = new VacationDao("bainiu");
		IDataset tabset = vacationDao.queryExportTab("IshareList");
		IDataset tabset2 = vacationDao.queryExportTab("IshareEntryList");
		ExportFile file = new ExportFile();
		IData allInfo = new DataMap();
		
		IDataset sheetNameSet =new DatasetList();
		IDataset tabHeadSet =new DatasetList();
		IDataset tabInfoSet =new DatasetList();
		
		sheetNameSet.add("爱分享活动信息");
		sheetNameSet.add("爱分享活动报名信息");		
		allInfo.put("SHEET_NAME", sheetNameSet);
		
		tabHeadSet.add(tabset);		
		tabHeadSet.add(tabset2);		
		allInfo.put("TABHEAD", tabHeadSet);
		
		tabInfoSet.add(set);
		tabInfoSet.add(set1);
		allInfo.put("TABINFO", tabInfoSet);
		
		file.exportInfo(response,"爱分享信息.xls",allInfo);
	}	
}
