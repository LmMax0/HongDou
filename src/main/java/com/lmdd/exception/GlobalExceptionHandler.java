package com.lmdd.exception;

import com.lmdd.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 处理统一异常
 * @author LM_MAX
 * @date 2022/2/23
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     *  系统异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e){
        log.error("Default Exception: " ,e);
        return ApiRestResponse.error(LmExceptionEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(LmException.class)
    @ResponseBody
    public Object handleLmException(LmException e){
        log.error("LmException: " ,e);
        return ApiRestResponse.error(e.getCode(),e.getMessage());
    }


}
