package com.challenge.wit.calculator.service;

import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.shared.logging.LoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculationService {

    private static final Logger logger = LoggerFactory.getLogger(CalculationService.class);

    public CalculationResponse calculate(CalculationRequest request) {
        BigDecimal result;
        String error = null;

        try {
            switch (request.getOperation().toLowerCase()) {
                case "sum" -> {
                    result = request.getOperandA().add(request.getOperandB());
                    logger.debug(LoggingConstants.LOG_CALCULATION_RESULT, "Sum", result);
                }
                case "subtract" -> {
                    result = request.getOperandA().subtract(request.getOperandB());
                    logger.debug(LoggingConstants.LOG_CALCULATION_RESULT, "Subtraction", result);
                }
                case "multiply" -> {
                    result = request.getOperandA().multiply(request.getOperandB());
                    logger.debug(LoggingConstants.LOG_CALCULATION_RESULT, "Multiplication", result);
                }
                case "divide" -> {
                    if (request.getOperandB().compareTo(BigDecimal.ZERO) == 0) {
                        logger.error("Division by zero attempt: OperandB=0");
                        throw new ArithmeticException("Division by zero");
                    }
                    // Specify scale and rounding mode to handle non-terminating decimals
                    result = request.getOperandA().divide(request.getOperandB(), 10, RoundingMode.HALF_UP);
                    logger.debug(LoggingConstants.LOG_CALCULATION_RESULT, "Division", result);
                }
                default -> {
                    logger.error(LoggingConstants.LOG_UNSUPPORTED_OPERATION, request.getOperation());
                    throw new UnsupportedOperationException("Unsupported operation: " + request.getOperation());
                }
            }
        } catch (Exception ex) {
            logger.error(LoggingConstants.LOG_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
            result = null;
            error = ex.getMessage();
        }


        logger.info(LoggingConstants.LOG_CALCULATION_RESPONSE, result, error);

        return new CalculationResponse(request.getRequestId(), result, error);
    }
}
