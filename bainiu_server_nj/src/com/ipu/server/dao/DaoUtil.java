package com.ipu.server.dao;


import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public final class DaoUtil {
		
	private DaoUtil() {}
	
	/**
	 * get timestamp format
	 * @param value
	 * @return String
	 */
	public static final String getTimestampFormat(String value) {
		switch (value.length()) {
			case 4:
				return "yyyy";
			case 6:
				return "yyyyMM";
			case 7:
				return "yyyy-MM";
			case 8:
				return "yyyyMMdd";
			case 10:
				return "yyyy-MM-dd";
			case 13:
				return "yyyy-MM-dd HH";
			case 14:
				return "yyyyMMddHHmmss";
			case 15:
				return "yyyyMMddHHmmssS";
			case 16:
				return "yyyy-MM-dd HH:mm";
			case 19:
				return "yyyy-MM-dd HH:mm:ss";
			case 21:
				return "yyyy-MM-dd HH:mm:ss.S";
		}
		
		throw new IllegalArgumentException("无法解析正确的日期格式[" + value + "]");
	}
	
	/**
	 * encode timestamp
	 * @param timeStr
	 * @return Timestamp
	 * @throws Exception
	 */
	public static final Timestamp encodeTimestamp(String timeStr) {
		String format = getTimestampFormat(timeStr);
		return encodeTimestamp(format, timeStr);
	}
	
	/**
	 * encode timestamp
	 * @param format
	 * @param timeStr
	 * @return Timestamp
	 * @throws Exception
	 */
	public static final Timestamp encodeTimestamp(String format, String timeStr) {
		
		if (null == timeStr || timeStr.length() <= 0) {
			return null;
		}
		
		if (format.length() != timeStr.length()) format = getTimestampFormat(timeStr);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return new Timestamp(sdf.parse(timeStr).getTime());
		} catch (ParseException e) {
			throw new IllegalArgumentException(String.format("错误的日期格式%s,%s", format, timeStr));
		}
	}
	
	/**
	 * decode timestamp
	 * @param format
	 * @param timeStr
	 * @return String
	 * @throws Exception
	 */
	public static final String decodeTimestamp(String format, String timeStr) throws Exception {
		Timestamp time = encodeTimestamp(format, timeStr);
		return decodeTimestamp(format, time);
	}
	
	/**
	 * decode timestamp
	 * @param format
	 * @param time
	 * @return String
	 * @throws Exception
	 */
	public static final String decodeTimestamp(String format, Timestamp time) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}
	
	/**
	 * trim prefix
	 * @param str
	 * @param suffix
	 * @return String
	 */
	public static final String trimPrefix(String str, String prefix) {
		return str.startsWith(prefix) ? str.substring(prefix.length()) : str;
	}
	
	/**
	 * trim suffix
	 * @param str
	 * @param suffix
	 * @return String
	 */
	public static final String trimSuffix(String str, String suffix) {
		return str.endsWith(suffix) ? str.substring(0, str.length() - 1) : str;
	}
		
	/**
	 * is datetime column
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public static final boolean isDatetimeColumn(int columnType) throws Exception {
		return columnType == Types.DATE || columnType == Types.TIME || columnType == Types.TIMESTAMP;
	}

	/**
	 * get result value
	 * 
	 * @param rs
	 * @param type
	 * @param name
	 * @return String
	 * @throws Exception
	 */
	public static final String getValueByResultSet(ResultSet rs, int type, String name) throws Exception {
		if (type == Types.BLOB) {
			return null;
		} /*else if (type == Types.DATE) {
			return rs.getDate(name, Calendar.getInstance()).toString();
		}*/ else {
			return rs.getString(name);
		}
	}

	/**
	 * decode param info
	 * @param paramNames
	 * @param paramKinds
	 * @param paramTypes
	 */
	public static void decodeParamInfo(String[] paramNames, int[] paramKinds, int[] paramTypes) {
		for (int i = 0, size = paramNames.length; i < size; i++) {
			paramKinds[i] = decodeParamKind(paramNames[i]);
			paramTypes[i] = decodeParamType(paramNames[i]);
		}
	}

	/**
	 * decode param kind
	 * @param paramName
	 * @returnint
	 */
	public static final int decodeParamKind(String paramName) {
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
	
	/**
	 * decode param type
	 * @param paramName
	 * @return int
	 */
	public static final int decodeParamType(String paramName) {
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

	/**
	 * 判断字符是否为参数字符
	 * 
	 * @author zhoulin2
	 * @param c
	 * @return
	 */
	public static final boolean isVariableChar(char c) {
		if (48 <= c && c <= 57)  return true; // 0-9
		if (65 <= c && c <= 90)  return true; // A-Z
		if (97 <= c && c <= 122) return true; // a-z
		if (95 == c)             return true; // _

		return false;
	}
	
}
