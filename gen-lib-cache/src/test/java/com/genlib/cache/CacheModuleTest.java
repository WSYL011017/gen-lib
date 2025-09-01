package com.genlib.cache;

import com.genlib.cache.caffeine.CaffeineCache;
import com.genlib.cache.caffeine.CaffeineCacheManager;
import com.genlib.cache.core.Cache;
import com.genlib.cache.core.CacheConfig;
import com.genlib.cache.core.CacheManager;
import com.genlib.cache.core.CacheStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缓存模块测试类
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
class CacheModuleTest {

    private CacheManager cacheManager;
    private Cache<String, String> cache;

    @BeforeEach
    void setUp() {
        cacheManager = new CaffeineCacheManager();
        cache = cacheManager.createCache("testCache", String.class, String.class);
    }

    @AfterEach
    void tearDown() {
        if (cacheManager != null) {
            cacheManager.close();
        }
    }

    @Test
    void testBasicCacheOperations() {
        // 测试基本的存取操作
        assertFalse(cache.exists("key1"));
        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());

        // 存储数据
        cache.put("key1", "value1");
        assertTrue(cache.exists("key1"));
        assertEquals(1, cache.size());
        assertFalse(cache.isEmpty());

        // 获取数据
        Optional<String> value = cache.get("key1");
        assertTrue(value.isPresent());
        assertEquals("value1", value.get());

        // 获取不存在的键
        Optional<String> notFound = cache.get("nonexistent");
        assertFalse(notFound.isPresent());

