package com.ipu.server.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.GroupDao;
import com.ipu.server.dao.MsgDao;
import com.ipu.server.dao.RightDao;
import com.ipu.server.dao.VacationDao;
import com.ipu.server.util.ExportFile;
import com.ipu.server.util.StringUtil;
import com.ipu.server.util.BNSMS;
 
public class VacationBean extends AppBean 
{
	public IData init(IData param) throws Exception
	{
		IData resultData = getResultData();
		resultData.putAll(param);
		
		VacationDao vacationDao = new VacationDao("bainiu");
		//返回请假类型和状态框
		vacationDao.querySelectType(param,resultData);
		
		return resultData;
	}
	
	public IData submitLeave(IData param) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		IData resultData = getResultData();
		VacationDao vacationDao = new VacationDao("bainiu");
		IData BAKNT=vacationDao.queryUserByNT(param.getString("BAK_NT"));
		
		GroupDao gdao = new GroupDao("bainiu");
		IData groupInfo 	= gdao.qryUserGroup(getContextData().getUserID().toString());
		String groupName    = groupInfo.getString("GROUP2_NAME")+"->"+groupInfo.getString("GROUP3_NAME");
		
		if(BAKNT==null|| BAKNT.isEmpty())
		{
			resultData.put("result", "交接人NT帐号不存在");
			return resultData;
		}
		if(BAKNT.getString("USER_ID").equals(getContextData().getUserID()))
		{
			resultData.put("result", "交接人不能是自己");
			return resultData;
		}
		IData FIRSTCHKTNT=vacationDao.queryUserByNT(param.getString("FIR_CHK_NT"));
		if(FIRSTCHKTNT==null || FIRSTCHKTNT.isEmpty())
		{
			resultData.put("result", "所属组长NT帐号不存在");
			return resultData;
		}
		if(FIRSTCHKTNT.getString("USER_ID").equals(getContextData().getUserID()))
		{
			resultData.put("result", "所属组长为一级审批人，不能是自己");
			return resultData;
		}
		
		//获取自己的一级归属
		String UserGroupID = vacationDao.queryGroupID(getContextData().getUserID());
		//获取B角一级归属
		String BGroupID = vacationDao.queryGroupID(BAKNT.getString("USER_ID"));
		//获取组长一级归属
		String FirGroupID = vacationDao.queryGroupID(FIRSTCHKTNT.getString("USER_ID"));
		if(!UserGroupID.equals(BGroupID)){
			resultData.put("result", "请假人和交接人不在一个项目组!!!");
			return resultData;
		}
		if(!UserGroupID.equals(FirGroupID)){
			resultData.put("result", "请假人和所属组长不在一个项目组!!!");
			return resultData;
		}
		
		String[] leaveDays=param.getString("leaveDays").split("F");
		
	     Date outDate = df.parse(leaveDays[0]);
	     Date backDate = df.parse(leaveDays[1]);
		
		//
		IData BakNtList = vacationDao.queryBakNtList(BAKNT.getString("USER_ID"), getContextData().getUserID(),df1.format(outDate),df1.format(backDate));
		if(BakNtList != null){
			if(BakNtList.getString("BAK_USER_ID").equals(getContextData().getUserID())){
				IData USER_BAKNT=vacationDao.queryUserByUserId(BakNtList.getString("USER_ID"));
				resultData.put("result", "您已经是"+USER_BAKNT.getString("NAME")+"请假中的B角，时间交叉范围内不能请假");
				return resultData;
			}
			if(BakNtList.getString("BAK_USER_ID").equals(BAKNT.getString("USER_ID"))){
				IData USER_BAKNT=vacationDao.queryUserByUserId(BakNtList.getString("BAK_USER_ID"));
				resultData.put("result", BAKNT.get("NAME")+"已经是"+USER_BAKNT.getString("NAME")+"请假中的B角，时间交叉范围内不能再次被设为B角");
				return resultData;
			}
		}
		
		IData subParam = new DataMap();
		//subParam.put("ID", StringUtil.getSequence(vacationDao, StringUtil.LogIdSeq));
		subParam.put("ID", StringUtil.getSequenceNew(vacationDao, StringUtil.StrLogIdSeq));
		subParam.put("USER_ID", getContextData().getUserID());
		subParam.put("BAK_USER_ID", BAKNT.getString("USER_ID"));
		subParam.put("FIR_CHK", FIRSTCHKTNT.getString("USER_ID"));
		subParam.put("WORK_GROUP", param.getString("WORK_GROUP"));
		subParam.put("WORK_SUBGROUP", param.getString("WORK_SUBGROUP"));
		subParam.put("REQ_TIME", df3.format(new Date()));
		subParam.put("OUT_PLACE", param.getString("OUT_PLACE"));
		
