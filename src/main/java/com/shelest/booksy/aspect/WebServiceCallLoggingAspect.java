package com.shelest.booksy.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class WebServiceCallLoggingAspect {
    @Around("execution(* com.shelest.booksy.web.controller..*(..))")
    public Object auditMethod(ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().getDeclaringTypeName() + "." + jp.getSignature().getName();

        log.info("--------------------------------------------------------------------------");
        log.info("Web service call: {} with arguments {}", methodName, Arrays.toString(jp.getArgs()));
        long t0 = System.currentTimeMillis();
        Object obj = jp.proceed();
        long t1 = System.currentTimeMillis();

        log.info("Web service call: {} finished in {} milliseconds. ", methodName, (t1 - t0));
        log.info("--------------------------------------------------------------------------");
        return obj;
    }
}
