package com.genlib.monitor.health;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

/**
 * 内存健康检查指示器
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class MemoryHealthIndicator implements HealthIndicator {

    private static final double DEFAULT_THRESHOLD = 0.85; // 85%
    private final double threshold;

    public MemoryHealthIndicator() {
        this(DEFAULT_THRESHOLD);
    }

    public MemoryHealthIndicator(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public HealthStatus health() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        
        long used = heapMemoryUsage.getUsed();
        long max = heapMemoryUsage.getMax();
        double usage = (double) used / max;
        
        HealthStatus.Builder builder = usage < threshold ? HealthStatus.up() : HealthStatus.down();
        
        return builder
                .withDetail("used", used)
                .withDetail("max", max)
                .withDetail("usage", String.format("%.2f%%", usage * 100))
                .withDetail("threshold", String.format("%.2f%%", threshold * 100))
                .build();
    }
}

/**
 * 磁盘空间健康检查指示器
 */
class DiskSpaceHealthIndicator implements HealthIndicator {

    private static final long DEFAULT_THRESHOLD = 10 * 1024 * 1024 * 1024L; // 10GB
    private final File path;
    private final long threshold;

    public DiskSpaceHealthIndicator() {
        this(new File("."), DEFAULT_THRESHOLD);
    }

    public DiskSpaceHealthIndicator(File path, long threshold) {
        this.path = path;
        this.threshold = threshold;
    }

    @Override
    public HealthStatus health() {
        long freeSpace = path.getFreeSpace();
        long totalSpace = path.getTotalSpace();
        
        HealthStatus.Builder builder = freeSpace > threshold ? HealthStatus.up() : HealthStatus.down();
        
        return builder
                .withDetail("free", freeSpace)
                .withDetail("total", totalSpace)
                .withDetail("threshold", threshold)
                .withDetail("path", path.getAbsolutePath())
                .build();
    }
}

/**
 * 死锁检查指示器
 */
class DeadlockHealthIndicator implements HealthIndicator {

    @Override
    public HealthStatus health() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadBean.findDeadlockedThreads();
        
        boolean hasDeadlock = deadlockedThreads != null && deadlockedThreads.length > 0;
        HealthStatus.Builder builder = hasDeadlock ? HealthStatus.down() : HealthStatus.up();
        
        return builder
                .withDetail("deadlockedThreads", hasDeadlock ? deadlockedThreads.length : 0)
                .build();
    }
}