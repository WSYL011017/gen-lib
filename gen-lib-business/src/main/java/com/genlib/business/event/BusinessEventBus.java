package com.genlib.business.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 业务事件总线
 * 提供事件的发布和订阅功能
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class BusinessEventBus {

    private static final Logger logger = LoggerFactory.getLogger(BusinessEventBus.class);

    private final String name;
    private final Map<String, List<EventHandler<?>>> handlers;
    private final ExecutorService executorService;
    private final boolean asyncEnabled;

    public BusinessEventBus(String name) {
        this(name, false);
    }

    public BusinessEventBus(String name, boolean asyncEnabled) {
        this.name = name;
        this.handlers = new ConcurrentHashMap<>();
        this.asyncEnabled = asyncEnabled;
        this.executorService = asyncEnabled ? 
            Executors.newCachedThreadPool(r -> {
                Thread thread = new Thread(r, "EventBus-" + name + "-");
                thread.setDaemon(true);
                return thread;
            }) : null;
    }

    /**
     * 订阅事件
     */
    public <T extends BusinessEvent> void subscribe(String eventType, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
        logger.debug("事件总线[{}] 订阅事件: {} -> {}", name, eventType, handler.getClass().getSimpleName());
    }

    /**
     * 取消订阅
     */
    public <T extends BusinessEvent> void unsubscribe(String eventType, EventHandler<T> handler) {
        List<EventHandler<?>> eventHandlers = handlers.get(eventType);
        if (eventHandlers != null) {
            eventHandlers.remove(handler);
            logger.debug("事件总线[{}] 取消订阅事件: {} -> {}", name, eventType, handler.getClass().getSimpleName());
        }
    }

    /**
     * 发布事件
     */
    public void publish(BusinessEvent event) {
        String eventType = event.getEventType();
        List<EventHandler<?>> eventHandlers = handlers.get(eventType);
        
        if (eventHandlers == null || eventHandlers.isEmpty()) {
            logger.debug("事件总线[{}] 没有找到事件[{}]的处理器", name, eventType);
            return;
        }

        logger.debug("事件总线[{}] 发布事件: {} -> {} 个处理器", name, eventType, eventHandlers.size());

        for (EventHandler<?> handler : eventHandlers) {
            if (asyncEnabled) {
                executorService.submit(() -> handleEvent(event, handler));
            } else {
                handleEvent(event, handler);
            }
        }
    }

    /**
     * 处理事件
     */
    @SuppressWarnings("unchecked")
    private void handleEvent(BusinessEvent event, EventHandler<?> handler) {
        try {
            long startTime = System.currentTimeMillis();
            
            EventHandler<BusinessEvent> eventHandler = (EventHandler<BusinessEvent>) handler;
            eventHandler.handle(event);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("事件处理完成: {} -> {} ({}ms)", 
                event.getEventType(), handler.getClass().getSimpleName(), duration);
                
        } catch (Exception e) {
            logger.error("事件处理失败: {} -> {}", 
                event.getEventType(), handler.getClass().getSimpleName(), e);
        }
    }

    /**
     * 获取事件订阅数量
     */
    public int getSubscriberCount(String eventType) {
        List<EventHandler<?>> eventHandlers = handlers.get(eventType);
        return eventHandlers != null ? eventHandlers.size() : 0;
    }

    /**
     * 获取总订阅数量
     */
    public int getTotalSubscriberCount() {
        return handlers.values().stream().mapToInt(List::size).sum();
    }

    /**
     * 清除所有订阅
     */
    public void clearSubscribers() {
        handlers.clear();
        logger.debug("事件总线[{}] 清除所有订阅", name);
    }

    /**
     * 清除指定事件类型的订阅
     */
    public void clearSubscribers(String eventType) {
        handlers.remove(eventType);
        logger.debug("事件总线[{}] 清除事件[{}]的所有订阅", name, eventType);
    }

    /**
     * 关闭事件总线
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            logger.info("事件总线[{}] 已关闭", name);
        }
    }

    /**
     * 获取事件总线名称
     */
    public String getName() {
        return name;
    }

    /**
     * 是否启用异步处理
     */
    public boolean isAsyncEnabled() {
        return asyncEnabled;
    }
}