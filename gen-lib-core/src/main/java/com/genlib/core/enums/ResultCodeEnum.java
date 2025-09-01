package com.genlib.core.enums;

/**
 * 响应码枚举
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public enum ResultCodeEnum {

    // ================== 成功响应 ==================
    
    /** 成功 */
    SUCCESS("0000", "操作成功"),
    
    /** 创建成功 */
    CREATED("0001", "创建成功"),
    
    /** 更新成功 */
    UPDATED("0002", "更新成功"),
    
    /** 删除成功 */
    DELETED("0003", "删除成功"),
    
    // ================== 客户端错误 (1xxx) ==================
    
    /** 参数错误 */
    PARAM_ERROR("1000", "参数错误"),
    
    /** 参数缺失 */
    PARAM_MISSING("1001", "参数缺失"),
    
    /** 参数格式错误 */
    PARAM_FORMAT_ERROR("1002", "参数格式错误"),
    
    /** 参数值无效 */
    PARAM_INVALID("1003", "参数值无效"),
    
    /** 参数超出范围 */
    PARAM_OUT_OF_RANGE("1004", "参数超出范围"),
    
    /** 请求方法不支持 */
    METHOD_NOT_SUPPORTED("1005", "请求方法不支持"),
    
    /** 请求内容类型不支持 */
    CONTENT_TYPE_NOT_SUPPORTED("1006", "请求内容类型不支持"),
    
    /** 请求体缺失 */
    REQUEST_BODY_MISSING("1007", "请求体缺失"),
    
    /** JSON解析错误 */
    JSON_PARSE_ERROR("1008", "JSON解析错误"),
    
    /** 数据验证失败 */
    VALIDATION_FAILED("1009", "数据验证失败"),
    
    // ================== 认证授权错误 (2xxx) ==================
    
    /** 未认证 */
    UNAUTHORIZED("2000", "未认证"),
    
    /** 认证失败 */
    AUTHENTICATION_FAILED("2001", "认证失败"),
    
    /** Token无效 */
    TOKEN_INVALID("2002", "Token无效"),
    
    /** Token过期 */
    TOKEN_EXPIRED("2003", "Token过期"),
    
    /** 无权限 */
    FORBIDDEN("2004", "无权限"),
    
    /** 访问被拒绝 */
    ACCESS_DENIED("2005", "访问被拒绝"),
    
    /** 请求方法不允许 */
    METHOD_NOT_ALLOWED("2006", "请求方法不允许"),
    
    /** 资源未找到 */
    NOT_FOUND("2007", "资源未找到"),
    
    /** 用户不存在 */
    USER_NOT_FOUND("2008", "用户不存在"),
    
    /** 用户已被禁用 */
    USER_DISABLED("2009", "用户已被禁用"),
    
    /** 密码错误 */
    PASSWORD_ERROR("2010", "密码错误"),
    
    /** 用户已锁定 */
    USER_LOCKED("2011", "用户已锁定"),
    
    // ================== 业务错误 (3xxx) ==================
    
    /** 业务异常 */
    BUSINESS_ERROR("3000", "业务异常"),
    
    /** 数据不存在 */
    DATA_NOT_FOUND("3001", "数据不存在"),
    
    /** 数据已存在 */
    DATA_ALREADY_EXISTS("3002", "数据已存在"),
    
    /** 数据状态异常 */
    DATA_STATUS_ERROR("3003", "数据状态异常"),
    
    /** 操作失败 */
    OPERATION_FAILED("3004", "操作失败"),
    
    /** 操作超时 */
    OPERATION_TIMEOUT("3005", "操作超时"),
    
    /** 操作频繁 */
    OPERATION_TOO_FREQUENT("3006", "操作过于频繁"),
    
    /** 数据冲突 */
    DATA_CONFLICT("3007", "数据冲突"),
    
    /** 资源不足 */
    RESOURCE_INSUFFICIENT("3008", "资源不足"),
    
    /** 配置错误 */
    CONFIG_ERROR("3009", "配置错误"),
    
    // ================== 第三方服务错误 (4xxx) ==================
    
    /** 第三方服务不可用 */
    THIRD_PARTY_SERVICE_UNAVAILABLE("4000", "第三方服务不可用"),
    
    /** 第三方服务响应超时 */
    THIRD_PARTY_SERVICE_TIMEOUT("4001", "第三方服务响应超时"),
    
    /** 第三方服务返回错误 */
    THIRD_PARTY_SERVICE_ERROR("4002", "第三方服务返回错误"),
    
    /** 外部API调用失败 */
    EXTERNAL_API_CALL_FAILED("4003", "外部API调用失败"),
    
    /** 网络连接异常 */
    NETWORK_ERROR("4004", "网络连接异常"),
    
    // ================== 数据库错误 (5xxx) ==================
    
    /** 数据库连接错误 */
    DATABASE_CONNECTION_ERROR("5000", "数据库连接错误"),
    
    /** 数据库查询错误 */
    DATABASE_QUERY_ERROR("5001", "数据库查询错误"),
    
    /** 数据库更新错误 */
    DATABASE_UPDATE_ERROR("5002", "数据库更新错误"),
    
    /** 数据库事务失败 */
    DATABASE_TRANSACTION_FAILED("5003", "数据库事务失败"),
    
    /** 数据完整性约束违反 */
    DATABASE_INTEGRITY_VIOLATION("5004", "数据完整性约束违反"),
    
    /** 数据库锁超时 */
    DATABASE_LOCK_TIMEOUT("5005", "数据库锁超时"),
    
    // ================== 缓存错误 (6xxx) ==================
    
    /** 缓存连接错误 */
    CACHE_CONNECTION_ERROR("6000", "缓存连接错误"),
    
    /** 缓存操作失败 */
    CACHE_OPERATION_FAILED("6001", "缓存操作失败"),
    
    /** 缓存序列化错误 */
    CACHE_SERIALIZATION_ERROR("6002", "缓存序列化错误"),
    
    /** 缓存反序列化错误 */
    CACHE_DESERIALIZATION_ERROR("6003", "缓存反序列化错误"),
    
    // ================== 文件相关错误 (7xxx) ==================
    
    /** 文件上传失败 */
    FILE_UPLOAD_FAILED("7000", "文件上传失败"),
    
    /** 文件下载失败 */
    FILE_DOWNLOAD_FAILED("7001", "文件下载失败"),
    
    /** 文件不存在 */
    FILE_NOT_FOUND("7002", "文件不存在"),
    
    /** 文件格式不支持 */
    FILE_FORMAT_NOT_SUPPORTED("7003", "文件格式不支持"),
    
    /** 文件大小超限 */
    FILE_SIZE_EXCEEDED("7004", "文件大小超限"),
    
    /** 文件读取错误 */
    FILE_READ_ERROR("7005", "文件读取错误"),
    
    /** 文件写入错误 */
    FILE_WRITE_ERROR("7006", "文件写入错误"),
    
    // ================== 消息队列错误 (8xxx) ==================
    
    /** 消息发送失败 */
    MESSAGE_SEND_FAILED("8000", "消息发送失败"),
    
    /** 消息消费失败 */
    MESSAGE_CONSUME_FAILED("8001", "消息消费失败"),
    
    /** 消息队列连接错误 */
    MESSAGE_QUEUE_CONNECTION_ERROR("8002", "消息队列连接错误"),
    
    /** 消息序列化错误 */
    MESSAGE_SERIALIZATION_ERROR("8003", "消息序列化错误"),
    
    // ================== 系统错误 (9xxx) ==================
    
    /** 系统异常 */
    SYSTEM_ERROR("9000", "系统异常"),
    
    /** 内部服务器错误 */
    INTERNAL_SERVER_ERROR("9001", "内部服务器错误"),
    
    /** 服务不可用 */
    SERVICE_UNAVAILABLE("9002", "服务不可用"),
    
    /** 服务降级 */
    SERVICE_DEGRADED("9003", "服务降级"),
    
    /** 服务熔断 */
    SERVICE_CIRCUIT_BREAKER("9004", "服务熔断"),
    
    /** 资源耗尽 */
    RESOURCE_EXHAUSTED("9005", "资源耗尽"),
    
    /** 未知错误 */
    UNKNOWN_ERROR("9999", "未知错误");

    /**
     * 响应码
     */
    private final String code;

    /**
     * 响应消息
     */
    private final String message;

    ResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取响应码
     *
     * @return 响应码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 根据响应码获取枚举
     *
     * @param code 响应码
     * @return ResultCodeEnum
     */
    public static ResultCodeEnum getByCode(String code) {
        for (ResultCodeEnum resultCode : values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    /**
     * 判断是否为成功响应码
     *
     * @param code 响应码
     * @return 是否成功
     */
    public static boolean isSuccess(String code) {
        return SUCCESS.getCode().equals(code);
    }

    /**
     * 判断是否为成功响应码
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return SUCCESS.getCode().equals(this.code);
    }

    @Override
    public String toString() {
        return String.format("ResultCodeEnum{code='%s', message='%s'}", code, message);
    }
}