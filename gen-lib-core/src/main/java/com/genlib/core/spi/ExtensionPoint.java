package com.genlib.core.spi;

/**
 * 扩展点接口
 * 所有扩展点都应该实现此接口
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public interface ExtensionPoint {

    /**
     * 获取扩展点名称
     *
     * @return 扩展点名称
     */
    String getName();

    /**
     * 获取扩展点优先级（数值越小优先级越高）
     *
     * @return 优先级
     */
    default int getOrder() {
        return Integer.MAX_VALUE;
    }

    /**
     * 是否启用此扩展点
     *
     * @return 是否启用
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * 获取扩展点版本
     *
     * @return 版本号
     */
    default String getVersion() {
        return "1.0.0";
    }

    /**
     * 获取扩展点描述
     *
     * @return 描述信息
     */
    default String getDescription() {
        return "";
    }

    /**
     * 扩展点初始化
     */
    default void initialize() {
        // 默认空实现
    }

    /**
     * 扩展点销毁
     */
    default void destroy() {
        // 默认空实现
    }
}