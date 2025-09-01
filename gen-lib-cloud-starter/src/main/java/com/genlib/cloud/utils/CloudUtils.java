package com.genlib.cloud.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

/**
 * 微服务工具类
 * 提供微服务环境下的常用工具方法
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class CloudUtils {

    private static final Logger logger = LoggerFactory.getLogger(CloudUtils.class);

    /**
     * 获取本机IP地址
     */
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                
                // 跳过回环接口和虚拟接口
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    
                    // 只获取IPv4地址且非回环地址
                    if (!address.isLoopbackAddress() && !address.isLinkLocalAddress() 
                        && address.getAddress().length == 4) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取本机IP地址失败", e);
        }
        
        return "127.0.0.1";
    }

    /**
     * 获取服务实例列表
     */
    public static List<ServiceInstance> getServiceInstances(DiscoveryClient discoveryClient, String serviceId) {
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            return instances != null ? instances : Collections.emptyList();
        } catch (Exception e) {
            logger.error("获取服务实例失败: {}", serviceId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取健康的服务实例
     */
    public static List<ServiceInstance> getHealthyServiceInstances(DiscoveryClient discoveryClient, String serviceId) {
        return getServiceInstances(discoveryClient, serviceId).stream()
                .filter(instance -> {
                    // 检查实例是否健康
                    Object healthy = instance.getMetadata().get("healthy");
                    return healthy == null || Boolean.parseBoolean(healthy.toString());
                })
                .toList();
    }

    /**
     * 随机选择一个服务实例
     */
    public static Optional<ServiceInstance> chooseRandomInstance(DiscoveryClient discoveryClient, String serviceId) {
        List<ServiceInstance> instances = getHealthyServiceInstances(discoveryClient, serviceId);
        if (instances.isEmpty()) {
            return Optional.empty();
        }
        
        int index = (int) (Math.random() * instances.size());
        return Optional.of(instances.get(index));
    }

    /**
     * 构建服务URL
     */
    public static String buildServiceUrl(ServiceInstance instance, String path) {
        if (instance == null) {
            return null;
        }
        
        String scheme = instance.isSecure() ? "https" : "http";
        String url = String.format("%s://%s:%d", scheme, instance.getHost(), instance.getPort());
        
        if (path != null && !path.isEmpty()) {
            if (!path.startsWith("/")) {
                url += "/";
            }
            url += path;
        }
        
        return url;
    }

    /**
     * 检查服务是否可用
     */
    public static boolean isServiceAvailable(DiscoveryClient discoveryClient, String serviceId) {
        List<ServiceInstance> instances = getHealthyServiceInstances(discoveryClient, serviceId);
        return !instances.isEmpty();
    }

    /**
     * 获取服务的基础URL
     */
    public static Optional<String> getServiceBaseUrl(DiscoveryClient discoveryClient, String serviceId) {
        return chooseRandomInstance(discoveryClient, serviceId)
                .map(instance -> buildServiceUrl(instance, null));
    }

    /**
     * 获取当前应用名称
     */
    public static String getCurrentApplicationName() {
        return System.getProperty("spring.application.name", "unknown");
    }

    /**
     * 获取当前环境
     */
    public static String getCurrentProfile() {
        String profiles = System.getProperty("spring.profiles.active");
        return profiles != null ? profiles.split(",")[0] : "default";
    }

    /**
     * 生成服务实例ID
     */
    public static String generateInstanceId(String serviceName, String host, int port) {
        return String.format("%s:%s:%d", serviceName, host, port);
    }

    /**
     * 解析服务实例ID
     */
    public static ServiceInstanceInfo parseInstanceId(String instanceId) {
        try {
            String[] parts = instanceId.split(":");
            if (parts.length >= 3) {
                String serviceName = parts[0];
                String host = parts[1];
                int port = Integer.parseInt(parts[2]);
                return new ServiceInstanceInfo(serviceName, host, port);
            }
        } catch (Exception e) {
            logger.error("解析服务实例ID失败: {}", instanceId, e);
        }
        return null;
    }

    /**
     * 服务实例信息
     */
    public static class ServiceInstanceInfo {
        private final String serviceName;
        private final String host;
        private final int port;

        public ServiceInstanceInfo(String serviceName, String host, int port) {
            this.serviceName = serviceName;
            this.host = host;
            this.port = port;
        }

        public String getServiceName() { return serviceName; }
        public String getHost() { return host; }
        public int getPort() { return port; }

        @Override
        public String toString() {
            return "ServiceInstanceInfo{" +
                    "serviceName='" + serviceName + '\'' +
                    ", host='" + host + '\'' +
                    ", port=" + port +
                    '}';
        }
    }
}