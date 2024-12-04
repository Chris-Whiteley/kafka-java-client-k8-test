package com.cwsoft.messaging.kafka.test;

import com.cwsoft.messaging.ClosableConsumer;
import com.cwsoft.messaging.ClosableProducer;
import com.cwsoft.messaging.kafka.ConsumerConfig;
import com.cwsoft.messaging.kafka.ProducerConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class KafkaIntegrationTest {

    private static ClosableProducer<MyMessage> producer;
    private static ClosableConsumer<MyMessage> consumer;
    private static final String BOOTSTRAP_SERVERS = System.getenv("BOOTSTRAP_SERVER");
    private static final String CONFIG_PATH = "/etc/kafka/secrets/client_security.properties";

    public static void main(String[] args) {
        try {
            setUp();
            testProducerConsumerIntegration();
            tearDown();
            System.exit(0); // Exit code 0 for success
        } catch (Exception e) {
            log.error("Error in main method", e);
            System.exit(1); // Exit code 1 for failure
        }
    }

    // Initialize the Kafka producer and consumer
    private static void setUp() {
        if (BOOTSTRAP_SERVERS == null || BOOTSTRAP_SERVERS.isEmpty()) {
            throw new IllegalStateException("BOOTSTRAP_SERVER environment variable is not set or empty");
        }

        Properties kafkaProperties = new Properties();

        // Load properties from client_security.properties
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            kafkaProperties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Kafka configuration file", e);
        }

        // Set the bootstrap servers from the environment variable
        kafkaProperties.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);

        // Create producer and consumer configurations using the loaded properties
        ProducerConfig producerConfig = ProducerConfig.builder()
                .bootstrapServers(BOOTSTRAP_SERVERS)
                .securityProtocol(kafkaProperties.getProperty("security.protocol"))
                .truststorePath(kafkaProperties.getProperty("ssl.truststore.location"))
                .truststorePassword(kafkaProperties.getProperty("ssl.truststore.password"))
                .build();

        ConsumerConfig consumerConfig = ConsumerConfig.builder()
                .bootstrapServers(BOOTSTRAP_SERVERS)
                .groupId("test-group")
                .securityProtocol(kafkaProperties.getProperty("security.protocol"))
                .truststorePath(kafkaProperties.getProperty("ssl.truststore.location"))
                .truststorePassword(kafkaProperties.getProperty("ssl.truststore.password"))
                .build();

        // Initialize producer and consumer
        producer = new MyKafkaProducer(producerConfig.getProperties());
        consumer = new MyKafkaConsumer(consumerConfig.getProperties(), MyMessage.topic());
    }

    private static void testProducerConsumerIntegration() {
        // Create and send the first test message
        MyMessage messageToSend1 = new MyMessage("Hello, Kafka!");
        producer.produce(messageToSend1);

        // Create and send the second test message
        MyMessage messageToSend2 = new MyMessage("Hello again, Kafka!");
        producer.produce(messageToSend2);

        // Consume the first message
        Optional<MyMessage> consumedMessage1 = consumer.consume(Duration.ofSeconds(5));
        if (consumedMessage1.isPresent() && "Hello, Kafka!".equals(consumedMessage1.get().getMessage())) {
            log.info("First message consumed successfully: [{}]", consumedMessage1.get().getMessage());
        } else {
            log.error ("First message consumption failed or message content mismatch.");
        }

        // Consume the second message
        Optional<MyMessage> consumedMessage2 = consumer.consume(Duration.ofSeconds(5));
        if (consumedMessage2.isPresent() && "Hello again, Kafka!".equals(consumedMessage2.get().getMessage())) {
            log.info("Second message consumed successfully: [{}]", consumedMessage2.get().getMessage());
        } else {
            log.error ("Second message consumption failed or message content mismatch.");
        }
    }

    public static void tearDown() {
        if (producer != null) {
            log.info ("About to close producer");
            producer.close();
            log.info ("producer closed");
        }
        if (consumer != null) {
            log.info("About to close consumer");
            consumer.close();
            log.info("consumer closed");
        }
    }

}

