package com.jm.bapkp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 日期工具类
 * 
 */
public class DateUtil {

	/**
	 * 获取调整日期
	 * 
	 * @param originalDate
	 *            被调整的日期，null表示当前时间
	 * @param type
	 *            {@link `}类定义的各种常量，年、月、周、日、时、分、秒、毫秒
	 * @param value
	 *            调整值 正数表示日期之后，负数表示之前
	 * @return
	 */
	public static Date adjust(Date originalDate, int type, int value) {
		Calendar cal = Calendar.getInstance();

		if (originalDate != null) {
			cal.setTime(originalDate);
		}

		if (value != 0) {
			cal.add(type, value);
		}

		return cal.getTime();
	}

	/**
	 * 任意设置当前时间的时分秒
	 * 
	 */
	public static Date setTime(Date date, int hour, int minute, int second, int millisecond) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millisecond);

		return calendar.getTime();
	}

	/**
	 * 比较两个时间的年月日是否相同,不比较时间部分
	 * 
	 * @param 传入两个不同的时间
	 * @return
	 * 
	 */
	public static boolean compareDateValue(Date d1, Date d2) {
		if (d1 == null && d2 == null) {
			return true;
		}

		if (d1 != null && d2 == null) {
			return false;
		}

		if (d1 == null && d2 != null) {
			return true;
		}

		int year1 = getYearValue(d1);
		int year2 = getYearValue(d2);

		int monthValue1 = getMonthValue(d1);
		int monthValue2 = getMonthValue(d2);

		int dayValue1 = getDayValue(d1);
		int dayValue2 = getDayValue(d2);

		if (year1 == year2 && monthValue1 == monthValue2 && dayValue1 == dayValue2) {
			return true;
		}

		return false;
	}

	/**
	 * 获取当前日期的年份
	 * 
	 */
	public static int getYearValue(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取当前日期的月份
	 * 
	 */
	public static int getMonthValue(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取当前日期的日份
	 * 
	 */
	public static int getDayValue(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 按格式化字符串："yyyy-MM-dd HH:mm:ss" 格式化日期对象
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return format(date, DEF_DATETIME_FMT);
	}

	/**
	 * 指定格式化字符串，格式化日期对象
	 * 
	 * @param date
	 * @param format
	 *            <li>yyyy:年份
	 *            <li>MM:月份
	 *            <li>dd:日期
	 *            <li>HH:小时
	 *            <li>mm:分钟
	 *            <li>ss:秒
	 *            <li>ms:毫秒
	 * @return
	 */
	public static String format(Date date, String format) {
		if (date == null) {
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 获取当月第一天
	 * 
	 * @return
	 */
	public static Date getFirstDayOfMonth() {
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.DAY_OF_MONTH, 1);
		clearTimeField(cal);

		return cal.getTime();
	}

	/**
	 * 获取本周一
	 * 
	 * @return
	 */
	public static Date getFirstDayOfWeek() {
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		clearTimeField(cal);

		return cal.getTime();
	}

	/**
	 * 获取当月最后一天
	 * 
	 * @return
	 */
	public static Date getLastDayOfMonth() {
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		clearTimeField(cal);

		return cal.getTime();
	}

	/**
	 * 获取本周日
	 * 
	 * @return
	 */
	public static Date getLastDayOfWeek() {
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.add(Calendar.DATE, 1);
		clearTimeField(cal);

		return cal.getTime();
	}

	/**
	 * 获取当天日期与时间
	 * 
	 * @return
	 */
	public static Date getNow() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}

	/**
	 * 获取当天日期，不包括时间部分
	 * 
	 * @return
	 */
	public static Date getToday() {
		Calendar cal = Calendar.getInstance();
		clearTimeField(cal);
		return cal.getTime();
	}

	/**
	 * 判断参数是否符合日期正则
	 * <p>
	 * 
	 * <li>(1)能匹配的年月日类型有： 2014年4月19日 2014年4月19号 2014-4-19 2014/4/19 2014.4.19
	 * <li>(2)能匹配的时分秒类型有： 15:28:21 15:28 5:28 pm 15点28分21秒 15点28分 15点
	 * <li>(3)能匹配的年月日时分秒类型有： (1)和(2)的任意组合，二者中间可有任意多个空格
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isMatchDatePattern(String date) {
		return datePattern.matcher(date).matches();
	}

	private static void clearTimeField(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	private static final Pattern datePattern = Pattern.compile(
			"(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String DEF_DATETIME_FMT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * yyyy-MM-dd
	 */
	public static final String DEF_DATE_FMT = "yyyy-MM-dd";

	public static final String DEF_DATE_FMT2 = "yyyyMMdd";

	/**
	 * yyyy-MM-dd-HHmmss
	 */
	public static final String DEF_DATETIME_FMT2 = "yyyy-MM-dd-HHmmss";

	/**
	 * HH:mm:ss
	 */
	public static final String DEF_TIME_FMT = "HH:mm:ss";

	/**
	 * "yyyy-MM-dd HH:mm
	 */
	public static final String DEF_DATETIME_FMT3 = "yyyyMMddHHmmss";
	public static final String DEF_DATETIME_FMT4 = "yyyy-MM-dd HH:mm";

	public static final String RANGE_DEFAULTTIME = "00:00:00" + " " + "23:59:59";
	public static final String START_DEFAULTTIME = "00:00:00";
	public static final String END_DEFAULTTIME = "23:59:59";

}
