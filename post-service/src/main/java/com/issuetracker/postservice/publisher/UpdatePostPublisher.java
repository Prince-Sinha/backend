package com.issuetracker.postservice.publisher;

import com.issuetracker.postservice.event.UpdatePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePostPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.update-routing-key}")
    private String updateRoutingKey;

    public void publishUpdateEvent(UpdatePostEvent event) {
        System.out.println("Publishing update event for post: " + event.getPostId());
        rabbitTemplate.convertAndSend(exchangeName, updateRoutingKey, event);
        System.out.println("Update event published successfully");
    }
}