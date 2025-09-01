package com.genlib.config.core;

/**
 * 配置变更监听器接口
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public interface ConfigChangeListener {

    /**
     * 配置变更事件处理
     *
     * @param event 变更事件
     */
    void onConfigChange(ConfigChangeEvent event);

    /**
     * 获取监听器名称
     *
     * @return 监听器名称
     */
    default String getName() {
        return getClass().getSimpleName();
    }

    /**
     * 获取监听器优先级（数值越小优先级越高）
     *
     * @return 优先级
     */
    default int getOrder() {
        return Integer.MAX_VALUE;
    }

    /**
     * 检查是否感兴趣的配置键
     *
     * @param key 配置键
     * @return 是否感兴趣
     */
    default boolean isInterestedIn(String key) {
        return true;
    }
}