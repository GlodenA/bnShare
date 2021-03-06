package com.ipu.server.dao;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ipu.server.util.Pagination;
import org.apache.log4j.Logger;
import scala.annotation.meta.param;

import java.io.File;

public class DocsCentreDao extends SmartBaseDao {
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
        if (!"".equals(params.getString("DOC_NAME")) && params.getString("DOC_NAME") != null) {
            if (!"".equals(params.getString("DOC_LABEL")) && params.getString("DOC_LABEL") != null) {
                strBuf.append("and (A.DOC_NAME like '%" + params.getString("DOC_NAME") + "%' ");
                strBuf.append(" or A.DOC_LABEL like '%" + params.getString("DOC_LABEL") + "%' )");
            } else {
                strBuf.append("and A.DOC_NAME like '%" + params.getString("DOC_NAME") + "%' ");
            }
        }
        if (!"".equals(params.getString("DOC_TYPE")) && params.getString("DOC_TYPE") != null) {
            strBuf.append("and A.DOC_TYPE = " + params.getString("DOC_TYPE"));
        }

        if (!"".equals(params.getString("DOC_AUTHOR_NAME")) && params.getString("DOC_AUTHOR_NAME") != null) {
            strBuf.append("and A.DOC_AUTHOR_NAME like '%" + params.getString("DOC_AUTHOR_NAME") + "%' ");
        }
        if (!"".equals(params.getString("DOC_AUTHOR_ACCT")) && params.getString("DOC_AUTHOR_ACCT") != null) {
            strBuf.append("and A.DOC_AUTHOR_ACCT like '%" + params.getString("DOC_AUTHOR_ACCT") + "%' ");
        }
        if (!"".equals(params.getString("DOC_ID")) && params.getString("DOC_ID") != null) {
            strBuf.append("and A.DOC_ID = " + params.getString("DOC_ID"));
        }

