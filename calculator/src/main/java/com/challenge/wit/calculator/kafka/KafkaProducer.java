package com.challenge.wit.calculator.kafka;

import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.shared.logging.LoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, CalculationResponse> kafkaTemplate;
    private final String requestsTopic;


    public KafkaProducer(KafkaTemplate<String, CalculationResponse> kafkaTemplate,
                         @Value("${calculator.responses.topic}") String requestsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.requestsTopic = requestsTopic;
    }

    public void sendRequest(CalculationResponse response) {
        logger.info(LoggingConstants.LOG_KAFKA_SEND,"calculation-responses");
        kafkaTemplate.send(requestsTopic, response);
    }
}