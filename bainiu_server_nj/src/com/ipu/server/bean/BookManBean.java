package com.ipu.server.bean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.BookManDao;
import com.ipu.server.dao.RightDao;
import com.ipu.server.util.SeqMaker;
import com.ipu.server.util.randomNum;
/**
 * 
 * @ClassName: BookManBean
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhangjq6
 * @date 2017-11-28 上午9:28:47
 */
public class BookManBean extends AppBean{
	/**
	 * 
	 * @Title: init
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author zhangjq6
	 * @date  2017-11-28 上午9:28:31
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
	 * @Title: queryBook
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author zhangjq6
	 * @date  2017-12-18 上午9:26:33
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData queryBook(IData param) throws Exception{
		IData resultData = getResultData();
		BookManDao bookDao = new BookManDao("bainiu");
		String userId = "";
		userId = getContextData().getUserID();
		param.put("USER_ID", userId);
		resultData = bookDao.queryBook(param, resultData, "BOOOKSLIST");
		//工号具有书籍状态操作权限
		RightDao rightDao = new RightDao("bainiu");
		if(rightDao.queryUserRight(userId,"DATA_BOOKMAN_OPER")){
			resultData.put("BOOKOPERATERIGHT","1");
		}
		doActionLog(param);
		return resultData;
	}
	
	/**
	 * 
	 * @Title: qryLoanHis
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author zhangjq6
	 * @date  2017-12-18 上午9:26:33
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData qryLoanHis(IData param) throws Exception{
		IData resultData = getResultData();
		BookManDao bookDao = new BookManDao("bainiu");

		resultData = bookDao.qryLoanHis(param, resultData, "LOANLIST");

		doActionLog(param);
		return resultData;
	}
	/**
	 * 
	 * @Title: loanBook
	 * @Description: TODO(借书方法)
	 * @author zhangjq6
	 * @date  2017-12-16 下午1:09:14
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData loanBook(IData param) throws Exception{
		IData resultData = getResultData();
		BookManDao bookDao = new BookManDao("bainiu");
		String userId = getContextData().getUserID();
		IData params = new DataMap();
		IData qBook = new DataMap();
		//增加查询库存校验
		params.clear();
		params.put("BOOK_ID", param.getString("BOOK_ID"));
		bookDao.queryBook(params, resultData, "BOOKINFO");
		qBook = (IData)((IDataset)resultData.get("BOOKINFO")).get(0);
		if(qBook.getInt("COPY") == 0){
			resultData.put("resultInfo", "对不起，【"+qBook.getString("BOOK_NAME")+"】库存为【"+qBook.getInt("COPY")+"】,图书状态为【"+qBook.getString("STATE")+"】,无法成功借阅");
			resultData.put("result", "0");
			return resultData;
		}
		IData result = new DataMap();
		result = updateBook(param);
		
		//更新书籍状态成功后插入tf_f_user_loan表
		if("1".equals(result.getString("result"))){			
			params.clear();
			params.put("USER_ID", userId);
			params.put("BOOK_ID", param.getString("BOOK_ID"));
			params.put("USER_ACCT", getContextData().getUserAcct());
			SeqMaker seqMaker = new SeqMaker();
			String loanSeq = seqMaker.getSeqStr(3);
			params.put("LOAN_SEQ", loanSeq);
			params.put("PRE_BACK_TIME", param.getString("PRE_BACK_TIME"));
			params.put("BOOK_NAME", param.getString("BOOK_NAME"));
			resultData = bookDao.insertLoanInfo(params, resultData);
			if("1".equals(resultData.getString("result"))){
				resultData.put("resultInfo", "记录借阅信息错误，请联系管理员！");
			}
			doActionLog(param);
			return resultData;
		}else{
			resultData.put("resultInfo", "更新书籍状态错误，请联系管理员！");
			doActionLog(param);
			return resultData;
		}
		
	}
	/**
	 * 
	 * @Title: updateBook
	 * @Description: TODO(更新书籍状态以及库存状态)
	 * @author zhangjq6
	 * @date  2017-12-16 下午4:47:36
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public  IData updateBook(IData param) throws Exception{
 		BookManDao bookDao = new BookManDao("bainiu");
		String userId = getContextData().getUserID();
		//操作 0:借书  1：还书  2：删除
		String operType = param.getString("OPER_TYPE");
		//状态 0：在架 1：借出  2：遗失
		String nextState = "";
		if("0".equals(operType)){
			nextState = "1";
		}else if("1".equals(operType)){
			nextState = "0";
		}else if("2".equals(operType)){
			nextState = "2";
		}
		IData params = new DataMap();
		IData result = new DataMap();
		IData resultData = getResultData();
		IData qBook = new DataMap();
		
		//ORG_COPY>=COPY 如果库存信息已满，还书操作就不做了
		params.clear();
		params.put("BOOK_ID", param.getString("BOOK_ID"));
		bookDao.queryBook(params, resultData, "BOOKINFO");
		qBook = (IData)((IDataset)resultData.get("BOOKINFO")).get(0);
		if(operType.equals("1") && (qBook.getInt("COPY") == qBook.getInt("ORG_COPY"))){
			result.put("result", "0");
			result.put("resultInfo", "所选择书籍已全部归还");
			return result;
		}
		
		params.clear();
		params.put("USER_ID", userId);
		params.put("STATE", nextState);
		params.put("BOOK_ID", param.getString("BOOK_ID"));
		
		result = bookDao.updateBook(params, result);
		doActionLog(param);
		return result;
	}
	/**
	 * 
	 * @Title: backBook
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author zhangjq6
	 * @date  2017-12-27 下午7:58:13
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public IData backBook(IData inparam) throws Exception{
		IData resultData = getResultData();
		BookManDao bookDao = new BookManDao("bainiu");
		IData param = new DataMap();
		int count = 0;
		IDataset books = (IDataset) inparam.get("BOOK_LIST");
		String resultInfo = "";
		for(int i=0;i<books.size();i++){
			String bookID = ((IData)books.get(i)).getString("BOOK_ID");
			param.put("OPER_TYPE", inparam.getString("OPER_TYPE"));
			param.put("BOOK_ID", bookID);
			IData result = updateBook(param);
			if(result.getInt("result")> 0 ){
				param.put("OPER_USERID", getContextData().getUserID());
				bookDao.updateLoanHis(param);
			}
			resultInfo = result.getString("resultInfo");
			count+=result.getInt("result");
		}
		if(count==0)
			resultData.put("resultInfo", resultInfo);
		resultData.put("result", count);
		doActionLog(param);
		return resultData;
	}
	
	public IData createBook(IData inparam) throws Exception{
		IData resultData = getResultData();
		BookManDao bookDao = new BookManDao("bainiu");
		randomNum rand = new randomNum();
		String bookID = rand.generateNumber(8);
		inparam.put("BOOK_ID", bookID);
		inparam.put("STATE", "0");
		inparam.put("UPD_USERID", getContextData().getUserID());
		inparam.put("BOOK_ADMINID", getContextData().getUserID());
		resultData = bookDao.insertBook(inparam,resultData);
		doActionLog(inparam);
		return resultData;
	}
}
