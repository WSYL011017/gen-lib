package com.genlib.utils.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * AES加密解密工具类
 * 支持AES-GCM和AES-CBC两种模式
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public final class AESUtils {

    /** AES算法 */
    private static final String AES_ALGORITHM = "AES";
    
    /** AES-GCM算法 */
    private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";
    
    /** AES-CBC算法 */
    private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS5Padding";
    
    /** GCM模式IV长度 */
    private static final int GCM_IV_LENGTH = 12;
    
    /** GCM模式Tag长度 */
    private static final int GCM_TAG_LENGTH = 16;
    
    /** AES密钥长度 */
    private static final int AES_KEY_LENGTH = 256;

    static {
        // 添加BouncyCastle提供者
        Security.addProvider(new BouncyCastleProvider());
    }

    private AESUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ================== 密钥生成 ==================

    /**
     * 生成AES密钥
     *
     * @return Base64编码的密钥
     */
    public static String generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(AES_KEY_LENGTH);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }

    /**
     * 从字符串创建AES密钥
     *
     * @param keyStr Base64编码的密钥字符串
     * @return SecretKey
     */
    private static SecretKey createKey(String keyStr) {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    // ================== AES-GCM 加密解密 ==================

    /**
     * AES-GCM加密
     *
     * @param plaintext 明文
     * @param keyStr Base64编码的密钥
     * @return Base64编码的密文（包含IV）
     */
    public static String encryptGCM(String plaintext, String keyStr) {
        if (plaintext == null || keyStr == null) {
            return null;
        }
        
        try {
            SecretKey key = createKey(keyStr);
            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
            
            byte[] encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            
            // 将IV和加密数据合并
            byte[] encryptedWithIv = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(encryptedWithIv);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt with AES-GCM", e);
        }
    }

    /**
     * AES-GCM解密
     *
     * @param ciphertext Base64编码的密文（包含IV）
     * @param keyStr Base64编码的密钥
     * @return 明文
     */
    public static String decryptGCM(String ciphertext, String keyStr) {
        if (ciphertext == null || keyStr == null) {
            return null;
        }
        
        try {
            SecretKey key = createKey(keyStr);
            byte[] encryptedWithIv = Base64.getDecoder().decode(ciphertext);
            
            // 分离IV和加密数据
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, iv.length);
            System.arraycopy(encryptedWithIv, iv.length, encryptedData, 0, encryptedData.length);
            
            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
            
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt with AES-GCM", e);
        }
    }

    // ================== 简化的加密解密方法 ==================

    /**
     * 使用默认AES-GCM模式加密
     *
     * @param plaintext 明文
     * @param password 密码（将被处理为256位密钥）
     * @return Base64编码的密文
     */
    public static String encrypt(String plaintext, String password) {
        String key = generateKeyFromPassword(password);
        return encryptGCM(plaintext, key);
    }

    /**
     * 使用默认AES-GCM模式解密
     *
     * @param ciphertext Base64编码的密文
     * @param password 密码（将被处理为256位密钥）
     * @return 明文
     */
    public static String decrypt(String ciphertext, String password) {
        String key = generateKeyFromPassword(password);
        return decryptGCM(ciphertext, key);
    }

    /**
     * 从密码生成AES密钥
     *
     * @param password 密码
     * @return Base64编码的密钥
     */
    private static String generateKeyFromPassword(String password) {
        try {
            // 使用SHA-256哈希密码并截取前32字节作为AES-256密钥
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = md.digest(passwordBytes);
            return Base64.getEncoder().encodeToString(keyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate key from password", e);
        }
    }

    // ================== 工具方法 ==================

    /**
     * 验证密钥格式是否正确
     *
     * @param keyStr Base64编码的密钥字符串
     * @return 是否有效
     */
    public static boolean isValidKey(String keyStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyStr);
            return keyBytes.length == 32; // 256位密钥
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成随机盐值
     *
     * @param length 盐值长度
     * @return Base64编码的盐值
     */
    public static String generateSalt(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 使用PBKDF2从密码和盐值派生密钥
     *
     * @param password 密码
     * @param salt 盐值
     * @param iterations 迭代次数
     * @return Base64编码的派生密钥
     */
    public static String deriveKey(String password, String salt, int iterations) {
        try {
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                password.toCharArray(),
                Base64.getDecoder().decode(salt),
                iterations,
                AES_KEY_LENGTH
            );
            
            javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey key = factory.generateSecret(spec);
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Failed to derive key using PBKDF2", e);
        }
    }

    /**
     * 安全的字符串比较（防止时序攻击）
     *
     * @param a 字符串a
     * @param b 字符串b
     * @return 是否相等
     */
    public static boolean safeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }
        
        if (a.length() != b.length()) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}