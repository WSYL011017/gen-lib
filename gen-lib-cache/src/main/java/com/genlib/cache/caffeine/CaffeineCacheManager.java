package com.genlib.cache.caffeine;

import com.genlib.cache.core.Cache;
import com.genlib.cache.core.CacheConfig;
import com.genlib.cache.core.CacheManager;
import com.genlib.cache.core.CacheManagerStats;
import com.genlib.cache.core.CacheStats;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Caffeine缓存管理器
 * 管理多个Caffeine缓存实例
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class CaffeineCacheManager implements CacheManager {

    /** 缓存实例映射 */
    private final ConcurrentMap<String, Cache<Object, Object>> caches = new ConcurrentHashMap<>();

    /** 默认缓存配置 */
    private CacheConfig defaultConfig;

    /** 是否已关闭 */
    private volatile boolean closed = false;

    /**
     * 构造方法
     */
    public CaffeineCacheManager() {
        this.defaultConfig = CacheConfig.defaultConfig("default");
    }

    /**
     * 构造方法
     *
     * @param defaultConfig 默认缓存配置
     */
    public CaffeineCacheManager(CacheConfig defaultConfig) {
        this.defaultConfig = defaultConfig != null ? defaultConfig : CacheConfig.defaultConfig("default");
    }

    @Override
    public Optional<Cache<Object, Object>> getCache(String name) {
        checkNotClosed();
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(caches.get(name));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Optional<Cache<K, V>> getCache(String name, Class<K> keyType, Class<V> valueType) {
        Optional<Cache<Object, Object>> cache = getCache(name);
        return cache.map(c -> (Cache<K, V>) c);
    }

    @Override
    public Cache<Object, Object> createCache(String name) {
        return createCache(name, defaultConfig);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> createCache(String name, Class<K> keyType, Class<V> valueType) {
        Cache<Object, Object> cache = createCache(name);
        return (Cache<K, V>) cache;
    }

    @Override
    public Cache<Object, Object> createCache(String name, CacheConfig config) {
        checkNotClosed();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Cache name cannot be null or empty");
        }

        CacheConfig finalConfig = config != null ? config : defaultConfig;
        finalConfig.setName(name);

        return caches.computeIfAbsent(name, cacheName -> {
            CaffeineCache<Object, Object> cache = new CaffeineCache<>(cacheName, finalConfig);
            return cache;
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> createCache(String name, CacheConfig config, Class<K> keyType, Class<V> valueType) {
        Cache<Object, Object> cache = createCache(name, config);
        return (Cache<K, V>) cache;
    }

    @Override
    public boolean removeCache(String name) {
        checkNotClosed();
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        Cache<Object, Object> removedCache = caches.remove(name);
        if (removedCache != null) {
            removedCache.clear();
            return true;
        }
        return false;
    }

    @Override
    public Collection<String> getCacheNames() {
        checkNotClosed();
        return caches.keySet();
    }

    @Override
    public boolean hasCache(String name) {
        checkNotClosed();
        return name != null && caches.containsKey(name);
    }

    @Override
    public void clearAll() {
        checkNotClosed();
        for (Cache<Object, Object> cache : caches.values()) {
            cache.clear();
        }
    }

    @Override
    public int getCacheCount() {
        checkNotClosed();
        return caches.size();
    }

    @Override
    public CacheManagerStats getStats() {
        checkNotClosed();
        ConcurrentHashMap<String, CacheStats> statsMap = new ConcurrentHashMap<>();
        
        for (String cacheName : caches.keySet()) {
            Cache<Object, Object> cache = caches.get(cacheName);
            if (cache != null) {
                statsMap.put(cacheName, cache.getStats());
            }
        }
        
        return new CacheManagerStats(statsMap);
    }

    @Override
    public void close() {
        if (!closed) {
            // 先清理缓存，再设置关闭标志
            for (Cache<Object, Object> cache : caches.values()) {
                cache.clear();
            }
            caches.clear();
            closed = true;
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public Object getNativeCacheManager() {
        return this;
    }

    /**
     * 设置默认缓存配置
     *
     * @param defaultConfig 默认配置
     */
    public void setDefaultConfig(CacheConfig defaultConfig) {
        this.defaultConfig = defaultConfig != null ? defaultConfig : CacheConfig.defaultConfig("default");
    }

    /**
     * 获取默认缓存配置
     *
     * @return 默认配置
     */
    public CacheConfig getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * 预创建缓存
     *
     * @param cacheConfigs 缓存配置数组
     */
    public void preCreateCaches(CacheConfig... cacheConfigs) {
        checkNotClosed();
        if (cacheConfigs == null || cacheConfigs.length == 0) {
            return;
        }

        for (CacheConfig config : cacheConfigs) {
            if (config != null && config.getName() != null) {
                createCache(config.getName(), config);
            }
        }
    }

    /**
     * 批量移除缓存
     *
     * @param cacheNames 缓存名称数组
     * @return 成功移除的数量
     */
    public int removeCaches(String... cacheNames) {
        checkNotClosed();
        if (cacheNames == null || cacheNames.length == 0) {
            return 0;
        }

        int removedCount = 0;
        for (String cacheName : cacheNames) {
            if (removeCache(cacheName)) {
                removedCount++;
            }
        }
        return removedCount;
    }

    /**
     * 获取所有缓存的总大小
     *
     * @return 总缓存项数量
     */
    public long getTotalSize() {
        checkNotClosed();
        long totalSize = 0;
        for (Cache<Object, Object> cache : caches.values()) {
            totalSize += cache.size();
        }
        return totalSize;
    }

    /**
     * 检查是否所有缓存都为空
     *
     * @return 是否所有缓存都为空
     */
    public boolean isAllEmpty() {
        checkNotClosed();
        for (Cache<Object, Object> cache : caches.values()) {
            if (!cache.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 刷新所有缓存统计信息
     */
    public void refreshStats() {
        checkNotClosed();
        // Caffeine会自动更新统计信息，这里可以触发清理过期缓存等操作
        for (Cache<Object, Object> cache : caches.values()) {
            cache.size(); // 触发清理操作
        }
    }

    /**
     * 检查缓存管理器是否已关闭
     */
    private void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("CacheManager has been closed");
        }
    }

    @Override
    public String toString() {
        return String.format("CaffeineCacheManager{cacheCount=%d, closed=%s, totalSize=%d}",
                           getCacheCount(), closed, getTotalSize());
    }
}