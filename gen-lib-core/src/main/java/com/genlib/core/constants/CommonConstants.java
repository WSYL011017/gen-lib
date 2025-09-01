package com.genlib.core.constants;

/**
 * 通用常量定义
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public final class CommonConstants {

    private CommonConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ================== 系统常量 ==================
    
    /** 系统名称 */
    public static final String SYSTEM_NAME = "gen-lib";
    
    /** 系统版本 */
    public static final String SYSTEM_VERSION = "1.0.0";
    
    /** 默认字符编码 */
    public static final String DEFAULT_CHARSET = "UTF-8";
    
    /** 默认时区 */
    public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";
    
    // ================== 响应常量 ==================
    
    /** 成功响应码 */
    public static final String SUCCESS_CODE = "0000";
    
    /** 成功响应消息 */
    public static final String SUCCESS_MESSAGE = "操作成功";
    
    /** 系统错误响应码 */
    public static final String ERROR_CODE = "9999";
    
    /** 系统错误响应消息 */
    public static final String ERROR_MESSAGE = "系统异常";
    
    /** 参数错误响应码 */
    public static final String PARAM_ERROR_CODE = "1001";
    
    /** 参数错误响应消息 */
    public static final String PARAM_ERROR_MESSAGE = "参数错误";
    
    /** 业务错误响应码 */
    public static final String BUSINESS_ERROR_CODE = "2001";
    
    /** 业务错误响应消息 */
    public static final String BUSINESS_ERROR_MESSAGE = "业务异常";
    
    // ================== 分页常量 ==================
    
    /** 默认页码 */
    public static final int DEFAULT_PAGE_NUM = 1;
    
    /** 默认页大小 */
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    /** 最大页大小 */
    public static final int MAX_PAGE_SIZE = 1000;
    
    /** 分页参数 - 页码 */
    public static final String PAGE_NUM = "pageNum";
    
    /** 分页参数 - 页大小 */
    public static final String PAGE_SIZE = "pageSize";
    
    /** 分页参数 - 排序字段 */
    public static final String ORDER_BY = "orderBy";
    
    /** 分页参数 - 排序方向 */
    public static final String ORDER_DIRECTION = "orderDirection";
    
    // ================== HTTP常量 ==================
    
    /** Content-Type: application/json */
    public static final String CONTENT_TYPE_JSON = "application/json";
    
    /** Content-Type: application/xml */
    public static final String CONTENT_TYPE_XML = "application/xml";
    
    /** Content-Type: text/plain */
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    
    /** Content-Type: application/octet-stream */
    public static final String CONTENT_TYPE_STREAM = "application/octet-stream";
    
    /** 请求头 - 链路追踪ID */
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    
    /** 请求头 - 用户ID */
    public static final String HEADER_USER_ID = "X-User-Id";
    
    /** 请求头 - 租户ID */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";
    
    // ================== 缓存常量 ==================
    
    /** 默认缓存过期时间(秒) - 1小时 */
    public static final long DEFAULT_CACHE_EXPIRE = 3600L;
    
    /** 短期缓存过期时间(秒) - 5分钟 */
    public static final long SHORT_CACHE_EXPIRE = 300L;
    
    /** 长期缓存过期时间(秒) - 24小时 */
    public static final long LONG_CACHE_EXPIRE = 86400L;
    
    /** 永久缓存过期时间 */
    public static final long PERMANENT_CACHE_EXPIRE = -1L;
    
    // ================== 数据库常量 ==================
    
    /** 逻辑删除 - 已删除 */
    public static final Integer DELETED_YES = 1;
    
    /** 逻辑删除 - 未删除 */
    public static final Integer DELETED_NO = 0;
    
    /** 默认版本号 */
    public static final Integer DEFAULT_VERSION = 1;
    
    // ================== 业务常量 ==================
    
    /** 状态 - 启用 */
    public static final Integer STATUS_ENABLED = 1;
    
    /** 状态 - 禁用 */
    public static final Integer STATUS_DISABLED = 0;
    
    /** 是否标识 - 是 */
    public static final String YES = "Y";
    
    /** 是否标识 - 否 */
    public static final String NO = "N";
    
    // ================== 正则表达式常量 ==================
    
    /** 手机号正则 */
    public static final String REGEX_MOBILE = "^1[3-9]\\d{9}$";
    
    /** 邮箱正则 */
    public static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    /** 身份证号正则 */
    public static final String REGEX_ID_CARD = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    
    /** 中文正则 */
    public static final String REGEX_CHINESE = "^[\\u4e00-\\u9fa5]+$";
    
    /** 数字正则 */
    public static final String REGEX_NUMBER = "^\\d+$";
    
    /** 字母正则 */
    public static final String REGEX_LETTER = "^[A-Za-z]+$";
    
    /** 字母数字正则 */
    public static final String REGEX_ALPHANUMERIC = "^[A-Za-z0-9]+$";
    
    // ================== 符号常量 ==================
    
    /** 逗号分隔符 */
    public static final String COMMA = ",";
    
    /** 分号分隔符 */
    public static final String SEMICOLON = ";";
    
    /** 冒号分隔符 */
    public static final String COLON = ":";
    
    /** 点分隔符 */
    public static final String DOT = ".";
    
    /** 下划线分隔符 */
    public static final String UNDERSCORE = "_";
    
    /** 横线分隔符 */
    public static final String HYPHEN = "-";
    
    /** 斜杠分隔符 */
    public static final String SLASH = "/";
    
    /** 反斜杠分隔符 */
    public static final String BACKSLASH = "\\";
    
    /** 空字符串 */
    public static final String EMPTY_STRING = "";
    
    /** 空格 */
    public static final String SPACE = " ";
}