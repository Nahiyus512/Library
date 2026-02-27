package com.wkc.library.util;

import com.wkc.library.exception.MyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;


/**
 * @author Nah
 * @date 2025/12/31 15:10
 */
@Component
@ToString
@Slf4j
public class JwtUtil {

    @Resource
    private RedisUtil redisUtil;

    /**
     * 密钥
     */
    private Key secretKey;

    @PostConstruct
    public void init() {
        String key = "jwt_secret_key";
        if (redisUtil.hasKey(key)) {
            try {
                String secretString = (String) redisUtil.get(key);
                byte[] keyBytes = Decoders.BASE64.decode(secretString);
                secretKey = Keys.hmacShaKeyFor(keyBytes);
                log.info("从 Redis 加载 JWT 密钥成功");
            } catch (Exception e) {
                log.error("从 Redis 加载 JWT 密钥失败，重新生成", e);
                generateAndSaveKey(key);
            }
        } else {
            generateAndSaveKey(key);
        }
    }

    private void generateAndSaveKey(String key) {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretString = Encoders.BASE64.encode(secretKey.getEncoded());
        redisUtil.set(key, secretString);
        log.info("生成新的 JWT 密钥并保存到 Redis");
    }

    /**
     * 生成用户token
     */
    public String createToken(String username) {
        //过期时间
        /*
        JwtBuilder jwtBuilder = Jwts.builder().setSubject(username)
                .claim("username",username)
                .claim("uuid",UUIDUtil.generateUUID())
                .signWith(SignatureAlgorithm.HS256,secret);
        return jwtBuilder.compact();*/
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setSubject(username);
        jwtBuilder.claim("username",username);
        jwtBuilder.signWith(secretKey);
        return jwtBuilder.compact();

    }

    /**
     * 校验token并解析token
     *
     * @return
     */
    public String parseToken(String token) throws Exception {
        try {
            Claims claims = (Claims)Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
            return String.valueOf(claims.get("username"));
        } catch (Exception e) {
            throw new MyException("401","非法token!");
            //return "非法token!";
        }
    }

}
