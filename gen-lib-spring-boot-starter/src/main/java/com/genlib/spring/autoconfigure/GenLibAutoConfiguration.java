package com.genlib.spring.autoconfigure;

import com.genlib.cache.caffeine.CaffeineCacheManager;
import com.genlib.cache.core.CacheConfig;
import com.genlib.cache.core.CacheManager;
import com.genlib.config.core.ConfigManager;
import com.genlib.config.impl.DefaultConfigManager;
import com.genlib.config.provider.PropertiesConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.time.Duration;

/**
 * Gen-Lib自动配置类
 * 根据配置属性自动配置相关组件
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(GenLibProperties.class)
@ConditionalOnProperty(prefix = "genlib", name = "enabled", havingValue = "true", matchIfMissing = true)
public class GenLibAutoConfiguration {

    // ================== 配置管理自动配置 ==================

    /**
     * 配置默认配置管理器
     *
     * @param properties Gen-Lib配置属性
     * @return 配置管理器
     */
    @Bean
    @ConditionalOnMissingBean
    @Primary
    public ConfigManager configManager(GenLibProperties properties) {
        DefaultConfigManager configManager = new DefaultConfigManager();
        
        // 根据配置添加Properties文件提供者
        GenLibProperties.ConfigProperties configProps = properties.getConfig();
        if (configProps.isEnabled()) {
            for (String location : configProps.getLocations()) {
                try {
                    String providerName = "properties-" + location.replace("/", "-").replace(".", "-");
                    PropertiesConfigProvider provider = new PropertiesConfigProvider(
                        providerName, 
                        new ClassPathResource(location).getFile().getAbsolutePath(),
                        50 // 高优先级
                    );
                    configManager.registerProvider(provider);
                } catch (Exception e) {
                    // 文件不存在时不抛异常，只是跳过
                    System.out.println("Config file not found: " + location + ", skipping...");
                }
            }
        }
        
        // 设置缓存
        configManager.setCacheEnabled(configProps.isCacheEnabled());
        
        return configManager;
    }

    // ================== 缓存管理自动配置 ==================

    /**
     * 配置Caffeine缓存管理器
     *
     * @param properties Gen-Lib配置属性
     * @return 缓存管理器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(com.github.benmanes.caffeine.cache.Caffeine.class)
    @ConditionalOnProperty(prefix = "genlib.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
    public CacheManager cacheManager(GenLibProperties properties) {
        GenLibProperties.CacheProperties cacheProps = properties.getCache();
        
        // 创建默认配置
        CacheConfig defaultConfig = CacheConfig.defaultConfig("default")
                .maxSize(cacheProps.getMaxSize())
                .expiration(Duration.ofSeconds(cacheProps.getDefaultExpiration()))
                .serializer(CacheConfig.SerializerType.valueOf(cacheProps.getSerializerType().toUpperCase()))
                .evictionPolicy(CacheConfig.EvictionPolicy.valueOf(cacheProps.getEvictionPolicy().toUpperCase()));
        
        if (cacheProps.isStatsEnabled()) {
            defaultConfig.enableStats();
        } else {
            defaultConfig.disableStats();
        }
        
        if (cacheProps.isCompressionEnabled()) {
            defaultConfig.enableCompression();
        }
        
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(defaultConfig);
        
        // 预创建配置的缓存
        for (String cacheName : cacheProps.getCacheNames()) {
            cacheManager.createCache(cacheName);
        }
        
        return cacheManager;
    }

    // ================== 工具类自动配置 ==================

    /**
     * 注册工具类配置（如果需要特殊配置）
     *
     * @param properties Gen-Lib配置属性
     * @return 工具类配置
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "genlib.utils", name = "enabled", havingValue = "true", matchIfMissing = true)
    public GenLibUtilsConfiguration utilsConfiguration(GenLibProperties properties) {
        return new GenLibUtilsConfiguration(properties.getUtils());
    }

    /**
     * 工具类配置类
     */
    public static class GenLibUtilsConfiguration {
        private final GenLibProperties.UtilsProperties properties;

        public GenLibUtilsConfiguration(GenLibProperties.UtilsProperties properties) {
            this.properties = properties;
        }

        public GenLibProperties.UtilsProperties getProperties() {
            return properties;
        }
    }
}