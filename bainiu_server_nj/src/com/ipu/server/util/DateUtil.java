package com.ipu.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: DateUtil
 * @Description: 日期工具类
 */
public abstract class DateUtil extends DateUtils
{
    private static final Object lockObj = new Object();
    /**
     * @Fields SECOND : 1秒=多少毫秒
     */
    public static final long SECOND = 1000L;
    
    /**
     * @Fields MINUTE : 1分钟等于多少毫秒
     */
    public static final long MINUTE = 60L * SECOND;
    
    /**
     * @Fields HOUR : 一小时等于多少毫秒
     */
    public static final long HOUR = 60L * MINUTE;
    
    /**
     * @Fields DAY : 一天等于多少毫秒
     */
    public static final long DAY = 24L * HOUR;
    
    /**
     * @Fields WEEK : 一星期等于多少毫秒
     */
    public static final long WEEK = 7L * DAY;
    
    /**
     * @Fields YEAR_FORMAT : 年
     */
    public static final String YEAR_FORMAT = "yyyy";
    
    /**
     * @Fields MONTH_FORMAT : 年月
     */
    public static final String MONTH_FORMAT = "yyyy-MM";
    
    /**
     * @Fields DAY_FORMAT : 年月日
     */
    public static final String DAY_FORMAT = "yyyy-MM-dd";
    
    /**
     * @Fields HOUR_FORMAT : 年月日小时
     */
    public static final String HOUR_FORMAT = "yyyy-MM-dd HH";
    
    /**
     * @Fields MINUTE_FORMAT : 年月日小时分钟
     */
    public static final String MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    
    /**
     * @Fields SECOND_FORMAT : 年月日小时分钟秒
     */
    public static final String SECOND_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * @Fields MONTH_NUMBER_FORMAT : 年月
     */
    public static final String MONTH_NUMBER_FORMAT = "yyyyMM";
    
    /**
     * @Fields DAY_NUMBER_FORMAT : 年月日
     */
    public static final String DAY_NUMBER_FORMAT = "yyyyMMdd";
    
    /**
     * @Fields HOUR_NUMBER_FORMAT : 年月日小时
     */
    public static final String HOUR_NUMBER_FORMAT = "yyyyMMddHH";
    
    /**
     * @Fields MINUTE_NUMBER_FORMAT : 年月日小时分钟
     */
    public static final String MINUTE_NUMBER_FORMAT = "yyyyMMddHHmm";
    
    /**
     * @Fields SECOND_NUMBER_FORMAT : 年月日小时分钟秒
     */
    public static final String SECOND_NUMBER_FORMAT = "yyyyMMddHHmmss";

    /**
     * @Fields MONTH_NUMBER_FORMAT : 年月
     */
    public static final String MONTH_MM_FORMAT = "MM";

    /**
     * @Fields DEFAULT_PATTERN : 默认日期格式化类
     */
    public static final String DEFAULT_PATTERN = SECOND_FORMAT;
    
    /**
     * @Fields DAY_NUM_OF_WEEK : 一周有7天
     */
    public static final int DAY_NUM_OF_WEEK = 7;
    
    /**
     * @Fields WORKING_DAY_OF_WEEK : 一周有5个工作日
     */
    public static final int WORKING_DAY_OF_WEEK = 5;
    public static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * @Fields FORMATS_CROSS_BAR_SPACE : 带横杠和空格的日期格式
     */
    private static final String[] FORMATS_CROSS_BAR_SPACE = {SECOND_FORMAT, MINUTE_FORMAT, HOUR_FORMAT, DAY_FORMAT,
        MONTH_FORMAT, YEAR_FORMAT};
    
    /**
     * @Fields FORMATS_NUMERIC : 全数字的日期格式
     */
    private static final String[] FORMATS_NUMERIC = {SECOND_NUMBER_FORMAT, MINUTE_NUMBER_FORMAT, HOUR_NUMBER_FORMAT,
        DAY_NUMBER_FORMAT, MONTH_NUMBER_FORMAT, YEAR_FORMAT,};
    
    /**
     * @Fields FORMATS_CROSS_BAR : 带横杠无空格的日期格式
     */
    private static final String[] FORMATS_CROSS_BAR = {DAY_FORMAT, MONTH_FORMAT,};
    
