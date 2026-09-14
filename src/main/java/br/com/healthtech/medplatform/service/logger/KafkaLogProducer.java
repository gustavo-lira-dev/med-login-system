package br.com.healthtech.medplatform.service.logger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaLogProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "auth-events";


    // Publishes a log message asynchronously to the Kafka broker.
    public void sendLog(LogMessage logMessage) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(logMessage);

            // Asynchronously send using the method name as the partition routing key
            kafkaTemplate.send(TOPIC, logMessage.method(), jsonMessage)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("[-] Failed to publish log to Kafka for method: {}", logMessage.method(), exception);
                        } else {
                            log.info("[+] Log published successfully to topic '{}' [Severity: {}]",
                                    TOPIC, logMessage.severity());
                        }
                    });

        } catch (Exception e) {
            log.error("[-] Serialization error while preparing Kafka log for method: {}", logMessage.method(), e);
        }
    }


}
