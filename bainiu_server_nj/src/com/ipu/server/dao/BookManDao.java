package com.ipu.server.dao;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ipu.server.util.Pagination;
import com.ipu.server.util.StringUtil;

public class BookManDao extends SmartBaseDao{
	private static transient Logger log = Logger.getLogger(BookManDao.class);
	static String TABLE_NAME = "TD_B_BOOK";
	
	public BookManDao(String connName) throws Exception {
		super(connName);
		// TODO Auto-generated constructor stub
	}
	public IData queryBook(IData params, IData outParams, String keyList) throws Exception {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("SELECT BOOK_ID,BOOK_NAME,BOOK_ADMINID,(SELECT NAME FROM TF_F_USER WHERE USER_ID = A.BOOK_ADMINID) BOOK_ADMINNAME,BOOK_PUBLISH,OWNER_TEAM,ISBN,COPY,ORG_COPY,PRICE,BUYCNL,date_format(INS_TIME, '%Y/%m/%d %H:%i') INS_TIME,");
		strBuf.append("(case when a.copy != '0' then '在架' when a.copy='0' and a.state!='2' then '不可借' when a.state = '2' and a.copy='0' then '遗失' else null end) STATE,");
		strBuf.append("	(case when a.copy != '0' then '0' else NULL END) CAN_LOAN,(select count(1) from tf_f_user_loan where book_id = a.book_id) LOAN_SUM ");
		strBuf.append("FROM td_b_book a");
		strBuf.append(" WHERE 1=1 ");
		if(!"".equals(params.getString("BOOK_NAME")) && params.getString("BOOK_NAME") != null){
			strBuf.append("and A.BOOK_NAME like '%"+params.getString("BOOK_NAME")+"%' ");
		}
		if(!"".equals(params.getString("BOOK_COPY")) && params.getString("BOOK_COPY") != null){
			if(params.getString("BOOK_COPY").equals("0"))
				strBuf.append("and A.COPY > 0 ");
			else if(params.getString("BOOK_COPY").equals("1"))
				strBuf.append("and A.ORG_COPY > A.COPY ");
		}
		if(!"".equals(params.getString("BOOK_ID")) && params.getString("BOOK_ID") != null){
			strBuf.append("and A.BOOK_ID = "+params.getString("BOOK_ID"));
		}
		return this.queryPaginationList(strBuf.toString(), params, outParams, keyList, new Pagination(8, 6));
	}
	
	public IData qryLoanHis(IData params, IData outParams, String keyList) throws Exception {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("select (SELECT NAME FROM TF_F_USER WHERE USER_ID = A.USER_ID) NAME,date_format(LOAN_TIME, '%Y/%m/%d %H:%i') LOAN_TIME,");
		strBuf.append("date_format(PRE_BACK_TIME, '%Y/%m/%d %H:%i') PRE_BACK_TIME,");
		strBuf.append(" if(date_format(REAL_BACK_TIME, '%Y/%m/%d %H:%i') is NULL,'借阅中',date_format(REAL_BACK_TIME, '%Y/%m/%d %H:%i')) REAL_BACK_TIME,");
		strBuf.append("(SELECT NAME FROM TF_F_USER WHERE USER_ID = A.OPER_USERID) OPER_NAME,REMARK");
		strBuf.append(" FROM tf_f_user_loan a");
		strBuf.append(" WHERE 1=1 ");
		strBuf.append("and A.BOOK_ID = '"+params.getString("BOOK_ID")+"' ");
		strBuf.append(" order by A.LOAN_TIME DESC ");
		return this.queryPaginationList(strBuf.toString(), params, outParams, keyList, new Pagination(8, 6));
	}
	
	/**
	 * 
	 * @Title: updateBook
	 * @Description: TODO(更新书本状态以及库存，借书-1，还书+1,遗失不变)
	 * @author zhangjq6
	 * @date  2017-12-16 下午1:09:29
	 * @param params
	 * @param outParams
	 * @param keyList
	 * @return
	 * @throws Exception
	 */
	public IData updateBook(IData params, IData outParam) throws Exception {
		StringBuffer strBuf = new StringBuffer();
		int count=0;
		strBuf.append("UPDATE td_b_book A SET a.STATE=?,a.copy=if(?='2',copy,if(?='0',copy+1,copy-1)),a.UPD_TIME= now(),a.UPD_USERID=? where a.BOOK_ID=?");
		count = this.executeUpdate(strBuf.toString(), new Object[]{
			params.getString("STATE"),
			params.getString("STATE"),
			params.getString("STATE"),
			params.getString("USER_ID"),
			params.getString("BOOK_ID")
		});
		outParam.put("result", count);
		return outParam;
	}
	
	public IData insertLoanInfo(IData param,IData outparam) throws Exception{
		StringBuffer strBuf  = new StringBuffer();
		int count = 0;
		strBuf.append("INSERT INTO tf_f_user_loan (LOAN_SEQ,USER_ID,BOOK_ID,LOAN_TIME,PRE_BACK_TIME)");
		strBuf.append("SELECT ?,?,?,NOW(),? FROM dual");
		count = this.executeUpdate(strBuf.toString(),new Object[]{
			param.getString("LOAN_SEQ"),
			param.getString("USER_ID"),
			param.getString("BOOK_ID"),
			param.getString("PRE_BACK_TIME")
		});
		outparam.put("result", count);
		return outparam;
	}
	public IData insertBook(IData inparam, IData result) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf  = new StringBuffer();
		int count = 0;
		strBuf.append("insert into td_b_book values (?,?,?,?,?,?,?,NOW(),?,?,?,?,?,NOW(),?)");
		count = this.executeUpdate(strBuf.toString(),new Object[]{
			inparam.getString("BOOK_ID"),
			inparam.getString("BOOK_NAME"),
			inparam.getString("BOOK_AUTHOR"),
			inparam.getString("BOOK_PUBLISH"),
			inparam.getString("ISBN"),
			inparam.getString("PRICE"),
			inparam.getString("BUYCNL"),
			//
			inparam.getString("COPY"),
			inparam.getString("COPY"),
			inparam.getString("STATE"),
			inparam.getString("BOOK_ADMINID"),
			inparam.getString("OWNER_TEAM"),
			//
			inparam.getString("UPD_USERID")
		});
		result.put("result", count);
		return result;
	}
	public void updateLoanHis(IData param) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strBuf = new StringBuffer();
		int count=0;
		strBuf.append("update tf_f_user_loan set REAL_BACK_TIME= now(),OPER_USERID=? where book_id = ?  and REAL_BACK_TIME is null");//and user_id =?
		count = this.executeUpdate(strBuf.toString(),new Object[]{
			param.getString("OPER_USERID"),
			param.getString("BOOK_ID")/*,
			param.getString("USER_ID")*/
		});
		
	}
}
