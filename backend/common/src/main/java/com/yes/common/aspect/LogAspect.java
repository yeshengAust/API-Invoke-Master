package com.yes.common.aspect;

import com.alibaba.fastjson.JSON;

import com.yes.common.annotation.SystemLog;
import com.yes.common.utils.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j
public class LogAspect {
    @Pointcut("@annotation(com.yes.common.annotation.SystemLog)")
    public void pt() {
    }
//
    @Around("pt()")
    public Object around(JoinPoint joinPoint) throws Throwable {
        //执行方法之前打印方法的名字和参数
        beforeHandle(joinPoint);
        MethodInvocationProceedingJoinPoint joinPoint1 = (MethodInvocationProceedingJoinPoint) joinPoint;
        Object result = ResponseResult.okResult();

        try {
            result = joinPoint1.proceed();
        }
        finally {
            afterHandle(result);
        }
    return result;

    }

    private void beforeHandle(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //拿到接口的名称
        String message = method.getAnnotation(SystemLog.class).message();
        //拿到请求头里的东西
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}", request.getRequestURL());
        // 打印描述信息
        log.info("BusinessName   : {}", message);
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", signature.getDeclaringTypeName(), method.getName());
        // 打印请求的 IP
        log.info("IP             : {}", request.getRemoteHost());
        // 打印请求入参
        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));
    }

    private void afterHandle(Object result) {
        // 打印出参
        log.info("Response       : {}", JSON.toJSONString(result));
        // 结束后换行
        log.info("=======End=======" + System.lineSeparator());
    }

}
