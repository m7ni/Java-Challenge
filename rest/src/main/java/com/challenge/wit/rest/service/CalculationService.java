package com.challenge.wit.rest.service;

import com.challenge.wit.rest.exception.CalculationException;
import com.challenge.wit.rest.exception.InvalidOperationException;
import com.challenge.wit.rest.exception.TimeoutException;
import com.challenge.wit.rest.filter.RequestIdFilter;
import com.challenge.wit.rest.kafka.KafkaConsumer;
import com.challenge.wit.rest.kafka.KafkaProducer;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.shared.dto.CalculationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class CalculationService implements ICalculationService {

    private static final Logger logger = LoggerFactory.getLogger(CalculationService.class);

    private final KafkaProducer kafkaProducer;
    private final KafkaConsumer kafkaConsumer;

    public CalculationService(KafkaProducer kafkaProducer, KafkaConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
    }

    @Override
    public CalculationResult calculate(String operation, double a, double b) {
        String requestId = MDC.get(RequestIdFilter.MDC_REQUEST_ID_KEY);
        logger.info("Processing calculation request: RequestId={}, Operation={}, OperandA={}, OperandB={}",
                requestId, operation, a, b);

        // Validate operation
        if (!isValidOperation(operation)) {
            logger.error("Unsupported operation: {}", operation);
            throw new InvalidOperationException("Unsupported operation: " + operation);
        }

        // Validate operands
        if (!Double.isFinite(a) || !Double.isFinite(b)) {
            logger.error("Invalid operands: a={}, b={}", a, b);
            throw new CalculationException("Invalid operands: Operands must be finite numbers.");
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
            logger.debug("Sent calculation request to Kafka: {}", request);
        } catch (Exception e) {
            logger.error("Failed to send calculation request to Kafka: {}", e.getMessage(), e);
            throw new CalculationException("Failed to send calculation request to Kafka.", e);
        }

        CalculationResponse response;
        try {
            CompletableFuture<CalculationResponse> future = kafkaConsumer.createPendingRequest(requestId);
            response = future.get(5, TimeUnit.SECONDS);
            logger.debug("Received calculation response from Kafka: {}", response);
        } catch (java.util.concurrent.TimeoutException e) {
            logger.error("Calculation request timed out for RequestId={} after 5 seconds.", requestId);
            throw new TimeoutException("Calculation request timed out after 5 seconds.", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during calculation for RequestId={}: {}", requestId, e.getMessage(), e);
            throw new CalculationException("An unexpected error occurred during calculation.", e);
        }

        if (response.getError() != null) {
            logger.error("Calculation error for RequestId={}: {}", requestId, response.getError());
            throw new CalculationException(response.getError());
        } else {
            logger.info("Calculation successful for RequestId={}: Result={}", requestId, response.getResult());
            return new CalculationResult(response.getResult());
        }
    }

    private boolean isValidOperation(String operation) {
        boolean valid = operation.equalsIgnoreCase("sum") ||
                operation.equalsIgnoreCase("subtract") ||
                operation.equalsIgnoreCase("multiply") ||
                operation.equalsIgnoreCase("divide");
        logger.debug("Operation '{}' is valid: {}", operation, valid);
        return valid;
    }
}