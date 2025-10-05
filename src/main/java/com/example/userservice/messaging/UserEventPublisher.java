package com.example.userservice.messaging;

import com.example.userservice.event.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, UserEvent> kafka;

    @Value("${app.kafka.topic.users-events:users.events}")
    private String topic;

    public void publish(UserEvent.Operation op, Long id, String email, String name) {
        var evt = UserEvent.builder()
                .operation(op)
                .userId(id)
                .email(email)
                .name(name)
                .occurredAt(Instant.now())
                .build();
        kafka.send(topic, email, evt);
    }
}