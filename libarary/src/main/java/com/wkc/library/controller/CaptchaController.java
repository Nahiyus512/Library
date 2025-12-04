package com.wkc.library.controller;

import com.wkc.library.entity.R;
import com.wkc.library.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author xiaoniao
 * @date 2024/5/30 22:21
 */
@Controller
@ResponseBody
@CrossOrigin
@Slf4j
@RequestMapping("/code")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/captcha")
    public R<Map<String, String>> getCaptcha() {
        Map<String, String> captcha = captchaService.getCaptcha();

        return R.success(captcha);
    }
}
