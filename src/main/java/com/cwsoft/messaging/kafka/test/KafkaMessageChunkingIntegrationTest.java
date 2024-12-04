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
public class KafkaMessageChunkingIntegrationTest {

    private static ClosableProducer<MyChunkingMessage> producer;
    private static ClosableConsumer<MyChunkingMessage> consumer;
    private static final String BOOTSTRAP_SERVERS = System.getenv("BOOTSTRAP_SERVER");
    private static final String CONFIG_PATH = "/etc/kafka/secrets/client_security.properties";

    public static void main(String[] args) {
        try {
            setUp();
            testChunkingProducerConsumerIntegration();
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
        int chunkSize = 10;  // note. In normal use chunkSize would be a lot bigger (nearer max kafka message size)
        producer = new MyKafkaChunkingProducer(producerConfig.getProperties(), chunkSize);
        consumer = new MyKafkaChunkingConsumer(consumerConfig.getProperties(), MyChunkingMessage.topic());
    }

    private static void testChunkingProducerConsumerIntegration() {
        // Create a test message
        MyChunkingMessage messageToSend = new MyChunkingMessage("Hello, Kafka! This is a larger message to demonstrate message chunking");

        // Send the message using the producer
        producer.produce(messageToSend);

        // Consume the message with the consumer
        Optional<MyChunkingMessage> consumedMessage = consumer.consume(Duration.ofSeconds(10));

        // Log results
        if (consumedMessage.isPresent() &&
                "Hello, Kafka! This is a larger message to demonstrate message chunking".equals(consumedMessage.get().getMessage())) {
            log.info("Chunked message consumed successfully: [{}]", consumedMessage.get().getMessage());
        } else {
            log.error("Chunked message consumption failed or message content mismatch.");
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

