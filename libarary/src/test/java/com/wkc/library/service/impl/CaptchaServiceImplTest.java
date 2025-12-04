package com.wkc.library.service.impl;

import com.wkc.library.service.CaptchaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xiaoniao
 * @date 2024/5/30 20:24
 */
@SpringBootTest
public class CaptchaServiceImplTest {

    @Autowired
    private CaptchaService captchaService;

    @Test
    public void geCaptcha() {
        Map<String, String> captcha = captchaService.getCaptcha();
        System.out.println(captcha);
    }
}