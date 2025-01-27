package com.challenge.wit.rest.service;

import com.challenge.wit.rest.kafka.KafkaConsumer;
import com.challenge.wit.rest.kafka.KafkaProducer;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class CalculationService implements ICalculationService{
    private final KafkaProducer kafkaProducer;
    private final KafkaConsumer kafkaConsumer;

    public CalculationService(KafkaProducer kafkaProducer, KafkaConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
    }

    public ResponseEntity<?> calculate(String operation, double a, double b) {
        String requestId = UUID.randomUUID().toString();

        // Create the request
        CalculationRequest request = new CalculationRequest.Builder()
                .requestId(requestId)
                .operation(operation)
                .operandA(BigDecimal.valueOf(a))
                .operandB(BigDecimal.valueOf(b))
                .build();

        // Send the request to Kafka
        kafkaProducer.sendRequest(request);

        // Wait for the response
        try {
            CompletableFuture<CalculationResponse> future = kafkaConsumer.createPendingRequest(requestId);
            CalculationResponse response = future.get(5, TimeUnit.SECONDS);

            if (response.getError() != null) {
                return ResponseEntity.badRequest().body(response.getError());
            } else {
                return ResponseEntity.ok(response.getResult());
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Timeout or error while processing the request.");
        }
    }
}