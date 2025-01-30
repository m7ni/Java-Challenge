package com.challenge.wit.calculator.kafka;

import com.challenge.wit.shared.dto.CalculationResponse;
import com.challenge.wit.shared.logging.LoggingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaException;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, CalculationResponse> kafkaTemplate;
    private final String responsesTopic;

    public KafkaProducer(KafkaTemplate<String, CalculationResponse> kafkaTemplate,
                         @Value("${calculator.responses.topic}") String responsesTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.responsesTopic = responsesTopic;
    }

    /**
     * Sends a CalculationResponse to the specified Kafka topic.
     *
     * @param response The CalculationResponse to send.
     */
    public void sendRequest(CalculationResponse response) {
        // Log the Kafka send action
        logger.info(LoggingConstants.LOG_KAFKA_SEND, "calculation-responses");

        try {
            // Send the CalculationResponse to Kafka and wait for the send to complete
            kafkaTemplate.send(responsesTopic, response).get(5, TimeUnit.SECONDS);

            // Log the successful send
            logger.debug(LoggingConstants.LOG_CALCULATION_RESULT,
                    "Message sent successfully to topic",
                    responsesTopic);
        } catch (TimeoutException e) {
            // Log a timeout error if the send operation exceeds the specified timeout
            logger.error(LoggingConstants.LOG_ERROR,
                    "KafkaSendTimeout",
                    "Sending message to Kafka timed out after 5 seconds: " + e.getMessage(),
                    e);
        } catch (Exception e) {
            // Log any other exceptions that occur during the send operation
            logger.error(LoggingConstants.LOG_ERROR,
                    "KafkaSendFailure",
                    "Failed to send calculation response to Kafka: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Retrieves the name of the responses Kafka topic.
     *
     * @return The responses topic name.
     */
    public String getResponsesTopic() {
        return responsesTopic;
    }
}
