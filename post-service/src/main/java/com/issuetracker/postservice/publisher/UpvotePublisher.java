



package com.issuetracker.postservice.publisher;

import com.issuetracker.postservice.event.UpvoteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpvotePublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.upvote-routing-key}")
    private String upvoteRoutingKey;

    public void publishUpvoteEvent(UpvoteEvent event) {
        System.out.println("Publishing upvote event for post: " + event.getPostId() + " by user: " + event.getUserId());
        rabbitTemplate.convertAndSend(exchangeName, upvoteRoutingKey, event);
        System.out.println("Upvote event published successfully");
    }
}
