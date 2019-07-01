package com.ipu.server.dao;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.mobile.db.dao.impl.BaseDAO;

public class IpuMemberInfoDao extends BaseDAO {
	private static transient Logger log = Logger.getLogger(IpuMemberInfoDao.class);
	static String TABLE_IPU_MEMBER = "table_ipu_member";

	public IpuMemberInfoDao(String connName) throws Exception {
		super(connName);
	}

	/**
	 * 查询成员信息
	 */
	public DatasetList selectInfos(IData params) {
		DatasetList infos = new DatasetList();
		try {
			IData datas = this.queryList(
					"select * from " + TABLE_IPU_MEMBER + " s where s.STAFF_ID = :STAFF_ID",
					params).first();

			infos.add(datas);
			if (log.isDebugEnabled()) {
				log.debug("查询到的数据为" + datas == null ? "null" : datas.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return infos;
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
		boolean resultFlag = this.insert("TABLE_IPU_MEMBER", data);
		if (log.isDebugEnabled()) {
			log.debug(resultFlag ? "数据插入成功" : "数据插入失败");
		}
		return resultFlag;
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

}
