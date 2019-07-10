package com.ipu.server.bean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.BookManDao;
import com.ipu.server.dao.DocsCentreDao;
import com.ipu.server.dao.RightDao;
import com.ipu.server.util.HttpRequest;
import com.ipu.server.util.SeqMaker;
import com.ipu.server.util.randomNum;
import org.bouncycastle.jce.provider.JDKKeyFactory;

import com.ipu.server.util.*;

/**
 *
 * @ClassName: DocsCentreBean
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fengsq
 * @date 2019-05-19
 */
public class DocsCentreBean extends AppBean {
    /**
     *
     * @Title: init
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author fengsq
     * @date  2019-05-19
     * @param param
     * @return
     * @throws Exception
     */
    public IData init(IData param) throws Exception
    {
        IData resultData = getResultData();
        resultData.putAll(param);
        return resultData;
    }

    /**
     *
     * @Title: queryDocs
     * @Description: 资料中心下载页面初始化函数，包含表格，图标和关键词查询
     * @author fengsq
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryDocs(IData param) throws Exception{
         String userId = getContextData().getUserID();
         param.put("USER_ID",userId);
        IData resultData = getResultData();
        DocsCentreDao docsCentreDao = new DocsCentreDao("bainiu");
        resultData = docsCentreDao.queryDocs(param, resultData, "DOCLIST");

        //判断查询标记，显示不同数据
        if(!"".equals(param.getString("QUERY_TAG")) && param.getString("QUERY_TAG") != null) {
            String queryTag = param.getString("QUERY_TAG");
            resultData.put("QUERY_TAG",queryTag);
        }
        //查询入日志表
        if(!"".equals(param.getString("HOT_KEY")) && param.getString("HOT_KEY") != null){
            randomNum rand = new randomNum();
            String LOGID = rand.generateNumber(8);
            param.put("LOG_ID", LOGID);
            param.put("USER_ID",userId);
            docsCentreDao.insertQueryLog(param,resultData);
        }

        //统计入库 begin
        String sysDate = DateUtil.getDateString("yyyyMMdd");
        String sysWeekday = DateUtil.getWeekOfDate();
        //更新统计表
        IData dataParam = new DataMap();
        dataParam.put("DATE",sysDate);
        dataParam.put("SYSWEEKDAY",sysWeekday);
        docsCentreDao.isUpdateHotKeyAll(dataParam);


        if (!"".equals(param.getString("HOT_KEY")) && param.getString("HOT_KEY") != null) {
            IDataset isHotKeyList = docsCentreDao.getHotKeyCount(param);
            if (isHotKeyList.size() > 0) {
                IData t_data = isHotKeyList.getData(0);
                if (sysWeekday.equals("星期一")) {
                    if (!isHotKeyList.getData(0).getString("DATE1").equals(sysDate)) {
                        int count = 1;
                        t_data.put("DATE1", sysDate);
                        t_data.put("VALUE1", count);
                    } else {
                        int count = t_data.getInt("VALUE1") + 1;
                        t_data.put("VALUE1", count);
                    }
                    int todayCount = t_data.getInt("VALUE1");
                    t_data.put("VALUE",todayCount);

                } else if (sysWeekday.equals("星期二")) {
                    if (!t_data.getString("DATE2").equals(sysDate)) {
                        int count = 1;
                        t_data.put("DATE2", sysDate);
                        t_data.put("VALUE2", count);
                    } else {
                        int count = t_data.getInt("VALUE2") + 1;
                        t_data.put("VALUE2", count);
                    }
                    int todayCount = t_data.getInt("VALUE2");
                    t_data.put("VALUE",todayCount);

                } else if (sysWeekday.equals("星期三")) {
                    if (!t_data.getString("DATE3").equals(sysDate)) {
                        int count = 1;
                        t_data.put("DATE3", sysDate);
                        t_data.put("VALUE3", count);
                    } else {
                        int count = t_data.getInt("VALUE3") + 1;
                        t_data.put("VALUE3", count);
                    }
                    int todayCount = t_data.getInt("VALUE3");
                    t_data.put("VALUE",todayCount);

                } else if (sysWeekday.equals("星期四")) {
                    if (!t_data.getString("DATE4").equals(sysDate)) {
                        int count = 1;
                        t_data.put("DATE4", sysDate);
                        t_data.put("VALUE4", count);
                    } else {
                        int count = t_data.getInt("VALUE4") + 1;
                        t_data.put("VALUE4", count);
                    }
                    int todayCount = t_data.getInt("VALUE4");
                    t_data.put("VALUE",todayCount);

                } else if (sysWeekday.equals("星期五")) {
                    if (!t_data.getString("DATE5").equals(sysDate)) {
                        int count = 1;
                        t_data.put("DATE5", sysDate);
                        t_data.put("VALUE5", count);
                    } else {
                        int count = t_data.getInt("VALUE5") + 1;
                        t_data.put("VALUE5", count);
                    }
                    int todayCount = t_data.getInt("VALUE5");
                    t_data.put("VALUE",todayCount);

                } else if (sysWeekday.equals("星期六")) {
                    if (!t_data.getString("DATE6").equals(sysDate)) {
                        int count = 1;
                        t_data.put("DATE6", sysDate);
                        t_data.put("VALUE6", count);
                    } else {
                        int count = t_data.getInt("VALUE6") + 1;
                        t_data.put("VALUE6", count);
                    }
                    int todayCount = t_data.getInt("VALUE6");
                    t_data.put("VALUE",todayCount);

                } else if (sysWeekday.equals("星期日")) {
                    if (!t_data.getString("DATE7").equals(sysDate)) {
                        int count = 1;
                        t_data.put("DATE7", sysDate);
                        t_data.put("VALUE7", count);
                    } else {
                        int count = t_data.getInt("VALUE7") + 1;
                        t_data.put("VALUE7", count);
                    }
                    int todayCount = t_data.getInt("VALUE7");
                    t_data.put("VALUE",todayCount);

                }
                t_data.put("DATE", sysDate);
                IData t_resultdata = new DataMap();
                docsCentreDao.updateHotKeyCount(t_data, t_resultdata);

            }
            else {
                IData t_resultdata = new DataMap();
                param.put("VALUE1", 0);
                param.put("VALUE2", 0);
                param.put("VALUE3", 0);
                param.put("VALUE4", 0);
                param.put("VALUE5", 0);
                param.put("VALUE6", 0);
                param.put("VALUE7", 0);
                param.put("VALUE",1);
                param.put("DATE1", sysDate);
                param.put("DATE2", sysDate);
                param.put("DATE3", sysDate);
                param.put("DATE4", sysDate);
                param.put("DATE5", sysDate);
                param.put("DATE6", sysDate);
                param.put("DATE7", sysDate);
                param.put("DATE", sysDate);
                if (sysWeekday.equals("星期一")) {
                    param.put("VALUE1", 1);

                } else if (sysWeekday.equals("星期二")) {
                    param.put("VALUE2", 1);

                } else if (sysWeekday.equals("星期三")) {
                    param.put("VALUE3", 1);

                } else if (sysWeekday.equals("星期四")) {
                    param.put("VALUE4", 1);

                } else if (sysWeekday.equals("星期五")) {
                    param.put("VALUE5", 1);

                } else if (sysWeekday.equals("星期六")) {
                    param.put("VALUE6", 1);

                } else if (sysWeekday.equals("星期日")) {
                    param.put("VALUE7", 1);

                }
                docsCentreDao.insertHotKeyCount(param, t_resultdata);

            }
        }
        //统计入库 end
        //组织图标数据
        //总下载量
        IData lineData =  new DataMap();
        IDataset sumtop5List = docsCentreDao.getSUMTOP5(param);
        lineData.put("LINEDATA", sumtop5List);
        resultData.put("SUMTOP5DATA", lineData);
        //近一月下载量
        IDataset rectop5List = docsCentreDao.getRECTOP5(param);
        IData lineData1 =  new DataMap();
        lineData1.put("LINEDATA", rectop5List);
        resultData.put("RECTOP5DATA", lineData1);
        //历史查询
        IDataset querytop5List = docsCentreDao.getQRYTOP5(param);
        IData queryData =  new DataMap();
        queryData.put("QUERYDATA", querytop5List);
        resultData.put("QRYHISDATA", queryData);
        doActionLog(param);
        return resultData;
    }
    /**
     *
     * @Title: insertDocDownloadLog
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author fengsq
     * @date  2019-05-29
     * @param param
     * @return
     * @throws Exception
     */
    public IData insertDocDownloadLog(IData param) throws Exception
    {
        IData resultData = new DataMap();
        DocsCentreDao DocsDao = new DocsCentreDao("bainiu");
        randomNum rand = new randomNum();
        String LOGID = rand.generateNumber(8);
        param.put("LOG_ID", LOGID);
        resultData = DocsDao.insertDocDownloadLog(param,resultData);
        doActionLog(param);
        return resultData;
    }

