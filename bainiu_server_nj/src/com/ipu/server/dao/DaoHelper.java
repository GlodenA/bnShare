package com.ipu.server.dao;



import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.dialect.DBDialectFactory;
import com.ailk.database.dialect.IDBDialect;
import com.ailk.database.object.IColumnObject;
import com.ailk.database.object.ITableObject;
import com.ailk.database.object.impl.ColumnObject;
import com.ailk.database.object.impl.TableObject;
import com.ailk.database.statement.Parameter;
import com.ailk.database.util.DaoJvmCache;
import com.ailk.database.util.DaoMemCache;
import com.ailk.database.util.DataOraMap;

public class DaoHelper {

	private static transient Logger log = Logger.getLogger(DaoHelper.class);
	public static final int COLUMN_TYPE_STRING = 1;
	public static final int COLUMN_TYPE_NUMBER = 2;
	public static final int COLUMN_TYPE_DATETIME = 3;
	public static final int COLUMN_TYPE_OTHER = 4;
	public static final int DEFAULT_FETCH_SIZE = 1000;
	
	static final String GET_PRIMARY_KEY_SQL = "select c.owner AS table_schem, c.table_name, c.column_name, c.position AS key_seq, c.constraint_name AS pk_name FROM all_cons_columns c, all_constraints k WHERE k.constraint_type = 'P' AND k.owner = ? AND k.table_name = ? AND k.constraint_name = c.constraint_name AND k.table_name = c.table_name AND k.owner = c.owner";

	/**
	 * get tochar sql
	 * 
	 * @param value
	 * @param format
	 * @return String
	 * @throws Exception
	 */
	static String getTocharSql(String key, String format) throws Exception {
		IDBDialect dialect = DBDialectFactory.getDBDialect();
		String dformat = dialect.getDateFormat(format);
		return "to_char(t." + key + ", '" + dformat + "')";
	}
	
	/**
	 * get to date sql
	 * @param key
	 * @param format
	 * @return
	 * @throws Exception
	 */
	private static String getTodateSql(String value, String format) throws Exception {
		IDBDialect dialect = DBDialectFactory.getDBDialect();
		String dformat = dialect.getDateFormat(format);
		return " = to_date( ?, '" + dformat + "')";
	}

	/**
	 * get count sql
	 * 
	 * @param s
	 * @return String
	 */
	public static String getCountSql(String s) {
		StringBuilder sbf = new StringBuilder();
		sbf.append("SELECT COUNT(1) FROM (");
		sbf.append(s);
		sbf.append(")");
		return sbf.toString();
	}

	/**
	 * get count sql by DB2
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static String getCountSqlByDB2(String sql) {
		int orderidx = sql.toLowerCase().indexOf("order");
		if (orderidx != -1) {
			int start = orderidx + "order".length();
			int end = -1;
			while (start < sql.length()) {
				char ch = sql.charAt(start);
				if (ch == ')') {
					end = start;
					start = sql.length();
					continue;
				}
				start++;
			}
			if (end == -1) {
				return sql.substring(0, orderidx);
			} else {
				return sql.substring(0, orderidx) + sql.substring(end + 1, sql.length());
			}
		}

		return sql;
	}

	/**
	 * check add string
	 * 
	 * @param ls
	 * @param s
	 */
	public static void checkAddString(List<String> ls, Object s) {
		if (s.toString().trim().length() > 0) {
			ls.add(s.toString().trim());
		}
	}

	/**
	 * tokenize
	 * 
	 * @param s
	 * @param delims
	 * @return List
	 */
	public static List<String> tokenize(String s, String[] delims) {
		List<String> ls = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sbRemark = new StringBuilder();
		StringBuilder sbQuote = new StringBuilder();
		boolean quote = false;
		boolean remark = false;
		int len = s.length();
		int i = 0;
		String delim = "";
		while (i < len) {
			char c = s.charAt(i);
			if (quote) {
				if (c == '\'') {
					if (i + 1 < len) {
						char cn = s.charAt(i + 1);
						if (cn == '\'') {
							sbQuote.append(c);
							sbQuote.append(cn);
							i++;
						} else {
							sbQuote.append(c);
							checkAddString(ls, sbQuote);
							sbQuote = new StringBuilder();
							quote = false;
						}
					} else {
						sbQuote.append(c);
						checkAddString(ls, sbQuote);
						sbQuote = new StringBuilder();
						quote = false;
					}
				} else {
					sbQuote.append(c);
				}
			} else {
				if (remark) {
					if (c == '*') {
						if (i + 1 < len) {
							char cn = s.charAt(i + 1);
							if (cn == '/') {
								sbRemark.append(c);
								sbRemark.append(cn);
								checkAddString(ls, sbRemark);
								sbRemark = new StringBuilder();
								remark = false;
								i++;
							} else {
								sbRemark.append(c);
							}
						} else {
							sbRemark.append(c);
						}
					} else {
						sbRemark.append(c);
					}
				} else {
					if (c == '\'') {
						checkAddString(ls, sb);
						sb = new StringBuilder();
						sbQuote.append(c);
						quote = true;
					} else if ((c == '/') && (i + 1 < len) && (s.charAt(i + 1) == '*')) {
						checkAddString(ls, sb);
						sb = new StringBuilder();
						sbRemark.append("/*");
						remark = true;
						i++;
					} else {
						int sep = 0;
						for (int j = 0, delimsSize=delims.length; j < delimsSize; j++) {
							if (s.substring(i).startsWith(delims[j])) {
								sep = delims[j].length();
								delim = delims[j];
								break;
							}
						}
						if (sep > 0) {
							checkAddString(ls, sb);
							checkAddString(ls, delim);
							sb = new StringBuilder();
							i += sep - 1;
						} else {
							sb.append(c);
						}
					}
				}
			}
			i++;
		}
		checkAddString(ls, sb);
		if (quote) {
			throw new RuntimeException("quoted string not properly terminated");
		}
		if (remark) {
			throw new RuntimeException("remark not properly terminated");
		}
		return ls;
	}

