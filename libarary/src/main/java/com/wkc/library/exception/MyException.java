package com.wkc.library.exception;

import lombok.Getter;

/**
 * @author xiaoniao
 * @date 2024/6/1 17:15
 */
@Getter
public class MyException extends RuntimeException{

    private final String code;

    public MyException(String msg) {
        super(msg);
        this.code="500";
    }

    public MyException(String code,String msg) {
        super(msg);
        this.code = code;
    }

}
