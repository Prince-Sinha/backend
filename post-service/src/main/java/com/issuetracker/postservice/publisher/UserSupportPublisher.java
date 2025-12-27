package com.issuetracker.postservice.publisher;

import com.issuetracker.postservice.event.UserSupportEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSupportPublisher {

    private final RabbitTemplate rabbitTemplate;

    // User Service queue details
    private static final String USER_EXCHANGE = "user.exchange";
    private static final String USER_SUPPORT_ROUTING_KEY = "user.support.routing.key";

    public void publishUserSupportEvent(UserSupportEvent event) {
        System.out.println("Publishing user support event: User " + event.getUserId() + " supported Post " + event.getPostId());
        rabbitTemplate.convertAndSend(USER_EXCHANGE, USER_SUPPORT_ROUTING_KEY, event);
        System.out.println("User support event published successfully to User Service queue");
    }
}
