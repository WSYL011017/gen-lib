package com.genlib.core.annotation;

import java.lang.annotation.*;

/**
 * API版本注解
 * 用于标记API的版本信息
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {

    /**
     * API版本号
     *
     * @return 版本号
     */
    String value() default "1.0";

    /**
     * 版本描述
     *
     * @return 版本描述
     */
    String description() default "";

    /**
     * 是否已弃用
     *
     * @return 是否已弃用
     */
    boolean deprecated() default false;

    /**
     * 弃用说明
     *
     * @return 弃用说明
     */
    String deprecatedMessage() default "";
}