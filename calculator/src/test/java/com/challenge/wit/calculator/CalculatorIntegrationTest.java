package com.challenge.wit.calculator;

import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.calculator.service.CalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CalculatorIntegrationTest {

    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new CalculationService();
    }

    @Test
    void testFullSumFlow() {
        CalculationRequest request = new CalculationRequest.Builder()
                .requestId("integration1")
                .operation("sum")
                .operandA(BigDecimal.valueOf(20))
                .operandB(BigDecimal.valueOf(22))
                .build();

        CalculationResponse response = calculationService.calculate(request);
        assertNotNull(response.getResult());
        assertEquals(BigDecimal.valueOf(42), response.getResult());
        assertNull(response.getError());
    }

}