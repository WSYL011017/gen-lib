package com.genlib.config.provider;

import com.genlib.config.core.ConfigProvider;
import com.genlib.config.core.ConfigSourceType;
import com.genlib.config.core.ConfigChangeListener;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 环境变量配置提供者
 * 从系统环境变量读取配置
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class EnvironmentConfigProvider implements ConfigProvider {

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
    public EnvironmentConfigProvider(String name, int priority) {
        this(name, priority, null);
    }

    /**
     * 构造函数（带前缀）
     *
     * @param name 提供者名称
     * @param priority 优先级
     * @param prefix 环境变量前缀
     */
    public EnvironmentConfigProvider(String name, int priority, String prefix) {
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

        // 尝试多种环境变量名称格式
        String[] envKeys = generateEnvKeys(key);
        
        for (String envKey : envKeys) {
            String value = System.getenv(envKey);
            if (value != null) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }

    @Override
    public <T> Optional<T> getObject(String key, Class<T> clazz) {
        Optional<String> value = getString(key);
        if (!value.isPresent()) {
            return Optional.empty();
        }
        // 简单实现，只支持基本类型
        try {
            if (clazz == String.class) {
                return Optional.of(clazz.cast(value.get()));
            } else if (clazz == Integer.class) {
                return Optional.of(clazz.cast(Integer.parseInt(value.get())));
            } else if (clazz == Long.class) {
                return Optional.of(clazz.cast(Long.parseLong(value.get())));
            } else if (clazz == Boolean.class) {
                return Optional.of(clazz.cast(Boolean.parseBoolean(value.get())));
            }
        } catch (Exception e) {
            // 转换失败，返回空
        }
        return Optional.empty();
    }

    @Override
    public Map<String, String> getProperties(String prefix) {
        Map<String, String> result = new HashMap<>();
        Map<String, String> allProps = getAllProperties();
        
        if (prefix == null || prefix.isEmpty()) {
            return allProps;
        }
        
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

        // 尝试多种环境变量名称格式
        String[] envKeys = generateEnvKeys(key);
        
        for (String envKey : envKeys) {
            if (System.getenv(envKey) != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void refresh() {
        // 环境变量不需要刷新
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
        return ConfigSourceType.ENVIRONMENT;
    }

    /**
     * 获取所有环境变量配置
     *
     * @return 配置映射
     */
    private Map<String, String> getAllProperties() {
        Map<String, String> result = new HashMap<>();
        Map<String, String> envMap = System.getenv();
        
        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            String envKey = entry.getKey();
            String envValue = entry.getValue();
            
            // 如果指定了前缀，只返回匹配前缀的环境变量
            if (prefix != null && !prefix.isEmpty()) {
                if (envKey.startsWith(prefix)) {
                    // 移除前缀并转换为标准格式
                    String configKey = envKey.substring(prefix.length());
                    if (configKey.startsWith("_")) {
                        configKey = configKey.substring(1);
                    }
                    configKey = convertEnvKeyToConfigKey(configKey);
                    result.put(configKey, envValue);
                }
            } else {
                // 转换所有环境变量
                String configKey = convertEnvKeyToConfigKey(envKey);
                result.put(configKey, envValue);
            }
        }
        
        return result;
    }



    /**
     * 生成可能的环境变量键名
     * 将配置键转换为多种可能的环境变量格式
     *
     * @param configKey 配置键
     * @return 环境变量键数组
     */
    private String[] generateEnvKeys(String configKey) {
        if (configKey == null || configKey.isEmpty()) {
            return new String[0];
        }

        // 生成多种可能的环境变量名称
        String upperKey = configKey.toUpperCase().replace('.', '_').replace('-', '_');
        String lowerKey = configKey.toLowerCase().replace('.', '_').replace('-', '_');
        String originalKey = configKey;

        if (prefix != null && !prefix.isEmpty()) {
            String prefixUpper = prefix.toUpperCase();
            if (!prefixUpper.endsWith("_")) {
                prefixUpper += "_";
            }
            return new String[] {
                prefixUpper + upperKey,
                prefixUpper + lowerKey,
                prefix + "_" + originalKey,
                prefix + "." + originalKey,
                upperKey,
                lowerKey,
                originalKey
            };
        } else {
            return new String[] {
                upperKey,
                lowerKey,
                originalKey
            };
        }
    }

    /**
     * 将环境变量键转换为配置键
     * 如：APP_DATABASE_URL -> app.database.url
     *
     * @param envKey 环境变量键
     * @return 配置键
     */
    private String convertEnvKeyToConfigKey(String envKey) {
        if (envKey == null || envKey.isEmpty()) {
            return envKey;
        }

        return envKey.toLowerCase().replace('_', '.');
    }
}