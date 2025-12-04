package com.wkc.library.interceptor;

import com.wkc.library.common.Constant;
import com.wkc.library.entity.R;
import com.wkc.library.exception.MyException;
import com.wkc.library.util.JwtUtil;
import com.wkc.library.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author xiaoniao
 * @date 2024/5/30 12:42
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("进入拦截器");
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            log.info("OPTIONS请求，放行");
            return true;
        }
//        if(request.getRequestURI().equals("/common/download")){
//            log.info("获取图片放行");
//            return true;
//        }
        String token = request.getHeader("token");
        log.info("token====={}",token);
        if(token==null) {
            log.info("token为空");
            throw new MyException("401","token为空");
        }
        String s = jwtUtil.parseToken(token);
        log.info(s);
        Object o = redisUtil.get(Constant.getTokenKey(s));
        if(o == null) {
            log.info("token已过期");
            throw new MyException("401","token已过期");
        }
        log.info("成功放行");

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {


    }
}
