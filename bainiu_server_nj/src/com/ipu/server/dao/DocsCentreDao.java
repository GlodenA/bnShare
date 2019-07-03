package com.ipu.server.dao;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ipu.server.util.Pagination;
import org.apache.log4j.Logger;

public class DocsCentreDao extends SmartBaseDao{
    private static transient Logger log = Logger.getLogger(DocsCentreDao.class);
    static String TABLE_NAME = "TF_F_DOCS";

    public DocsCentreDao(String connName) throws Exception {
        super(connName);
        // TODO Auto-generated constructor stub
    }
    public IData queryDocs(IData params, IData outParams, String keyList) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("SELECT  DOC_ID,DOC_NAME,DOC_AUTHOR_NAME,DOC_AUTHOR_ACCT,DOC_LABEL,DOC_PATH,DOC_TYPE,DOC_UPLOADER_ID,DOC_UPLOADER_NAME,DOWNLOAD_CNT,DOC_SUMMARY,date_format(INS_TIME, '%Y/%m/%d %H:%i') INS_TIME ");
        strBuf.append("FROM tf_f_docs a");
        strBuf.append(" WHERE 1=1 ");
        if(!"".equals(params.getString("DOC_NAME")) && params.getString("DOC_NAME") != null){
            if (!"".equals(params.getString("DOC_LABEL")) && params.getString("DOC_LABEL") != null){
                strBuf.append("and (A.DOC_NAME like '%"+params.getString("DOC_NAME")+"%' ");
                strBuf.append(" or A.DOC_LABEL like '%"+params.getString("DOC_LABEL")+"%' )");
            }else {
                strBuf.append("and A.DOC_NAME like '%" + params.getString("DOC_NAME") + "%' ");
            }
        }
        if(!"".equals(params.getString("DOC_TYPE")) && params.getString("DOC_TYPE") != null){
            strBuf.append("and A.DOC_TYPE = "+params.getString("DOC_TYPE"));
        }

        if(!"".equals(params.getString("DOC_AUTHOR_NAME")) && params.getString("DOC_AUTHOR_NAME") != null){
            strBuf.append("and A.DOC_AUTHOR_NAME like '%"+params.getString("DOC_AUTHOR_NAME")+"%' ");
        }
        if(!"".equals(params.getString("DOC_AUTHOR_ACCT")) && params.getString("DOC_AUTHOR_ACCT") != null){
            strBuf.append("and A.DOC_AUTHOR_ACCT like '%"+params.getString("DOC_AUTHOR_ACCT")+"%' ");
        }
        if(!"".equals(params.getString("DOC_ID")) && params.getString("DOC_ID") != null){
            strBuf.append("and A.DOC_ID = "+params.getString("DOC_ID") );
        }

        strBuf.append(" order by DOWNLOAD_CNT desc" );
        return this.queryPaginationList(strBuf.toString(), params, outParams, keyList, new Pagination(8, 6));
    }

    public IData insertDocDownloadLog(IData inparam, IData result) throws Exception {
        // TODO Auto-generated method stub
        StringBuffer strBuf  = new StringBuffer();
        int count = 0;
        strBuf.append("insert into tf_b_download_log values (?,?,?,?,?,?,?,NOW()) ");
        count = this.executeUpdate(strBuf.toString(),new Object[]{
                inparam.getString("LOG_ID"),
                inparam.getString("DOC_ID"),
                inparam.getString("DOC_NAME"),
                inparam.getString("DOC_AUTHOR_NAME"),
                inparam.getString("DOC_AUTHOR_ACCT"),
                inparam.getString("DOC_TYPE"),
                inparam.getString("DOC_UPLOADER_NAME")
        });
        this.commit();
        result.put("result", count);
        return result;
    }

    public IData insertDocs(IData inparam, IData result) throws Exception {
        // TODO Auto-generated method stub
        StringBuffer strBuf  = new StringBuffer();
        int count = 0;
        strBuf.append("insert into tf_f_docs values (?,?,?,?,?,?,?,?,?,0,NOW())");
        count = this.executeUpdate(strBuf.toString(),new Object[]{
                inparam.getString("DOC_ID"),
                inparam.getString("DOC_NAME"),
                inparam.getString("DOC_AUTHOR_NAME"),
                inparam.getString("DOC_AUTHOR_ACCT"),
                inparam.getString("DOC_LABEL"),
                inparam.getString("DOC_PATH"),
                inparam.getString("DOC_TYPE"),
                inparam.getString("DOC_UPLOADER_ID"),
                inparam.getString("DOC_UPLOADER_NAME")
        });

        this.commit();
        result.put("result", count);
        return result;
    }

    public IData insertQueryLog(IData inparam, IData result) throws Exception {
        // TODO Auto-generated method stub
        StringBuffer strBuf  = new StringBuffer();
        int count = 0;
        strBuf.append("insert into tf_f_docsQuery_log values (?,?,?,NOW())");
        count = this.executeUpdate(strBuf.toString(),new Object[]{
                inparam.getString("LOG_ID"),
                inparam.getString("USER_ID"),
                inparam.getString("HOT_KEY"),

        });

        this.commit();
        result.put("result", count);
        return result;
    }
    public IData updateDocDownloadcnt(IData params, IData outParam) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        int count=0;
        strBuf.append("UPDATE tf_f_docs A SET a.DOWNLOAD_CNT=DOWNLOAD_CNT+1  where a.DOC_ID=?");
        count = this.executeUpdate(strBuf.toString(), new Object[]{
                params.getString("DOC_ID"),
        });
        this.commit();
        outParam.put("result", count);
        return outParam;
    }

    /**
     * 查询用户信息
     */
    public IData queryUserInfo(IData param) throws Exception
    {
        return this.queryList("SELECT * FROM TF_F_USER WHERE USER_ACCT=:DOC_AUTHOR_ACCT", param).first();
    }

    public IDataset getRECTOP5(IData param) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("select count(DOC_ID) COU,DOC_NAME NAME from tf_b_download_log a WHERE DATE_FORMAT(a.download_time,'%Y%m')=DATE_FORMAT(NOW(),'%Y%m') GROUP BY DOC_ID ORDER BY  COU desc LIMIT 0,5");
        return this.queryList(strBuf.toString(), param);
    }

    public IDataset getSUMTOP5(IData param) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("select  DOWNLOAD_CNT COU, DOC_NAME NAME from tf_f_docs a  ORDER BY  COU desc LIMIT 0,5");
        return this.queryList(strBuf.toString(), param);
    }

    public IDataset getQRYTOP5(IData param) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("select  distinct(HOT_KEY) HOT_KEY from  tf_f_docsquery_log a where a.user_id =:USER_ID ORDER BY  query_time desc LIMIT 0,5");
        return this.queryList(strBuf.toString(), param);
    }

    public IDataset getQRYHOTKEYTOP5(IData param) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        String typeString = param.getString("TYPE_KEY");
        if("ONE".equals(typeString))
        {
            strBuf.append("select HOT_KEY ,VALUE  from  tf_b_hotkeysum_log a where a.DATE =date_format(sysdate() ,'%Y%m%d') ORDER BY  VALUE desc LIMIT 0,5 ");
        }
        else
        {
            strBuf.append("select b.HOT_KEY,b.VALUE from ( select HOT_KEY ,VALUE1+VALUE2+VALUE3+VALUE4+VALUE5+VALUE6+VALUE7   VALUE from  tf_b_hotkeysum_log a ) b   ORDER BY  b.VALUE desc LIMIT 0,5;");
        }
        return this.queryList(strBuf.toString(), param);
    }
}
