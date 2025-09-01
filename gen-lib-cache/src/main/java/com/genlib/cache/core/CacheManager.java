package com.genlib.cache.core;

import java.util.Collection;
import java.util.Optional;

/**
 * 缓存管理器接口
 * 负责管理多个缓存实例
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public interface CacheManager {

    /**
     * 获取指定名称的缓存
     *
     * @param name 缓存名称
     * @return 缓存实例
     */
    Optional<Cache<Object, Object>> getCache(String name);

    /**
     * 获取指定名称和类型的缓存
     *
     * @param name 缓存名称
     * @param keyType 键类型
     * @param valueType 值类型
     * @param <K> 键泛型
     * @param <V> 值泛型
     * @return 缓存实例
     */
    <K, V> Optional<Cache<K, V>> getCache(String name, Class<K> keyType, Class<V> valueType);

    /**
     * 创建或获取缓存
     *
     * @param name 缓存名称
     * @return 缓存实例
     */
    Cache<Object, Object> createCache(String name);

    /**
     * 创建或获取指定类型的缓存
     *
     * @param name 缓存名称
     * @param keyType 键类型
     * @param valueType 值类型
     * @param <K> 键泛型
     * @param <V> 值泛型
     * @return 缓存实例
     */
    <K, V> Cache<K, V> createCache(String name, Class<K> keyType, Class<V> valueType);

    /**
     * 使用配置创建缓存
     *
     * @param name 缓存名称
     * @param config 缓存配置
     * @return 缓存实例
     */
    Cache<Object, Object> createCache(String name, CacheConfig config);

    /**
     * 使用配置创建指定类型的缓存
     *
     * @param name 缓存名称
     * @param config 缓存配置
     * @param keyType 键类型
     * @param valueType 值类型
     * @param <K> 键泛型
     * @param <V> 值泛型
     * @return 缓存实例
     */
    <K, V> Cache<K, V> createCache(String name, CacheConfig config, Class<K> keyType, Class<V> valueType);

    /**
     * 移除缓存
     *
     * @param name 缓存名称
     * @return 是否成功移除
     */
    boolean removeCache(String name);

    /**
     * 获取所有缓存名称
     *
     * @return 缓存名称集合
     */
    Collection<String> getCacheNames();

    /**
     * 检查缓存是否存在
     *
     * @param name 缓存名称
     * @return 是否存在
     */
    boolean hasCache(String name);

    /**
     * 清空所有缓存
     */
    void clearAll();

    /**
     * 获取缓存数量
     *
     * @return 缓存数量
     */
    int getCacheCount();

    /**
     * 获取缓存管理器统计信息
     *
     * @return 统计信息
     */
    CacheManagerStats getStats();

    /**
     * 关闭缓存管理器
     */
    void close();

    /**
     * 检查缓存管理器是否已关闭
     *
     * @return 是否已关闭
     */
    boolean isClosed();

    /**
     * 获取原生缓存管理器对象
     *
     * @return 原生缓存管理器对象
     */
    Object getNativeCacheManager();
}