	/**
	 * get insert sql
	 * 
	 * @param table_name
	 * @param namestr
	 * @param valuestr
	 * @return String
	 */
	public static String getInsertSql(String table_name, String namestr, String valuestr) {
		StringBuilder str = new StringBuilder(300);
		str.append("insert into " + table_name);
		str.append("(" + namestr + ") values (");
		
		str.append(valuestr.replaceAll("/n", "") + ")");
		return str.toString();
	}

	/**
	 * get update sql
	 * 
	 * @param table_name
	 * @param setstr
	 * @param keystr
	 * @return String
	 */
	public static String getUpdateSql(String table_name, String setstr, String keystr) {
		StringBuilder str = new StringBuilder(300);
		str.append("update ").append(table_name).append(" t set ");
		str.append(setstr);
		str.append(" where ");
		str.append(keystr);
		return str.toString();
	}

	/**
	 * get delete sql
	 * 
	 * @param table_name
	 * @param setstr
	 * @param keystr
	 * @return String
	 */
	public static String getDeleteSql(String table_name, String keystr) {
		StringBuilder str = new StringBuilder(200);
		str.append("delete from ").append(table_name);
		str.append(" t where ");
		str.append(keystr);
		return str.toString();
	}

	/**
	 * get query sql
	 * 
	 * @param table_name
	 * @param setstr
	 * @param keystr
	 * @return String
	 */
	public static String getQuerySql(String table_name, String keystr) {
		StringBuilder str = new StringBuilder(100);
		str.append("select t.*, t.rowid from ").append(table_name);
		str.append(" t where ");
		str.append(keystr);
		return str.toString();
	}

	/**
	 * get objects by column
	 * 
	 * @param column
	 * @param data
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByColumn(IColumnObject column, IData data) throws Exception {
		String columnName = column.getColumnName();
		Object columnValue = data.get(columnName);
		
		if (DaoUtil.isDatetimeColumn(column.getColumnType())) {
			if (columnValue != null) {
				if ("".equals(columnValue)) {
					columnValue = null;
				} else {
					columnValue = DaoUtil.encodeTimestamp(columnValue.toString().replaceAll("\n", ""));
				}
			}
		} else if (column.getColumnType() == Types.LONGVARCHAR) {
			if (columnValue != null && !"".equals(columnValue)) {
				columnValue = new StringReader(columnValue.toString().replaceAll("\n", ""));
			}
		} 
		else if(columnValue instanceof String){
			columnValue = columnValue.toString().replaceAll("\n", "");
		}
		
		return new Object[] { columnName, columnValue };
	}

	/**
	 * get data by keys
	 * 
	 * @param keys
	 * @param values
	 * @return IData
	 * @throws Exception
	 */
	public static IData getDataByKeys(String[] keys, String[] values) throws Exception {
		IData data = new DataMap();
		for (int i = 0, size = keys.length; i < size; i++) {
			data.put(keys[i], values[i]);
		}
		return data;
	}

