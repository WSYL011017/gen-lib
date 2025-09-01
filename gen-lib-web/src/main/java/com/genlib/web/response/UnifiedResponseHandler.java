package com.genlib.web.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genlib.core.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一响应处理器
 * 自动包装Controller返回值为统一的Result格式
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@RestControllerAdvice(basePackages = {"com.genlib"})
public class UnifiedResponseHandler implements ResponseBodyAdvice<Object> {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedResponseHandler.class);
    
    private final ObjectMapper objectMapper;

    public UnifiedResponseHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果返回类型已经是Result或ResponseEntity，则不需要包装
        Class<?> parameterType = returnType.getParameterType();
        return !Result.class.isAssignableFrom(parameterType) 
               && !ResponseEntity.class.isAssignableFrom(parameterType)
               && !isExcludedPath(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {
        
        try {
            // 包装为Result格式
            Result<Object> result = Result.success(body);
            
            // 如果返回类型是String，需要特殊处理
            if (returnType.getParameterType() == String.class) {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(result);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("统一响应处理失败", e);
            return Result.error("响应处理失败");
        }
    }

    /**
     * 判断是否为排除的路径
     * 一些特殊路径不需要统一响应处理
     */
    private boolean isExcludedPath(MethodParameter returnType) {
        String className = returnType.getContainingClass().getName();
        String methodName = returnType.getMethod().getName();
        
        // 排除Swagger相关
        if (className.contains("swagger") || className.contains("api-docs")) {
            return true;
        }
        
        // 排除Spring Boot Actuator
        if (className.contains("actuator")) {
            return true;
        }
        
        // 排除错误页面
        if (className.contains("BasicErrorController")) {
            return true;
        }
        
        return false;
    }
}