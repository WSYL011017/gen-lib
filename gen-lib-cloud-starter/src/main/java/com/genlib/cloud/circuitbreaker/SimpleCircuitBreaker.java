package com.genlib.cloud.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * 简单熔断器实现
 * 提供基本的熔断保护功能
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class SimpleCircuitBreaker {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCircuitBreaker.class);

    private final String name;
    private final CircuitBreakerConfig config;
    private final AtomicReference<State> state;
    private final AtomicInteger failureCount;
    private final AtomicInteger successCount;
    private final AtomicLong lastFailureTime;
    private final AtomicLong stateChangeTime;

    public SimpleCircuitBreaker(String name, CircuitBreakerConfig config) {
        this.name = name;
        this.config = config;
        this.state = new AtomicReference<>(State.CLOSED);
        this.failureCount = new AtomicInteger(0);
        this.successCount = new AtomicInteger(0);
        this.lastFailureTime = new AtomicLong(0);
        this.stateChangeTime = new AtomicLong(System.currentTimeMillis());
    }

    /**
     * 执行受保护的操作
     */
    public <T> T execute(Supplier<T> operation) throws Exception {
        return execute(operation, null);
    }

    /**
     * 执行受保护的操作（带降级）
     */
    public <T> T execute(Supplier<T> operation, Supplier<T> fallback) throws Exception {
        if (!allowRequest()) {
            logger.debug("熔断器[{}] 拒绝请求", name);
            if (fallback != null) {
                return fallback.get();
            } else {
                throw new CircuitBreakerOpenException("熔断器开启，请求被拒绝");
            }
        }

        long startTime = System.currentTimeMillis();
        try {
            T result = operation.get();
            onSuccess(System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            onFailure(System.currentTimeMillis() - startTime, e);
            throw e;
        }
    }

    /**
     * 检查是否允许请求通过
     */
    private boolean allowRequest() {
        State currentState = state.get();
        
        switch (currentState) {
            case CLOSED:
                return true;
                
            case OPEN:
                // 检查是否可以进入半开状态
                if (System.currentTimeMillis() - lastFailureTime.get() > config.getWaitDurationInOpenState()) {
                    if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                        logger.info("熔断器[{}] 从开启状态转为半开状态", name);
                        stateChangeTime.set(System.currentTimeMillis());
                        successCount.set(0);
                        return true;
                    }
                }
                return false;
                
            case HALF_OPEN:
                // 半开状态允许有限的请求通过
                return successCount.get() < config.getPermittedNumberOfCallsInHalfOpenState();
                
            default:
                return false;
        }
    }

    /**
     * 处理成功响应
     */
    private void onSuccess(long responseTime) {
        State currentState = state.get();
        
        if (currentState == State.HALF_OPEN) {
            int currentSuccessCount = successCount.incrementAndGet();
            
            // 检查是否可以关闭熔断器
            if (currentSuccessCount >= config.getMinimumNumberOfCallsInHalfOpenState()) {
                if (state.compareAndSet(State.HALF_OPEN, State.CLOSED)) {
                    logger.info("熔断器[{}] 从半开状态转为关闭状态", name);
                    reset();
                }
            }
        } else if (currentState == State.CLOSED) {
            // 重置失败计数
            failureCount.set(0);
        }
        
        logger.debug("熔断器[{}] 请求成功，响应时间: {}ms", name, responseTime);
    }

    /**
     * 处理失败响应
     */
    private void onFailure(long responseTime, Exception exception) {
        lastFailureTime.set(System.currentTimeMillis());
        int currentFailureCount = failureCount.incrementAndGet();
        
        State currentState = state.get();
        
        if (currentState == State.CLOSED) {
            // 检查是否需要开启熔断器
            if (currentFailureCount >= config.getFailureRateThreshold()) {
                if (state.compareAndSet(State.CLOSED, State.OPEN)) {
                    logger.warn("熔断器[{}] 从关闭状态转为开启状态，失败次数: {}", name, currentFailureCount);
                    stateChangeTime.set(System.currentTimeMillis());
                }
            }
        } else if (currentState == State.HALF_OPEN) {
            // 半开状态下的失败直接转为开启状态
            if (state.compareAndSet(State.HALF_OPEN, State.OPEN)) {
                logger.warn("熔断器[{}] 从半开状态转为开启状态", name);
                stateChangeTime.set(System.currentTimeMillis());
            }
        }
        
        logger.debug("熔断器[{}] 请求失败，响应时间: {}ms，异常: {}", 
            name, responseTime, exception.getMessage());
    }

    /**
     * 重置熔断器状态
     */
    private void reset() {
        failureCount.set(0);
        successCount.set(0);
        stateChangeTime.set(System.currentTimeMillis());
    }

    /**
     * 获取熔断器指标
     */
    public CircuitBreakerMetrics getMetrics() {
        return new CircuitBreakerMetrics(
            name,
            state.get(),
            failureCount.get(),
            successCount.get(),
            stateChangeTime.get()
        );
    }

    /**
     * 手动开启熔断器
     */
    public void open() {
        state.set(State.OPEN);
        stateChangeTime.set(System.currentTimeMillis());
        logger.info("熔断器[{}] 手动开启", name);
    }

    /**
     * 手动关闭熔断器
     */
    public void close() {
        state.set(State.CLOSED);
        reset();
        logger.info("熔断器[{}] 手动关闭", name);
    }

    /**
     * 熔断器状态枚举
     */
    public enum State {
        CLOSED,    // 关闭状态：正常处理请求
        OPEN,      // 开启状态：拒绝所有请求
        HALF_OPEN  // 半开状态：允许有限的请求测试服务是否恢复
    }

    /**
     * 熔断器配置
     */
    public static class CircuitBreakerConfig {
        private int failureRateThreshold = 5;                    // 失败次数阈值
        private int minimumNumberOfCallsInHalfOpenState = 3;     // 半开状态最小调用次数
        private int permittedNumberOfCallsInHalfOpenState = 5;   // 半开状态允许的调用次数
        private long waitDurationInOpenState = 60000;            // 开启状态等待时间（毫秒）

        // getters and setters
        public int getFailureRateThreshold() { return failureRateThreshold; }
        public void setFailureRateThreshold(int failureRateThreshold) { this.failureRateThreshold = failureRateThreshold; }
        public int getMinimumNumberOfCallsInHalfOpenState() { return minimumNumberOfCallsInHalfOpenState; }
        public void setMinimumNumberOfCallsInHalfOpenState(int minimumNumberOfCallsInHalfOpenState) { this.minimumNumberOfCallsInHalfOpenState = minimumNumberOfCallsInHalfOpenState; }
        public int getPermittedNumberOfCallsInHalfOpenState() { return permittedNumberOfCallsInHalfOpenState; }
        public void setPermittedNumberOfCallsInHalfOpenState(int permittedNumberOfCallsInHalfOpenState) { this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState; }
        public long getWaitDurationInOpenState() { return waitDurationInOpenState; }
        public void setWaitDurationInOpenState(long waitDurationInOpenState) { this.waitDurationInOpenState = waitDurationInOpenState; }

        public static CircuitBreakerConfig defaultConfig() {
            return new CircuitBreakerConfig();
        }
    }

    /**
     * 熔断器指标
     */
    public static class CircuitBreakerMetrics {
        private final String name;
        private final State state;
        private final int failureCount;
        private final int successCount;
        private final long stateChangeTime;

        public CircuitBreakerMetrics(String name, State state, int failureCount, 
                                   int successCount, long stateChangeTime) {
            this.name = name;
            this.state = state;
            this.failureCount = failureCount;
            this.successCount = successCount;
            this.stateChangeTime = stateChangeTime;
        }

        // getters
        public String getName() { return name; }
        public State getState() { return state; }
        public int getFailureCount() { return failureCount; }
        public int getSuccessCount() { return successCount; }
        public long getStateChangeTime() { return stateChangeTime; }
        public LocalDateTime getStateChangeDateTime() { 
            return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(stateChangeTime), 
                java.time.ZoneId.systemDefault()
            ); 
        }

        @Override
        public String toString() {
            return "CircuitBreakerMetrics{" +
                    "name='" + name + '\'' +
                    ", state=" + state +
                    ", failureCount=" + failureCount +
                    ", successCount=" + successCount +
                    ", stateChangeTime=" + getStateChangeDateTime() +
                    '}';
        }
    }

    /**
     * 熔断器开启异常
     */
    public static class CircuitBreakerOpenException extends RuntimeException {
        public CircuitBreakerOpenException(String message) {
            super(message);
        }
    }
}