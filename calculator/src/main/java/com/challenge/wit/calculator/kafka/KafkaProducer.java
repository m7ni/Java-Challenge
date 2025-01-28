package com.challenge.wit.calculator.kafka;

import com.challenge.wit.shared.dto.CalculationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, CalculationResponse> kafkaTemplate;
    private final String requestsTopic;


    public KafkaProducer(KafkaTemplate<String, CalculationResponse> kafkaTemplate,
                         @Value("${calculator.responses.topic}") String requestsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.requestsTopic = requestsTopic;
    }

    public void sendRequest(CalculationResponse response) {
        kafkaTemplate.send(requestsTopic, response);
    }
}