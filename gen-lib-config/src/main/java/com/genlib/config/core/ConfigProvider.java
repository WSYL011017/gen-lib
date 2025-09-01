package com.genlib.config.core;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 配置提供者接口
 * 定义统一的配置获取接口
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public interface ConfigProvider {

    /**
     * 获取配置提供者名称
     *
     * @return 提供者名称
     */
    String getName();

    /**
     * 获取配置优先级（数值越小优先级越高）
     *
     * @return 优先级
     */
    default int getOrder() {
        return Integer.MAX_VALUE;
    }

    /**
     * 检查是否支持指定的配置键
     *
     * @param key 配置键
     * @return 是否支持
     */
    boolean supports(String key);

    /**
     * 获取字符串配置值
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
    default String getString(String key, String defaultValue) {
        return getString(key).orElse(defaultValue);
    }

    /**
     * 获取整数配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    default Optional<Integer> getInteger(String key) {
        return getString(key).map(value -> {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                throw new ConfigException("Invalid integer value for key '" + key + "': " + value, e);
            }
        });
    }

    /**
     * 获取整数配置值，带默认值
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    default Integer getInteger(String key, Integer defaultValue) {
        return getInteger(key).orElse(defaultValue);
    }

    /**
     * 获取长整数配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    default Optional<Long> getLong(String key) {
        return getString(key).map(value -> {
            try {
                return Long.parseLong(value.trim());
            } catch (NumberFormatException e) {
                throw new ConfigException("Invalid long value for key '" + key + "': " + value, e);
            }
        });
    }

    /**
     * 获取长整数配置值，带默认值
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    default Long getLong(String key, Long defaultValue) {
        return getLong(key).orElse(defaultValue);
    }

    /**
     * 获取浮点数配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    default Optional<Double> getDouble(String key) {
        return getString(key).map(value -> {
            try {
                return Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                throw new ConfigException("Invalid double value for key '" + key + "': " + value, e);
            }
        });
    }

    /**
     * 获取浮点数配置值，带默认值
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    default Double getDouble(String key, Double defaultValue) {
        return getDouble(key).orElse(defaultValue);
    }

    /**
     * 获取布尔配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    default Optional<Boolean> getBoolean(String key) {
        return getString(key).map(value -> {
            String trimmed = value.trim().toLowerCase();
            if ("true".equals(trimmed) || "yes".equals(trimmed) || "1".equals(trimmed)) {
                return true;
            } else if ("false".equals(trimmed) || "no".equals(trimmed) || "0".equals(trimmed)) {
                return false;
            } else {
                throw new ConfigException("Invalid boolean value for key '" + key + "': " + value);
            }
        });
    }

    /**
     * 获取布尔配置值，带默认值
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    default Boolean getBoolean(String key, Boolean defaultValue) {
        return getBoolean(key).orElse(defaultValue);
    }

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
    default <T> T getObject(String key, Class<T> clazz, T defaultValue) {
        return getObject(key, clazz).orElse(defaultValue);
    }

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
     * 刷新配置
     * 重新加载配置数据
     */
    void refresh();

    /**
     * 添加配置变更监听器
     *
     * @param listener 监听器
     */
    void addListener(ConfigChangeListener listener);

    /**
     * 移除配置变更监听器
     *
     * @param listener 监听器
     */
    void removeListener(ConfigChangeListener listener);

    /**
     * 获取配置源类型
     *
     * @return 配置源类型
     */
    ConfigSourceType getSourceType();

    /**
     * 检查配置提供者是否可用
     *
     * @return 是否可用
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * 关闭配置提供者
     */
    default void close() {
        // 默认空实现
    }
}