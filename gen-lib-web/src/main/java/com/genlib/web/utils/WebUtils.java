package com.genlib.web.utils;

import com.genlib.utils.json.JsonUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Web工具类
 * 提供Web开发中常用的工具方法
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class WebUtils {

    /**
     * 获取当前请求
     */
    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取当前响应
     */
    public static HttpServletResponse getCurrentResponse() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }

    /**
     * 获取客户端IP地址
     */
    public static String getClientIpAddress() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? getClientIpAddress(request) : null;
    }

    /**
     * 获取客户端IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", 
            "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", 
            "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", 
            "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 处理多个IP的情况，取第一个
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 获取User-Agent
     */
    public static String getUserAgent() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getHeader("User-Agent") : null;
    }

    /**
     * 获取User-Agent
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * 获取请求URL
     */
    public static String getRequestUrl() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? getRequestUrl(request) : null;
    }

    /**
     * 获取请求URL
     */
    public static String getRequestUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }

    /**
     * 获取请求参数Map
     */
    public static Map<String, String> getParameterMap() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? getParameterMap(request) : new HashMap<>();
    }

    /**
     * 获取请求参数Map
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                if (values.length == 1) {
                    paramMap.put(key, values[0]);
                } else {
                    paramMap.put(key, Arrays.toString(values));
                }
            }
        }
        
        return paramMap;
    }

    /**
     * 获取请求头Map
     */
    public static Map<String, String> getHeaderMap() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? getHeaderMap(request) : new HashMap<>();
    }

    /**
     * 获取请求头Map
     */
    public static Map<String, String> getHeaderMap(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headerMap.put(headerName, headerValue);
        }
        
        return headerMap;
    }

    /**
     * 判断是否为Ajax请求
     */
    public static boolean isAjaxRequest() {
        HttpServletRequest request = getCurrentRequest();
        return request != null && isAjaxRequest(request);
    }

    /**
     * 判断是否为Ajax请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String xRequestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(xRequestedWith);
    }

    /**
     * 判断是否为移动端请求
     */
    public static boolean isMobileRequest() {
        HttpServletRequest request = getCurrentRequest();
        return request != null && isMobileRequest(request);
    }

    /**
     * 判断是否为移动端请求
     */
    public static boolean isMobileRequest(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        if (userAgent == null) {
            return false;
        }
        
        String lowerUserAgent = userAgent.toLowerCase();
        return lowerUserAgent.contains("mobile") 
               || lowerUserAgent.contains("android")
               || lowerUserAgent.contains("iphone")
               || lowerUserAgent.contains("ipad")
               || lowerUserAgent.contains("windows phone");
    }

    /**
     * 获取请求方法
     */
    public static String getRequestMethod() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getMethod() : null;
    }

    /**
     * 获取请求URI
     */
    public static String getRequestUri() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getRequestURI() : null;
    }

    /**
     * 获取查询字符串
     */
    public static String getQueryString() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getQueryString() : null;
    }

    /**
     * 输出JSON响应
     */
    public static void writeJsonResponse(HttpServletResponse response, Object data) {
        writeJsonResponse(response, data, 200);
    }

    /**
     * 输出JSON响应
     */
    public static void writeJsonResponse(HttpServletResponse response, Object data, int status) {
        try {
            response.setStatus(status);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            
            PrintWriter writer = response.getWriter();
            writer.write(JsonUtils.toJson(data));
            writer.flush();
            
        } catch (IOException e) {
            throw new RuntimeException("输出JSON响应失败", e);
        }
    }

    /**
     * 输出文本响应
     */
    public static void writeTextResponse(HttpServletResponse response, String text) {
        writeTextResponse(response, text, 200);
    }

    /**
     * 输出文本响应
     */
    public static void writeTextResponse(HttpServletResponse response, String text, int status) {
        try {
            response.setStatus(status);
            response.setContentType("text/plain;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            
            PrintWriter writer = response.getWriter();
            writer.write(text);
            writer.flush();
            
        } catch (IOException e) {
            throw new RuntimeException("输出文本响应失败", e);
        }
    }

    /**
     * 设置响应头
     */
    public static void setResponseHeader(String name, String value) {
        HttpServletResponse response = getCurrentResponse();
        if (response != null) {
            response.setHeader(name, value);
        }
    }

    /**
     * 添加响应头
     */
    public static void addResponseHeader(String name, String value) {
        HttpServletResponse response = getCurrentResponse();
        if (response != null) {
            response.addHeader(name, value);
        }
    }

    /**
     * 设置跨域响应头
     */
    public static void setCorsHeaders() {
        HttpServletResponse response = getCurrentResponse();
        if (response != null) {
            setCorsHeaders(response);
        }
    }

    /**
     * 设置跨域响应头
     */
    public static void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", 
            "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}