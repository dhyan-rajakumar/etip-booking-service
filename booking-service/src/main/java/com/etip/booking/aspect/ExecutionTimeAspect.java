package com.etip.booking.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Aspect
public class ExecutionTimeAspect {
    private static final Logger log =
            LoggerFactory.getLogger(ExecutionTimeAspect.class);
    private final ObjectMapper objectMapper;

    public ExecutionTimeAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(audit)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable{
        String correlationId = MDC.get("correlationId");
        long start = System.currentTimeMillis();
        Object [] args = joinPoint.getArgs();
        String request = objectMapper.writeValueAsString(args[0]);
        if(audit.logRequest()){
                log.info("Request = {}", request);
        }
        try{
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            log.info("CorrelationId = {} Operation = {} Method = {} ExecutionTime = {}ms Status = {}",
                    correlationId, audit.operation(), joinPoint.getSignature().getName(),
                    executionTime, "SUCCESS");
            String response = objectMapper.writeValueAsString(result);
            if(audit.logResponse()){
                log.info("Response = {}", response);
            }

            return result;
        }catch (Throwable ex){
            long executionTime = System.currentTimeMillis() - start;
            log.info("CorrelationId = {} Operation = {} Method = {} ExecutionTime = {}ms Status = {} Exception = {}",
                    correlationId, audit.operation(), joinPoint.getSignature().getName(),
                    executionTime, "FAILURE", ex.getClass().getSimpleName());
            throw ex;
        }

    }
}
