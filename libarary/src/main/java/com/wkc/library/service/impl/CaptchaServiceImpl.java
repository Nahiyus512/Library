package com.wkc.library.service.impl;

import com.google.code.kaptcha.Producer;
import com.wkc.library.common.Constant;
import com.wkc.library.service.CaptchaService;
import com.wkc.library.util.RedisUtil;
import com.wkc.library.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoniao
 * @date 2024/5/30 20:08
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Autowired
    private RedisUtil redisUtil;



    @Override
    public Map<String, String> getCaptcha() {
        //获取验证码
        String captcha = captchaProducer.createText();

        //生成随机数，绑定验证码,验证码_随机数
        String uuid = UUIDUtil.generateUUID();


        //设置验证码有效期
        redisUtil.set(Constant.getCaptchaKey(uuid),captcha,Constant.CAPTCHA_TIME_OUT);

        log.info("captcha = {}",captcha);
        BufferedImage image = captchaProducer.createImage(captcha);
        //转换信息流写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image,"jpg",os);
        } catch (IOException e) {
            log.error("captcha to image error={}",e.getMessage());
            e.printStackTrace();
        }
        Base64.Encoder encoder = Base64.getEncoder();

        String imageCode =  encoder.encodeToString(os.toByteArray());

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("uuid",uuid);
        resultMap.put("imageBase64",imageCode);
        return resultMap;

    }
}