    // /**
    // * @Fields FORMATS_FORWARD_SPLASH_SPACE : 带斜杠和空格的日期格式
    // */
    // private static final String[] FORMATS_FORWARD_SPLASH_SPACE = { "yyyy/MM/dd HH:mm:ss.S", "yyyy/MM/dd HH:mm:ss",
    // "yyyy/MM/dd HH:mm",
    // "MM/dd/yyyy HH:mm:ss.S", "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy HH:mm", };
    // /**
    // * @Fields FORMATS_FORWARD_SPLASH : 带斜杠不带空格的日期格式
    // */
    // private static final String[] FORMATS_FORWARD_SPLASH = { "yyyy/MM/dd", "yyyy/MM", "MM/dd/yyyy", "MM/yyyy", };
    
    // /**
    // * @Fields FORMATS_COLON : 带冒号的日期格式
    // */
    // private static final String[] FORMATS_COLON = { "HH:mm:ss", "HH:mm", };
    
    /**
     * @Fields LOGGER : 日志操作类
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);
    
    /**
     * 存储SimpleDateFormat对应格式发的String类型
     */
    private static Map<String, SimpleDateFormat> map = new HashMap<String, SimpleDateFormat>();
    
    /**
     * @Title: getNow
     * @Description: 获取jvm当前时间
     * @return jvm的当前时间
     */
    public static Date getNow()
    {
        return Calendar.getInstance().getTime();
    }
    
    /**
     * 通过时间格式化字符串获取SimpleDateFormat
     * 
     * @param parsePatterns 时间格式化
     * @return {@link SimpleDateFormat}
     */
    public static SimpleDateFormat getSimpleDateFormat(final String parsePatterns)
    {
        SimpleDateFormat dateFormat = null;
        if (map.containsKey(parsePatterns))
        {
            dateFormat = map.get(parsePatterns);
        }
        else
        {
            dateFormat = new SimpleDateFormat(parsePatterns);
            map.put(parsePatterns, dateFormat);
        }
        return dateFormat;
    }
    