        strBuf.append(" order by DOWNLOAD_CNT desc");
        return this.queryPaginationList(strBuf.toString(), params, outParams, keyList, new Pagination(8, 6));

    }

    public IData insertDocDownloadLog(IData inparam, IData result) throws Exception {
        // TODO Auto-generated method stub
        StringBuffer strBuf = new StringBuffer();
        int count = 0;
        strBuf.append("insert into tf_b_download_log values (?,?,?,?,?,?,?,NOW()) ");
        count = this.executeUpdate(strBuf.toString(), new Object[]{
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
        StringBuffer strBuf = new StringBuffer();
        int count = 0;
        strBuf.append("insert into tf_f_docs values (?,?,?,?,?,?,?,?,?,0,NOW(),?)");
        count = this.executeUpdate(strBuf.toString(), new Object[]{
                inparam.getString("DOC_ID"),
                inparam.getString("DOC_NAME"),
                inparam.getString("DOC_AUTHOR_NAME"),
                inparam.getString("DOC_AUTHOR_ACCT"),
                inparam.getString("DOC_LABEL"),
                inparam.getString("DOC_PATH"),
                inparam.getString("DOC_TYPE"),
                inparam.getString("DOC_UPLOADER_ID"),
                inparam.getString("DOC_UPLOADER_NAME"),
                inparam.getString("DOC_SUMMARY")
        });

        this.commit();
        result.put("result", count);
        return result;
    }

    public IData insertQueryLog(IData inparam, IData result) throws Exception {
        // TODO Auto-generated method stub
        StringBuffer strBuf = new StringBuffer();
        int count = 0;
        strBuf.append("insert into tf_f_docsQuery_log values (?,?,?,NOW())");
        count = this.executeUpdate(strBuf.toString(), new Object[]{
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
        int count = 0;
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
    public IData queryUserInfo(IData param) throws Exception {
        return this.queryList("SELECT * FROM TF_F_USER WHERE USER_ACCT=:DOC_AUTHOR_ACCT", param).first();
    }

    public IDataset getRECTOP5(IData param) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("select count(DOC_ID) COU,DOC_NAME NAME from tf_b_download_log a WHERE DATE_FORMAT(a.download_time,'%Y%m')=DATE_FORMAT(NOW(),'%Y%m') GROUP BY DOC_ID ORDER BY  COU desc LIMIT 0,5");
        return this.queryList(strBuf.toString(), param);
    }

    public IDataset getSUMTOP5(IData param) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("select  DOWNLOAD_CNT COU, DOC_NAME NAME from tf_f_docs a  where DOWNLOAD_CNT <>0 ORDER BY  COU desc LIMIT 0,5");
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
        if ("ONE".equals(typeString)) {
            strBuf.append("select HOT_KEY ,VALUE  from  tf_b_hotkeysum_log a where a.DATE =date_format(sysdate() ,'%Y%m%d') and a.value <>0 ORDER BY  VALUE desc LIMIT 0,5 ");
        } else {
            strBuf.append("select b.HOT_KEY,b.VALUE from ( select HOT_KEY ,VALUE1+VALUE2+VALUE3+VALUE4+VALUE5+VALUE6+VALUE7   VALUE from  tf_b_hotkeysum_log a ) b  where b.value <>0 ORDER BY  b.VALUE desc LIMIT 0,5;");
        }
        return this.queryList(strBuf.toString(), param);
    }

    public void updateDocsByID(IData params) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        int count = 0;
        strBuf.append("UPDATE tf_f_docs SET DOC_NAME=?,DOC_LABEL=?,DOC_SUMMARY=? where DOC_ID=?");
        count = this.executeUpdate(strBuf.toString(), new Object[]{
                params.getString("DOC_NAME"),
                params.getString("DOC_LABEL"),
                params.getString("DOC_SUMMARY"),
                params.getInt("DOC_ID"),
        });
        this.commit();
    }

    public IData DeleDocByID(IData params) throws Exception {
        StringBuffer str = new StringBuffer();
        str.append("select DOC_PATH from tf_f_docs where DOC_ID=" + params.getInt("DOC_ID"));
        IDataset datapath = this.queryList(str.toString(), params);

        StringBuffer strBuf = new StringBuffer();
        strBuf.append("DELETE from tf_f_docs where DOC_ID=?");
        if(datapath.size()>0){
            if (clearFiles((datapath.getData(0)).getString("DOC_PATH"))) {
                params.put("delete", 1);
                params.put("result", 1);
                this.executeUpdate(strBuf.toString(), new Object[]{
                        params.getInt("DOC_ID")
                });
                this.commit();
            } else {
                params.put("delete",  0);
            }
            return params;
        }else {
            params.put("delete", 0);
            return params;
        }

    }

    private boolean clearFiles(String workspaceRootPath) {
        workspaceRootPath.replace("[\"", "");
        workspaceRootPath.replace("\"]", "");
        File file = new File(workspaceRootPath.toString());
        if (file.exists()) {
            deleteFile(file);
            return true;
        } else return false;
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }

    public IDataset queryDocsSum(IData param) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        String doc_name = param.getString("DOC_NAME");
        strBuf.append("select DOC_AUTHOR_NAME ,DOC_LABEL, DOWNLOAD_CNT,INS_TIME,DOC_SUMMARY  from  tf_f_docs  where DOC_NAME='" + doc_name + "'");
        return this.queryList(strBuf.toString(), param);
    }

    public IDataset getHotKeyCount(IData param) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("select HOT_KEY,VALUE1, VALUE2,VALUE3,VALUE4,VALUE5,VALUE6,VALUE7, " +
                " DATE1, DATE2, DATE3," +
                "DATE4" +
                ",DATE5,DATE6," +
                "DATE7 from  tf_b_hotkeysum_log a where HOT_KEY=:HOT_KEY");
        return this.queryList(strBuf.toString(), param);
    }

    public IData insertHotKeyCount(IData inparam, IData result) throws Exception {
        // TODO Auto-generated method stub
        StringBuffer strBuf = new StringBuffer();
        int count = 0;
        strBuf.append("insert into tf_b_hotkeysum_log values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?)");
        count = this.executeUpdate(strBuf.toString(), new Object[]{
                inparam.getString("HOT_KEY"),
                inparam.getString("VALUE1"),
                inparam.getString("VALUE2"),
                inparam.getString("VALUE3"),
                inparam.getString("VALUE4"),
                inparam.getString("VALUE5"),
                inparam.getString("VALUE6"),
                inparam.getString("VALUE7"),
                inparam.getString("DATE1"),
                inparam.getString("DATE2"),
                inparam.getString("DATE3"),
                inparam.getString("DATE4"),
                inparam.getString("DATE5"),
                inparam.getString("DATE6"),
                inparam.getString("DATE7"),
                inparam.getString("VALUE"),
                inparam.getString("DATE")
        });
        this.commit();
        result.put("result", count);
        return result;
    }

    public IData updateHotKeyCount(IData params, IData outParam) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        int count = 0;
        strBuf.append("UPDATE tf_b_hotkeysum_log A SET a.VALUE1=? , a.VALUE2=?,a.VALUE3=?,a.VALUE4=?,a.VALUE5=?,a.VALUE6=?" +
                ", a.VALUE1=? , a.DATE1=?,a.DATE2=?,a.DATE3=?,a.DATE4=?,a.DATE5=?,a.DATE6=?,a.DATE7=?,a.VALUE=?,a.update_time=now() where a.HOT_KEY=?");
        count = this.executeUpdate(strBuf.toString(), new Object[]{
                params.getString("VALUE1"),
                params.getString("VALUE2"),
                params.getString("VALUE3"),
                params.getString("VALUE4"),
                params.getString("VALUE5"),
                params.getString("VALUE6"),
                params.getString("VALUE7"),
                params.getString("DATE1"),
                params.getString("DATE2"),
                params.getString("DATE3"),
                params.getString("DATE4"),
                params.getString("DATE5"),
                params.getString("DATE6"),
                params.getString("DATE7"),
                params.getString("VALUE"),
                params.getString("HOT_KEY")
        });
        this.commit();
        outParam.put("result", count);
        return outParam;
    }
    public void isUpdateHotKeyAll(IData param ) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("select * from  tf_b_hotkeysum_log a where DATE = "+param.getString("DATE"));
        IDataset  resultData = this.queryList(strBuf.toString(), param);
        if (resultData.size() == 0)
        {
            StringBuffer strBufUp = new StringBuffer();
            strBufUp.append("update  tf_b_hotkeysum_log set ");
            String sysWeekday = param.getString("SYSWEEKDAY");
            if (sysWeekday.equals("星期一")) {
                strBufUp.append(" DATE1 = "+param.getString("DATE") + " , " + " VALUE1 = 0 , ");
            } else if (sysWeekday.equals("星期二")) {
                strBufUp.append(" DATE2 = "+param.getString("DATE") + " , " + " VALUE2 = 0 , ");
            } else if (sysWeekday.equals("星期三")) {
                strBufUp.append(" DATE3 = "+param.getString("DATE") + " , " + " VALUE3 = 0 , ");
            } else if (sysWeekday.equals("星期四")) {
                strBufUp.append(" DATE4 = "+param.getString("DATE") + " , " + " VALUE4 = 0 , ");
            } else if (sysWeekday.equals("星期五")) {
                strBufUp.append(" DATE5 = "+param.getString("DATE") + " , " + " VALUE5 = 0 , ");
            } else if (sysWeekday.equals("星期六")) {
                strBufUp.append(" DATE6 = "+param.getString("DATE") + " , " + " VALUE6 = 0 , ");
            } else if (sysWeekday.equals("星期日")) {
                strBufUp.append(" DATE7 = "+param.getString("DATE") + " , " + " VALUE7 = 0 , ");
            }
            strBufUp.append(" DATE = "+param.getString("DATE") + " , " + " VALUE = 0 ");
            this.executeUpdate(strBufUp.toString(),param);
            this.commit();
        }
    }
    public IDataset getHotKey(IData param) throws Exception{

        return this.queryList("SELECT HOT_KEY KEYWORD FROM tf_b_hotkeysum_log WHERE HOT_KEY LIKE  '%"+param.getString("KEY_WORD")+"%' ", param);
    }

    public IData queryWebLink(IData param, IData resultData, String webslist) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("SELECT SYS_NAME,USER_NAME,PASSWD,WEB_URL from  tf_f_weblink ");
        return this.queryPaginationList(strBuf.toString(), param, resultData, webslist, new Pagination(10, 6));

    }
}