		String reqTypeName = StringUtil.getCodeName(vacationDao,"TF_F_USER_ASKLEAVE","REQ_TYPE",param.getString("REQ_TYPE"));
		subParam.put("REQ_TYPE", param.getString("REQ_TYPE"));
		
		subParam.put("REQ_MARK", param.getString("REQ_MARK"));
		subParam.put("STATE", "0");
		
 	     subParam.put("LEAVE_DAYS", (backDate.getTime()-outDate.getTime())/(1000 * 60 * 60 * 24)+1);
 		 subParam.put("OUT_DATE", df1.format(outDate));
 		 subParam.put("BACK_DATE", df1.format(backDate));
		
		 
		boolean isSuccess = vacationDao.submitLeave(subParam);
		if (isSuccess) {

			MsgDao msgDao = new MsgDao("bainiu");
			
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
			smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+getContextData().getName()+"</td>                 ");
			smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">联系电话</td>                                       ");
			smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+getContextData().getPhone()+"</td>                 ");
			smslBufAll.append("    </tr>                                                                                                                                                                                                                                                                         ");
			smslBufAll.append("    <tr height=\"28\" style=\"height:21.0pt\">                                                                                                                                                                                                                                    ");
			smslBufAll.append("     <td height=\"28\" class=\"xl127\" style=\"height: 21pt; border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">B角联系人</td>          ");
			smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+BAKNT.get("NAME")+"</td>                        ");
			smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">B角联系电话</td>                                    ");
			smslBufAll.append("     <td class=\"xl127\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+BAKNT.get("PHONE")+"</td>                           ");
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
			
			
//			IData msgParam = new DataMap();
//			msgParam.put("SMS_ID", StringUtil.getSequenceNew(msgDao, StringUtil.StrLogIdSeq));
//			msgParam.put("USER_ID", getContextData().getUserID());
//			msgParam.put("SMS_TITLE", "您有一份新的请假待审批中...");
//			msgParam.put("SMS_ABSTRACT", "您有一份新的请假待审批中...");
//			msgParam.put("SMS_CONTENT", "您在"+subParam.get("REQ_TIME")+"提交了一份请假申请，正等待"+FIRSTCHKTNT.get("NAME")+"审批中，具体请查看请假申请！");
// 			msgDao.leave2Msg(msgParam);
//			
//			msgParam.put("SMS_ID", StringUtil.getSequenceNew(msgDao, StringUtil.StrLogIdSeq));
//			msgParam.put("USER_ID", FIRSTCHKTNT.getString("USER_ID"));
//			msgParam.put("SMS_TITLE", "您有一份新的请假需要审批...");
//			msgParam.put("SMS_ABSTRACT", "您有一份新的请假需要审批...");
//			msgParam.put("SMS_CONTENT", getContextData().getName()+"在"+subParam.get("REQ_TIME")+"提交了一份请假申请需要您审批，具体请查看请假审核！");
//		 
//			msgDao.leave2Msg(msgParam);
			StringBuffer smslBuf =  new StringBuffer();
			smslBuf.append("<p>                                                                                                     ");
			smslBuf.append("    &nbsp; ["+getContextData().getName()+"]，您好：                                                                        ");
			smslBuf.append("</p>                                                                                                    ");
			smslBuf.append("<p>                                                                                                     ");
			smslBuf.append("&nbsp; &nbsp; &nbsp; 您在"+subParam.get("REQ_TIME")+"提交了一份请假申请，正等待["+FIRSTCHKTNT.get("NAME")+"]审批中，具体请查看请假申请！       ");
			smslBuf.append("</p>                                                                                                    ");
			sendSMS(smslBuf.toString(),BNSMS.SMS_LEAVE_MYNOTICE_MOD);
			
			smslBuf =  new StringBuffer();
			smslBuf.append("<html><body>");
			smslBuf.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
			smslBuf.append("   <tbody>");
			smslBuf.append("    <tr><td>"+getContextData().getName()+"，您好！</td></tr>");
			smslBuf.append("    <tr>");
			smslBuf.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;您于"+subParam.get("REQ_TIME")+"成功提交了一份请假申请。</td>");
			smslBuf.append("    </tr>");
			smslBuf.append("    <tr><td></td></tr>");
			smslBuf.append("    <tr><td>当前审批人："+FIRSTCHKTNT.get("NAME")+"，具体请查看请假申请</td></tr>");
			smslBuf.append("    <tr><td></td></tr>");
			smslBuf.append("    <tr><td></td></tr>");
			smslBuf.append("   </tbody>");
			smslBuf.append("</table>");
			smslBuf.append("</body></html>");
			smslBuf.append(smslBufAll.toString());
			
			
			//String mailId = StringUtil.getSequence(msgDao, StringUtil.LogIdSeq);
			String mailId = StringUtil.getSequenceNew(msgDao, StringUtil.StrLogIdSeq);
			StringBuffer smslBuf1 =  new StringBuffer();
			StringBuffer smslBuf2 =  new StringBuffer();
			smslBuf1.append("<html><body>");
			smslBuf1.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
			smslBuf1.append("   <tbody>");
			smslBuf1.append("    <tr><td>"+FIRSTCHKTNT.get("NAME")+"，您好！</td></tr>");
			smslBuf1.append("    <tr>");
			smslBuf1.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;您收到来自["+getContextData().getName()+"]的请假申请，请您尽快进行审批。</td>");
			smslBuf1.append("    </tr>");
			smslBuf1.append("    <tr><td>&nbsp;&nbsp;&nbsp;&nbsp;审批链接：<a   href=\""+StringUtil.domain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"
					//ID|SMS_ID|STATE
					+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#1#"+FIRSTCHKTNT.getString("USER_ID"), "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"green\"><b>通过</b></font></a>");
			smslBuf1.append("  &nbsp;&nbsp;<a href=\""+StringUtil.domain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"
					//ID|SMS_ID|STATE
					+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#-1#"+FIRSTCHKTNT.getString("USER_ID"), "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"red\"><b>不通过</b></font></a></td></tr>");
			/*smslBuf1.append("    <tr><td>&nbsp;&nbsp;&nbsp;&nbsp;内网审批链接：<a   href=\""+StringUtil.innerdomain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"
					+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#1#"+FIRSTCHKTNT.getString("USER_ID"), "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"green\"><b>通过</b></font></a>");
			smslBuf1.append("  &nbsp;&nbsp;<a href=\""+StringUtil.innerdomain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"
					+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#-1#"+FIRSTCHKTNT.getString("USER_ID"), "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"red\"><b>不通过</b></font></a></td></tr>");*/
			smslBuf1.append("    <tr><td></td></tr>");
			smslBuf1.append("    <tr><td></td></tr>");
			smslBuf1.append("    <tr><td></td></tr>");
			smslBuf1.append("   </tbody>");
			smslBuf1.append("</table>");
			smslBuf1.append("</body></html>");
			smslBuf1.append(smslBufAll.toString());
			
			smslBuf2.append("<html><body>");
			smslBuf2.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
			smslBuf2.append("   <tbody>");
			smslBuf2.append("    <tr><td>"+BAKNT.get("NAME")+"，您好！</td></tr>");
			smslBuf2.append("    <tr>");
			smslBuf2.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;["+getContextData().getName()+"]的请假申请将您置为B角，请务必知晓，其请假期间相关工作问题将由您处理。</td>");
			smslBuf2.append("    </tr>");
			smslBuf2.append("    <tr><td></td></tr>");
			smslBuf2.append("    <tr><td></td></tr>");
			smslBuf2.append("    <tr><td></td></tr>");
			smslBuf2.append("    <tr><td></td></tr>");
			smslBuf2.append("   </tbody>");
			smslBuf2.append("</table>");
			smslBuf2.append("</body></html>");
			smslBuf2.append(smslBufAll.toString());
			
			StringBuffer smslBufOnline =  new StringBuffer();
			smslBufOnline.append("<p>                                                                                                    ");      
			smslBufOnline.append("    尊敬的["+FIRSTCHKTNT.get("NAME")+"]：                                                                                   "); 
			smslBufOnline.append("</p>                                                                                                   "); 
			smslBufOnline.append("<p>                                                                                                    "); 
			smslBufOnline.append("    &nbsp; &nbsp; &nbsp;您收到来自["+getContextData().getName()+"]的请假申请，请您尽快进行审批。                  "); 
			smslBufOnline.append("</p>                                                                                                   "); 
			smslBufOnline.append("<p>                                                                                                    "); 
			smslBufOnline.append("    <br/>                                                                                              "); 
			smslBufOnline.append("</p>                                                                                                   "); 
			smslBufOnline.append("<table cellpadding='0' cellspacing='0' width='665'>                                                    "); 
			smslBufOnline.append("    <colgroup>                                                                                         "); 
			smslBufOnline.append("        <col width='124' style=';width:124px'/>                                                        "); 
			smslBufOnline.append("        <col width='186' style=';width:187px'/>                                                        "); 
			smslBufOnline.append("        <col width='127' style=';width:127px'/>                                                        "); 
			smslBufOnline.append("        <col width='228' style=';width:228px'/>                                                        "); 
			smslBufOnline.append("    </colgroup>                                                                                        "); 
			smslBufOnline.append("    <tbody>                                                                                            ");
			smslBufOnline.append("        <tr height='33' style=';height:33px' class='firstRow'>                                         "); 
			smslBufOnline.append("            <td height='33' width='124'>                                                               "); 
			smslBufOnline.append("                姓名                                                                                   "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td width='187' style='border-left-style: none;'>                                          "); 
			smslBufOnline.append("                "+getContextData().getName()+"                                                                                 "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td width='127' style='border-left-style: none;'>                                          "); 
			smslBufOnline.append("                联系电话                                                                               "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td width='228' style='border-left-style: none;'>                                          "); 
			smslBufOnline.append("                "+getContextData().getPhone()+"                                                                            "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("        </tr>                                                                                          "); 
			smslBufOnline.append("        <tr height='33' style=';height:33px'>                                                          "); 
			smslBufOnline.append("            <td height='33' style='border-top-style: none;'>                                           "); 
			smslBufOnline.append("                B角联系人                                                                              "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td style='border-top:none;border-left:none'>                                              "); 
			smslBufOnline.append("                "+BAKNT.get("NAME")+"                                                                                 "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td style='border-top:none;border-left:none'>                                              "); 
			smslBufOnline.append("                B角联系电话                                                                            "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td style='border-top:none;border-left:none'>                                              "); 
			smslBufOnline.append("                "+BAKNT.getString("PHONE")+"                                                                            "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("        </tr>                                                                                          "); 
			smslBufOnline.append("        <tr height='33' style=';height:33px'>                                                          "); 
			smslBufOnline.append("            <td height='33' style='border-top-style: none;'>                                           "); 
			smslBufOnline.append("                公司                                                                                   "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td colspan='3' style='border-top:none;border-left:none;'>               "); 
			smslBufOnline.append("                亚信                                                                                   "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("        </tr>                                                                                          "); 
			smslBufOnline.append("        <tr height='33' style=';height:33px'>                                                          "); 
			smslBufOnline.append("            <td height='33' style='border-top-style: none;'>                                           "); 
			smslBufOnline.append("                部门                                                                                   "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td colspan='3' style='border-top:none;border-left:none;'>               "); 
			smslBufOnline.append("                "+getContextData().getOrg()+"                                                                                  "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("        </tr>                                                                                          "); 
			smslBufOnline.append("        <tr height='33' style=';height:33px'>                                                          "); 
			smslBufOnline.append("            <td height='33' style='border-top-style: none;'>                                           "); 
			smslBufOnline.append("                模块                                                                                   "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td colspan='3' style='border-top:none;border-left:none;'>               "); 
			smslBufOnline.append("                "+subParam.get("WORK_SUBGROUP")+"                                                                                   "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("        </tr>                                                                                          "); 
			smslBufOnline.append("        <tr height='33' style=';height:33px'>                                                          "); 
			smslBufOnline.append("            <td height='33' style='border-top-style: none;'>                                           "); 
			smslBufOnline.append("                离开时间                                                                               "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td style='border-top:none;border-left:none'>                                              "); 
			smslBufOnline.append("                "+subParam.get("OUT_DATE")+"                                                                             "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td style='border-top:none;border-left:none'>                                              "); 
			smslBufOnline.append("                目的地                                                                                 "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td style='border-top:none;border-left:none'>                                              "); 
			smslBufOnline.append("                "+subParam.get("OUT_PLACE")+"                                                                                   "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("        </tr>                                                                                          "); 
			smslBufOnline.append("        <tr height='33' style=';height:33px'>                                                          "); 
			smslBufOnline.append("            <td height='33' style='border-top-style: none;'>                                           "); 
			smslBufOnline.append("                预计返回时间                                                                           "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td style='border-top:none;border-left:none'>                                              "); 
			smslBufOnline.append("                "+subParam.get("BACK_DATE")+"                                                                             "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td style='border-top:none;border-left:none'>                                              "); 
			smslBufOnline.append("                申请日期                                                                               "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td style='border-top:none;border-left:none'>                                              "); 
			smslBufOnline.append("                "+subParam.get("REQ_TIME")+"                                                                             "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("        </tr>                                                                                          "); 
			smslBufOnline.append("        <tr height='27' style='height:27px'>                                                           "); 
			smslBufOnline.append("            <td height='27' style='border-top-style: none;'>                                           "); 
			smslBufOnline.append("                休假类型                                                                               "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td colspan='3' height='27' width='541' style='border-top:none;border-left:none;' valign='top'>   "); 
			smslBufOnline.append("                "+reqTypeName+"                                                                                   "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("        </tr>                                                                                          "); 
			smslBufOnline.append("        <tr height='28' style='height:28px'>                                                           "); 
			smslBufOnline.append("            <td height='28' style='border-top-style: none;'>                                           "); 
			smslBufOnline.append("                休假事由                                                                               "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("            <td colspan='3' style='border-top:none;border-left:none;'>               "); 
			smslBufOnline.append("                "+subParam.get("REQ_MARK")+"                                                                 "); 
			smslBufOnline.append("            </td>                                                                                      "); 
			smslBufOnline.append("        </tr>                                                                                          "); 
			smslBufOnline.append("    </tbody>                                                                                           "); 
			smslBufOnline.append("</table>                                                                                               "); 
			smslBufOnline.append("<p></p>                                                                                                "); 
			smslBufOnline.append("<p>                                                                                                    ");
			smslBufOnline.append("    <br/>                                                                                              "); 
			smslBufOnline.append("</p>                                                                                                   "); 
			smslBufOnline.append("<p>                                                                                                    "); 
			smslBufOnline.append("    <textarea placeholder='您的意见' id='you_idle' class='form-control'></textarea><span class='bn_btn ipt_btn' id='agreeSub' state='1' leaveId='"+subParam.getString("ID")+"' mailId='"+mailId+"'>同意休假</span><span class='bn_btn ipt_btn' id='disAgreeSub' state='-1' leaveId='"+subParam.getString("ID")+"' mailId='"+mailId+"'>不同意休假</span><br/>                                                                                              "); 
			smslBufOnline.append("</p>                                                                                                   ");
			
			sendSMStoSomeone(smslBufOnline.toString(),FIRSTCHKTNT.getString("USER_ID"),mailId,BNSMS.SMS_LEAVE_ADMINNOTICE_MOD);
			
			resultData.put("result", "0");
			doActionLog(param);	
			
			String name = "-"+getContextData().getName().toString();
			//发送通知邮件
			List<String> listMail = new ArrayList<String>();	
			listMail.add(getContextData().getEmail());
			sendMailSimple(listMail,"摆牛请假申请通知"+name,smslBuf.toString());
			
			listMail.add(BAKNT.get("EMAIL").toString());
			sendMailSimple(listMail,"摆牛请假B角通知"+name,smslBuf2.toString());
			
			listMail.add(FIRSTCHKTNT.get("EMAIL").toString());
			sendMailSimple(listMail,"摆牛请假审批通知"+name,smslBuf1.toString());
			
			return resultData;
		} else {
			resultData.put("result", "休假提交失败!!!");
			return resultData;
		}
	}

	public IData queryOwnLeave(IData param) throws Exception
	{
		IData resultData = getResultData();
		//param.put("USER_ID", getContextData().getUserID());//查所有人
		VacationDao vacationDao = new VacationDao("bainiu");
		param.put("USERIDS", getContextData().getUserID());
		resultData=vacationDao.queryOwnLeave(param, resultData, "LEAVELIST");
		resultData.put("NT_NAME", param.getString("NT_NAME"));
		resultData.put("USER_ID", getContextData().getUserID());
		
		RightDao rightDao = new RightDao("bainiu");
		if(rightDao.queryUserRight(getContextData().getUserID(),"DATA_LEAVE_EXPORT")){
			resultData.put("FLAT","1");
		}
		
		resultData.put("USER_ID", getContextData().getUserID());
		
		//返回请假类型和状态框
		vacationDao.querySelectType(param,resultData);
		
		doActionLog(param);
		return resultData;
	}
	
	public void exportAskleave(IData param,HttpServletResponse response)throws Exception
	{
		VacationDao vacationDao = new VacationDao("bainiu");
		IDataset resultData=vacationDao.exportOwnLeave(param);
		IDataset tabset = vacationDao.queryExportTab("userAskleave");
		ExportFile file = new ExportFile();
		file.exportAskleave(response,"员工请假.xls","员工请假明细",resultData,tabset);
	}
	
	public IData queryResultData(IData param)throws Exception
	{
		return getResultData();
	}
}
