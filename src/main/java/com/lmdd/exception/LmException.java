package com.lmdd.exception;

/**
 * 统一异常 自定义异常类
 * @author LM_MAX
 * @date 2022/2/23
 */
public class LmException extends Exception {
    private final Integer code;
    private final String message;

    public LmException(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public LmException(LmExceptionEnum exceptionEnum){
        this(exceptionEnum.getCode(),exceptionEnum.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
