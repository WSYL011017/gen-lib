package com.genlib.monitor.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 健康检查管理器
 * 管理和执行各种健康检查
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class HealthCheckManager {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckManager.class);

    private final Map<String, HealthIndicator> healthIndicators;
    private final ExecutorService executorService;
    private final long defaultTimeout;

    public HealthCheckManager() {
        this(30, TimeUnit.SECONDS);
    }

    public HealthCheckManager(long timeout, TimeUnit timeUnit) {
        this.healthIndicators = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r, "HealthCheck-");
            thread.setDaemon(true);
            return thread;
        });
        this.defaultTimeout = timeUnit.toMillis(timeout);
        
        // 注册默认健康检查
        registerDefaultHealthIndicators();
    }

    /**
     * 注册健康检查指示器
     */
    public void register(String name, HealthIndicator indicator) {
        healthIndicators.put(name, indicator);
        logger.debug("注册健康检查指示器: {}", name);
    }

    /**
     * 取消注册健康检查指示器
     */
    public void unregister(String name) {
        healthIndicators.remove(name);
        logger.debug("取消注册健康检查指示器: {}", name);
    }

    /**
     * 执行所有健康检查
     */
    public HealthCheckResult checkHealth() {
        return checkHealth(defaultTimeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 执行所有健康检查（自定义超时时间）
     */
    public HealthCheckResult checkHealth(long timeout, TimeUnit timeUnit) {
        long startTime = System.currentTimeMillis();
        Map<String, HealthStatus> results = new HashMap<>();
        
        if (healthIndicators.isEmpty()) {
            logger.warn("没有注册任何健康检查指示器");
            return new HealthCheckResult(HealthStatus.Status.UP, results, 0);
        }

        // 并行执行所有健康检查
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (Map.Entry<String, HealthIndicator> entry : healthIndicators.entrySet()) {
            String name = entry.getKey();
            HealthIndicator indicator = entry.getValue();
            
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    HealthStatus status = indicator.health();
                    results.put(name, status);
                    logger.debug("健康检查[{}] 完成: {}", name, status.getStatus());
                } catch (Exception e) {
                    logger.error("健康检查[{}] 执行失败", name, e);
                    results.put(name, HealthStatus.down()
                        .withDetail("error", e.getMessage())
                        .build());
                }
            }, executorService);
            
            futures.add(future);
        }

        // 等待所有检查完成
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(timeout, timeUnit);
        } catch (Exception e) {
            logger.error("健康检查超时或失败", e);
            
            // 对于未完成的检查，标记为超时
            for (Map.Entry<String, HealthIndicator> entry : healthIndicators.entrySet()) {
                String name = entry.getKey();
                if (!results.containsKey(name)) {
                    results.put(name, HealthStatus.down()
                        .withDetail("error", "健康检查超时")
                        .build());
                }
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        
        // 计算整体健康状态
        HealthStatus.Status overallStatus = calculateOverallStatus(results);
        
        return new HealthCheckResult(overallStatus, results, duration);
    }

    /**
     * 执行单个健康检查
     */
    public HealthStatus checkHealth(String name) {
        HealthIndicator indicator = healthIndicators.get(name);
        if (indicator == null) {
            logger.warn("健康检查指示器不存在: {}", name);
            return HealthStatus.unknown()
                .withDetail("error", "健康检查指示器不存在")
                .build();
        }

        try {
            return indicator.health();
        } catch (Exception e) {
            logger.error("健康检查[{}] 执行失败", name, e);
            return HealthStatus.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }

    /**
     * 获取所有注册的健康检查指示器名称
     */
    public java.util.Set<String> getHealthIndicatorNames() {
        return healthIndicators.keySet();
    }

    /**
     * 关闭健康检查管理器
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("健康检查管理器已关闭");
    }

    /**
     * 注册默认健康检查指示器
     */
    private void registerDefaultHealthIndicators() {
        // JVM内存健康检查
        register("memory", new MemoryHealthIndicator());
        
        // 磁盘空间健康检查
        register("diskSpace", new DiskSpaceHealthIndicator());
        
        // 线程死锁检查
        register("deadlock", new DeadlockHealthIndicator());
    }

    /**
     * 计算整体健康状态
     */
    private HealthStatus.Status calculateOverallStatus(Map<String, HealthStatus> results) {
        if (results.isEmpty()) {
            return HealthStatus.Status.UNKNOWN;
        }

        boolean hasDown = false;
        boolean hasUnknown = false;

        for (HealthStatus status : results.values()) {
            switch (status.getStatus()) {
                case DOWN:
                    hasDown = true;
                    break;
                case UNKNOWN:
                    hasUnknown = true;
                    break;
                case UP:
                    // 继续检查其他状态
                    break;
            }
        }

        if (hasDown) {
            return HealthStatus.Status.DOWN;
        } else if (hasUnknown) {
            return HealthStatus.Status.UNKNOWN;
        } else {
            return HealthStatus.Status.UP;
        }
    }
}