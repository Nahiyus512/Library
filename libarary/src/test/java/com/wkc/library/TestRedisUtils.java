package com.wkc.library;

import com.wkc.library.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xiaoniao
 * @date 2024/5/30 21:09
 */
@SpringBootTest
public class TestRedisUtils {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void testSet(){
        redisUtil.set("username","xiaoniao");
    }

    @Test
    public void testExpireSet(){
        redisUtil.set("username","bbb",10);
    }

    @Test
    public void getTest() {
        Object username = redisUtil.get("username");
        System.out.println(String.valueOf(username));
    }

    @Test
    public void delTest() {
        redisUtil.del("username");
    }

    @Test
    public void hsKeyTest() {

    }
}
