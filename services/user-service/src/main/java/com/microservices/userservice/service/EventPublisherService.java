package com.microservices.userservice.service;

import com.microservices.userservice.model.event.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EventPublisherService {

    public void publishUserCreated(String userId, String email) {
        UserEvent event = UserEvent.builder()
                .userId(userId)
                .email(email)
                .timestamp(LocalDateTime.now())
                .eventType("UserCreated")
                .build();
        publishEvent(event);
    }

    public void publishUserUpdated(String userId, String email, List<String> updatedFields) {
        UserEvent event = UserEvent.builder()
                .userId(userId)
                .email(email)
                .updatedFields(updatedFields)
                .timestamp(LocalDateTime.now())
                .eventType("UserUpdated")
                .build();
        publishEvent(event);
    }

    private void publishEvent(UserEvent event) {
        // TODO: Implement actual message broker integration
        log.info("Publishing user event: {}", event);
    }
}