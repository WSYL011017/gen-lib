package com.genlib.config.provider;

import com.genlib.config.core.*;
import com.genlib.utils.json.JsonUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Properties配置文件提供者
 * 支持.properties文件的读取和监听
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class PropertiesConfigProvider implements ConfigProvider {

    /** 提供者名称 */
    private final String name;

    /** 配置文件路径 */
    private final String filePath;

    /** 配置属性 */
    private final Properties properties;

    /** 读写锁 */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /** 配置变更监听器列表 */
    private final List<ConfigChangeListener> listeners = new CopyOnWriteArrayList<>();

    /** 文件监听器 */
    private WatchService watchService;
    private Path watchPath;
    private volatile boolean watching = false;

    /** 优先级 */
    private int order = 100;

    /**
     * 构造方法
     *
     * @param name 提供者名称
     * @param filePath 配置文件路径
     */
    public PropertiesConfigProvider(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
        this.properties = new Properties();
        loadProperties();
        startFileWatcher();
    }

    /**
     * 构造方法
     *
     * @param name 提供者名称
     * @param filePath 配置文件路径
     * @param order 优先级
     */
    public PropertiesConfigProvider(String name, String filePath, int order) {
        this(name, filePath);
        this.order = order;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean supports(String key) {
        return key != null && !key.trim().isEmpty();
    }

    @Override
    public Optional<String> getString(String key) {
        if (key == null || key.trim().isEmpty()) {
            return Optional.empty();
        }

        lock.readLock().lock();
        try {
            String value = properties.getProperty(key.trim());
            return Optional.ofNullable(value);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T> Optional<T> getObject(String key, Class<T> clazz) {
        Optional<String> stringValue = getString(key);
        if (!stringValue.isPresent()) {
            return Optional.empty();
        }

        String value = stringValue.get();
        try {
            // 尝试JSON反序列化
            if (value.trim().startsWith("{") || value.trim().startsWith("[")) {
                return Optional.ofNullable(JsonUtils.fromJson(value, clazz));
            }

            // 基本类型转换
            if (clazz == String.class) {
                return Optional.of(clazz.cast(value));
            } else if (clazz == Integer.class || clazz == int.class) {
                return Optional.of(clazz.cast(Integer.parseInt(value.trim())));
            } else if (clazz == Long.class || clazz == long.class) {
                return Optional.of(clazz.cast(Long.parseLong(value.trim())));
            } else if (clazz == Double.class || clazz == double.class) {
                return Optional.of(clazz.cast(Double.parseDouble(value.trim())));
            } else if (clazz == Boolean.class || clazz == boolean.class) {
                String trimmed = value.trim().toLowerCase();
                boolean boolValue = "true".equals(trimmed) || "yes".equals(trimmed) || "1".equals(trimmed);
                return Optional.of(clazz.cast(boolValue));
            }

            return Optional.empty();
        } catch (Exception e) {
            throw new ConfigException("Failed to convert value '" + value + "' to type " + clazz.getName(), e);
        }
    }

    @Override
    public Map<String, String> getProperties(String prefix) {
        lock.readLock().lock();
        try {
            Map<String, String> result = new HashMap<>();
            String normalizedPrefix = prefix == null ? "" : prefix.trim();
            
            for (String propertyName : properties.stringPropertyNames()) {
                if (propertyName.startsWith(normalizedPrefix)) {
                    result.put(propertyName, properties.getProperty(propertyName));
                }
            }
            
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<String> getKeys() {
        lock.readLock().lock();
        try {
            return new HashSet<>(properties.stringPropertyNames());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<String> getKeys(String prefix) {
        lock.readLock().lock();
        try {
            Set<String> result = new HashSet<>();
            String normalizedPrefix = prefix == null ? "" : prefix.trim();
            
            for (String propertyName : properties.stringPropertyNames()) {
                if (propertyName.startsWith(normalizedPrefix)) {
                    result.add(propertyName);
                }
            }
            
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }

        lock.readLock().lock();
        try {
            return properties.containsKey(key.trim());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void refresh() {
        loadProperties();
    }

    @Override
    public void addListener(ConfigChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public ConfigSourceType getSourceType() {
        return ConfigSourceType.PROPERTIES;
    }

    @Override
    public boolean isAvailable() {
        return Files.exists(Paths.get(filePath));
    }

    @Override
    public void close() {
        stopFileWatcher();
        listeners.clear();
    }

    /**
     * 加载Properties文件
     */
    private void loadProperties() {
        lock.writeLock().lock();
        try {
            Properties oldProperties = new Properties();
            oldProperties.putAll(properties);

            properties.clear();

            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                try (InputStream inputStream = Files.newInputStream(path);
                     InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }

                // 通知配置变更
                notifyConfigChanges(oldProperties, properties);
            }
        } catch (IOException e) {
            throw new ConfigException("Failed to load properties file: " + filePath, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 通知配置变更
     *
     * @param oldProperties 旧配置
     * @param newProperties 新配置
     */
    private void notifyConfigChanges(Properties oldProperties, Properties newProperties) {
        if (listeners.isEmpty()) {
            return;
        }

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(oldProperties.stringPropertyNames());
        allKeys.addAll(newProperties.stringPropertyNames());

        for (String key : allKeys) {
            String oldValue = oldProperties.getProperty(key);
            String newValue = newProperties.getProperty(key);

            ConfigChangeEvent event = null;
            if (oldValue == null && newValue != null) {
                // 新增
                event = ConfigChangeEvent.added(key, newValue, name);
            } else if (oldValue != null && newValue == null) {
                // 删除
                event = ConfigChangeEvent.deleted(key, oldValue, name);
            } else if (oldValue != null && !oldValue.equals(newValue)) {
                // 修改
                event = ConfigChangeEvent.modified(key, oldValue, newValue, name);
            }

            if (event != null) {
                for (ConfigChangeListener listener : listeners) {
                    if (listener.isInterestedIn(key)) {
                        try {
                            listener.onConfigChange(event);
                        } catch (Exception e) {
                            // 监听器异常不应该影响配置加载
                            System.err.println("Error in config change listener: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    /**
     * 启动文件监听器
     */
    private void startFileWatcher() {
        try {
            Path filePath = Paths.get(this.filePath);
            if (!Files.exists(filePath)) {
                return;
            }

            watchPath = filePath.getParent();
            if (watchPath == null) {
                return;
            }

            watchService = FileSystems.getDefault().newWatchService();
            watchPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            watching = true;
            Thread watchThread = new Thread(this::watchFileChanges, "PropertiesConfigWatcher-" + name);
            watchThread.setDaemon(true);
            watchThread.start();

        } catch (IOException e) {
            System.err.println("Failed to start file watcher for: " + filePath + ", error: " + e.getMessage());
        }
    }

    /**
     * 监听文件变更
     */
    private void watchFileChanges() {
        while (watching && watchService != null) {
            try {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        Path changed = (Path) event.context();
                        if (changed.toString().equals(Paths.get(filePath).getFileName().toString())) {
                            // 延迟一下再加载，避免文件还在写入过程中
                            Thread.sleep(100);
                            loadProperties();
                        }
                    }
                }
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error watching file changes: " + e.getMessage());
            }
        }
    }

    /**
     * 停止文件监听器
     */
    private void stopFileWatcher() {
        watching = false;
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                System.err.println("Error closing watch service: " + e.getMessage());
            }
        }
    }

    /**
     * 获取配置文件路径
     *
     * @return 文件路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 设置优先级
     *
     * @param order 优先级
     */
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return String.format("PropertiesConfigProvider{name='%s', filePath='%s', order=%d, available=%s}",
                           name, filePath, order, isAvailable());
    }
}