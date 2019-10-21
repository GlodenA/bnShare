package com.ipu.server.bean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.DocsCentreDao;
import com.ipu.server.dao.RightDao;
import com.ipu.server.util.DateUtil;
import com.ipu.server.util.randomNum;

/**
 *
 * @ClassName: WebLinkBean
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fengsq
 * @date 2019-05-19
 */
public class WebLinkBean extends AppBean {
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
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author fengsq
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryWebLink(IData param) throws Exception{
        IData resultData = getResultData();
        DocsCentreDao docsCentreDao = new DocsCentreDao("bainiu");
        resultData = docsCentreDao.queryWebLink(param, resultData, "WEBSLIST");
        return resultData;
    }


}
