package com.genlib.core.exception;

import com.genlib.core.enums.ResultCodeEnum;

/**
 * 参数异常类
 * 用于表示参数验证失败的异常情况
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class ParamException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造方法
     */
    public ParamException() {
        super(ResultCodeEnum.PARAM_ERROR);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public ParamException(String message) {
        super(ResultCodeEnum.PARAM_ERROR.getCode(), message);
    }

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public ParamException(String code, String message) {
        super(code, message);
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     */
    public ParamException(ResultCodeEnum resultCode) {
        super(resultCode);
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     * @param data 错误数据
     */
    public ParamException(ResultCodeEnum resultCode, Object data) {
        super(resultCode, data);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause 原因异常
     */
    public ParamException(String message, Throwable cause) {
        super(ResultCodeEnum.PARAM_ERROR.getCode(), message, cause);
    }

    /**
     * 构造方法
     *
     * @param resultCode 响应码枚举
     * @param cause 原因异常
     */
    public ParamException(ResultCodeEnum resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    /**
     * 构造方法
     *
     * @param cause 原因异常
     */
    public ParamException(Throwable cause) {
        super(ResultCodeEnum.PARAM_ERROR, cause);
    }

    /**
     * 创建参数异常
     *
     * @param message 错误消息
     * @return 参数异常实例
     */
    public static ParamException of(String message) {
        return new ParamException(message);
    }

    /**
     * 创建参数异常
     *
     * @param resultCode 响应码枚举
     * @return 参数异常实例
     */
    public static ParamException of(ResultCodeEnum resultCode) {
        return new ParamException(resultCode);
    }

    /**
     * 创建参数异常
     *
     * @param resultCode 响应码枚举
     * @param data 错误数据
     * @return 参数异常实例
     */
    public static ParamException of(ResultCodeEnum resultCode, Object data) {
        return new ParamException(resultCode, data);
    }

    /**
     * 验证参数不为null
     *
     * @param param 参数
     * @param paramName 参数名称
     * @throws ParamException 参数异常
     */
    public static void requireNonNull(Object param, String paramName) {
        if (param == null) {
            throw new ParamException(String.format("参数 %s 不能为空", paramName));
        }
    }

    /**
     * 验证字符串参数不为空
     *
     * @param param 参数
     * @param paramName 参数名称
     * @throws ParamException 参数异常
     */
    public static void requireNonEmpty(String param, String paramName) {
        if (param == null || param.trim().isEmpty()) {
            throw new ParamException(String.format("参数 %s 不能为空", paramName));
        }
    }

    /**
     * 验证参数为true
     *
     * @param condition 条件
     * @param message 错误消息
     * @throws ParamException 参数异常
     */
    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new ParamException(message);
        }
    }

    /**
     * 验证参数为true
     *
     * @param condition 条件
     * @param resultCode 响应码枚举
     * @throws ParamException 参数异常
     */
    public static void require(boolean condition, ResultCodeEnum resultCode) {
        if (!condition) {
            throw new ParamException(resultCode);
        }
    }

    /**
     * 验证数值参数在指定范围内
     *
     * @param value 数值
     * @param min 最小值
     * @param max 最大值
     * @param paramName 参数名称
     * @throws ParamException 参数异常
     */
    public static void requireInRange(long value, long min, long max, String paramName) {
        if (value < min || value > max) {
            throw new ParamException(String.format("参数 %s 必须在 %d 到 %d 之间", paramName, min, max));
        }
    }

    /**
     * 验证数值参数为正数
     *
     * @param value 数值
     * @param paramName 参数名称
     * @throws ParamException 参数异常
     */
    public static void requirePositive(long value, String paramName) {
        if (value <= 0) {
            throw new ParamException(String.format("参数 %s 必须为正数", paramName));
        }
    }

    /**
     * 验证数值参数为非负数
     *
     * @param value 数值
     * @param paramName 参数名称
     * @throws ParamException 参数异常
     */
    public static void requireNonNegative(long value, String paramName) {
        if (value < 0) {
            throw new ParamException(String.format("参数 %s 不能为负数", paramName));
        }
    }
}