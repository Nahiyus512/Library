package com.wkc.library.exception;

import com.wkc.library.entity.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nah
 * @date 2025/12/1 17:18
 */
@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(MyException.class)
    @ResponseBody
    public R<Map<String,Object>> myException(MyException e) {
        R<Map<String, Object>> r = new R<>();
        r.setCode(Integer.valueOf(e.getCode()));
        r.setMsg(e.getMessage());
        return r;
    }
}
