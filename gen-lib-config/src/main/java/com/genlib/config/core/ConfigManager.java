package com.genlib.config.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 配置管理器接口
 * 统一管理多个配置提供者
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public interface ConfigManager {

    /**
     * 注册配置提供者
     *
     * @param provider 配置提供者
     */
    void registerProvider(ConfigProvider provider);

    /**
     * 注销配置提供者
     *
     * @param providerName 提供者名称
     * @return 是否成功注销
     */
    boolean unregisterProvider(String providerName);

    /**
     * 获取配置提供者
     *
     * @param providerName 提供者名称
     * @return 配置提供者
     */
    Optional<ConfigProvider> getProvider(String providerName);

    /**
     * 获取所有配置提供者
     *
     * @return 配置提供者列表（按优先级排序）
     */
    List<ConfigProvider> getProviders();

    /**
     * 获取字符串配置值
     * 按优先级从各提供者中获取配置
     *
     * @param key 配置键
     * @return 配置值
     */
    Optional<String> getString(String key);

    /**
     * 获取字符串配置值，带默认值
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    String getString(String key, String defaultValue);

    /**
     * 获取整数配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    Optional<Integer> getInteger(String key);

    /**
     * 获取整数配置值，带默认值
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    Integer getInteger(String key, Integer defaultValue);

    /**
     * 获取长整数配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    Optional<Long> getLong(String key);

    /**
     * 获取长整数配置值，带默认值
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    Long getLong(String key, Long defaultValue);

    /**
     * 获取浮点数配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    Optional<Double> getDouble(String key);

    /**
     * 获取浮点数配置值，带默认值
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    Double getDouble(String key, Double defaultValue);

    /**
     * 获取布尔配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    Optional<Boolean> getBoolean(String key);

    /**
     * 获取布尔配置值，带默认值
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    Boolean getBoolean(String key, Boolean defaultValue);

    /**
     * 获取对象配置值
     *
     * @param key 配置键
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 配置值
     */
    <T> Optional<T> getObject(String key, Class<T> clazz);

    /**
     * 获取对象配置值，带默认值
     *
     * @param key 配置键
     * @param clazz 目标类型
     * @param defaultValue 默认值
     * @param <T> 泛型类型
     * @return 配置值或默认值
     */
    <T> T getObject(String key, Class<T> clazz, T defaultValue);

    /**
     * 获取指定前缀的所有配置
     *
     * @param prefix 配置前缀
     * @return 配置映射
     */
    Map<String, String> getProperties(String prefix);

    /**
     * 获取所有配置键
     *
     * @return 配置键集合
     */
    Set<String> getKeys();

    /**
     * 获取指定前缀的所有配置键
     *
     * @param prefix 配置前缀
     * @return 配置键集合
     */
    Set<String> getKeys(String prefix);

    /**
     * 检查配置键是否存在
     *
     * @param key 配置键
     * @return 是否存在
     */
    boolean containsKey(String key);

    /**
     * 刷新所有配置提供者
     */
    void refreshAll();

    /**
     * 刷新指定配置提供者
     *
     * @param providerName 提供者名称
     */
    void refresh(String providerName);

    /**
     * 添加全局配置变更监听器
     *
     * @param listener 监听器
     */
    void addListener(ConfigChangeListener listener);

    /**
     * 移除全局配置变更监听器
     *
     * @param listener 监听器
     */
    void removeListener(ConfigChangeListener listener);

    /**
     * 添加指定键的配置变更监听器
     *
     * @param key 配置键
     * @param listener 监听器
     */
    void addListener(String key, ConfigChangeListener listener);

    /**
     * 移除指定键的配置变更监听器
     *
     * @param key 配置键
     * @param listener 监听器
     */
    void removeListener(String key, ConfigChangeListener listener);

    /**
     * 获取配置来源信息
     *
     * @param key 配置键
     * @return 来源信息（提供者名称）
     */
    Optional<String> getConfigSource(String key);

    /**
     * 获取配置统计信息
     *
     * @return 统计信息
     */
    ConfigManagerStats getStats();

    /**
     * 关闭配置管理器
     */
    void close();

    /**
     * 检查配置管理器是否已关闭
     *
     * @return 是否已关闭
     */
    boolean isClosed();
}