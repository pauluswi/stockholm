package com.europe.sepa.resilience.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for resilience monitor.
 * Sets up consumers for anomaly.detected and settlement.failed topics.
 */
@Configuration
@EnableKafka
public class ResilienceKafkaConfiguration {

    private final KafkaProperties kafkaProperties;
    private final ResilienceKafkaProperties resilienceKafkaProperties;

    public ResilienceKafkaConfiguration(KafkaProperties kafkaProperties,
                                        ResilienceKafkaProperties resilienceKafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        this.resilienceKafkaProperties = resilienceKafkaProperties;
    }

    /**
     * Kafka Admin for topic creation.
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                String.join(",", kafkaProperties.getBootstrapServers()));
        return new KafkaAdmin(configs);
    }

    /**
     * Create anomaly.detected topic.
     */
    @Bean
    public NewTopic anomalyDetectedTopic() {
        return TopicBuilder.name(resilienceKafkaProperties.getAnomalyDetected())
                .partitions(resilienceKafkaProperties.getPartitions())
                .replicas(resilienceKafkaProperties.getReplicationFactor())
                .build();
    }

    /**
     * Create settlement.failed topic.
     */
    @Bean
    public NewTopic settlementFailedTopic() {
        return TopicBuilder.name(resilienceKafkaProperties.getSettlementFailed())
                .partitions(resilienceKafkaProperties.getPartitions())
                .replicas(resilienceKafkaProperties.getReplicationFactor())
                .build();
    }

    /**
     * Consumer factory for generic message consumption.
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                String.join(",", kafkaProperties.getBootstrapServers()));
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "resilience-monitor-group");
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(configs);
    }

    /**
     * Producer factory used by integration tests and any future operational events.
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                String.join(",", kafkaProperties.getBootstrapServers()));
        configs.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configs.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Listener container factory for Kafka listeners.
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(
                new org.springframework.kafka.listener.DefaultErrorHandler()
        );
        factory.setConcurrency(1);
        factory.setConsumerFactory(consumerFactory());
        factory.setRecordMessageConverter(new StringJsonMessageConverter());
        return factory;
    }
}

