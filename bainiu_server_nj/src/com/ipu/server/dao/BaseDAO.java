package com.ipu.server.dao;


import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.ailk.common.config.SystemCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.logger.AbstractLogger;
import com.ailk.common.logger.ILogger;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.dialect.DBDialectFactory;
import com.ailk.database.dialect.IDBDialect;
import com.ailk.database.statement.IStatement;
import com.ailk.database.statement.Parameter;
import com.ailk.database.statement.impl.BindingStatement;
import com.ailk.database.statement.impl.ParameterStatement;
import com.ailk.database.statement.impl.SimpleStatement;
import com.ailk.database.util.SQLParser;
import com.ailk.mobile.db.dao.impl.AbstractDAO;
import com.ailk.mobile.db.seq.SequenceFactory;
import com.ailk.mobile.frame.context.IContext;
import com.ailk.mobile.frame.context.IContextData;
import com.ailk.mobile.frame.context.impl.AbstractContextManager;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: BaseDAO
 * @description: 基类DAO
 * 
 * @version: v1.0.0
 * @author: $Id: BaseDAO.java 1 2014-02-20 08:34:02Z huangbo $
 * @date: 2013-7-20
 */
public class BaseDAO extends AbstractDAO {

	private static transient Logger log = Logger.getLogger(BaseDAO.class);
	
	// 数据库方言实现类
	private static IDBDialect DB_DIALECT = DBDialectFactory.getDBDialect();
	
	// 线程对象
	private IContext<IContextData> session;
	
	@SuppressWarnings("unchecked")
	public BaseDAO(String connName) throws Exception{
		session = AbstractContextManager.getInstance().getContext();
		initial(connName);
	}
	
	/**
	 * 初始化
	 */
	public void initial(String connName) {
		if (null == connName || connName.length() <= 0) {
			throw new IllegalArgumentException("数据库连接名不能为空!");
		}
		this.connName = connName;
	}
	
	/**
	 * 获取数据库连接
	 * 
	 * @return
	 * @throws Exception
	 */
	private DBConnection getDataBaseConnection() throws Exception {
		if (null == connName || connName.length() <= 0) {
			throw new IllegalArgumentException("数据库连接名不能为空!");
		}
		
		DBConnection conn = session.get(connName);
		return conn;
	}
	
	/**
	 * 释放数据库连接
	 * @throws Exception
	 */
	private void destroyDataBaseConnection() throws Exception {
		DBConnection conn = getDataBaseConnection();
		
		if (conn != null)
			session.close(connName);
	}

	/**
	 * execute query
	 * 
	 * @param conn
	 * @param sql
	 * @return ResultSet
	 * @throws Exception
	 */
	private ResultSet executeQuery(DBConnection conn, String sql) throws Exception {
		long start = System.currentTimeMillis();
		
		try {
			ResultSet rs = DB_DIALECT.executeQuery(conn, sql);
			return rs;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			
			sendLog(start, null);
		}
		
	}

	/**
	 * execute query
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return ResultSet
	 * @throws Exception
	 */
	private ResultSet executeQuery(DBConnection conn, String sql, Parameter param) throws Exception {
		long start = System.currentTimeMillis();
		
		try {
			ResultSet rs = DB_DIALECT.executeQuery(conn, sql, param);
			return rs;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			
			sendLog(start, null);
		}
		
	}

	/**
	 * execute query
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return ResultSet
	 * @throws Exception
	 */
	private ResultSet executeQuery(DBConnection conn, String sql, IData param) throws Exception {
		long start = System.currentTimeMillis();
		
		try {
			ResultSet rs = DB_DIALECT.executeQuery(conn, sql, param);
			
			return rs;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			
			sendLog(start, null);
		}
		
	}

	/**
	 * execute update
	 * 
	 * @param sql
	 * @return int
	 * @throws Exception
	 */
	@Deprecated
	public int executeUpdate(String sql) throws Exception {
		throw new Exception("dao.executeUpdate(sql) 方法已作废!");
	}

	/**
	 * execute update
	 * 
	 * @parma conn
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(DBConnection conn, String sql, Object[] param) throws Exception {
		return executeUpdate(conn, sql, new Parameter(param));
	}

	/**
	 * execute update
	 * 
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(String sql, Object[] param) throws Exception {
		return executeUpdate(getDataBaseConnection(), sql, param);
	}

	/**
	 * execute update
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(DBConnection conn, String sql, Parameter param) throws Exception {
		long start = System.currentTimeMillis();

		IStatement statement = null;
		try {
			statement = new ParameterStatement(conn.getConnection(), sql, param);
			//statement.setQueryTimeout(conn.getStmtTimeout());
			
			conn.activeTransaction();
			int result = statement.executeUpdate();
			
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			
			sendLog(start, null);
			
			if (statement != null) {
				statement.close();
			}
		}
	}

	/**
	 * execute update
	 * 
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(String sql, Parameter param) throws Exception {
		return executeUpdate(getDataBaseConnection(), sql, param);
	}

	/**
	 * execute update
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(DBConnection conn, String sql, IData param) throws Exception {
		long start = System.currentTimeMillis();
		IStatement statement = null;
		try {
			statement = new BindingStatement(conn.getConnection(), sql, param);
			//statement.setQueryTimeout(conn.getStmtTimeout());
			
			conn.activeTransaction();
			int result = statement.executeUpdate();
			
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			
			sendLog(start, null);
			
			if (statement != null) {
				statement.close();
			}
		}
	}

	/**
	 * execute update
	 * 
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(String sql, IData param) throws Exception {
		return executeUpdate(getDataBaseConnection(), sql, param);
	}

	/**
	 * execute batch
	 * 
	 * @param conn
	 * @param sqls
	 * @return int[]
	 * @throws Exception
	 */
	public int[] executeBatch(DBConnection conn, String[] sqls) throws Exception {
		long start = System.currentTimeMillis();
		IStatement statement = null;
		try {
			statement = new SimpleStatement(conn.getConnection(), sqls);
			//statement.setQueryTimeout(conn.getStmtTimeout());
			
			conn.activeTransaction();
			int[] result = statement.executeBatch();
			
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			
			sendLog(start, null);
			
			if (statement != null) {
				statement.close();
			}
		}
		
	}

