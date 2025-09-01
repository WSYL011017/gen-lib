package com.genlib.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存注解
 * 用于方法级别的缓存控制
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {

    /**
     * 缓存名称
     *
     * @return 缓存名称
     */
    String[] value() default {};

    /**
     * 缓存名称（别名）
     *
     * @return 缓存名称
     */
    String[] cacheNames() default {};

    /**
     * 缓存键表达式
     * 支持SpEL表达式
     *
     * @return 键表达式
     */
    String key() default "";

    /**
     * 缓存键生成器Bean名称
     *
     * @return 生成器Bean名称
     */
    String keyGenerator() default "";

    /**
     * 缓存管理器Bean名称
     *
     * @return 管理器Bean名称
     */
    String cacheManager() default "";

    /**
     * 缓存条件表达式
     * 只有当条件为true时才缓存
     *
     * @return 条件表达式
     */
    String condition() default "";

    /**
     * 排除条件表达式
     * 当条件为true时不缓存
     *
     * @return 排除条件表达式
     */
    String unless() default "";

    /**
     * 是否同步执行
     * 当多个线程同时访问相同的key时，是否同步执行方法
     *
     * @return 是否同步
     */
    boolean sync() default false;

    /**
     * 过期时间
     *
     * @return 过期时间
     */
    long expiration() default -1;

    /**
     * 过期时间单位
     *
     * @return 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}