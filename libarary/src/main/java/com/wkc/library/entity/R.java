package com.wkc.library.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoniao
 * @date 2022/9/19 17:01
 */
public class R<T> {
    private Integer code;//编码：1代表成功，0代表失败
    private String msg;//错误信息
    private T data;//数据
    private Map map = new HashMap();//动态数据

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        data = data;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public static <T> R<T> success(T obj) {
        R<T> r = new R<T>();
        r.data = obj;
        r.code = 200;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.code = 400;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}