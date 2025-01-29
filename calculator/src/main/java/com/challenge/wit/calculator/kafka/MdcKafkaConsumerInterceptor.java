package com.challenge.wit.calculator.kafka;

import com.challenge.wit.shared.dto.CalculationRequest;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

public class MdcKafkaConsumerInterceptor implements ConsumerInterceptor<String, CalculationRequest> {
    private static final Logger logger = LoggerFactory.getLogger(MdcKafkaConsumerInterceptor.class);
    public static final String MDC_REQUEST_ID_KEY = "requestId";

    @Override
    public ConsumerRecords<String, CalculationRequest> onConsume(ConsumerRecords<String, CalculationRequest> records) {
        records.forEach(record -> {
            CalculationRequest request = record.value();
            if (request != null && request.getRequestId() != null) {
                MDC.put(MDC_REQUEST_ID_KEY, request.getRequestId());
                logger.debug("Added requestId {} to MDC from CalculationRequest", request.getRequestId());
            }
        });
        return records;
    }

    @Override
    public void onCommit(Map<org.apache.kafka.common.TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> offsets) {
        MDC.remove(MDC_REQUEST_ID_KEY);
    }

    @Override
    public void close() {
        MDC.clear();
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}