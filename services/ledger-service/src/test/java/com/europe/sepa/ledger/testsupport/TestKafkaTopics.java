package com.europe.sepa.ledger.testsupport;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Small helper for deterministic Kafka topic setup in ledger integration tests.
 */
public final class TestKafkaTopics {

    private static final short REPLICATION_FACTOR = 1;
    private static final int PARTITIONS = 1;

    private final String bootstrapServers;

    public TestKafkaTopics(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public void recreateTopics(String... topicNames) {
        deleteTopics(topicNames);
        createTopics(topicNames);
    }

    public void createTopics(String... topicNames) {
        try (AdminClient admin = adminClient()) {
            for (String topicName : normalize(topicNames)) {
                NewTopic topic = TopicBuilder.name(topicName)
                        .partitions(PARTITIONS)
                        .replicas(REPLICATION_FACTOR)
                        .build();
                admin.createTopics(Set.of(topic)).all().get();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create Kafka test topics", ex);
        }
    }

    public void deleteTopics(String... topicNames) {
        try (AdminClient admin = adminClient()) {
            admin.deleteTopics(normalize(topicNames)).all().get();
        } catch (Exception ex) {
            // Deleting a missing topic is fine for test isolation; ignore and continue.
        }
    }

    private AdminClient adminClient() {
        return AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
    }

    private Set<String> normalize(String... topicNames) {
        return new LinkedHashSet<>(Arrays.asList(topicNames));
    }
}

