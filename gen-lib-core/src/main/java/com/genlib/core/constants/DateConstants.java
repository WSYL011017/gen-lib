package com.genlib.core.constants;

/**
 * 日期时间格式常量
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public final class DateConstants {

    private DateConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ================== 日期格式 ==================
    
    /** 标准日期格式：yyyy-MM-dd */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /** 简短日期格式：yyyyMMdd */
    public static final String DATE_FORMAT_SHORT = "yyyyMMdd";
    
    /** 中文日期格式：yyyy年MM月dd日 */
    public static final String DATE_FORMAT_CN = "yyyy年MM月dd日";
    
    /** 斜杠日期格式：yyyy/MM/dd */
    public static final String DATE_FORMAT_SLASH = "yyyy/MM/dd";
    
    /** 点分割日期格式：yyyy.MM.dd */
    public static final String DATE_FORMAT_DOT = "yyyy.MM.dd";
    
    // ================== 时间格式 ==================
    
    /** 标准时间格式：HH:mm:ss */
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    /** 简短时间格式：HHmmss */
    public static final String TIME_FORMAT_SHORT = "HHmmss";
    
    /** 12小时制时间格式：hh:mm:ss a */
    public static final String TIME_FORMAT_12H = "hh:mm:ss a";
    
    /** 时分格式：HH:mm */
    public static final String TIME_FORMAT_HM = "HH:mm";
    
    // ================== 日期时间格式 ==================
    
    /** 标准日期时间格式：yyyy-MM-dd HH:mm:ss */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /** 简短日期时间格式：yyyyMMddHHmmss */
    public static final String DATETIME_FORMAT_SHORT = "yyyyMMddHHmmss";
    
    /** 中文日期时间格式：yyyy年MM月dd日 HH时mm分ss秒 */
    public static final String DATETIME_FORMAT_CN = "yyyy年MM月dd日 HH时mm分ss秒";
    
    /** ISO日期时间格式：yyyy-MM-dd'T'HH:mm:ss */
    public static final String DATETIME_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss";
    
    /** ISO日期时间带时区格式：yyyy-MM-dd'T'HH:mm:ss.SSSXXX */
    public static final String DATETIME_FORMAT_ISO_ZONE = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    
    /** 毫秒级日期时间格式：yyyy-MM-dd HH:mm:ss.SSS */
    public static final String DATETIME_FORMAT_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
    
    // ================== 特殊格式 ==================
    
    /** 年月格式：yyyy-MM */
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";
    
    /** 年月格式（中文）：yyyy年MM月 */
    public static final String YEAR_MONTH_FORMAT_CN = "yyyy年MM月";
    
    /** 月日格式：MM-dd */
    public static final String MONTH_DAY_FORMAT = "MM-dd";
    
    /** 年格式：yyyy */
    public static final String YEAR_FORMAT = "yyyy";
    
    /** 月格式：MM */
    public static final String MONTH_FORMAT = "MM";
    
    /** 日格式：dd */
    public static final String DAY_FORMAT = "dd";
    
    /** 小时格式：HH */
    public static final String HOUR_FORMAT = "HH";
    
    /** 分钟格式：mm */
    public static final String MINUTE_FORMAT = "mm";
    
    /** 秒格式：ss */
    public static final String SECOND_FORMAT = "ss";
    
    // ================== 时间戳格式 ==================
    
    /** 时间戳格式（精确到秒）：yyyyMMddHHmmss */
    public static final String TIMESTAMP_SECOND_FORMAT = "yyyyMMddHHmmss";
    
    /** 时间戳格式（精确到毫秒）：yyyyMMddHHmmssSSS */
    public static final String TIMESTAMP_MILLIS_FORMAT = "yyyyMMddHHmmssSSS";
    
    // ================== 星期常量 ==================
    
    /** 星期一 */
    public static final String MONDAY = "Monday";
    
    /** 星期二 */
    public static final String TUESDAY = "Tuesday";
    
    /** 星期三 */
    public static final String WEDNESDAY = "Wednesday";
    
    /** 星期四 */
    public static final String THURSDAY = "Thursday";
    
    /** 星期五 */
    public static final String FRIDAY = "Friday";
    
    /** 星期六 */
    public static final String SATURDAY = "Saturday";
    
    /** 星期日 */
    public static final String SUNDAY = "Sunday";
    
    // ================== 中文星期常量 ==================
    
    /** 星期一（中文） */
    public static final String MONDAY_CN = "星期一";
    
    /** 星期二（中文） */
    public static final String TUESDAY_CN = "星期二";
    
    /** 星期三（中文） */
    public static final String WEDNESDAY_CN = "星期三";
    
    /** 星期四（中文） */
    public static final String THURSDAY_CN = "星期四";
    
    /** 星期五（中文） */
    public static final String FRIDAY_CN = "星期五";
    
    /** 星期六（中文） */
    public static final String SATURDAY_CN = "星期六";
    
    /** 星期日（中文） */
    public static final String SUNDAY_CN = "星期日";
    
    // ================== 月份常量 ==================
    
    /** 一月 */
    public static final String JANUARY = "January";
    
    /** 二月 */
    public static final String FEBRUARY = "February";
    
    /** 三月 */
    public static final String MARCH = "March";
    
    /** 四月 */
    public static final String APRIL = "April";
    
    /** 五月 */
    public static final String MAY = "May";
    
    /** 六月 */
    public static final String JUNE = "June";
    
    /** 七月 */
    public static final String JULY = "July";
    
    /** 八月 */
    public static final String AUGUST = "August";
    
    /** 九月 */
    public static final String SEPTEMBER = "September";
    
    /** 十月 */
    public static final String OCTOBER = "October";
    
    /** 十一月 */
    public static final String NOVEMBER = "November";
    
    /** 十二月 */
    public static final String DECEMBER = "December";
    
    // ================== 时间单位常量 ==================
    
    /** 一秒的毫秒数 */
    public static final long MILLIS_PER_SECOND = 1000L;
    
    /** 一分钟的毫秒数 */
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    
    /** 一小时的毫秒数 */
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    
    /** 一天的毫秒数 */
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
    
    /** 一周的毫秒数 */
    public static final long MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY;
    
    /** 一秒的秒数 */
    public static final int SECONDS_PER_MINUTE = 60;
    
    /** 一小时的秒数 */
    public static final int SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;
    
    /** 一天的秒数 */
    public static final int SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR;
    
    /** 一分钟的分钟数 */
    public static final int MINUTES_PER_HOUR = 60;
    
    /** 一天的分钟数 */
    public static final int MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR;
    
    /** 一天的小时数 */
    public static final int HOURS_PER_DAY = 24;
    
    /** 一周的天数 */
    public static final int DAYS_PER_WEEK = 7;
}