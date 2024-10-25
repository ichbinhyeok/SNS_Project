package com.example.sns_project.batch.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//메소드 실행시간 측정
@Component
@Aspect
public class ExecutionTimeAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    @Around("execution(* com.example.sns_project.batch.service.*.*(..))") // 해당 패키지의 모든 메서드에 대해 적용
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed(); // 메서드 실행

        long executionTime = System.currentTimeMillis() - start;

        logger.info("메소드 이름 >> {} 실행시간 >> {} 밀리초, {} 초", joinPoint.getSignature(), executionTime, executionTime/1000);
        return proceed;
    }
}
