package br.com.healthtech.medplatform.service.logger;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LogControllerAspect {

    private final KafkaLogProducer kafkaLogProducer;

    // 1. SUCCESS (INFO): Triggered automatically when a controller method returns successfully.
    @AfterReturning(pointcut = "within(br.com.healthtech.medplatform.controller..*)", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        LogMessage infoLog = LogMessage.info(
                "API request processed successfully",
                className,
                methodName
        );

        kafkaLogProducer.sendLog(infoLog);
    }

    // 2. WARN and ERROR loggers: will handle any exception, treated or not, and convert a message for the log.
    @Around("within(br.com.healthtech.medplatform.controller..*)")
    public Object logErrorsAndWarnings(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (IllegalArgumentException ex) {
            logToKafka(joinPoint, LogMessage.warn(
                    "BUSINESS_VIOLATION",
                    "Request rejected: " + ex.getMessage(),
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName()
            ));
            throw ex;
        } catch (Throwable ex) {
            logToKafka(joinPoint, LogMessage.error(
                    "Critical API execution failure: " + ex.getMessage(),
                    joinPoint.getSignature().getName(),
                    joinPoint.getTarget().getClass().getSimpleName()
            ));
            throw ex;
        }
    }

    private void logToKafka(ProceedingJoinPoint joinPoint, LogMessage logMessage) {
        kafkaLogProducer.sendLog(logMessage);
    }
}
