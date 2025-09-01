package com.genlib.cloud.loadbalancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 随机负载均衡策略
 */
class RandomRule implements LoadBalancerRule {
    
    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        
        int index = ThreadLocalRandom.current().nextInt(instances.size());
        return instances.get(index);
    }
}

/**
 * 轮询负载均衡策略
 */
class RoundRobinRule implements LoadBalancerRule {
    
    private final AtomicInteger counter = new AtomicInteger(0);
    
    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        
        int index = counter.getAndIncrement() % instances.size();
        return instances.get(index);
    }
}

/**
 * 加权轮询负载均衡策略
 */
class WeightedRoundRobinRule implements LoadBalancerRule {
    
    private static final Logger logger = LoggerFactory.getLogger(WeightedRoundRobinRule.class);
    private final AtomicInteger counter = new AtomicInteger(0);
    
    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        
        // 计算总权重
        int totalWeight = 0;
        for (ServiceInstance instance : instances) {
            int weight = getWeight(instance);
            totalWeight += weight;
        }
        
        if (totalWeight <= 0) {
            // 如果没有权重信息，使用简单轮询
            int index = counter.getAndIncrement() % instances.size();
            return instances.get(index);
        }
        
        // 根据权重选择实例
        int targetWeight = counter.getAndIncrement() % totalWeight;
        int currentWeight = 0;
        
        for (ServiceInstance instance : instances) {
            currentWeight += getWeight(instance);
            if (currentWeight > targetWeight) {
                return instance;
            }
        }
        
        // 理论上不应该到达这里
        return instances.get(0);
    }
    
    private int getWeight(ServiceInstance instance) {
        try {
            String weightStr = instance.getMetadata().get("weight");
            if (weightStr != null) {
                return Integer.parseInt(weightStr);
            }
        } catch (NumberFormatException e) {
            logger.warn("解析实例权重失败: {}", instance.getInstanceId());
        }
        return 1; // 默认权重
    }
}

/**
 * 最少连接负载均衡策略
 */
class LeastConnectionsRule implements LoadBalancerRule {
    
    private static final Logger logger = LoggerFactory.getLogger(LeastConnectionsRule.class);
    
    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        
        ServiceInstance chosen = null;
        int minConnections = Integer.MAX_VALUE;
        
        for (ServiceInstance instance : instances) {
            int connections = getActiveConnections(instance);
            if (connections < minConnections) {
                minConnections = connections;
                chosen = instance;
            }
        }
        
        return chosen != null ? chosen : instances.get(0);
    }
    
    private int getActiveConnections(ServiceInstance instance) {
        try {
            String connectionsStr = instance.getMetadata().get("activeConnections");
            if (connectionsStr != null) {
                return Integer.parseInt(connectionsStr);
            }
        } catch (NumberFormatException e) {
            logger.warn("解析实例连接数失败: {}", instance.getInstanceId());
        }
        return 0; // 默认连接数
    }
}

/**
 * 响应时间加权负载均衡策略
 */
class ResponseTimeWeightedRule implements LoadBalancerRule {
    
    private static final Logger logger = LoggerFactory.getLogger(ResponseTimeWeightedRule.class);
    
    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        
        // 计算反向权重（响应时间越短，权重越高）
        double totalWeight = 0.0;
        double[] weights = new double[instances.size()];
        
        for (int i = 0; i < instances.size(); i++) {
            ServiceInstance instance = instances.get(i);
            long responseTime = getAverageResponseTime(instance);
            
            // 避免除零，设置最小响应时间
            responseTime = Math.max(responseTime, 1);
            
            // 反向权重：响应时间越短，权重越高
            double weight = 1.0 / responseTime;
            weights[i] = weight;
            totalWeight += weight;
        }
        
        if (totalWeight <= 0) {
            // 如果没有响应时间信息，使用随机选择
            int index = ThreadLocalRandom.current().nextInt(instances.size());
            return instances.get(index);
        }
        
        // 根据权重随机选择
        double random = ThreadLocalRandom.current().nextDouble() * totalWeight;
        double currentWeight = 0.0;
        
        for (int i = 0; i < instances.size(); i++) {
            currentWeight += weights[i];
            if (currentWeight >= random) {
                return instances.get(i);
            }
        }
        
        // 理论上不应该到达这里
        return instances.get(instances.size() - 1);
    }
    
    private long getAverageResponseTime(ServiceInstance instance) {
        try {
            String responseTimeStr = instance.getMetadata().get("avgResponseTime");
            if (responseTimeStr != null) {
                return Long.parseLong(responseTimeStr);
            }
        } catch (NumberFormatException e) {
            logger.warn("解析实例响应时间失败: {}", instance.getInstanceId());
        }
        return 100; // 默认响应时间（毫秒）
    }
}

/**
 * 负载均衡器
 */
public class LoadBalancer {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancer.class);
    
    private LoadBalancerRule rule;
    
    public LoadBalancer() {
        this.rule = new RoundRobinRule(); // 默认使用轮询策略
    }
    
    public LoadBalancer(LoadBalancerRule rule) {
        this.rule = rule;
    }
    
    /**
     * 选择服务实例
     */
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            logger.warn("没有可用的服务实例");
            return null;
        }
        
        try {
            ServiceInstance chosen = rule.choose(instances);
            if (chosen != null) {
                logger.debug("选择服务实例: {} ({}:{})", 
                    chosen.getInstanceId(), chosen.getHost(), chosen.getPort());
            }
            return chosen;
        } catch (Exception e) {
            logger.error("负载均衡选择实例失败", e);
            // 降级策略：返回第一个实例
            return instances.get(0);
        }
    }
    
    /**
     * 设置负载均衡规则
     */
    public void setRule(LoadBalancerRule rule) {
        this.rule = rule;
        logger.info("设置负载均衡规则: {}", rule.getClass().getSimpleName());
    }
    
    /**
     * 获取当前负载均衡规则
     */
    public LoadBalancerRule getRule() {
        return rule;
    }
    
    // 静态工厂方法
    public static LoadBalancer random() {
        return new LoadBalancer(new RandomRule());
    }
    
    public static LoadBalancer roundRobin() {
        return new LoadBalancer(new RoundRobinRule());
    }
    
    public static LoadBalancer weightedRoundRobin() {
        return new LoadBalancer(new WeightedRoundRobinRule());
    }
    
    public static LoadBalancer leastConnections() {
        return new LoadBalancer(new LeastConnectionsRule());
    }
    
    public static LoadBalancer responseTimeWeighted() {
        return new LoadBalancer(new ResponseTimeWeightedRule());
    }
}