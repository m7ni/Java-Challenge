package com.challenge.wit.rest.controller;

import com.challenge.wit.rest.service.ICalculationService;
import com.challenge.wit.shared.dto.CalculationResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperationController.class)
class OperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICalculationService calculationService;


    @Test
    void testCalculateSum() throws Exception {
        CalculationResult result = new CalculationResult(BigDecimal.valueOf(15));
        when(calculationService.calculate("sum", 10.0, 5.0)).thenReturn(result);

        mockMvc.perform(post("/calculate/sum")
                        .param("a", "10")
                        .param("b", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(15));
    }

    @Test
    void testCalculateDivisionByZero() throws Exception {
        Mockito.when(calculationService.calculate(eq("divide"), eq(1.0), eq(0.0)))
                .thenThrow(new com.challenge.wit.rest.exception.CalculationException("Division by zero"));

        mockMvc.perform(post("/calculate/divide")
                        .param("a", "1")
                        .param("b", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Division by zero"))
                .andExpect(jsonPath("$.errorCode").value("CALCULATION_ERROR"));
    }

    @Test
    void testHandleMissingOperation() throws Exception {
        mockMvc.perform(post("/calculate")
                        .param("a", "10")
                        .param("b", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Operation not specified. Please provide a valid operation in the URL path."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_OPERATION"));
    }

    @Test
    void testUnsupportedOperation() throws Exception {
        Mockito.when(calculationService.calculate(eq("pow"), eq(2.0), eq(3.0)))
                .thenThrow(new com.challenge.wit.rest.exception.InvalidOperationException("Unsupported operation: pow"));

        mockMvc.perform(post("/calculate/pow")
                        .param("a", "2")
                        .param("b", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported operation: pow"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_OPERATION"));
    }

    // Additional tests for invalid parameters, etc.
}