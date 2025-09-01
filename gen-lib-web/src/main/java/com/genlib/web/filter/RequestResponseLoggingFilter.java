package com.genlib.web.filter;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求响应日志过滤器
 * 缓存请求和响应内容，用于日志记录
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        // 包装请求和响应以便缓存内容
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // 复制响应内容到原始响应中
            wrappedResponse.copyBodyToResponse();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // 排除静态资源和管理端点
        return uri.startsWith("/actuator") 
               || uri.startsWith("/swagger") 
               || uri.startsWith("/api-docs")
               || uri.startsWith("/webjars")
               || uri.equals("/favicon.ico")
               || uri.startsWith("/error");
    }
}