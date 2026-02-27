package com.wkc.library.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Nah
 * @date 2025/12/31 14:40
 */
public class UUIDUtil {
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
