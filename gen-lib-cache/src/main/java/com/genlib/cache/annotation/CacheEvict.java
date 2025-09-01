package com.genlib.cache.annotation;

import java.lang.annotation.*;

/**
 * 缓存清除注解
 * 用于清除缓存
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {

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
     * 只有当条件为true时才清除缓存
     *
     * @return 条件表达式
     */
    String condition() default "";

    /**
     * 是否清除所有缓存
     *
     * @return 是否清除所有
     */
    boolean allEntries() default false;

    /**
     * 是否在方法执行前清除缓存
     * true: 方法执行前清除
     * false: 方法执行后清除
     *
     * @return 是否提前清除
     */
    boolean beforeInvocation() default false;
}