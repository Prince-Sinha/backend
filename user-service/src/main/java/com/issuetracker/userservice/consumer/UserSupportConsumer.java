package com.issuetracker.userservice.consumer;

import com.issuetracker.userservice.event.UserSupportEvent;
import com.issuetracker.userservice.model.User;
import com.issuetracker.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSupportConsumer {

    private final UserRepository userRepository;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consumeUserSupportEvent(UserSupportEvent event) {
        try {
            log.info("Consuming user support event: User {} supporting Post {}", 
                    event.getUserId(), event.getPostId());
            
            // Get user from database
            User user = userRepository.findById(event.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + event.getUserId()));
            
            // Add postId to supported list if not already present
            if (!user.getSupported().contains(event.getPostId())) {
                user.getSupported().add(event.getPostId());
                userRepository.save(user);
                log.info("User's supported list updated successfully. User {} now supports {} posts", 
                        event.getUserId(), user.getSupported().size());
            } else {
                log.info("User already has this post in supported list");
            }
            
        } catch (Exception e) {
            log.error("Error processing user support event: {}", e.getMessage(), e);
            // In production, you might want to send to a dead letter queue
        }
    }
}
