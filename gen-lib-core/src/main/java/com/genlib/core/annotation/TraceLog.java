package com.genlib.core.annotation;

import java.lang.annotation.*;

/**
 * 链路追踪注解
 * 用于标记需要进行链路追踪的方法
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TraceLog {

    /**
     * 操作名称
     *
     * @return 操作名称
     */
    String value() default "";

    /**
     * 操作描述
     *
     * @return 操作描述
     */
    String description() default "";

    /**
     * 是否记录参数
     *
     * @return 是否记录参数
     */
    boolean logArgs() default true;

    /**
     * 是否记录返回值
     *
     * @return 是否记录返回值
     */
    boolean logResult() default true;

    /**
     * 是否记录异常
     *
     * @return 是否记录异常
     */
    boolean logException() default true;

    /**
     * 超时告警时间（毫秒）
     *
     * @return 超时告警时间
     */
    long timeoutWarning() default 5000L;

    /**
     * 业务标签
     *
     * @return 业务标签
     */
    String[] tags() default {};
}