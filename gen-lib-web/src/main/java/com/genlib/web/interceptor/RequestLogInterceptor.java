package com.genlib.web.interceptor;

import com.genlib.utils.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志拦截器
 * 记录请求和响应的详细信息
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogInterceptor.class);
    
    private static final String START_TIME_ATTRIBUTE = "REQUEST_START_TIME";
    private static final String REQUEST_ID_ATTRIBUTE = "REQUEST_ID";
    
    /**
     * 需要排除的路径前缀
     */
    private static final String[] EXCLUDED_PATHS = {
        "/actuator",
        "/swagger",
        "/api-docs",
        "/webjars",
        "/favicon.ico",
        "/error"
    };
    
    /**
     * 需要排除的内容类型
     */
    private static final String[] EXCLUDED_CONTENT_TYPES = {
        "multipart/form-data",
        "application/octet-stream"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 检查是否需要排除
        if (shouldExclude(request)) {
            return true;
        }
        
        // 生成请求ID
        String requestId = generateRequestId();
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        
        // 记录请求信息
        logRequest(request, requestId);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        // 检查是否需要排除
        if (shouldExclude(request)) {
            return;
        }
        
        String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            logResponse(request, response, requestId, duration, ex);
        }
    }

    /**
     * 记录请求信息
     */
    private void logRequest(HttpServletRequest request, String requestId) {
        try {
            RequestLogInfo logInfo = new RequestLogInfo();
            logInfo.setRequestId(requestId);
            logInfo.setMethod(request.getMethod());
            logInfo.setUri(request.getRequestURI());
            logInfo.setQueryString(request.getQueryString());
            logInfo.setRemoteAddr(getClientIpAddress(request));
            logInfo.setUserAgent(request.getHeader("User-Agent"));
            logInfo.setHeaders(getHeaders(request));
            
            // 记录请求参数
            if (!"GET".equalsIgnoreCase(request.getMethod())) {
                logInfo.setParameters(getParameters(request));
                
                // 记录请求体（如果是可缓存的请求）
                if (request instanceof ContentCachingRequestWrapper) {
                    ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                    byte[] content = wrapper.getContentAsByteArray();
                    if (content.length > 0 && !isBinaryContent(request)) {
                        String requestBody = new String(content, StandardCharsets.UTF_8);
                        if (requestBody.length() <= 1000) { // 限制日志大小
                            logInfo.setRequestBody(requestBody);
                        }
                    }
                }
            }
            
            logger.info("请求开始: {}", JsonUtils.toJson(logInfo));
            
        } catch (Exception e) {
            logger.warn("记录请求日志失败: {}", e.getMessage());
        }
    }

    /**
     * 记录响应信息
     */
    private void logResponse(HttpServletRequest request, HttpServletResponse response, 
                           String requestId, long duration, Exception ex) {
        try {
            ResponseLogInfo logInfo = new ResponseLogInfo();
            logInfo.setRequestId(requestId);
            logInfo.setStatus(response.getStatus());
            logInfo.setDuration(duration);
            
            // 如果有异常，记录异常信息
            if (ex != null) {
                logInfo.setException(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
            
            // 记录响应体（如果是可缓存的响应）
            if (response instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                byte[] content = wrapper.getContentAsByteArray();
                if (content.length > 0 && !isBinaryResponse(response)) {
                    String responseBody = new String(content, StandardCharsets.UTF_8);
                    if (responseBody.length() <= 1000) { // 限制日志大小
                        logInfo.setResponseBody(responseBody);
                    }
                }
            }
            
            // 根据响应状态选择日志级别
            if (response.getStatus() >= 400) {
                logger.warn("请求完成: {}", JsonUtils.toJson(logInfo));
            } else {
                logger.info("请求完成: {}", JsonUtils.toJson(logInfo));
            }
            
        } catch (Exception e) {
            logger.warn("记录响应日志失败: {}", e.getMessage());
        }
    }

    /**
     * 判断是否需要排除
     */
    private boolean shouldExclude(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return Arrays.stream(EXCLUDED_PATHS).anyMatch(uri::startsWith);
    }

    /**
     * 判断是否为二进制内容
     */
    private boolean isBinaryContent(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        return Arrays.stream(EXCLUDED_CONTENT_TYPES)
                .anyMatch(type -> contentType.toLowerCase().contains(type));
    }

    /**
     * 判断是否为二进制响应
     */
    private boolean isBinaryResponse(HttpServletResponse response) {
        String contentType = response.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("image/") 
               || contentType.startsWith("video/")
               || contentType.startsWith("audio/")
               || contentType.contains("octet-stream");
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", 
            "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", 
            "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", 
            "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 获取请求头
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // 排除敏感头信息
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        return headers;
    }

    /**
     * 获取请求参数
     */
    private Map<String, String[]> getParameters(HttpServletRequest request) {
        return new HashMap<>(request.getParameterMap());
    }

    /**
     * 判断是否为敏感头信息
     */
    private boolean isSensitiveHeader(String headerName) {
        String lowerHeaderName = headerName.toLowerCase();
        return lowerHeaderName.contains("authorization") 
               || lowerHeaderName.contains("cookie")
               || lowerHeaderName.contains("token");
    }

    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }

    /**
     * 请求日志信息
     */
    public static class RequestLogInfo {
        private String requestId;
        private String method;
        private String uri;
        private String queryString;
        private String remoteAddr;
        private String userAgent;
        private Map<String, String> headers;
        private Map<String, String[]> parameters;
        private String requestBody;

        // getters and setters
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getUri() { return uri; }
        public void setUri(String uri) { this.uri = uri; }
        public String getQueryString() { return queryString; }
        public void setQueryString(String queryString) { this.queryString = queryString; }
        public String getRemoteAddr() { return remoteAddr; }
        public void setRemoteAddr(String remoteAddr) { this.remoteAddr = remoteAddr; }
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        public Map<String, String> getHeaders() { return headers; }
        public void setHeaders(Map<String, String> headers) { this.headers = headers; }
        public Map<String, String[]> getParameters() { return parameters; }
        public void setParameters(Map<String, String[]> parameters) { this.parameters = parameters; }
        public String getRequestBody() { return requestBody; }
        public void setRequestBody(String requestBody) { this.requestBody = requestBody; }
    }

    /**
     * 响应日志信息
     */
    public static class ResponseLogInfo {
        private String requestId;
        private int status;
        private long duration;
        private String exception;
        private String responseBody;

        // getters and setters
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
        public String getException() { return exception; }
        public void setException(String exception) { this.exception = exception; }
        public String getResponseBody() { return responseBody; }
        public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
    }
}