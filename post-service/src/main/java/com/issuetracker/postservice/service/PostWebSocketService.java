package com.issuetracker.postservice.service;

import com.issuetracker.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastUpvote(Post post) {
        log.info("Broadcasting upvote for post: {}", post.getId());
        
        Map<String, Object> update = new HashMap<>();
        update.put("postId", post.getId());
        update.put("supportCount", post.getSupport().size());
        update.put("ratingQuantity", post.getRatingQuantity());
        update.put("support", post.getSupport());
        update.put("type", "UPVOTE");
        update.put("timestamp", System.currentTimeMillis());
        
        // Broadcast to all clients subscribed to /topic/posts/{postId}
        String destination = "/topic/posts/" + post.getId();
        messagingTemplate.convertAndSend(destination, update);
        
        log.info("Upvote broadcasted successfully to: {}", destination);
    }

    public void broadcastNewPost(Post post) {
        log.info("Broadcasting new post: {}", post.getId());
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NEW_POST");
        notification.put("post", post);
        notification.put("timestamp", System.currentTimeMillis());
        
        // Broadcast to all clients subscribed to /topic/posts/new
        messagingTemplate.convertAndSend("/topic/posts/new", notification);
        
        log.info("New post broadcasted successfully");
    }
}
