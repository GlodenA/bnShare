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
     * @Title: queryDocsByUserName
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author fengsq
     * @param param
     * @return
     * @throws Exception
     */
    /*public IData queryDocsByHotKey(IData param) throws Exception{

        IData resultData = getResultData();
        DocsCentreDao docsCentreDao = new DocsCentreDao("bainiu");
        resultData = docsCentreDao.queryDocs(param, resultData, "DOCLIST");
        String userId = "";
        userId = getContextData().getUserID();
        String userName = getContextData().getName();
        if(!"".equals(param.getString("HOT_KEY")) && param.getString("HOT_KEY") != null){
            randomNum rand = new randomNum();
            String LOGID = rand.generateNumber(8);
            param.put("LOG_ID", LOGID);
            param.put("USER_ID",userId);
            docsCentreDao.insertQueryLog(param,resultData);
        }
        resultData.put("USER_ID", userId);
        resultData.put("USER_NAME", userName);
        //历史查询
        IDataset querytop5List = docsCentreDao.getQRYTOP5(param);
        IData queryData =  new DataMap();
        queryData.put("QUERYDATA", querytop5List);
        resultData.put("QRYHISDATA", queryData);
        return resultData;
    }*/


}
