package com.ipu.server.dao;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.mobile.db.dao.impl.BaseDAO;

public class LoginDao extends SmartBaseDao {
	private static transient Logger log = Logger.getLogger(IpuMemberInfoDao.class);
	
	public LoginDao(String connName) throws Exception {
		super(connName);
	}

	public IData getUserInfo(IData param) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM TF_F_USER WHERE USER_ACCT=:USER_ACCT");
		if(!(null==param.get("PASSWORD")||param.getString("PASSWORD").trim().isEmpty()))
		{
			sb.append(" and PASSWORD=:PASSWORD");
		}		
		return this.queryList(sb.toString(), param).first();
	}

	public IDataset getRightInfos(IData param) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM TD_S_RIGHT R  WHERE 1=1 AND EXISTS (SELECT RIGHT_CODE FROM                  ");
		sb.append("(SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0'       ");
		sb.append("	UNION                                                                                    ");
		sb.append("	SELECT MUM_CODE AS RIGHT_CODE FROM td_m_role_mum WHERE ROLE_CODE IN(                     ");
		sb.append("	SELECT RIGHT_CODE FROM td_m_role RO WHERE RIGHT_CODE IN (                                ");
		sb.append("	SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0')      ");
		sb.append("	)                                                                                        ");
		sb.append(")RI WHERE RI.RIGHT_CODE = R.RIGHT_CODE)                                                   ");
		return this.queryList(sb.toString(), param);
	}

	public IDataset getQuestionRightInfos(IData param) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT R.*,IF(R.UNDONUMFUN = 'UNDOERR',(SELECT COUNT(1) FROM tf_l_exception WHERE EX_STATE='1'),                       ");
		sb.append("						IF(R.UNDONUMFUN = 'UNDORIGHT',(SELECT COUNT(1) FROM tf_f_user_right WHERE STATE='0'),NULL)) AS UNDO_NUM,    ");
		sb.append("  IF(R.IS_GETUNDONUM=1,'am-badge am-badge-secondary am-margin-right am-fr','display:none') AS UNDO_CLASS               ");
		sb.append(" FROM TD_S_RIGHT R  WHERE 1=1 AND EXISTS (SELECT RIGHT_CODE FROM                                                       ");
		sb.append("(SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0'                                    ");
		sb.append("	UNION                                                                                                                 ");
		sb.append("	SELECT MUM_CODE AS RIGHT_CODE FROM td_m_role_mum WHERE ROLE_CODE IN(                                                  ");
		sb.append("	SELECT RIGHT_CODE FROM td_m_role RO WHERE RIGHT_CODE IN (                                                             ");
		sb.append("	SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0')                                   ");
		sb.append("	)                                                                                                                     ");
		sb.append(")RI WHERE RI.RIGHT_CODE = R.RIGHT_CODE)                                                                                ");
		sb.append("AND R.RIGHT_TYPE = 'FUN'                                                                                               ");
		sb.append("AND R.PARENT_MODCODE = 'question-nav';                                                                                 ");
		return this.queryList(sb.toString(), param);
	}

	public IDataset getSysmanmRightInfos(IData param) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT R.*,IF(R.UNDONUMFUN = 'UNDOERR',(SELECT COUNT(1) FROM tf_l_exception WHERE EX_STATE='1'),                       ");
		sb.append("						IF(R.UNDONUMFUN = 'UNDORIGHT',(SELECT COUNT(1) FROM tf_f_user_right WHERE STATE='0'),NULL)) AS UNDO_NUM,    ");
		sb.append("  IF(R.IS_GETUNDONUM=1,'am-badge am-badge-secondary am-margin-right am-fr','display:none') AS UNDO_CLASS               ");
		sb.append(" FROM TD_S_RIGHT R  WHERE 1=1 AND EXISTS (SELECT RIGHT_CODE FROM                                                       ");
		sb.append("(SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0'                                    ");
		sb.append("	UNION                                                                                                                 ");
		sb.append("	SELECT MUM_CODE AS RIGHT_CODE FROM td_m_role_mum WHERE ROLE_CODE IN(                                                  ");
		sb.append("	SELECT RIGHT_CODE FROM td_m_role RO WHERE RIGHT_CODE IN (                                                             ");
		sb.append("	SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0')                                   ");
		sb.append("	)                                                                                                                     ");
		sb.append(")RI WHERE RI.RIGHT_CODE = R.RIGHT_CODE)                                                                                ");
		sb.append("AND R.RIGHT_TYPE = 'FUN'                                                                                               ");
		sb.append("AND R.PARENT_MODCODE = 'sysman-nav';                                                                                   ");
		return this.queryList(sb.toString(), param);
	}

	public void insertVistBrower(IData param) throws Exception{
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("update tf_f_browservist set COUNTP= COUNTP+1 WHERE BROWSER = :BROWSER ");
		this.executeUpdate(sb.toString(), param);
	}

	public IData getbrowserInfo(IData param) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT 	(SELECT COUNTP FROM tf_f_browservist WHERE BROWSER='Google Chrome') AS GoogleChrome,  ");
		sb.append("(SELECT COUNTP FROM tf_f_browservist WHERE BROWSER='Internet Explorer') AS InternetExplorer,   ");
		sb.append("(SELECT COUNTP FROM tf_f_browservist WHERE BROWSER='Mozilla Firefox') AS MozillaFirefox,       ");
		sb.append("(SELECT COUNTP FROM tf_f_browservist WHERE BROWSER='Opera') AS Opera,                          ");
		sb.append("(SELECT COUNTP FROM tf_f_browservist WHERE BROWSER='Safari') AS Safari                         ");
		sb.append(" FROM tf_f_browservist  A WHERE BROWSER='Google Chrome'                                        ");

		return this.queryList(sb.toString(), param).first();
	}

	public IDataset getToolsRightInfos(IData param) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT R.*,IF(R.UNDONUMFUN = 'UNDOERR',(SELECT COUNT(1) FROM tf_l_exception WHERE EX_STATE='1'),                       ");
		sb.append("						IF(R.UNDONUMFUN = 'UNDORIGHT',(SELECT COUNT(1) FROM tf_f_user_right WHERE STATE='0'),NULL)) AS UNDO_NUM,    ");
		sb.append("  IF(R.IS_GETUNDONUM=1,'am-badge am-badge-secondary am-margin-right am-fr','display:none') AS UNDO_CLASS               ");
		sb.append(" FROM TD_S_RIGHT R  WHERE 1=1 AND EXISTS (SELECT RIGHT_CODE FROM                                                       ");
		sb.append("(SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0'                                    ");
		sb.append("	UNION                                                                                                                 ");
		sb.append("	SELECT MUM_CODE AS RIGHT_CODE FROM td_m_role_mum WHERE ROLE_CODE IN(                                                  ");
		sb.append("	SELECT RIGHT_CODE FROM td_m_role RO WHERE RIGHT_CODE IN (                                                             ");
		sb.append("	SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0')                                   ");
		sb.append("	)                                                                                                                     ");
		sb.append(")RI WHERE RI.RIGHT_CODE = R.RIGHT_CODE)                                                                                ");
		sb.append("AND R.RIGHT_TYPE = 'FUN'                                                                                               ");
		sb.append("AND R.PARENT_MODCODE = 'tools-nav';                                                                                   ");
		return this.queryList(sb.toString(), param);
	}

	public IDataset getPMRightInfos(IData param) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT R.*,IF(R.UNDONUMFUN = 'UNDOERR',(SELECT COUNT(1) FROM tf_l_exception WHERE EX_STATE='1'),                       ");
		sb.append("						IF(R.UNDONUMFUN = 'UNDORIGHT',(SELECT COUNT(1) FROM tf_f_user_right WHERE STATE='0'),NULL)) AS UNDO_NUM,    ");
		sb.append("  IF(R.IS_GETUNDONUM=1,'am-badge am-badge-secondary am-margin-right am-fr','display:none') AS UNDO_CLASS               ");
		sb.append(" FROM TD_S_RIGHT R  WHERE 1=1 AND EXISTS (SELECT RIGHT_CODE FROM                                                       ");
		sb.append("(SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0'                                    ");
		sb.append("	UNION                                                                                                                 ");
		sb.append("	SELECT MUM_CODE AS RIGHT_CODE FROM td_m_role_mum WHERE ROLE_CODE IN(                                                  ");
		sb.append("	SELECT RIGHT_CODE FROM td_m_role RO WHERE RIGHT_CODE IN (                                                             ");
		sb.append("	SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0')                                   ");
		sb.append("	)                                                                                                                     ");
		sb.append(")RI WHERE RI.RIGHT_CODE = R.RIGHT_CODE)                                                                                ");
		sb.append("AND R.RIGHT_TYPE = 'FUN'                                                                                               ");
		sb.append("AND R.PARENT_MODCODE = 'pm-nav';                                                                                   ");
		return this.queryList(sb.toString(), param);
	}

	public IDataset getBPSRightInfos(IData param) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT R.*,IF(R.UNDONUMFUN = 'UNDOERR',(SELECT COUNT(1) FROM tf_l_exception WHERE EX_STATE='1'),                       ");
		sb.append("						IF(R.UNDONUMFUN = 'UNDORIGHT',(SELECT COUNT(1) FROM tf_f_user_right WHERE STATE='0'),NULL)) AS UNDO_NUM,    ");
		sb.append("  IF(R.IS_GETUNDONUM=1,'am-badge am-badge-secondary am-margin-right am-fr','display:none') AS UNDO_CLASS               ");
		sb.append(" FROM TD_S_RIGHT R  WHERE 1=1 AND EXISTS (SELECT RIGHT_CODE FROM                                                       ");
		sb.append("(SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0'                                    ");
		sb.append("	UNION                                                                                                                 ");
		sb.append("	SELECT MUM_CODE AS RIGHT_CODE FROM td_m_role_mum WHERE ROLE_CODE IN(                                                  ");
		sb.append("	SELECT RIGHT_CODE FROM td_m_role RO WHERE RIGHT_CODE IN (                                                             ");
		sb.append("	SELECT RIGHT_CODE FROM TF_F_USER_RIGHT UR WHERE USER_ID = :USER_ID AND USE_TAG='0')                                   ");
		sb.append("	)                                                                                                                     ");
		sb.append(")RI WHERE RI.RIGHT_CODE = R.RIGHT_CODE)                                                                                ");
		sb.append("AND R.RIGHT_TYPE = 'FUN'                                                                                               ");
		sb.append("AND R.PARENT_MODCODE = 'bps-nav';                                                                                   ");
		return this.queryList(sb.toString(), param);
	}

	public void loginOut(IData param) throws Exception{
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("update tf_l_userlog set OUT_TIME = :OUT_TIME WHERE SESSION_ID = :SESSION_ID ");
		this.executeUpdate(sb.toString(), param);
	}

	public boolean isThisDayfristLogin(IData param) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("select COUNT(1) LOGIN_TIME from tf_l_userlog where user_id=:USER_ID AND TO_DAYS(NOW()) = TO_DAYS(IN_TIME) AND ACTION_CODE='Login.doLogin' ");
		return this.queryList(sb.toString(), param).first().getInt("LOGIN_TIME")==0;
	}

	public Boolean addNB(IData buf) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE TF_F_USER SET NB=NB+:NB WHERE USER_ID= :USER_ID ");

		return this.executeUpdate(sb.toString(), buf)>=0;
	}

	public String getMyranking(IData param) throws Exception{
		String countSql = "select count(1) cnt from tf_f_exception where INS_USERID=:USERID";
		int cnt = this.queryList(countSql, param).getData(0).getInt("cnt");
		if(cnt>0){
		param.put("CNT", cnt);
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) ROW  from  ( SELECT   ");
		sb.append("	INS_USERID,cnt                  ");
		sb.append("FROM                             ");
		sb.append("	(                               ");
		sb.append("		SELECT                        ");
		sb.append("			a.INS_USERID,               ");
		sb.append("			count(a.INS_USERID) cnt     ");
		sb.append("		FROM                          ");
		sb.append("			tf_f_exception a            ");
		sb.append("		GROUP BY                      ");
		sb.append("			a.INS_USERID                ");
		sb.append("		ORDER BY                      ");
		sb.append("			a.INS_USERID                ");
		sb.append("	) b                             ");
		sb.append("ORDER BY cnt desc ) c            ");
		sb.append("where c.cnt>=:CNT                ");
		int row = this.queryList(sb.toString(), param).getData(0).getInt("ROW");
		return "第"+row+"名";
		}
		return "未提问";
	}

	public String getActNum(IData param) throws Exception{
		// TODO Auto-generated method stub
		return this.queryList("SELECT COUNT(1) CNT FROM TF_L_USERLOG WHERE USER_ID=:USERID ", param).getData(0).getString("CNT");
	}

}
