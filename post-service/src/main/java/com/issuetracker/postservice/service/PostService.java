package com.issuetracker.postservice.service;

import com.issuetracker.postservice.client.UserServiceClient;
import com.issuetracker.postservice.dto.ApiResponse;
import com.issuetracker.postservice.dto.CreatePostRequest;
import com.issuetracker.postservice.dto.UpdatePostRequest;
import com.issuetracker.postservice.dto.UserDTO;
import com.issuetracker.postservice.event.PostEvent;
import com.issuetracker.postservice.model.Department;
import com.issuetracker.postservice.model.Post;
import com.issuetracker.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostPublisher postPublisher;
    private final PostWebSocketService webSocketService;
    private final UserServiceClient userServiceClient;
    private final com.issuetracker.postservice.publisher.UpdatePostPublisher updatePostPublisher;
    private final com.issuetracker.postservice.publisher.UpvotePublisher upvotePublisher;
    private final com.issuetracker.postservice.publisher.UserSupportPublisher userSupportPublisher;

    // Synchronous - Direct save to database
    public Post createPost(CreatePostRequest request) {
        log.info("Creating post synchronously");
        
        Post post = Post.builder()
                .title(request.getTitle())
                .problemStatement(request.getProblemStatement())
                .userId(request.getUserId())
                .dept(request.getDept())
                .postImg(request.getPostImg())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return postRepository.save(post);
    }

    // Asynchronous - Publishes to Queue FIRST (alternative method)
    public void createPostAsync(CreatePostRequest request) {
        log.info("Creating post asynchronously for userId: {}", request.getUserId());
        
        // 🔒 VALIDATE USER EXISTS via Feign Client call to User Service
        try {
            log.info("Validating user with User Service...");
            ApiResponse<UserDTO> response = userServiceClient.getUserById(request.getUserId());
            
            if (response.getData() == null) {
                throw new RuntimeException("User not found with ID: " + request.getUserId());
            }
            
            log.info("User validated successfully: {} (role: {})", 
                    response.getData().getName(), response.getData().getRole());
            
        } catch (FeignException.NotFound e) {
            log.error("User not found: {}", request.getUserId());
            throw new RuntimeException("User not found with ID: " + request.getUserId());
        } catch (FeignException e) {
            log.error("Error calling User Service: {}", e.getMessage());
            throw new RuntimeException("Failed to validate user: " + e.getMessage());
        }
        
        // User exists! Now create the post event
        PostEvent event = PostEvent.builder()
                .title(request.getTitle())
                .problemStatement(request.getProblemStatement())
                .userId(request.getUserId())
                .dept(request.getDept())
                .postImg(request.getPostImg())
                .timestamp(LocalDateTime.now())
                .build();
        
        // Push to RabbitMQ Queue FIRST
        postPublisher.publishPostEvent(event);
        
        log.info("Post creation request accepted for validated user");
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByDepartment(Department dept) {
        return postRepository.findByDept(dept);
    }

    public List<Post> getUnresolvedPosts() {
        // Assuming posts without status field or status null/false are unresolved
        return postRepository.findAll().stream()
                .filter(post -> post.getStatus() == null || !post.getStatus())
                .toList();
    }
    
    // Helper method to convert Post to PostResponse with user details
    private com.issuetracker.postservice.dto.PostResponse convertToPostResponse(Post post) {
        // Fetch user details from user-service
        UserDTO user = null;
        try {
            ApiResponse<UserDTO> response = userServiceClient.getUserById(post.getUserId());
            user = response.getData();
        } catch (Exception e) {
            log.error("Failed to fetch user details for userId: {}", post.getUserId(), e);
            // Create a fallback user
            user = UserDTO.builder()
                    .id(post.getUserId())
                    .name("Anonymous")
                    .build();
        }
        
        return com.issuetracker.postservice.dto.PostResponse.builder()
                ._id(post.getId())
                .title(post.getTitle())
                .problemStatement(post.getProblemStatement())
                .userId(post.getUserId())
                .user(user)
                .dept(post.getDept())
                .postImg(post.getPostImg())
                .ratingQuantity(post.getRatingQuantity())
                .support(post.getSupport())
                .status(post.getStatus())
                .opinions(new ArrayList<>())  // Empty for now
                .UpVote(post.getRatingQuantity())  // Use ratingQuantity as upvote count
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .photo(post.getPostImg() != null && !post.getPostImg().isEmpty() ? post.getPostImg().get(0) : null)
                .build();
    }
    
    // New method that returns posts with user details
    public List<com.issuetracker.postservice.dto.PostResponse> getUnresolvedPostsWithUserDetails() {
        log.info("Fetching unresolved posts with user details");
        
        List<Post> posts = postRepository.findAll().stream()
                .filter(post -> post.getStatus() == null || !post.getStatus())
                .toList();
        
        return posts.stream()
                .map(this::convertToPostResponse)
                .toList();
    }

    public List<Post> getUnresolvedPostsByDept(Department dept) {
        return postRepository.findByDept(dept).stream()
                .filter(post -> post.getStatus() == null || !post.getStatus())
                .toList();
    }

    public List<Post> getResolvedPosts() {
        return postRepository.findAll().stream()
                .filter(post -> post.getStatus() != null && post.getStatus())
                .toList();
    }
    
    // New method that returns resolved posts with user details
    public List<com.issuetracker.postservice.dto.PostResponse> getResolvedPostsWithUserDetails() {
        log.info("Fetching resolved posts with user details");
        
        List<Post> posts = postRepository.findAll().stream()
                .filter(post -> post.getStatus() != null && post.getStatus())
                .toList();
        
        return posts.stream()
                .map(this::convertToPostResponse)
                .toList();
    }

    public List<Post> getResolvedPostsByDept(Department dept) {
        return postRepository.findByDept(dept).stream()
                .filter(post -> post.getStatus() != null && post.getStatus())
                .toList();
    }

    public Post getPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    public List<Post> getPostsByUserId(String userId) {
        return postRepository.findByUserId(userId);
    }

    public Post addSupport(String postId, String userId) {
        log.info("Adding support for post {} by user {}", postId, userId);
        
        // 🔒 VALIDATE USER EXISTS via Feign Client
        try {
            log.info("Validating user with User Service...");
            ApiResponse<UserDTO> response = userServiceClient.getUserById(userId);
            
            if (response.getData() == null) {
                throw new RuntimeException("User not found with ID: " + userId);
            }
            
            log.info("User validated successfully: {}", response.getData().getName());
            
        } catch (FeignException.NotFound e) {
            log.error("User not found: {}", userId);
            throw new RuntimeException("User not found with ID: " + userId);
        } catch (FeignException e) {
            log.error("Error calling User Service: {}", e.getMessage());
            throw new RuntimeException("Failed to validate user: " + e.getMessage());
        }
        
        // User exists! Now add support
        Post post = getPostById(postId);
        if (!post.getSupport().contains(userId)) {
            post.getSupport().add(userId);
            post.setRatingQuantity(post.getSupport().size());
            post.setUpdatedAt(LocalDateTime.now());
            Post updatedPost = postRepository.save(post);
            
            // ✅ UPDATE USER'S SUPPORTED LIST via User Service
            try {
                log.info("Updating user's supported list...");
                userServiceClient.addSupportedPost(userId, postId);
                log.info("User's supported list updated successfully");
            } catch (Exception e) {
                log.error("Failed to update user's supported list: {}", e.getMessage());
                // Don't fail the entire operation if user update fails
            }
            
            // 🔔 BROADCAST UPVOTE VIA WEBSOCKET - Real-time updates!
            webSocketService.broadcastUpvote(updatedPost);
            
            log.info("Support added successfully. Total supports: {}", updatedPost.getRatingQuantity());
            return updatedPost;
        }
        
        log.info("User already supported this post");
        return post;
    }

    public Post updatePost(String id, UpdatePostRequest request) {
        Post post = getPostById(id);
        
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            post.setProblemStatement(request.getDescription());
        }
        if (request.getLocation() != null) {
            // Note: Post model doesn't have location field in current schema
            // Add if needed
        }
        if (request.getStatus() != null) {
            post.setStatus(request.getStatus());
        }
        
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void deletePost(String id) {
        // Check if post exists and is not resolved
        Post post = getPostById(id);
        
        if (post.getStatus() != null && post.getStatus()) {
            throw new RuntimeException("Cannot delete a resolved post");
        }
        
        postRepository.deleteById(id);
        log.info("Post deleted successfully: {}", id);
    }

    // ASYNC UPDATE - Publishes to Queue FIRST for high availability
    public void updatePostAsync(String id, UpdatePostRequest request) {
        log.info("Updating post asynchronously: {}", id);
        
        // Verify post exists
        Post post = getPostById(id);
        
        // Create update event
        com.issuetracker.postservice.event.UpdatePostEvent event = 
            com.issuetracker.postservice.event.UpdatePostEvent.builder()
                .postId(id)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .status(request.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
        
        // Push to RabbitMQ Queue FIRST
        updatePostPublisher.publishUpdateEvent(event);
        
        log.info("Post update request accepted and queued");
    }

    // ASYNC UPVOTE - Publishes to Queue FIRST for high availability
    public void addSupportAsync(String postId, String userId) {
        log.info("Adding support asynchronously for post {} by user {}", postId, userId);
        
        // 🔒 VALIDATE USER EXISTS via Feign Client
        try {
            log.info("Validating user with User Service...");
            ApiResponse<UserDTO> response = userServiceClient.getUserById(userId);
            
            if (response.getData() == null) {
                throw new RuntimeException("User not found with ID: " + userId);
            }
            
            log.info("User validated successfully: {}", response.getData().getName());
            
        } catch (FeignException.NotFound e) {
            log.error("User not found: {}", userId);
            throw new RuntimeException("User not found with ID: " + userId);
        } catch (FeignException e) {
            log.error("Error calling User Service: {}", e.getMessage());
            throw new RuntimeException("Failed to validate user: " + e.getMessage());
        }
        
        // Verify post exists
        Post post = getPostById(postId);
        
        // Create upvote event
        com.issuetracker.postservice.event.UpvoteEvent event = 
            com.issuetracker.postservice.event.UpvoteEvent.builder()
                .postId(postId)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();
        
        // Push to RabbitMQ Queue FIRST
        upvotePublisher.publishUpvoteEvent(event);
        
        log.info("Upvote request accepted and queued");
    }
}