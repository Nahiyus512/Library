package com.wkc.library.exception;

import com.wkc.library.entity.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoniao
 * @date 2024/6/1 17:18
 */
@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(MyException.class)
    @ResponseBody
    public R<Map<String,Object>> myException(MyException e) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",e.getCode());
        map.put("msg",e.getMessage());
        return R.success(map);
    }
}
