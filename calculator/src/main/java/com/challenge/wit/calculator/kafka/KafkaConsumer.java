package com.challenge.wit.calculator.kafka;

import com.challenge.wit.calculator.service.CalculationService;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class KafkaConsumer {

    private final ConcurrentMap<String, CompletableFuture<CalculationRequest>> pendingRequests = new ConcurrentHashMap<>();

    private final CalculationService calculationService;
    private final KafkaProducer kafkaProducer;

    public KafkaConsumer(CalculationService calculationService,
                         KafkaProducer kafkaProducer) {
        this.calculationService = calculationService;
        this.kafkaProducer = kafkaProducer;
    }

    @KafkaListener(topics = "${calculator.requests.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(CalculationRequest request) {

        try{
            CalculationResponse response = calculationService.calculate(request);
            kafkaProducer.sendRequest(response);
        } catch (Exception ex){
            //log fail
        }

    }

}