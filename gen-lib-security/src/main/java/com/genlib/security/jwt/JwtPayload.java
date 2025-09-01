package com.genlib.security.jwt;

import io.jsonwebtoken.Claims;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * JWT载荷类
 * 封装JWT Token的载荷信息
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class JwtPayload {

    private final Claims claims;

    public JwtPayload(Claims claims) {
        this.claims = claims;
    }

    /**
     * 获取主题（用户ID或用户名）
     */
    public String getSubject() {
        return claims.getSubject();
    }

    /**
     * 获取签发时间
     */
    public Date getIssuedAt() {
        return claims.getIssuedAt();
    }

    /**
     * 获取签发时间（LocalDateTime）
     */
    public LocalDateTime getIssuedAtLocalDateTime() {
        Date issuedAt = getIssuedAt();
        return issuedAt != null ? 
            LocalDateTime.ofInstant(issuedAt.toInstant(), ZoneId.systemDefault()) : null;
    }

    /**
     * 获取过期时间
     */
    public Date getExpiration() {
        return claims.getExpiration();
    }

    /**
     * 获取过期时间（LocalDateTime）
     */
    public LocalDateTime getExpirationLocalDateTime() {
        Date expiration = getExpiration();
        return expiration != null ? 
            LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault()) : null;
    }

    /**
     * 获取生效时间
     */
    public Date getNotBefore() {
        return claims.getNotBefore();
    }

    /**
     * 获取生效时间（LocalDateTime）
     */
    public LocalDateTime getNotBeforeLocalDateTime() {
        Date notBefore = getNotBefore();
        return notBefore != null ? 
            LocalDateTime.ofInstant(notBefore.toInstant(), ZoneId.systemDefault()) : null;
    }

    /**
     * 获取受众
     */
    public String getAudience() {
        Object audience = claims.getAudience();
        if (audience instanceof String) {
            return (String) audience;
        } else if (audience instanceof Collection) {
            Collection<?> audienceSet = (Collection<?>) audience;
            return audienceSet.isEmpty() ? null : audienceSet.iterator().next().toString();
        }
        return null;
    }

    /**
     * 获取签发者
     */
    public String getIssuer() {
        return claims.getIssuer();
    }

    /**
     * 获取JWT ID
     */
    public String getId() {
        return claims.getId();
    }

    /**
     * 获取自定义声明
     */
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String name) {
        return (T) claims.get(name);
    }

    /**
     * 获取自定义声明（带默认值）
     */
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String name, T defaultValue) {
        Object value = claims.get(name);
        return value != null ? (T) value : defaultValue;
    }

    /**
     * 获取字符串类型的声明
     */
    public String getStringClaim(String name) {
        Object value = claims.get(name);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取整数类型的声明
     */
    public Integer getIntegerClaim(String name) {
        Object value = claims.get(name);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.valueOf((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取长整数类型的声明
     */
    public Long getLongClaim(String name) {
        Object value = claims.get(name);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.valueOf((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取布尔类型的声明
     */
    public Boolean getBooleanClaim(String name) {
        Object value = claims.get(name);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.valueOf((String) value);
        }
        return null;
    }

    /**
     * 获取所有声明
     */
    public Map<String, Object> getAllClaims() {
        return claims;
    }

    /**
     * 检查是否包含声明
     */
    public boolean containsClaim(String name) {
        return claims.containsKey(name);
    }

    /**
     * 检查Token是否过期
     */
    public boolean isExpired() {
        Date expiration = getExpiration();
        return expiration != null && expiration.before(new Date());
    }

    /**
     * 检查Token是否还未生效
     */
    public boolean isNotYetValid() {
        Date notBefore = getNotBefore();
        return notBefore != null && notBefore.after(new Date());
    }

    /**
     * 检查Token是否有效（未过期且已生效）
     */
    public boolean isValid() {
        return !isExpired() && !isNotYetValid();
    }

    /**
     * 获取Token剩余有效时间（秒）
     */
    public long getRemainingTime() {
        Date expiration = getExpiration();
        if (expiration == null) {
            return Long.MAX_VALUE;
        }
        long remaining = (expiration.getTime() - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    @Override
    public String toString() {
        return "JwtPayload{" +
                "subject='" + getSubject() + '\'' +
                ", issuedAt=" + getIssuedAt() +
                ", expiration=" + getExpiration() +
                ", audience='" + getAudience() + '\'' +
                ", issuer='" + getIssuer() + '\'' +
                ", id='" + getId() + '\'' +
                ", valid=" + isValid() +
                '}';
    }
}