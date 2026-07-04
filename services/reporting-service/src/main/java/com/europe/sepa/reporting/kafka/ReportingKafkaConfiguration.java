package com.europe.sepa.reporting.kafka;

import com.europe.sepa.reporting.kafka.event.LedgerUpdatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka consumer configuration for the reporting service.
 * Consumes {@code ledger.updated} events and triggers report generation.
 */
@Configuration
@EnableKafka
@EnableConfigurationProperties(ReportingKafkaProperties.class)
public class ReportingKafkaConfiguration {

    private final ReportingKafkaProperties topicProperties;
    private final KafkaProperties kafkaProperties;

    public ReportingKafkaConfiguration(ReportingKafkaProperties topicProperties,
                                       KafkaProperties kafkaProperties) {
        this.topicProperties = topicProperties;
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    @ConditionalOnProperty(prefix = "stockholm.kafka", name = "create-topics", havingValue = "true")
    public NewTopic ledgerUpdatedTopic() {
        return TopicBuilder.name(topicProperties.getLedgerUpdated())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicationFactor())
                .build();
    }

    @Bean
    public ConsumerFactory<String, LedgerUpdatedEvent> ledgerUpdatedConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                String.join(",", kafkaProperties.getBootstrapServers()));
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "reporting-service-group");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<LedgerUpdatedEvent> deserializer =
                new JsonDeserializer<>(LedgerUpdatedEvent.class);
        deserializer.addTrustedPackages("com.europe.sepa.reporting.kafka.event");
        deserializer.addTrustedPackages("com.europe.sepa.ledger.kafka.event");
        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, LedgerUpdatedEvent>>
    ledgerUpdatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LedgerUpdatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(ledgerUpdatedConsumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler());
        factory.setConcurrency(1);
        return factory;
    }
}

