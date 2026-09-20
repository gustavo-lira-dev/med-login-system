package br.com.healthtech.medplatform.service.logger;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class LogServiceAspect {

    private final KafkaLogProducer kafkaLogProducer;
    private static final long MAX_EXECUTION_TIME_MS = 1000;

    @Around("within(br.com.healthtech.medplatform.service.serviceclass..*)")
    public Object monitorServicePerformanceAndErrors(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            // 1. Logs success execution
            kafkaLogProducer.sendLog(LogMessage.info(
                    "Service class method successfully executed",
                    className,
                    methodName
            ));
            // 2. Checks and logs performance degradation (Delay)
            if (executionTime > MAX_EXECUTION_TIME_MS) {
                kafkaLogProducer.sendLog(LogMessage.warn(
                        "DELAY_DETECTED",
                        "Execution took " + executionTime + "ms, exceeding the limit of " + MAX_EXECUTION_TIME_MS + "ms",
                        className,
                        methodName
                ));
            }

            // Crucial: Returns the actual result back to the controller/caller
            return result;

        } catch (Throwable ex) {
            // 3. Logs any exception thrown by the service
            kafkaLogProducer.sendLog(LogMessage.warn(
                    "PROBLEM_DETECTED",
                    "Exception thrown at service class method: " + ex.getMessage(),
                    className,
                    methodName
            ));

            // Re-throws the exception to avoid swallowing it and altering application behavior
            throw ex;
        }
    }
}


