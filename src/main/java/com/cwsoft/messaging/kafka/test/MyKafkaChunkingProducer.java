package com.cwsoft.messaging.kafka.test;

import com.cwsoft.messaging.kafka.AbstractKafkaChunkingProducer;

import java.util.Properties;

public class MyKafkaChunkingProducer extends AbstractKafkaChunkingProducer<MyChunkingMessage> {

    public MyKafkaChunkingProducer(Properties kafkaProperties, int maxMessageSize) {
        super(kafkaProperties, maxMessageSize);
    }

    @Override
    public String getDestination(MyChunkingMessage message) {
        return message.getTopic(); // Extract topic from your custom message
    }

    @Override
    public String getMessageName(MyChunkingMessage message) {
        return message.getId(); // Extract unique ID for the message
    }

    @Override
    public String encode(MyChunkingMessage message) {
        return message.toJson(); // Convert message to JSON (or other format)
    }
}
