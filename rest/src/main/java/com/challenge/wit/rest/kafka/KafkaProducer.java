package com.challenge.wit.rest.kafka;

import com.challenge.wit.shared.dto.CalculationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, CalculationRequest> kafkaTemplate;
    private final String requestsTopic;


    public KafkaProducer(KafkaTemplate<String, CalculationRequest> kafkaTemplate,
                         @Value("${calculator.requests.topic}") String requestsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.requestsTopic = requestsTopic;
    }

    public void sendRequest(CalculationRequest request) {
        kafkaTemplate.send(requestsTopic, request);
    }
}