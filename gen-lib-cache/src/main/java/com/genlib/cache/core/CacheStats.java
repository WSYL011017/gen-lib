package com.genlib.cache.core;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存统计信息
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class CacheStats {

    /** 命中次数 */
    private final AtomicLong hitCount = new AtomicLong(0);

    /** 未命中次数 */
    private final AtomicLong missCount = new AtomicLong(0);

    /** 加载次数 */
    private final AtomicLong loadCount = new AtomicLong(0);

    /** 加载时间总和（纳秒） */
    private final AtomicLong loadTime = new AtomicLong(0);

    /** 驱逐次数 */
    private final AtomicLong evictionCount = new AtomicLong(0);

    /** 创建时间 */
    private final LocalDateTime createTime = LocalDateTime.now();

    /** 最后重置时间 */
    private volatile LocalDateTime lastResetTime = LocalDateTime.now();

    /**
     * 记录命中
     */
    public void recordHit() {
        hitCount.incrementAndGet();
    }

    /**
     * 记录未命中
     */
    public void recordMiss() {
        missCount.incrementAndGet();
    }

    /**
     * 记录加载
     *
     * @param loadTimeNanos 加载时间（纳秒）
     */
    public void recordLoad(long loadTimeNanos) {
        loadCount.incrementAndGet();
        loadTime.addAndGet(loadTimeNanos);
    }

    /**
     * 记录驱逐
     */
    public void recordEviction() {
        evictionCount.incrementAndGet();
    }

    /**
     * 获取命中次数
     *
     * @return 命中次数
     */
    public long getHitCount() {
        return hitCount.get();
    }

    /**
     * 获取未命中次数
     *
     * @return 未命中次数
     */
    public long getMissCount() {
        return missCount.get();
    }

    /**
     * 获取请求总数
     *
     * @return 请求总数
     */
    public long getRequestCount() {
        return getHitCount() + getMissCount();
    }

    /**
     * 获取命中率
     *
     * @return 命中率（0.0-1.0）
     */
    public double getHitRate() {
        long requestCount = getRequestCount();
        return requestCount == 0 ? 0.0 : (double) getHitCount() / requestCount;
    }

    /**
     * 获取未命中率
     *
     * @return 未命中率（0.0-1.0）
     */
    public double getMissRate() {
        return 1.0 - getHitRate();
    }

    /**
     * 获取加载次数
     *
     * @return 加载次数
     */
    public long getLoadCount() {
        return loadCount.get();
    }

    /**
     * 获取加载时间总和（纳秒）
     *
     * @return 加载时间总和
     */
    public long getTotalLoadTime() {
        return loadTime.get();
    }

    /**
     * 获取平均加载时间（纳秒）
     *
     * @return 平均加载时间
     */
    public double getAverageLoadTime() {
        long count = getLoadCount();
        return count == 0 ? 0.0 : (double) getTotalLoadTime() / count;
    }

    /**
     * 获取驱逐次数
     *
     * @return 驱逐次数
     */
    public long getEvictionCount() {
        return evictionCount.get();
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 获取最后重置时间
     *
     * @return 最后重置时间
     */
    public LocalDateTime getLastResetTime() {
        return lastResetTime;
    }

    /**
     * 重置统计信息
     */
    public void reset() {
        hitCount.set(0);
        missCount.set(0);
        loadCount.set(0);
        loadTime.set(0);
        evictionCount.set(0);
        lastResetTime = LocalDateTime.now();
    }

    /**
     * 合并其他统计信息
     *
     * @param other 其他统计信息
     */
    public void merge(CacheStats other) {
        if (other == null) {
            return;
        }
        hitCount.addAndGet(other.getHitCount());
        missCount.addAndGet(other.getMissCount());
        loadCount.addAndGet(other.getLoadCount());
        loadTime.addAndGet(other.getTotalLoadTime());
        evictionCount.addAndGet(other.getEvictionCount());
    }

    /**
     * 创建统计信息副本
     *
     * @return 统计信息副本
     */
    public CacheStats copy() {
        CacheStats copy = new CacheStats();
        copy.hitCount.set(this.getHitCount());
        copy.missCount.set(this.getMissCount());
        copy.loadCount.set(this.getLoadCount());
        copy.loadTime.set(this.getTotalLoadTime());
        copy.evictionCount.set(this.getEvictionCount());
        return copy;
    }

    @Override
    public String toString() {
        long requestCount = getRequestCount();
        return String.format(
            "CacheStats{hitCount=%d, missCount=%d, requestCount=%d, hitRate=%.2f%%, " +
            "loadCount=%d, avgLoadTime=%.2fns, evictionCount=%d, createTime=%s}",
            getHitCount(), getMissCount(), requestCount, getHitRate() * 100,
            getLoadCount(), getAverageLoadTime(), getEvictionCount(), createTime
        );
    }
}