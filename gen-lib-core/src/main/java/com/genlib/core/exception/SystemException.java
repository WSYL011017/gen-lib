package com.genlib.core.exception;

import com.genlib.core.enums.ResultCodeEnum;

/**
 * 系统异常类
 * 用于表示系统级别的异常情况，如配置错误、资源不足等
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class SystemException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造方法
     */
    public SystemException() {
        super(ResultCodeEnum.SYSTEM_ERROR);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public SystemException(String message) {
        super(ResultCodeEnum.SYSTEM_ERROR.getCode(), message);
    }

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public SystemException(String code, String message) {
        super(code, message);
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     */
    public SystemException(ResultCodeEnum resultCode) {
        super(resultCode);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause 原因异常
     */
    public SystemException(String message, Throwable cause) {
        super(ResultCodeEnum.SYSTEM_ERROR.getCode(), message, cause);
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     * @param cause 原因异常
     */
    public SystemException(ResultCodeEnum resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    /**
     * 构造方法
     *
     * @param cause 原因异常
     */
    public SystemException(Throwable cause) {
        super(ResultCodeEnum.SYSTEM_ERROR, cause);
    }

    /**
     * 创建系统异常
     *
     * @param message 错误消息
     * @return 系统异常实例
     */
    public static SystemException of(String message) {
        return new SystemException(message);
    }

    /**
     * 创建系统异常
     *
     * @param resultCode 响应码枚举
     * @return 系统异常实例
     */
    public static SystemException of(ResultCodeEnum resultCode) {
        return new SystemException(resultCode);
    }

    /**
     * 创建系统异常
     *
     * @param message 错误消息
     * @param cause 原因异常
     * @return 系统异常实例
     */
    public static SystemException of(String message, Throwable cause) {
        return new SystemException(message, cause);
    }
}