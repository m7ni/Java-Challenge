package com.challenge.wit.rest.kafka;

import com.challenge.wit.rest.controller.OperationController;
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
    private static final Logger logger = LoggerFactory.getLogger(OperationController.class);
    private final ConcurrentMap<String, CompletableFuture<CalculationResponse>> pendingRequests = new ConcurrentHashMap<>();

    @KafkaListener(topics = "${calculator.responses.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void receiveResponse(CalculationResponse response) {
        logger.info(LoggingConstants.LOG_KAFKA_RECEIVE, "calculator-responses");
        CompletableFuture<CalculationResponse> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        }
    }

    public CompletableFuture<CalculationResponse> createPendingRequest(String requestId) {
        CompletableFuture<CalculationResponse> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        return future;
    }
}