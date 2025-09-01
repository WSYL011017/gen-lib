package com.genlib.security.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * 密码工具类
 * 提供密码加密、验证、强度检查等功能
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class PasswordUtils {

    private static final Logger logger = LoggerFactory.getLogger(PasswordUtils.class);

    // PBKDF2算法相关常量
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int DEFAULT_SALT_LENGTH = 32;
    private static final int DEFAULT_ITERATIONS = 10000;
    private static final int DEFAULT_KEY_LENGTH = 256;

    // 密码强度检查正则
    private static final String WEAK_PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    private static final String MEDIUM_PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final String STRONG_PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$";

    /**
     * 使用PBKDF2算法加密密码
     *
     * @param password 原始密码
     * @return 加密后的密码（包含盐值）
     */
    public static String encryptPassword(String password) {
        return encryptPassword(password, generateSalt(), DEFAULT_ITERATIONS);
    }

    /**
     * 使用PBKDF2算法加密密码（自定义盐值）
     *
     * @param password 原始密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    public static String encryptPassword(String password, byte[] salt) {
        return encryptPassword(password, salt, DEFAULT_ITERATIONS);
    }

    /**
     * 使用PBKDF2算法加密密码（自定义盐值和迭代次数）
     *
     * @param password 原始密码
     * @param salt 盐值
     * @param iterations 迭代次数
     * @return 加密后的密码
     */
    public static String encryptPassword(String password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, DEFAULT_KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            
            // 格式：iterations:salt:hash
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);
            
            return iterations + ":" + saltBase64 + ":" + hashBase64;
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("密码加密失败", e);
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     *
     * @param password 原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String password, String encryptedPassword) {
        try {
            String[] parts = encryptedPassword.split(":");
            if (parts.length != 3) {
                logger.warn("密码格式错误: {}", encryptedPassword);
                return false;
            }
            
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);
            
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            byte[] testHash = skf.generateSecret(spec).getEncoded();
            
            return slowEquals(hash, testHash);
            
        } catch (Exception e) {
            logger.error("密码验证失败", e);
            return false;
        }
    }

    /**
     * 生成随机盐值
     *
     * @return 盐值
     */
    public static byte[] generateSalt() {
        return generateSalt(DEFAULT_SALT_LENGTH);
    }

    /**
     * 生成指定长度的随机盐值
     *
     * @param length 长度
     * @return 盐值
     */
    public static byte[] generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 生成随机密码
     *
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        return generateRandomPassword(length, true, true, true, true);
    }

    /**
     * 生成随机密码（自定义字符类型）
     *
     * @param length 密码长度
     * @param includeUppercase 包含大写字母
     * @param includeLowercase 包含小写字母
     * @param includeNumbers 包含数字
     * @param includeSpecial 包含特殊字符
     * @return 随机密码
     */
    public static String generateRandomPassword(int length, boolean includeUppercase, 
                                               boolean includeLowercase, boolean includeNumbers, 
                                               boolean includeSpecial) {
        StringBuilder charset = new StringBuilder();
        
        if (includeUppercase) {
            charset.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }
        if (includeLowercase) {
            charset.append("abcdefghijklmnopqrstuvwxyz");
        }
        if (includeNumbers) {
            charset.append("0123456789");
        }
        if (includeSpecial) {
            charset.append("!@#$%^&*()_+-=[]{}|;:,.<>?");
        }
        
        if (charset.length() == 0) {
            throw new IllegalArgumentException("至少需要选择一种字符类型");
        }
        
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            password.append(charset.charAt(index));
        }
        
        return password.toString();
    }

    /**
     * 检查密码强度
     *
     * @param password 密码
     * @return 密码强度
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return PasswordStrength.VERY_WEAK;
        }
        
        if (password.matches(STRONG_PASSWORD_PATTERN)) {
            return PasswordStrength.STRONG;
        } else if (password.matches(MEDIUM_PASSWORD_PATTERN)) {
            return PasswordStrength.MEDIUM;
        } else if (password.matches(WEAK_PASSWORD_PATTERN)) {
            return PasswordStrength.WEAK;
        } else {
            return PasswordStrength.VERY_WEAK;
        }
    }

    /**
     * 检查密码是否符合最低强度要求
     *
     * @param password 密码
     * @param minStrength 最低强度要求
     * @return 是否符合要求
     */
    public static boolean isPasswordStrengthValid(String password, PasswordStrength minStrength) {
        PasswordStrength strength = checkPasswordStrength(password);
        return strength.getLevel() >= minStrength.getLevel();
    }

    /**
     * 使用MD5算法加密（不推荐用于密码，仅用于数据校验）
     *
     * @param input 输入字符串
     * @return MD5哈希值
     */
    public static String md5(String input) {
        return hash(input, "MD5");
    }

    /**
     * 使用SHA-256算法加密
     *
     * @param input 输入字符串
     * @return SHA-256哈希值
     */
    public static String sha256(String input) {
        return hash(input, "SHA-256");
    }

    /**
     * 使用SHA-512算法加密
     *
     * @param input 输入字符串
     * @return SHA-512哈希值
     */
    public static String sha512(String input) {
        return hash(input, "SHA-512");
    }

    /**
     * 使用指定算法加密
     *
     * @param input 输入字符串
     * @param algorithm 算法名称
     * @return 哈希值
     */
    private static String hash(String input, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("不支持的算法: {}", algorithm, e);
            throw new RuntimeException("不支持的算法: " + algorithm, e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * 安全的数组比较（避免时序攻击）
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    /**
     * 密码强度枚举
     */
    public enum PasswordStrength {
        VERY_WEAK(0, "非常弱"),
        WEAK(1, "弱"),
        MEDIUM(2, "中等"),
        STRONG(3, "强");

        private final int level;
        private final String description;

        PasswordStrength(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return description;
        }
    }
}