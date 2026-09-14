package br.com.healthtech.medplatform.service.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// Service responsible for consuming and processing structured log events from Kafka.
@Slf4j
@Service
public class KafkaLogListener {

    // Continuously listens to the 'auth-events' topic and prints payloads to the console.
    @KafkaListener(topics = "auth-events", groupId = "auth-logger-group")
    public void consumeLog(String message) {
        log.info("[📥 Kafka Consumer Received] -> Payload: {}", message);

        // This is where you could route the log to an external file, ELK Stack, or AWS S3 in the future.
    }
}

