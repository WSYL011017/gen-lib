package com.genlib.cloud.discovery;

import com.genlib.cloud.utils.CloudUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务发现增强器
 * 提供服务发现的增强功能，如服务缓存、健康检查等
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class ServiceDiscoveryEnhancer {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryEnhancer.class);

    private final DiscoveryClient discoveryClient;
    private final Map<String, ServiceCacheEntry> serviceCache;
    private final ScheduledExecutorService scheduler;
    private final long cacheTimeout;
    private final boolean enableCache;

    public ServiceDiscoveryEnhancer(DiscoveryClient discoveryClient) {
        this(discoveryClient, true, 30);
    }

    public ServiceDiscoveryEnhancer(DiscoveryClient discoveryClient, boolean enableCache, long cacheTimeoutSeconds) {
        this.discoveryClient = discoveryClient;
        this.enableCache = enableCache;
        this.cacheTimeout = cacheTimeoutSeconds * 1000; // 转换为毫秒
        this.serviceCache = new ConcurrentHashMap<>();
        
        if (enableCache) {
            this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "ServiceDiscovery-Cache-Cleaner");
                thread.setDaemon(true);
                return thread;
            });
            
            // 定期清理过期缓存
            scheduler.scheduleAtFixedRate(this::cleanExpiredCache, cacheTimeoutSeconds, cacheTimeoutSeconds, TimeUnit.SECONDS);
        } else {
            this.scheduler = null;
        }
    }

    /**
     * 获取服务实例（带缓存）
     */
    public List<ServiceInstance> getInstances(String serviceId) {
        if (!enableCache) {
            return CloudUtils.getServiceInstances(discoveryClient, serviceId);
        }

        ServiceCacheEntry cacheEntry = serviceCache.get(serviceId);
        long currentTime = System.currentTimeMillis();

        // 检查缓存是否有效
        if (cacheEntry != null && (currentTime - cacheEntry.getTimestamp()) < cacheTimeout) {
            logger.debug("从缓存获取服务实例: {}", serviceId);
            return new ArrayList<>(cacheEntry.getInstances());
        }

        // 缓存无效，重新获取
        List<ServiceInstance> instances = CloudUtils.getServiceInstances(discoveryClient, serviceId);
        serviceCache.put(serviceId, new ServiceCacheEntry(instances, currentTime));
        
        logger.debug("刷新服务实例缓存: {} -> {} 个实例", serviceId, instances.size());
        return new ArrayList<>(instances);
    }

    /**
     * 获取健康的服务实例
     */
    public List<ServiceInstance> getHealthyInstances(String serviceId) {
        return getInstances(serviceId).stream()
                .filter(this::isInstanceHealthy)
                .toList();
    }

    /**
     * 强制刷新服务缓存
     */
    public void refreshCache(String serviceId) {
        if (!enableCache) {
            return;
        }
        
        List<ServiceInstance> instances = CloudUtils.getServiceInstances(discoveryClient, serviceId);
        serviceCache.put(serviceId, new ServiceCacheEntry(instances, System.currentTimeMillis()));
        logger.info("强制刷新服务缓存: {}", serviceId);
    }

    /**
     * 清理所有缓存
     */
    public void clearCache() {
        if (enableCache) {
            serviceCache.clear();
            logger.info("清理所有服务缓存");
        }
    }

    /**
     * 获取所有服务名称
     */
    public List<String> getServices() {
        try {
            return discoveryClient.getServices();
        } catch (Exception e) {
            logger.error("获取服务列表失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取服务统计信息
     */
    public ServiceStats getServiceStats(String serviceId) {
        List<ServiceInstance> allInstances = getInstances(serviceId);
        List<ServiceInstance> healthyInstances = getHealthyInstances(serviceId);
        
        return new ServiceStats(
            serviceId,
            allInstances.size(),
            healthyInstances.size(),
            allInstances.size() - healthyInstances.size()
        );
    }

    /**
     * 获取所有服务的统计信息
     */
    public List<ServiceStats> getAllServiceStats() {
        List<ServiceStats> statsList = new ArrayList<>();
        List<String> services = getServices();
        
        for (String serviceId : services) {
            try {
                ServiceStats stats = getServiceStats(serviceId);
                statsList.add(stats);
            } catch (Exception e) {
                logger.error("获取服务统计信息失败: {}", serviceId, e);
            }
        }
        
        return statsList;
    }

    /**
     * 检查实例是否健康
     */
    private boolean isInstanceHealthy(ServiceInstance instance) {
        // 检查元数据中的健康状态
        Map<String, String> metadata = instance.getMetadata();
        if (metadata != null) {
            String healthy = metadata.get("healthy");
            if (healthy != null) {
                return Boolean.parseBoolean(healthy);
            }
            
            String status = metadata.get("status");
            if (status != null) {
                return "UP".equalsIgnoreCase(status);
            }
        }
        
        // 默认认为是健康的
        return true;
    }

    /**
     * 清理过期缓存
     */
    private void cleanExpiredCache() {
        long currentTime = System.currentTimeMillis();
        
        serviceCache.entrySet().removeIf(entry -> {
            boolean expired = (currentTime - entry.getValue().getTimestamp()) > cacheTimeout;
            if (expired) {
                logger.debug("清理过期服务缓存: {}", entry.getKey());
            }
            return expired;
        });
    }

    /**
     * 关闭服务发现增强器
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (enableCache) {
            clearCache();
        }
        
        logger.info("服务发现增强器已关闭");
    }

    /**
     * 服务缓存条目
     */
    private static class ServiceCacheEntry {
        private final List<ServiceInstance> instances;
        private final long timestamp;

        public ServiceCacheEntry(List<ServiceInstance> instances, long timestamp) {
            this.instances = new ArrayList<>(instances);
            this.timestamp = timestamp;
        }

        public List<ServiceInstance> getInstances() {
            return instances;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * 服务统计信息
     */
    public static class ServiceStats {
        private final String serviceId;
        private final int totalInstances;
        private final int healthyInstances;
        private final int unhealthyInstances;

        public ServiceStats(String serviceId, int totalInstances, int healthyInstances, int unhealthyInstances) {
            this.serviceId = serviceId;
            this.totalInstances = totalInstances;
            this.healthyInstances = healthyInstances;
            this.unhealthyInstances = unhealthyInstances;
        }

        // getters
        public String getServiceId() { return serviceId; }
        public int getTotalInstances() { return totalInstances; }
        public int getHealthyInstances() { return healthyInstances; }
        public int getUnhealthyInstances() { return unhealthyInstances; }
        
        public double getHealthyRate() {
            return totalInstances > 0 ? (double) healthyInstances / totalInstances : 0.0;
        }

        @Override
        public String toString() {
            return "ServiceStats{" +
                    "serviceId='" + serviceId + '\'' +
                    ", totalInstances=" + totalInstances +
                    ", healthyInstances=" + healthyInstances +
                    ", unhealthyInstances=" + unhealthyInstances +
                    ", healthyRate=" + String.format("%.2f%%", getHealthyRate() * 100) +
                    '}';
        }
    }
}