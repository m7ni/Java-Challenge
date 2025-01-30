package com.challenge.wit.rest.kafka;

import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.shared.logging.LoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ConcurrentMap<String, CompletableFuture<CalculationResponse>> pendingRequests = new ConcurrentHashMap<>();

    /**
     * Listens to the 'calculator-responses' Kafka topic and processes incoming CalculationResponse messages.
     *
     * @param response The CalculationResponse received from Kafka.
     */
    @KafkaListener(topics = "${calculator.responses.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void receiveResponse(CalculationResponse response) {
        // Log the reception of a Kafka message
        logger.info(LoggingConstants.LOG_KAFKA_RECEIVE, "calculator-responses");
        
        // Retrieve the corresponding CompletableFuture for the requestId and complete it
        CompletableFuture<CalculationResponse> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        }
    }

    /**
     * Creates a pending request by associating a requestId with a CompletableFuture.
     *
     * @param requestId The unique identifier for the request.
     * @return A CompletableFuture that will be completed when the response is received.
     */
    public CompletableFuture<CalculationResponse> createPendingRequest(String requestId) {
        CompletableFuture<CalculationResponse> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        return future;
    }
}