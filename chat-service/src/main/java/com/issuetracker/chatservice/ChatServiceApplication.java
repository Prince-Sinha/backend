package com.issuetracker.chatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Main application class for Chat Service
 * Enables real-time chat between normal users and departments using WebSocket
 */
@SpringBootApplication
@EnableFeignClients
@EnableMongoRepositories
public class ChatServiceApplication {
 
 public static void main(String[] args) {
 SpringApplication.run(ChatServiceApplication.class, args);
 System.out.println("🚀 Chat Service started successfully on port 8084");
 System.out.println("📡 WebSocket endpoint: ws://localhost:8084/chat-websocket");
 }
}