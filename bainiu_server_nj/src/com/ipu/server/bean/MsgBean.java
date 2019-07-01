package com.ipu.server.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ailk.mobile.frame.bean.CommonBean;
import org.apache.commons.mail.EmailAttachment;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.GroupDao;
import com.ipu.server.dao.MsgDao;
import com.ipu.server.dao.VacationDao;
import com.ipu.server.util.BNSMS;
import com.ipu.server.util.StringUtil;

public class MsgBean extends AppBean
{
	public IData queryMsg(IData params) throws Exception
	{
		IData resultData = getResultData();
		params.put("USER_ID", getContextData().getUserID());
		MsgDao dao =new MsgDao("bainiu");
	    resultData.putAll(dao.queryMsg(params, resultData, "MSGLIST"));
	    return  resultData;
	}

	public IData chkLeave(IData params) throws Exception
	{
		IData resultData = getResultData();
		IData resultDataTemp = getResultData();
		IDataset wa = (IDataset) params.get("CHK_LIST");
		int m=0,n=0;
		String errorInfo = "";
		for(int i=0;i<wa.size();i++){
			IData signParam = wa.getData(i);
    		IData param = new DataMap();
    		param.put("ID", signParam.get("ID"));
    		param.put("USER_ID", signParam.get("USER_ID"));
    		param.put("STATE", signParam.get("STATE"));
    		param.put("PS", signParam.get("PS"));

    		resultDataTemp = auditLeava(param);//处理结果还有问题
    		String re  =resultDataTemp.getString("RETRUN_STR");
    		String re2 = resultDataTemp.getString("DEAL_TAG");
    		if(re.equals(BNSMS.SMS_LEAVE_CHKYES)||re2.equals("YES"))
    			m++;
    		else if(re.equals(BNSMS.SMS_LEAVE_CHKNO)||re2.equals("NO")){
    			n++;
    			errorInfo += re+"|";
    		}
		}
		if(wa.size()==1)
			resultData.put("RETRUN_STR", resultDataTemp.get("RETRUN_STR"));
		else{//审批多个整合结果
			if(n>0)
				resultData.put("RETRUN_STR", "处理结果："+m+"成功,"+n+"失败。 失败原因："+errorInfo);
			else
				resultData.put("RETRUN_STR", BNSMS.SMS_LEAVE_CHKYES);
		}
	    return  resultData;
	}

