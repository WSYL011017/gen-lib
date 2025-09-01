package com.genlib.monitor.performance;

import java.util.HashMap;
import java.util.Map;

/**
 * 性能指标数据
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class PerformanceMetrics {

    private long timestamp;
    private long collectTime;
    private final Map<String, Map<String, Object>> categories;

    public PerformanceMetrics() {
        this.categories = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 添加指标分类
     */
    public void addCategory(String categoryName, Map<String, Object> metrics) {
        categories.put(categoryName, new HashMap<>(metrics));
    }

    /**
     * 获取指标分类
     */
    public Map<String, Object> getCategory(String categoryName) {
        return categories.get(categoryName);
    }

    /**
     * 获取所有分类
     */
    public Map<String, Map<String, Object>> getAllCategories() {
        return new HashMap<>(categories);
    }

    /**
     * 获取特定指标值
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetric(String category, String metric) {
        Map<String, Object> categoryMetrics = categories.get(category);
        return categoryMetrics != null ? (T) categoryMetrics.get(metric) : null;
    }

    /**
     * 设置特定指标值
     */
    public void setMetric(String category, String metric, Object value) {
        categories.computeIfAbsent(category, k -> new HashMap<>()).put(metric, value);
    }

    // getters and setters
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public long getCollectTime() { return collectTime; }
    public void setCollectTime(long collectTime) { this.collectTime = collectTime; }

    @Override
    public String toString() {
        return "PerformanceMetrics{" +
                "timestamp=" + timestamp +
                ", collectTime=" + collectTime +
                ", categories=" + categories +
                '}';
    }
}

/**
 * 性能监听器接口
 */
interface PerformanceListener {
    void onMetricsCollected(PerformanceMetrics metrics);
}