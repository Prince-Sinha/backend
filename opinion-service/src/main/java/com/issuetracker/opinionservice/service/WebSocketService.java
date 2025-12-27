package com.issuetracker.opinionservice.service;

import com.issuetracker.opinionservice.model.Opinion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastNewOpinion(Opinion opinion) {
        log.info("Broadcasting new opinion via WebSocket: {}", opinion.getId());
        
        // Broadcast to all clients subscribed to /topic/opinions/{postId}
        String destination = "/topic/opinions/" + opinion.getPostId();
        messagingTemplate.convertAndSend(destination, opinion);
        
        log.info("Opinion broadcasted successfully");
    }

    public void broadcastMessage(String destination, Object message) {
        log.info("Broadcasting message to: {}", destination);
        messagingTemplate.convertAndSend(destination, message);
    }
}
