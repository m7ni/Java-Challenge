package com.challenge.wit.rest.kafka;

import com.challenge.wit.shared.dto.CalculationRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, CalculationRequest> kafkaTemplate(ProducerFactory<String, CalculationRequest> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, CalculationRequest> producerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.producer.key-serializer}") String keySerializer,
            @Value("${spring.kafka.producer.value-serializer}") String valueSerializer) {

        Map<String, Object> configs = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer
        );
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public ConsumerFactory<String, CalculationRequest> consumerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.producer.key-serializer}") String keySerializer,
            @Value("${spring.kafka.producer.value-serializer}") String valueSerializer) {

        Map<String, Object> configs = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keySerializer,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueSerializer
        );
        return new DefaultKafkaConsumerFactory<>(configs);
    }
}
