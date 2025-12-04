package com.wkc.library.config;

import com.wkc.library.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiaoniao
 * @date 2024/5/30 12:37
 */
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {
    private static List<String> excludePathList = Arrays.asList("/user/login","/user/logon","/code/captcha","/common/download");

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(excludePathList);
    }

}
