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

    // 2. CRITICAL ERRORS (ERROR): Triggered automatically when an unexpected/system exception is thrown.
    // Excludes IllegalArgumentException since it represents handled business warnings.
    @AfterThrowing(pointcut = "within(br.com.healthtech.medplatform.controller..*)", throwing = "exception")
    public void logCriticalError(JoinPoint joinPoint, Throwable exception) {
        if (exception instanceof IllegalArgumentException) {
            return; // Aspect-Handled by 'logBusinessWarning' method below.
        }

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        LogMessage errorLog = LogMessage.error(
                "Critical API execution failure: " + exception.getMessage(),
                methodName,
                className
        );

        kafkaLogProducer.sendLog(errorLog);
    }

    // 3. MID-TERMS / BUSINESS WARNINGS (WARN): Intercepts and logs handled business validations.
    @Around("within(br.com.healthtech.medplatform.controller..*)")
    public Object logBusinessWarning(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (IllegalArgumentException ex) {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            LogMessage warnLog = LogMessage.warn(
                    "BUSINESS_VIOLATION",
                    "Request rejected: " + ex.getMessage(),
                    className,
                    methodName
            );

            kafkaLogProducer.sendLog(warnLog);

            // Re-throw the exception so the GlobalExceptionHandler can process the HTTP payload
            throw ex;
        }
    }
}
