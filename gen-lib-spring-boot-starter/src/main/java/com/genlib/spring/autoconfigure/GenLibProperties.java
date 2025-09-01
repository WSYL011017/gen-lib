package com.genlib.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Gen-Lib配置属性
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "genlib")
public class GenLibProperties {

    /**
     * 是否启用Gen-Lib
     */
    private boolean enabled = true;

    /**
     * 配置管理属性
     */
    private ConfigProperties config = new ConfigProperties();

    /**
     * 缓存配置属性
     */
    private CacheProperties cache = new CacheProperties();

    /**
     * 工具类配置属性
     */
    private UtilsProperties utils = new UtilsProperties();

    /**
     * 数据访问配置属性
     */
    private DataProperties data = new DataProperties();

    /**
     * Web配置属性
     */
    private WebProperties web = new WebProperties();

    /**
     * 安全配置属性
     */
    private SecurityProperties security = new SecurityProperties();

    /**
     * 监控配置属性
     */
    private MonitorProperties monitor = new MonitorProperties();

    // ================== Getters and Setters ==================

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ConfigProperties getConfig() {
        return config;
    }

    public void setConfig(ConfigProperties config) {
        this.config = config;
    }

    public CacheProperties getCache() {
        return cache;
    }

    public void setCache(CacheProperties cache) {
        this.cache = cache;
    }

    public UtilsProperties getUtils() {
        return utils;
    }

    public void setUtils(UtilsProperties utils) {
        this.utils = utils;
    }

    public DataProperties getData() {
        return data;
    }

    public void setData(DataProperties data) {
        this.data = data;
    }

    public WebProperties getWeb() {
        return web;
    }

    public void setWeb(WebProperties web) {
        this.web = web;
    }

    public SecurityProperties getSecurity() {
        return security;
    }

    public void setSecurity(SecurityProperties security) {
        this.security = security;
    }

