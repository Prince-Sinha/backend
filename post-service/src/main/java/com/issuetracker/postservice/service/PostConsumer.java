package com.issuetracker.postservice.service;

import com.issuetracker.postservice.event.PostEvent;
import com.issuetracker.postservice.model.Post;
import com.issuetracker.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostConsumer {

    private final PostRepository postRepository;
    private final PostWebSocketService webSocketService;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consumePostEvent(PostEvent event) {
        log.info("Consuming post event from queue: {}", event);
        
        try {
            // Create Post object from event
            Post post = Post.builder()
                    .title(event.getTitle())
                    .problemStatement(event.getProblemStatement())
                    .userId(event.getUserId())
                    .dept(event.getDept())
                    .postImg(event.getPostImg())
                    .createdAt(LocalDateTime.now())
                    .build();
            
            // 💾 SAVE TO DATABASE LAST!
            Post savedPost = postRepository.save(post);
            log.info("Post saved successfully with ID: {}", savedPost.getId());
            
            // 🔔 BROADCAST NEW POST VIA WEBSOCKET - Real-time updates!
            try {
                webSocketService.broadcastNewPost(savedPost);
                log.info("New post broadcasted via WebSocket to all clients");
            } catch (Exception wsException) {
                log.error("Failed to broadcast new post via WebSocket: {}", wsException.getMessage());
                // Don't fail the entire operation if WebSocket broadcast fails
            }
            
        } catch (Exception e) {
            log.error("Error processing post event: {}", e.getMessage());
            throw e; // RabbitMQ will retry
        }
    }
}

