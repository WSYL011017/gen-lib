package com.genlib.core.spi;

import java.util.List;
import java.util.Optional;

/**
 * 扩展点管理器接口
 * 负责管理所有的扩展点
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public interface ExtensionManager {

    /**
     * 注册扩展点
     *
     * @param extensionPoint 扩展点实例
     */
    void register(ExtensionPoint extensionPoint);

    /**
     * 注册扩展点
     *
     * @param extensionClass 扩展点类
     */
    void register(Class<? extends ExtensionPoint> extensionClass);

    /**
     * 注销扩展点
     *
     * @param name 扩展点名称
     */
    void unregister(String name);

    /**
     * 注销扩展点
     *
     * @param extensionClass 扩展点类
     */
    void unregister(Class<? extends ExtensionPoint> extensionClass);

    /**
     * 获取指定类型的扩展点
     *
     * @param extensionClass 扩展点类型
     * @param <T> 扩展点类型
     * @return 扩展点实例
     */
    <T extends ExtensionPoint> Optional<T> getExtension(Class<T> extensionClass);

    /**
     * 获取指定名称的扩展点
     *
     * @param name 扩展点名称
     * @return 扩展点实例
     */
    Optional<ExtensionPoint> getExtension(String name);

    /**
     * 获取指定类型的所有扩展点
     *
     * @param extensionClass 扩展点类型
     * @param <T> 扩展点类型
     * @return 扩展点实例列表
     */
    <T extends ExtensionPoint> List<T> getExtensions(Class<T> extensionClass);

    /**
     * 获取所有扩展点
     *
     * @return 所有扩展点实例
     */
    List<ExtensionPoint> getAllExtensions();

    /**
     * 获取已启用的扩展点
     *
     * @param extensionClass 扩展点类型
     * @param <T> 扩展点类型
     * @return 已启用的扩展点实例列表
     */
    <T extends ExtensionPoint> List<T> getEnabledExtensions(Class<T> extensionClass);

    /**
     * 检查是否存在指定类型的扩展点
     *
     * @param extensionClass 扩展点类型
     * @return 是否存在
     */
    boolean hasExtension(Class<? extends ExtensionPoint> extensionClass);

    /**
     * 检查是否存在指定名称的扩展点
     *
     * @param name 扩展点名称
     * @return 是否存在
     */
    boolean hasExtension(String name);

    /**
     * 加载SPI扩展点
     *
     * @param extensionClass 扩展点类型
     * @param <T> 扩展点类型
     */
    <T extends ExtensionPoint> void loadSpiExtensions(Class<T> extensionClass);

    /**
     * 初始化所有扩展点
     */
    void initializeAll();

    /**
     * 销毁所有扩展点
     */
    void destroyAll();

    /**
     * 获取扩展点数量
     *
     * @return 扩展点数量
     */
    int getExtensionCount();

    /**
     * 清空所有扩展点
     */
    void clear();
}