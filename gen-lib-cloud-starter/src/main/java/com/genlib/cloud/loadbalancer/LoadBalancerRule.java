package com.genlib.cloud.loadbalancer;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List; /**
 * 负载均衡策略接口
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public interface LoadBalancerRule {
    ServiceInstance choose(List<ServiceInstance> instances);
}
