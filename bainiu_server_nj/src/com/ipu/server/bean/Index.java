package com.ipu.server.bean;




import java.sql.Timestamp;
import java.util.Random;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.mobile.util.MobileUtility;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.CommentDao;
import com.ipu.server.dao.ErrDao;
import com.ipu.server.dao.LoginDao;
import com.ipu.server.util.StringUtil;

public class Index extends AppBean {
	public IData insReply(IData param) throws Exception{
		IData resultData = getResultData();
		resultData.putAll(param);
		param.put("WHO_RESP", getContextData().getUserAcct());
		param.put("WHEN_RESP", StringUtil.getSysDate());
		param.put("STATE", "1");
		CommentDao dao = new CommentDao("bainiu");
		dao.insReply(param);
		return resultData;
	}
	public IData swcaacInfo(IData param) throws Exception{
		IData resultData = new DataMap();
		//留言信息
		CommentDao cdao = new CommentDao("bainiu");
		cdao.getCommentInfo(param,resultData,"COMMENT_INFOS");
		return resultData;
	}
	
	
	public IData getComment(IData param) throws Exception{
		IData resultData = new DataMap();
		//留言信息
		CommentDao cdao = new CommentDao("bainiu");
		cdao.getCommentInfo(param,resultData,"COMMENT_INFOS");
		return resultData;
	}
	public IData initData(IData param) throws Exception{
		IData resultData = getResultData();
		resultData.putAll(param);
		//报表
		ErrDao dao = new ErrDao("bainiu");
		//组织图标数据
		//提问达人榜
		IData lineData =  new DataMap();
		IDataset errtop10List = dao.getERRTOP10(param);
		lineData.put("LINEDATA", errtop10List);
		resultData.put("ERRTOP10DATA", lineData);
		//答疑英雄榜
		IDataset top10List = dao.getTOP10(param);
		IData lineData1 =  new DataMap();
		lineData1.put("LINEDATA", top10List);
		resultData.put("TROUBLETOP10DATA", lineData1);
		//活跃份子榜
		IDataset activeList = dao.getActiveList(param);
		IData lineData2 =  new DataMap();
		lineData2.put("LINEDATA", activeList);
		resultData.put("ACTIVETOP10DATA", lineData2);
		//热度曲线
		IDataset hotList = dao.gethotList(param);
		IData lineData3 =  new DataMap();
		lineData3.put("LINEDATA", hotList);
		resultData.put("HOTDATA", lineData3);
		//类型
		IDataset errTypeInfos =dao.getErrTypeInfos(param);
		IData lineData4 =  new DataMap();
		lineData4.put("LINEDATA", errTypeInfos);
		resultData.put("TYPEDATA", lineData4);
		//rad
		if(getContextData()!=null){
			param.put("USERID", getContextData().getUserID());
		}
		IDataset mydealTypeInfos =dao.mydealTypeInfos(param);
		IData lineData5 =  new DataMap();
		lineData5.put("LINEDATA", mydealTypeInfos);
		resultData.put("DEALTYPEDATA", lineData5);
		
		
		//留言信息
		CommentDao cdao = new CommentDao("bainiu");
		cdao.getCommentInfo(param,resultData,"COMMENT_INFOS");
		
		//浏览器使用
		LoginDao ldao = new LoginDao("bainiu");
		IData browserInfo = ldao.getbrowserInfo(param);
		resultData.putAll(browserInfo);
		//个人信息
		String ranking = ldao.getMyranking(param);
		String actNum = ldao.getActNum(param);
		String careMan = dao.getCareMan(param);
		resultData.put("RANKING", ranking);
		resultData.put("ACTNUM", actNum);
		resultData.put("CAREFORMAN", careMan);
		return resultData;
	}
	
	
	public IData submitComment(IData param) throws Exception{
		IData resultData = getResultData();
		resultData.putAll(param);
		CommentDao dao = new CommentDao("bainiu");
		//param.put("KEYID", StringUtil.getSequence(dao, StringUtil.LogIdSeq));
		param.put("KEYID", StringUtil.getSequenceNew(dao, StringUtil.StrLogIdSeq));
		if(null != getContextData()){
			param.put("WHO_REQ", getContextData().getUserAcct());
		}
		param.put("WHEN_REQ", StringUtil.getSysDate());
		param.put("CONTENT_REQ", param.getString("FEEDBACK_INFO","什么也没说"));
		param.put("STATE", "0");
		param.put("USE_TAG", "0");
		dao.insertComment(param);
		return resultData;
	}

	
	
}
