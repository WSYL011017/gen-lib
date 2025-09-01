package com.genlib.utils.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
// Jackson TypeReference与FastJSON2冲突，使用全限定名
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 * 基于FastJSON2和Jackson提供JSON操作功能
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public final class JsonUtils {

    /** Jackson ObjectMapper实例 */
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private JsonUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 创建ObjectMapper实例
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 注册Java时间模块
        mapper.registerModule(new JavaTimeModule());
        // 禁用时间戳格式
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略未知属性
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    // ================== FastJSON2 方法 ==================

    /**
     * 对象转JSON字符串（FastJSON2）
     *
     * @param object 对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    /**
     * 对象转格式化的JSON字符串（FastJSON2）
     *
     * @param object 对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return JSON.toJSONString(object, com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to pretty JSON", e);
        }
    }

    /**
     * JSON字符串转对象（FastJSON2）
     *
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to object", e);
        }
    }

    /**
     * JSON字符串转对象（FastJSON2，支持泛型）
     *
     * @param json JSON字符串
     * @param typeReference 类型引用
     * @param <T> 泛型类型
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to object", e);
        }
    }

    /**
     * JSON字符串转List（FastJSON2）
     *
     * @param json JSON字符串
     * @param clazz 元素类型
     * @param <T> 泛型类型
     * @return List对象
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseArray(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to list", e);
        }
    }

    /**
     * JSON字符串转Map
     *
     * @param json JSON字符串
     * @return Map对象
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to map", e);
        }
    }

    /**
     * 解析JSON字符串为JSONObject
     *
     * @param json JSON字符串
     * @return JSONObject
     */
    public static JSONObject parseObject(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to JSONObject", e);
        }
    }

    /**
     * 解析JSON字符串为JSONArray
     *
     * @param json JSON字符串
     * @return JSONArray
     */
    public static JSONArray parseArray(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseArray(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to JSONArray", e);
        }
    }

    // ================== Jackson 方法 ==================

    /**
     * 对象转JSON字符串（Jackson）
     *
     * @param object 对象
     * @return JSON字符串
     */
    public static String toJsonByJackson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to JSON using Jackson", e);
        }
    }

    /**
     * 对象转格式化的JSON字符串（Jackson）
     *
     * @param object 对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyJsonByJackson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to pretty JSON using Jackson", e);
        }
    }

    /**
     * JSON字符串转对象（Jackson）
     *
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 对象
     */
    public static <T> T fromJsonByJackson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to object using Jackson", e);
        }
    }

    /**
     * JSON字符串转对象（Jackson，支持泛型）
     *
     * @param json JSON字符串
     * @param typeReference 类型引用
     * @param <T> 泛型类型
     * @return 对象
     */
    public static <T> T fromJsonByJackson(String json, com.fasterxml.jackson.core.type.TypeReference<T> typeReference) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to object using Jackson", e);
        }
    }

    // ================== 通用工具方法 ==================

    /**
     * 判断字符串是否为有效的JSON
     *
     * @param json JSON字符串
     * @return 是否为有效JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        try {
            JSON.parse(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为有效的JSON对象
     *
     * @param json JSON字符串
     * @return 是否为有效JSON对象
     */
    public static boolean isValidJsonObject(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        try {
            Object obj = JSON.parse(json);
            return obj instanceof JSONObject;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为有效的JSON数组
     *
     * @param json JSON字符串
     * @return 是否为有效JSON数组
     */
    public static boolean isValidJsonArray(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        try {
            Object obj = JSON.parse(json);
            return obj instanceof JSONArray;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 深拷贝对象（通过JSON序列化和反序列化）
     *
     * @param object 源对象
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 拷贝后的对象
     */
    public static <T> T deepCopy(Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        }
        String json = toJson(object);
        return fromJson(json, clazz);
    }

    /**
     * 对象转换（通过JSON）
     *
     * @param source 源对象
     * @param targetClass 目标类型
     * @param <T> 泛型类型
     * @return 转换后的对象
     */
    public static <T> T convertValue(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.convertValue(source, targetClass);
        } catch (Exception e) {
            // 如果Jackson转换失败，尝试通过JSON字符串转换
            String json = toJson(source);
            return fromJson(json, targetClass);
        }
    }

    /**
     * 合并JSON对象
     *
     * @param json1 JSON字符串1
     * @param json2 JSON字符串2
     * @return 合并后的JSON字符串
     */
    public static String mergeJson(String json1, String json2) {
        if (json1 == null || json1.trim().isEmpty()) {
            return json2;
        }
        if (json2 == null || json2.trim().isEmpty()) {
            return json1;
        }
        
        try {
            JSONObject obj1 = parseObject(json1);
            JSONObject obj2 = parseObject(json2);
            
            if (obj1 == null) {
                return json2;
            }
            if (obj2 == null) {
                return json1;
            }
            
            // 将obj2的键值对合并到obj1中
            obj2.forEach(obj1::put);
            
            return toJson(obj1);
        } catch (Exception e) {
            throw new RuntimeException("Failed to merge JSON objects", e);
        }
    }

    /**
     * 压缩JSON字符串（移除不必要的空白字符）
     *
     * @param json JSON字符串
     * @return 压缩后的JSON字符串
     */
    public static String compactJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return json;
        }
        try {
            Object obj = JSON.parse(json);
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compact JSON", e);
        }
    }

    /**
     * 格式化JSON字符串
     *
     * @param json JSON字符串
     * @return 格式化后的JSON字符串
     */
    public static String formatJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return json;
        }
        try {
            Object obj = JSON.parse(json);
            return JSON.toJSONString(obj, com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat);
        } catch (Exception e) {
            throw new RuntimeException("Failed to format JSON", e);
        }
    }

    /**
     * 获取ObjectMapper实例
     *
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}