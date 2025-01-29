package com.challenge.wit.calculator.service;

import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculationServiceTest {

    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new CalculationService();
    }

    @Test
    void testSumOperation() {
        CalculationRequest request = new CalculationRequest.Builder()
                .requestId("1")
                .operation("sum")
                .operandA(BigDecimal.valueOf(10))
                .operandB(BigDecimal.valueOf(5))
                .build();

        CalculationResponse response = calculationService.calculate(request);
        assertNotNull(response.getResult());
        assertEquals(BigDecimal.valueOf(15), response.getResult());
        assertNull(response.getError());
    }

    @Test
    void testDivideByZero() {
        CalculationRequest request = new CalculationRequest.Builder()
                .requestId("2")
                .operation("divide")
                .operandA(BigDecimal.valueOf(1))
                .operandB(BigDecimal.ZERO)
                .build();

        CalculationResponse response = calculationService.calculate(request);
        assertNull(response.getResult());
        assertEquals("Division by zero", response.getError());
    }

}