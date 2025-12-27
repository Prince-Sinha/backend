package com.issuetracker.postservice.consumer;

import com.issuetracker.postservice.client.UserServiceClient;
import com.issuetracker.postservice.dto.ApiResponse;
import com.issuetracker.postservice.dto.UserDTO;
import com.issuetracker.postservice.event.UpvoteEvent;
import com.issuetracker.postservice.model.Post;
import com.issuetracker.postservice.repository.PostRepository;
import com.issuetracker.postservice.service.PostWebSocketService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpvoteConsumer {

    private final PostRepository postRepository;
    private final PostWebSocketService webSocketService;
    private final UserServiceClient userServiceClient;
    private final com.issuetracker.postservice.publisher.UserSupportPublisher userSupportPublisher;

    @RabbitListener(queues = "${rabbitmq.upvote-queue}")
    public void consumeUpvoteEvent(UpvoteEvent event) {
        try {
            log.info("Consuming upvote event for post: {} by user: {}", event.getPostId(), event.getUserId());
            
            // 🔒 VALIDATE USER EXISTS via Feign Client
            try {
                log.info("Validating user with User Service...");
                ApiResponse<UserDTO> response = userServiceClient.getUserById(event.getUserId());
                
                if (response.getData() == null) {
                    log.error("User not found with ID: {}", event.getUserId());
                    return; // Skip this event
                }
                
                log.info("User validated successfully: {}", response.getData().getName());
                
            } catch (FeignException.NotFound e) {
                log.error("User not found: {}", event.getUserId());
                return; // Skip this event
            } catch (FeignException e) {
                log.error("Error calling User Service: {}", e.getMessage());
                return; // Skip this event
            }
            
            // Get post from database
            Post post = postRepository.findById(event.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found with id: " + event.getPostId()));
            
            // Add support if not already present
            if (!post.getSupport().contains(event.getUserId())) {
                post.getSupport().add(event.getUserId());
                post.setRatingQuantity(post.getSupport().size());
                post.setUpdatedAt(event.getTimestamp());
                
                // Save to database
                Post updatedPost = postRepository.save(post);
                
                // ✅ UPDATE USER'S SUPPORTED LIST via RabbitMQ Queue (High Availability!)
                try {
                    log.info("Publishing user support event to User Service queue...");
                    com.issuetracker.postservice.event.UserSupportEvent userEvent = 
                        com.issuetracker.postservice.event.UserSupportEvent.builder()
                            .userId(event.getUserId())
                            .postId(event.getPostId())
                            .timestamp(event.getTimestamp())
                            .build();
                    userSupportPublisher.publishUserSupportEvent(userEvent);
                    log.info("User support event queued successfully");
                } catch (Exception e) {
                    log.error("Failed to queue user support event: {}", e.getMessage());
                    // Don't fail the entire operation if user event fails to queue
                }
                
                // 🔔 BROADCAST UPVOTE VIA WEBSOCKET - Real-time updates!
                webSocketService.broadcastUpvote(updatedPost);
                
                log.info("Upvote processed successfully. Total supports: {}", updatedPost.getRatingQuantity());
            } else {
                log.info("User already supported this post");
            }
            
        } catch (Exception e) {
            log.error("Error processing upvote event: {}", e.getMessage(), e);
            // In production, you might want to send to a dead letter queue
        }
    }
}