	/**
	 * get objects by insert
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByInsert(DBConnection conn, String table_name, IData data) throws Exception {
		StringBuilder namestr = new StringBuilder();
		StringBuilder valuestr = new StringBuilder();
		Parameter param = new Parameter();

		IColumnObject[] columns = getColumns(conn, table_name);

		for (IColumnObject column : columns) {

			if ("ROWID".equals(column.getColumnName())) {
				continue;
			}

			Object[] colobjs = getObjectsByColumn(column, data);
			
			if(colobjs[1]!=null)//如果没值不默认NULL，不拼串，让数据库取默认值
			{
				namestr.append(colobjs[0] + ",");
				valuestr.append("?" + ",");
				param.add(colobjs[1]);
			}
		}
		
		String sql = getInsertSql(table_name, DaoUtil.trimSuffix(namestr.toString(), ","),
				DaoUtil.trimSuffix(valuestr.toString(), ","));
		
		if (log.isDebugEnabled()) {
			log.debug("get insert sql :" + conn.getUserName() + "." + table_name + ">>>" + sql);
		}

		return new Object[] { sql, param };
	}

	/**
	 * get objects by insert
	 * 
	 * @param conn
	 * @param table_name
	 * @param dataset
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByInsert(DBConnection conn, String table_name, IDataset dataset) throws Exception {
		StringBuilder namestr = new StringBuilder();
		StringBuilder valuestr = new StringBuilder();
		IColumnObject[] columns = getColumns(conn, table_name);

		Parameter[] params = new Parameter[dataset.size()];
		for (int i = 0, size = params.length; i < size; i++) {
			IData data = (IData) dataset.get(i);
			params[i] = new Parameter();

			for (int j = 0, colsize = columns.length; j < colsize; j++) {

				if ("ROWID".equals(columns[j].getColumnName()))
					continue;

				Object[] colobjs = getObjectsByColumn(columns[j], data);

				if (i == 0) {
					namestr.append(colobjs[0] + ",");
					valuestr.append("?" + ",");
				}
				params[i].add(colobjs[1]);
			}
		}

		String sql = getInsertSql(table_name, DaoUtil.trimSuffix(namestr.toString(), ","),
				DaoUtil.trimSuffix(valuestr.toString(), ","));
		
		if (log.isDebugEnabled()) {
			log.debug("get insert sql :" + conn.getUserName() + "." + table_name + ">>>" + sql);
		}
		
		return new Object[] { sql, params };
	}

	/**
	 * get objects by update
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByUpdate(DBConnection conn, String table_name, IData data) throws Exception {
		return getObjectsByUpdate(conn, table_name, data, null);
	}

	/**
	 * get object by update
	 * 
	 * @param conn
	 * @param table_name
	 * @param dataset
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByUpdate(DBConnection conn, String table_name, IDataset dataset) throws Exception {
		return getObjectsByUpdate(conn, table_name, dataset, null);
	}

	/**
	 * get objects by update
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param keys
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByUpdate(DBConnection conn, String table_name, IData data, String[] keys)
			throws Exception {
		return getObjectsByUpdate(conn, table_name, data, keys, null);
	}

	/**
	 * get objects by update
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param keys
	 * @param values
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByUpdate(DBConnection conn, String table_name, IData data, String[] keys,
			String[] values) throws Exception {
		StringBuilder setstr = new StringBuilder();
		Parameter param = new Parameter();
		
		IColumnObject[] columns = getColumns(conn, table_name);
		for (IColumnObject column : columns) {

			if ("ROWID".equals(column.getColumnName()))
				continue;

			Object[] colobjs = getObjectsByColumn(column, data);

			setstr.append("t.").append(colobjs[0]).append(" = ?,");
			param.add(colobjs[1]);
		}

		Object[] keyobjs = getObjectsByKeys(conn, table_name, keys, values == null ? data : getDataByKeys(keys, values));

		param.addAll((Parameter) keyobjs[1]);
		
		String sql = getUpdateSql(table_name, DaoUtil.trimSuffix(setstr.toString(), ","), (String) keyobjs[0]);
		
		if (log.isDebugEnabled()) {
			log.debug("get update sql :" + conn.getUserName() + "." + table_name + ">>>" + sql);
		}

		return new Object[] { sql, param };
	}
	
	
	
	/**
	 * get objects by update
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param columns
	 * @param keys
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public static Object[] getObjectsByUpdate(DBConnection conn, String table_name, IData data, String[] columns, String[] keys,
			String[] values) throws Exception {
		StringBuilder setstr = new StringBuilder();
		Parameter param = new Parameter();

		Map<String, IColumnObject> cols = getColumnsByData(conn, table_name);
		for (int i = 0, size=columns.length; i < size; i++) {
			Object[] colobjs = getObjectsByColumn(cols.get(columns[i].toUpperCase()), data);

			setstr.append("t.").append(colobjs[0]).append(" = ?,");
			param.add(colobjs[1]);
		}

		Object[] keyobjs = getObjectsByKeys(conn, table_name, keys, values == null ? data : getDataByKeys(keys, values));

		param.addAll((Parameter) keyobjs[1]);
		
		String sql = getUpdateSql(table_name, DaoUtil.trimSuffix(setstr.toString(), ","), (String) keyobjs[0]);
		
		if (log.isDebugEnabled()) {
			log.debug("get update sql :" + conn.getUserName() + "." + table_name + ">>>" + sql);
		}

		return new Object[] { sql, param };
	}

	/**
	 * get objects by update
	 * 
	 * @param conn
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByUpdate(DBConnection conn, String table_name, IDataset dataset, String[] keys)
			throws Exception {
		StringBuilder setstr = new StringBuilder();
		StringBuilder keystr = new StringBuilder();
		IColumnObject[] columns = getColumns(conn, table_name);

		Parameter[] params = new Parameter[dataset.size()];
		for (int i = 0, size = params.length; i < size; i++) {
			IData data = (IData) dataset.get(i);
			params[i] = new Parameter();

			for (IColumnObject column : columns) {

				if ("ROWID".equals(column.getColumnName()))
					continue;

				Object[] colobjs = getObjectsByColumn(column, data);

				if (i == 0)
					setstr.append(colobjs[0] + " = ?,");
				params[i].add(colobjs[1]);
			}

			Object[] keyobjs = getObjectsByKeys(conn, table_name, keys, data);
			params[i].addAll((Parameter) keyobjs[1]);

			if (i == 0)
				keystr.append(keyobjs[0]);
		}
		
		String sql = getUpdateSql(table_name, DaoUtil.trimSuffix(setstr.toString(), ","), keystr.toString());
		
		if (log.isDebugEnabled()) {
			log.debug("get update sql :" + conn.getUserName() + "." + table_name + ">>>" + sql);
		}

		return new Object[] { sql, params };
	}

	/**
	 * get objects by update
	 * 
	 * @param conn
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public static Object[] getObjectsByUpdate(DBConnection conn, String table_name, IDataset dataset, String[] columns,
			String[] keys) throws Exception {
		StringBuilder setstr = new StringBuilder();
		StringBuilder keystr = new StringBuilder();
		Map<String, IColumnObject> cols = getColumnsByData(conn, table_name);

		Parameter[] params = new Parameter[dataset.size()];
		for (int i = 0, size = params.length; i < size; i++) {
			IData data = (IData) dataset.get(i);
			params[i] = new Parameter();

			for (int j = 0, colsize = columns.length; j < colsize; j++) {
				Object[] colobjs = getObjectsByColumn(cols.get(columns[j]), data);

				if (i == 0)
					setstr.append(colobjs[0] + " = ?,");
				params[i].add(colobjs[1]);
			}

			Object[] keyobjs = getObjectsByKeys(conn, table_name, keys, data);
			params[i].addAll((Parameter) keyobjs[1]);

			if (i == 0)
				keystr.append(keyobjs[0]);
		}

		String sql = getUpdateSql(table_name, DaoUtil.trimSuffix(setstr.toString(), ","), keystr.toString());
		
		if (log.isDebugEnabled()) {
			log.debug("get update sql :" + conn.getUserName() + "." + table_name + ">>>" + sql);
		}
		
		return new Object[] { sql, params };
	}

	/**
	 * get objects by delete
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByDelete(DBConnection conn, String table_name, IData data) throws Exception {
		return getObjectsByDelete(conn, table_name, data, null);
	}

	/**
	 * get objects by delete
	 * 
	 * @param conn
	 * @param table_name
	 * @param dataset
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByDelete(DBConnection conn, String table_name, IDataset dataset) throws Exception {
		return getObjectsByDelete(conn, table_name, dataset, null);
	}

	/**
	 * get objects by delete
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param keys
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByDelete(DBConnection conn, String table_name, IData data, String[] keys)
			throws Exception {
		Object[] keyobjs = getObjectsByKeys(conn, table_name, keys, data);
		
		String sql = getDeleteSql(table_name, (String) keyobjs[0]);
		
		if (log.isDebugEnabled()) {
			log.debug("get delete sql :" + conn.getUserName() + "." + table_name + ">>>" + sql);
		}
		
		return new Object[] { sql, (Parameter) keyobjs[1] };
	}

	/**
	 * get objects by delete
	 * 
	 * @param conn
	 * @param table_name
	 * @param dataset
	 * @param keys
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByDelete(DBConnection conn, String table_name, IDataset dataset, String[] keys)
			throws Exception {
		StringBuilder keysql = new StringBuilder();

		Parameter[] params = new Parameter[dataset.size()];
		for (int i = 0, size = params.length; i < size; i++) {
			Object[] keyobjs = getObjectsByKeys(conn, table_name, keys, (IData) dataset.get(i));

			if (i == 0)
				keysql.append(keyobjs[0]);
			params[i] = (Parameter) keyobjs[1];
		}
		
		
		String sql = getDeleteSql(table_name, keysql.toString());
		
		if (log.isDebugEnabled()) {
			log.debug("get delete sql :" + conn.getUserName() + "." + table_name + ">>>" + sql);
		}

		return new Object[] { sql, params };
	}

	/**
	 * get objects by query
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByQuery(DBConnection conn, String table_name, IData data) throws Exception {
		return getObjectsByQuery(conn, table_name, data, null);
	}

	/**
	 * get objects by query
	 * 
	 * @param conn
	 * @param table_name
	 * @param data
	 * @param keys
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByQuery(DBConnection conn, String table_name, IData data, String[] keys)
			throws Exception {
		Object[] keyobjs = getObjectsByKeys(conn, table_name, keys, data);
		String sql = getQuerySql(table_name, (String) keyobjs[0]);
		Parameter parameter = (Parameter) keyobjs[1];
		
		if (log.isDebugEnabled()) {
			log.debug("get query sql :" + conn.getUserName() + "." + table_name + ">>>" + sql);
		}
		
		return new Object[] {sql , parameter };
	}

	/**
	 * get object by keys
	 * 
	 * @param conn
	 * @param table_name
	 * @param keys
	 * @param data
	 * @return Object[]
	 * @throws Exception
	 */
	public static Object[] getObjectsByKeys(DBConnection conn, String table_name, String[] keys, IData data)
			throws Exception {
		if (keys == null)
			keys = getPrimaryKeys(conn, table_name);

		Map<String, IColumnObject> columns = getColumnsByData(conn, table_name);

		StringBuilder sqlstr = new StringBuilder();
		Parameter param = new Parameter();

		for (int i = 0, size = keys.length; i < size; i++) {
			String key = keys[i];

			if ("ROWID".equals(key.toUpperCase())) {
				Object value = data.get(key);
				sqlstr.append(" and ").append("rowid").append(" = ?");
				param.add(value);
			} else {
				Object value = data.get(key);

				IColumnObject column = (IColumnObject) columns.get(key.toUpperCase());
				if (null == value) {
					sqlstr.append(" and t.").append(key).append(" = ?");
					param.add(value);
				} else {
					if (DaoUtil.isDatetimeColumn(column.getColumnType())) {
						sqlstr.append(" and t.").append(key);
						//sqlstr.append(getTocharSql(key, value.toString()));
						sqlstr.append(getTodateSql(value.toString(), value.toString()));
						//sqlstr.append(" = ?");
					} else {
						sqlstr.append(" and t.").append(key).append(" = ?");
					}
					param.add(value);
				}
			}
		}

		return new Object[] { DaoUtil.trimPrefix(sqlstr.toString(), " and "), param };
	}