	/**
	 * execute batch
	 * 
	 * @param sqls
	 * @return int[]
	 * @throws Exception
	 */
	public int[] executeBatch(String[] sqls) throws Exception {
		return executeBatch(getDataBaseConnection(), sqls);
	}

	/**
	 * execute batch
	 * 
	 * @param conn
	 * @param sqls
	 * @param params
	 * @return int[]
	 * @throws Exception
	 */
	public int[] executeBatch(DBConnection conn, String sql, Parameter[] params) throws Exception {
		long start = System.currentTimeMillis();
		IStatement statement = null;
		
		try {
			statement = new ParameterStatement(conn.getConnection(), sql, params);
			//statement.setQueryTimeout(conn.getStmtTimeout());
			
			conn.activeTransaction();
			int[] result = statement.executeBatch();

			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			
			sendLog(start, null);
			
			if (statement != null) {
				statement.close();
			}
		}
	}

	/**
	 * execute batch
	 * 
	 * @param sql
	 * @param params
	 * @return int[]
	 * @throws Exception
	 */
	public int[] executeBatch(String sql, Parameter[] params) throws Exception {
		return executeBatch(getDataBaseConnection(), sql, params);
	}

	/**
	 * execute batch
	 * 
	 * @param sql
	 * @param params
	 * @param batchCount
	 * @return int[]
	 * @throws Exception
	 */
	public int[] executeBatch(String sql, IDataset params, int batchCount) throws Exception {
		return executeBatch(getDataBaseConnection(), sql, params, batchCount);
	}

