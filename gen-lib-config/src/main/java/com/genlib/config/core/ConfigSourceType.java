package com.genlib.config.core;

/**
 * 配置源类型枚举
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public enum ConfigSourceType {

    /** Properties文件 */
    PROPERTIES("properties", "Properties配置文件"),

    /** YAML文件 */
    YAML("yaml", "YAML配置文件"),

    /** JSON文件 */
    JSON("json", "JSON配置文件"),

    /** 系统属性 */
    SYSTEM_PROPERTIES("system-properties", "系统属性"),

    /** 环境变量 */
    ENVIRONMENT("environment", "环境变量"),

    /** Nacos配置中心 */
    NACOS("nacos", "Nacos配置中心"),

    /** Consul配置中心 */
    CONSUL("consul", "Consul配置中心"),

    /** Apollo配置中心 */
    APOLLO("apollo", "Apollo配置中心"),

    /** 数据库配置 */
    DATABASE("database", "数据库配置"),

    /** 内存配置 */
    MEMORY("memory", "内存配置"),

    /** 自定义配置 */
    CUSTOM("custom", "自定义配置源");

    /** 类型代码 */
    private final String code;

    /** 类型描述 */
    private final String description;

    /**
     * 构造方法
     *
     * @param code 类型代码
     * @param description 类型描述
     */
    ConfigSourceType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取类型代码
     *
     * @return 类型代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取类型描述
     *
     * @return 类型描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取配置源类型
     *
     * @param code 类型代码
     * @return 配置源类型
     */
    public static ConfigSourceType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        
        for (ConfigSourceType type : values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return type;
            }
        }
        
        return null;
    }

    /**
     * 检查是否为文件类型配置源
     *
     * @return 是否为文件类型
     */
    public boolean isFileType() {
        return this == PROPERTIES || this == YAML || this == JSON;
    }

    /**
     * 检查是否为远程配置源
     *
     * @return 是否为远程配置源
     */
    public boolean isRemoteType() {
        return this == NACOS || this == CONSUL || this == APOLLO || this == DATABASE;
    }

    /**
     * 检查是否为系统配置源
     *
     * @return 是否为系统配置源
     */
    public boolean isSystemType() {
        return this == SYSTEM_PROPERTIES || this == ENVIRONMENT;
    }

    @Override
    public String toString() {
        return String.format("ConfigSourceType{code='%s', description='%s'}", code, description);
    }
}