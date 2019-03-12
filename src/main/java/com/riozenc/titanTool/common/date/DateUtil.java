/**
 *    Auth:riozenc
 *    Date:2018年9月27日 上午9:19:46
 *    Title:com.riozenc.quicktool.common.util.date.DateUtil.java
 **/
package com.riozenc.titanTool.common.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 
 * @author riozenc
 *
 */
public class DateUtil {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public enum NdateFormatter {
		DATE_TIME, DATE
	}

	/**
	 * 获取当前日期和时分秒
	 * 
	 * @return
	 */
	public static String getDateTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static String getDate() {
		return LocalDate.now().format(DateTimeFormatter.ISO_DATE);
	}

	/**
	 * 通过java.util.Date 获取指定格式的日期格式字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String getDate(Date date, NdateFormatter pattern) {
		return getDate(local2Date(date), pattern);
	}

	public static String getDate(Date date, long day, NdateFormatter pattern) {
		return getDate(local2Date(date).plusDays(day), pattern);
	}

	public static Date Date(Date date, long day) {
		return Date.from(local2Date(date).plusDays(day).atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getDate(String date) {
		return Date.from(makeInstant(date));
	}

	public static String getDate(LocalDateTime localDateTime, NdateFormatter pattern) {
		switch (pattern) {
		case DATE: {
			return localDateTime.format(DateTimeFormatter.ISO_DATE);
		}
		case DATE_TIME: {
			return localDateTime.format(DATE_TIME_FORMATTER);
		}
		}
		return null;
	}

	public static Long between(Date startDate, Date endDate) {
		LocalDateTime startLocalDate = local2Date(startDate);
		LocalDateTime endLocalDate = local2Date(endDate);
		return endLocalDate.toLocalDate().toEpochDay() - startLocalDate.toLocalDate().toEpochDay();
	}

	/**
	 * String -> Instant (String -> LocalDate(LocalDateTime) -> Instant)
	 * 
	 * @param date
	 * @return
	 */
	protected static Instant makeInstant(String date) {
		if (date.length() > 10) {
			return local2Instant(LocalDateTime.parse(date, DATE_TIME_FORMATTER), ZoneId.systemDefault());
		} else {
			return local2Instant(LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE), ZoneId.systemDefault());
		}
	}

	/**
	 * Date -> LocalDateTime
	 * 
	 * @param date
	 * @return
	 */
	protected static LocalDateTime local2Date(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * LocalDateTime -> Instant
	 * 
	 * @param localDateTime
	 * @param zoneId
	 * @return
	 */
	protected static Instant local2Instant(LocalDateTime localDateTime, ZoneId zoneId) {
		return localDateTime.atZone(zoneId).toInstant();
	}

	/**
	 * LocalDate -> Instant
	 * 
	 * @param localDate
	 * @param zoneId
	 * @return
	 */
	protected static Instant local2Instant(LocalDate localDate, ZoneId zoneId) {
		return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
	}

}
