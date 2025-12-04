package com.wkc.library.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import com.google.code.kaptcha.Constants;

/**
 * @author xiaoniao
 * @date 2024/5/30 19:45
 * @desc 验证码
 */
@Configuration
public class CaptchaConfig {

    @Bean(name = "captchaProducer")
    public DefaultKaptcha getKaptchaBean() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();

        //是否有边框
        properties.setProperty(Constants.KAPTCHA_BORDER,"yes");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR,"black");
        properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT,"48");
        properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH,"160");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE,"38");
        properties.setProperty(Constants.KAPTCHA_SESSION_KEY,"kaptchaCode");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH,"4");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES,"Arial,Courier");
        //properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL,"com.google.code.impl.WaterRipple");

        Config config= new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;


    }
}
