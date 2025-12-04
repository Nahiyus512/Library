package com.wkc.library;

import com.wkc.library.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xiaoniao
 * @date 2024/6/1 11:45
 */
@SpringBootTest
public class JwtUtilTest {

    @Autowired
    JwtUtil jwtUtil;

    @Test
    public void testCreateToken(){
        String admin = jwtUtil.createToken("admin");
        System.out.println(admin);
        try {
            String s = jwtUtil.parseToken(admin);
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
