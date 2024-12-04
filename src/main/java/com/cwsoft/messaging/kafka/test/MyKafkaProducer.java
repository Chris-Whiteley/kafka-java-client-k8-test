package com.cwsoft.messaging.kafka.test;

import com.cwsoft.messaging.kafka.AbstractKafkaProducer;

import java.util.Properties;

public class MyKafkaProducer extends AbstractKafkaProducer<MyMessage> {

    public MyKafkaProducer(Properties kafkaProperties) {
        super(kafkaProperties);
    }

    @Override
    public String getDestination(MyMessage message) {
        return message.getTopic(); // Extract topic from your custom message
    }

    @Override
    public String getMessageName(MyMessage message) {
        return message.getId(); // Extract unique ID for the message
    }

    @Override
    public String encode(MyMessage message) {
        return message.toJson(); // Convert message to JSON (or other format)
    }
}
