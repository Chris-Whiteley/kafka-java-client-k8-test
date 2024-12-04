package com.cwsoft.messaging.kafka.test;

import com.cwsoft.messaging.kafka.AbstractKafkaConsumer;

import java.util.Properties;

public class MyKafkaConsumer extends AbstractKafkaConsumer<MyMessage> {

    public MyKafkaConsumer(Properties properties, String topic) {
        super(properties, topic);
    }

    @Override
    public MyMessage decode(String encodedMessage) {
        return MyMessage.fromJson(encodedMessage); // Assuming MyMessage has a fromJson method
    }
}
