package com.genlib.config.core;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 配置变更事件
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class ConfigChangeEvent {

    /** 配置键 */
    private final String key;

    /** 旧值 */
    private final String oldValue;

    /** 新值 */
    private final String newValue;

    /** 变更类型 */
    private final ChangeType changeType;

    /** 配置源 */
    private final String source;

    /** 变更时间 */
    private final LocalDateTime changeTime;

    /**
     * 构造方法
     *
     * @param key 配置键
     * @param oldValue 旧值
     * @param newValue 新值
     * @param changeType 变更类型
     * @param source 配置源
     */
    public ConfigChangeEvent(String key, String oldValue, String newValue, 
                           ChangeType changeType, String source) {
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changeType = changeType;
        this.source = source;
        this.changeTime = LocalDateTime.now();
    }

    /**
     * 创建新增事件
     *
     * @param key 配置键
     * @param newValue 新值
     * @param source 配置源
     * @return 变更事件
     */
    public static ConfigChangeEvent added(String key, String newValue, String source) {
        return new ConfigChangeEvent(key, null, newValue, ChangeType.ADDED, source);
    }

    /**
     * 创建修改事件
     *
     * @param key 配置键
     * @param oldValue 旧值
     * @param newValue 新值
     * @param source 配置源
     * @return 变更事件
     */
    public static ConfigChangeEvent modified(String key, String oldValue, String newValue, String source) {
        return new ConfigChangeEvent(key, oldValue, newValue, ChangeType.MODIFIED, source);
    }

    /**
     * 创建删除事件
     *
     * @param key 配置键
     * @param oldValue 旧值
     * @param source 配置源
     * @return 变更事件
     */
    public static ConfigChangeEvent deleted(String key, String oldValue, String source) {
        return new ConfigChangeEvent(key, oldValue, null, ChangeType.DELETED, source);
    }

    /**
     * 变更类型枚举
     */
    public enum ChangeType {
        /** 新增 */
        ADDED("新增"),
        
        /** 修改 */
        MODIFIED("修改"),
        
        /** 删除 */
        DELETED("删除");

        private final String description;

        ChangeType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ================== Getter 方法 ==================

    public String getKey() {
        return key;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public String getSource() {
        return source;
    }

    public LocalDateTime getChangeTime() {
        return changeTime;
    }

    // ================== 工具方法 ==================

    /**
     * 检查是否为新增事件
     *
     * @return 是否为新增
     */
    public boolean isAdded() {
        return changeType == ChangeType.ADDED;
    }

    /**
     * 检查是否为修改事件
     *
     * @return 是否为修改
     */
    public boolean isModified() {
        return changeType == ChangeType.MODIFIED;
    }

    /**
     * 检查是否为删除事件
     *
     * @return 是否为删除
     */
    public boolean isDeleted() {
        return changeType == ChangeType.DELETED;
    }

    /**
     * 获取当前值（新值或旧值）
     *
     * @return 当前值
     */
    public String getCurrentValue() {
        return isDeleted() ? oldValue : newValue;
    }

    /**
     * 检查值是否实际发生了变化
     *
     * @return 是否变化
     */
    public boolean hasValueChanged() {
        return !Objects.equals(oldValue, newValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigChangeEvent that = (ConfigChangeEvent) o;
        return Objects.equals(key, that.key) &&
               Objects.equals(oldValue, that.oldValue) &&
               Objects.equals(newValue, that.newValue) &&
               changeType == that.changeType &&
               Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, oldValue, newValue, changeType, source);
    }

    @Override
    public String toString() {
        return String.format("ConfigChangeEvent{key='%s', changeType=%s, oldValue='%s', newValue='%s', source='%s', changeTime=%s}",
                           key, changeType.getDescription(), oldValue, newValue, source, changeTime);
    }
}