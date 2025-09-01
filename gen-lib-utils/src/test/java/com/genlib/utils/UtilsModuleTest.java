package com.genlib.utils;

import com.genlib.utils.text.StringUtils;
import com.genlib.utils.time.DateUtils;
import com.genlib.utils.json.JsonUtils;
import com.genlib.utils.crypto.AESUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工具类模块测试
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
class UtilsModuleTest {

    @Test
    void testStringUtils() {
        // 测试基础判断
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("test"));

        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank("   "));
        assertFalse(StringUtils.isBlank("test"));

        // 测试安全操作
        assertEquals("", StringUtils.nullToEmpty(null));
        assertEquals("test", StringUtils.nullToEmpty("test"));
        assertEquals("default", StringUtils.defaultIfEmpty("", "default"));
        assertEquals("test", StringUtils.defaultIfEmpty("test", "default"));

        // 测试字符串操作
        assertEquals("test", StringUtils.left("testing", 4));
        assertEquals("ting", StringUtils.right("testing", 4));
        assertEquals("a,b,c", StringUtils.join(",", "a", "b", "c"));

        // 测试命名转换
        assertEquals("userName", StringUtils.toCamelCase("user_name", "_"));
        assertEquals("UserName", StringUtils.toPascalCase("user_name", "_"));
        assertEquals("user_name", StringUtils.toUnderscoreCase("userName"));
        assertEquals("user-name", StringUtils.toKebabCase("userName"));

        // 测试脱敏
        assertEquals("138****5678", StringUtils.maskMobile("13812345678"));
        assertEquals("t***t@example.com", StringUtils.maskEmail("test@example.com"));
        assertEquals("1234**********5678", StringUtils.maskIdCard("123456789012345678"));
    }

    @Test
    void testDateUtils() {
        // 测试当前时间
        assertNotNull(DateUtils.now());
        assertNotNull(DateUtils.today());
        assertNotNull(DateUtils.currentTime());

        // 测试格式化
        LocalDateTime now = LocalDateTime.of(2023, 12, 25, 10, 30, 45);
        assertEquals("2023-12-25 10:30:45", DateUtils.formatDateTime(now));
        assertEquals("2023-12-25", DateUtils.formatDate(now.toLocalDate()));
        assertEquals("10:30:45", DateUtils.formatTime(now.toLocalTime()));

        // 测试解析
        LocalDateTime parsed = DateUtils.parseDateTime("2023-12-25 10:30:45");
        assertEquals(now, parsed);

        // 测试时间计算
        LocalDateTime future = DateUtils.plusDays(now, 7);
        assertEquals(7, DateUtils.daysBetween(now.toLocalDate(), future.toLocalDate()));

        // 测试特殊日期
        LocalDate date = LocalDate.of(2023, 12, 15);
        assertEquals(LocalDate.of(2023, 12, 1), DateUtils.getFirstDayOfMonth(date));
        assertEquals(LocalDate.of(2023, 12, 31), DateUtils.getLastDayOfMonth(date));

        // 测试闰年判断
        assertTrue(DateUtils.isLeapYear(2024));
        assertFalse(DateUtils.isLeapYear(2023));
    }

    @Test
    void testJsonUtils() {
        // 测试对象转JSON
        Map<String, Object> map = new HashMap<>();
        map.put("name", "test");
        map.put("age", 25);
        map.put("active", true);

        String json = JsonUtils.toJson(map);
        assertNotNull(json);
        assertTrue(JsonUtils.isValidJson(json));
        assertTrue(JsonUtils.isValidJsonObject(json));

        // 测试JSON转对象
        Map<String, Object> parsedMap = JsonUtils.fromJsonToMap(json);
        assertEquals("test", parsedMap.get("name"));
        assertEquals(25, parsedMap.get("age"));
        assertEquals(true, parsedMap.get("active"));

        // 测试JSON数组
        List<String> list = Arrays.asList("item1", "item2", "item3");
        String arrayJson = JsonUtils.toJson(list);
        assertTrue(JsonUtils.isValidJsonArray(arrayJson));

        List<String> parsedList = JsonUtils.fromJsonToList(arrayJson, String.class);
        assertEquals(3, parsedList.size());
        assertEquals("item1", parsedList.get(0));

        // 测试格式化
        String formatted = JsonUtils.formatJson(json);
        assertNotNull(formatted);
        assertTrue(formatted.length() > json.length()); // 格式化后应该更长

        // 测试压缩
        String compacted = JsonUtils.compactJson(formatted);
        assertNotNull(compacted);
    }

    @Test
    void testAESUtils() {
        String plaintext = "Hello, World! 这是一个测试消息。";
        String password = "mySecretPassword123";

        // 测试加密解密
        String encrypted = AESUtils.encrypt(plaintext, password);
        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);

        String decrypted = AESUtils.decrypt(encrypted, password);
        assertEquals(plaintext, decrypted);

        // 测试密钥生成
        String key = AESUtils.generateKey();
        assertNotNull(key);
        assertTrue(AESUtils.isValidKey(key));

        // 测试GCM模式
        String encryptedGCM = AESUtils.encryptGCM(plaintext, key);
        assertNotNull(encryptedGCM);
        
        String decryptedGCM = AESUtils.decryptGCM(encryptedGCM, key);
        assertEquals(plaintext, decryptedGCM);

        // 测试盐值生成
        String salt = AESUtils.generateSalt(16);
        assertNotNull(salt);

        // 测试密钥派生
        String derivedKey = AESUtils.deriveKey(password, salt, 10000);
        assertTrue(AESUtils.isValidKey(derivedKey));

        // 测试安全比较
        assertTrue(AESUtils.safeEquals("test", "test"));
        assertFalse(AESUtils.safeEquals("test", "different"));
        assertFalse(AESUtils.safeEquals("test", null));
        assertTrue(AESUtils.safeEquals(null, null));
    }

    @Test
    void testJsonUtilsEdgeCases() {
        // 测试null值处理
        assertNull(JsonUtils.toJson(null));
        assertNull(JsonUtils.fromJson(null, String.class));
        assertNull(JsonUtils.fromJsonToMap(null));
        assertNull(JsonUtils.fromJsonToList(null, String.class));

        // 测试空字符串处理
        assertNull(JsonUtils.fromJson("", String.class));
        assertNull(JsonUtils.fromJson("   ", String.class));

        // 测试无效JSON
        assertFalse(JsonUtils.isValidJson("invalid json"));
        assertFalse(JsonUtils.isValidJsonObject("[1,2,3]")); // 数组不是对象
        assertFalse(JsonUtils.isValidJsonArray("{\"key\":\"value\"}")); // 对象不是数组
    }

    @Test
    void testAESUtilsEdgeCases() {
        // 测试null值处理
        assertNull(AESUtils.encrypt(null, "password"));
        assertNull(AESUtils.encrypt("text", null));
        assertNull(AESUtils.decrypt(null, "password"));
        assertNull(AESUtils.decrypt("encrypted", null));

        // 测试无效密钥
        assertFalse(AESUtils.isValidKey("invalid"));
        assertFalse(AESUtils.isValidKey(null));

        // 测试不同密码解密（应该失败）
        String plaintext = "test message";
        String encrypted = AESUtils.encrypt(plaintext, "password1");
        
        assertThrows(RuntimeException.class, () -> {
            AESUtils.decrypt(encrypted, "password2");
        });
    }
}