    /**
     *
     * @Title: updateDocDownloadcnt
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author fengsq
     * @date  2019-05-29
     * @param param
     * @return
     * @throws Exception
     */
    public IData updateDocDownloadcnt(IData param) throws Exception
    {
        IData resultData = new DataMap();
        DocsCentreDao DocsDao = new DocsCentreDao("bainiu");
        resultData = DocsDao.updateDocDownloadcnt(param,resultData);
        doActionLog(param);
        return resultData;
    }

    /**
     *
     * @Title: queryDocs
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author fengsq
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryDocsByID(IData param) throws Exception{
        IData resultData = getResultData();
        DocsCentreDao docsCentreDao = new DocsCentreDao("bainiu");
        resultData = docsCentreDao.queryDocs(param, resultData, "DOCLIST");
        return resultData;
    }

    /**
     *
     * @Title: queryDocsByUserName
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author fengsq
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryDocsByUserName(IData param) throws Exception{
        IData resultData = getResultData();
        DocsCentreDao docsCentreDao = new DocsCentreDao("bainiu");
        resultData = docsCentreDao.queryDocs(param, resultData, "DOCLIST");
        if(param.containsKey("DOC_AUTHOR_ACCT")&&!"".endsWith(param.getString("DOC_AUTHOR_ACCT")))
        {
            IData userInfo = docsCentreDao.queryUserInfo(param);
            resultData.putAll(userInfo);
        }

        resultData.put("DOC_AUTHOR_NAME",param.getString("DOC_AUTHOR_NAME"));

        return resultData;
    }

    /**
     *
     * @Title: queryHotKeySort
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author wangdd
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryHotKeySort(IData param) throws Exception{

        IData resultData = getResultData();
        DocsCentreDao docsCentreDao = new DocsCentreDao("bainiu");
        IDataset queryData = docsCentreDao.getQRYHOTKEYTOP5(param);
        resultData.put("HOTKEY_LIST", queryData);
        return resultData;
    }

    public void updateDocs_Name_Lable_SummaryByID(IData param) throws  Exception{

        DocsCentreDao DocsDao = new DocsCentreDao("bainiu");
        DocsDao.updateDocs_Name_Lable_SummaryByID(param);
    }

    public IData DeleDocByID(IData param) throws Exception{
        IData resultData = getResultData();
        resultData.put("result","100");
        try {
            RightDao rightDao = new RightDao("bainiu");
            String userId = getContextData().getUserID();
            if (!rightDao.queryUserRight(userId, "DATA_DOCSCENTRE_OPER")) {
                resultData.put("result", "0");
                resultData.put("resultInfo", "对不起，您没有删除权限！");

            }
            else {
                DocsCentreDao DocsDao = new DocsCentreDao("bainiu");
                DocsDao.DeleDocByID(param);
                return resultData;
            }
        } catch (Exception e) {
           e.printStackTrace();
           resultData.put("result","0");
           resultData.put("resultInfo","系统异常:"+e);
           return resultData;
        }
        return resultData;
    }
    public IData queryDOC_SUMMARY(IData param) throws Exception{

        IData resultData = getResultData();
        DocsCentreDao docsCentreDao = new DocsCentreDao("bainiu");
        IDataset queryData = docsCentreDao.queryDOC_SUMMARY(param);
        resultData.put("DOC_INFO", queryData);
        return resultData;
    }
}
