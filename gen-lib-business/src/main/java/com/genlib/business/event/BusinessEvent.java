package com.genlib.business.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务事件基类
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public abstract class BusinessEvent {

    private static final Logger logger = LoggerFactory.getLogger(BusinessEvent.class);

    private final String eventId;
    private final String eventType;
    private final LocalDateTime occurTime;
    private final Map<String, Object> eventData;
    private String source;
    private String version;

    public BusinessEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.occurTime = LocalDateTime.now();
        this.eventData = new ConcurrentHashMap<>();
        this.version = "1.0";
    }

    public BusinessEvent(String eventType, String source) {
        this(eventType);
        this.source = source;
    }

    /**
     * 添加事件数据
     */
    public BusinessEvent addData(String key, Object value) {
        eventData.put(key, value);
        return this;
    }

    /**
     * 获取事件数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) eventData.get(key);
    }

    /**
     * 获取事件数据（带默认值）
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key, T defaultValue) {
        return (T) eventData.getOrDefault(key, defaultValue);
    }

    // getters and setters
    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public LocalDateTime getOccurTime() { return occurTime; }
    public Map<String, Object> getEventData() { return eventData; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    @Override
    public String toString() {
        return "BusinessEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", occurTime=" + occurTime +
                ", source='" + source + '\'' +
                ", version='" + version + '\'' +
                ", eventData=" + eventData +
                '}';
    }
}