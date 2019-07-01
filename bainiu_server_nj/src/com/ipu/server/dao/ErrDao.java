package com.ipu.server.dao;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.mobile.db.dao.impl.BaseDAO;
 
import com.ipu.server.util.StringUtil;
 
import com.ipu.server.util.Pagination;
 

public class ErrDao extends SmartBaseDao {
	private static transient Logger log = Logger.getLogger(ErrDao.class);
	static String TABLE_IPU_MEMBER = "tf_f_exception";
	static String TABLE_IPU_MEMBER_L = "tf_l_exception";
	public ErrDao(String connName) throws Exception {
		super(connName);
	}

	/**
	 * 查询成员信息
	 */
	public IData selectInfos(IData params,IData outParam,String keyList) {
		try {
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("SELECT                                     ");
			strBuf.append("	S.DEAL_TIME,S.EX_ABSTRACT,S.EX_DESC,                              ");
			strBuf.append("	(                                         ");
			strBuf.append("		SELECT                                  ");
			strBuf.append("			U1.NAME                               ");
			strBuf.append("		FROM                                    ");
			strBuf.append("			TF_F_USER U1                          ");
			strBuf.append("		WHERE                                   ");
			strBuf.append("			U1.USER_ID = S.DEAL_USERID            ");
			strBuf.append("	) AS DEAL_USER_NAME,                         ");
			strBuf.append("	(                                         ");
			strBuf.append("		SELECT                                  ");
			strBuf.append("			U2.NAME                               ");
			strBuf.append("		FROM                                    ");
			strBuf.append("			TF_F_USER U2                          ");
			strBuf.append("		WHERE                                   ");
			strBuf.append("			U2.USER_ID = S.INS_USERID             ");
			strBuf.append("	) AS INS_USER_NAME,                          ");
			strBuf.append("	(                                         ");
			strBuf.append("		SELECT                                  ");
			strBuf.append("			E1.ENUM_NAME                           ");
			strBuf.append("		FROM                                    ");
			strBuf.append("			td_s_enumerate E1                     ");
			strBuf.append("		WHERE                                   ");
			strBuf.append("			E1.SUBSYS_CODE = 'EX'                 ");
			strBuf.append("		AND E1.TABLE_CODE = 'EX_EXCEPTION'      ");
			strBuf.append("		AND E1.COL_CODE = 'S_TYPE'              ");
			strBuf.append("		AND E1.ENUM_CODE = S.S_TYPE             ");
			strBuf.append("	) AS B_TYPE_NAME,                            ");
			strBuf.append("	(                                         ");
			strBuf.append("		SELECT                                  ");
			strBuf.append("			E2.ENUM_CODE                          ");
			strBuf.append("		FROM                                    ");
			strBuf.append("			td_s_enumerate E2                     ");
			strBuf.append("		WHERE                                   ");
			strBuf.append("			E2.SUBSYS_CODE = 'SYS'                ");
			strBuf.append("		AND E2.TABLE_CODE = 'ALL'      ");
			strBuf.append("		AND E2.COL_CODE = 'S_TYPE_CLASS'        ");
			strBuf.append("		AND E2.ENUM_NAME = S.S_TYPE             ");
			strBuf.append("	) AS B_TYPE_CLASS                            ");
			strBuf.append(" FROM                                       ");
			strBuf.append("	"+TABLE_IPU_MEMBER+" S                          ");

			outParam = this.queryPaginationList(strBuf.toString(),params,outParam,keyList,new Pagination(8,5));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return outParam;
	}

	/**
	 * 修改成员信息
	 */
	public boolean updateInfos(IData data) throws Exception {
		boolean resultFlag = this.update("TABLE_IPU_MEMBER", data, new String[] { "STAFF_ID" });
		if (log.isDebugEnabled()) {
			log.debug(resultFlag ? "数据更新成功" : "数据更新失败");
		}
		this.commit();
		return resultFlag;
	}

	/**
	 * 新增成员信息
	 */
	public boolean insertInfos(IData data) throws Exception {
		boolean resultFlag = this.insert(TABLE_IPU_MEMBER_L, data);
		if (log.isDebugEnabled()) {
			log.debug(resultFlag ? "数据插入成功" : "数据插入失败");
		}
		return resultFlag;
	}

	
	/**
	 * 增加关键词
	 */
	public boolean addHotkey(IData data) throws Exception {

		StringBuffer updateSql = new StringBuffer();
		updateSql.append("update td_s_hotkey  a set a.TIMES=a.TIMES+1 where a.KEYWORD=:KEYWORD");
        int cnt = this.executeUpdate(updateSql.toString(),data);
        int cnt1=0; 
       if(cnt==0)
       {
    	StringBuffer insertSql = new StringBuffer();
    	insertSql.append("insert into td_s_hotkey(KEYWORD,TIMES,SUBSYS_CODE) values(:KEYWORD,'0','EX')");
    	cnt1=this.executeUpdate(insertSql.toString(),data);
       }
		
		
		return (cnt!=0||cnt1!=0)?true:false;
	}
	
	/**
	 * 删除成员信息
	 */
	public boolean deleteInfos(IData data) throws Exception {
		boolean resultFlag = this.delete("TABLE_IPU_MEMBER", data, new String[] { "STAFF_ID" });
		if (log.isDebugEnabled()) {
			log.debug(resultFlag ? "数据删除成功" : "数据删除失败");
		}
		return resultFlag;
	}
	public IDataset getUnDealErrDtl(IData param) throws Exception {
		IDataset infos = new DatasetList();
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SELECT                                     ");
		strBuf.append("	S.EX_DESC,S.EX_ABSTRACT,S.EX_ID,EX_REASON,EX_SOLUTION,LOG_ID,      ");
		strBuf.append("	S.DEAL_TIME,                              ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			U1.NAME                               ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			TF_F_USER U1                          ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			U1.USER_ID = S.DEAL_USERID            ");
		strBuf.append("	) AS DEAL_USER_NAME,                         ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			U2.NAME                               ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			TF_F_USER U2                          ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			U2.USER_ID = S.INS_USERID             ");
		strBuf.append("	) INS_USER_NAME,                          ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			E1.ENUM_NAME                           ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			td_s_enumerate E1                     ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			E1.SUBSYS_CODE = 'EX'                 ");
		strBuf.append("		AND E1.TABLE_CODE = 'EX_EXCEPTION'      ");
		strBuf.append("		AND E1.COL_CODE = 'S_TYPE'              ");
		strBuf.append("		AND E1.ENUM_CODE = S.S_TYPE             ");
		strBuf.append("	) B_TYPE_NAME,INS_USERID,                            ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			E2.ENUM_CODE                          ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			td_s_enumerate E2                     ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			E2.SUBSYS_CODE = 'SYS'                ");
		strBuf.append("		AND E2.TABLE_CODE = 'ALL'      ");
		strBuf.append("		AND E2.COL_CODE = 'S_TYPE_CLASS'        ");
		strBuf.append("		AND E2.ENUM_NAME = S.S_TYPE             ");
		strBuf.append("	) B_TYPE_CLASS,REWARD                            ");
		strBuf.append("FROM                                       ");
		strBuf.append(" "+TABLE_IPU_MEMBER_L+" S                          ");
		strBuf.append("	WHERE  EX_ID =:EX_ID ");

		infos = this.queryList(strBuf.toString(),param);
		return infos;
	}
	public IDataset selectDelInfos(IData param) throws Exception {
		IDataset infos = new DatasetList();
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SELECT                                     ");
		strBuf.append("	S.EX_DESC,S.EX_ABSTRACT,S.EX_ID,EX_REASON,EX_SOLUTION,      ");
		strBuf.append("	S.DEAL_TIME,                              ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			U1.NAME                               ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			TF_F_USER U1                          ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			U1.USER_ID = S.DEAL_USERID            ");
		strBuf.append("	) AS DEAL_USER_NAME,                         ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			U2.NAME                               ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			TF_F_USER U2                          ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			U2.USER_ID = S.INS_USERID             ");
		strBuf.append("	) INS_USER_NAME,                          ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			E1.ENUM_NAME                           ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			td_s_enumerate E1                     ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			E1.SUBSYS_CODE = 'EX'                 ");
		strBuf.append("		AND E1.TABLE_CODE = 'EX_EXCEPTION'      ");
		strBuf.append("		AND E1.COL_CODE = 'S_TYPE'              ");
		strBuf.append("		AND E1.ENUM_CODE = S.S_TYPE             ");
		strBuf.append("	) B_TYPE_NAME,                            ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			E2.ENUM_CODE                          ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			td_s_enumerate E2                     ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			E2.SUBSYS_CODE = 'SYS'                ");
		strBuf.append("		AND E2.TABLE_CODE = 'ALL'      ");
		strBuf.append("		AND E2.COL_CODE = 'S_TYPE_CLASS'        ");
		strBuf.append("		AND E2.ENUM_NAME = S.S_TYPE             ");
		strBuf.append("	) B_TYPE_CLASS                            ");
		strBuf.append("FROM                                       ");
		strBuf.append(" "+TABLE_IPU_MEMBER+" S                          ");
		strBuf.append("	WHERE  EX_ID =:EX_ID ");

		infos = this.queryList(strBuf.toString(),param);
		return infos;
	}
	public IData goQryUnsolved(IData param,IData outParam,String keyList) throws Exception {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SELECT                                     ");
		strBuf.append("	S.EX_DESC,S.EX_ABSTRACT,S.EX_ID,                    ");
		strBuf.append("	S.DEAL_TIME,                              ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			U1.NAME                               ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			TF_F_USER U1                          ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			U1.USER_ID = S.DEAL_USERID            ");
		strBuf.append("	) AS DEAL_USER_NAME,                         ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			U2.NAME                               ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			TF_F_USER U2                          ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			U2.USER_ID = S.INS_USERID             ");
		strBuf.append("	) INS_USER_NAME,                          ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			E1.ENUM_NAME                           ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			td_s_enumerate E1                     ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			E1.SUBSYS_CODE = 'EX'                 ");
		strBuf.append("		AND E1.TABLE_CODE = 'EX_EXCEPTION'      ");
		strBuf.append("		AND E1.COL_CODE = 'S_TYPE'              ");
		strBuf.append("		AND E1.ENUM_CODE = S.S_TYPE             ");
		strBuf.append("	) B_TYPE_NAME,                            ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			E2.ENUM_CODE                          ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			td_s_enumerate E2                     ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			E2.SUBSYS_CODE = 'SYS'                ");
		strBuf.append("		AND E2.TABLE_CODE = 'ALL'      ");
		strBuf.append("		AND E2.COL_CODE = 'S_TYPE_CLASS'        ");
		strBuf.append("		AND E2.ENUM_NAME = S.S_TYPE             ");
		strBuf.append("	) B_TYPE_CLASS                            ");
		strBuf.append("FROM                                       ");
		strBuf.append(" "+TABLE_IPU_MEMBER_L+" S                          ");
		strBuf.append("	WHERE  EX_DESC like '%"+param.getString("KEY_WORD","")+"%' ");
		strBuf.append("	AND  EX_STATE = :EX_STATE ");
		outParam = this.queryPaginationList(strBuf.toString(),param,outParam,keyList,"UNSOLVEDPAGE",new Pagination(8,5));
		return outParam;
	}
	public IData goQry(IData param,IData outParam,String keyList) throws Exception {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SELECT                                     ");
		strBuf.append("	S.EX_DESC,S.EX_ABSTRACT,S.EX_ID,                    ");
		strBuf.append("	S.DEAL_TIME,                              ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			U1.NAME                               ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			TF_F_USER U1                          ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			U1.USER_ID = S.DEAL_USERID            ");
		strBuf.append("	) AS DEAL_USER_NAME,                         ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			U2.NAME                               ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			TF_F_USER U2                          ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			U2.USER_ID = S.INS_USERID             ");
		strBuf.append("	) INS_USER_NAME,                          ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			E1.ENUM_NAME                           ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			td_s_enumerate E1                     ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			E1.SUBSYS_CODE = 'EX'                 ");
		strBuf.append("		AND E1.TABLE_CODE = 'EX_EXCEPTION'      ");
		strBuf.append("		AND E1.COL_CODE = 'S_TYPE'              ");
		strBuf.append("		AND E1.ENUM_CODE = S.S_TYPE             ");
		strBuf.append("	) B_TYPE_NAME,                            ");
		strBuf.append("	(                                         ");
		strBuf.append("		SELECT                                  ");
		strBuf.append("			E2.ENUM_CODE                          ");
		strBuf.append("		FROM                                    ");
		strBuf.append("			td_s_enumerate E2                     ");
		strBuf.append("		WHERE                                   ");
		strBuf.append("			E2.SUBSYS_CODE = 'SYS'                ");
		strBuf.append("		AND E2.TABLE_CODE = 'ALL'      ");
		strBuf.append("		AND E2.COL_CODE = 'S_TYPE_CLASS'        ");
		strBuf.append("		AND E2.ENUM_NAME = S.S_TYPE             ");
		strBuf.append("	) B_TYPE_CLASS                            ");
		strBuf.append("FROM                                       ");
		strBuf.append(" "+TABLE_IPU_MEMBER+" S                          ");
		strBuf.append("	WHERE  EX_DESC like '%"+param.getString("KEY_WORD","")+"%' ");
		strBuf.append("	AND  EX_STATE = :EX_STATE ");
		strBuf.append("	AND  SECURITY_LEVEL <= :SECURITY_LEVEL ");
		String chooseType=param.getString("CHOOSE_TYPE","empty");
		if(!"所有类别".equals(chooseType)&&!"".equals(chooseType)&&!"empty".equals(chooseType)){
			strBuf.append("	AND  SYS = :CHOOSE_TYPE ");
		}
		strBuf.append("	ORDER BY S.DEAL_TIME desc ");
		
		outParam = this.queryPaginationList(strBuf.toString(),param,outParam,keyList,"GOQRYPAGE",new Pagination(8,10));
		return outParam;
	}

	public IDataset getHotKey(IData param) throws Exception{
		
		return this.queryList("SELECT KEYWORD FROM TD_S_HOTKEY WHERE KEYWORD LIKE  '%"+param.getString("KEY_WORD")+"%' ", param);
	}
	
   public IData querySelectType(IData param) throws Exception
   {
		IData resultData = new DataMap();   
		IDataset BTypeList=this.queryList("select a.ENUM_CODE,a.ENUM_NAME  from td_s_enumerate a where a.COL_CODE='B_TYPE' and a.SUBSYS_CODE='EX' and a.TABLE_CODE='EX_EXCEPTION'", param);
		resultData.put("BTYPELIST", BTypeList);
		IDataset STypeList=this.queryList("select a.ENUM_CODE,a.ENUM_NAME  from td_s_enumerate a where a.COL_CODE='S_TYPE' and a.SUBSYS_CODE='EX' and a.TABLE_CODE='EX_EXCEPTION'", param);
		resultData.put("STYPELIST", STypeList);
		IDataset SEypeList=this.queryList("select a.ENUM_CODE,a.ENUM_NAME  from td_s_enumerate a where a.COL_CODE='SECURITY_LEVEL' and a.SUBSYS_CODE='EX' and a.TABLE_CODE='EX_EXCEPTION'", param);
		resultData.put("SECURITYLIST", SEypeList);
		return resultData;
	}

   public int updateErr(IData param) throws Exception{
	   String sql = "update "+TABLE_IPU_MEMBER_L+" set EX_REASON=:EX_REASON ,EX_SOLUTION=:EX_SOLUTION,EX_STATE=:EX_STATE";
	   if("1".equals(param.getString("EX_STATE"))){
		   sql +=",REVIEW_USERID=:REVIEW_USERID,REVIEW_PSCODE=:REVIEW_PSCODE"; 
	   }
	   if("2".equals(param.getString("EX_STATE"))){
		   sql +=",DEAL_USERID=:DEAL_USERID,DEAL_TIME=SYSDATE() "; 
	   }
	   sql +=" WHERE EX_ID=:EX_ID";
	   return this.executeUpdate(sql, param);
   }

public IDataset getErrDealInfos(IData param) throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("SELECT                                        ");
	strBuf.append("	COUNT(1) NUM,                                    ");
	strBuf.append("	(                                            ");
	strBuf.append("		SELECT                                     ");
	strBuf.append("			IFNULL(en.ENUM_NAME,'未知状态')                             ");
	strBuf.append("		FROM                                       ");
	strBuf.append("			td_s_enumerate en                        ");
	strBuf.append("		WHERE                                      ");
	strBuf.append("			en.COL_CODE = 'EX_STATE'                 ");
	strBuf.append("		AND en.TABLE_CODE = 'EX_EXCEPTION'         ");
	strBuf.append("		AND en.SUBSYS_CODE = 'EX'                  ");
	strBuf.append("		AND en.ENUM_CODE = err.EX_STATE            ");
	strBuf.append("	) EX_STATE_NAME                              ");
	strBuf.append("FROM                                          ");
	strBuf.append("	tf_f_exception err                           ");
	strBuf.append("GROUP BY                                      ");
	strBuf.append("	err.EX_STATE                                 ");
	
	return this.queryList(strBuf.toString(), param);
}

public IDataset getErrDealerInfos(IData param) throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("SELECT                          ");
	strBuf.append("	COUNT(1) NUM,                      ");
	strBuf.append("	(                              ");
	strBuf.append("		SELECT                       ");
	strBuf.append("			IFNULL(u.NAME,'')                     ");
	strBuf.append("		FROM                         ");
	strBuf.append("			tf_f_user u                ");
	strBuf.append("		WHERE                        ");
	strBuf.append("			u.USER_ID=err.DEAL_USERID  ");
	strBuf.append("	) EX_DEAL_NAME                 ");
	strBuf.append("FROM                            ");
	strBuf.append("	tf_f_exception err             ");
	strBuf.append("GROUP BY                        ");
	strBuf.append("	err.DEAL_USERID                ");
	