    public MonitorProperties getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorProperties monitor) {
        this.monitor = monitor;
    }

    // ================== 内部配置类 ==================

    /**
     * 配置管理属性
     */
    public static class ConfigProperties {
        /**
         * 是否启用配置管理
         */
        private boolean enabled = true;

        /**
         * 配置文件位置
         */
        private List<String> locations = new ArrayList<>();

        /**
         * 是否启用配置缓存
         */
        private boolean cacheEnabled = true;

        /**
         * 配置刷新间隔（秒）
         */
        private int refreshInterval = 60;

        /**
         * 是否启用配置加密
         */
        private boolean encryptionEnabled = false;

        /**
         * 加密密钥
         */
        private String encryptionKey;

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getLocations() {
            return locations;
        }

        public void setLocations(List<String> locations) {
            this.locations = locations;
        }

        public boolean isCacheEnabled() {
            return cacheEnabled;
        }

        public void setCacheEnabled(boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }

        public int getRefreshInterval() {
            return refreshInterval;
        }

        public void setRefreshInterval(int refreshInterval) {
            this.refreshInterval = refreshInterval;
        }

        public boolean isEncryptionEnabled() {
            return encryptionEnabled;
        }

        public void setEncryptionEnabled(boolean encryptionEnabled) {
            this.encryptionEnabled = encryptionEnabled;
        }

        public String getEncryptionKey() {
            return encryptionKey;
        }

        public void setEncryptionKey(String encryptionKey) {
            this.encryptionKey = encryptionKey;
        }
    }

    /**
     * 缓存配置属性
     */
    public static class CacheProperties {
        /**
         * 是否启用缓存
         */
        private boolean enabled = true;

        /**
         * 缓存类型
         */
        private String type = "caffeine";

        /**
         * 默认过期时间（秒）
         */
        private long defaultExpiration = 3600;

        /**
         * 最大缓存大小
         */
        private long maxSize = 10000;

        /**
         * 序列化器类型
         */
        private String serializerType = "json";

        /**
         * 淘汰策略
         */
        private String evictionPolicy = "lru";

        /**
         * 是否启用统计
         */
        private boolean statsEnabled = false;

        /**
         * 是否启用压缩
         */
        private boolean compressionEnabled = false;

        /**
         * 缓存名称列表
         */
        private List<String> cacheNames = new ArrayList<>();

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getDefaultExpiration() {
            return defaultExpiration;
        }

        public void setDefaultExpiration(long defaultExpiration) {
            this.defaultExpiration = defaultExpiration;
        }

        public long getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(long maxSize) {
            this.maxSize = maxSize;
        }

        public String getSerializerType() {
            return serializerType;
        }

        public void setSerializerType(String serializerType) {
            this.serializerType = serializerType;
        }

        public String getEvictionPolicy() {
            return evictionPolicy;
        }

        public void setEvictionPolicy(String evictionPolicy) {
            this.evictionPolicy = evictionPolicy;
        }

        public boolean isStatsEnabled() {
            return statsEnabled;
        }

        public void setStatsEnabled(boolean statsEnabled) {
            this.statsEnabled = statsEnabled;
        }

        public boolean isCompressionEnabled() {
            return compressionEnabled;
        }

        public void setCompressionEnabled(boolean compressionEnabled) {
            this.compressionEnabled = compressionEnabled;
        }

        public List<String> getCacheNames() {
            return cacheNames;
        }

        public void setCacheNames(List<String> cacheNames) {
            this.cacheNames = cacheNames;
        }
    }

    /**
     * 工具类配置属性
     */
    public static class UtilsProperties {
        /**
         * 是否启用工具类
         */
        private boolean enabled = true;

        /**
         * 日期工具配置
         */
        private DateUtilsConfig dateUtils = new DateUtilsConfig();

        /**
         * 字符串工具配置
         */
        private StringUtilsConfig stringUtils = new StringUtilsConfig();

        /**
         * 加密工具配置
         */
        private CryptoUtilsConfig cryptoUtils = new CryptoUtilsConfig();

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public DateUtilsConfig getDateUtils() {
            return dateUtils;
        }

        public void setDateUtils(DateUtilsConfig dateUtils) {
            this.dateUtils = dateUtils;
        }

        public StringUtilsConfig getStringUtils() {
            return stringUtils;
        }

        public void setStringUtils(StringUtilsConfig stringUtils) {
            this.stringUtils = stringUtils;
        }

        public CryptoUtilsConfig getCryptoUtils() {
            return cryptoUtils;
        }

        public void setCryptoUtils(CryptoUtilsConfig cryptoUtils) {
            this.cryptoUtils = cryptoUtils;
        }

        /**
         * 日期工具配置
         */
        public static class DateUtilsConfig {
            private String defaultTimeZone = "Asia/Shanghai";
            private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";

            public String getDefaultTimeZone() {
                return defaultTimeZone;
            }

            public void setDefaultTimeZone(String defaultTimeZone) {
                this.defaultTimeZone = defaultTimeZone;
            }

            public String getDefaultDateFormat() {
                return defaultDateFormat;
            }

            public void setDefaultDateFormat(String defaultDateFormat) {
                this.defaultDateFormat = defaultDateFormat;
            }
        }

        /**
         * 字符串工具配置
         */
        public static class StringUtilsConfig {
            private String defaultCharset = "UTF-8";
            private boolean enableSensitiveWordFilter = false;

            public String getDefaultCharset() {
                return defaultCharset;
            }

            public void setDefaultCharset(String defaultCharset) {
                this.defaultCharset = defaultCharset;
            }

            public boolean isEnableSensitiveWordFilter() {
                return enableSensitiveWordFilter;
            }

            public void setEnableSensitiveWordFilter(boolean enableSensitiveWordFilter) {
                this.enableSensitiveWordFilter = enableSensitiveWordFilter;
            }
        }

        /**
         * 加密工具配置
         */
        public static class CryptoUtilsConfig {
            private String defaultAesKey;
            private String defaultRsaKeySize = "2048";

            public String getDefaultAesKey() {
                return defaultAesKey;
            }

            public void setDefaultAesKey(String defaultAesKey) {
                this.defaultAesKey = defaultAesKey;
            }

            public String getDefaultRsaKeySize() {
                return defaultRsaKeySize;
            }

            public void setDefaultRsaKeySize(String defaultRsaKeySize) {
                this.defaultRsaKeySize = defaultRsaKeySize;
            }
        }
    }

    /**
     * 数据访问配置属性
     */
    public static class DataProperties {
        /**
         * 是否启用数据访问模块
         */
        private boolean enabled = true;

        /**
         * 主数据源名称
         */
        private String primary = "master";

        /**
         * 是否启用多数据源
         */
        private boolean multiDatasourceEnabled = false;

        /**
         * 是否启用读写分离
         */
        private boolean readWriteSplitEnabled = false;

        /**
         * 分页配置
         */
        private PageConfig page = new PageConfig();

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPrimary() {
            return primary;
        }

        public void setPrimary(String primary) {
            this.primary = primary;
        }

        public boolean isMultiDatasourceEnabled() {
            return multiDatasourceEnabled;
        }

        public void setMultiDatasourceEnabled(boolean multiDatasourceEnabled) {
            this.multiDatasourceEnabled = multiDatasourceEnabled;
        }

        public boolean isReadWriteSplitEnabled() {
            return readWriteSplitEnabled;
        }

        public void setReadWriteSplitEnabled(boolean readWriteSplitEnabled) {
            this.readWriteSplitEnabled = readWriteSplitEnabled;
        }

        public PageConfig getPage() {
            return page;
        }

        public void setPage(PageConfig page) {
            this.page = page;
        }

        /**
         * 分页配置
         */
        public static class PageConfig {
            private int defaultPageSize = 20;
            private int maxPageSize = 1000;
            private boolean reasonable = true;

            public int getDefaultPageSize() {
                return defaultPageSize;
            }

            public void setDefaultPageSize(int defaultPageSize) {
                this.defaultPageSize = defaultPageSize;
            }

            public int getMaxPageSize() {
                return maxPageSize;
            }

            public void setMaxPageSize(int maxPageSize) {
                this.maxPageSize = maxPageSize;
            }

            public boolean isReasonable() {
                return reasonable;
            }

            public void setReasonable(boolean reasonable) {
                this.reasonable = reasonable;
            }
        }
    }

    /**
     * Web配置属性
     */
    public static class WebProperties {
        /**
         * 是否启用Web模块
         */
        private boolean enabled = true;

        /**
         * 是否启用全局异常处理
         */
        private boolean globalExceptionHandlerEnabled = true;

        /**
         * 是否启用统一响应格式
         */
        private boolean unifiedResponseEnabled = true;

        /**
         * 是否启用请求日志
         */
        private boolean requestLogEnabled = false;

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isGlobalExceptionHandlerEnabled() {
            return globalExceptionHandlerEnabled;
        }

        public void setGlobalExceptionHandlerEnabled(boolean globalExceptionHandlerEnabled) {
            this.globalExceptionHandlerEnabled = globalExceptionHandlerEnabled;
        }

        public boolean isUnifiedResponseEnabled() {
            return unifiedResponseEnabled;
        }

        public void setUnifiedResponseEnabled(boolean unifiedResponseEnabled) {
            this.unifiedResponseEnabled = unifiedResponseEnabled;
        }

        public boolean isRequestLogEnabled() {
            return requestLogEnabled;
        }

        public void setRequestLogEnabled(boolean requestLogEnabled) {
            this.requestLogEnabled = requestLogEnabled;
        }
    }

    /**
     * 安全配置属性
     */
    public static class SecurityProperties {
        /**
         * 是否启用安全模块
         */
        private boolean enabled = false;

        /**
         * JWT配置
         */
        private JwtConfig jwt = new JwtConfig();

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public JwtConfig getJwt() {
            return jwt;
        }

        public void setJwt(JwtConfig jwt) {
            this.jwt = jwt;
        }

        /**
         * JWT配置
         */
        public static class JwtConfig {
            private String secret = "genlib-default-secret";
            private long expiration = 86400; // 24小时

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }

            public long getExpiration() {
                return expiration;
            }

            public void setExpiration(long expiration) {
                this.expiration = expiration;
            }
        }
    }

    /**
     * 监控配置属性
     */
    public static class MonitorProperties {
        /**
         * 是否启用监控模块
         */
        private boolean enabled = false;

        /**
         * 是否启用性能监控
         */
        private boolean performanceEnabled = false;

        /**
         * 是否启用健康检查
         */
        private boolean healthCheckEnabled = true;

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isPerformanceEnabled() {
            return performanceEnabled;
        }

        public void setPerformanceEnabled(boolean performanceEnabled) {
            this.performanceEnabled = performanceEnabled;
        }

        public boolean isHealthCheckEnabled() {
            return healthCheckEnabled;
        }

        public void setHealthCheckEnabled(boolean healthCheckEnabled) {
            this.healthCheckEnabled = healthCheckEnabled;
        }
    }
}