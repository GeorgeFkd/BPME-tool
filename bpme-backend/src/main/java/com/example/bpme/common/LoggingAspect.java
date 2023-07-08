package com.example.bpme.common;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
//    @Around("@annotation(com.example.bpme.MeasurePerformance)")
//    public void logExecTime(ProceedingJoinPoint joinPoint) throws Throwable{
//
//        long startTime = System.currentTimeMillis();
//        log.info("Logging: " + joinPoint.getSignature().getName());
//        joinPoint.proceed(joinPoint.getArgs());
//        long endTime = System.currentTimeMillis();
//        System.out.println("Time needed: " + (endTime-startTime) + "ms");
//
//
//    }
}
