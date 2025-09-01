package com.genlib.cache.core;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * 统一缓存接口
 * 提供通用的缓存操作方法，支持多种缓存实现
 * 
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public interface Cache<K, V> {

    /**
     * 获取缓存名称
     *
     * @return 缓存名称
     */
    String getName();

    /**
     * 存储键值对
     *
     * @param key 键
     * @param value 值
     */
    void put(K key, V value);

    /**
     * 存储键值对，带过期时间
     *
     * @param key 键
     * @param value 值
     * @param expiration 过期时间
     */
    void put(K key, V value, Duration expiration);

    /**
     * 如果键不存在则存储
     *
     * @param key 键
     * @param value 值
     * @return 是否成功存储（键不存在时返回true）
     */
    boolean putIfAbsent(K key, V value);

    /**
     * 如果键不存在则存储，带过期时间
     *
     * @param key 键
     * @param value 值
     * @param expiration 过期时间
     * @return 是否成功存储（键不存在时返回true）
     */
    boolean putIfAbsent(K key, V value, Duration expiration);

    /**
     * 批量存储
     *
     * @param entries 键值对集合
     */
    void putAll(Map<? extends K, ? extends V> entries);

    /**
     * 获取值
     *
     * @param key 键
     * @return 值（可能为空）
     */
    Optional<V> get(K key);

    /**
     * 获取值，如果不存在则通过valueLoader加载
     *
     * @param key 键
     * @param valueLoader 值加载器
     * @return 值
     */
    V get(K key, Callable<V> valueLoader);

    /**
     * 获取值，如果不存在则返回默认值
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 值或默认值
     */
    V getOrDefault(K key, V defaultValue);

    /**
     * 批量获取
     *
     * @param keys 键集合
     * @return 键值对Map
     */
    Map<K, V> getAll(Collection<? extends K> keys);

    /**
     * 移除键值对
     *
     * @param key 键
     * @return 被移除的值（可能为空）
     */
    Optional<V> evict(K key);

    /**
     * 批量移除
     *
     * @param keys 键集合
     */
    void evictAll(Collection<? extends K> keys);

    /**
     * 清空所有缓存
     */
    void clear();

    /**
     * 检查键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    boolean exists(K key);

    /**
     * 获取所有键
     *
     * @return 键集合
     */
    Set<K> keys();

    /**
     * 获取缓存大小
     *
     * @return 缓存项数量
     */
    long size();

    /**
     * 检查缓存是否为空
     *
     * @return 是否为空
     */
    boolean isEmpty();

    /**
     * 设置键的过期时间
     *
     * @param key 键
     * @param expiration 过期时间
     * @return 是否设置成功
     */
    boolean expire(K key, Duration expiration);

    /**
     * 获取键的剩余过期时间
     *
     * @param key 键
     * @return 剩余过期时间（永不过期返回null）
     */
    Optional<Duration> getExpiration(K key);

    /**
     * 移除键的过期时间（设置为永不过期）
     *
     * @param key 键
     * @return 是否操作成功
     */
    boolean persist(K key);

    /**
     * 刷新缓存（将内存中的数据写入持久化存储）
     */
    default void flush() {
        // 默认空实现，各实现类可根据需要重写
    }

    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计
     */
    CacheStats getStats();

    /**
     * 获取原生缓存对象
     *
     * @return 原生缓存对象
     */
    Object getNativeCache();
}