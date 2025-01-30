package com.challenge.wit.rest.service;

import com.challenge.wit.rest.exception.CalculationException;
import com.challenge.wit.rest.exception.InvalidOperationException;
import com.challenge.wit.rest.exception.TimeoutException;
import com.challenge.wit.rest.kafka.KafkaConsumer;
import com.challenge.wit.rest.kafka.KafkaProducer;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.shared.dto.CalculationResult;
import com.challenge.wit.shared.logging.LoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        String requestId = MDC.get(LoggingConstants.MDC_REQUEST_ID_KEY);

        logger.info(LoggingConstants.LOG_CALCULATION_REQUEST, operation, a, b);

        if (!isValidOperation(operation)) {
            logger.error(LoggingConstants.LOG_UNSUPPORTED_OPERATION, operation);
            throw new InvalidOperationException("Unsupported operation: " + operation);
        }

        if (!Double.isFinite(a) || !Double.isFinite(b)) {
            logger.error(LoggingConstants.LOG_ERROR, "InvalidOperands", "Operands must be finite numbers. Received a=" + a + ", b=" + b);
            throw new CalculationException("Invalid operands: Operands must be finite numbers.");
        }

        CalculationRequest request = new CalculationRequest.Builder()
                .requestId(requestId)
                .operation(operation)
                .operandA(BigDecimal.valueOf(a))
                .operandB(BigDecimal.valueOf(b))
                .build();

        try {
            kafkaProducer.sendRequest(request);
            logger.debug(LoggingConstants.LOG_KAFKA_SEND, "calculator.requests.topic");
            logger.debug("CalculationRequest details: {}", request); // Optional: Additional debug info
        } catch (Exception e) {
            logger.error(LoggingConstants.LOG_ERROR, "KafkaSendFailure", e.getMessage());
            throw new CalculationException("Failed to send calculation request to Kafka.", e);
        }

        CalculationResponse response;
        try {
            CompletableFuture<CalculationResponse> future = kafkaConsumer.createPendingRequest(requestId);
            response = future.get(5, TimeUnit.SECONDS);
            logger.debug(LoggingConstants.LOG_KAFKA_RECEIVE, "calculator.responses.topic");
            logger.debug(LoggingConstants.LOG_CALCULATION_RESPONSE, response.getResult(), response.getError());
        } catch (java.util.concurrent.TimeoutException e) {
            logger.error(LoggingConstants.LOG_ERROR, "Timeout", "Calculation request timed out after 5 seconds.");
            throw new TimeoutException("Calculation request timed out after 5 seconds.");
        } catch (Exception e) {
            logger.error(LoggingConstants.LOG_ERROR, "UnexpectedError", e.getMessage());
            throw new CalculationException("An unexpected error occurred during calculation.");
        }

        if (response.getError() != null) {
            logger.error(LoggingConstants.LOG_ERROR, "CalculationError", response.getError());
            throw new CalculationException(response.getError());
        } else {
            logger.info(LoggingConstants.LOG_CALCULATION_RESULT, "Calculation successful", response.getResult());
            return new CalculationResult(response.getResult());
        }
    }

    private boolean isValidOperation(String operation) {
        boolean valid = operation.equalsIgnoreCase("sum") ||
                operation.equalsIgnoreCase("subtract") ||
                operation.equalsIgnoreCase("multiply") ||
                operation.equalsIgnoreCase("divide");
        logger.debug(LoggingConstants.LOG_CALCULATION_RESULT, "Operation validity", valid);
        return valid;
    }
}