	/**
	 * get primary keys
	 * 
	 * @param connection
	 * @param table_name
	 * @param columns
	 * @return String[]
	 * @throws Exception
	 */
	public static String[] getPrimaryKeys(DBConnection dbc, String table_name) throws Exception {
		String user = dbc.getUserName();
		
		String key = user + "_" + table_name;
		
		String[] pkeys = DaoJvmCache.getPrimaryKeys(key);
		if (pkeys != null) {
			if (log.isDebugEnabled()) {
				log.debug(">>>hit primary keys jvm : " + key);
			}
			return pkeys;
		}

		pkeys = DaoMemCache.getPrimaryKeys(key);
		if (pkeys != null) {
			DaoJvmCache.putPrimaryKeys(key, pkeys);
			
			if (log.isDebugEnabled()) {
				log.debug(">>>hit primary keys mc and put jvm : " + key);
			}
			return pkeys;
		}

		Map<String, IColumnObject> columns = getColumnsByData(dbc, table_name);
		List<String> keys = new ArrayList<String>();

		IDBDialect dialect = DBDialectFactory.getDBDialect();
		Connection conn = dbc.getConnection();
		ResultSet rs = conn.getMetaData().getPrimaryKeys(null, dialect.getSchema(conn), table_name.toUpperCase());
		
		while (rs.next()) {
			String column_name = rs.getString("COLUMN_NAME").toUpperCase();
			if (columns.containsKey(column_name) && !keys.contains(column_name))
				keys.add(column_name);
		}
		rs.close();

		String[] primaryKeys = keys.toArray(new String[0]);

		DaoJvmCache.putPrimaryKeys(key, primaryKeys);
		DaoMemCache.putPrimaryKeys(key, primaryKeys);
		
		if (log.isDebugEnabled()) {
			log.debug(">>>miss primary keys cache, put mc and jvm : " + key);
		}
		
		return primaryKeys;
	}