	return this.queryList(strBuf.toString(), param);
}

public IDataset getErrInserInfos(IData param) throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("SELECT                          ");
	strBuf.append("	COUNT(1) NUM,                      ");
	strBuf.append("	(                              ");
	strBuf.append("		SELECT                       ");
	strBuf.append("			IFNULL(u.NAME,'')                     ");
	strBuf.append("		FROM                         ");
	strBuf.append("			tf_f_user u                ");
	strBuf.append("		WHERE                        ");
	strBuf.append("			u.USER_ID=err.INS_USERID  ");
	strBuf.append("	) EX_INS_NAME                 ");
	strBuf.append("FROM                            ");
	strBuf.append("	tf_f_exception err             ");
	strBuf.append("GROUP BY                        ");
	strBuf.append("	err.INS_USERID                ");
	
	return this.queryList(strBuf.toString(), param);
}

public IDataset getErrTypeInfos(IData param) throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("SELECT                                                                                                               ");
	strBuf.append("	COUNT(1) NUM,                                                                                                       ");
	strBuf.append("	(                                                                                                                   ");
	strBuf.append("		SELECT                                                                                                            ");
	strBuf.append("			IFNULL(en.ENUM_NAME,'未知模块')                                                                                                    ");
	strBuf.append("		FROM                                                                                                              ");
	strBuf.append("			td_s_enumerate en                                                                                               ");
	strBuf.append("		WHERE                                                                                                             ");
	strBuf.append("			en.SUBSYS_CODE='EX' and en.TABLE_CODE='EX_EXCEPTION' and en.COL_CODE='B_TYPE' and en.ENUM_CODE=err.B_TYPE       ");
	strBuf.append("	) EX_TYPE,CASE WHEN B_TYPE ='EX' THEN '#faa819' WHEN B_TYPE ='EQ' THEN '#31b5ee' WHEN B_TYPE ='ES' THEN '#fa451a' END AS COLOR                                                                                                           ");
	strBuf.append("FROM                                                                                                                 ");
	strBuf.append("	tf_f_exception err                                                                                                  ");
	strBuf.append("GROUP BY                                                                                                             ");
	strBuf.append("	err.B_TYPE                                                                                                          ");
	
	return this.queryList(strBuf.toString(), param);
}

