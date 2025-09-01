package com.genlib.cache.core;

import java.time.Duration;
import java.util.Objects;

/**
 * 缓存配置类
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class CacheConfig {

    /** 默认过期时间（1小时） */
    public static final Duration DEFAULT_EXPIRATION = Duration.ofHours(1);

    /** 默认最大缓存数量 */
    public static final long DEFAULT_MAX_SIZE = 10000L;

    /** 缓存名称 */
    private String name;

    /** 过期时间 */
    private Duration expiration = DEFAULT_EXPIRATION;

    /** 最大缓存数量 */
    private Long maxSize = DEFAULT_MAX_SIZE;

    /** 是否启用统计 */
    private Boolean statsEnabled = true;

    /** 序列化器类型 */
    private SerializerType serializerType = SerializerType.JSON;

    /** 是否启用压缩 */
    private Boolean compressionEnabled = false;

    /** 驱逐策略 */
    private EvictionPolicy evictionPolicy = EvictionPolicy.LRU;

    /** 刷新策略 */
    private RefreshPolicy refreshPolicy = RefreshPolicy.MANUAL;

    /** 预热策略 */
    private WarmupPolicy warmupPolicy = WarmupPolicy.NONE;

    /**
     * 无参构造方法
     */
    public CacheConfig() {
    }

    /**
     * 构造方法
     *
     * @param name 缓存名称
     */
    public CacheConfig(String name) {
        this.name = name;
    }

    /**
     * 创建默认配置
     *
     * @param name 缓存名称
     * @return 默认配置
     */
    public static CacheConfig defaultConfig(String name) {
        return new CacheConfig(name);
    }

    /**
     * 创建永不过期的配置
     *
     * @param name 缓存名称
     * @return 永不过期配置
     */
    public static CacheConfig neverExpire(String name) {
        CacheConfig config = new CacheConfig(name);
        config.setExpiration(null);
        return config;
    }

    /**
     * 创建快速过期配置（5分钟）
     *
     * @param name 缓存名称
     * @return 快速过期配置
     */
    public static CacheConfig fastExpire(String name) {
        CacheConfig config = new CacheConfig(name);
        config.setExpiration(Duration.ofMinutes(5));
        return config;
    }

    /**
     * 创建慢速过期配置（24小时）
     *
     * @param name 缓存名称
     * @return 慢速过期配置
     */
    public static CacheConfig slowExpire(String name) {
        CacheConfig config = new CacheConfig(name);
        config.setExpiration(Duration.ofHours(24));
        return config;
    }

    // ================== 流式配置方法 ==================

    /**
     * 设置过期时间
     *
     * @param expiration 过期时间
     * @return 当前配置对象
     */
    public CacheConfig expiration(Duration expiration) {
        this.expiration = expiration;
        return this;
    }

    /**
     * 设置最大缓存数量
     *
     * @param maxSize 最大数量
     * @return 当前配置对象
     */
    public CacheConfig maxSize(Long maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    /**
     * 启用统计
     *
     * @return 当前配置对象
     */
    public CacheConfig enableStats() {
        this.statsEnabled = true;
        return this;
    }

    /**
     * 禁用统计
     *
     * @return 当前配置对象
     */
    public CacheConfig disableStats() {
        this.statsEnabled = false;
        return this;
    }

    /**
     * 设置序列化器类型
     *
     * @param serializerType 序列化器类型
     * @return 当前配置对象
     */
    public CacheConfig serializer(SerializerType serializerType) {
        this.serializerType = serializerType;
        return this;
    }

    /**
     * 启用压缩
     *
     * @return 当前配置对象
     */
    public CacheConfig enableCompression() {
        this.compressionEnabled = true;
        return this;
    }

    /**
     * 设置驱逐策略
     *
     * @param evictionPolicy 驱逐策略
     * @return 当前配置对象
     */
    public CacheConfig evictionPolicy(EvictionPolicy evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
        return this;
    }

    /**
     * 设置刷新策略
     *
     * @param refreshPolicy 刷新策略
     * @return 当前配置对象
     */
    public CacheConfig refreshPolicy(RefreshPolicy refreshPolicy) {
        this.refreshPolicy = refreshPolicy;
        return this;
    }

    /**
     * 设置预热策略
     *
     * @param warmupPolicy 预热策略
     * @return 当前配置对象
     */
    public CacheConfig warmupPolicy(WarmupPolicy warmupPolicy) {
        this.warmupPolicy = warmupPolicy;
        return this;
    }

    // ================== 枚举定义 ==================

    /**
     * 序列化器类型
     */
    public enum SerializerType {
        /** JSON序列化 */
        JSON,
        /** Java原生序列化 */
        JAVA,
        /** Kryo序列化 */
        KRYO,
        /** Protobuf序列化 */
        PROTOBUF
    }

    /**
     * 驱逐策略
     */
    public enum EvictionPolicy {
        /** 最近最少使用 */
        LRU,
        /** 最近最少访问 */
        LFU,
        /** 先进先出 */
        FIFO,
        /** 随机驱逐 */
        RANDOM
    }

    /**
     * 刷新策略
     */
    public enum RefreshPolicy {
        /** 手动刷新 */
        MANUAL,
        /** 定时刷新 */
        SCHEDULED,
        /** 写后刷新 */
        WRITE_THROUGH,
        /** 写后异步刷新 */
        WRITE_BEHIND
    }

    /**
     * 预热策略
     */
    public enum WarmupPolicy {
        /** 无预热 */
        NONE,
        /** 启动时预热 */
        STARTUP,
        /** 延迟预热 */
        LAZY,
        /** 定时预热 */
        SCHEDULED
    }

    // ================== Getter 和 Setter ==================

    public String getName() {
        return name;
    }

    public CacheConfig setName(String name) {
        this.name = name;
        return this;
    }

    public Duration getExpiration() {
        return expiration;
    }

    public CacheConfig setExpiration(Duration expiration) {
        this.expiration = expiration;
        return this;
    }

    public Long getMaxSize() {
        return maxSize;
    }

    public CacheConfig setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public Boolean getStatsEnabled() {
        return statsEnabled;
    }

    public CacheConfig setStatsEnabled(Boolean statsEnabled) {
        this.statsEnabled = statsEnabled;
        return this;
    }

    public SerializerType getSerializerType() {
        return serializerType;
    }

    public CacheConfig setSerializerType(SerializerType serializerType) {
        this.serializerType = serializerType;
        return this;
    }

    public Boolean getCompressionEnabled() {
        return compressionEnabled;
    }

    public CacheConfig setCompressionEnabled(Boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
        return this;
    }

    public EvictionPolicy getEvictionPolicy() {
        return evictionPolicy;
    }

    public CacheConfig setEvictionPolicy(EvictionPolicy evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
        return this;
    }

    public RefreshPolicy getRefreshPolicy() {
        return refreshPolicy;
    }

    public CacheConfig setRefreshPolicy(RefreshPolicy refreshPolicy) {
        this.refreshPolicy = refreshPolicy;
        return this;
    }

    public WarmupPolicy getWarmupPolicy() {
        return warmupPolicy;
    }

    public CacheConfig setWarmupPolicy(WarmupPolicy warmupPolicy) {
        this.warmupPolicy = warmupPolicy;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheConfig that = (CacheConfig) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(expiration, that.expiration) &&
               Objects.equals(maxSize, that.maxSize) &&
               Objects.equals(statsEnabled, that.statsEnabled) &&
               serializerType == that.serializerType &&
               Objects.equals(compressionEnabled, that.compressionEnabled) &&
               evictionPolicy == that.evictionPolicy &&
               refreshPolicy == that.refreshPolicy &&
               warmupPolicy == that.warmupPolicy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, expiration, maxSize, statsEnabled, serializerType,
                          compressionEnabled, evictionPolicy, refreshPolicy, warmupPolicy);
    }

    @Override
    public String toString() {
        return "CacheConfig{" +
               "name='" + name + '\'' +
               ", expiration=" + expiration +
               ", maxSize=" + maxSize +
               ", statsEnabled=" + statsEnabled +
               ", serializerType=" + serializerType +
               ", compressionEnabled=" + compressionEnabled +
               ", evictionPolicy=" + evictionPolicy +
               ", refreshPolicy=" + refreshPolicy +
               ", warmupPolicy=" + warmupPolicy +
               '}';
    }
}