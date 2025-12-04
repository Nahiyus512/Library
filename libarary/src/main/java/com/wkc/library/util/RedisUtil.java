package com.wkc.library.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoniao
 * @date 2024/5/30 20:50
 * @deprecated redis工具类
 */
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 设置redis缓存
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key,Object value) {
        try {
            redisTemplate.opsForValue().set(key,value);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 放置缓存设置缓存时间
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean set(String key,Object value,long time) {
        try {
            if(time > 0) {
                redisTemplate.opsForValue().set(key,value,time, TimeUnit.SECONDS);
            }else {
                set(key,value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 获取缓存
     * @param key
     * @return
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     * @param key
     */
    public void del(String... key) {
        if(key != null && key.length > 0) {
            if(key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }

        }
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
