package com.genlib.config.impl;

import com.genlib.config.core.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 默认配置管理器实现
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class DefaultConfigManager implements ConfigManager {

    /** 配置提供者列表（按优先级排序） */
    private final List<ConfigProvider> providers = new CopyOnWriteArrayList<>();

    /** 配置提供者映射 */
    private final Map<String, ConfigProvider> providerMap = new ConcurrentHashMap<>();

    /** 全局配置变更监听器 */
    private final List<ConfigChangeListener> globalListeners = new CopyOnWriteArrayList<>();

    /** 按键分组的配置变更监听器 */
    private final Map<String, List<ConfigChangeListener>> keyListeners = new ConcurrentHashMap<>();

    /** 配置缓存 */
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    /** 配置来源缓存 */
    private final Map<String, String> sourceCache = new ConcurrentHashMap<>();

    /** 读写锁 */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /** 统计信息 */
    private final ConfigManagerStats stats = new ConfigManagerStats();

    /** 是否已关闭 */
    private volatile boolean closed = false;

    /** 是否启用缓存 */
    private boolean cacheEnabled = true;

    /**
     * 构造方法
     */
    public DefaultConfigManager() {
        // 默认添加系统属性和环境变量提供者
        registerProvider(new SystemPropertiesConfigProvider());
        registerProvider(new EnvironmentConfigProvider());
    }

    @Override
    public void registerProvider(ConfigProvider provider) {
        checkNotClosed();
        if (provider == null) {
            throw new IllegalArgumentException("ConfigProvider cannot be null");
        }

        lock.writeLock().lock();
        try {
            // 检查是否已存在同名提供者
            if (providerMap.containsKey(provider.getName())) {
                throw new IllegalArgumentException("Provider with name '" + provider.getName() + "' already exists");
            }

            providers.add(provider);
            providerMap.put(provider.getName(), provider);

            // 按优先级排序
            providers.sort(Comparator.comparingInt(ConfigProvider::getOrder));

            // 清空缓存
            clearCache();

            // 添加变更监听器
            provider.addListener(this::handleProviderConfigChange);

        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean unregisterProvider(String providerName) {
        checkNotClosed();
        if (providerName == null || providerName.trim().isEmpty()) {
            return false;
        }

        lock.writeLock().lock();
        try {
            ConfigProvider provider = providerMap.remove(providerName);
            if (provider != null) {
                providers.remove(provider);
                provider.close();
                clearCache();
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<ConfigProvider> getProvider(String providerName) {
        checkNotClosed();
        return Optional.ofNullable(providerMap.get(providerName));
    }

    @Override
    public List<ConfigProvider> getProviders() {
        checkNotClosed();
        lock.readLock().lock();
        try {
            return new ArrayList<>(providers);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<String> getString(String key) {
        checkNotClosed();
        if (key == null || key.trim().isEmpty()) {
            stats.recordQuery(false);
            return Optional.empty();
        }

        // 先从缓存获取
        if (cacheEnabled) {
            String cachedValue = configCache.get(key);
            if (cachedValue != null) {
                stats.recordQuery(true);
                return Optional.of(cachedValue);
            }
        }

        lock.readLock().lock();
        try {
            for (ConfigProvider provider : providers) {
                if (!provider.isAvailable() || !provider.supports(key)) {
                    continue;
                }

                Optional<String> value = provider.getString(key);
                if (value.isPresent()) {
                    // 缓存结果
                    if (cacheEnabled) {
                        configCache.put(key, value.get());
                        sourceCache.put(key, provider.getName());
                    }
                    stats.recordQuery(true);
                    return value;
                }
            }

            stats.recordQuery(false);
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String getString(String key, String defaultValue) {
        return getString(key).orElse(defaultValue);
    }

    @Override
    public Optional<Integer> getInteger(String key) {
        Optional<String> value = getString(key);
        return value.map(v -> {
            try {
                return Integer.parseInt(v.trim());
            } catch (NumberFormatException e) {
                throw new ConfigException("Invalid integer value for key '" + key + "': " + v, e);
            }
        });
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return getInteger(key).orElse(defaultValue);
    }

    @Override
    public Optional<Long> getLong(String key) {
        Optional<String> value = getString(key);
        return value.map(v -> {
            try {
                return Long.parseLong(v.trim());
            } catch (NumberFormatException e) {
                throw new ConfigException("Invalid long value for key '" + key + "': " + v, e);
            }
        });
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return getLong(key).orElse(defaultValue);
    }

    @Override
    public Optional<Double> getDouble(String key) {
        Optional<String> value = getString(key);
        return value.map(v -> {
            try {
                return Double.parseDouble(v.trim());
            } catch (NumberFormatException e) {
                throw new ConfigException("Invalid double value for key '" + key + "': " + v, e);
            }
        });
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return getDouble(key).orElse(defaultValue);
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        Optional<String> value = getString(key);
        return value.map(v -> {
            String trimmed = v.trim().toLowerCase();
            if ("true".equals(trimmed) || "yes".equals(trimmed) || "1".equals(trimmed)) {
                return true;
            } else if ("false".equals(trimmed) || "no".equals(trimmed) || "0".equals(trimmed)) {
                return false;
            } else {
                throw new ConfigException("Invalid boolean value for key '" + key + "': " + v);
            }
        });
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return getBoolean(key).orElse(defaultValue);
    }

    @Override
    public <T> Optional<T> getObject(String key, Class<T> clazz) {
        checkNotClosed();
        if (key == null || key.trim().isEmpty() || clazz == null) {
            return Optional.empty();
        }

        lock.readLock().lock();
        try {
            for (ConfigProvider provider : providers) {
                if (!provider.isAvailable() || !provider.supports(key)) {
                    continue;
                }

                Optional<T> value = provider.getObject(key, clazz);
                if (value.isPresent()) {
                    return value;
                }
            }

            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz, T defaultValue) {
        return getObject(key, clazz).orElse(defaultValue);
    }

    @Override
    public Map<String, String> getProperties(String prefix) {
        checkNotClosed();
        lock.readLock().lock();
        try {
            Map<String, String> result = new HashMap<>();

            // 按逆序遍历，让低优先级的先添加，高优先级的覆盖
            for (int i = providers.size() - 1; i >= 0; i--) {
                ConfigProvider provider = providers.get(i);
                if (provider.isAvailable()) {
                    Map<String, String> providerProps = provider.getProperties(prefix);
                    result.putAll(providerProps);
                }
            }

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<String> getKeys() {
        checkNotClosed();
        lock.readLock().lock();
        try {
            Set<String> result = new HashSet<>();
            for (ConfigProvider provider : providers) {
                if (provider.isAvailable()) {
                    result.addAll(provider.getKeys());
                }
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<String> getKeys(String prefix) {
        checkNotClosed();
        lock.readLock().lock();
        try {
            Set<String> result = new HashSet<>();
            for (ConfigProvider provider : providers) {
                if (provider.isAvailable()) {
                    result.addAll(provider.getKeys(prefix));
                }
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsKey(String key) {
        return getString(key).isPresent();
    }

    @Override
    public void refreshAll() {
        checkNotClosed();
        lock.readLock().lock();
        try {
            for (ConfigProvider provider : providers) {
                try {
                    provider.refresh();
                } catch (Exception e) {
                    System.err.println("Error refreshing provider " + provider.getName() + ": " + e.getMessage());
                }
            }
            clearCache();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void refresh(String providerName) {
        checkNotClosed();
        ConfigProvider provider = providerMap.get(providerName);
        if (provider != null) {
            provider.refresh();
            clearCache();
        }
    }

    @Override
    public void addListener(ConfigChangeListener listener) {
        checkNotClosed();
        if (listener != null && !globalListeners.contains(listener)) {
            globalListeners.add(listener);
        }
    }

    @Override
    public void removeListener(ConfigChangeListener listener) {
        globalListeners.remove(listener);
    }

    @Override
    public void addListener(String key, ConfigChangeListener listener) {
        checkNotClosed();
        if (key != null && listener != null) {
            keyListeners.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(listener);
        }
    }

    @Override
    public void removeListener(String key, ConfigChangeListener listener) {
        List<ConfigChangeListener> listeners = keyListeners.get(key);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                keyListeners.remove(key);
            }
        }
    }

    @Override
    public Optional<String> getConfigSource(String key) {
        checkNotClosed();
        if (cacheEnabled) {
            return Optional.ofNullable(sourceCache.get(key));
        }

        // 如果没有缓存，重新查找
        lock.readLock().lock();
        try {
            for (ConfigProvider provider : providers) {
                if (provider.isAvailable() && provider.supports(key) && provider.containsKey(key)) {
                    return Optional.of(provider.getName());
                }
            }
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ConfigManagerStats getStats() {
        checkNotClosed();
        ConfigManagerStats currentStats = new ConfigManagerStats();
        
        lock.readLock().lock();
        try {
            for (ConfigProvider provider : providers) {
                int configCount = provider.getKeys().size();
                boolean available = provider.isAvailable();
                currentStats.addProviderStats(provider.getName(), configCount, available);
            }
        } finally {
            lock.readLock().unlock();
        }

        return currentStats;
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            
            lock.writeLock().lock();
            try {
                for (ConfigProvider provider : providers) {
                    try {
                        provider.close();
                    } catch (Exception e) {
                        System.err.println("Error closing provider " + provider.getName() + ": " + e.getMessage());
                    }
                }
                
                providers.clear();
                providerMap.clear();
                clearCache();
                globalListeners.clear();
                keyListeners.clear();
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
     * 处理提供者配置变更
     *
     * @param event 变更事件
     */
    private void handleProviderConfigChange(ConfigChangeEvent event) {
        // 清空相关缓存
        if (cacheEnabled) {
            configCache.remove(event.getKey());
            sourceCache.remove(event.getKey());
        }

        // 通知全局监听器
        for (ConfigChangeListener listener : globalListeners) {
            if (listener.isInterestedIn(event.getKey())) {
                try {
                    listener.onConfigChange(event);
                } catch (Exception e) {
                    System.err.println("Error in global config change listener: " + e.getMessage());
                }
            }
        }

        // 通知键特定监听器
        List<ConfigChangeListener> listeners = keyListeners.get(event.getKey());
        if (listeners != null) {
            for (ConfigChangeListener listener : listeners) {
                try {
                    listener.onConfigChange(event);
                } catch (Exception e) {
                    System.err.println("Error in key-specific config change listener: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 清空缓存
     */
    private void clearCache() {
        configCache.clear();
        sourceCache.clear();
    }

    /**
     * 检查管理器是否已关闭
     */
    private void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("ConfigManager has been closed");
        }
    }

    /**
     * 设置是否启用缓存
     *
     * @param cacheEnabled 是否启用缓存
     */
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        if (!cacheEnabled) {
            clearCache();
        }
    }

    /**
     * 检查是否启用缓存
     *
     * @return 是否启用缓存
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * 获取缓存大小
     *
     * @return 缓存项数量
     */
    public int getCacheSize() {
        return configCache.size();
    }

    @Override
    public String toString() {
        return String.format("DefaultConfigManager{providerCount=%d, cacheEnabled=%s, cacheSize=%d, closed=%s}",
                           providers.size(), cacheEnabled, getCacheSize(), closed);
    }

    /**
     * 系统属性配置提供者
     */
    private static class SystemPropertiesConfigProvider implements ConfigProvider {
        @Override
        public String getName() {
            return "system-properties";
        }

        @Override
        public int getOrder() {
            return 200;
        }

        @Override
        public boolean supports(String key) {
            return key != null && !key.trim().isEmpty();
        }

        @Override
        public Optional<String> getString(String key) {
            return Optional.ofNullable(System.getProperty(key));
        }

        @Override
        public <T> Optional<T> getObject(String key, Class<T> clazz) {
            // 系统属性只支持字符串
            if (clazz == String.class) {
                return (Optional<T>) getString(key);
            }
            return Optional.empty();
        }

        @Override
        public Map<String, String> getProperties(String prefix) {
            Map<String, String> result = new HashMap<>();
            String normalizedPrefix = prefix == null ? "" : prefix.trim();
            
            for (String propertyName : System.getProperties().stringPropertyNames()) {
                if (propertyName.startsWith(normalizedPrefix)) {
                    result.put(propertyName, System.getProperty(propertyName));
                }
            }
            
            return result;
        }

        @Override
        public Set<String> getKeys() {
            return System.getProperties().stringPropertyNames();
        }

        @Override
        public Set<String> getKeys(String prefix) {
            Set<String> result = new HashSet<>();
            String normalizedPrefix = prefix == null ? "" : prefix.trim();
            
            for (String propertyName : System.getProperties().stringPropertyNames()) {
                if (propertyName.startsWith(normalizedPrefix)) {
                    result.add(propertyName);
                }
            }
            
            return result;
        }

        @Override
        public boolean containsKey(String key) {
            return System.getProperties().containsKey(key);
        }

        @Override
        public void refresh() {
            // 系统属性不需要刷新
        }

        @Override
        public void addListener(ConfigChangeListener listener) {
            // 系统属性不支持监听
        }

        @Override
        public void removeListener(ConfigChangeListener listener) {
            // 系统属性不支持监听
        }

        @Override
        public ConfigSourceType getSourceType() {
            return ConfigSourceType.SYSTEM_PROPERTIES;
        }
    }

    /**
     * 环境变量配置提供者
     */
    private static class EnvironmentConfigProvider implements ConfigProvider {
        @Override
        public String getName() {
            return "environment";
        }

        @Override
        public int getOrder() {
            return 300;
        }

        @Override
        public boolean supports(String key) {
            return key != null && !key.trim().isEmpty();
        }

        @Override
        public Optional<String> getString(String key) {
            return Optional.ofNullable(System.getenv(key));
        }

        @Override
        public <T> Optional<T> getObject(String key, Class<T> clazz) {
            // 环境变量只支持字符串
            if (clazz == String.class) {
                return (Optional<T>) getString(key);
            }
            return Optional.empty();
        }

        @Override
        public Map<String, String> getProperties(String prefix) {
            Map<String, String> result = new HashMap<>();
            String normalizedPrefix = prefix == null ? "" : prefix.trim();
            
            for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
                if (entry.getKey().startsWith(normalizedPrefix)) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
            
            return result;
        }

        @Override
        public Set<String> getKeys() {
            return System.getenv().keySet();
        }

        @Override
        public Set<String> getKeys(String prefix) {
            Set<String> result = new HashSet<>();
            String normalizedPrefix = prefix == null ? "" : prefix.trim();
            
            for (String key : System.getenv().keySet()) {
                if (key.startsWith(normalizedPrefix)) {
                    result.add(key);
                }
            }
            
            return result;
        }

        @Override
        public boolean containsKey(String key) {
            return System.getenv().containsKey(key);
        }

        @Override
        public void refresh() {
            // 环境变量不需要刷新
        }

        @Override
        public void addListener(ConfigChangeListener listener) {
            // 环境变量不支持监听
        }

        @Override
        public void removeListener(ConfigChangeListener listener) {
            // 环境变量不支持监听
        }

        @Override
        public ConfigSourceType getSourceType() {
            return ConfigSourceType.ENVIRONMENT;
        }
    }
}