public IDataset getDomain(IData param) throws Exception{
	// TODO Auto-generated method stub
	return this.queryList("SELECT ENUM_CODE FROM TD_S_ENUMERATE WHERE SUBSYS_CODE= 'SYS' AND COL_CODE='SYS_DO' ", param);
}

public void insertL2Err(IData param)  throws Exception{
	String sql = "insert into "+TABLE_IPU_MEMBER+"(EX_ID,SYS,B_TYPE,S_TYPE,EX_DESC,EX_PICLOC,EX_REASON,EX_SOLUTION,EX_ABSTRACT,KEY_LIST,EX_STATE,INS_USERID,INS_TIME,DEAL_USERID,DEAL_TIME,REVIEW_USERID,REVIEW_PSCODE,REMARK) SELECT  EX_ID,SYS,B_TYPE,S_TYPE,EX_DESC,EX_PICLOC,EX_REASON,EX_SOLUTION,EX_ABSTRACT,KEY_LIST,EX_STATE,INS_USERID,INS_TIME,DEAL_USERID,DEAL_TIME,REVIEW_USERID,REVIEW_PSCODE,REMARK FROM "+TABLE_IPU_MEMBER_L+" WHERE EX_ID=:EX_ID";
	
	this.executeUpdate(sql, param);
}

