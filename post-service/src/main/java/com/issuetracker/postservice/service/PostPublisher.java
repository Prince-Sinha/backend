package com.issuetracker.postservice.service;

import com.issuetracker.postservice.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public void publishPostEvent(PostEvent event) {
        log.info("Publishing post event to queue: {}", event);
        
        // 🚀 PUSH TO RABBITMQ QUEUE FIRST!
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        
        log.info("Post event published successfully");
    }
}