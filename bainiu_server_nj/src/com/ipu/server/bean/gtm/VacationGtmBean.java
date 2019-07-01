package com.ipu.server.bean.gtm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.GroupDao;
import com.ipu.server.dao.MsgDao;
import com.ipu.server.dao.VacationDao;
import com.ipu.server.dao.gtm.VacationGtmDao;
import com.ipu.server.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.mail.EmailAttachment;

public class VacationGtmBean extends AppBean {
	private static int counter = 0;
	protected void execute()  {
		long ms = System.currentTimeMillis();
		System.out.println("###\t\t" + new Date(ms)+"(" + counter++ + ")");		
		
		try {
			MsgDao msgDao = new MsgDao("bainiu");
			chkState("0",msgDao);
			chkState("1",msgDao);
			chkState("2",msgDao);
			chkIllegal("7",msgDao);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 非法请假校验
	 * */
	public void chkIllegal(String days,MsgDao msgDao) throws Exception  {
		IData insParam = new DataMap();
		IData resultData = getResultData();
		StringBuffer buffermail = new StringBuffer();
		StringBuffer buffermailHead = new StringBuffer();
		
		List<String> listMail = new ArrayList<String>();
		List<String> listMailCc = new ArrayList<String>();
		List<String> listMailBcc = new ArrayList<String>();	
		List<EmailAttachment> listMailAtta = new ArrayList<EmailAttachment>();
		
		VacationGtmDao vacationGtmDao = new VacationGtmDao("bainiu");
		VacationDao adao = new VacationDao("bainiu");
		GroupDao gdao = new GroupDao("bainiu");
		insParam.put("DAYS", days);
		resultData=vacationGtmDao.queryIllegalLeave(insParam, resultData, "LEAVELIST");		
		IDataset leaveList = (IDataset) resultData.get("LEAVELIST");
						
		IData Rsp = new DataMap();		
		for(int i=0;i<leaveList.size();i++){
			IData subParam = leaveList.getData(i);
			String tempid="",gName="";
			IDataset pmInfos = new DatasetList();
			
			GroupDao groupDao = new GroupDao("bainiu");			
			IData resultDataGroup = getResultData();
			IData param = new DataMap();
			param.put("QUERY_KEY", subParam.get("PHONE"));
			param.put("QUERY_STATE", "0");		
			try {
				resultDataGroup = groupDao.queryGroup(param, resultDataGroup,"GROUPUSER");
				if(resultDataGroup.containsKey("GROUPUSER")){
					IDataset tempids = (IDataset) resultDataGroup.get("GROUPUSER");
					tempid = tempids.getData(0).getString("GROUP1_ID");
					gName = tempids.getData(0).getString("GROUP3_NAME");
					pmInfos = gdao.qryQjManagerRight(subParam,"DATA_LEAVE_CHK");
				}
				else
				{
					System.out.println("###群组查询无结果"+subParam.getString("NAME"));
					continue;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("###"+e);
				continue;
			}
			
			subParam.put("GROUP3_NAME", gName);
			
			if(Rsp.containsKey(tempid)){
				IDataset RspUser = (IDataset) Rsp.get(tempid+"_X");
				RspUser.add(subParam);
				Rsp.put(tempid+"_X",RspUser);
			}
			else{
				IDataset RspUser = new DatasetList();
				RspUser.add(subParam);				
				Rsp.put(tempid,pmInfos);
				Rsp.put(tempid+"_X",RspUser);
			}
		}
		
		buffermailHead.append("<style class=\"fox_global_style\">div.fox_html_content { line-height: 1.5; }div.fox_html_content { font-size: 10.5pt; font-family: 微软雅黑; color: rgb(0, 0, 0); line-height: 1.5; }</style> ");
		buffermailHead.append("<font size=\"2\"><span style=\"line-height: normal;\">各位好：</span></font> ");
		buffermailHead.append("<div>&nbsp; &nbsp;&nbsp;</div><div>&nbsp; &nbsp; 系统自动检测到"+days+"日内以下项目组员工存在非常规请假，请知晓</div> ");
		buffermailHead.append("<div>&nbsp; &nbsp; 包括申请时间大于离开时间或审批时间大于离开时间</div> ");
		buffermailHead.append("<div> ");
		buffermailHead.append("<div><span style=\"font-size: 13px;\">&nbsp; &nbsp;&nbsp;</span><span style=\"font-size: 13px; line-height: 1.5; background-color: window;\">&nbsp; &nbsp;</span></div> ");
		buffermailHead.append("<table border=\"1\" bordercolor=\"#000000\" cellpadding=\"2\" cellspacing=\"0\" style=\"font-size: 10pt; border-collapse:collapse; border:none\" width=\"70%\"> ");
		buffermailHead.append("	<tbody> ");
		buffermailHead.append("		<tr> ");
		buffermailHead.append("			<td width=\"6%\" style=\"border: solid 1 #000000\">姓名</td> ");
		buffermailHead.append("			<td width=\"11%\" style=\"border: solid 1 #000000\">邮箱</td> ");
		buffermailHead.append("			<td width=\"11%\" style=\"border: solid 1 #000000\">群组</td> ");
		buffermailHead.append("			<td width=\"11%\" style=\"border: solid 1 #000000\">申请时间</td> ");
		buffermailHead.append("			<td width=\"11%\" style=\"border: solid 1 #000000\">请假起始</td> ");
		buffermailHead.append("			<td width=\"11%\" style=\"border: solid 1 #000000\">第一审批</td> ");
		buffermailHead.append("			<td width=\"11%\" style=\"border: solid 1 #000000\">第二审批</td> ");
		buffermailHead.append("			<td width=\"8%\" style=\"border: solid 1 #000000\">当前状态</td> ");
		buffermailHead.append("			<td width=\"20%\" style=\"border: solid 1 #000000\">事由</td> ");
		buffermailHead.append("		</tr> ");		
		
		for(Entry<String, Object> entry:Rsp.entrySet()){
			buffermail = new StringBuffer();
			String key = entry.getKey();
			if(key.substring(key.length()-2).equals("_X"))
				continue;
			IDataset pminfos =(IDataset) entry.getValue();
			IDataset userInfos = (IDataset) Rsp.get(key+"_X");
			
			for(int i =0;i<pminfos.size();i++){
				IData PmUserbuf = (IData)pminfos.get(i);
				IData PmUserInfo = adao.queryUserByUserId(PmUserbuf.getString("USER_ID"));
				listMail.add(PmUserInfo.getString("EMAIL"));
			}
			
			buffermail.append(buffermailHead.toString());
			
			for(int x=0;x<userInfos.size();x++){
				IData userInfo = (IData)userInfos.get(x);
				String name = userInfo.get("NAME").toString();
				String email = userInfo.get("EMAIL").toString();
				String reqMark = StringUtil.getStringNew(userInfo,"REQ_MARK","");
				String stateName = userInfo.get("STATE_NAME").toString();
				String reqTime = userInfo.get("REQ_TIME").toString();
				String outDate = userInfo.get("OUT_DATE").toString();
				String firChktime = StringUtil.getStringNew(userInfo,"FIR_CHKTIME",""); 
				String secChktime = StringUtil.getStringNew(userInfo,"SEC_CHKTIME",""); 
				String gName = userInfo.get("GROUP3_NAME").toString();

				
				buffermail.append("		<tr> ");
				buffermail.append("			<td style=\"border: solid 1 #000000\">"+name+"</td> ");
				buffermail.append("			<td style=\"border: solid 1 #000000\">"+email+"</td> ");
				buffermail.append("			<td style=\"border: solid 1 #000000\">"+gName+"</td> ");				
				buffermail.append("			<td style=\"border: solid 1 #000000\">"+reqTime+"</td> ");
				buffermail.append("			<td style=\"border: solid 1 #000000\">"+outDate+"</td> ");
				buffermail.append("			<td style=\"border: solid 1 #000000\">"+firChktime+"</td> ");
				buffermail.append("			<td style=\"border: solid 1 #000000\">"+secChktime+"</td> ");
				buffermail.append("			<td style=\"border: solid 1 #000000\">"+stateName+"</td> ");				
				buffermail.append("			<td style=\"border: solid 1 #000000\">"+reqMark+"</td> ");
				buffermail.append("		</tr> ");
			}
			
			buffermail.append("</tbody> ");
			buffermail.append("</table> ");
			buffermail.append("</div> ");
			sendMail(listMail,listMailCc,listMailBcc,listMailAtta,"摆牛"+days+"日内违规请假通知",buffermail.toString());
		}   
		
	}
	/* 
	 * 以天为周期
	 * 从请假次日开始若一级未审批，发送一级审批提醒 0
	 * 一级审批通过次日开始若二级未审批，则发送二级审批提醒 1
	 * 从请假结束时间次日开始，若未销假发送销假提醒 2
	 * */
	public void chkState(String state,MsgDao msgDao) throws Exception {
		IData insParam = new DataMap();
		IData resultData = getResultData();
		StringBuffer smslBufMail = new StringBuffer();
		StringBuffer smslBufAll =  new StringBuffer();
		
		VacationGtmDao vacationGtmDao = new VacationGtmDao("bainiu");
		VacationDao adao = new VacationDao("bainiu");
		GroupDao gdao = new GroupDao("bainiu");
//		MsgDao msgDao = new MsgDao("bainiu");	//放到此处会锁表
		insParam.put("STATE", state);
		resultData=vacationGtmDao.queryOwnLeaveCk(insParam, resultData, "LEAVELIST");
		
		IDataset leaveList = (IDataset) resultData.get("LEAVELIST");
		
		for(int i=0;i<leaveList.size();i++){
			IData subParam = leaveList.getData(i);
			
			IData leverBuf 		= adao.queryUserByUserId(subParam.getString("USER_ID"));						
			IData groupInfo 	= gdao.qryUserGroup(subParam.getString("USER_ID").toString());
			String groupName    = groupInfo.getString("GROUP2_NAME")+"->"+groupInfo.getString("GROUP3_NAME");
			IData bakBuf 		= adao.queryUserByUserId(subParam.getString("BAK_USERID"));
			IData firChkBuf 	= adao.queryUserByUserId(subParam.getString("FIR_CHK_USERID"));
			
			
			IDataset pmInfos 	= new DatasetList();
			pmInfos 			= gdao.qryQjManagerRight(leverBuf,"DATA_LEAVE_CHK");
			IData group2Info 	= gdao.qryGroupInfoById(groupInfo,"GROUP2_ID");
			
			IDataset newPminfo = new DatasetList();
			if(group2Info.containsKey("MAN_USERLIST")&& !group2Info.get("MAN_USERLIST").equals("")){
				String[] chkUser = group2Info.get("MAN_USERLIST").toString().split(",");
				for(int x=0;x<chkUser.length;x++){
					IData newPm = new DataMap();
					newPm.put("USER_ID", chkUser[x]);
					newPminfo.add(newPm);
				}
			}
			//获取群组上的审批人，如果是请假，非空则替换pm邮件集合，同时如果是超长请假的销假，销假也通知有权限的人
			if(newPminfo.size()>0)
				pmInfos = newPminfo;
			
			int leavadays = StringUtil.compDateDif(subParam.getString("BACK_DATE"),subParam.getString("OUT_DATE"))-1;
			System.out.println("###leavadays="+leavadays);
			//特殊七天的////特殊十天的
			if(leavadays >= 10){
				IDataset pmInfos10 = gdao.qryQjManagerRight(leverBuf,"DATA_LEAVE_CHK10");
				if(pmInfos10.size()>0)
					pmInfos = pmInfos10;
			}
			else if(leavadays >= 7){
				IDataset pmInfos7 = gdao.qryQjManagerRight(leverBuf,"DATA_LEAVE_CHK7");
				if(pmInfos7.size()>0)
					pmInfos = pmInfos7;
			}
			
			System.out.println("###leverBuf="+leverBuf);			
			System.out.println("###"+leverBuf.getString("NAME")+subParam.getString("STATE_NAME")+subParam.getString("BACK_DATE"));
			int days = 0;			
			
			String nowState = subParam.getString("STATE");
			String stateName =subParam.getString("STATE_NAME");
			
			IData pmInfo = new DataMap();
			String titleName = "";//EMAIL标题名字
			String contentName = "";//EMAIL内名字
			String email ="";
			String chkUserId ="";//默认第一审批人USER_ID
			int emailCn = 1;//默认1，主要给PM使用
			String resultState = "0";//审批后状态，用于链接参数
			
			switch(Integer.parseInt(nowState))
			{
				case 0: //提醒第一审批组长
					titleName = "摆牛请假审批提醒-"+leverBuf.getString("NAME");
					contentName = firChkBuf.getString("NAME");
					days = StringUtil.compDateDif(StringUtil.getSysDate(),subParam.getString("REQ_TIME"))-1;
					email = firChkBuf.getString("EMAIL");
					chkUserId = firChkBuf.getString("USER_ID");
					resultState = "1";
					break;
				case 1: //提醒第二审批PM
					titleName = "摆牛请假审批提醒-"+leverBuf.getString("NAME");
					days = StringUtil.compDateDif(StringUtil.getSysDate(),subParam.getString("FIR_CHKTIME"))-1;
					
					System.out.println("###pmInfos="+pmInfos);
					emailCn = pmInfos.size();
					resultState = "2";
					break;
				case 2: //销假提醒
					titleName = "摆牛请假销假提醒-"+leverBuf.getString("NAME");
					contentName = leverBuf.getString("NAME");
					days = StringUtil.compDateDif(StringUtil.getSysDate(),subParam.getString("BACK_DATE"))-1;					
					//请假人邮箱为空则发给B角
					email = leverBuf.getString("EMAIL").equals("")?bakBuf.getString("EMAIL"):leverBuf.getString("EMAIL");
					break;
				default : 
					;
			}			
			if(days < 1) continue;
			
			//请假基础信息，邮件使用
			smslBufAll =  new StringBuffer();
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
			smslBufAll.append("     <td colspan=\"3\" class=\"xl129\" style=\"border: 1pt solid windowtext; padding-top: 1px; padding-right: 1px; padding-left: 1px; font-family: 微软雅黑, sans-serif; vertical-align: middle; white-space: nowrap; text-align: center;\">"+subParam.get("REQ_TYPENAME")+"</td>   ");
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
			

			for(int x=0;x<emailCn;x++){
				if(nowState.equals("1"))
					pmInfo = adao.queryUserByUserId(pmInfos.getData(x).getString("USER_ID"));
				contentName = nowState.equals("1") ? pmInfo.getString("NAME"):contentName;
				
				smslBufMail =  new StringBuffer();
				smslBufMail.append("<html><body>");
				smslBufMail.append("  <table cellspacing=\"6\" cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"font-family: 微软雅黑, Tahoma; font-size: 16px;\">");
				smslBufMail.append("   <tbody>");
				smslBufMail.append("    <tr><td>"+contentName+"，您好！</td></tr>");					
				smslBufMail.append("    <tr>");			
				smslBufMail.append("     <td>&nbsp;&nbsp;&nbsp;&nbsp;您有逾期"+stateName+"(逾期"+days+"天)状态的请假单待处理，请及时登录摆牛请假系统处理！ </td>");			
				smslBufMail.append("    </tr>");
				
				if(nowState.equals("2"))
					smslBufMail.append("    <tr><td></td></tr>");
				else if(nowState.equals("1")||nowState.equals("0"))
				{
					String mailId = StringUtil.getSequence(msgDao ,StringUtil.LogIdSeq);
					chkUserId = nowState.equals("0")?chkUserId:pmInfo.getString("EMAIL");					
					//ID|SMS_ID|STATE
					smslBufMail.append("    <tr><td>&nbsp;&nbsp;&nbsp;&nbsp;审批链接：<a   href=\""+StringUtil.domain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"							
							+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#"+resultState+"#"+chkUserId, "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"green\"><b>通过</b></font></a>");
					smslBufMail.append("  &nbsp;&nbsp;<a href=\""+StringUtil.domain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"
							+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#-"+resultState+"#"+chkUserId, "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"red\"><b>不通过</b></font></a></td></tr>");
				
/*					smslBufMail.append("    <tr><td>&nbsp;&nbsp;&nbsp;&nbsp;内网审批链接：<a   href=\""+StringUtil.innerdomain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"							
							+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#"+resultState+"#"+chkUserId, "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"green\"><b>通过</b></font></a>");
					smslBufMail.append("  &nbsp;&nbsp;<a href=\""+StringUtil.innerdomain+"/mobile?action=LeaveNoter&data={'MAILFLAG':'mail','SUBDATA':'"
							+StringUtil.praseStrByDES(subParam.getString("ID")+"#"+mailId+"#-"+resultState+"#"+chkUserId, "mamashuomiyaoyidingyaochang", StringUtil.DES_ENCRYPT)+"'}\"><font color=\"red\"><b>不通过</b></font></a></td></tr>");		*/		
				}			
				smslBufMail.append("    <tr><td></td></tr>");	
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("    <tr><td></td></tr>");
				smslBufMail.append("   </tbody>");
				smslBufMail.append("</table>");
				smslBufMail.append("</body></html>");
				smslBufMail.append(smslBufAll);
							 
				System.out.println("###"+email+":"+smslBufMail.toString());
				List<String> listMail = new ArrayList<String>();
				listMail.add(email);
				sendMailSimple(listMail,titleName,smslBufMail.toString());
			}
		}
	}
}

/*
在Spring中使用Quartz有两种方式实现：第一种是任务类继承QuartzJobBean，
第二种则是在配置文件里定义任务类和要执行的方法，类和方法仍然是普通类。
很显然，第二种方式远比第一种方式来的灵活。 
package com.ncs.hj;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SpringQtz extends QuartzJobBean{
	private static int counter = 0;
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		System.out.println();
		long ms = System.currentTimeMillis();
		System.out.println("\t\t" + new Date(ms));
		System.out.println(ms);
		System.out.println("(" + counter++ + ")");
		String s = (String) context.getMergedJobDataMap().get("service");
		System.out.println(s);
		System.out.println();
	}
}
*/