public IDataset getUnsolvedTypeList(IData param) throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("SELECT DISTINCT                                             ");
	strBuf.append("	(                                                          ");
	strBuf.append("		SELECT                                                  ");
	strBuf.append("			concat(B.ENUM_NAME, '类问题')                        ");
	strBuf.append("		FROM                                                    ");
	strBuf.append("			td_s_enumerate B                                    ");
	strBuf.append("		WHERE                                                   ");
	strBuf.append("			B.COL_CODE = 'B_TYPE'                               ");
	strBuf.append("		AND B.ENUM_CODE = A.B_TYPE                              ");
	strBuf.append("	) EX_TYPE_DESC,                                            ");
	strBuf.append("	A.B_TYPE EX_TYPE_CODE                                      ");
	strBuf.append("FROM                                                        ");
	strBuf.append("	tf_l_exception A                                           ");
	strBuf.append("WHERE  1=1                                                  ");
	strBuf.append("AND	SYS = :SYS                                             ");
	strBuf.append("AND EX_STATE = '1'                                          ");
	return  this.queryList(strBuf.toString(), param);
}

public IDataset getUnsolvedInfosByType(IData unsolvedType) throws Exception{
	// TODO Auto-generated method stub
	String sql = "select * from tf_l_exception a where a.EX_STATE = '1' and SYS = 'N6' and B_TYPE=:EX_TYPE_CODE order by REWARD ";
	return this.queryList(sql, unsolvedType);
}

