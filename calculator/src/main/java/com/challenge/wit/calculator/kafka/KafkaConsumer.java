package com.challenge.wit.calculator.kafka;

import com.challenge.wit.calculator.service.CalculationService;
import com.challenge.wit.shared.dto.CalculationRequest;
import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.shared.logging.LoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final CalculationService calculationService;
    private final KafkaProducer kafkaProducer;

    public KafkaConsumer(CalculationService calculationService,
                         KafkaProducer kafkaProducer) {
        this.calculationService = calculationService;
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * Listens to the 'calculator-requests' Kafka topic and processes incoming CalculationRequest messages.
     *
     * @param request The CalculationRequest received from Kafka.
     */
    @KafkaListener(topics = "${calculator.requests.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(CalculationRequest request) {
        // Log the reception of a new Kafka message
        logger.info(LoggingConstants.LOG_KAFKA_RECEIVE, "calculator-requests");
        logger.info(LoggingConstants.LOG_CALCULATION_REQUEST, request.getOperation(), request.getOperandA(), request.getOperandB());

        CalculationResponse response;
        try {
            // Perform the calculation using the CalculationService
            response = calculationService.calculate(request);

            // If an error occurred during calculation, log it
            if (response.getError() != null) {
                logger.error(LoggingConstants.LOG_ERROR, "CalculationModuleError", response.getError());
            }
        } catch (Exception ex) {
            // Log unexpected exceptions and create an error response
            logger.error(LoggingConstants.LOG_ERROR, "UnexpectedError", ex.getMessage());
            response = new CalculationResponse(request.getRequestId(), null, ex.getMessage());
        }

        try {
            // Send the CalculationResponse back to Kafka
            kafkaProducer.sendRequest(response);
        } catch (Exception ex) {
            // Log any failures that occur while sending the response to Kafka
            logger.error(LoggingConstants.LOG_ERROR, "KafkaSendFailure",
                    "Failed to send calculation response to Kafka: " + ex.getMessage());
        }
    }
}
