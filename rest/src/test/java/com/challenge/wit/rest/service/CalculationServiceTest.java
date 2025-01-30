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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CalculationServiceTest {

    private KafkaProducer kafkaProducer;
    private KafkaConsumer kafkaConsumer;
    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        kafkaProducer = mock(KafkaProducer.class);
        kafkaConsumer = mock(KafkaConsumer.class);
        calculationService = new CalculationService(kafkaProducer, kafkaConsumer);
        MDC.put(LoggingConstants.MDC_REQUEST_ID_KEY, "testRequestId");

        CalculationResponse defaultResponse = new CalculationResponse(MDC.get(LoggingConstants.MDC_REQUEST_ID_KEY), BigDecimal.ZERO, null);
        when(kafkaConsumer.createPendingRequest(anyString()))
                .thenReturn(CompletableFuture.completedFuture(defaultResponse));
    }

    @Test
    void testSuccessfulCalculation() throws Exception {
        String operation = "sum";
        double a = 10.0;
        double b = 5.0;
        String requestId = "test1";

        // We want a specific successful response for this scenario
        CalculationResponse response = new CalculationResponse(requestId, BigDecimal.valueOf(15), null);
        CompletableFuture<CalculationResponse> future = CompletableFuture.completedFuture(response);

        // Override the default stub for this test
        when(kafkaConsumer.createPendingRequest(anyString())).thenReturn(future);

        CalculationResult result = calculationService.calculate(operation, a, b);
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(15), result.getResult());

        // Ensure we called sendRequest exactly once
        verify(kafkaProducer, times(1)).sendRequest(any(CalculationRequest.class));
    }

    @Test
    void testDivisionByZero() throws Exception {
        String operation = "divide";
        double a = 1.0;
        double b = 0.0;
        String requestId = "test2";

        // The calculator module is expected to produce an error "Division by zero"
        CalculationResponse response = new CalculationResponse(requestId, null, "Division by zero");
        CompletableFuture<CalculationResponse> future = CompletableFuture.completedFuture(response);

        when(kafkaConsumer.createPendingRequest(anyString())).thenReturn(future);

        CalculationException exception = assertThrows(CalculationException.class,
                () -> calculationService.calculate(operation, a, b));

        assertEquals("Division by zero", exception.getMessage());
        verify(kafkaProducer, times(1)).sendRequest(any(CalculationRequest.class));
    }

    @Test
    void testUnsupportedOperation() {
        String operation = "pow"; // not supported
        double a = 2.0;
        double b = 3.0;

        // No need to override the stub because
        // we never even send a message if the operation is invalid
        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> calculationService.calculate(operation, a, b));

        assertEquals("Unsupported operation: pow", exception.getMessage());
        // No Kafka send if invalid operation
        verify(kafkaProducer, times(0)).sendRequest(any(CalculationRequest.class));
    }

    @Test
    void testKafkaTimeout() throws Exception {
        String operation = "sum";
        double a = 10.0;
        double b = 5.0;

        // Return a never-completed future to simulate a timeout scenario
        CompletableFuture<CalculationResponse> neverCompleted = new CompletableFuture<>();
        when(kafkaConsumer.createPendingRequest(anyString())).thenReturn(neverCompleted);

        TimeoutException exception = assertThrows(TimeoutException.class,
                () -> calculationService.calculate(operation, a, b));

        assertEquals("Calculation request timed out after 5 seconds.", exception.getMessage());
        verify(kafkaProducer, times(1)).sendRequest(any(CalculationRequest.class));
    }
}
