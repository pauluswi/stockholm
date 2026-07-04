package com.europe.sepa.reporting.testsupport;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Helper for deterministic Kafka topic lifecycle in integration tests.
 * Accepts a bootstrap-servers string so there is no direct dependency on
 * {@code EmbeddedKafkaBroker} at the call site.
 *
 * <pre>
 *     &#64;Value("${spring.embedded.kafka.brokers}")
 *     String brokers;
 *
 *     TestKafkaTopics kafkaTopics;
 *
 *     &#64;BeforeAll
 *     void setUp() {
 *         kafkaTopics = new TestKafkaTopics(brokers);
 *         kafkaTopics.recreateTopics("ledger.updated");
 *     }
 *
 *     &#64;AfterAll
 *     void tearDown() {
 *         kafkaTopics.deleteTopics("ledger.updated");
 *     }
 * </pre>
 */
public final class TestKafkaTopics {

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
            List<NewTopic> topics = Arrays.stream(topicNames)
                    .map(name -> new NewTopic(name, 1, (short) 1))
                    .collect(Collectors.toList());
            admin.createTopics(topics).all().get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create topics: " + Arrays.toString(topicNames), e);
        }
    }

    public void deleteTopics(String... topicNames) {
        try (AdminClient admin = adminClient()) {
            admin.deleteTopics(Arrays.asList(topicNames)).all().get(10, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            // Topics may not exist yet on first run — safe to swallow
        }
    }

    private AdminClient adminClient() {
        return AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
    }
}

