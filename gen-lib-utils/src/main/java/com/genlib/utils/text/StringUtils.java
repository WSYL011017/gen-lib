package com.genlib.utils.text;

import com.genlib.core.constants.CommonConstants;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 字符串工具类
 * 提供常用的字符串操作方法
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ================== 基础判断 ==================

    /**
     * 判断字符串是否为空（null或空字符串）
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白（null、空字符串或只包含空白字符）
     *
     * @param str 字符串
     * @return 是否为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空白
     *
     * @param str 字符串
     * @return 是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断所有字符串是否都为空
     *
     * @param strings 字符串数组
     * @return 是否都为空
     */
    public static boolean isAllEmpty(String... strings) {
        if (strings == null || strings.length == 0) {
            return true;
        }
        return Arrays.stream(strings).allMatch(StringUtils::isEmpty);
    }

    /**
     * 判断是否有任意字符串为空
     *
     * @param strings 字符串数组
     * @return 是否有空字符串
     */
    public static boolean isAnyEmpty(String... strings) {
        if (strings == null || strings.length == 0) {
            return true;
        }
        return Arrays.stream(strings).anyMatch(StringUtils::isEmpty);
    }

    // ================== 安全操作 ==================

    /**
     * 安全的字符串转换，null转为空字符串
     *
     * @param str 字符串
     * @return 非null字符串
     */
    public static String nullToEmpty(String str) {
        return str == null ? CommonConstants.EMPTY_STRING : str;
    }

    /**
     * 安全的字符串转换，空字符串转为null
     *
     * @param str 字符串
     * @return 字符串或null
     */
    public static String emptyToNull(String str) {
        return isEmpty(str) ? null : str;
    }

    /**
     * 获取默认值，如果字符串为空则返回默认值
     *
     * @param str 字符串
     * @param defaultValue 默认值
     * @return 字符串或默认值
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * 获取默认值，如果字符串为空白则返回默认值
     *
     * @param str 字符串
     * @param defaultValue 默认值
     * @return 字符串或默认值
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }

    // ================== 字符串操作 ==================

    /**
     * 安全的字符串截取
     *
     * @param str 字符串
     * @param start 开始位置
     * @return 截取后的字符串
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return CommonConstants.EMPTY_STRING;
        }
        return str.substring(start);
    }

    /**
     * 安全的字符串截取
     *
     * @param str 字符串
     * @param start 开始位置
     * @param end 结束位置
     * @return 截取后的字符串
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start = 0;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return CommonConstants.EMPTY_STRING;
        }
        return str.substring(start, end);
    }

    /**
     * 左侧截取指定长度
     *
     * @param str 字符串
     * @param length 长度
     * @return 截取后的字符串
     */
    public static String left(String str, int length) {
        if (str == null) {
            return null;
        }
        if (length < 0) {
            return CommonConstants.EMPTY_STRING;
        }
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0, length);
    }

    /**
     * 右侧截取指定长度
     *
     * @param str 字符串
     * @param length 长度
     * @return 截取后的字符串
     */
    public static String right(String str, int length) {
        if (str == null) {
            return null;
        }
        if (length < 0) {
            return CommonConstants.EMPTY_STRING;
        }
        if (str.length() <= length) {
            return str;
        }
        return str.substring(str.length() - length);
    }

    /**
     * 字符串连接
     *
     * @param strings 字符串数组
     * @return 连接后的字符串
     */
    public static String concat(String... strings) {
        if (strings == null || strings.length == 0) {
            return CommonConstants.EMPTY_STRING;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : strings) {
            if (str != null) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    /**
     * 使用分隔符连接字符串
     *
     * @param delimiter 分隔符
     * @param strings 字符串数组
     * @return 连接后的字符串
     */
    public static String join(String delimiter, String... strings) {
        if (strings == null || strings.length == 0) {
            return CommonConstants.EMPTY_STRING;
        }
        return String.join(nullToEmpty(delimiter), strings);
    }

    /**
     * 使用分隔符连接集合
     *
     * @param delimiter 分隔符
     * @param collection 字符串集合
     * @return 连接后的字符串
     */
    public static String join(String delimiter, Collection<String> collection) {
        if (collection == null || collection.isEmpty()) {
            return CommonConstants.EMPTY_STRING;
        }
        return String.join(nullToEmpty(delimiter), collection);
    }

    // ================== 字符串比较 ==================

    /**
     * 安全的字符串比较（忽略大小写）
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 是否相等
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return Objects.equals(str1, str2) || 
               (str1 != null && str2 != null && str1.equalsIgnoreCase(str2));
    }

    /**
     * 安全的字符串比较
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 是否相等
     */
    public static boolean equals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    /**
     * 检查字符串是否包含指定子字符串（忽略大小写）
     *
     * @param str 字符串
     * @param searchStr 搜索字符串
     * @return 是否包含
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }

    // ================== 字符串转换 ==================

    /**
     * 转换为驼峰命名法
     *
     * @param str 字符串
     * @param delimiter 分隔符
     * @return 驼峰命名字符串
     */
    public static String toCamelCase(String str, String delimiter) {
        if (isBlank(str)) {
            return str;
        }
        String[] words = str.split(Pattern.quote(delimiter));
        StringBuilder result = new StringBuilder(words[0].toLowerCase());
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            if (isNotBlank(word)) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 转换为帕斯卡命名法（首字母大写的驼峰命名）
     *
     * @param str 字符串
     * @param delimiter 分隔符
     * @return 帕斯卡命名字符串
     */
    public static String toPascalCase(String str, String delimiter) {
        String camelCase = toCamelCase(str, delimiter);
        if (isBlank(camelCase)) {
            return camelCase;
        }
        return Character.toUpperCase(camelCase.charAt(0)) + camelCase.substring(1);
    }

    /**
     * 转换为下划线命名法
     *
     * @param str 驼峰命名字符串
     * @return 下划线命名字符串
     */
    public static String toUnderscoreCase(String str) {
        if (isBlank(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append(CommonConstants.UNDERSCORE);
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString();
    }

    /**
     * 转换为短横线命名法
     *
     * @param str 驼峰命名字符串
     * @return 短横线命名字符串
     */
    public static String toKebabCase(String str) {
        if (isBlank(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append(CommonConstants.HYPHEN);
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString();
    }

    // ================== 字符串清理 ==================

    /**
     * 移除所有空白字符
     *
     * @param str 字符串
     * @return 清理后的字符串
     */
    public static String removeWhitespace(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\\s+", "");
    }

    /**
     * 移除开头和结尾的指定字符
     *
     * @param str 字符串
     * @param stripChars 要移除的字符
     * @return 清理后的字符串
     */
    public static String strip(String str, String stripChars) {
        if (str == null || stripChars == null) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    /**
     * 移除开头的指定字符
     *
     * @param str 字符串
     * @param stripChars 要移除的字符
     * @return 清理后的字符串
     */
    public static String stripStart(String str, String stripChars) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (stripChars == null || stripChars.isEmpty()) {
            return str;
        }
        int start = 0;
        while (start < str.length() && stripChars.indexOf(str.charAt(start)) != -1) {
            start++;
        }
        return str.substring(start);
    }

    /**
     * 移除结尾的指定字符
     *
     * @param str 字符串
     * @param stripChars 要移除的字符
     * @return 清理后的字符串
     */
    public static String stripEnd(String str, String stripChars) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (stripChars == null || stripChars.isEmpty()) {
            return str;
        }
        int end = str.length();
        while (end > 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
            end--;
        }
        return str.substring(0, end);
    }

    // ================== 字符串掩码 ==================

    /**
     * 手机号脱敏
     *
     * @param mobile 手机号
     * @return 脱敏后的手机号
     */
    public static String maskMobile(String mobile) {
        if (isBlank(mobile) || mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    /**
     * 邮箱脱敏
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (isBlank(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (username.length() <= 2) {
            return email;
        }
        
        String maskedUsername = username.charAt(0) + "***" + username.charAt(username.length() - 1);
        return maskedUsername + domain;
    }

    /**
     * 身份证号脱敏
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCard(String idCard) {
        if (isBlank(idCard) || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }

    // ================== 字符串生成 ==================

    /**
     * 重复字符串
     *
     * @param str 字符串
     * @param count 重复次数
     * @return 重复后的字符串
     */
    public static String repeat(String str, int count) {
        if (str == null || count <= 0) {
            return CommonConstants.EMPTY_STRING;
        }
        return str.repeat(count);
    }

    /**
     * 左填充
     *
     * @param str 字符串
     * @param size 目标长度
     * @param padChar 填充字符
     * @return 填充后的字符串
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        return repeat(String.valueOf(padChar), pads) + str;
    }

    /**
     * 右填充
     *
     * @param str 字符串
     * @param size 目标长度
     * @param padChar 填充字符
     * @return 填充后的字符串
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        return str + repeat(String.valueOf(padChar), pads);
    }
}