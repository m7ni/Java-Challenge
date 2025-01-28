package com.challenge.wit.calculator.service;

import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculationService {

    public CalculationResponse calculate(CalculationRequest request) {
        BigDecimal result;
        String error = null;

        try {
            switch (request.getOperation().toLowerCase()) {
                case "sum":
                    result = request.getOperandA().add(request.getOperandB());
                    break;
                case "subtract":
                    result = request.getOperandA().subtract(request.getOperandB());
                    break;
                case "multiply":
                    result = request.getOperandA().multiply(request.getOperandB());
                    break;
                case "divide":
                    if (request.getOperandB().compareTo(BigDecimal.ZERO) == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    result = request.getOperandA().divide(request.getOperandB(), 10, RoundingMode.HALF_UP);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported operation: " + request.getOperation());
            }
        } catch (Exception ex) {
            result = null;
            error = ex.getMessage();
        }

        return new CalculationResponse(request.getRequestId(), result, error);
    }
}