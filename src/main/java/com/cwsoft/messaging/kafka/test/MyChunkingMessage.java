package com.cwsoft.messaging.kafka.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyChunkingMessage {
    private String message;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonIgnore
    public String getTopic() {
        return topic();
    }

    public static String topic() {return  "myChunkingTopic";}

    @JsonIgnore
    public String getId() {
        return "MyChunkingMessage";
    }

    // Converts the MyMessage object to a JSON string
    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert MyChunkingMessage to JSON", e);
        }
    }

    // Creates a MyMessage object from a JSON string
    public static MyChunkingMessage fromJson(String json) {
        try {
            return objectMapper.readValue(json, MyChunkingMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON into MyChunkingMessage", e);
        }
    }
}
