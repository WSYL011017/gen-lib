package com.genlib.config.provider;

import com.genlib.config.core.ConfigProvider;
import com.genlib.config.core.ConfigSourceType;
import com.genlib.config.core.ConfigChangeListener;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * YAML配置提供者
 * 从YAML文件读取配置
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class YamlConfigProvider implements ConfigProvider {

    private final String name;
    private final String yamlFilePath;
    private final int priority;
    private final Map<String, Object> configData;
    private final List<ConfigChangeListener> listeners;
    private long lastModified;

    public YamlConfigProvider(String name, String yamlFilePath, int priority) {
        this.name = name;
        this.yamlFilePath = yamlFilePath;
        this.priority = priority;
        this.configData = new HashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
        loadConfig();
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
        Object value = getNestedProperty(configData, key);
        return value != null ? Optional.of(value.toString()) : Optional.empty();
    }

    @Override
    public <T> Optional<T> getObject(String key, Class<T> clazz) {
        Object value = getNestedProperty(configData, key);
        if (value == null) {
            return Optional.empty();
        }
        
        try {
            if (clazz == String.class) {
                return Optional.of(clazz.cast(value.toString()));
            } else if (clazz == Integer.class) {
                return Optional.of(clazz.cast(Integer.valueOf(value.toString())));
            } else if (clazz == Long.class) {
                return Optional.of(clazz.cast(Long.valueOf(value.toString())));
            } else if (clazz == Boolean.class) {
                return Optional.of(clazz.cast(Boolean.valueOf(value.toString())));
            } else if (clazz == Double.class) {
                return Optional.of(clazz.cast(Double.valueOf(value.toString())));
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
        return getNestedProperty(configData, key) != null;
    }

    @Override
    public void refresh() {
        loadConfig();
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
        return ConfigSourceType.YAML;
    }

    /**
     * 获取所有配置
     *
     * @return 配置映射
     */
    private Map<String, String> getAllProperties() {
        Map<String, String> result = new HashMap<>();
        flattenMap("", configData, result);
        return result;
    }



    /**
     * 加载YAML配置
     */
    private void loadConfig() {
        try {
            File file = new File(yamlFilePath);
            if (!file.exists()) {
                System.out.println("YAML config file not found: " + yamlFilePath);
                return;
            }

            // 检查文件是否有更新
            long currentModified = file.lastModified();
            if (currentModified == lastModified) {
                return;
            }
            lastModified = currentModified;

            // 读取YAML文件
            Yaml yaml = new Yaml();
            try (InputStream inputStream = Files.newInputStream(Paths.get(yamlFilePath))) {
                Object data = yaml.load(inputStream);
                configData.clear();
                if (data instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> mapData = (Map<String, Object>) data;
                    configData.putAll(mapData);
                }
            }

            System.out.println("Loaded YAML config from: " + yamlFilePath);
        } catch (Exception e) {
            System.err.println("Failed to load YAML config from " + yamlFilePath + ": " + e.getMessage());
        }
    }

    /**
     * 获取嵌套属性值
     * 支持点分割的属性名，如：app.database.url
     *
     * @param map 配置Map
     * @param key 属性键
     * @return 属性值
     */
    private Object getNestedProperty(Map<String, Object> map, String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }

        String[] keys = key.split("\\.");
        Object current = map;

        for (String k : keys) {
            if (current instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> currentMap = (Map<String, Object>) current;
                current = currentMap.get(k);
            } else {
                return null;
            }
        }

        return current;
    }

    /**
     * 扁平化Map结构
     * 将嵌套的Map转换为点分割的键值对
     *
     * @param prefix 前缀
     * @param map 原始Map
     * @param result 结果Map
     */
    private void flattenMap(String prefix, Map<String, Object> map, Map<String, String> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                flattenMap(key, nestedMap, result);
            } else if (value != null) {
                result.put(key, value.toString());
            }
        }
    }
}