public void deleteErr(IData param) throws Exception{
	// TODO Auto-generated method stub
	String sql = "DELETE FROM "+TABLE_IPU_MEMBER_L+" WHERE LOG_ID = :LOG_ID";
	this.executeUpdate(sql, param);
}


public IData queryErrByUser(IData param,IData outParam,String keylist,String pageKey) throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("select '1' as pub,a.EX_ID,left(a.EX_ABSTRACT,15) as EX_ABSTRACT from tf_f_exception a where ");
	if(!StringUtil.isNull((String)param.get("USER_ID")))
	{
		 strBuf.append("a.INS_USERID=:USER_ID");
	}
	if(!StringUtil.isNull((String)param.get("DEAL_USERID")))
	{
		 strBuf.append("a.DEAL_USERID=:DEAL_USERID");
	}
	
	strBuf.append("	UNION  ");
	strBuf.append("select NULL as pub,b.EX_ID,left(b.EX_ABSTRACT,15) as EX_ABSTRACT  from tf_l_exception b where EX_STATE='1' ");
	
	if(!StringUtil.isNull((String)param.get("USER_ID")))
	{
		 strBuf.append(" and b.INS_USERID=:USER_ID");
	}
	if(!StringUtil.isNull((String)param.get("DEAL_USERID")))
	{
		 strBuf.append(" and b.DEAL_USERID=:DEAL_USERID");
	}
	return  this.queryPaginationList(strBuf.toString(), param,outParam,keylist,pageKey,new Pagination(3,5));
}

