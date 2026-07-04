package com.europe.sepa.paymentorchestrator.infrastructure.kafka;

import com.europe.sepa.paymentorchestrator.domain.event.DomainEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for Stockholm.
 * Configures topics, producers, consumers for local development and testing.
 */
@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class KafkaConfiguration {
    
    private final KafkaTopicProperties topicProperties;
    private final KafkaProperties kafkaProperties;
    
    public KafkaConfiguration(KafkaTopicProperties topicProperties, KafkaProperties kafkaProperties) {
        this.topicProperties = topicProperties;
        this.kafkaProperties = kafkaProperties;
    }
    
    // ============================================
    // Topic Configuration
    // ============================================
    
    @Bean
    @ConditionalOnProperty(prefix = "stockholm.kafka", name = "create-topics", havingValue = "true", matchIfMissing = false)
    public NewTopic paymentInitiatedTopic() {
        return TopicBuilder.name(topicProperties.getPaymentInitiated())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicationFactor())
                .build();
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "stockholm.kafka", name = "create-topics", havingValue = "true", matchIfMissing = false)
    public NewTopic paymentValidatedTopic() {
        return TopicBuilder.name(topicProperties.getPaymentValidated())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicationFactor())
                .build();
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "stockholm.kafka", name = "create-topics", havingValue = "true", matchIfMissing = false)
    public NewTopic settlementCompletedTopic() {
        return TopicBuilder.name(topicProperties.getSettlementCompleted())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicationFactor())
                .build();
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "stockholm.kafka", name = "create-topics", havingValue = "true", matchIfMissing = false)
    public NewTopic settlementFailedTopic() {
        return TopicBuilder.name(topicProperties.getSettlementFailed())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicationFactor())
                .build();
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "stockholm.kafka", name = "create-topics", havingValue = "true", matchIfMissing = false)
    public NewTopic anomalyDetectedTopic() {
        return TopicBuilder.name(topicProperties.getAnomalyDetected())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicationFactor())
                .build();
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "stockholm.kafka", name = "create-topics", havingValue = "true", matchIfMissing = false)
    public NewTopic ledgerUpdatedTopic() {
        return TopicBuilder.name(topicProperties.getLedgerUpdated())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicationFactor())
                .build();
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "stockholm.kafka", name = "create-topics", havingValue = "true", matchIfMissing = false)
    public NewTopic reportGeneratedTopic() {
        return TopicBuilder.name(topicProperties.getReportGenerated())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicationFactor())
                .build();
    }
    
    // ============================================
    // Producer Configuration
    // ============================================
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        String bootstrapServers = String.join(",", kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    // ============================================
    // Consumer Configuration
    // ============================================
    
    @Bean
    public ConsumerFactory<String, DomainEvent> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        String bootstrapServers = String.join(",", kafkaProperties.getBootstrapServers());
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "stockholm-consumer-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, DomainEvent.class.getName());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.europe.sepa.paymentorchestrator.domain.event");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }
    
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, DomainEvent>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DomainEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}