	/**
	 * get the columns
	 * 
	 * @param conn
	 * @param table_name
	 * @return IColumn[]
	 * @throws Exception
	 */
	public static IColumnObject[] getColumns(DBConnection conn, String table_name) throws Exception {
		Map<String, IColumnObject> columns = getColumnsByData(conn, table_name);
		List<String> keys = Arrays.asList(getPrimaryKeys(conn, table_name));

		Iterator<String> it = columns.keySet().iterator();
		List<IColumnObject> list = new ArrayList<IColumnObject>();

		while (it.hasNext()) {
			IColumnObject data = columns.get(it.next());
			data.setKey(keys.contains(data.getColumnName()) ? true : false);
			list.add(data);
		}

		return list.toArray(new IColumnObject[0]);
	}

	/**
	 * 将MC做为二级缓存，优先从JVM取，若没有则从MC取，若表字段有变更，则需要重启APP
	 * 
	 * @param conn
	 * @param table_name
	 * @return Map
	 * @throws Exception
	 */
	public static Map<String, IColumnObject> getColumnsByData(DBConnection conn, String table_name) throws Exception {
		String user = conn.getUserName();
		
		String key = user + "_" + table_name;
		
		Map<String, IColumnObject> obj = DaoJvmCache.getColumn(key);
		if (obj != null) {
			if (log.isDebugEnabled()) {
				log.debug(">>>hit table columns jvm : " + key);
			}
			return obj;
		}
		
		obj = DaoMemCache.getColumn(key);
		if (obj != null) {
			DaoJvmCache.putColumn(key, obj);
			
			if (log.isDebugEnabled()) {
				log.debug(">>>hit table columns mc and put jvm: " + key);
			}
			return obj;
		}

		Map<String, IColumnObject> columns = getColumnsByResult(conn, table_name);

		DaoJvmCache.putColumn(key, columns);
		DaoMemCache.putColumn(key, columns);
		
		if (log.isDebugEnabled()) {
			log.debug(">>>miss table columns cache, put jvm and mc: " + key);
		}
		
		return columns;
	}

