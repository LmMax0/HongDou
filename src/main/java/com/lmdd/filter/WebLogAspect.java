package com.lmdd.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/** 打印请求和响应信息
 * @author LM_MAX
 * @date 2022/2/21
 */
@Aspect
@Component  // 让springboot 识别到这个类
public class WebLogAspect {
    // 日志log
    private final Logger log = LoggerFactory.getLogger(com.lmdd.filter.WebLogAspect.class);

    /**
     *  配置拦截切入点
     */
    @Pointcut("execution(public * com.lmdd.controller.*.*(..))")
    public void webLog(){

    }

    /**
     *  切面业务之前要做的工作
     * @param joinPoint 获取切入对象
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){
        // 收到请求，记录请求内容
        // 在Service中就不需要controller传递参数 然后就可以通过这个方法来获取HttpServletRequest
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        // 用info类别来记录请求信息
        log.info("URL : " + request.getRequestURL().toString());
        log.info("HTTP_METHOD : " + request.getMethod());
        log.info("IP : " + request.getRemoteAddr());

        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
        log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "res" , pointcut = "webLog()")
    public void doAfterReturning(Object res) throws JsonProcessingException {
        // 处理完请求，返回内容 转成json字符串
        log.info("RESPONSE : " + new ObjectMapper().writeValueAsString(res) );
    }
}
