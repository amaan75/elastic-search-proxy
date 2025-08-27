package com.elasticsearchproxy.service;

import com.elasticsearchproxy.config.ApplicationProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPublisherService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;

    public KafkaPublisherService(KafkaTemplate<String, String> kafkaTemplate,
                                ApplicationProperties applicationProperties,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<Void> publishElasticsearchOperation(String operation, String index, String documentId, Object request) {
        if (!applicationProperties.getKafka().isEnablePublishing()) {
            logger.debug("Kafka publishing is disabled, skipping message publication");
            return CompletableFuture.completedFuture(null);
        }

        String topic = applicationProperties.getKafka().getTopic();
        String key = generateMessageKey(operation, index, documentId);
        
        try {
            Map<String, Object> messagePayload = createMessagePayload(operation, index, documentId, request);
            String message = objectMapper.writeValueAsString(messagePayload);

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message);
            
            return future.handle((result, throwable) -> {
                if (throwable != null) {
                    logger.error("Failed to publish message to Kafka topic: {}, key: {}, error: {}", 
                            topic, key, throwable.getMessage(), throwable);
                    // Don't throw exception - we want to continue processing even if Kafka fails
                } else {
                    logger.info("Successfully published message to Kafka topic: {}, key: {}, partition: {}, offset: {}", 
                            topic, key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
                return null;
            });

        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize message payload for operation: {}, index: {}, id: {}", 
                    operation, index, documentId, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private String generateMessageKey(String operation, String index, String documentId) {
        // Create a key that ensures related operations for the same document are processed in order
        return String.format("%s:%s:%s", index, documentId != null ? documentId : "null", operation);
    }

    private Map<String, Object> createMessagePayload(String operation, String index, String documentId, Object request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("operation", operation);
        payload.put("index", index);
        payload.put("documentId", documentId);
        payload.put("request", request);
        payload.put("timestamp", LocalDateTime.now().toString());
        payload.put("service", "elasticsearch-proxy");
        return payload;
    }
}