	/**
	 * get columns by result
	 * 
	 * @param conn
	 * @param table_name
	 * @return Map
	 * @throws Exception
	 */
	public static Map<String, IColumnObject> getColumnsByResult(DBConnection conn, String table_name) throws Exception {
		Map<String, IColumnObject> columns = new HashMap<String, IColumnObject>();

		IColumnObject rowid = new ColumnObject();
		rowid.setColumnName("ROWID");
		rowid.setColumnType(Types.VARCHAR);
		rowid.setNullable(false);
		columns.put("ROWID", rowid);

		PreparedStatement statement = conn.getConnection().prepareStatement("select * from " + table_name.toUpperCase()
				+ " where 1 = 0");
		ResultSetMetaData metaData = statement.executeQuery().getMetaData();
		for (int i = 1, cnt = metaData.getColumnCount(); i <= cnt; i++) {
			IColumnObject column = new ColumnObject();
			column.setColumnName(metaData.getColumnLabel(i).toUpperCase());
			column.setColumnType(metaData.getColumnType(i));
			column.setColumnDesc(metaData.getColumnLabel(i));
			column.setColumnSize(metaData.getColumnDisplaySize(i));
			column.setDecimalDigits(metaData.getScale(i));
			column.setNullable(metaData.isNullable(i) == ResultSetMetaData.columnNoNulls ? false : true);

			columns.put(column.getColumnName(), column);
		}
		statement.close();
		return columns;
	}

	/**
	 * getTablesByDatabase
	 * 
	 * @param connection
	 * @param catalog
	 * @param schema
	 * @param name
	 * @return Map
	 * @throws Exception
	 */
	public static Map<String, ITableObject> getTablesByDatabase(DBConnection dbc, String catalog, String schema,
			String name) throws Exception {
		
		String user = dbc.getUserName();
		String key = user + "_" + name;
		
		Map<String, ITableObject> obj = DaoJvmCache.getTable(key);
		if (obj != null) {
			if (log.isDebugEnabled()) {
				log.debug(">>>hit table jvm : " + key);
			}
			return obj;
		}
		
		obj = DaoMemCache.getTable(key);
		if (obj != null) {
			DaoJvmCache.putTable(key, obj);
			if (log.isDebugEnabled()) {
				log.debug(">>>hit table mc and put jvm: " + key);
			}
		}
		
		Map<String, ITableObject> tables = getTablesByResult(dbc.getConnection(), catalog, schema, name);

		DaoJvmCache.putTable(key, tables);
		DaoMemCache.putTable(key, tables);
		
		if (log.isDebugEnabled()) {
			log.debug(">>>miss table cache put mc and jvm: " + key);
		}
		
		return tables;
	}

	/**
	 * getTablesByDatabase
	 * 
	 * @param conn
	 * @param catalog
	 * @param schema
	 * @param name
	 * @param types
	 * @return
	 * @throws Exception
	 */
	public static Map<String, ITableObject> getTablesByResult(Connection conn, String catalog, String schema,
			String name) throws Exception {
		Map<String, ITableObject> tables = new HashMap<String, ITableObject>();
		ResultSet rs = conn.getMetaData().getTables(catalog, schema, name, null);
		String username = conn.getMetaData().getUserName().toUpperCase();

		while (rs.next()) {
			if (!username.equals(rs.getString("TABLE_SCHEM")))
				continue;

			String table_type = rs.getString("TABLE_TYPE");
			if (!"SYNONYM".equals(table_type) && !"TABLE".equals(table_type))
				continue;

			ITableObject table = new TableObject();
			table.setTableCat(rs.getString("TABLE_CAT"));
			table.setTableSchem(rs.getString("TABLE_SCHEM"));
			table.setTableName(rs.getString("TABLE_NAME"));
			table.setTableType(table_type);
			table.setRemarks(rs.getString("REMARKS"));
			tables.put(table.getTableName(), table);
		}

		return tables;
	}