    /**
     * 格式化时间，返回格式化后的时间字符串
     * 
     * <pre>
     * 例子,如有一个Date = 2012-08-09:
     * DateUtils.format(date,"yyyy-MM-dd") = "2012-08-09"
     * DateUtils.format(date,"yyyy年MM月dd日") = "2012年08月09日"
     * DateUtils.format(date,"") = null
     * DateUtils.format(date,null) = null
     * </pre>
     * 
     * @param date 时间
     * @param parsePatterns 格式化字符串
     * @return String
     */
    public static String format(final Date date, final String parsePatterns)
    {
        if (StringUtils.isEmpty(parsePatterns) || date == null)
        {
            return null;
        }
        return getSimpleDateFormat(parsePatterns).format(date);
    }
    public static Date parse_datetimeFormat(String dateStr) throws ParseException {
        return parse(dateStr, datetimeFormat);
    }
    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }
    private static ThreadLocal<Map<String, SimpleDateFormat>> threadDateFormat = new ThreadLocal<Map<String, SimpleDateFormat>>(){
        protected Map<String, SimpleDateFormat> initialValue() {
            return new HashMap<String, SimpleDateFormat>();
        }
    };

    private static SimpleDateFormat getSdf(final String pattern) {
        SimpleDateFormat sdf = threadDateFormat.get().get(pattern);
        if (sdf == null) {
            synchronized (lockObj) {
                sdf = threadDateFormat.get().get(pattern);
                if (sdf == null) {
                    sdf = new SimpleDateFormat(pattern);
                    threadDateFormat.get().put(pattern, sdf);
                }
            }
        }

        return sdf;
    }
    /**
     * 判断某个时间time2是否在另一个时间time1之前
     * 
     * @param time1 第一个时间
     * @param time2 第二个时间
     * @return 时间time2是否在另一个时间time1之前
     */
    public static boolean beforeTime(final Date time1, final Date time2)
    {
        final Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(time1);
        
        final Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(time2);
        
        return calendar1.before(calendar2);
    }
    
    /**
     * @Title: getCurrentTimeAsNumber
     * @Description: 当前时间数字化输出yyyyMMddHHmmss
     * @return yyyyMMddHHmmss格式的当前时间
     */
    public static BigDecimal getCurrentTimeAsNumber()
    {
        String returnStr = null;
        final SimpleDateFormat f = new SimpleDateFormat(SECOND_NUMBER_FORMAT);
        returnStr = f.format(getNow());
        return new BigDecimal(returnStr);
    }
    
    /**
     * 简单转换日期类型到默认字符串格式 "yyyy-MM-dd HH:mm:ss"
     * 
     * @param date 日期
     * @return String 格式化好的日期字符串 "yyyy-MM-dd HH:mm:ss"
     */
    public static String format(final Date date)
    {
        return format(date, DEFAULT_PATTERN);
    }
    
    /**
     * 按指定格式将java.util.Date日期转换为字符串 例如:2009-10-01
     * 
     * @param date 要转换的时间
     * @return "yyyy-MM-dd"日期的字符串
     */
    public static String getDate(final Date date)
    {
        return getDateTime(DAY_FORMAT, date);
    }

    /**
     * 按指定格式将java.util.Date日期转换为字符串 例如:2009-01-01 15:02:01
     *
     * @param date 要转换的时间
     * @return 例如:2009-01-01 15:02:01格式的时间字符串
     */
    public static String getDateTime(final Date date)
    {
        return getDateTime(SECOND_FORMAT, date);
    }

    /**
     * 按给定格式转换java.util.Date日期为字符串
     *
     * @param pattern 指定日期格式
     * @param date 日期
     * @return 格式化后的时间
     */
    public static String getDateTime(final String pattern, final Date date)
    {
        if (date == null)
        {
            return "";
        }
        String formatPattern = pattern;
        if (pattern == null)
        {
            formatPattern = SECOND_FORMAT;
        }
        final SimpleDateFormat formatter = new SimpleDateFormat(formatPattern, Locale.getDefault());
        final String ret = formatter.format(date);
        return ret;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param date1 开始时间
     * @param date2 结束时间
     * @return 两个日期之间的天数
     */
    public static int getDaysBetween(final Calendar date1, final Calendar date2)
    {
        Calendar startDate = date1;
        Calendar endDate = date2;
        if (startDate.after(endDate))
        {
            final Calendar swap = startDate;
            startDate = endDate;
            endDate = swap;
        }
        int days = endDate.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);
        final int y2 = endDate.get(Calendar.YEAR);
        if (startDate.get(Calendar.YEAR) != y2)
        {
            startDate = (Calendar)startDate.clone();
            do
            {
                days += startDate.getActualMaximum(Calendar.DAY_OF_YEAR);
                startDate.add(Calendar.YEAR, 1);
            } while (startDate.get(Calendar.YEAR) != y2);
        }
        return days;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 间隔的天数
     */
    public static int getDaysBetween(final Date startDate, final Date endDate)
    {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(startDate);
        cal2.setTime(endDate);
        return getDaysBetween(cal1, cal2);
    }

    /**
     * 计算两个日期之间的假期天数（仅仅是正常休息日即周六周日）
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 中间的假期天数
     */
    public static int getHolidays(final Calendar startDate, final Calendar endDate)
    {
        return getDaysBetween(startDate, endDate) - getWorkingDay(startDate, endDate);
    }

    /**
     * 获得指定日期的下一个星期一的日期
     *
     * @param date 日期
     * @return 下一个周一的日期
     */
    public static Calendar getNextMonday(final Calendar date)
    {
        Calendar result = date;
        do
        {
            result = (Calendar)result.clone();
            result.add(Calendar.DATE, 1);
        } while (result.get(Calendar.DAY_OF_WEEK) != 2);
        return result;
    }

    /**
     * @Title: getNowYearMonthDay
     * @Description: 获取当前纯数字的日期
     * @return yyyyMMdd格式的当前日期
     */
    public static int getNowYearMonthDay()
    {
        String returnStr = null;
        final SimpleDateFormat f = new SimpleDateFormat(DAY_NUMBER_FORMAT);
        returnStr = f.format(getNow());
        return Integer.parseInt(returnStr);
    }

    /**
     * 计算两个日期之间的工作天数
     *
     * @param date1 开始日期
     * @param date2 结束日期
     * @return 两个日期之间的周一到周五天数
     */
    public static int getWorkingDay(final Calendar date1, final Calendar date2)
    {
        int result = -1;
        Calendar startDate = date1;
        Calendar endDate = date2;
        if (startDate.after(endDate))
        {
            final Calendar swap = startDate;
            startDate = endDate;
            endDate = swap;
        }
        
        int chargeStartDate = 0;// 开始日期的日期偏移量
        int chargeEndDate = 0;// 结束日期的日期偏移量
        // 日期不在同一个日期内
        int stmp;
        int etmp;
        stmp = DAY_NUM_OF_WEEK - startDate.get(Calendar.DAY_OF_WEEK);
        etmp = DAY_NUM_OF_WEEK - endDate.get(Calendar.DAY_OF_WEEK);
        if (stmp != Calendar.SUNDAY - 1 && stmp != Calendar.SATURDAY - 1)
        {// 开始日期为星期六和星期日时偏移量为6和0
            chargeStartDate = stmp - 1;
        }
        if (stmp != Calendar.SUNDAY - 1 && stmp != Calendar.SATURDAY - 1)
        {// 结束日期为星期六和星期日时偏移量为6和0
            chargeEndDate = etmp - 1;
        }
        result =
            (getDaysBetween(getNextMonday(startDate), getNextMonday(endDate)) / DAY_NUM_OF_WEEK) * WORKING_DAY_OF_WEEK
                + chargeStartDate - chargeEndDate;
        return result;
    }
    
    /**
     * 计算两个日期之间的工作天数
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 两个日期之间的工作日期
     */
    public static int getWorkingDay(final Date startDate, final Date endDate)
    {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(startDate);
        cal2.setTime(endDate);
        return getWorkingDay(cal1, cal2);
    }
    
    /**
     * @Title: getYearMonth
     * @Description: 获取年月yyyyMM格式数字
     * @param date 日期
     * @return 年月yyyyMM格式数字
     */
    public static int getYearMonth(final Date date)
    {
        final SimpleDateFormat formatter = new SimpleDateFormat(MONTH_NUMBER_FORMAT, Locale.getDefault());
        final String ret = formatter.format(date);
        return Integer.parseInt(ret);
    }

    /**
     * @Title: getYearMonth
     * @Description: 获取年月yyyyMM格式数字
     * @param date 日期
     * @return 年月yyyyMM格式数字
     */
    public static int getMonth(final Date date)
    {
        final SimpleDateFormat formatter = new SimpleDateFormat(MONTH_MM_FORMAT, Locale.getDefault());
        final String ret = formatter.format(date);
        return Integer.parseInt(ret);
    }

    /**
     * @Title: getYearMonthDay
     * @Description: 获取日期yyyyMMDD格式数字
     * @param date 日期
     * @return 日期yyyyMMDD格式数字
     */
    public static int getYearMonthDay(final Date date)
    {
        final SimpleDateFormat formatter = new SimpleDateFormat(DAY_NUMBER_FORMAT, Locale.getDefault());
        final String ret = formatter.format(date);
        return Integer.parseInt(ret);
    }
    


    /**
     * @Title: parseDate
     * @Description: 根据格式列表，解析日期字符串
     * @param dateStr 日期字符串
     * @param formats 日期格式列表
     * @return 日期格式
     */
    public static Date parse(final String dateStr, final String[] formats)
    {
        SimpleDateFormat df = null;
        for (final String dateFormat : formats)
        {
            df = new SimpleDateFormat(dateFormat);
            try
            {
                return df.parse(dateStr);
            }
            catch (final ParseException e)
            {
                LOGGER.warn("Date {} does not match with the format {}", dateStr, dateFormat);
            }
        }
        return null;
    }

    /**
     * 求两个时间的毫秒值的差，第一个减去第二个为结果
     *
     * @param date1 开始日期
     * @param date2 结束日期
     * @return 毫秒
     */
    public static long poor(final Date date1, final Date date2)
    {
        if (null == date1 || null == date2)
        {
            return 0;
        }
        final Calendar calendar1 = Calendar.getInstance();
        final Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        final long millisecond = calendar1.getTimeInMillis() - calendar2.getTimeInMillis();
        return millisecond;
    }

    // 获得当天0点时间

    /**
     * @Title: getDateAfter
     * @Description: 获取指定天数后的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day)
    {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 根据周期获取两个时间段内的天数
     *
     * @param date
     * @param py
     * @param cycle
     * @return
     */
    public static int getDaysByRule(Date date, int py, int cycle)
    {

        Date newDate = new Date();
        //周
        if (cycle == 2)
        {

            newDate = addDayas(date, 7*py);
        }

        //月
        else if (cycle == 3)
        {

            newDate = addMonth(date, py);
        }

        else
        {
            return py;
        }

        return getDaysBetween(date, newDate);
    }

    /**
     * 日期加N天
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDayas(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        Date newDate = cal.getTime();
        return newDate;
    }

    /**
     * 日期加N天
     *
     * @param date
     * @param days
     * @return
     */
    public static Date setDatesForday(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, days);
        Date newDate = cal.getTime();
        return newDate;
    }

    /**
     * 日期加N月
     *
     * @param date
     * @param months
     * @return
     */
    public static Date addMonth(Date date, int months)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        Date newDate = cal.getTime();
        return newDate;
    }
    
}
