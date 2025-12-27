


package com.issuetracker.postservice.controller;

import com.issuetracker.postservice.dto.ApiResponse;
import com.issuetracker.postservice.dto.CreatePostRequest;
import com.issuetracker.postservice.dto.UpdatePostRequest;
import com.issuetracker.postservice.dto.UpvoteRequest;
import com.issuetracker.postservice.model.Department;
import com.issuetracker.postservice.model.Post;
import com.issuetracker.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Post Management", description = "Post/Issue management endpoints")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    @Operation(summary = "Create a new post asynchronously via RabbitMQ (Queue FIRST - High Availability!)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createPost(@Valid @RequestBody CreatePostRequest request) {
        // ⚡ ASYNC: Queue FIRST → DB LAST (High Availability!)
        postService.createPostAsync(request);
        
        // Return 202 ACCEPTED immediately - don't wait for database!
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("title", request.getTitle());
        responseData.put("dept", request.getDept());
        responseData.put("message", "Post is being processed asynchronously");
        
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.<Map<String, Object>>builder()
                        .status("ACCEPTED")
                        .message("Post creation request accepted and queued")
                        .data(responseData)
                        .build());
    }

    @GetMapping
    @Operation(summary = "Get all posts")
    public ResponseEntity<ApiResponse<List<Post>>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(ApiResponse.<List<Post>>builder()
                .status("success")
                .data(posts)
                .build());
    }

    @GetMapping("/unresolved")
    @Operation(summary = "Get all unresolved posts with user details")
    public ResponseEntity<ApiResponse<List<com.issuetracker.postservice.dto.PostResponse>>> getUnresolvedPosts() {
        List<com.issuetracker.postservice.dto.PostResponse> posts = postService.getUnresolvedPostsWithUserDetails();
        return ResponseEntity.ok(ApiResponse.<List<com.issuetracker.postservice.dto.PostResponse>>builder()
                .status("success")
                .data(posts)
                .build());
    }

    @GetMapping("/unresolved/{dept}")
    @Operation(summary = "Get unresolved posts by department")
    public ResponseEntity<ApiResponse<List<Post>>> getUnresolvedPostsByDept(@PathVariable Department dept) {
        List<Post> posts = postService.getUnresolvedPostsByDept(dept);
        return ResponseEntity.ok(ApiResponse.<List<Post>>builder()
                .status("success")
                .data(posts)
                .build());
    }

    @GetMapping("/resolved")
    @Operation(summary = "Get all resolved posts with user details")
    public ResponseEntity<ApiResponse<List<com.issuetracker.postservice.dto.PostResponse>>> getResolvedPosts() {
        List<com.issuetracker.postservice.dto.PostResponse> posts = postService.getResolvedPostsWithUserDetails();
        return ResponseEntity.ok(ApiResponse.<List<com.issuetracker.postservice.dto.PostResponse>>builder()
                .status("success")
                .data(posts)
                .build());
    }

    @GetMapping("/resolved/{dept}")
    @Operation(summary = "Get resolved posts by department")
    public ResponseEntity<ApiResponse<List<Post>>> getResolvedPostsByDept(@PathVariable Department dept) {
        List<Post> posts = postService.getResolvedPostsByDept(dept);
        return ResponseEntity.ok(ApiResponse.<List<Post>>builder()
                .status("success")
                .data(posts)
                .build());
    }

    @GetMapping("/department/{dept}")
    @Operation(summary = "Get posts by department")
    public ResponseEntity<ApiResponse<List<Post>>> getPostsByDepartment(@PathVariable Department dept) {
        List<Post> posts = postService.getPostsByDepartment(dept);
        return ResponseEntity.ok(ApiResponse.<List<Post>>builder()
                .status("success")
                .data(posts)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID")
    public ResponseEntity<ApiResponse<Post>> getPostById(@PathVariable String id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(ApiResponse.<Post>builder()
                .status("success")
                .data(post)
                .build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all posts by user ID")
    public ResponseEntity<ApiResponse<List<Post>>> getPostsByUserId(@PathVariable String userId) {
        List<Post> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<List<Post>>builder()
                .status("success")
                .data(posts)
                .build());
    }

    @PostMapping("/{postId}/upvote")
    @Operation(summary = "Upvote/support a post asynchronously via RabbitMQ (Queue FIRST - High Availability!)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> upvotePost(
            @PathVariable String postId,
            @RequestBody UpvoteRequest request) {
        // ⚡ ASYNC: Queue FIRST → DB LAST (High Availability!)
        postService.addSupportAsync(postId, request.getUserId());
        
        // Return 202 ACCEPTED immediately - don't wait for database!
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("postId", postId);
        responseData.put("userId", request.getUserId());
        responseData.put("message", "Upvote is being processed asynchronously");
        
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.<Map<String, Object>>builder()
                        .status("ACCEPTED")
                        .message("Upvote request accepted and queued")
                        .data(responseData)
                        .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update post asynchronously via RabbitMQ (Queue FIRST - High Availability!)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updatePost(
            @PathVariable String id,
            @RequestBody UpdatePostRequest request) {
        // ⚡ ASYNC: Queue FIRST → DB LAST (High Availability!)
        postService.updatePostAsync(id, request);
        
        // Return 202 ACCEPTED immediately - don't wait for database!
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("postId", id);
        responseData.put("title", request.getTitle());
        responseData.put("status", request.getStatus());
        responseData.put("message", "Update is being processed asynchronously");
        
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.<Map<String, Object>>builder()
                        .status("ACCEPTED")
                        .message("Post update request accepted and queued")
                        .data(responseData)
                        .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status("success")
                .message("Post deleted successfully")
                .build());
    }
}
