// src/main/java/com/challenge/wit/rest/service/CalculationService.java
package com.challenge.wit.rest.service;

import com.challenge.wit.rest.kafka.KafkaConsumer;
import com.challenge.wit.rest.kafka.KafkaProducer;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.rest.exception.CalculationException;
import com.challenge.wit.rest.exception.TimeoutException;
import com.challenge.wit.rest.exception.InvalidOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class CalculationService implements ICalculationService {

    private final KafkaProducer kafkaProducer;
    private final KafkaConsumer kafkaConsumer;

    public CalculationService(KafkaProducer kafkaProducer, KafkaConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
    }

    public BigDecimal calculate(String operation, double a, double b) {
        String requestId = UUID.randomUUID().toString();

        // Validate operation
        if (!isValidOperation(operation)) {
            throw new InvalidOperationException("Unsupported operation: " + operation);
        }

        // Create the calculation request
        CalculationRequest request = new CalculationRequest.Builder()
                .requestId(requestId)
                .operation(operation)
                .operandA(BigDecimal.valueOf(a))
                .operandB(BigDecimal.valueOf(b))
                .build();

        // Send the request to Kafka
        try {
            kafkaProducer.sendRequest(request);
        } catch (Exception e) {
            throw new CalculationException("Failed to send calculation request to Kafka.", e);
        }

        // Wait for the response
        try {
            CompletableFuture<CalculationResponse> future = kafkaConsumer.createPendingRequest(requestId);
            CalculationResponse response = future.get(5, TimeUnit.SECONDS);

            if (response.getError() != null) {
                throw new CalculationException(response.getError());
            } else {
                return response.getResult();
            }
        } catch (java.util.concurrent.TimeoutException e) {
            throw new TimeoutException("Calculation request timed out after 5 seconds.", e);
        } catch (Exception e) {
            throw new CalculationException("An unexpected error occurred during calculation.", e);
        }
    }

    private boolean isValidOperation(String operation) {
        return operation.equalsIgnoreCase("sum") ||
                operation.equalsIgnoreCase("subtract") ||
                operation.equalsIgnoreCase("multiply") ||
                operation.equalsIgnoreCase("divide");
    }
}
