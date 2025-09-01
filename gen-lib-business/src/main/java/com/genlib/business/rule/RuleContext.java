package com.genlib.business.rule;

import java.util.HashMap;
import java.util.Map;

/**
 * 规则上下文
 * 用于在规则执行过程中传递数据
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class RuleContext {

    private final Map<String, Object> data;
    private final Map<String, Object> metadata;

    public RuleContext() {
        this.data = new HashMap<>();
        this.metadata = new HashMap<>();
    }

    public RuleContext(Map<String, Object> data) {
        this.data = new HashMap<>(data);
        this.metadata = new HashMap<>();
    }

    /**
     * 设置数据
     */
    public RuleContext put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    /**
     * 获取数据
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    /**
     * 获取数据（带默认值）
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return (T) data.getOrDefault(key, defaultValue);
    }

    /**
     * 获取字符串
     */
    public String getString(String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取整数
     */
    public Integer getInteger(String key) {
        Object value = data.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.valueOf((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取长整数
     */
    public Long getLong(String key) {
        Object value = data.get(key);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.valueOf((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取布尔值
     */
    public Boolean getBoolean(String key) {
        Object value = data.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.valueOf((String) value);
        }
        return null;
    }

    /**
     * 检查是否包含键
     */
    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    /**
     * 移除数据
     */
    public Object remove(String key) {
        return data.remove(key);
    }

    /**
     * 获取所有数据
     */
    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    /**
     * 设置元数据
     */
    public RuleContext putMetadata(String key, Object value) {
        metadata.put(key, value);
        return this;
    }

    /**
     * 获取元数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key) {
        return (T) metadata.get(key);
    }

    /**
     * 获取所有元数据
     */
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    /**
     * 清空数据
     */
    public void clear() {
        data.clear();
        metadata.clear();
    }

    /**
     * 复制上下文
     */
    public RuleContext copy() {
        RuleContext newContext = new RuleContext(this.data);
        newContext.metadata.putAll(this.metadata);
        return newContext;
    }

    @Override
    public String toString() {
        return "RuleContext{" +
                "data=" + data +
                ", metadata=" + metadata +
                '}';
    }
}