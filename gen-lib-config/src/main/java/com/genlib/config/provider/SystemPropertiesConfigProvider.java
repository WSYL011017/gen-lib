package com.genlib.config.provider;

import com.genlib.config.core.ConfigProvider;
import com.genlib.config.core.ConfigSourceType;
import com.genlib.config.core.ConfigChangeListener;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 系统属性配置提供者
 * 从JVM系统属性读取配置
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class SystemPropertiesConfigProvider implements ConfigProvider {

    private final String name;
    private final int priority;
    private final String prefix;
    private final List<ConfigChangeListener> listeners;

    /**
     * 构造函数
     *
     * @param name 提供者名称
     * @param priority 优先级
     */
    public SystemPropertiesConfigProvider(String name, int priority) {
        this(name, priority, null);
    }

    /**
     * 构造函数（带前缀）
     *
     * @param name 提供者名称
     * @param priority 优先级
     * @param prefix 系统属性前缀
     */
    public SystemPropertiesConfigProvider(String name, int priority, String prefix) {
        this.name = name;
        this.priority = priority;
        this.prefix = prefix;
        this.listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrder() {
        return priority;
    }

    @Override
    public boolean supports(String key) {
        return containsKey(key);
    }

    @Override
    public Optional<String> getString(String key) {
        if (key == null || key.isEmpty()) {
            return Optional.empty();
        }

        // 如果指定了前缀，先尝试带前缀的键
        if (prefix != null && !prefix.isEmpty()) {
            String prefixedKey = prefix + "." + key;
            String value = System.getProperty(prefixedKey);
            if (value != null) {
                return Optional.of(value);
            }
            
            // 尝试其他前缀格式
            prefixedKey = prefix + "_" + key;
            value = System.getProperty(prefixedKey);
            if (value != null) {
                return Optional.of(value);
            }
        }

        // 直接使用原始键
        String value = System.getProperty(key);
        return value != null ? Optional.of(value) : Optional.empty();
    }

    @Override
    public <T> Optional<T> getObject(String key, Class<T> clazz) {
        Optional<String> value = getString(key);
        if (!value.isPresent()) {
            return Optional.empty();
        }
        
        try {
            if (clazz == String.class) {
                return Optional.of(clazz.cast(value.get()));
            } else if (clazz == Integer.class) {
                return Optional.of(clazz.cast(Integer.valueOf(value.get())));
            } else if (clazz == Long.class) {
                return Optional.of(clazz.cast(Long.valueOf(value.get())));
            } else if (clazz == Boolean.class) {
                return Optional.of(clazz.cast(Boolean.valueOf(value.get())));
            } else if (clazz == Double.class) {
                return Optional.of(clazz.cast(Double.valueOf(value.get())));
            }
        } catch (Exception e) {
            // 转换失败，返回空
        }
        return Optional.empty();
    }

    @Override
    public Map<String, String> getProperties(String prefix) {
        Map<String, String> allProps = getAllProperties();
        if (prefix == null || prefix.isEmpty()) {
            return allProps;
        }
        
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : allProps.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    @Override
    public Set<String> getKeys() {
        return getAllProperties().keySet();
    }

    @Override
    public Set<String> getKeys(String prefix) {
        return getProperties(prefix).keySet();
    }

    @Override
    public boolean containsKey(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }

        // 如果指定了前缀，先检查带前缀的键
        if (prefix != null && !prefix.isEmpty()) {
            String prefixedKey = prefix + "." + key;
            if (System.getProperty(prefixedKey) != null) {
                return true;
            }
            
            prefixedKey = prefix + "_" + key;
            if (System.getProperty(prefixedKey) != null) {
                return true;
            }
        }

        // 检查原始键
        return System.getProperty(key) != null;
    }

    @Override
    public void refresh() {
        // 系统属性不需要刷新
    }

    @Override
    public void addListener(ConfigChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(ConfigChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public ConfigSourceType getSourceType() {
        return ConfigSourceType.SYSTEM_PROPERTIES;
    }

    /**
     * 获取所有系统属性配置
     *
     * @return 配置映射
     */
    private Map<String, String> getAllProperties() {
        Map<String, String> result = new HashMap<>();
        Properties systemProperties = System.getProperties();
        
        for (Map.Entry<Object, Object> entry : systemProperties.entrySet()) {
            String propKey = entry.getKey().toString();
            String propValue = entry.getValue().toString();
            
            // 如果指定了前缀，只返回匹配前缀的属性
            if (prefix != null && !prefix.isEmpty()) {
                if (propKey.startsWith(prefix + ".")) {
                    // 移除前缀
                    String configKey = propKey.substring((prefix + ".").length());
                    result.put(configKey, propValue);
                } else if (propKey.startsWith(prefix + "_")) {
                    // 移除前缀并转换下划线为点
                    String configKey = propKey.substring((prefix + "_").length()).replace('_', '.');
                    result.put(configKey, propValue);
                }
            } else {
                // 返回所有系统属性
                result.put(propKey, propValue);
            }
        }
        
        return result;
    }


}