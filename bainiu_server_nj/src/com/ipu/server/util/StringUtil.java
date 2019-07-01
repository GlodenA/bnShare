package com.ipu.server.util;

import java.io.File;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.mobile.db.dao.impl.AbstractDAO;
import com.ailk.mobile.util.MobileUtility;
import com.ipu.server.dao.BaseDAO;

public class StringUtil {
	public static int UserIdSeq = 1; 
	public static int ExIdSeq = 2;
	public static int LogIdSeq = 3;

	
	public static String StrUserIdSeq = "userId"; 
	public static String StrExIdSeq = "exId";
	public static String StrLogIdSeq = "logId";
	public static String StrIsIdSeq = "isId";
	public static String StrTempletIdSeq = "templetId";
	public static String StrSmsIdSeq = "smsId";
	public static String StrGroupIdSeq = "groupId";
	public static String StrN6DevGroupId = "10004";
	public final static int DES_ENCRYPT = 0;
	public final static int DES_DECIPHER = 1;
	public final static String domain = "http://www.bainiu6.com";//本机127.0.0.1:7002
	public final static String innerdomain = "http://10.20.16.75:8070";
	public final static String ISHARE_EMAIL =  "iShare@asiainfo.com";
	
	public static String UPD_CHK_LOCKINFO = "系统升级暂时无法登陆，请稍候再试";
	public static String USER_LOGIN_ERRPWD = "登陆失败,用户名或密码不正确";
	//不支持负载均衡的获取序列
	public static String getSequence(BaseDAO dao,int SeqType) throws Exception{
		SeqMaker sm = null;
		try {
			sm = SeqMaker.getSeqMaker();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sm.getSeqStr(SeqType);
	}
	public static String getSequenceNew(BaseDAO dao,String SeqType)  throws Exception{
		SeqMaker sm = null;
		try {
			sm = SeqMaker.getSeqMaker();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sm.getSeqStr(SeqType);
	}
	//支持负载待优化
//	 public static String getSequence(BaseDAO dao,int SeqType) {
//		IData param = new DataMap();
//		long sum;
//		try {
//			switch(SeqType){
//			case 2:
//				dao.executeUpdate("UPDATE TD_S_SEQUENCE SET CURRENT_VALUE=CURRENT_VALUE+INCREMENT WHERE NAME='exId' ", param);
//				dao.commit();
//				param =dao.queryList("SELECT CURRENT_VALUE,INCREMENT,DATE_FORMAT(SYSDATE(),'%Y%m%d') NOW_DATE FROM TD_S_SEQUENCE WHERE NAME = 'exId' ",param).first();
//				break;
//			case 1:
//				dao.executeUpdate("UPDATE TD_S_SEQUENCE SET CURRENT_VALUE=CURRENT_VALUE+INCREMENT WHERE NAME='userId' ", param);
//				dao.commit();
//				param =dao.queryList("SELECT CURRENT_VALUE,INCREMENT,DATE_FORMAT(SYSDATE(),'%Y%m%d') NOW_DATE FROM TD_S_SEQUENCE WHERE NAME = 'userId' ",param).first();
//				break;
//			case 3:
//				dao.executeUpdate("UPDATE TD_S_SEQUENCE SET CURRENT_VALUE=CURRENT_VALUE+INCREMENT WHERE NAME='logId' ", param);
//				dao.commit();
//				param =dao.queryList("SELECT CURRENT_VALUE,INCREMENT,DATE_FORMAT(SYSDATE(),'%Y%m%d') NOW_DATE FROM TD_S_SEQUENCE WHERE NAME = 'logId' ",param).first();
//				break;
//		}
//		long nowLong = param.getLong("NOW_DATE");
//		sum = nowLong*100000000 + param.getLong("CURRENT_VALUE");
//		return String.valueOf(sum);
//
//		} catch (Exception e) {
//			MobileUtility.error("获取序列失败");
//		}
		
//		return null;
//
//	 }
	 
	 
	 
public static byte[] hex2byte(byte[] b) {
		 if ((b.length % 2) != 0)
			 throw new IllegalArgumentException("长度不是偶数");
		 byte[] b2 = new byte[b.length / 2];
		 for (int n = 0; n < b.length; n += 2) {
			 String item = new String(b, n, 2);
			 b2[n / 2] = (byte) Integer.parseInt(item, 16);
		 }
		
		
		return b2;
		
	}
	private static String byte2hex(byte[] b) {
		// TODO Auto-generated method stub
		 String hs = "";
		 String stmp = "";
		 for (int n = 0; n < b.length; n++) {
			 stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			 if (stmp.length() == 1)
				 hs = hs + "0" + stmp;
			 else
				 hs = hs + stmp;
		 }
		return hs.toLowerCase();
	}
	 
//	 	public static String getSequenceNew(BaseDAO dao,String SeqType) {
//			IData param = new DataMap();
//			String sum;
//
//			try
//			{
//				StringBuffer strBuf = new StringBuffer();
//				strBuf.append("select getseqid('");
//				strBuf.append(SeqType);
//				strBuf.append("','') CURRENT_VALUE");
//				param = dao.queryList(strBuf.toString(), param).first();
//				sum = param.getString("CURRENT_VALUE");
//				return sum;
//			} catch (Exception e) {
//				MobileUtility.error("获取序列失败");
//			}
//			return null;
//		 }
	 
		public static boolean isNull(String p_str) {
			if (p_str != null && !p_str.trim().equals(""))
				return false;
			else
				return true;
		}
		/**
		 * get timestamp format
		 * @param value
		 * @return String
		 */
		public static String getTimestampFormat(String value) {
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
				case 16:
					return "yyyy-MM-dd HH:mm";
				case 19:
					return "yyyy-MM-dd HH:mm:ss";
				case 21:
					return "yyyy-MM-dd HH:mm:ss.S";
			}
			return null;
		}

		/**
		 * encode timestamp
		 * @param timeStr
		 * @return Timestamp
		 * @throws Exception
		 */
		public static Timestamp encodeTimestamp(String timeStr) throws Exception {
			String format = getTimestampFormat(timeStr);
			return encodeTimestamp(format, timeStr);
		}
		
		/**
		 * encode timestamp
		 * @param format
		 * @param timeStr
		 * @return Timestamp
		 * @throws Exception
		 * modified by caom on 08.7.28, check timeStr is null
		 */
		public static Timestamp encodeTimestamp(String format, String timeStr) throws Exception {
			if (null == timeStr || "".equals(timeStr))return null;
			if (format.length() != timeStr.length()) format = getTimestampFormat(timeStr);
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return new Timestamp(sdf.parse(timeStr).getTime());
		}
		
		/**
		 * decode timestamp
		 * @param format
		 * @param timeStr
		 * @return String
		 * @throws Exception
		 */
		public  static String decodeTimestamp(String format, String timeStr) throws Exception {
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
		public static String decodeTimestamp(String format, Timestamp time) throws Exception {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(time);
		}

		/**
		 * get current time
		 * @return Timestamp
		 * @throws Exception
		 */
		public static Timestamp getCurrentTime() throws Exception {
			return new Timestamp(System.currentTimeMillis());
		}
		
		/**
		 * get sys time
		 * @return String
		 * @throws Exception
		 */
		public static String getSysTime() throws Exception {
			return decodeTimestamp("yyyy-MM-dd HH:mm:ss", new Timestamp(System.currentTimeMillis()));
		}
		
		/**
		 * get sys date
		 * @return String
		 * @throws Exception
		 */
		public static String getSysDate() throws Exception {
			return decodeTimestamp("yyyy-MM-dd", new Timestamp(System.currentTimeMillis()));
		}
		
		/**
		 * get last day
		 * @return String
		 * @throws Exception
		 */
		public static String getLastDay() throws Exception {
			return getLastDay(getSysDate());
		}
		
		/**
		 * get last day
		 * @return String
		 * @throws Exception
		 */
		public static String getLastDay(String timestr) throws Exception {
			Calendar cal = Calendar.getInstance();
			cal.setTime(encodeTimestamp(timestr));
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			return dateformat.format(cal.getTime());
		}

		/**
		 * get prev day by curr date
		 * @return String
		 * @throws Exception
		 */
		public static String getPrevDayByCurrDate() throws Exception {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);

			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			return dateformat.format(cal.getTime());
		}
		
		/**
		 * format decimal
		 * @param format <"#.##(mentisia lack ignore)、0.00(appoint mentisia，lack add 0>"
		 * @param decimal
		 * @return String
		 * @throws Exception
		 */
		public static String formatDecimal(String format, double decimal) throws Exception {
			DecimalFormat df = new DecimalFormat(format);
			return df.format(decimal);
		}
		
		/**
		 * @param haha
		 * @return
		 */
		public static JSONArray getTieStr2Json(String haha) {
			JSONArray json = null;
			if(haha.length()>0&&haha.substring(0,haha.length()-1).split("\\|").length>1){

				String newstr = haha.substring(0,haha.length()-1).split("\\|")[1];
				newstr = newstr.replace(",", "','").replace(":", "':'").replace(",'工号", "},{'工号");
				newstr = "[{'"+newstr+"'}]";
				json = JSONArray.fromObject(newstr); //首先把字符串转成 JSONArray  对象
				
			}
			return json;
		}
		/**
		 * @param json
		 * @return
		 */
		public static IDataset JSONArray2IDataset(JSONArray json){
			IDataset infos = new DatasetList();
			if(json!=null&&json.size()>0){
				  for(int i=0;i<json.size();i++){
				    JSONObject job = json.getJSONObject(i);    //遍历 jsonarray 数组，把每一个对象转成 json 对象
				    Iterator it = job.keys();
				    IData buf = new DataMap();
			        while(it.hasNext()){
			        	String key = it.next().toString();
			        	buf.put(key, job.get(key));
			        }
			        infos.add(buf);
				  }
			}
			return infos;
		}
		
		/**
		 * @param haha
		 * @return IDataset
		 */
		public static IDataset getTieStr2IDataset(String haha) {
			JSONArray json = getTieStr2Json(haha);
			return JSONArray2IDataset(json);
		}	 


		/**
		 * @param
		 * @return IDataset
		 * @throws Exception 
		 */
		public static DatasetList increaseRowNo(DatasetList IDS) throws Exception {
			
			if(null==IDS)
			{
				throw new Exception("集合不存在");
			}
			
			 for (int i = 0; i < IDS.size(); i++) 
			 {
				Object o =IDS.get(i);
				
				if(o instanceof IData)
				{
					((IData) o).put("RowNo", i+1);
				}
			   
			 }
			 return IDS;
		}

		public static IDataset addMap2IDataset(IDataset infos,
				IData Buf) throws Exception{
			if(null!=infos)
			{
				for(int i=0;i<infos.size();i++){
					IData map =infos.getData(i);
					map.putAll(Buf);
				}
				
			}
		
			return infos;
		}
		
		public static String getLocalIp(HttpServletRequest request) {
	        String remoteAddr = request.getRemoteAddr();
	        String forwarded = request.getHeader("X-Forwarded-For");
	        String realIp = request.getHeader("X-Real-IP");

	        String ip = null;
	        if (realIp == null) {
	            if (forwarded == null) {
	                ip = remoteAddr;
	            } else {
	                ip = remoteAddr + "/" + forwarded.split(",")[0];
	            }
	        } else {
	            if (realIp.equals(forwarded)) {
	                ip = realIp;
	            } else {
	                if(forwarded != null){
	                    forwarded = forwarded.split(",")[0];
	                }
	                ip = realIp + "/" + forwarded;
	            }
	        }
	        return ip;
	    }

	 	public static String getCodeName(BaseDAO dao,String strTableName,String strCode,String strValue) {
			IData param = new DataMap();
			String strName = "";

			try
			{
				StringBuffer strBuf = new StringBuffer();
				strBuf.append("SELECT ENUM_NAME FROM TD_S_ENUMERATE WHERE TABLE_CODE = '");
				strBuf.append(strTableName);
				strBuf.append("' AND COL_CODE='");
				strBuf.append(strCode);
				strBuf.append("' AND ENUM_CODE = '");
				strBuf.append(strValue);
				strBuf.append("'");
				param = dao.queryList(strBuf.toString(), param).first();
				strName = param.getString("ENUM_NAME");
				return strName;
			} catch (Exception e) {
				MobileUtility.error("翻译"+strTableName+"."+strCode+"="+strValue+"失败");
			}
			return "";
		 }
	 	
	 	/*
	 	 * endDate > startDate 返回true
	 	 * */
	 	public static boolean compareTime(String endDate,String startDate) throws ParseException{
	 		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 		Date dateE = formatter.parse(endDate);
	 		Date dateS = formatter.parse(startDate);
            if (dateE.getTime() > dateS.getTime()) {
                return true;
            } else {//相等
                return false;
            }
	 	}
	 	
	 	public static int compDateDif(String endDate,String startDate) throws ParseException{
	 		
	 		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
	 		Date dateE = formatter.parse(endDate);
	 		Date dateS = formatter.parse(startDate);
	 		int dif = dateDiff(dateE,dateS)+1;
	 		return dif;
	 	}
	 	
		public static int dateDiff(Date date1, Date date2) {
	        Calendar cal1 = Calendar.getInstance();
	        Calendar cal2 = Calendar.getInstance();
	        cal1.setTime(date1);
	        cal2.setTime(date2);
	        long ldate1 = date1.getTime() + cal1.get(Calendar.ZONE_OFFSET) + cal1.get(Calendar.DST_OFFSET);
	        long ldate2 = date2.getTime() + cal2.get(Calendar.ZONE_OFFSET) + cal2.get(Calendar.DST_OFFSET);
	        // Use integer calculation, truncate the decimals
	        int hr1 = (int) (ldate1 / 3600000); // 60*60*1000
	        int hr2 = (int) (ldate2 / 3600000);

	        int days1 = hr1 / 24;
	        int days2 = hr2 / 24;

	        int dateDiff = days1 - days2;
	        return dateDiff;
	    }
		public static String praseStrByDES(String str,String rule,int mode) throws Exception {
			SecureRandom sr = new SecureRandom();
			DESKeySpec dks = new DESKeySpec(rule.getBytes());
		    // 创建一个密匙工厂，然后用它把DESKeySpec转换成       
			// 一个SecretKey对象        
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(dks);
			// 用密匙初始化Cipher对象
			Cipher cipher = Cipher.getInstance("DES");
			if(mode == DES_ENCRYPT){
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
				return byte2hex(cipher.doFinal(str.getBytes()));
			}
			if(mode == DES_DECIPHER){
				cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
				return new String(cipher.doFinal(hex2byte(str.getBytes())));
			}
			return null;
		}
		/*
		 * 通用空判断获取
		 * */
		public static String getStringNew(IData sou,String key,String defaultvalue) throws Exception {
			if(sou.get(key)==null)
				return defaultvalue;
			else
				return sou.get(key).toString();
		}
}