public IDataset getERRTOP10(IData param) throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("select count(INS_USERID) COU,(SELECT NAME FROM tf_f_user u WHERE  u.USER_ID = a.INS_USERID) NAME from tf_f_exception a GROUP BY INS_USERID ORDER BY  COU desc LIMIT 0,10");
	return this.queryList(strBuf.toString(), param);
}

public IDataset getTOP10(IData param) throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("select count(INS_USERID) COU,(SELECT NAME FROM tf_f_user u WHERE  u.USER_ID = a.DEAL_USERID) NAME from tf_f_exception a GROUP BY DEAL_USERID ORDER BY  COU desc LIMIT 0,10");
	return this.queryList(strBuf.toString(), param);
}

public IDataset getActiveList(IData param)throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("select count(USER_ID) COU,(SELECT NAME FROM tf_f_user u WHERE  u.USER_ID = a.USER_ID) NAME,USER_ID from tf_l_userlog a WHERE USER_ID IS NOT NULL GROUP BY USER_ID ORDER BY  COU desc LIMIT 0,10");
	return this.queryList(strBuf.toString(), param);
}

public IDataset gethotList(IData param) throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("SELECT DATE_FORMAT(IN_TIME,'%Y-%m-%d') AS NAME,COUNT(1) AS COU  from tf_l_userlog WHERE DATE_FORMAT(IN_TIME,'%Y-%m-%d') BETWEEN DATE_FORMAT(NOW()-7,'%Y-%m-%d') AND DATE_FORMAT(NOW(),'%Y-%m-%d') GROUP BY NAME ");
	return this.queryList(strBuf.toString(), param);
}