	public IData auditLeava(IData params) throws Exception
	{
		IData resultData = getResultData();
		List<String> listMail = new ArrayList<String>();
		List<String> listMailCc = new ArrayList<String>();
		List<String> listMailBcc = new ArrayList<String>();
		List<EmailAttachment> listMailAtta = new ArrayList<EmailAttachment>();

		if("mail".equals(params.getString("MAILFLAG"))){
			String data =StringUtil.praseStrByDES(params.getString("SUBDATA"), "mamashuomiyaoyidingyaochang", StringUtil.DES_DECIPHER);
			String[] infos=data.split("#");
			params.put("ID", infos[0]);
			params.put("SMS_ID", infos[1]);
			params.put("STATE", infos[2]);
			params.put("USER_ID", infos[3]);
		}else{
			params.put("USER_ID", getContextData().getUserID());
		}
		VacationDao adao = new VacationDao("bainiu");
		GroupDao gdao = new GroupDao("bainiu");
		IData subParam 		= adao.queryLeaveByID(params.getString("ID"));
		IDataset pmInfos 	= gdao.qryQjManagerRight(subParam,"DATA_LEAVE_CHK");
		IDataset pmInfos7 	= gdao.qryQjManagerRight(subParam,"DATA_LEAVE_CHK7");
		IDataset pmInfos10 	= gdao.qryQjManagerRight(subParam,"DATA_LEAVE_CHK10");
		IData groupInfo 	= gdao.qryUserGroup(subParam.getString("USER_ID").toString());

		String groupName    = groupInfo.getString("GROUP2_NAME")+"->"+groupInfo.getString("GROUP3_NAME");
		IData leverBuf 		= adao.queryUserByUserId(subParam.getString("USER_ID"));
		IData bakBuf 		= adao.queryUserByUserId(subParam.getString("BAK_USER_ID"));
		IData firChkBuf 	= adao.queryUserByUserId(subParam.getString("FIR_CHK"));

		IData group2Info 	= gdao.qryGroupInfoById(groupInfo,"GROUP2_ID");
		IDataset newPminfo = new DatasetList();
		if(group2Info.containsKey("MAN_USERLIST")&& !group2Info.get("MAN_USERLIST").equals("")){
			String[] chkUser = group2Info.get("MAN_USERLIST").toString().split(",");
			if(chkUser !=null && !"".equals(chkUser)) {
                for (int x = 0; x < chkUser.length; x++) {
                    IData newPm = new DataMap();
                    newPm.put("USER_ID", chkUser[x]);
                    newPminfo.add(newPm);
                }
            }
		}
		//获取群组上的审批人，如果是请假，非空则替换pm邮件集合，同时如果是超长请假的销假，销假也通知有权限的人
		if(newPminfo.size()>0)
			pmInfos = newPminfo;

		int leavadays = StringUtil.compDateDif(subParam.getString("BACK_DATE"),subParam.getString("OUT_DATE"))-1;
		System.out.println("###leavadays="+leavadays);
		//特殊七天的////特殊十天的
		if(leavadays >= 10){
			if(pmInfos10.size()>0)
				pmInfos = pmInfos10;
		}
		else if(leavadays >= 7){
			if(pmInfos7.size()>0)
				pmInfos = pmInfos7;
		}

		String reqTypeName = StringUtil.getCodeName(adao,"TF_F_USER_ASKLEAVE","REQ_TYPE",subParam.getString("REQ_TYPE"));

		//状态判断，旧与新对比
		String chk = adao.chkState(subParam.getInt("STATE"),params.getInt("STATE"));
		if(!chk.equals("")){
			resultData.put("RETRUN_STR", chk);
			resultData.put("DEAL_TAG", "NO");
			resultData.put("RETRUN_ERR", chk);
			return resultData;
		}
		//人员判断
		chk = adao.chkPerson(params.getString("USER_ID"),params.getInt("STATE"),subParam,pmInfos);
		if(!chk.equals("")){
			resultData.put("RETRUN_STR", chk);
			resultData.put("DEAL_TAG", "NO");
			resultData.put("RETRUN_ERR", chk);
			return resultData;
		}

		//请假基础信息，邮件使用
		StringBuffer smslBufAll =  new StringBuffer();
		smslBufAll.append("<html>                                                                                                                             ");
		smslBufAll.append(" <body>                                                                                                                         ");
		smslBufAll.append("  <div>                                                                                                                         ");
		smslBufAll.append("   <br />                                                                                                                       ");
		smslBufAll.append("  </div>                                                                                                                        ");
		smslBufAll.append("  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"665\" style=\"border-collapse: collapse;width:499pt\">        ");
		smslBufAll.append("   <!--StartFragment-->                                                                                                         ");
		smslBufAll.append("   <colgroup>                                                                                                                   ");
		smslBufAll.append("    <col width=\"124\" style=\"mso-width-source:userset;mso-width-alt:3968;width:93pt\" />                                      ");
		smslBufAll.append("    <col width=\"186\" style=\"mso-width-source:userset;mso-width-alt:5952;width:140pt\" />                                     ");
		smslBufAll.append("    <col width=\"127\" style=\"mso-width-source:userset;mso-width-alt:4064;width:95pt\" />                                      ");
		smslBufAll.append("    <col width=\"228\" style=\"mso-width-source:userset;mso-width-alt:7296;width:171pt\" />                                     ");
		smslBufAll.append("   </colgroup>                                                                                                                  ");
		smslBufAll.append("   <tbody>                                                                                                                      ");
		smslBufAll.append("    <tr height=\"42\" style=\"height:31.5pt\">                                                                                  ");
		smslBufAll.append("     <td colspan=\"4\" height=\"42\" class=\"xl126\" width=\"665\" style=\"height: 31.5pt; width: 499pt; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-weight: 700; font-family: 华文中宋; vertical-align: middle; border: 1pt solid windowtext; white-space: nowrap; text-align: center;\">请&nbsp;&nbsp;&nbsp;&nbsp; 假&nbsp;&nbsp;&nbsp;&nbsp; 单</td>       ");
		smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
		smslBufAll.append("    <tr height=\"28\" style=\"height:21.0pt\">                                                                                                                                                                                                                                    ");
		smslBufAll.append("     <td height=\"28\" class=\"xl127\" style=\"height: 21pt; border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">姓名</td>               ");
		smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+leverBuf.get("NAME")+"</td>                      ");
		smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">联系电话</td>                                       ");
		smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+leverBuf.get("PHONE")+"</td>                        ");
		smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
		smslBufAll.append("    <tr height=\"28\" style=\"height:21.0pt\">                                                                                                                                                                                                                                    ");
		smslBufAll.append("     <td height=\"28\" class=\"xl127\" style=\"height: 21pt; border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">B角联系人</td>          ");
		smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+bakBuf.get("NAME")+"</td>                        ");
		smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">B角联系电话</td>                                    ");
		smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+bakBuf.get("PHONE")+"</td>                           ");
		smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
		smslBufAll.append("    <tr height=\"28\" style=\"height:21.0pt\">                                                                                                                                                                                                                                    ");
		smslBufAll.append("     <td height=\"28\" class=\"xl127\" style=\"height: 21pt; border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">公司</td>               ");
		smslBufAll.append("     <td colspan=\"3\" class=\"xl128\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-style: italic; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">亚信</td>         ");
		smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
		smslBufAll.append("    <tr height=\"28\" style=\"height:21.0pt\">                                                                                                                                                                                                                                    ");
		smslBufAll.append("     <td height=\"28\" class=\"xl127\" style=\"height: 21pt; border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">组别</td>               ");
		smslBufAll.append("     <td colspan=\"3\" class=\"xl128\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-style: italic; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+groupName/*getContextData().getOrg()*/+"</td>         ");
		smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
		smslBufAll.append("    <tr height=\"28\" style=\"height:21.0pt\">                                                                                                                                                                                                                                    ");
		smslBufAll.append("     <td height=\"28\" class=\"xl127\" style=\"height: 21pt; border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">模块</td>               ");
		smslBufAll.append("     <td colspan=\"3\" class=\"xl128\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-style: italic; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+subParam.get("WORK_SUBGROUP")+"</td>         ");
		smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
		smslBufAll.append("    <tr height=\"28\" style=\"height:21.0pt\">                                                                                                                                                                                                                                    ");
		smslBufAll.append("     <td height=\"28\" class=\"xl127\" style=\"height: 21pt; border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">离开时间</td>           	 ");
		smslBufAll.append("     <td class=\"xl129\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+subParam.get("OUT_DATE")+"</td>                     ");
		smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">目的地</td>                                         	 ");
		smslBufAll.append("     <td class=\"xl129\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+subParam.get("OUT_PLACE")+"</td>                    ");
		smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
		smslBufAll.append("    <tr height=\"28\" style=\"height:21.0pt\">                                                                                                                                                                                                                                    ");
		smslBufAll.append("     <td height=\"28\" class=\"xl127\" style=\"height: 21pt; border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">预计返回时间</td>       	 ");
		smslBufAll.append("     <td class=\"xl129\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+subParam.get("BACK_DATE")+"</td>                    ");
		smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">申请日期</td>                                       	 ");
		smslBufAll.append("     <td class=\"xl129\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+subParam.get("REQ_TIME")+"</td>                     ");
		smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
		smslBufAll.append("    <tr height=\"28\" style=\"height:21.0pt\">                                                                                                                                                                                                                                    ");
		smslBufAll.append("     <td height=\"28\" class=\"xl127\" style=\"height: 21pt; border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">休假类型</td>           	 ");
		smslBufAll.append("     <td colspan=\"3\" class=\"xl129\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+reqTypeName+"</td>       ");
		smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
		smslBufAll.append("    <tr height=\"28\" style=\"height:21.0pt\">                                                                                                                                                                                                                                    ");
		smslBufAll.append("     <td height=\"28\" class=\"xl127\" style=\"height: 21pt; border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">休假事由</td>               ");
		smslBufAll.append("     <td colspan=\"3\" class=\"xl129\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+subParam.get("REQ_MARK")+"</td>       ");
		smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
		smslBufAll.append("    <!--EndFragment-->                                                                                                                                                                                                                                                            ");
		smslBufAll.append("   </tbody>                                                                                                                                                                                                                                                                       ");
		smslBufAll.append("  </table>                                                                                                                                                                                                                                                                        ");
		smslBufAll.append("  <br />                                                                                                                                                                                                                                                                          ");
		smslBufAll.append(" </body>                                                                                                                                                                                                                                                                          ");
		smslBufAll.append("</html>                                                                                                                                                                                                                                                                           ");

		String name = "-"+leverBuf.getString("NAME");

		if(adao.validateAudit(params)){
			//1：第一审批人审批通过 -1：第一审批人审批不通过 2：第二审批人审批通过 -2：第二审批人审批不通过 3-销假 99-撤销
			StringBuffer smslBufMail =  new StringBuffer();
			StringBuffer smslBuf1 =  new StringBuffer();
			if(99==params.getInt("STATE")){
				adao.auditLeava(params);
				//邮件通知第一审批人
				smslBufMail =  new StringBuffer();
				smslBufMail.append("<html><body>");
				smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
				smslBufMail.append("   <tbody>");
				smslBufMail.append("    <tr><td>"+firChkBuf.getString("NAME")+"，您好！</td></tr>");
				smslBufMail.append("    <tr>");
				smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;["+leverBuf.getString("NAME")+"]的请假已经取消，请您知晓。 </td>");
				smslBufMail.append("    </tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("   </tbody>");
				smslBufMail.append("</table>");
				smslBufMail.append("</body></html>");
				smslBufMail.append(smslBufAll.toString());
				listMail.add(firChkBuf.getString("EMAIL"));
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,"摆牛请假取消通知"+name,smslBufMail.toString());

				//第一审批人审批通过通知
				smslBufMail =  new StringBuffer();
				smslBufMail.append("<html><body>");
				smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
				smslBufMail.append("   <tbody>");
				smslBufMail.append("    <tr><td>"+leverBuf.getString("NAME")+"，您好！</td></tr>");
				smslBufMail.append("    <tr>");
				smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;您在"+subParam.get("REQ_TIME")+"提交的请假申请已经取消。</td>");
				smslBufMail.append("    </tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("   </tbody>");
				smslBufMail.append("</table>");
				smslBufMail.append("</body></html>");
				smslBufMail.append(smslBufAll.toString());

				listMail.add(leverBuf.getString("EMAIL"));
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,"摆牛请假取消通知"+name,smslBufMail.toString());
			}
			if(3==params.getInt("STATE")){
				adao.auditLeava(params);
				//取销假时间
				subParam = adao.queryLeaveByID(params.getString("ID"));
				int days = StringUtil.compDateDif(subParam.getString("RETURN_TIME"),subParam.getString("BACK_DATE"))-1;

				for(int i =0;i<pmInfos.size();i++){
					IData PmUserbuf = (IData)pmInfos.get(i);
					IData PmUserInfo = adao.queryUserByUserId(PmUserbuf.getString("USER_ID"));
					if(PmUserInfo!=null) {
						listMail.add(PmUserInfo.getString("EMAIL"));
					}
				}

				//邮件通知PM成员
				smslBufMail =  new StringBuffer();
				smslBufMail.append("<html><body>");
				smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
				smslBufMail.append("   <tbody>");
				smslBufMail.append("    <tr><td> 您好！</td></tr>");
				smslBufMail.append("    <tr>");
				smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;["+leverBuf.getString("NAME")+"]已经销假，请您知晓。 </td>");
				smslBufMail.append("    </tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				if(days>0)
					smslBufMail.append("    <tr><td>&nbsp;&nbsp;&nbsp;&nbsp;延迟"+days+"天</td></tr>");
				else
					smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("   </tbody>");
				smslBufMail.append("</table>");
				smslBufMail.append("</body></html>");
				smslBufMail.append(smslBufAll.toString());

				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,"摆牛请假销假通知"+name,smslBufMail.toString());

				//邮件通知第一审批人
				smslBufMail =  new StringBuffer();
				smslBufMail.append("<html><body>");
				smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
				smslBufMail.append("   <tbody>");
				smslBufMail.append("    <tr><td>"+firChkBuf.getString("NAME")+"，您好！</td></tr>");
				smslBufMail.append("    <tr>");
				smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;["+leverBuf.getString("NAME")+"]已经销假，请您知晓。 </td>");
				smslBufMail.append("    </tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				if(days>0)
					smslBufMail.append("    <tr><td>&nbsp;&nbsp;&nbsp;&nbsp;延迟"+days+"天</td></tr>");
				else
					smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("   </tbody>");
				smslBufMail.append("</table>");
				smslBufMail.append("</body></html>");
				smslBufMail.append(smslBufAll.toString());

				listMail.add(firChkBuf.getString("EMAIL"));
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,"摆牛请假销假通知"+name,smslBufMail.toString());
			}
			if(1==params.getInt("STATE")){
				if(!"".equals(params.getString("PS",""))){
					params.put("FIR_PS", params.getString("PS"));
				}
				adao.auditLeava(params);

				String strSecChk = "";

				String noticeStr = "您收到来自["+leverBuf.getString("NAME")+"]的请假申请,["+firChkBuf.getString("NAME")+"]已经一级审批通过,您是他的最终审批人,请您进行审批。";

				for(int i =0;i<pmInfos.size();i++){
					//给pm成员发请假通知
					IData PmUserbuf = (IData)pmInfos.get(i);
					String mailId = StringUtil.getSequenceNew(adao, StringUtil.StrLogIdSeq);//StringUtil.getSequence(adao, StringUtil.LogIdSeq);

					IData PmUserInfo = adao.queryUserByUserId(PmUserbuf.getString("USER_ID"));
					if(PmUserInfo==null){
						throw new Exception("数据错误，没有此pmuser="+PmUserbuf.getString("USER_ID"));
					}
					strSecChk = strSecChk + "|" + PmUserInfo.getString("NAME") + "|";
					smslBuf1 =  new StringBuffer();
					smslBuf1.append("<p>                                                                                                    ");
					smslBuf1.append("    尊敬的["+PmUserInfo.getString("NAME")+"]：                                                                                   ");
					smslBuf1.append("</p>                                                                                                   ");
					smslBuf1.append("<p>                                                                                                    ");
					smslBuf1.append("    &nbsp; &nbsp; &nbsp;"+noticeStr+"                  ");
					smslBuf1.append("</p>                                                                                                   ");
					smslBuf1.append("<p>                                                                                                    ");
					smslBuf1.append("    <br/>                                                                                              ");
					smslBuf1.append("</p>                                                                                                   ");
					smslBuf1.append("<table cellpadding='0' cellspacing='0' width='665'>                                                    ");
					smslBuf1.append("    <colgroup>                                                                                         ");
					smslBuf1.append("        <col width='124' style=';width:124px'/>                                                        ");
					smslBuf1.append("        <col width='186' style=';width:187px'/>                                                        ");
					smslBuf1.append("        <col width='127' style=';width:127px'/>                                                        ");
					smslBuf1.append("        <col width='228' style=';width:228px'/>                                                        ");
					smslBuf1.append("    </colgroup>                                                                                        ");
					smslBuf1.append("    <tbody>                                                                                            ");
					smslBuf1.append("        <tr height='33' style=';height:33px' class='firstRow'>                                         ");
					smslBuf1.append("            <td height='33' width='124'>                                                               ");
					smslBuf1.append("                姓名                                                                                   ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td width='187' style='border-left-style: none;'>                                          ");
					smslBuf1.append("                "+leverBuf.getString("NAME")+"                                                                                 ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td width='127' style='border-left-style: none;'>                                          ");
					smslBuf1.append("                联系电话                                                                               ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td width='228' style='border-left-style: none;'>                                          ");
					smslBuf1.append("                "+leverBuf.getString("PHONE")+"                                                                            ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("        </tr>                                                                                          ");
					smslBuf1.append("        <tr height='33' style=';height:33px'>                                                          ");
					smslBuf1.append("            <td height='33' style='border-top-style: none;'>                                           ");
					smslBuf1.append("                B角联系人                                                                              ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td style='border-top:none;border-left:none'>                                              ");
					smslBuf1.append("                "+bakBuf.getString("NAME")+"                                                                                 ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td style='border-top:none;border-left:none'>                                              ");
					smslBuf1.append("                B角联系电话                                                                            ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td style='border-top:none;border-left:none'>                                              ");
					smslBuf1.append("                "+bakBuf.getString("PHONE")+"                                                                            ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("        </tr>                                                                                          ");
					smslBuf1.append("        <tr height='33' style=';height:33px'>                                                          ");
					smslBuf1.append("            <td height='33' style='border-top-style: none;'>                                           ");
					smslBuf1.append("                公司                                                                                   ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td colspan='3' style='border-top:none;border-left:none;'>               ");
					smslBuf1.append("                亚信                                                                                   ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("        </tr>                                                                                          ");
					smslBuf1.append("        <tr height='33' style=';height:33px'>                                                          ");
					smslBuf1.append("            <td height='33' style='border-top-style: none;'>                                           ");
					smslBuf1.append("                部门                                                                                   ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td colspan='3' style='border-top:none;border-left:none;'>               ");
					smslBuf1.append("                "+leverBuf.getString("ORG")+"                                                                                  ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("        </tr>                                                                                          ");
					smslBuf1.append("        <tr height='33' style=';height:33px'>                                                          ");
					smslBuf1.append("            <td height='33' style='border-top-style: none;'>                                           ");
					smslBuf1.append("                模块                                                                                   ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td colspan='3' style='border-top:none;border-left:none;'>               ");
					smslBuf1.append("                "+subParam.get("WORK_SUBGROUP")+"                                                                                   ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("        </tr>                                                                                          ");
					smslBuf1.append("        <tr height='33' style=';height:33px'>                                                          ");
					smslBuf1.append("            <td height='33' style='border-top-style: none;'>                                           ");
					smslBuf1.append("                离开时间                                                                               ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td style='border-top:none;border-left:none'>                                              ");
					smslBuf1.append("                "+subParam.get("OUT_DATE")+"                                                                             ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td style='border-top:none;border-left:none'>                                              ");
					smslBuf1.append("                目的地                                                                                 ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td style='border-top:none;border-left:none'>                                              ");
					smslBuf1.append("                "+subParam.get("OUT_PLACE")+"                                                                                   ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("        </tr>                                                                                          ");
					smslBuf1.append("        <tr height='33' style=';height:33px'>                                                          ");
					smslBuf1.append("            <td height='33' style='border-top-style: none;'>                                           ");
					smslBuf1.append("                预计返回时间                                                                           ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td style='border-top:none;border-left:none'>                                              ");
					smslBuf1.append("                "+subParam.get("BACK_DATE")+"                                                                             ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td style='border-top:none;border-left:none'>                                              ");
					smslBuf1.append("                申请日期                                                                               ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td style='border-top:none;border-left:none'>                                              ");
					smslBuf1.append("                "+subParam.get("REQ_TIME")+"                                                                             ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("        </tr>                                                                                          ");
					smslBuf1.append("        <tr height='27' style='height:27px'>                                                           ");
					smslBuf1.append("            <td height='27' style='border-top-style: none;'>                                           ");
					smslBuf1.append("                休假类型                                                                               ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td colspan='3' height='27' width='541' style='border-top:none;border-left:none;' valign='top'>   ");
					smslBuf1.append("                "+reqTypeName+"                                                                                   ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("        </tr>                                                                                          ");
					smslBuf1.append("        <tr height='28' style='height:28px'>                                                           ");
					smslBuf1.append("            <td height='28' style='border-top-style: none;'>                                           ");
					smslBuf1.append("                休假事由                                                                               ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("            <td colspan='3' style='border-top:none;border-left:none;'>               ");
					smslBuf1.append("                "+subParam.get("REQ_MARK")+"                                                                 ");
					smslBuf1.append("            </td>                                                                                      ");
					smslBuf1.append("        </tr>                                                                                          ");
					smslBuf1.append("    </tbody>                                                                                           ");
					smslBuf1.append("</table>                                                                                               ");
					smslBuf1.append("<p></p>                                                                                                ");
					smslBuf1.append("<p>                                                                                                    ");
					smslBuf1.append("    <br/>                                                                                              ");
					smslBuf1.append("</p>                                                                                                   ");
					smslBuf1.append("<p>                                                                                                    ");
					smslBuf1.append("    <textarea placeholder='您的意见' id='you_idle' class='form-control'></textarea><span class='bn_btn ipt_btn' id='agreeSub' state='2' leaveId='"+subParam.getString("ID")+"' mailId='"+mailId+"'>同意休假</span><span class='bn_btn ipt_btn' id='disAgreeSub' state='-2' leaveId='"+subParam.getString("ID")+"' mailId='"+mailId+"'>不同意休假</span><br/>                                                                                              ");
					smslBuf1.append("</p>                                                                                                   ");
					sendSMStoSomeone(smslBuf1.toString(),PmUserInfo.getString("USER_ID"),mailId,BNSMS.SMS_LEAVE_ADMINNOTICE_MOD);

					//邮件通知
					smslBufMail =  new StringBuffer();
					smslBufMail.append("<html><body>");
					smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
					smslBufMail.append("   <tbody>");
					smslBufMail.append("    <tr><td>"+PmUserInfo.get("NAME")+"，您好！</td></tr>");
					smslBufMail.append("    <tr>");
					smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;"+noticeStr+" </td>");
					smslBufMail.append("    </tr>");
					smslBufMail.append("    <tr><td>&nbsp;&nbsp;&nbsp;&nbsp;审批链接：<a   href=\""+StringUtil.domain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"
							//ID|SMS_ID|STATE
							+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#2#"+PmUserInfo.getString("USER_ID"), "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"green\"><b>通过</b></font></a>");
					smslBufMail.append("  &nbsp;&nbsp;<a href=\""+StringUtil.domain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"
							//ID|SMS_ID|STATE
							+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#-2#"+PmUserInfo.getString("USER_ID"), "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"red\"><b>不通过</b></font></a></td></tr>");			
					/*smslBufMail.append("    <tr><td>&nbsp;&nbsp;&nbsp;&nbsp;内网审批链接：<a   href=\""+StringUtil.innerdomain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"
							+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#2#"+PmUserInfo.getString("USER_ID"), "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"green\"><b>通过</b></font></a>");
					smslBufMail.append("  &nbsp;&nbsp;<a href=\""+StringUtil.innerdomain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"
							+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#-2#"+PmUserInfo.getString("USER_ID"), "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"red\"><b>不通过</b></font></a></td></tr>");*/
					smslBufMail.append("    <tr><td></td></tr>");
					smslBufMail.append("    <tr><td></td></tr>");
					smslBufMail.append("    <tr><td></td></tr>");
					smslBufMail.append("   </tbody>");
					smslBufMail.append("</table>");
					smslBufMail.append("</body></html>");
					smslBufMail.append(smslBufAll.toString());

					listMail.add(PmUserInfo.getString("EMAIL"));
					sendMail(listMail,listMailCc,listMailBcc,listMailAtta,"摆牛请假审批通知"+name,smslBufMail.toString());
				}

				//第一审批人审批通过通知
				smslBufMail =  new StringBuffer();
				smslBufMail.append("<html><body>");
				smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
				smslBufMail.append("   <tbody>");
				smslBufMail.append("    <tr><td>"+leverBuf.getString("NAME")+"，您好！</td></tr>");
				smslBufMail.append("    <tr>");
				smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;"+firChkBuf.getString("NAME")+"批准您的请假申请。</td>");
				smslBufMail.append("    </tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td>当前审批人："+strSecChk+" 联系方式()，具体请查看请假申请</td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("   </tbody>");
				smslBufMail.append("</table>");
				smslBufMail.append("</body></html>");
				smslBufMail.append(smslBufAll.toString());
				listMail.add(leverBuf.getString("EMAIL"));
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,"摆牛请假审批结果通知"+name,smslBufMail.toString());
			}
			if(params.getInt("STATE")==-1){
				if(!"".equals(params.getString("PS",""))){
					params.put("FIR_PS", params.getString("PS"));
				}
				adao.auditLeava(params);
				smslBuf1 =  new StringBuffer();
				smslBuf1.append("<p>                 ");
				smslBuf1.append("    &nbsp;"+leverBuf.getString("NAME")+"，您好：");
				smslBuf1.append("</p>                ");
				smslBuf1.append("<p>                 ");
				smslBuf1.append("&nbsp; &nbsp; &nbsp; 您在"+subParam.get("REQ_TIME")+"提交的请假申请未通过，原因:"+subParam.get("FIR_PS")+"，具体请查看请假申请！       ");
				smslBuf1.append("</p>                ");
				sendSMStoSomeone(smslBuf1.toString(),leverBuf.getString("USER_ID"),BNSMS.SMS_LEAVE_MYNOTICE_MOD);

				//邮件通知
				smslBufMail =  new StringBuffer();
				smslBufMail.append("<html><body>");
				smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
				smslBufMail.append("   <tbody>");
				smslBufMail.append("    <tr><td>"+leverBuf.getString("NAME")+"，您好！</td></tr>");
				smslBufMail.append("    <tr>");
				smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;您于"+subParam.get("REQ_TIME")+"提交的请假申请未通过，原因:"+subParam.get("FIR_PS")+"，具体请查看请假申请！</td>");
				smslBufMail.append("    </tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("   </tbody>");
				smslBufMail.append("</table>");
				smslBufMail.append("</body></html>");
				smslBufMail.append(smslBufAll.toString());

				listMail.add(leverBuf.getString("EMAIL"));
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,"摆牛请假审批结果通知"+name,smslBufMail.toString());
			}
			if(params.getInt("STATE")==2){
				if(!"".equals(params.getString("PS",""))){
					params.put("SEC_PS", params.getString("PS"));
				}
				//getContextData().getUserID()
				params.put("SEC_CHK",params.getString("USER_ID"));
				adao.auditLeava(params);
				smslBuf1 =  new StringBuffer();
				smslBuf1.append("<p>                        ");
				smslBuf1.append("    &nbsp;"+leverBuf.getString("NAME")+"，您好： ");
				smslBuf1.append("</p>                       ");
				smslBuf1.append("<p>                        ");
				smslBuf1.append("&nbsp; &nbsp; &nbsp; 您在"+subParam.get("REQ_TIME")+"提交的请假申请已经审批通过，具体请查看请假申请！");
				smslBuf1.append("</p>                       ");
				sendSMStoSomeone(smslBuf1.toString(),leverBuf.getString("USER_ID"),BNSMS.SMS_LEAVE_MYNOTICE_MOD);

				//邮件通知
				smslBufMail =  new StringBuffer();
				smslBufMail.append("<html><body>");
				smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
				smslBufMail.append("   <tbody>");
				smslBufMail.append("    <tr><td>"+leverBuf.getString("NAME")+"，您好！</td></tr>");
				smslBufMail.append("    <tr>");
				smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;您于"+subParam.get("REQ_TIME")+"提交的请假申请已经审批通过，具体请查看请假申请！</td>");
				smslBufMail.append("    </tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("   </tbody>");
				smslBufMail.append("</table>");
				smslBufMail.append("</body></html>");
				smslBufMail.append(smslBufAll.toString());

				listMail.add(leverBuf.getString("EMAIL"));
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,"摆牛请假审批结果通知"+name,smslBufMail.toString());
			}
			if(params.getInt("STATE")==-2){
				if(!"".equals(params.getString("PS",""))){
					params.put("SEC_PS", params.getString("PS"));
				}
				//getContextData().getUserID()
				params.put("SEC_CHK",params.getString("USER_ID"));
				adao.auditLeava(params);
				smslBuf1 =  new StringBuffer();
				smslBuf1.append("<p>                        ");
				smslBuf1.append("    &nbsp;"+leverBuf.getString("NAME")+"，您好： ");
				smslBuf1.append("</p>                       ");
				smslBuf1.append("<p>                        ");
				smslBuf1.append("&nbsp; &nbsp; &nbsp; 您在"+subParam.get("REQ_TIME")+"提交的请假申请未通过审批，原因:"+subParam.get("SEC_PS")+"，具体请查看请假申请！       ");
				smslBuf1.append("</p>                       ");
				sendSMStoSomeone(smslBuf1.toString(),leverBuf.getString("USER_ID"),BNSMS.SMS_LEAVE_MYNOTICE_MOD);

				//邮件通知
				smslBufMail =  new StringBuffer();
				smslBufMail.append("<html><body>");
				smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
				smslBufMail.append("   <tbody>");
				smslBufMail.append("    <tr><td>"+leverBuf.getString("NAME")+"，您好！</td></tr>");
				smslBufMail.append("    <tr>");
				smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;您于"+subParam.get("REQ_TIME")+"提交的请假申请未通过审批，原因:"+subParam.get("SEC_PS")+"，具体请查看请假申请！</td>");
				smslBufMail.append("    </tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("   </tbody>");
				smslBufMail.append("</table>");
				smslBufMail.append("</body></html>");
				smslBufMail.append(smslBufAll.toString());

				listMail.add(leverBuf.getString("EMAIL"));
				sendMail(listMail,listMailCc,listMailBcc,listMailAtta,"摆牛请假审批结果通知"+name,smslBufMail.toString());
			}
			resultData.put("RETRUN_STR", BNSMS.SMS_LEAVE_CHKYES);
			resultData.put("DEAL_TAG", "YES");
		}else{
			resultData.put("RETRUN_STR", BNSMS.SMS_LEAVE_CHKNO);
			resultData.put("DEAL_TAG", "NO");
			resultData.put("RETRUN_ERR", BNSMS.SMS_LEAVE_CHKNO);
		}

	   return  resultData;
	}

	public IData queryNoReadMsg(IData params) throws Exception
	{
		IData resultData = getResultData();
		try
		{
			params.put("USER_ID", getContextData().getUserID());
			MsgDao dao =new MsgDao("bainiu");
			int noRead=dao.queryNoReadMsg(params);
		    resultData.put("noRead", noRead);

		    if(noRead!=0)//如果有未读消息就显示未读消息
		    {
		    	 resultData.put("showNoRead", "1");
		    }
		}
		catch (Exception e)
		{
			 resultData.put("noRead", "0");
		}
	    return  resultData;
	}

	public IData signMsg(IData params) throws Exception
	{
		IData resultData = getResultData();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MsgDao dao =new MsgDao("bainiu");
		 for (String key:params.keySet())
		    {

		    	if( params.get(key) instanceof IData)
		    	{
		    		IData signParam=(IData) params.get(key);
		    		signParam.put("UPD_TIME", df.format(new Date()));

		    		if("0".equals(signParam.get("READ_FALG")))
		    		{
		    			signParam.put("SMS_STATE", "0");//标记为已读或未读 改变状态
		    		}
		    		else if("1".equals(signParam.get("READ_FALG")))
		    		{
		    			signParam.put("SMS_STATE", "1");//标记为已读或未读 改变状态
		    		}
		    		else
		    		{
		    			signParam.put("SMS_STATE", "2");//标记为已读或未读 改变状态
		    		}

	    			if(!("-1".equals(signParam.get("USER_ID"))))//不是全局消息
	    			{
	    				resultData=dao.signMsg(signParam, resultData);
	    			}
	    			else//如果是全局消息,则新增一条消息状态为删除
	    			{
	    				signParam.remove("USER_ID");//先移除全局消息ID
	    				signParam.put("USER_ID", getContextData().getUserID());
	    				resultData=dao.signMsg2PubMsg(signParam, resultData);
		    		}
		    	}
			}
		 doActionLog(params);
	     return  resultData;
	}

	public IData queryDetailMsg(IData param) throws Exception
	{
		IData resultData = getResultData();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MsgDao dao =new MsgDao("bainiu");
    	if( param instanceof IData)
    	{
    		if(!("1".equals(param.get("SMS_STATE"))))
    		{
    			param.put("UPD_TIME", df.format(new Date()));
    			param.put("SMS_STATE", "1");//都标记为已读状态
	    		if(!("-1".equals(param.get("USER_ID"))))//不是全局消息
	    		{
	    			dao.signMsg(param, resultData);
	    		}
	    		else//如果是全局消息,则新增一条消息状态为删除
	    		{
	    		   param.remove("USER_ID");//先移除全局消息ID
	    		   param.put("USER_ID", getContextData().getUserID());
	    		   dao.signMsg2PubMsg(param, resultData);
	    		}
    		}
    	}
	   return  dao.queryDetailMsg(param, resultData);
	}
}
