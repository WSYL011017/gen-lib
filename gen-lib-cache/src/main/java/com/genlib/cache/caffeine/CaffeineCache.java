package com.genlib.cache.caffeine;

import com.genlib.cache.core.Cache;
import com.genlib.cache.core.CacheConfig;
import com.genlib.cache.core.CacheStats;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Weigher;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine缓存实现
 * 基于Caffeine的高性能本地缓存实现
 * 
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class CaffeineCache<K, V> implements Cache<K, V> {

    /** 缓存名称 */
    private final String name;

    /** Caffeine缓存实例 */
    private final com.github.benmanes.caffeine.cache.Cache<K, V> caffeineCache;

    /** 缓存配置 */
    private final CacheConfig config;

    /** 缓存统计 */
    private final CacheStats stats;

    /** 过期时间映射（用于记录每个键的过期时间） */
    private final Map<K, Long> expirationMap = new ConcurrentHashMap<>();

    /**
     * 构造方法
     *
     * @param name 缓存名称
     * @param config 缓存配置
     */
    public CaffeineCache(String name, CacheConfig config) {
        this.name = name;
        this.config = config;
        this.stats = new CacheStats();
        this.caffeineCache = buildCaffeineCache(config);
    }

    /**
     * 构建Caffeine缓存
     *
     * @param config 缓存配置
     * @return Caffeine缓存实例
     */
    private com.github.benmanes.caffeine.cache.Cache<K, V> buildCaffeineCache(CacheConfig config) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();

        // 设置最大缓存数量
        if (config.getMaxSize() != null && config.getMaxSize() > 0) {
            builder.maximumSize(config.getMaxSize());
        }

        // 设置过期时间
        if (config.getExpiration() != null) {
            builder.expireAfterWrite(config.getExpiration().toNanos(), TimeUnit.NANOSECONDS);
        }

        // 启用统计
        if (config.getStatsEnabled() != null && config.getStatsEnabled()) {
            builder.recordStats();
        }

        // 设置移除监听器
        builder.removalListener((RemovalListener<K, V>) (key, value, cause) -> {
            stats.recordEviction();
            expirationMap.remove(key);
        });

        // 根据驱逐策略设置权重器
        if (config.getEvictionPolicy() == CacheConfig.EvictionPolicy.LFU) {
            builder.weigher((Weigher<K, V>) (key, value) -> 1);
            builder.maximumWeight(config.getMaxSize() != null ? config.getMaxSize() : 10000L);
        }

        return builder.build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void put(K key, V value) {
        if (key == null || value == null) {
            return;
        }
        caffeineCache.put(key, value);
        
        // 记录过期时间
        if (config.getExpiration() != null) {
            long expirationTime = System.currentTimeMillis() + config.getExpiration().toMillis();
            expirationMap.put(key, expirationTime);
        }
    }

    @Override
    public void put(K key, V value, Duration expiration) {
        if (key == null || value == null) {
            return;
        }
        
        // Caffeine不支持单个键的过期时间，这里模拟实现
        caffeineCache.put(key, value);
        
        if (expiration != null) {
            long expirationTime = System.currentTimeMillis() + expiration.toMillis();
            expirationMap.put(key, expirationTime);
        }
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        if (key == null || value == null) {
            return false;
        }
        
        V existingValue = caffeineCache.getIfPresent(key);
        if (existingValue == null) {
            put(key, value);
            return true;
        }
        return false;
    }

    @Override
    public boolean putIfAbsent(K key, V value, Duration expiration) {
        if (key == null || value == null) {
            return false;
        }
        
        V existingValue = caffeineCache.getIfPresent(key);
        if (existingValue == null) {
            put(key, value, expiration);
            return true;
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> entries) {
        if (entries == null || entries.isEmpty()) {
            return;
        }
        caffeineCache.putAll(entries);
        
        // 记录过期时间
        if (config.getExpiration() != null) {
            long expirationTime = System.currentTimeMillis() + config.getExpiration().toMillis();
            for (K key : entries.keySet()) {
                expirationMap.put(key, expirationTime);
            }
        }
    }

    @Override
    public Optional<V> get(K key) {
        if (key == null) {
            stats.recordMiss();
            return Optional.empty();
        }
        
        // 检查是否过期
        if (isExpired(key)) {
            caffeineCache.invalidate(key);
            expirationMap.remove(key);
            stats.recordMiss();
            return Optional.empty();
        }
        
        V value = caffeineCache.getIfPresent(key);
        if (value != null) {
            stats.recordHit();
            return Optional.of(value);
        } else {
            stats.recordMiss();
            return Optional.empty();
        }
    }

    @Override
    public V get(K key, Callable<V> valueLoader) {
        if (key == null || valueLoader == null) {
            return null;
        }
        
        long startTime = System.nanoTime();
        try {
            V value = caffeineCache.get(key, k -> {
                try {
                    return valueLoader.call();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to load value for key: " + k, e);
                }
            });
            
            long loadTime = System.nanoTime() - startTime;
            stats.recordLoad(loadTime);
            
            if (value != null) {
                stats.recordHit();
                // 记录过期时间
                if (config.getExpiration() != null) {
                    long expirationTime = System.currentTimeMillis() + config.getExpiration().toMillis();
                    expirationMap.put(key, expirationTime);
                }
            } else {
                stats.recordMiss();
            }
            
            return value;
        } catch (Exception e) {
            stats.recordMiss();
            throw new RuntimeException("Failed to get value for key: " + key, e);
        }
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        Optional<V> value = get(key);
        return value.orElse(defaultValue);
    }

    @Override
    public Map<K, V> getAll(Collection<? extends K> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<K, V> result = new HashMap<>();
        for (K key : keys) {
            Optional<V> value = get(key);
            value.ifPresent(v -> result.put(key, v));
        }
        
        return result;
    }

    @Override
    public Optional<V> evict(K key) {
        if (key == null) {
            return Optional.empty();
        }
        
        V value = caffeineCache.getIfPresent(key);
        if (value != null) {
            caffeineCache.invalidate(key);
            expirationMap.remove(key);
            stats.recordEviction();
            return Optional.of(value);
        }
        
        return Optional.empty();
    }

    @Override
    public void evictAll(Collection<? extends K> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        
        for (K key : keys) {
            evict(key);
        }
    }

    @Override
    public void clear() {
        caffeineCache.invalidateAll();
        expirationMap.clear();
    }

    @Override
    public boolean exists(K key) {
        if (key == null) {
            return false;
        }
        
        if (isExpired(key)) {
            caffeineCache.invalidate(key);
            expirationMap.remove(key);
            return false;
        }
        
        return caffeineCache.getIfPresent(key) != null;
    }

    @Override
    public Set<K> keys() {
        cleanupExpired();
        return new HashSet<>(caffeineCache.asMap().keySet());
    }

    @Override
    public long size() {
        cleanupExpired();
        return caffeineCache.estimatedSize();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean expire(K key, Duration expiration) {
        if (key == null || !exists(key)) {
            return false;
        }
        
        if (expiration != null) {
            long expirationTime = System.currentTimeMillis() + expiration.toMillis();
            expirationMap.put(key, expirationTime);
        } else {
            expirationMap.remove(key);
        }
        
        return true;
    }

    @Override
    public Optional<Duration> getExpiration(K key) {
        if (key == null) {
            return Optional.empty();
        }
        
        Long expirationTime = expirationMap.get(key);
        if (expirationTime != null) {
            long remainingTime = expirationTime - System.currentTimeMillis();
            return remainingTime > 0 ? 
                Optional.of(Duration.ofMillis(remainingTime)) : 
                Optional.empty();
        }
        
        return Optional.empty();
    }

    @Override
    public boolean persist(K key) {
        if (key == null || !exists(key)) {
            return false;
        }
        
        expirationMap.remove(key);
        return true;
    }

    @Override
    public CacheStats getStats() {
        // 合并Caffeine统计和自定义统计
        com.github.benmanes.caffeine.cache.stats.CacheStats caffeineStats = caffeineCache.stats();
        
        CacheStats mergedStats = stats.copy();
        mergedStats.recordHit(); // 添加Caffeine的命中统计
        
        return mergedStats;
    }

    @Override
    public Object getNativeCache() {
        return caffeineCache;
    }

    /**
     * 检查键是否过期
     *
     * @param key 键
     * @return 是否过期
     */
    private boolean isExpired(K key) {
        Long expirationTime = expirationMap.get(key);
        return expirationTime != null && System.currentTimeMillis() > expirationTime;
    }

    /**
     * 清理过期的键
     */
    private void cleanupExpired() {
        long currentTime = System.currentTimeMillis();
        List<K> expiredKeys = new ArrayList<>();
        
        for (Map.Entry<K, Long> entry : expirationMap.entrySet()) {
            if (currentTime > entry.getValue()) {
                expiredKeys.add(entry.getKey());
            }
        }
        
        for (K key : expiredKeys) {
            caffeineCache.invalidate(key);
            expirationMap.remove(key);
        }
    }

    /**
     * 获取缓存配置
     *
     * @return 缓存配置
     */
    public CacheConfig getConfig() {
        return config;
    }
}