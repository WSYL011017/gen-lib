package com.genlib.business.event;

/**
 * 事件处理器接口
 * 
 * @param <T> 事件类型
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@FunctionalInterface
public interface EventHandler<T extends BusinessEvent> {

    /**
     * 处理事件
     *
     * @param event 业务事件
     */
    void handle(T event);
}