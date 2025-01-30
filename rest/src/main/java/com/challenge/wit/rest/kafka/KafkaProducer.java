package com.challenge.wit.rest.kafka;

import com.challenge.wit.rest.controller.OperationController;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.logging.LoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, CalculationRequest> kafkaTemplate;

    private String requestsTopic;

    public KafkaProducer(KafkaTemplate<String, CalculationRequest> kafkaTemplate,
                         @Value("${calculator.requests.topic}") String requestsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.requestsTopic = requestsTopic;
    }

    public void sendRequest(CalculationRequest request) {
        logger.info(LoggingConstants.LOG_CALCULATION_REQUEST, request.getOperation(),request.getOperandA(),request.getOperandB());
        logger.info(LoggingConstants.LOG_KAFKA_SEND,"calculation-requests");
        kafkaTemplate.send(requestsTopic, request);
    }
}