package com.genlib.core.exception;

import com.genlib.core.enums.ResultCodeEnum;

/**
 * 业务异常类
 * 用于表示业务逻辑层面的异常情况
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造方法
     */
    public BusinessException() {
        super(ResultCodeEnum.BUSINESS_ERROR);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(ResultCodeEnum.BUSINESS_ERROR.getCode(), message);
    }

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(String code, String message) {
        super(code, message);
    }

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param message 错误消息
     * @param data 错误数据
     */
    public BusinessException(String code, String message, Object data) {
        super(code, message, data);
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     */
    public BusinessException(ResultCodeEnum resultCode) {
        super(resultCode);
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     * @param data 错误数据
     */
    public BusinessException(ResultCodeEnum resultCode, Object data) {
        super(resultCode, data);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause 原因异常
     */
    public BusinessException(String message, Throwable cause) {
        super(ResultCodeEnum.BUSINESS_ERROR.getCode(), message, cause);
    }

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public BusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     * @param cause 原因异常
     */
    public BusinessException(ResultCodeEnum resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    /**
     * 构造方法
     *
     * @param cause 原因异常
     */
    public BusinessException(Throwable cause) {
        super(ResultCodeEnum.BUSINESS_ERROR, cause);
    }

    /**
     * 创建业务异常
     *
     * @param message 错误消息
     * @return 业务异常实例
     */
    public static BusinessException of(String message) {
        return new BusinessException(message);
    }

    /**
     * 创建业务异常
     *
     * @param code 错误码
     * @param message 错误消息
     * @return 业务异常实例
     */
    public static BusinessException of(String code, String message) {
        return new BusinessException(code, message);
    }

    /**
     * 创建业务异常
     *
     * @param resultCode 响应码枚举
     * @return 业务异常实例
     */
    public static BusinessException of(ResultCodeEnum resultCode) {
        return new BusinessException(resultCode);
    }

    /**
     * 创建业务异常
     *
     * @param resultCode 响应码枚举
     * @param data 错误数据
     * @return 业务异常实例
     */
    public static BusinessException of(ResultCodeEnum resultCode, Object data) {
        return new BusinessException(resultCode, data);
    }

    /**
     * 断言条件为true，否则抛出业务异常
     *
     * @param condition 条件
     * @param message 错误消息
     * @throws BusinessException 业务异常
     */
    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言条件为true，否则抛出业务异常
     *
     * @param condition 条件
     * @param resultCode 响应码枚举
     * @throws BusinessException 业务异常
     */
    public static void assertTrue(boolean condition, ResultCodeEnum resultCode) {
        if (!condition) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * 断言条件为false，否则抛出业务异常
     *
     * @param condition 条件
     * @param message 错误消息
     * @throws BusinessException 业务异常
     */
    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言条件为false，否则抛出业务异常
     *
     * @param condition 条件
     * @param resultCode 响应码枚举
     * @throws BusinessException 业务异常
     */
    public static void assertFalse(boolean condition, ResultCodeEnum resultCode) {
        if (condition) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * 断言对象不为null，否则抛出业务异常
     *
     * @param object 对象
     * @param message 错误消息
     * @throws BusinessException 业务异常
     */
    public static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言对象不为null，否则抛出业务异常
     *
     * @param object 对象
     * @param resultCode 响应码枚举
     * @throws BusinessException 业务异常
     */
    public static void assertNotNull(Object object, ResultCodeEnum resultCode) {
        if (object == null) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * 断言字符串不为空，否则抛出业务异常
     *
     * @param str 字符串
     * @param message 错误消息
     * @throws BusinessException 业务异常
     */
    public static void assertNotEmpty(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言字符串不为空，否则抛出业务异常
     *
     * @param str 字符串
     * @param resultCode 响应码枚举
     * @throws BusinessException 业务异常
     */
    public static void assertNotEmpty(String str, ResultCodeEnum resultCode) {
        if (str == null || str.trim().isEmpty()) {
            throw new BusinessException(resultCode);
        }
    }
}