package com.genlib.config.core;

import com.genlib.core.exception.BaseException;
import com.genlib.core.enums.ResultCodeEnum;

/**
 * 配置异常类
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class ConfigException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造方法
     */
    public ConfigException() {
        super(ResultCodeEnum.CONFIG_ERROR);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public ConfigException(String message) {
        super(ResultCodeEnum.CONFIG_ERROR.getCode(), message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause 原因异常
     */
    public ConfigException(String message, Throwable cause) {
        super(ResultCodeEnum.CONFIG_ERROR.getCode(), message, cause);
    }

    /**
     * 构造方法
     *
     * @param cause 原因异常
     */
    public ConfigException(Throwable cause) {
        super(ResultCodeEnum.CONFIG_ERROR, cause);
    }

    /**
     * 创建配置异常
     *
     * @param message 错误消息
     * @return 配置异常实例
     */
    public static ConfigException of(String message) {
        return new ConfigException(message);
    }

    /**
     * 创建配置异常
     *
     * @param message 错误消息
     * @param cause 原因异常
     * @return 配置异常实例
     */
    public static ConfigException of(String message, Throwable cause) {
        return new ConfigException(message, cause);
    }
}