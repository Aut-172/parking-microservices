package com.demo.common.util;

import com.demo.common.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * JWT 工具类，用于生成和解析 JWT 令牌
 * 配置项从 application.yml 读取
 */
@Slf4j
@Component
public class JwtUtils {

    // 静态字段，用于静态方法
    private static String jwtKey;
    private static Long jwtTtl;       // 过期时间（毫秒）
    private static String jwtIssuer;

    // 实例字段，用于接收 Spring 注入的值
    @Value("${jwt.key}")
    private String instanceJwtKey;
    @Value("${jwt.ttl}")
    private Long instanceJwtTtl;
    @Value("${jwt.issuer}")
    private String instanceJwtIssuer;

    @PostConstruct
    public void init() {
        // 将实例字段的值赋给静态字段
        jwtKey = this.instanceJwtKey;
        jwtTtl = this.instanceJwtTtl;
        jwtIssuer = this.instanceJwtIssuer;
        System.out.println("JwtUtils init called, jwtKey=" + instanceJwtKey);
    }

    public static String createUserJWT(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("phone", user.getPhone());
        claims.put("role", user.getRole());
        String subject = JsonUtils.toJson(claims);  // 依赖 JsonUtils 工具类

        // 生成 JWT token
        return JwtUtils.createJWT(subject);
    }

    /**
     * 生成 JWT 令牌（使用默认过期时间）
     * @param subject 用户数据（JSON 格式）
     * @return JWT 字符串
     */
    public static String createJWT(String subject) {
        return createJWT(subject, jwtTtl);
    }

    /**
     * 生成 JWT 令牌（指定过期时间）
     * @param subject  用户数据（JSON 格式）
     * @param ttlMillis 过期时间（毫秒），若为 null 则使用默认值
     * @return JWT 字符串
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());
        return builder.compact();
    }

    /**
     * 生成带有自定义 ID 的 JWT
     */
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = jwtTtl;
        }
        long expMillis = nowMillis + ttlMillis;
        Date exp = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signatureAlgorithm, secretKey);
    }

    /**
     * 将 Base64 编码的密钥转换为 SecretKey 对象
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(jwtKey);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "HmacSHA256");
    }

    /**
     * 解析 JWT，获取 Claims
     * @param jwt JWT 字符串
     * @return Claims 对象
     * @throws Exception 解析失败时抛出异常
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 生成随机 UUID（去除连字符）
     */
    private static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}