	/**
	 * execute batch
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int[] executeBatch(DBConnection conn, String sql, IDataset params) throws Exception {
		return executeBatch(conn, sql, params, IStatement.DEFAULT_BATCH_SIZE);
	}

	/**
	 * execute batch
	 * 
	 * @param conn
	 * @param sqls
	 * @param params
	 * @return int[]
	 * @throws Exception
	 */
	public int[] executeBatch(DBConnection conn, String sql, IDataset params, int batchCount) throws Exception {
		long startTime = System.currentTimeMillis();
		
		BindingStatement statement = null;

		try {
			int size = params.size();
			int[] result = new int[size];
			int executeIdx = 0;
			if (batchCount <= 0) {
				batchCount = 2000;

				if (log.isDebugEnabled()) {
					log.debug("bad batch count, changed to 2000");
				}
			}
			
			IDataset subparams = null;
			for (int i = 0; i < size; i++) {
				if (subparams == null) {
					subparams = new DatasetList();
				}
				
				executeIdx ++;
				
				if ( i > 0 &&  (i + 1) % batchCount == 0 ) {
					if (statement == null) {
						statement = new BindingStatement(conn.getConnection(), sql, subparams);
						//statement.setQueryTimeout(conn.getStmtTimeout());
					}
					statement.setParams(subparams);
					
					conn.activeTransaction();
					int[] res = statement.executeBatch();
					
					int resSize = res.length;
					
					for (int j = 0; j < resSize; j++) {
						result[i - batchCount + 1 + j] = res[j];
					}
					
					
					if (log.isDebugEnabled()) {
						log.debug("execute batch begin " + (i - batchCount + 1) + " end " + i + ", batch size " + batchCount);
					}
					
					subparams = null;
				}
				
				if (subparams == null) {
					subparams = new DatasetList();
				}
				subparams.add(params.get(i));
			}
			
			if (subparams != null) {
				if (statement == null) {
					statement = new BindingStatement(conn.getConnection(), sql, subparams);
					//statement.setQueryTimeout(conn.getStmtTimeout());
				}
				statement.setParams(subparams);
				
				conn.activeTransaction();
				int[] res = statement.executeBatch();
				
				int resSize = res.length;
				
				for (int j = 0; j < resSize; j++) {
					result[size - resSize + j] = res[j];
				}
			}
			
			if (log.isDebugEnabled()) {
				log.debug("execute batch begin " + executeIdx + " end " + size + ", batch size " + batchCount);
			}
			
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			sendLog(startTime, null);
			
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - startTime));
			}
			
			if (statement != null) {
				statement.close();
			}
		}

		
	}

	/**
	 * execute batch
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @param batchCount
	 * @return
	 * @throws Exception
	 */
	public int[] executeBatch(DBConnection conn, String sql, Parameter[] params, int batchCount) throws Exception {
		long startTime = System.currentTimeMillis();
		
		ParameterStatement statement = null;
		
		try {
			int size = params.length;
			int index = 0;
			int[] result = new int[size];
			int executeIdx = 0;
			if (batchCount <= 0) {
				batchCount = 2000;

				if (log.isDebugEnabled()) {
					log.debug("bad batch count, changed to 2000");
				}
			}
			
			Parameter[] subparams = null;
			for (int i = 0; i < size; i++) {
				if (subparams == null) {
					subparams = new Parameter[batchCount];
				}
				
				executeIdx ++;
				
				if ( i > 0 &&  (i + 1) % batchCount == 0 ) {
					if (statement == null) {
						statement = new ParameterStatement(conn.getConnection(), sql, subparams);
						//statement.setQueryTimeout(conn.getStmtTimeout());
					}
					statement.setParams(subparams);
					
					conn.activeTransaction();
					int[] res = statement.executeBatch();
					
					int resSize = res.length;
					
					for (int j = 0; j < resSize; j++) {
						result[i - batchCount + 1 + j] = res[j];
					}
					
					index = 0;
					
					if (log.isDebugEnabled()) {
						log.debug("execute batch begin " + (i - batchCount + 1) + " end " + i + ", batch size " + batchCount);
					}
					
					subparams = null;
				} else {
					index ++;
				}
				
				if (subparams == null) {
					subparams = new Parameter[batchCount];
				}
				subparams[index] = params[i];
			}
			
			if (subparams != null) {
				if (statement == null) {
					statement = new ParameterStatement(conn.getConnection(), sql, subparams);
					//statement.setQueryTimeout(conn.getStmtTimeout());
				}
				statement.setParams(subparams);
				
				conn.activeTransaction();
				int[] res = statement.executeBatch();
				
				int resSize = res.length;
				
				for (int j = 0; j < resSize; j++) {
					result[size - resSize + j] = res[j];
				}
			}
			
			if (log.isDebugEnabled()) {
				log.debug("execute batch begin " + executeIdx + " end " + size + ", batch size " + batchCount);
			}
			
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - startTime));
			}
			
			sendLog(startTime, null);
			
			if (statement != null) {
				statement.close();
			}
		}
		
	}

	/**
	 * execute batch
	 * 
	 * @param sql
	 * @param params
	 * @return int[]
	 * @throws Exception
	 */
	public int[] executeBatch(String sql, IDataset params) throws Exception {
		return executeBatch(getDataBaseConnection(), sql, params);
	}

	/**
	 * get sys date
	 * 
	 * @param conn
	 * @return String
	 * @throws Exception
	 */
	public String getSysDate(DBConnection conn) throws Exception {
		Timestamp time = getCurrentTime(conn);
		return time == null ? null : DaoUtil.decodeTimestamp("yyyy-MM-dd", time);
	}

	/**
	 * get sys date
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getSysDate() throws Exception {
		String sysdate = getSysDate(getDataBaseConnection());
		
		if (SystemCfg.isDataPreloadOn) {
			destroyDataBaseConnection();
		}
		
		return sysdate;
	}

	/**
	 * get sys time
	 * 
	 * @param conn
	 * @return String
	 * @throws Exception
	 */
	public String getSysTime(DBConnection conn) throws Exception {
		Timestamp time = getCurrentTime(conn);
		return time == null ? null : DaoUtil.decodeTimestamp("yyyy-MM-dd HH:mm:ss", time);
	}

	/**
	 * get sys time
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getSysTime() throws Exception {
		String systime = getSysTime(getDataBaseConnection());
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return systime;
	}

	/**
	 * get current time
	 * 
	 * @param conn
	 * @return Timestamp
	 * @throws Exception
	 */
	public Timestamp getCurrentTime(DBConnection conn) throws Exception {
		ResultSet rs = executeQuery(conn, DB_DIALECT.getCurrentTimeSql());
		Timestamp time = rs.next() ? rs.getTimestamp(1) : null;
		rs.getStatement().close();
		return time;
	}

	/**
	 * get current time
	 * 
	 * @return Timestamp
	 * @throws Exception
	 */
	public Timestamp getCurrentTime() throws Exception {
		Timestamp st =  getCurrentTime(getDataBaseConnection());
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return st;
	}

	/**
	 * 获取序列的下一个值
	 * 
	 * @param clazz 序列类
	 * @return
	 * @throws Exception
	 */
	public String getSequence(Class<?> clazz) throws Exception {
		return SequenceFactory.nextval(this.connName, clazz);
	}

	/**
	 * 获取序列的下一个值
	 * 
	 * @param clazz 序列类
	 * @param eparchyCode 地州编码
	 * @return
	 * @throws Exception
	 */
	public String getSequence(Class<?> clazz, String eparchyCode) throws Exception {
		return SequenceFactory.nextval(this.connName, clazz, eparchyCode);
	}

	/**
	 * 获取序列的下一个值
	 * 
	 * @param seqName 序列名
	 * @return
	 * @throws Exception
	 */
	public String getSequence(String seqName) throws Exception {
		return SequenceFactory.nextval(this.connName, seqName);
	}

	/**
	 * get count
	 * 
	 * @param conn
	 * @param sql
	 * @return int
	 * @throws Exception
	 */
	private int getCount(DBConnection conn, String sql) throws Exception {
		long start = System.currentTimeMillis();

		Statement statement = null;
		try {
			ResultSet rs = DB_DIALECT.executeQuery(conn, DaoHelper.getCountSql(sql));
			int value = rs.next() ? rs.getInt(1) : 0;
			statement = rs.getStatement();
			
			if (log.isDebugEnabled()) {
				log.debug("[COUNT:" + sql + "]:" + value);
			}
			
			return value;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			
			sendLog(start, "count");
			
			if (statement != null) {
				statement.close();
			}
		}
	}

	/**
	 * get count
	 * 
	 * @param sql
	 * @return int
	 * @throws Exception
	 */
	public int getCount(String sql) throws Exception {
		int count = getCount(getDataBaseConnection(), sql);
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return count;
	}

	/**
	 * get count
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	private int getCount(DBConnection conn, String sql, Parameter param) throws Exception {
		if (null == param || param.size() == 0) {
			return getCount(conn, sql);
		}

		long start = System.currentTimeMillis();
		Statement statement = null;
		try {
			ResultSet rs = DB_DIALECT.executeQuery(conn, DaoHelper.getCountSql(sql), param);
			int value = rs.next() ? rs.getInt(1) : 0;
			statement = rs.getStatement();
			
			if (log.isDebugEnabled()) {
				log.debug("[COUNT:" + sql + "]:" + value);
			}
			
			return value;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			
			sendLog(start, "count");
			
			if (statement != null) {
				statement.close();
			}
		}
		
		
	}

	/**
	 * get count
	 * 
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int getCount(String sql, Parameter param) throws Exception {
		int count = getCount(getDataBaseConnection(), sql, param);
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return count;
	}

	/**
	 * get count
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	private int getCount(DBConnection conn, String sql, IData param) throws Exception {
		if (null == param || param.isEmpty()) {
			return getCount(conn, sql);
		}

		long start = System.currentTimeMillis();
		Statement statement = null;
		try {
			ResultSet rs = DB_DIALECT.executeQuery(conn, DaoHelper.getCountSql(sql), param);
			int value = rs.next() ? rs.getInt(1) : 0;
			statement = rs.getStatement();
			
			if (log.isDebugEnabled()) {
				log.debug("[COUNT:" + sql + "]:" + value);
			}

			return value;
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			
			sendLog(start, "count");
			
			if (statement != null) {
				statement.close();
			}
		}
		
	}

	/**
	 * get count
	 * 
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int getCount(String sql, IData param) throws Exception {
		int count = getCount(getDataBaseConnection(), sql, param);
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return count;
	}

	/**
	 * insert data
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @return boolean
	 * @throws Exception
	 */
	public boolean insert(DBConnection conn, String table_name, IData data) throws Exception {
		Object[] insobjs = DaoHelper.getObjectsByInsert(conn, table_name, data);
		int result = executeUpdate(conn, (String) insobjs[0], (Parameter) insobjs[1]);
		return result == 0 ? false : true;
	}

	/**
	 * insert data
	 * 
	 * @param table_name
	 * @param data
	 * @return boolean
	 * @throws Exception
	 */
	public boolean insert(String table_name, IData data) throws Exception {
		return insert(getDataBaseConnection(), table_name, data);
	}

	/**
	 * insert data
	 * 
	 * @param conn
	 * @param table_name
	 * @param dataset
	 * @return int[]
	 * @throws Exception
	 */
	public int[] insert(DBConnection conn, String table_name, IDataset dataset, int batchsize) throws Exception {
		
		if (null == dataset) {
			throw new IllegalArgumentException("批量insert时, 发现数据集为空!");
		}
		
		if (dataset.size() == 0) {
			return new int[0];
		}

		Object[] insobjs = DaoHelper.getObjectsByInsert(conn, table_name, dataset);
		return executeBatch(conn, (String) insobjs[0], (Parameter[]) insobjs[1], batchsize);
	}

	/**
	 * insert data
	 * 
	 * @param table_name
	 * @param dataset
	 * @return int[]
	 * @throws Exception
	 */
	public int[] insert(String table_name, IDataset dataset) throws Exception {
		return insert(getDataBaseConnection(), table_name, dataset, IStatement.DEFAULT_BATCH_SIZE);
	}

	/**
	 * insert data
	 * 
	 * @param table_name
	 * @param dataset
	 * @param batchsize
	 * @return
	 * @throws Exception
	 */
	public int[] insert(String table_name, IDataset dataset, int batchsize) throws Exception {
		return insert(getDataBaseConnection(), table_name, dataset, batchsize);
	}

	/**
	 * update data
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @return boolean
	 * @throws Exception
	 */
	public boolean update(DBConnection conn, String table_name, IData data) throws Exception {
		return update(conn, table_name, data, null);
	}

	/**
	 * update data
	 * 
	 * @param table_name
	 * @param data
	 * @return boolean
	 * @throws Exception
	 */
	public boolean update(String table_name, IData data) throws Exception {
		return update(getDataBaseConnection(), table_name, data);
	}

	/**
	 * update data
	 * 
	 * @param table_name
	 * @param dataset
	 * @return int[]
	 * @throws Exception
	 */
	public int[] update(String table_name, IDataset dataset, int batchsize) throws Exception {
		return update(getDataBaseConnection(), table_name, dataset, null, batchsize);
	}

	/**
	 * update data
	 * 
	 * @param table_name
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public int[] update(String table_name, IDataset dataset) throws Exception {
		return update(getDataBaseConnection(), table_name, dataset, null, IStatement.DEFAULT_BATCH_SIZE);
	}

	/**
	 * update data
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param keys
	 * @return boolean
	 * @throws Exception
	 */
	public boolean update(DBConnection conn, String table_name, IData data, String[] keys) throws Exception {
		return update(conn, table_name, data, keys, null);
	}

	/**
	 * update data
	 * 
	 * @param table_name
	 * @param data
	 * @param keys
	 * @return boolean
	 * @throws Exception
	 */
	public boolean update(String table_name, IData data, String[] keys) throws Exception {
		return update(getDataBaseConnection(), table_name, data, keys);
	}

	/**
	 * update data
	 * 
	 * @param conn
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @return int[]
	 * @throws Exception
	 */
	public int[] update(DBConnection conn, String table_name, IDataset dataset, String[] keys, int batchsize) throws Exception {
		if (dataset.size() == 0) {
			return null;
		}
		
		Object[] updobjs = DaoHelper.getObjectsByUpdate(conn, table_name, dataset, keys);
		return executeBatch(conn, (String) updobjs[0], (Parameter[]) updobjs[1], batchsize);
	}

	/**
	 * 将dataset里的数据批量update到表table_name, 并且set [columns] where [keys], keys &
	 * colums里的值必须在dataset里存在
	 * 
	 * @param conn
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public int[] update(DBConnection conn, String table_name, IDataset dataset, String[] columns, String[] keys, int batchsize) throws Exception {
		if (dataset.size() == 0) {
			return null;
		}
		
		Object[] updobjs = DaoHelper.getObjectsByUpdate(conn, table_name, dataset, columns, keys);
		return executeBatch(conn, (String) updobjs[0], (Parameter[]) updobjs[1], batchsize);
	}

	/**
	 * update data
	 * 
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @return int[]
	 * @throws Exception
	 */
	public int[] update(String table_name, IDataset dataset, String[] keys) throws Exception {
		return update(getDataBaseConnection(), table_name, dataset, keys, IStatement.DEFAULT_BATCH_SIZE);
	}

	/**
	 * update
	 * 
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @param batchsize
	 * @return
	 * @throws Exception
	 */
	public int[] update(String table_name, IDataset dataset, String[] keys, int batchsize) throws Exception {
		return update(getDataBaseConnection(), table_name, dataset, keys, batchsize);
	}

	/**
	 * 将dataset里的数据批量update到表table_name, 并且set [columns] where [keys], keys &
	 * colums里的值必须在dataset里存在
	 * 
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public int[] update(String table_name, IDataset dataset, String[] columns, String[] keys) throws Exception {
		return update(getDataBaseConnection(), table_name, dataset, columns, keys, IStatement.DEFAULT_BATCH_SIZE);
	}

	/**
	 * 将dataset里的数据批量update到表table_name, 并且set [columns] where [keys], keys &
	 * colums里的值必须在dataset里存在
	 * 
	 * @param table_name
	 * @param dataset
	 * @param columns
	 * @param keys
	 * @param batchsize
	 * @return
	 * @throws Exception
	 */
	public int[] update(String table_name, IDataset dataset, String[] columns, String[] keys, int batchsize) throws Exception {
		return update(getDataBaseConnection(), table_name, dataset, columns, keys, batchsize);
	}

	/**
	 * update data
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param keys
	 * @param values
	 * @return boolean
	 * @throws Exception
	 */
	public boolean update(DBConnection conn, String table_name, IData data, String[] keys, String[] values) throws Exception {
		Object[] updobjs = DaoHelper.getObjectsByUpdate(conn, table_name, data, keys, values);
		int result = executeUpdate(conn, (String) updobjs[0], (Parameter) updobjs[1]);
		return result == 0 ? false : true;
	}

	/**
	 * 将IData里的columns字段update到table_name,并且where [keys] = [values]
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param columns
	 * @param keys
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public boolean update(DBConnection conn, String table_name, IData data, String[] columns, String[] keys, String[] values) throws Exception {
		Object[] updobjs = DaoHelper.getObjectsByUpdate(conn, table_name, data, columns, keys, values);
		int result = executeUpdate(conn, (String) updobjs[0], (Parameter) updobjs[1]);
		return result == 0 ? false : true;
	}

	/**
	 * update data
	 * 
	 * @param table_name
	 * @param data
	 * @param keys
	 * @param values
	 * @return boolean
	 * @throws Exception
	 */
	public boolean update(String table_name, IData data, String[] keys, String[] values) throws Exception {
		return update(getDataBaseConnection(), table_name, data, keys, values);
	}

	/**
	 * 将IData里的columns字段update到table_name,并且where [keys] = [values]
	 * 
	 * @param table_name
	 * @param data
	 * @param columns
	 * @param keys
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public boolean update(String table_name, IData data, String[] columns, String[] keys, String[] values) throws Exception {
		return update(getDataBaseConnection(), table_name, data, columns, keys, values);
	}

	/**
	 * delete data
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @return boolean
	 * @throws Exception
	 */
	public boolean delete(DBConnection conn, String table_name, IData data) throws Exception {
		return delete(conn, table_name, data, null);
	}

	/**
	 * delete data
	 * 
	 * @param table_name
	 * @param data
	 * @return boolean
	 * @throws Exception
	 */
	public boolean delete(String table_name, IData data) throws Exception {
		return delete(getDataBaseConnection(), table_name, data);
	}

	/**
	 * delete data
	 * 
	 * @param table_name
	 * @param dataset
	 * @return int[]
	 * @throws Exception
	 */
	public int[] delete(String table_name, IDataset dataset) throws Exception {
		return delete(getDataBaseConnection(), table_name, dataset, null, IStatement.DEFAULT_BATCH_SIZE);
	}

	/**
	 * delete
	 * @param table_name
	 * @param dataset
	 * @param batchsize
	 * @return
	 * @throws Exception
	 */
	public int[] delete(String table_name, IDataset dataset, int batchsize) throws Exception {
		return delete(getDataBaseConnection(), table_name, dataset, null, batchsize);
	}

	/**
	 * delete data
	 * 
	 * @param conn
	 * @param table_name
	 * @param keys
	 * @param values
	 * @return boolean
	 * @throws Exception
	 */
	public boolean delete(DBConnection conn, String table_name, String[] keys, String[] values) throws Exception {
		return delete(conn, table_name, DaoHelper.getDataByKeys(keys, values), keys);
	}

	/**
	 * delete data
	 * 
	 * @param table_name
	 * @param keys
	 * @param values
	 * @return boolean
	 * @throws Exception
	 */
	public boolean delete(String table_name, String[] keys, String[] values) throws Exception {
		return delete(getDataBaseConnection(), table_name, keys, values);
	}

	/**
	 * delete data
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param keys
	 * @return boolean
	 * @throws Exception
	 */
	public boolean delete(DBConnection conn, String table_name, IData data, String[] keys) throws Exception {

		Object[] delobjs = DaoHelper.getObjectsByDelete(conn, table_name, data, keys);
		int result = executeUpdate(conn, (String) delobjs[0], (Parameter) delobjs[1]);

		return result == 0 ? false : true;
	}

	/**
	 * delete data
	 * 
	 * @param table_name
	 * @param data
	 * @param keys
	 * @return boolean
	 * @throws Exception
	 */
	public boolean delete(String table_name, IData data, String[] keys) throws Exception {
		return delete(getDataBaseConnection(), table_name, data, keys);
	}

	/**
	 * delete data
	 * 
	 * @param conn
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @return int[]
	 * @throws Exception
	 */
	public int[] delete(DBConnection conn, String table_name, IDataset dataset, String[] keys, int batchsize) throws Exception {
		if (dataset.size() == 0) {
			return null;
		}

		Object[] delobjs = DaoHelper.getObjectsByDelete(conn, table_name, dataset, keys);
		return executeBatch(conn, (String) delobjs[0], (Parameter[]) delobjs[1], batchsize);
	}

	/**
	 * delete data
	 * 
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @return int[]
	 * @throws Exception
	 */
	public int[] delete(String table_name, IDataset dataset, String[] keys) throws Exception {
		return delete(getDataBaseConnection(), table_name, dataset, keys, IStatement.DEFAULT_BATCH_SIZE);
	}

	/**
	 * delete data
	 * 
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @param batchsize
	 * @return
	 * @throws Exception
	 */
	public int[] delete(String table_name, IDataset dataset, String[] keys, int batchsize) throws Exception {
		return delete(getDataBaseConnection(), table_name, dataset, keys, batchsize);
	}

	/**
	 * save data
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @return boolean
	 * @throws Exception
	 */
	public boolean save(DBConnection conn, String table_name, IData data) throws Exception {
		return save(conn, table_name, data, null);
	}

	/**
	 * save data
	 * 
	 * @param table_name
	 * @param data
	 * @return boolean
	 * @throws Exception
	 */
	public boolean save(String table_name, IData data) throws Exception {
		return save(getDataBaseConnection(), table_name, data);
	}

	/**
	 * save data
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param keys
	 * @return boolean
	 * @throws Exception
	 */
	public boolean save(DBConnection conn, String table_name, IData data, String[] keys) throws Exception {
		return save(conn, table_name, data, keys, null);
	}

	/**
	 * save data
	 * 
	 * @param table_name
	 * @param data
	 * @parma keys
	 * @return boolean
	 * @throws Exception
	 */
	public boolean save(String table_name, IData data, String[] keys) throws Exception {
		return save(getDataBaseConnection(), table_name, data, keys);
	}

	/**
	 * save data
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param keys
	 * @param values
	 * @return boolean
	 * @throws Exception
	 */
	public boolean save(DBConnection conn, String table_name, IData data, String[] keys, String[] values) throws Exception {
		IData alldata = values == null ? queryByPK(conn, table_name, data, keys) : queryByPK(conn, table_name, keys, values);
		if (null == alldata) {
			return false;
		}
		alldata.putAll(data);
		return update(conn, table_name, alldata, keys, values);
	}

	/**
	 * save data
	 * 
	 * @param table_name
	 * @param data
	 * @param keys
	 * @param values
	 * @return boolean
	 * @throws Exception
	 */
	public boolean save(String table_name, IData data, String[] keys, String[] values) throws Exception {
		return save(getDataBaseConnection(), table_name, data, keys, values);
	}

	/**
	 * query by pk
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @return IData
	 * @throws Exception
	 */
	public IData queryByPK(DBConnection conn, String table_name, IData data) throws Exception {
		return queryByPK(conn, table_name, data, null);
	}

	/**
	 * query by pk
	 * 
	 * @param table_name
	 * @param data
	 * @return IData
	 * @throws Exception
	 */
	public IData queryByPK(String table_name, IData data) throws Exception {
		IData row = queryByPK(getDataBaseConnection(), table_name, data);
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return row;
	}

	/**
	 * query by pk
	 * 
	 * @param conn
	 * @param table_name
	 * @param keys
	 * @param values
	 * @return IData
	 * @throws Exception
	 */
	public IData queryByPK(DBConnection conn, String table_name, String[] keys, String[] values) throws Exception {
		return queryByPK(conn, table_name, DaoHelper.getDataByKeys(keys, values), keys);
	}

	/**
	 * query by pk
	 * 
	 * @param table_name
	 * @param keys
	 * @param values
	 * @return IData
	 * @throws Exception
	 */
	public IData queryByPK(String table_name, String[] keys, String[] values) throws Exception {
		IData row = queryByPK(getDataBaseConnection(), table_name, keys, values);
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return row;
	}

	/**
	 * query by pk
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param keys
	 * @return IData
	 * @throws Exception
	 */
	public IData queryByPK(DBConnection conn, String table_name, IData data, String[] keys) throws Exception {
		Object[] qryobjs = DaoHelper.getObjectsByQuery(conn, table_name, data, keys);
		IDataset dataset = queryList(conn, (String) qryobjs[0], (Parameter) qryobjs[1], 1);
		return dataset.size() == 0 ? null : (IData) dataset.get(0);
	}

	/**
	 * query by pk
	 * 
	 * @param table_name
	 * @param data
	 * @param keys
	 * @return IData
	 * @throws Exception
	 */
	public IData queryByPK(String table_name, IData data, String[] keys) throws Exception {
		IData row = queryByPK(getDataBaseConnection(), table_name, data, keys);
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return row;
	}

	/**
	 * query list
	 * 
	 * @param conn
	 * @param sql
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(DBConnection conn, String sql, int fetchsize) throws Exception {
		ResultSet rs = executeQuery(conn, sql);
		IDataset dataset = DaoHelper.rssetToDataset(rs, fetchsize);
		rs.getStatement().close();
		return dataset;
	}

	/**
	 * 查询列表
	 * 
	 * @param sql
	 * @return IDataset
	 * @throws Exception
	 * @deprecated 未采用动态变量绑定，影响DB性能，不推荐使用(周麟)。
	 */
	public IDataset queryList(String sql) throws Exception {
		throw new Exception("dao.queryList(sql)由于存在变量绑定漏洞,该方法已作废");
	}

	/**
	 * query list
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(DBConnection conn, String sql, Parameter param, int fetchsize) throws Exception {
		if (null == param || param.size() == 0) {
			return queryList(conn, sql, fetchsize);
		}
		
		ResultSet rs = executeQuery(conn, sql, param);
		IDataset dataset = DaoHelper.rssetToDataset(rs, fetchsize);
		rs.getStatement().close();
		return dataset;
	}

	/**
	 * query list
	 * 
	 * @param sql
	 * @param param
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(String sql, Parameter param) throws Exception {
		IDataset ds = queryList(getDataBaseConnection(), sql, param, DaoHelper.DEFAULT_FETCH_SIZE);
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return ds;
	}

	/**
	 * query list
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(DBConnection conn, String sql, IData param, int fetchsize) throws Exception {
		if (null == param) {
			return queryList(conn, sql, fetchsize);
		}
		
		ResultSet rs = executeQuery(conn, sql, param);
		IDataset dataset = DaoHelper.rssetToDataset(rs, fetchsize);
		rs.getStatement().close();
		return dataset;
	}

	/**
	 * query list
	 * 
	 * @param sql
	 * @param param
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(String sql, IData param) throws Exception {
		IDataset ds = queryList(getDataBaseConnection(), sql, param, DaoHelper.DEFAULT_FETCH_SIZE);
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return ds;
	}

	/**
	 * query list
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @param pagination
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(DBConnection conn, String sql, Parameter param, Pagination pagination) throws Exception {
		boolean isrange = pagination != null && pagination.getPageSize() > 0;
		
		long count = 0L;
		if (isrange) {
			if (pagination.isOnlyCount()) {
				pagination.setCount(pagination.getCount() > 0 ? pagination.getCount() : getCount(conn, sql, param));
				return new DatasetList();
			}
			
			if (pagination.isNeedCount()) {
				count = getCount(conn, sql, param);
			} else {
				count = pagination.getCount();
			}
		}
		
		//long count = isrange ? (pagination.isNeedCount() ? getCount(conn, sql, param) : pagination.getCount()) : 0;

		if (isrange) {
			
			if (count == 0 && pagination.isNeedCount()) {
				pagination.setCount(count);
				return new DatasetList();
			}
			
			if (null == param) {
				param = new Parameter();
			}
			sql = DB_DIALECT.getPagingSql(sql, param, pagination.getStart(), pagination.getEnd());
		}

		IDataset dataset = queryList(conn, sql, param, isrange ? pagination.getFetchSize() : DaoHelper.DEFAULT_FETCH_SIZE);
		if (isrange) {
			pagination.setCount(count);
		}
		return dataset;
	}

	/**
	 * query list
	 * 
	 * @param sql
	 * @param param
	 * @param pagination
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(String sql, Parameter param, Pagination pagination) throws Exception {
		IDataset ds = queryList(getDataBaseConnection(), sql, param, pagination);
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return ds;
	}

	/**
	 * query list
	 * 
	 * @param parser
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(SQLParser parser) throws Exception {
		return queryList(parser, null);
	}

	/**
	 * query list
	 * 
	 * @param parser
	 * @param
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(SQLParser parser, Pagination pagination) throws Exception {
		return queryList(parser.getSQL(), parser.getParam(), pagination);
	}

	/**
	 * execute update
	 * 
	 * @param parser
	 * @param int
	 * @throws Exception
	 */
	public int executeUpdate(SQLParser parser) throws Exception {
		return executeUpdate(parser.getSQL(), parser.getParam());
	}

	/**
	 * query list
	 * 
	 * @param sql
	 * @param pagination
	 * @return IDataset
	 * @throws Exception
	 * @deprecated 未采用动态变量绑定，影响DB性能，不推荐使用(周麟)。
	 */
	public IDataset queryList(String sql, Pagination pagination) throws Exception {
		throw new Exception("dao.queryList(sql, pagin)由于存在变量绑定漏洞,该方法已作废");
	}

	/**
	 * query list
	 * 
	 * @param sql
	 * @param param
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(String sql, Object[] param) throws Exception {
		return queryList(sql, param, null);
	}

	/**
	 * query list
	 * 
	 * @param sql
	 * @param param
	 * @param pagination
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(String sql, Object[] param, Pagination pagination) throws Exception {
		return queryList(sql, new Parameter(param), pagination);
	}

	/**
	 * query list
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @param pagination
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(DBConnection conn, String sql, IData param, Pagination pagination) throws Exception {
		boolean isrange = pagination != null && pagination.getPageSize() > 0;
		
		long count = 0L;
		if (isrange) {
			if (pagination.isOnlyCount()) {
				pagination.setCount(pagination.getCount() > 0 ? pagination.getCount() : getCount(conn, sql, param));
				return new DatasetList();
			}
			
			if (pagination.isNeedCount()) {
				count = getCount(conn, sql, param);
			} else {
				count = pagination.getCount();
			}
		}
		
		//long count = isrange ? (pagination.isNeedCount() ? getCount(conn, sql, param) : pagination.getCount()) : 0;
		
		if (isrange) {
			
			if (count == 0 && pagination.isNeedCount()) {
				pagination.setCount(count);
				return new DatasetList();
			}
			
			if (null == param) {
				param = new DataMap();
			}
			sql = DB_DIALECT.getPagingSql(sql, param, pagination.getStart(), pagination.getEnd());
		}

		IDataset dataset = queryList(conn, sql, param, isrange ? pagination.getFetchSize() : DaoHelper.DEFAULT_FETCH_SIZE);
		if (isrange) {
			pagination.setCount(count);
		}
		return dataset;
	}

	/**
	 * query list
	 * 
	 * @param sql
	 * @param param
	 * @param pagination
	 * @return IDataset
	 * @throws Exception
	 */
	public IDataset queryList(String sql, IData param, Pagination pagination) throws Exception {
		IDataset ds = queryList(getDataBaseConnection(), sql, param, pagination);
		
		if (SystemCfg.isReleaseDBConn) {
			destroyDataBaseConnection();
		}
		
		return ds;
	}

	/**
	 * call func
	 * 
	 * @param conn
	 * @param name
	 * @param paramNames
	 * @param params
	 * @param returnType
	 * @return Object
	 * @throws Exception
	 */
	public Object callFunc(DBConnection conn, String name, String[] paramNames, IData params, int returnType) throws Exception {
		long start = System.currentTimeMillis();
		
		try {
			conn.activeTransaction();
			Object obj = DaoHelper.callFunc(conn.getConnection(), name, paramNames, params, returnType);
			
			return obj;
		} catch (Exception e) {
			throw e;
		} finally {
			
			sendLog(start, null);
			
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
		}
	}

	/**
	 * call func
	 * 
	 * @param name
	 * @param paramNames
	 * @param params
	 * @param returnType
	 * @return
	 * @throws Exception
	 */
	public Object callFunc(String name, String[] paramNames, IData params, int returnType) throws Exception {
		return callFunc(getDataBaseConnection(), name, paramNames, params, returnType);
	}

	/**
	 * call proc
	 * 
	 * @param conn
	 * @param name
	 * @param paramNames
	 * @param params
	 * @throws Exception
	 */
	public void callProc(DBConnection conn, String name, String[] paramNames, IData params) throws Exception {
		long start = System.currentTimeMillis();
		
		try {
			conn.activeTransaction();
			DaoHelper.callProc(conn.getConnection(), name, paramNames, params);
		} catch (Exception e) {
			throw e;
		} finally {
			sendLog(start, null);
			
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
		}
	}

	/**
	 * call proc
	 * 
	 * @param name
	 * @param paramNames
	 * @param params
	 * @throws Exception
	 */
	public void callProc(String name, String[] paramNames, IData params) throws Exception {
		callProc(getDataBaseConnection(), name, paramNames, params);
	}
	
	/**
	 * commit
	 * @throws Exception
	 */
	public void commit() throws Exception {
		getDataBaseConnection().commit();
	}

	/**
	 * send log
	 * 
	 * @param start
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	private void sendLog(long start, String subkey) {
		ILogger logger = AbstractLogger.getLogger(getClass());
		if (null == logger) return ;
			
		logger.log(this, subkey, start, (System.currentTimeMillis() - start), null);
	}
}