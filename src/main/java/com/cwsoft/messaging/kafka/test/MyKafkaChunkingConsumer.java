package com.cwsoft.messaging.kafka.test;

import com.cwsoft.messaging.kafka.AbstractKafkaChunkingConsumer;

import java.util.Properties;

public class MyKafkaChunkingConsumer extends AbstractKafkaChunkingConsumer<MyChunkingMessage> {

    public MyKafkaChunkingConsumer(Properties properties, String topic) {
        super(properties, topic);
    }

    @Override
    public MyChunkingMessage decode(String encodedMessage) {
        return MyChunkingMessage.fromJson(encodedMessage); // Assuming MyMessage has a fromJson method
    }
}
