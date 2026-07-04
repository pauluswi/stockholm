package com.europe.sepa.ledger.kafka;

import com.europe.sepa.ledger.kafka.event.SettlementCompletedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
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
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for the ledger service.
 */
@Configuration
@EnableKafka
@EnableConfigurationProperties(LedgerKafkaProperties.class)
public class LedgerKafkaConfiguration {

    private final LedgerKafkaProperties topicProperties;
    private final KafkaProperties kafkaProperties;

    public LedgerKafkaConfiguration(LedgerKafkaProperties topicProperties, KafkaProperties kafkaProperties) {
        this.topicProperties = topicProperties;
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    @ConditionalOnProperty(prefix = "stockholm.kafka", name = "create-topics", havingValue = "true")
    public NewTopic settlementCompletedTopic() {
        return TopicBuilder.name(topicProperties.getSettlementCompleted())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicationFactor())
                .build();
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
    @ConditionalOnProperty(prefix = "stockholm.kafka", name = "create-topics", havingValue = "true")
    public NewTopic ledgerFailedTopic() {
        return TopicBuilder.name(topicProperties.getLedgerFailed())
                .partitions(topicProperties.getPartitions())
                .replicas(topicProperties.getReplicationFactor())
                .build();
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaProperties.getBootstrapServers()));
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

    @Bean
    public ConsumerFactory<String, SettlementCompletedEvent> settlementCompletedConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaProperties.getBootstrapServers()));
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "ledger-service-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<SettlementCompletedEvent> deserializer = new JsonDeserializer<>(SettlementCompletedEvent.class);
        deserializer.addTrustedPackages("com.europe.sepa.ledger.kafka.event");
        deserializer.addTrustedPackages("com.europe.sepa.settlement.kafka.event");
        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SettlementCompletedEvent>> settlementCompletedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SettlementCompletedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(settlementCompletedConsumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler());
        factory.setConcurrency(1);
        return factory;
    }
}

