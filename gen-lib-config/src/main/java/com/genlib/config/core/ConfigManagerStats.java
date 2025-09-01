package com.genlib.config.core;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理器统计信息
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class ConfigManagerStats {

    /** 配置提供者数量 */
    private int providerCount;

    /** 总配置项数量 */
    private int totalConfigCount;

    /** 各提供者的配置数量 */
    private Map<String, Integer> providerConfigCounts;

    /** 各提供者的状态 */
    private Map<String, Boolean> providerStatuses;

    /** 统计时间 */
    private LocalDateTime statisticsTime;

    /** 总查询次数 */
    private long totalQueryCount;

    /** 命中次数 */
    private long hitCount;

    /**
     * 构造方法
     */
    public ConfigManagerStats() {
        this.providerConfigCounts = new HashMap<>();
        this.providerStatuses = new HashMap<>();
        this.statisticsTime = LocalDateTime.now();
    }

    /**
     * 添加提供者统计
     *
     * @param providerName 提供者名称
     * @param configCount 配置数量
     * @param available 是否可用
     */
    public void addProviderStats(String providerName, int configCount, boolean available) {
        providerConfigCounts.put(providerName, configCount);
        providerStatuses.put(providerName, available);
        this.totalConfigCount += configCount;
        this.providerCount = providerConfigCounts.size();
    }

    /**
     * 记录查询
     *
     * @param hit 是否命中
     */
    public void recordQuery(boolean hit) {
        totalQueryCount++;
        if (hit) {
            hitCount++;
        }
    }

    /**
     * 获取命中率
     *
     * @return 命中率（0.0-1.0）
     */
    public double getHitRate() {
        return totalQueryCount == 0 ? 0.0 : (double) hitCount / totalQueryCount;
    }

    /**
     * 获取可用提供者数量
     *
     * @return 可用提供者数量
     */
    public int getAvailableProviderCount() {
        return (int) providerStatuses.values().stream().mapToInt(available -> available ? 1 : 0).sum();
    }

    // ================== Getter 和 Setter ==================

    public int getProviderCount() {
        return providerCount;
    }

    public int getTotalConfigCount() {
        return totalConfigCount;
    }

    public Map<String, Integer> getProviderConfigCounts() {
        return new HashMap<>(providerConfigCounts);
    }

    public Map<String, Boolean> getProviderStatuses() {
        return new HashMap<>(providerStatuses);
    }

    public LocalDateTime getStatisticsTime() {
        return statisticsTime;
    }

    public long getTotalQueryCount() {
        return totalQueryCount;
    }

    public long getHitCount() {
        return hitCount;
    }

    @Override
    public String toString() {
        return String.format(
            "ConfigManagerStats{providerCount=%d, availableProviders=%d, totalConfigCount=%d, " +
            "totalQueryCount=%d, hitRate=%.2f%%, statisticsTime=%s}",
            providerCount, getAvailableProviderCount(), totalConfigCount,
            totalQueryCount, getHitRate() * 100, statisticsTime
        );
    }
}