package com.genlib.core.exception;

import com.genlib.core.enums.ResultCodeEnum;

/**
 * 基础异常类
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected String code;

    /**
     * 错误消息
     */
    protected String message;

    /**
     * 错误数据
     */
    protected Object data;

    /**
     * 构造方法
     */
    public BaseException() {
        super();
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public BaseException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param message 错误消息
     * @param data 错误数据
     */
    public BaseException(String code, String message, Object data) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     */
    public BaseException(ResultCodeEnum resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     * @param data 错误数据
     */
    public BaseException(ResultCodeEnum resultCode, Object data) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause 原因异常
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public BaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     * @param cause 原因异常
     */
    public BaseException(ResultCodeEnum resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 构造方法
     *
     * @param cause 原因异常
     */
    public BaseException(Throwable cause) {
        super(cause);
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置错误码
     *
     * @param code 错误码
     * @return 当前异常实例
     */
    public BaseException setCode(String code) {
        this.code = code;
        return this;
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 设置错误消息
     *
     * @param message 错误消息
     * @return 当前异常实例
     */
    public BaseException setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 获取错误数据
     *
     * @return 错误数据
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置错误数据
     *
     * @param data 错误数据
     * @return 当前异常实例
     */
    public BaseException setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return String.format("BaseException{code='%s', message='%s', data=%s}", 
                           code, message, data);
    }
}