	/**
	 * reset to dataset
	 * 
	 * @param rs
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset rssetToDataset(ResultSet rs, int fetchSize) throws Exception {
		IDataset dataset = new DatasetList();
		ResultSetMetaData rsmd = rs.getMetaData();

		String[] names = null;
		int[] types = null;

		rs.setFetchSize(fetchSize);
		
		int max = DatabaseCfg.getLimit();
		
		while (rs.next() && max > 0) {
			IData data = new DataMap();
			if (null == names) {
				int size = rsmd.getColumnCount();
				names = new String[size];
				types = new int[size];
				for (int i = 1; i <= size; i++) {
					String name = rsmd.getColumnLabel(i); // 数据库字段名统一约定为大写					
					int type = rsmd.getColumnType(i);
					names[i - 1] = name;
					types[i - 1] = type;
					data.put(name, DaoUtil.getValueByResultSet(rs, type, name));
				}
			} else {
				for (int i = 0, size = names.length; i < size; i++) {
					data.put(names[i], DaoUtil.getValueByResultSet(rs, types[i], names[i]));
				}
			}
			dataset.add(data);
			max --;
		}
		if (max == 0) {
			dataset = null;
			throw new RuntimeException("查询结果集超过最大阀值:limit=" + DatabaseCfg.getLimit() + "@database.xml");
		}
		return dataset;
	}

	public static int decodeParamType(String paramName) {
		int v;
		char c = paramName.charAt(1);
		switch (c) {
		case 'n':
			v = java.sql.Types.NUMERIC;
			break;
		case 'd':
			v = java.sql.Types.TIMESTAMP;
			break;
		case 'v':
		default:
			v = java.sql.Types.VARCHAR;
		}
		return v;
	}

	public static void decodeParamInfo(String[] paramNames, int[] paramKinds, int[] paramTypes) {
		for (int i = 0, size = paramNames.length; i < size; i++) {
			paramKinds[i] = decodeParamKind(paramNames[i]);
			paramTypes[i] = decodeParamType(paramNames[i]);
		}
	}

	public static int decodeParamKind(String paramName) {
		int v;
		char c = paramName.charAt(0);
		switch (c) {
		case 'i':
			v = 0;
			break;
		case 'o':
			v = 1;
			break;
		default:
			v = 2;
		}
		return v;
	}

	public static Object callFunc(Connection conn, String name, String[] paramNames, IData params, int returnType)
			throws SQLException {
		int[] paramKinds = new int[paramNames.length];
		int[] paramTypes = new int[paramNames.length];
		decodeParamInfo(paramNames, paramKinds, paramTypes);
		return callFunc(conn, name, paramNames, params, paramKinds, paramTypes, returnType);
	}

	/**
	 * 增强存储过程接口，Type1 procedure PRC_NAME(i in wade_idata, o out wade_idata)
	 * 
	 * @param prcName
	 * @param in
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static IData procCallType1(Connection conn, String prcName, IData in) throws Exception {
		String sql = "begin " + prcName + "(?,?);end;";
		java.sql.CallableStatement cs = conn.prepareCall(sql);

		cs.setObject(1, DataOraMap.toOraStruct(in, conn), oracle.jdbc.OracleTypes.STRUCT);
		cs.registerOutParameter(2, oracle.jdbc.OracleTypes.STRUCT, "WADE_IDATA");
		cs.execute();
		oracle.sql.STRUCT o = (oracle.sql.STRUCT) cs.getObject(2);
		IData retval = DataOraMap.fromOraStruct(o);
		cs.close();
		return retval;

	}

	static Connection doGetNativeConnection(Connection conn) throws SQLException {
		return conn;
	}

	/**
	 * 增强存储过程接口，Type2 procedure PRC_NAME(idata in wade_idata, cr odata
	 * sys_refcursor) authid current_user
	 * 
	 * @param prcName
	 * @param in
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static IDataset procCallType2(Connection conn, String prcName, IData in) throws Exception {
		String sql = "begin " + prcName + "(?,?);end;";
		Connection nativeCon = doGetNativeConnection(conn);
		if (nativeCon == conn) {
			DatabaseMetaData metaData = conn.getMetaData();
			if (metaData != null) {
				Connection metaCon = metaData.getConnection();
				if (metaCon != conn) {
					nativeCon = doGetNativeConnection(metaCon);
				}
			}
		}
		CallableStatement cs = nativeCon.prepareCall(sql);

		cs.setObject(1, DataOraMap.toOraStruct(in, nativeCon), OracleTypes.STRUCT);
		cs.registerOutParameter(2, OracleTypes.CURSOR);

		cs.execute();
		ResultSet rs = (ResultSet) ((OracleCallableStatement) cs).getCursor(2);
		IDataset retval = new DatasetList();

		while (rs.next()) {
			ResultSetMetaData rsmd = rs.getMetaData();

			IData data = new DataMap();
			for (int i = 1, size = rsmd.getColumnCount(); i <= size; i++) {
				String name = rsmd.getColumnName(i).toUpperCase();
				data.put(name, DaoHelper.getValueByResultSet(rs, rsmd.getColumnType(i), name));

				// if (rs.isFirst()) names.add(name);
			}

			retval.add(data);
		}

		rs.close();
		cs.close();
		return retval;

	}

	/**
	 * get result value
	 * 
	 * @param rs
	 * @param type
	 * @param name
	 * @return Object
	 * @throws Exception
	 */
	public static Object getValueByResultSet(ResultSet rs, int type, String name) throws Exception {
		if (type == Types.BLOB) {
			return rs.getBlob(name);
		} else {
			return rs.getString(name);
		}
	}

	public static void callProc(Connection conn, String name, String[] paramNames, IData params) throws SQLException {
		int[] paramKinds = new int[paramNames.length];
		int[] paramTypes = new int[paramNames.length];
		decodeParamInfo(paramNames, paramKinds, paramTypes);
		callProc(conn, name, paramNames, params, paramKinds, paramTypes);
	}

