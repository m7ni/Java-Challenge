package com.challenge.wit.calculator.kafka;

import com.challenge.wit.calculator.service.CalculationService;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
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
        logger.info("Received Kafka request: Operation={}, a={}, b={}, requestId ={}", request.getOperation(), request.getOperandA(), request.getOperandB(), MDC.get(MdcKafkaConsumerInterceptor.MDC_REQUEST_ID_KEY));
        try{
            CalculationResponse response = calculationService.calculate(request);
            kafkaProducer.sendRequest(response);
        } catch (Exception ex){
            //log fail
        }

    }

}