package com.genlib.cache.core;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存管理器统计信息
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class CacheManagerStats {

    /** 缓存数量 */
    private int cacheCount;

    /** 总命中次数 */
    private long totalHitCount;

    /** 总未命中次数 */
    private long totalMissCount;

    /** 总加载次数 */
    private long totalLoadCount;

    /** 总驱逐次数 */
    private long totalEvictionCount;

    /** 各缓存的统计信息 */
    private Map<String, CacheStats> cacheStatsMap;

    /** 统计时间 */
    private LocalDateTime statisticsTime;

    /**
     * 构造方法
     */
    public CacheManagerStats() {
        this.cacheStatsMap = new HashMap<>();
        this.statisticsTime = LocalDateTime.now();
    }

    /**
     * 构造方法
     *
     * @param cacheStatsMap 缓存统计信息Map
     */
    public CacheManagerStats(Map<String, CacheStats> cacheStatsMap) {
        this.cacheStatsMap = new HashMap<>(cacheStatsMap);
        this.statisticsTime = LocalDateTime.now();
        this.calculateTotals();
    }

    /**
     * 计算总计数据
     */
    private void calculateTotals() {
        this.cacheCount = cacheStatsMap.size();
        this.totalHitCount = 0;
        this.totalMissCount = 0;
        this.totalLoadCount = 0;
        this.totalEvictionCount = 0;

        for (CacheStats stats : cacheStatsMap.values()) {
            this.totalHitCount += stats.getHitCount();
            this.totalMissCount += stats.getMissCount();
            this.totalLoadCount += stats.getLoadCount();
            this.totalEvictionCount += stats.getEvictionCount();
        }
    }

    /**
     * 获取总请求数
     *
     * @return 总请求数
     */
    public long getTotalRequestCount() {
        return totalHitCount + totalMissCount;
    }

    /**
     * 获取总命中率
     *
     * @return 总命中率（0.0-1.0）
     */
    public double getTotalHitRate() {
        long totalRequestCount = getTotalRequestCount();
        return totalRequestCount == 0 ? 0.0 : (double) totalHitCount / totalRequestCount;
    }

    /**
     * 获取总未命中率
     *
     * @return 总未命中率（0.0-1.0）
     */
    public double getTotalMissRate() {
        return 1.0 - getTotalHitRate();
    }

    /**
     * 获取指定缓存的统计信息
     *
     * @param cacheName 缓存名称
     * @return 缓存统计信息
     */
    public CacheStats getCacheStats(String cacheName) {
        return cacheStatsMap.get(cacheName);
    }

    /**
     * 添加缓存统计信息
     *
     * @param cacheName 缓存名称
     * @param stats 统计信息
     */
    public void addCacheStats(String cacheName, CacheStats stats) {
        this.cacheStatsMap.put(cacheName, stats);
        this.calculateTotals();
    }

    /**
     * 移除缓存统计信息
     *
     * @param cacheName 缓存名称
     */
    public void removeCacheStats(String cacheName) {
        this.cacheStatsMap.remove(cacheName);
        this.calculateTotals();
    }

    /**
     * 获取性能最好的缓存（命中率最高）
     *
     * @return 缓存名称
     */
    public String getBestPerformanceCache() {
        String bestCache = null;
        double bestHitRate = 0.0;

        for (Map.Entry<String, CacheStats> entry : cacheStatsMap.entrySet()) {
            double hitRate = entry.getValue().getHitRate();
            if (hitRate > bestHitRate) {
                bestHitRate = hitRate;
                bestCache = entry.getKey();
            }
        }

        return bestCache;
    }

    /**
     * 获取性能最差的缓存（命中率最低）
     *
     * @return 缓存名称
     */
    public String getWorstPerformanceCache() {
        String worstCache = null;
        double worstHitRate = 1.0;

        for (Map.Entry<String, CacheStats> entry : cacheStatsMap.entrySet()) {
            double hitRate = entry.getValue().getHitRate();
            if (hitRate < worstHitRate) {
                worstHitRate = hitRate;
                worstCache = entry.getKey();
            }
        }

        return worstCache;
    }

    /**
     * 获取最活跃的缓存（请求次数最多）
     *
     * @return 缓存名称
     */
    public String getMostActiveCache() {
        String mostActiveCache = null;
        long maxRequestCount = 0;

        for (Map.Entry<String, CacheStats> entry : cacheStatsMap.entrySet()) {
            long requestCount = entry.getValue().getRequestCount();
            if (requestCount > maxRequestCount) {
                maxRequestCount = requestCount;
                mostActiveCache = entry.getKey();
            }
        }

        return mostActiveCache;
    }

    // ================== Getter 和 Setter ==================

    public int getCacheCount() {
        return cacheCount;
    }

    public long getTotalHitCount() {
        return totalHitCount;
    }

    public long getTotalMissCount() {
        return totalMissCount;
    }

    public long getTotalLoadCount() {
        return totalLoadCount;
    }

    public long getTotalEvictionCount() {
        return totalEvictionCount;
    }

    public Map<String, CacheStats> getCacheStatsMap() {
        return new HashMap<>(cacheStatsMap);
    }

    public LocalDateTime getStatisticsTime() {
        return statisticsTime;
    }

    @Override
    public String toString() {
        return String.format(
            "CacheManagerStats{cacheCount=%d, totalRequestCount=%d, totalHitRate=%.2f%%, " +
            "totalLoadCount=%d, totalEvictionCount=%d, statisticsTime=%s}",
            cacheCount, getTotalRequestCount(), getTotalHitRate() * 100,
            totalLoadCount, totalEvictionCount, statisticsTime
        );
    }
}