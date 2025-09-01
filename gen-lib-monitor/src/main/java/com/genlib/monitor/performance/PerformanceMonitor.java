package com.genlib.monitor.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 性能监控器
 * 收集和监控系统性能指标
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class PerformanceMonitor {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitor.class);

    private final ScheduledExecutorService scheduler;
    private final List<PerformanceListener> listeners;
    private final boolean autoStart;
    private final long collectInterval;

    // JMX Beans
    private final MemoryMXBean memoryMXBean;
    private final RuntimeMXBean runtimeMXBean;
    private final ThreadMXBean threadMXBean;
    private final OperatingSystemMXBean operatingSystemMXBean;
    private final List<GarbageCollectorMXBean> garbageCollectorMXBeans;

    private volatile boolean running = false;

    public PerformanceMonitor() {
        this(true, 30);
    }

    public PerformanceMonitor(boolean autoStart, long collectInterval) {
        this.autoStart = autoStart;
        this.collectInterval = collectInterval;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "PerformanceMonitor");
            thread.setDaemon(true);
            return thread;
        });
        this.listeners = new ArrayList<>();

        // 初始化JMX Beans
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        this.garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();

        if (autoStart) {
            start();
        }
    }

    /**
     * 启动性能监控
     */
    public synchronized void start() {
        if (running) {
            logger.warn("性能监控器已经在运行中");
            return;
        }

        running = true;
        scheduler.scheduleAtFixedRate(this::collectMetrics, 0, collectInterval, TimeUnit.SECONDS);
        logger.info("性能监控器已启动，收集间隔: {} 秒", collectInterval);
    }

    /**
     * 停止性能监控
     */
    public synchronized void stop() {
        if (!running) {
            logger.warn("性能监控器未在运行");
            return;
        }

        running = false;
        scheduler.shutdown();
        logger.info("性能监控器已停止");
    }

    /**
     * 添加性能监听器
     */
    public void addListener(PerformanceListener listener) {
        listeners.add(listener);
        logger.debug("添加性能监听器: {}", listener.getClass().getSimpleName());
    }

    /**
     * 移除性能监听器
     */
    public void removeListener(PerformanceListener listener) {
        listeners.remove(listener);
        logger.debug("移除性能监听器: {}", listener.getClass().getSimpleName());
    }

    /**
     * 收集性能指标
     */
    public PerformanceMetrics collectMetrics() {
        try {
            long startTime = System.currentTimeMillis();

            PerformanceMetrics metrics = new PerformanceMetrics();
            
            // 收集内存指标
            collectMemoryMetrics(metrics);
            
            // 收集线程指标
            collectThreadMetrics(metrics);
            
            // 收集运行时指标
            collectRuntimeMetrics(metrics);
            
            // 收集操作系统指标
            collectOSMetrics(metrics);
            
            // 收集垃圾回收指标
            collectGCMetrics(metrics);

            long endTime = System.currentTimeMillis();
            metrics.setCollectTime(endTime - startTime);
            metrics.setTimestamp(endTime);

            // 通知监听器
            notifyListeners(metrics);

            return metrics;

        } catch (Exception e) {
            logger.error("收集性能指标失败", e);
            return null;
        }
    }

    /**
     * 收集内存指标
     */
    private void collectMemoryMetrics(PerformanceMetrics metrics) {
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

        Map<String, Object> memoryMetrics = new HashMap<>();
        
        // 堆内存
        memoryMetrics.put("heap.used", heapMemoryUsage.getUsed());
        memoryMetrics.put("heap.committed", heapMemoryUsage.getCommitted());
        memoryMetrics.put("heap.max", heapMemoryUsage.getMax());
        memoryMetrics.put("heap.init", heapMemoryUsage.getInit());
        memoryMetrics.put("heap.usage", (double) heapMemoryUsage.getUsed() / heapMemoryUsage.getMax());

        // 非堆内存
        memoryMetrics.put("nonheap.used", nonHeapMemoryUsage.getUsed());
        memoryMetrics.put("nonheap.committed", nonHeapMemoryUsage.getCommitted());
        memoryMetrics.put("nonheap.max", nonHeapMemoryUsage.getMax());
        memoryMetrics.put("nonheap.init", nonHeapMemoryUsage.getInit());

        // 等待GC的对象数量
        memoryMetrics.put("finalization.pending", memoryMXBean.getObjectPendingFinalizationCount());

        metrics.addCategory("memory", memoryMetrics);
    }

    /**
     * 收集线程指标
     */
    private void collectThreadMetrics(PerformanceMetrics metrics) {
        Map<String, Object> threadMetrics = new HashMap<>();
        
        threadMetrics.put("count", threadMXBean.getThreadCount());
        threadMetrics.put("peak", threadMXBean.getPeakThreadCount());
        threadMetrics.put("daemon", threadMXBean.getDaemonThreadCount());
        threadMetrics.put("started", threadMXBean.getTotalStartedThreadCount());
        
        // 死锁检测
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        threadMetrics.put("deadlocked", deadlockedThreads != null ? deadlockedThreads.length : 0);

        metrics.addCategory("thread", threadMetrics);
    }

    /**
     * 收集运行时指标
     */
    private void collectRuntimeMetrics(PerformanceMetrics metrics) {
        Map<String, Object> runtimeMetrics = new HashMap<>();
        
        runtimeMetrics.put("uptime", runtimeMXBean.getUptime());
        runtimeMetrics.put("startTime", runtimeMXBean.getStartTime());
        runtimeMetrics.put("pid", runtimeMXBean.getName().split("@")[0]);

        metrics.addCategory("runtime", runtimeMetrics);
    }

    /**
     * 收集操作系统指标
     */
    private void collectOSMetrics(PerformanceMetrics metrics) {
        Map<String, Object> osMetrics = new HashMap<>();
        
        osMetrics.put("processors", operatingSystemMXBean.getAvailableProcessors());
        osMetrics.put("loadAverage", operatingSystemMXBean.getSystemLoadAverage());
        osMetrics.put("arch", operatingSystemMXBean.getArch());
        osMetrics.put("name", operatingSystemMXBean.getName());
        osMetrics.put("version", operatingSystemMXBean.getVersion());

        // 如果是com.sun.management.OperatingSystemMXBean，可以获取更多信息
        if (operatingSystemMXBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOSBean = 
                (com.sun.management.OperatingSystemMXBean) operatingSystemMXBean;
            
            osMetrics.put("processCpuLoad", sunOSBean.getProcessCpuLoad());
            osMetrics.put("systemCpuLoad", sunOSBean.getSystemCpuLoad());
            osMetrics.put("processCpuTime", sunOSBean.getProcessCpuTime());
            osMetrics.put("committedVirtualMemorySize", sunOSBean.getCommittedVirtualMemorySize());
            osMetrics.put("totalPhysicalMemorySize", sunOSBean.getTotalPhysicalMemorySize());
            osMetrics.put("freePhysicalMemorySize", sunOSBean.getFreePhysicalMemorySize());
            osMetrics.put("totalSwapSpaceSize", sunOSBean.getTotalSwapSpaceSize());
            osMetrics.put("freeSwapSpaceSize", sunOSBean.getFreeSwapSpaceSize());
        }

        metrics.addCategory("os", osMetrics);
    }

    /**
     * 收集垃圾回收指标
     */
    private void collectGCMetrics(PerformanceMetrics metrics) {
        Map<String, Object> gcMetrics = new HashMap<>();
        
        long totalCollections = 0;
        long totalCollectionTime = 0;
        
        for (GarbageCollectorMXBean gcBean : garbageCollectorMXBeans) {
            String gcName = gcBean.getName();
            long collections = gcBean.getCollectionCount();
            long collectionTime = gcBean.getCollectionTime();
            
            gcMetrics.put(gcName + ".collections", collections);
            gcMetrics.put(gcName + ".time", collectionTime);
            
            totalCollections += collections;
            totalCollectionTime += collectionTime;
        }
        
        gcMetrics.put("total.collections", totalCollections);
        gcMetrics.put("total.time", totalCollectionTime);

        metrics.addCategory("gc", gcMetrics);
    }

    /**
     * 通知监听器
     */
    private void notifyListeners(PerformanceMetrics metrics) {
        for (PerformanceListener listener : listeners) {
            try {
                listener.onMetricsCollected(metrics);
            } catch (Exception e) {
                logger.error("通知性能监听器失败: {}", listener.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * 获取当前性能快照
     */
    public PerformanceMetrics getCurrentMetrics() {
        return collectMetrics();
    }

    /**
     * 检查是否运行中
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * 获取收集间隔
     */
    public long getCollectInterval() {
        return collectInterval;
    }
}