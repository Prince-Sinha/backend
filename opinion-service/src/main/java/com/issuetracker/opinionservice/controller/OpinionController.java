package com.issuetracker.opinionservice.controller;

import com.issuetracker.opinionservice.dto.ApiResponse;
import com.issuetracker.opinionservice.dto.CreateOpinionRequest;
import com.issuetracker.opinionservice.model.Opinion;
import com.issuetracker.opinionservice.service.OpinionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/opinions")
@RequiredArgsConstructor
@Tag(name = "Opinion Management", description = "Opinion/Comment management with WebSocket")
public class OpinionController {

    private final OpinionService opinionService;

    @PostMapping
    @Operation(summary = "Create opinion (broadcasts via WebSocket)")
    public ResponseEntity<ApiResponse<Opinion>> createOpinion(@Valid @RequestBody CreateOpinionRequest request) {
        Opinion opinion = opinionService.createOpinion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Opinion>builder()
                        .status("success")
                        .message("Opinion created and broadcasted")
                        .data(opinion)
                        .build());
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Get opinions by post ID")
    public ResponseEntity<ApiResponse<List<Opinion>>> getOpinionsByPostId(@PathVariable String postId) {
        List<Opinion> opinions = opinionService.getOpinionsByPostId(postId);
        return ResponseEntity.ok(ApiResponse.<List<Opinion>>builder()
                .status("success")
                .data(opinions)
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all opinions")
    public ResponseEntity<ApiResponse<List<Opinion>>> getAllOpinions() {
        List<Opinion> opinions = opinionService.getAllOpinions();
        return ResponseEntity.ok(ApiResponse.<List<Opinion>>builder()
                .status("success")
                .data(opinions)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete opinion and all its nested replies (cascade)")
    public ResponseEntity<ApiResponse<String>> deleteOpinion(@PathVariable String id) {
        opinionService.deleteOpinion(id);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status("success")
                .message("Opinion and all nested replies deleted successfully")
                .build());
    }
    
    // ========== NESTED COMMENTS ENDPOINTS ==========
    
    @GetMapping("/post/{postId}/root")
    @Operation(summary = "Get root comments (top-level, no parent) for a post")
    public ResponseEntity<ApiResponse<List<Opinion>>> getRootComments(@PathVariable String postId) {
        List<Opinion> opinions = opinionService.getRootCommentsByPostId(postId);
        return ResponseEntity.ok(ApiResponse.<List<Opinion>>builder()
                .status("success")
                .message("Root comments retrieved successfully")
                .data(opinions)
                .build());
    }
    
    @GetMapping("/{parentId}/children")
    @Operation(summary = "Get direct children/replies of a comment")
    public ResponseEntity<ApiResponse<List<Opinion>>> getChildComments(@PathVariable String parentId) {
        List<Opinion> opinions = opinionService.getChildComments(parentId);
        return ResponseEntity.ok(ApiResponse.<List<Opinion>>builder()
                .status("success")
                .message("Child comments retrieved successfully")
                .data(opinions)
                .build());
    }
    
    @GetMapping("/post/{postId}/hierarchical")
    @Operation(summary = "Get all comments in hierarchical order (tree traversal) using materialized path")
    public ResponseEntity<ApiResponse<List<Opinion>>> getHierarchicalComments(@PathVariable String postId) {
        List<Opinion> opinions = opinionService.getOpinionsByPostIdHierarchical(postId);
        return ResponseEntity.ok(ApiResponse.<List<Opinion>>builder()
                .status("success")
                .message("Comments retrieved in hierarchical order")
                .data(opinions)
                .build());
    }
    
    @GetMapping("/{commentId}/descendants")
    @Operation(summary = "Get all descendants (nested replies at all levels) of a comment")
    public ResponseEntity<ApiResponse<List<Opinion>>> getAllDescendants(@PathVariable String commentId) {
        List<Opinion> opinions = opinionService.getAllDescendants(commentId);
        return ResponseEntity.ok(ApiResponse.<List<Opinion>>builder()
                .status("success")
                .message("All descendants retrieved successfully")
                .data(opinions)
                .build());
    }
}