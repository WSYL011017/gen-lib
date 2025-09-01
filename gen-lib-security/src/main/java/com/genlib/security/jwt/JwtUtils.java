package com.genlib.security.jwt;

import com.genlib.core.exception.BusinessException;
import com.genlib.core.enums.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 提供JWT Token的生成、解析和验证功能
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private final String secret;
    private final long expiration;
    private final Key signingKey;
    private final JwtParser jwtParser;

    public JwtUtils(String secret, long expiration) {
        this.secret = secret;
        this.expiration = expiration;
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtParser = Jwts.parser()
                .setSigningKey(signingKey)
                .build();
    }

    /**
     * 生成JWT Token
     *
     * @param subject 主题（通常是用户ID或用户名）
     * @return JWT Token
     */
    public String generateToken(String subject) {
        return generateToken(subject, new HashMap<>());
    }

    /**
     * 生成JWT Token（带自定义Claims）
     *
     * @param subject 主题
     * @param claims 自定义声明
     * @return JWT Token
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, expiration);
    }

    /**
     * 生成JWT Token（带自定义过期时间）
     *
     * @param subject 主题
     * @param claims 自定义声明
     * @param expiration 过期时间（秒）
     * @return JWT Token
     */
    public String generateToken(String subject, Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从Token中获取主题
     *
     * @param token JWT Token
     * @return 主题
     */
    public String getSubjectFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 从Token中获取过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 从Token中获取签发时间
     *
     * @param token JWT Token
     * @return 签发时间
     */
    public Date getIssuedAtFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getIssuedAt();
    }

    /**
     * 从Token中获取自定义声明
     *
     * @param token JWT Token
     * @param claimName 声明名称
     * @return 声明值
     */
    @SuppressWarnings("unchecked")
    public <T> T getClaimFromToken(String token, String claimName) {
        Claims claims = getClaimsFromToken(token);
        return (T) claims.get(claimName);
    }

    /**
     * 从Token中获取所有声明
     *
     * @param token JWT Token
     * @return 声明集合
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT Token已过期: {}", token);
            throw new BusinessException(ResultCodeEnum.TOKEN_EXPIRED, "Token已过期");
        } catch (UnsupportedJwtException e) {
            logger.warn("不支持的JWT Token: {}", token);
            throw new BusinessException(ResultCodeEnum.TOKEN_INVALID, "不支持的Token格式");
        } catch (MalformedJwtException e) {
            logger.warn("格式错误的JWT Token: {}", token);
            throw new BusinessException(ResultCodeEnum.TOKEN_INVALID, "Token格式错误");
        } catch (SignatureException e) {
            logger.warn("JWT Token签名验证失败: {}", token);
            throw new BusinessException(ResultCodeEnum.TOKEN_INVALID, "Token签名无效");
        } catch (IllegalArgumentException e) {
            logger.warn("JWT Token参数非法: {}", token);
            throw new BusinessException(ResultCodeEnum.TOKEN_INVALID, "Token参数非法");
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("JWT Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证Token是否有效（针对特定用户）
     *
     * @param token JWT Token
     * @param subject 用户主题
     * @return 是否有效
     */
    public boolean validateToken(String token, String subject) {
        try {
            String tokenSubject = getSubjectFromToken(token);
            return subject.equals(tokenSubject) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.debug("JWT Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查Token是否过期
     *
     * @param token JWT Token
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 获取Token剩余有效时间（秒）
     *
     * @param token JWT Token
     * @return 剩余有效时间
     */
    public long getTokenRemainingTime(String token) {
        Date expiration = getExpirationDateFromToken(token);
        Date now = new Date();
        return Math.max(0, (expiration.getTime() - now.getTime()) / 1000);
    }

    /**
     * 刷新Token
     *
     * @param token 旧Token
     * @return 新Token
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String subject = claims.getSubject();
        
        // 移除时间相关的声明
        claims.remove(Claims.ISSUED_AT);
        claims.remove(Claims.EXPIRATION);
        
        // 转换为Map
        Map<String, Object> claimsMap = new HashMap<>(claims);
        
        return generateToken(subject, claimsMap);
    }

    /**
     * 解析Token头部信息
     *
     * @param token JWT Token
     * @return 头部信息
     */
    public Header getHeaderFromToken(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getHeader();
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("解析JWT Token头部失败: {}", e.getMessage());
            throw new BusinessException(ResultCodeEnum.TOKEN_INVALID, "Token格式无效");
        }
    }

    /**
     * 创建JwtPayload对象
     *
     * @param token JWT Token
     * @return JwtPayload
     */
    public JwtPayload createPayload(String token) {
        Claims claims = getClaimsFromToken(token);
        return new JwtPayload(claims);
    }

    /**
     * 获取Token类型
     *
     * @param token JWT Token
     * @return Token类型
     */
    public String getTokenType(String token) {
        Header header = getHeaderFromToken(token);
        return (String) header.get("typ");
    }

    /**
     * 获取签名算法
     *
     * @param token JWT Token
     * @return 签名算法
     */
    public String getAlgorithm(String token) {
        Header header = getHeaderFromToken(token);
        return (String) header.get("alg");
    }

    /**
     * 从LocalDateTime创建Date
     */
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 从Date创建LocalDateTime
     */
    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}