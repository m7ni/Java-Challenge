package com.challenge.wit.calculator.kafka;

import com.challenge.wit.calculator.service.CalculationService;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.shared.logging.LoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CalculationService.class);
    private final CalculationService calculationService;
    private final KafkaProducer kafkaProducer;

    public KafkaConsumer(CalculationService calculationService,
                         KafkaProducer kafkaProducer) {
        this.calculationService = calculationService;
        this.kafkaProducer = kafkaProducer;
    }

    @KafkaListener(topics = "${calculator.requests.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(CalculationRequest request) {
        logger.info(LoggingConstants.LOG_KAFKA_RECEIVE, "calculator-requests");
        logger.info(LoggingConstants.LOG_CALCULATION_REQUEST, request.getOperation(),request.getOperandA(),request.getOperandB());
        try{
            CalculationResponse response = calculationService.calculate(request);
            kafkaProducer.sendRequest(response);
        } catch (Exception ex){
            //log fail
        }

    }

}