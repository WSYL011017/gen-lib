package com.genlib.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genlib.web.filter.RequestResponseLoggingFilter;
import com.genlib.web.interceptor.RequestLogInterceptor;
import com.genlib.web.response.UnifiedResponseHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web模块配置类
 * 配置Web相关的组件
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "genlib.web", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置统一响应处理器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "genlib.web", name = "unified-response-enabled", havingValue = "true", matchIfMissing = true)
    public UnifiedResponseHandler unifiedResponseHandler(ObjectMapper objectMapper) {
        return new UnifiedResponseHandler(objectMapper);
    }

    /**
     * 配置请求日志拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "genlib.web", name = "request-log-enabled", havingValue = "true", matchIfMissing = false)
    public RequestLogInterceptor requestLogInterceptor() {
        return new RequestLogInterceptor();
    }

    /**
     * 配置请求响应日志过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "genlib.web", name = "request-log-enabled", havingValue = "true", matchIfMissing = false)
    public FilterRegistrationBean<RequestResponseLoggingFilter> requestResponseLoggingFilter() {
        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = 
            new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestResponseLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册请求日志拦截器
        RequestLogInterceptor requestLogInterceptor = getRequestLogInterceptor();
        if (requestLogInterceptor != null) {
            registry.addInterceptor(requestLogInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/error"
                    );
        }
    }

    /**
     * 获取请求日志拦截器Bean（如果存在）
     */
    private RequestLogInterceptor getRequestLogInterceptor() {
        try {
            return requestLogInterceptor();
        } catch (Exception e) {
            return null;
        }
    }
}