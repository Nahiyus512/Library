package com.wkc.library.util;

import com.wkc.library.exception.MyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;


/**
 * @author xiaoniao
 * @date 2024/5/31 15:10
 */
@Component
@ToString
@Slf4j
public class JwtUtil {

    /**
     * 密钥
     */
    private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

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
