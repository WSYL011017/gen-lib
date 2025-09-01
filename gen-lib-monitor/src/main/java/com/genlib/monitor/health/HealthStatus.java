package com.genlib.monitor.health;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康状态
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class HealthStatus {

    private final Status status;
    private final Map<String, Object> details;

    private HealthStatus(Status status, Map<String, Object> details) {
        this.status = status;
        this.details = details != null ? new HashMap<>(details) : new HashMap<>();
    }

    /**
     * 创建UP状态
     */
    public static Builder up() {
        return new Builder(Status.UP);
    }

    /**
     * 创建DOWN状态
     */
    public static Builder down() {
        return new Builder(Status.DOWN);
    }

    /**
     * 创建UNKNOWN状态
     */
    public static Builder unknown() {
        return new Builder(Status.UNKNOWN);
    }

    // getters
    public Status getStatus() { return status; }
    public Map<String, Object> getDetails() { return new HashMap<>(details); }

    /**
     * 状态枚举
     */
    public enum Status {
        UP, DOWN, UNKNOWN
    }

    /**
     * 建造者模式
     */
    public static class Builder {
        private final Status status;
        private final Map<String, Object> details = new HashMap<>();

        private Builder(Status status) {
            this.status = status;
        }

        public Builder withDetail(String key, Object value) {
            details.put(key, value);
            return this;
        }

        public Builder withDetails(Map<String, Object> details) {
            this.details.putAll(details);
            return this;
        }

        public HealthStatus build() {
            return new HealthStatus(status, details);
        }
    }

    @Override
    public String toString() {
        return "HealthStatus{" +
                "status=" + status +
                ", details=" + details +
                '}';
    }
}

/**
 * 健康检查指示器接口
 */
interface HealthIndicator {
    HealthStatus health();
}

/**
 * 健康检查结果
 */
class HealthCheckResult {
    private final HealthStatus.Status overallStatus;
    private final Map<String, HealthStatus> details;
    private final long duration;
    private final long timestamp;

    public HealthCheckResult(HealthStatus.Status overallStatus, 
                           Map<String, HealthStatus> details, 
                           long duration) {
        this.overallStatus = overallStatus;
        this.details = new HashMap<>(details);
        this.duration = duration;
        this.timestamp = System.currentTimeMillis();
    }

    // getters
    public HealthStatus.Status getOverallStatus() { return overallStatus; }
    public Map<String, HealthStatus> getDetails() { return new HashMap<>(details); }
    public long getDuration() { return duration; }
    public long getTimestamp() { return timestamp; }

    public boolean isHealthy() {
        return overallStatus == HealthStatus.Status.UP;
    }

    @Override
    public String toString() {
        return "HealthCheckResult{" +
                "overallStatus=" + overallStatus +
                ", details=" + details +
                ", duration=" + duration +
                ", timestamp=" + timestamp +
                '}';
    }
}