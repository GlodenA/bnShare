package com.ipu.server.dao;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.mobile.db.dao.impl.BaseDAO;
import com.ipu.server.util.Pagination;

public class CommentDao extends SmartBaseDao {
	private static transient Logger log = Logger.getLogger(IpuMemberInfoDao.class);
	static String TABLE_IPU_MEMBER = "tf_f_suggestion";

	public CommentDao(String connName) throws Exception {
		super(connName);
	}

	public void insertComment(IData param) throws Exception{
		// TODO Auto-generated method stub
		this.insert(TABLE_IPU_MEMBER, param);
	}

	public IData getCommentInfo(IData params,IData outParam,String keyList) throws Exception{
		// TODO Auto-generated method stub
		outParam = this.queryPaginationList("SELECT S.*,left(CONTENT_REQ,15) as CONTENT_REQ_SBU,IFNULL((SELECT U1.NAME FROM TF_F_USER U1 WHERE U1.USER_ACCT = S.WHO_REQ ),'游客') as  REQ_NAME FROM "+TABLE_IPU_MEMBER+" S  order by WHEN_REQ DESC ",params,outParam,keyList,new Pagination(5,3));
		return outParam;
	}


	public void insReply(IData param) throws Exception{
		// TODO Auto-generated method stub
		String sql = "UPDATE tf_f_suggestion SET CONTENT_REQP=:CONTENT_REQP,STATE =:STATE,WHO_RESP=:WHO_RESP,WHEN_RESP=:WHEN_RESP  WHERE KEYID= :KEYID";
		this.executeUpdate(sql, param);
	}
	
}
