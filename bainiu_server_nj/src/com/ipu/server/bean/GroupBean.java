package com.ipu.server.bean;

import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import com.ai.MainQuery;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.GroupDao;
import com.ipu.server.dao.RightDao;
import com.ipu.server.dao.VacationDao;
import com.ipu.server.util.ExportFile;
import com.ipu.server.util.StringUtil;

public class GroupBean  extends AppBean {
	
	public IData init(IData param) throws Exception {
		GroupDao groupDao = new GroupDao("bainiu");
		IData resultData = getResultData();
		//resultData = groupDao.init(param, resultData);
		IData resultData2 = getResultData();
		resultData2 = queryGroupTree(param);
		resultData = queryGroup(param);
		resultData.put("GROUP1TREE",resultData2.get("GROUP1TREE"));
		chkExportRight(resultData);
		
		return resultData;
	}
	
	public IData queryGroup(IData param) throws Exception {

		IData resultData = getResultData();		
		GroupDao groupDao = new GroupDao("bainiu");
		resultData = groupDao.queryGroup(param, resultData,"GROUPUSER");
		chkExportRight(resultData);
		
		doActionLog(param);
		return resultData;
	}
	
	public IData queryGroupTree(IData param) throws Exception {

		IData resultData = getResultData();
		GroupDao groupDao = new GroupDao("bainiu");
		
		String groupL = "1";
		String respType = "";
		
		if(!(null==param.get("GROUP_LEVEL")||param.getString("GROUP_LEVEL").trim().isEmpty()))
		{
			groupL = param.getString("GROUP_LEVEL").trim();
		}
		if(groupL.equals("2")) respType = "GROUP2TREE" ;
		else if(groupL.equals("3")) respType = "GROUP3TREE" ;
		else if(groupL.equals("4")) respType = "GROUP4TREE" ;
		else if(groupL.equals("p2")) respType = "PGROUP2LIST" ;
		else if(groupL.equals("p3")) respType = "PGROUP3LIST" ;
		else if(groupL.equals("p4")) respType = "PGROUP4LIST" ;
		else respType = "GROUP1TREE" ;
		
		resultData = groupDao.queryGroupTree(param, resultData,respType);
		chkExportRight(resultData);
		
		doActionLog(param);
		return resultData;
	}
	
	public IData queryUser(IData param) throws Exception {
		IData resultData = getResultData();
		GroupDao groupDao = new GroupDao("bainiu");
		resultData = groupDao.queryUser(param, resultData,"USERLIST");
		doActionLog(param);
		return resultData;
	}
	
	public IData modifyGroup(IData param) throws Exception {
		IData resultData = getResultData();
		GroupDao groupDao = new GroupDao("bainiu");
		resultData = groupDao.modifyGroup(param, resultData);
		doActionLog(param);
		return resultData;
	}
	
	public IData QueryUserList(IData param) throws Exception {
		IData resultData = getResultData();
		MainQuery qry = new MainQuery();
		GroupDao groupDao = new GroupDao("bainiu");
		String userId = param.getString("USER_ID");
		IDataset dset = groupDao.queryItemNum(param);
		if(dset.size()>0){
			IData data = new DataMap();
			for(int i = 0;i<dset.size();i++){
				IData ds = (IData) dset.get(i);
				data.put(ds.getString("ATTR_CODE"), ds.getString("ATTR_VALUE"));
			}
			resultData.put("USERTYPE", "1");
			resultData.put("USERITEM", data);
		}else{
			String[] buf = param.getString("EMALL").split("@");
			if(buf.length>1){
				if("asiainfo.com".equals(buf[1].toLowerCase().trim())){
					String str = qry.queryAiBook("NT=" + buf[0] + "=BAINIU6");
					IDataset infos = StringUtil.getTieStr2IDataset(str);
					if (infos.size() > 0) {
						for (int i = 0; i < infos.size(); i++) {
							IData map = (IData) infos.get(i);
							if (buf[0].equals(map.getString("NT账号"))) {
								IData data = new DataMap();
								data.put("姓名", map.getString("姓名"));
								data.put("工号", map.getString("工号"));
								data.put("入职日期", map.getString("入职日期"));
								data.put("上级员工(员工号)", map.getString("上级员工(员工号)"));
								data.put("办公地点", map.getString("办公地点"));
								data.put("部门", map.getString("部门"));
								data.put("公司", map.getString("公司"));
								data.put("座机", map.getString("座机"));
								data.put("NT账号", map.getString("NT账号"));
								data.put("手机号", map.getString("手机号"));
								data.put("年龄", map.getString("年龄"));
								data.put("出生日期", map.getString("出生日期"));
								data.put("邮箱", map.getString("邮箱"));
								resultData.put("USERITEM", data);
								resultData.put("USERTYPE", "1");
								queryDatasetList(data, userId);
							}
						}
					}
				}
			}
		}
		return resultData;
	}
	
	public void queryDatasetList(IData data,String userId) throws Exception {
		IDataset set = new DatasetList();
		Iterator iter = data.entrySet().iterator();
		while(iter.hasNext()){
			IData da = new DataMap();
			String objValue = iter.next().toString();
			da.put("USER_ID", userId);
			da.put("ATTR_CODE", objValue.split("=")[0]);
			da.put("ATTR_VALUE", objValue.split("=")[1]);
			set.add(da);
		}
		GroupDao groupDao = new GroupDao("bainiu");
		groupDao.insertUserItem(set);
		
	}
	
	/*
	 * 判断导出权限
	 * */
	private void chkExportRight(IData resultData) throws Exception{
		
		RightDao rightDao = new RightDao("bainiu");
		if(rightDao.queryUserRight(getContextData().getUserID(),"DATA_GROUP_EXPORT")){
			resultData.put("FLAT","1");
		}		
	}
	
	public void exportGroupList(IData param,HttpServletResponse response)throws Exception
	{
		IData resultData = new DataMap();
		GroupDao groupDao = new GroupDao("bainiu");
		IDataset set = (IDataset)groupDao.queryGroup(param, resultData,"GROUPUSER").get("GROUPUSER");
		VacationDao vacationDao = new VacationDao("bainiu");
		IDataset tabset = vacationDao.queryExportTab("GroupUserList");
		ExportFile file = new ExportFile();
		file.exportAskleave(response,"人力管理.xls", "人力管理",set,tabset);
	}
	
}
