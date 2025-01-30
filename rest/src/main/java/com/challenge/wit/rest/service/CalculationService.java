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

    public CalculationService(KafkaProducer kafkaProducer,
                              KafkaConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
    }

    /**
     * Processes a calculation request by validating the operation, sending it to Kafka, and handling the response.
     *
     * @param request The CalculationRequest containing the operation and operands.
     * @return A CalculationResult containing the outcome of the calculation.
     */
    @Override
    public CalculationResult calculate(CalculationRequest request) {
        try {
            // Validate the operation
            if (!isValidOperation(request.getOperation())) {
                throw new InvalidOperationException("Unsupported operation: " + request.getOperation());
            }

            // Log the calculation request details
            logger.debug(LoggingConstants.LOG_CALCULATION_REQUEST, 
                         request.getOperation(), 
                         request.getOperandA(), 
                         request.getOperandB());

            // Send the CalculationRequest to Kafka and create a pending request
            CompletableFuture<CalculationResponse> future = kafkaConsumer.createPendingRequest(request.getRequestId());
            kafkaProducer.sendRequest(request);

            // Wait for the CalculationResponse with a timeout
            CalculationResponse response = future.get(30, TimeUnit.SECONDS);

            // Handle unexpected errors
            if (response == null) {
                throw new CalculationException("An unexpected error occurred during calculation.");
            }

            // If an error is present in the response, log and throw an exception
            if (response.getError() != null) {
                logger.error(LoggingConstants.LOG_ERROR, "CalculationError", response.getError());
                throw new CalculationException(response.getError());
            } else {
                // Log the successful calculation result
                logger.info(LoggingConstants.LOG_CALCULATION_RESULT, "Calculation successful", response.getResult());
                return new CalculationResult(response.getResult());
            }
        } catch (CalculationException e) {
            // Log known calculation exceptions
            logger.error(LoggingConstants.LOG_ERROR, "CalculationError", e.getMessage());
            throw e;
        } catch (InvalidOperationException e) {
            // Log invalid operation exceptions
            logger.error(LoggingConstants.LOG_ERROR, "InvalidOperation", e.getMessage());
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions and throw a generic CalculationException
            logger.error(LoggingConstants.LOG_ERROR, "CalculationException", "An unexpected error occurred.");
            throw new CalculationException("An unexpected error occurred during calculation.");
        }
    }

    /**
     * Validates if the provided operation is supported.
     *
     * @param operation The operation to validate.
     * @return true if the operation is valid, false otherwise.
     */
    private boolean isValidOperation(String operation) {
        return operation.equalsIgnoreCase("sum") ||
               operation.equalsIgnoreCase("subtract") ||
               operation.equalsIgnoreCase("multiply") ||
               operation.equalsIgnoreCase("divide");
    }
}
