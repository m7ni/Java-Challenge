package com.challenge.wit.calculator.service;

import com.challenge.wit.calculator.kafka.MdcKafkaConsumerInterceptor;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculationService {

    private static final Logger logger = LoggerFactory.getLogger(CalculationService.class);

    public CalculationResponse calculate(CalculationRequest request) {
        logger.info("Received calculation request: Operation={}, OperandA={}, OperandB={}",
                request.getOperation(), request.getOperandA(), request.getOperandB());

        BigDecimal result;
        String error = null;

        try {
            switch (request.getOperation().toLowerCase()) {
                case "sum":
                    result = request.getOperandA().add(request.getOperandB());
                    logger.debug("Sum result: {}", result);
                    break;
                case "subtract":
                    result = request.getOperandA().subtract(request.getOperandB());
                    logger.debug("Subtract result: {}", result);
                    break;
                case "multiply":
                    result = request.getOperandA().multiply(request.getOperandB());
                    logger.debug("Multiply result: {}", result);
                    break;
                case "divide":
                    if (request.getOperandB().compareTo(BigDecimal.ZERO) == 0) {
                        logger.error("Division by zero attempt: OperandB=0");
                        throw new ArithmeticException("Division by zero");
                    }
                    // Specify scale and rounding mode to handle non-terminating decimals
                    result = request.getOperandA().divide(request.getOperandB(), 10, RoundingMode.HALF_UP);
                    logger.debug("Division result: {}", result);
                    break;
                default:
                    logger.error("Unsupported operation: {}", request.getOperation());
                    throw new UnsupportedOperationException("Unsupported operation: " + request.getOperation());
            }
        } catch (Exception ex) {
            logger.error("Error during calculation: {}", ex.getMessage(), ex);
            result = null;
            error = ex.getMessage();
        }

        logger.info("Calculation response: RequestId={}, Result={}, Error={}",
                MDC.get(MdcKafkaConsumerInterceptor.MDC_REQUEST_ID_KEY), result, error);
        return new CalculationResponse(request.getRequestId(), result, error);
    }
}