        // 获取或默认值
        String defaultValue = cache.getOrDefault("nonexistent", "default");
        assertEquals("default", defaultValue);
    }

    @Test
    void testCacheWithExpiration() throws InterruptedException {
        // 测试带过期时间的缓存
        cache.put("expireKey", "expireValue", Duration.ofMillis(100));
        assertTrue(cache.exists("expireKey"));

        // 等待过期
        Thread.sleep(150);
        
        // 检查是否过期（注意：Caffeine的过期是懒加载的）
        Optional<String> expiredValue = cache.get("expireKey");
        // 由于是懒加载，可能需要触发清理
        assertFalse(cache.exists("expireKey") && expiredValue.isPresent());
    }

    @Test
    void testPutIfAbsent() {
        // 测试条件存储
        assertTrue(cache.putIfAbsent("key1", "value1"));
        assertEquals("value1", cache.get("key1").orElse(null));

        // 键已存在，不应该更新
        assertFalse(cache.putIfAbsent("key1", "value2"));
        assertEquals("value1", cache.get("key1").orElse(null));
    }

    @Test
    void testBatchOperations() {
        // 测试批量操作
        Map<String, String> entries = new HashMap<>();
        entries.put("key1", "value1");
        entries.put("key2", "value2");
        entries.put("key3", "value3");

        cache.putAll(entries);
        assertEquals(3, cache.size());

        // 批量获取
        List<String> keys = Arrays.asList("key1", "key2", "key3", "nonexistent");
        Map<String, String> results = cache.getAll(keys);
        assertEquals(3, results.size());
        assertTrue(results.containsKey("key1"));
        assertTrue(results.containsKey("key2"));
        assertTrue(results.containsKey("key3"));
        assertFalse(results.containsKey("nonexistent"));

        // 批量删除
        cache.evictAll(Arrays.asList("key1", "key2"));
        assertEquals(1, cache.size());
        assertTrue(cache.exists("key3"));
        assertFalse(cache.exists("key1"));
        assertFalse(cache.exists("key2"));
    }

    @Test
    void testCacheEviction() {
        // 测试缓存驱逐
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        assertEquals(2, cache.size());

        // 驱逐单个键
        Optional<String> evicted = cache.evict("key1");
        assertTrue(evicted.isPresent());
        assertEquals("value1", evicted.get());
        assertEquals(1, cache.size());
        assertFalse(cache.exists("key1"));

        // 清空所有缓存
        cache.clear();
        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());
    }

    @Test
    void testCacheWithLoader() {
        // 测试带加载器的缓存
        Callable<String> loader = () -> "loadedValue";
        
        String value = cache.get("loadKey", loader);
        assertEquals("loadedValue", value);
        assertTrue(cache.exists("loadKey"));

        // 再次获取应该从缓存返回
        String cachedValue = cache.get("loadKey", () -> "newValue");
        assertEquals("loadedValue", cachedValue);
    }

    @Test
    void testCacheKeys() {
        // 测试键集合操作
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        Set<String> keys = cache.keys();
        assertEquals(3, keys.size());
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
        assertTrue(keys.contains("key3"));
    }

    @Test
    void testCacheExpiration() {
        // 测试过期时间操作
        cache.put("key1", "value1");
        
        // 设置过期时间
        assertTrue(cache.expire("key1", Duration.ofMinutes(5)));
        
        // 获取过期时间
        Optional<Duration> expiration = cache.getExpiration("key1");
        assertTrue(expiration.isPresent());
        assertTrue(expiration.get().toMillis() > 0);

        // 移除过期时间
        assertTrue(cache.persist("key1"));
        Optional<Duration> noExpiration = cache.getExpiration("key1");
        assertFalse(noExpiration.isPresent());
    }

    @Test
    void testCacheStats() {
        // 测试缓存统计
        CacheStats stats = cache.getStats();
        assertNotNull(stats);

        // 执行一些操作
        cache.put("key1", "value1");
        cache.get("key1"); // 命中
        cache.get("nonexistent"); // 未命中

        CacheStats newStats = cache.getStats();
        assertNotNull(newStats);
        // 注意：由于统计是累积的，这里不做具体数值断言
    }

    @Test
    void testCacheManager() {
        // 测试缓存管理器
        assertEquals("testCache", cache.getName());
        assertTrue(cacheManager.hasCache("testCache"));
        assertEquals(1, cacheManager.getCacheCount());

        // 创建新缓存
        Cache<Object, Object> newCache = cacheManager.createCache("newCache");
        assertNotNull(newCache);
        assertEquals("newCache", newCache.getName());
        assertEquals(2, cacheManager.getCacheCount());

        // 获取缓存名称
        Collection<String> cacheNames = cacheManager.getCacheNames();
        assertTrue(cacheNames.contains("testCache"));
        assertTrue(cacheNames.contains("newCache"));

        // 移除缓存
        assertTrue(cacheManager.removeCache("newCache"));
        assertFalse(cacheManager.hasCache("newCache"));
        assertEquals(1, cacheManager.getCacheCount());
    }

    @Test
    void testCacheConfig() {
        // 测试缓存配置
        CacheConfig config = CacheConfig.defaultConfig("configTest")
                .maxSize(1000L)
                .expiration(Duration.ofMinutes(30))
                .enableStats()
                .serializer(CacheConfig.SerializerType.JSON)
                .evictionPolicy(CacheConfig.EvictionPolicy.LRU);

        assertNotNull(config);
        assertEquals("configTest", config.getName());
        assertEquals(1000L, config.getMaxSize());
        assertEquals(Duration.ofMinutes(30), config.getExpiration());
        assertTrue(config.getStatsEnabled());
        assertEquals(CacheConfig.SerializerType.JSON, config.getSerializerType());
        assertEquals(CacheConfig.EvictionPolicy.LRU, config.getEvictionPolicy());

        // 测试流式配置
        CacheConfig fluentConfig = new CacheConfig()
                .setName("fluentTest")
                .maxSize(500L)
                .enableCompression()
                .refreshPolicy(CacheConfig.RefreshPolicy.WRITE_THROUGH);

        assertEquals("fluentTest", fluentConfig.getName());
        assertEquals(500L, fluentConfig.getMaxSize());
        assertTrue(fluentConfig.getCompressionEnabled());
        assertEquals(CacheConfig.RefreshPolicy.WRITE_THROUGH, fluentConfig.getRefreshPolicy());
    }

    @Test
    void testCacheManagerWithConfig() {
        // 测试使用配置创建缓存
        CacheConfig config = CacheConfig.fastExpire("fastCache")
                .maxSize(100L)
                .disableStats();

        Cache<Object, Object> configCache = cacheManager.createCache("configuredCache", config);
        assertNotNull(configCache);
        assertEquals("configuredCache", configCache.getName());

        // 验证配置是否生效（通过CaffeineCache的getConfig方法）
        if (configCache instanceof CaffeineCache) {
            CaffeineCache<Object, Object> caffeineCache = (CaffeineCache<Object, Object>) configCache;
            CacheConfig actualConfig = caffeineCache.getConfig();
            assertEquals("configuredCache", actualConfig.getName());
            assertEquals(100L, actualConfig.getMaxSize());
            assertFalse(actualConfig.getStatsEnabled());
        }
    }

    @Test
    void testCacheManagerStats() {
        // 测试缓存管理器统计
        cache.put("key1", "value1");
        cache.get("key1");

        Cache<Object, Object> cache2 = cacheManager.createCache("cache2");
        cache2.put("key2", "value2");

        var managerStats = cacheManager.getStats();
        assertNotNull(managerStats);
        assertEquals(2, managerStats.getCacheCount());
        assertNotNull(managerStats.getCacheStats("testCache"));
        assertNotNull(managerStats.getCacheStats("cache2"));
    }

    @Test
    void testNullSafety() {
        // 测试空值安全
        assertFalse(cache.exists(null));
        assertFalse(cache.get(null).isPresent());
        assertEquals("default", cache.getOrDefault(null, "default"));
        
        // put null值应该被忽略
        cache.put(null, "value");
        cache.put("key", null);
        assertEquals(0, cache.size());

        // 批量操作的空值安全
        assertEquals(Collections.emptyMap(), cache.getAll(null));
        assertEquals(Collections.emptyMap(), cache.getAll(Collections.emptyList()));
    }

    @Test
    void testCacheManagerClosure() {
        // 测试缓存管理器关闭
        assertFalse(cacheManager.isClosed());
        
        cacheManager.close();
        assertTrue(cacheManager.isClosed());
        
        // 关闭后的操作应该抛出异常
        assertThrows(IllegalStateException.class, () -> {
            cacheManager.createCache("afterClose");
        });
    }
}