	/**
	 * 调用存储过程(函数)
	 * 
	 * @return Object - 存储过程返回的值, 类型由returnType决定
	 * @param conn
	 *            - JDBC连接
	 * @param name
	 *            - 存储过程名字
	 * @param paramNames
	 *            - 参数名字数组，必须与存储过程声明的参数顺序一致，名字不一定要与过程参数名一样
	 * @param params
	 *            - 存放每个参数对应的输入值，调用完成后保存对应参数的输出值，名字必须与paramNames中声明的一样
	 * @param paramKinds
	 *            - 参数输入输出类型数组，依次声明每个参数的输入输出类型，0:IN 1:OUT 2:IN OUT
	 * @param paramTypes
	 *            - 参数数据类型数组，一次声明每个参数的数据类型，参考java.sql.Types中的常量
	 * @param returnType
	 *            - 存储过程返回值的数据类型，参考java.sql.Types中的常量
	 * @throws SQLException
	 */
	public static Object callFunc(Connection conn, String name, String[] paramNames, IData params, int[] paramKinds,
			int[] paramTypes, int returnType) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("{?=call ");
		sb.append(name);
		sb.append("(");
		int i, nameSize = paramNames.length;
		for (i = 0; i < nameSize; i++) {
			sb.append("?");
			if (i < nameSize - 1) {
				sb.append(",");
			}
		}
		sb.append(")}");

		CallableStatement stmt = conn.prepareCall(sb.toString());

		if (log.isDebugEnabled())
			log.debug("execute func >>>" + sb.toString());

		stmt.registerOutParameter(1, returnType);

		if (log.isDebugEnabled())
			log.debug("bind func [" + name + "] out param >>>index:" + 1 + ",return type:" + returnType);

		for (i = 0; i < nameSize; i++) {
			String paramName = paramNames[i];
			int paramKind = paramKinds[i];
			int paramIdx = i + 2;
			if ((paramKind == 0) || (paramKind == 2)) {

				if (log.isDebugEnabled())
					log.debug("bind func [" + name + "] in param >>>index:" + paramIdx + ",name:" + paramName
							+ ",value:" + params.get(paramName) + ",type:" + paramTypes[i]);

				stmt.setObject(paramIdx, params.get(paramName), paramTypes[i]);
			}
			if ((paramKind == 1) || (paramKind == 2)) {

				if (log.isDebugEnabled())
					log.debug("bind func [" + name + "] out param >>>index:" + paramIdx + ",type:" + paramTypes[i]);

				stmt.registerOutParameter(paramIdx, paramTypes[i]);
			}
		}

		stmt.execute();
		for (i = 0; i < nameSize; i++) {
			String paramName = paramNames[i];
			int paramKind = paramKinds[i];
			int paramIdx = i + 2;
			if ((paramKind == 1) || (paramKind == 2)) {
				params.put(paramName, stmt.getObject(paramIdx));
			}
		}
		Object retval = stmt.getObject(1);
		stmt.close();
		return retval;
	}

	/**
	 * 调用存储过程
	 * 
	 * @param conn
	 *            - JDBC连接
	 * @param name
	 *            - 存储过程名字
	 * @param paramNames
	 *            - 参数名字数组，必须与存储过程声明的参数顺序一致，名字不一定要与过程参数名一样
	 * @param params
	 *            - 存放每个参数对应的输入值，调用完成后保存对应参数的输出值，名字必须与paramNames中声明的一样
	 * @param paramKinds
	 *            - 参数输入输出类型数组，依次声明每个参数的输入输出类型，0:IN 1:OUT 2:IN OUT
	 * @param paramTypes
	 *            - 参数数据类型数组，一次声明每个参数的数据类型，参考java.sql.Types中的常量
	 * @throws SQLException
	 */
	public static void callProc(Connection conn, String name, String[] paramNames, IData params, int[] paramKinds,
			int[] paramTypes) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("{call ");
		sb.append(name);
		sb.append("(");
		int i,nameSize = paramNames.length;
		for (i = 0; i < nameSize; i++) {
			sb.append("?");
			if (i < nameSize - 1) {
				sb.append(",");
			}
		}
		sb.append(")}");

		if (log.isDebugEnabled())
			log.debug("sql proc [" + name + "] >>>" + sb.toString());

		CallableStatement stmt = conn.prepareCall(sb.toString());
		for (i = 0; i < nameSize; i++) {
			String paramName = paramNames[i];
			int paramKind = paramKinds[i];
			int paramIdx = i + 1;
			if ((paramKind == 0) || (paramKind == 2)) {
				if (log.isDebugEnabled())
					log.debug("bind proc [" + name + "] in param >>>index:" + paramIdx + ",name:" + paramName
							+ ",value:" + params.get(paramName) + ",type:" + paramTypes[i]);

				stmt.setObject(paramIdx, params.get(paramName), paramTypes[i]);
			}
			if ((paramKind == 1) || (paramKind == 2)) {
				if (log.isDebugEnabled())
					log.debug("bind proc [" + name + "] out param >>>index:" + paramIdx + ",type:" + paramTypes[i]);

				stmt.registerOutParameter(paramIdx, paramTypes[i]);
			}
		}

		stmt.execute();

		for (i = 0; i < nameSize; i++) {
			String paramName = paramNames[i];
			int paramKind = paramKinds[i];
			int paramIdx = i + 1;
			if ((paramKind == 1) || (paramKind == 2)) {
				params.put(paramName, stmt.getObject(paramIdx));
			}
		}
		stmt.close();
	}

}