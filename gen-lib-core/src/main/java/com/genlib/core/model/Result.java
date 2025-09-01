package com.genlib.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.genlib.core.constants.CommonConstants;
import com.genlib.core.enums.ResultCodeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应结果封装类
 * 
 * @param <T> 数据类型
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * 链路追踪ID
     */
    private String traceId;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 无参构造方法
     */
    public Result() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 构造方法
     *
     * @param code 响应码
     * @param message 响应消息
     */
    public Result(String code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     *
     * @param code 响应码
     * @param message 响应消息
     * @param data 响应数据
     */
    public Result(String code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     */
    public Result(ResultCodeEnum resultCode) {
        this(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     * @param data 响应数据
     */
    public Result(ResultCodeEnum resultCode, T data) {
        this(resultCode.getCode(), resultCode.getMessage(), data);
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     * @param data 响应数据
     * @param message 响应消息
     */
    public Result(ResultCodeEnum resultCode,T data,String message){
        this(resultCode.getCode(),message,data);
    }


    /**
     * 创建成功响应
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(CommonConstants.SUCCESS_CODE, CommonConstants.SUCCESS_MESSAGE);
    }

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(CommonConstants.SUCCESS_CODE, CommonConstants.SUCCESS_MESSAGE, data);
    }

    /**
     * 创建成功响应
     *
     * @param message 响应消息
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(CommonConstants.SUCCESS_CODE, message);
    }

    /**
     * 创建成功响应
     *
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(CommonConstants.SUCCESS_CODE, message, data);
    }

    /**
     * 创建错误响应
     *
     * @param <T> 数据类型
     * @return 错误响应
     */
    public static <T> Result<T> error() {
        return new Result<>(CommonConstants.ERROR_CODE, CommonConstants.ERROR_MESSAGE);
    }

    /**
     * 创建错误响应
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误响应
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(CommonConstants.ERROR_CODE, message);
    }

    /**
     * 创建错误响应
     *
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误响应
     */
    public static <T> Result<T> error(String code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 创建错误响应
     *
     * @param resultCode 响应码枚举
     * @param <T> 数据类型
     * @return 错误响应
     */
    public static <T> Result<T> error(ResultCodeEnum resultCode) {
        return new Result<>(resultCode);
    }

    /**
     * 创建错误响应
     *
     * @param resultCode 响应码枚举
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 错误响应
     */
    public static <T> Result<T> error(ResultCodeEnum resultCode, T data) {
        return new Result<>(resultCode, data);
    }

    /**
     * 创建错误响应
     *
     * @param resultCode 响应码枚举
     * @param data 响应数据
     * @param message 响应消息
     * @return 错误响应
     * @param <T> 响应数据类型
     */
    public static <T> Result<T> error(ResultCodeEnum resultCode,T data,String message) {
        return new Result<>(resultCode, data, message);
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return CommonConstants.SUCCESS_CODE.equals(this.code);
    }

    /**
     * 判断是否失败
     *
     * @return 是否失败
     */
    public boolean isError() {
        return !isSuccess();
    }

    // ================== Getter 和 Setter ==================

    public String getCode() {
        return code;
    }

    public Result<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Result<T> setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public Result<T> setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Result<T> setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Result{code='%s', message='%s', data=%s, timestamp=%s, traceId='%s', path='%s'}", 
                           code, message, data, timestamp, traceId, path);
    }
}