public IDataset mydealTypeInfos(IData param) throws Exception{
	StringBuffer strBuf = new StringBuffer();
	strBuf.append("SELECT                                                                                                           ");
	strBuf.append("	count(1) COU,                                                                                                   ");
	strBuf.append("	(                                                                                                               ");
	strBuf.append("		SELECT                                                                                                        ");
	strBuf.append("			IFNULL(en.ENUM_NAME,'未知模块')                                                                             ");
	strBuf.append("		FROM                                                                                                          ");
	strBuf.append("			td_s_enumerate en                                                                                           ");
	strBuf.append("		WHERE                                                                                                         ");
	strBuf.append("			en.SUBSYS_CODE='EX' and en.TABLE_CODE='EX_EXCEPTION' and en.COL_CODE='S_TYPE' and en.ENUM_CODE=A.S_TYPE     ");   
	strBuf.append("	) NAME                                                                                                          ");
	strBuf.append("FROM                                                                                                             ");
	strBuf.append("	tf_f_exception a                                                                                                ");
	strBuf.append("	WHERE INS_USERID = :USERID                                                                           ");
	strBuf.append("	OR DEAL_USERID = :USERID                                                                             ");
	strBuf.append("GROUP BY                                                                                                         ");
	strBuf.append("	S_TYPE                                                                                                          ");
	strBuf.append("ORDER BY                                                                                                         ");
	strBuf.append("	COU DESC                                                                                                        ");
	return this.queryList(strBuf.toString(), param);
}

public String getCareMan(IData param) throws Exception{
	// TODO Auto-generated method stub
	IDataset infos= this.queryList("select (SELECT NAME FROM tf_f_user u WHERE  u.USER_ID = d.DEAL_USERID) NAME,CNT from(SELECT count(1) CNT,DEAL_USERID FROM tf_f_exception e WHERE e.INS_USERID = :USERID  GROUP BY DEAL_USERID) d ORDER BY CNT DESC", param);
	if(infos.size()==0){
		return "未提问";
	}
	return infos.first().getString("NAME");
}


}
