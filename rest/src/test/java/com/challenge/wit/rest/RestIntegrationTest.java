package com.challenge.wit.rest;

import com.challenge.wit.rest.exception.CalculationException;
import com.challenge.wit.rest.kafka.KafkaConsumer;
import com.challenge.wit.rest.kafka.KafkaProducer;
import com.challenge.wit.rest.service.CalculationService;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.shared.dto.CalculationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RestIntegrationTest {

    private KafkaProducer kafkaProducer;
    private KafkaConsumer kafkaConsumer;
    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        kafkaProducer = mock(KafkaProducer.class);
        kafkaConsumer = mock(KafkaConsumer.class);
        calculationService = new CalculationService(kafkaProducer, kafkaConsumer);
    }

    @Test
    void testCalculateSumIntegration() throws Exception {
        CalculationResponse response = new CalculationResponse("req1", BigDecimal.valueOf(25), null);
        CompletableFuture<CalculationResponse> future = CompletableFuture.completedFuture(response);

        when(kafkaConsumer.createPendingRequest(anyString())).thenReturn(future);

        CalculationResult result = calculationService.calculate("sum", 10.0, 15.0);
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(25), result.getResult());

        verify(kafkaProducer, times(1)).sendRequest(any(CalculationRequest.class));
    }

    @Test
    void testCalculateDivisionByZeroIntegration() throws Exception {
        CalculationResponse response = new CalculationResponse("req2", null, "Division by zero");
        CompletableFuture<CalculationResponse> future = CompletableFuture.completedFuture(response);

        when(kafkaConsumer.createPendingRequest(anyString())).thenReturn(future);

        CalculationException exception = assertThrows(CalculationException.class, () -> calculationService.calculate("divide", 10.0, 0.0));

        assertEquals("Division by zero", exception.getMessage());
        verify(kafkaProducer, times(1)).sendRequest(any(CalculationRequest.class));
    }

    // Additional integration tests for different scenarios
}