package com.issuetracker.postservice.consumer;

import com.issuetracker.postservice.event.UpdatePostEvent;
import com.issuetracker.postservice.model.Post;
import com.issuetracker.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePostConsumer {

    private final PostRepository postRepository;

    @RabbitListener(queues = "${rabbitmq.update-queue}")
    public void consumeUpdateEvent(UpdatePostEvent event) {
        try {
            System.out.println("Consuming update event for post: " + event.getPostId());
            
            // Get post from database
            Post post = postRepository.findById(event.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found with id: " + event.getPostId()));
            
            // Update fields if provided
            if (event.getTitle() != null) {
                post.setTitle(event.getTitle());
            }
            if (event.getDescription() != null) {
                post.setProblemStatement(event.getDescription());
            }
            if (event.getStatus() != null) {
                post.setStatus(event.getStatus());
            }
            
            // Update timestamp
            post.setUpdatedAt(event.getTimestamp());
            
            // Save updated post
            postRepository.save(post);
            System.out.println("Post updated successfully: " + event.getPostId());
            
        } catch (Exception e) {
            System.err.println("Error processing update event: " + e.getMessage());
            e.printStackTrace();
            // In production, you might want to send to a dead letter queue
        }
    }
}
