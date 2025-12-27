package com.issuetracker.opinionservice.service;

import com.issuetracker.opinionservice.dto.CreateOpinionRequest;
import com.issuetracker.opinionservice.exception.BadRequestException;
import com.issuetracker.opinionservice.exception.ResourceNotFoundException;
import com.issuetracker.opinionservice.model.Opinion;
import com.issuetracker.opinionservice.repository.OpinionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpinionService {

    private final OpinionRepository opinionRepository;
    private final WebSocketService webSocketService;
    private final MongoTemplate mongoTemplate;

    /**
     * Create a nested comment using Materialized Path pattern
     * Time Complexity: O(1) for root comments, O(log n) for nested comments
     * where n is the depth of nesting
     */
    public Opinion createOpinion(CreateOpinionRequest request) {
        log.info("Creating opinion for post: {}, parentId: {}", request.getPostId(), request.getParentId());
        
        Opinion opinion;
        
        if (request.getParentId() == null || request.getParentId().isEmpty()) {
            // ROOT COMMENT: No parent, depth 0
            opinion = Opinion.builder()
                    .postId(request.getPostId())
                    .userId(request.getUserId())
                    .comment(request.getComment())
                    .parentId(null)
                    .path(null)  // Will be set after getting ID
                    .depth(0)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            // Save to get ID
            Opinion savedOpinion = opinionRepository.save(opinion);
            
            // Update path with own ID for root comments
            savedOpinion.setPath(savedOpinion.getId());
            savedOpinion = opinionRepository.save(savedOpinion);
            
            // Broadcast via WebSocket
            webSocketService.broadcastNewOpinion(savedOpinion);
            
            log.info("Root opinion created: {} with path: {}", savedOpinion.getId(), savedOpinion.getPath());
            return savedOpinion;
            
        } else {
            // NESTED COMMENT: Has parent, inherit path and depth
            Opinion parent = opinionRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id: " + request.getParentId()));
            
            // Verify parent belongs to same post
            if (!parent.getPostId().equals(request.getPostId())) {
                throw new BadRequestException("Parent comment belongs to different post");
            }
            
            opinion = Opinion.builder()
                    .postId(request.getPostId())
                    .userId(request.getUserId())
                    .comment(request.getComment())
                    .parentId(request.getParentId())
                    .path(null)  // Will be constructed from parent
                    .depth(parent.getDepth() + 1)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            // Save to get ID
            Opinion savedOpinion = opinionRepository.save(opinion);
            
            // Construct materialized path: parent's path + "/" + own ID
            savedOpinion.setPath(parent.getPath() + "/" + savedOpinion.getId());
            savedOpinion = opinionRepository.save(savedOpinion);
            
            // Broadcast via WebSocket
            webSocketService.broadcastNewOpinion(savedOpinion);
            
            log.info("Nested opinion created: {} with path: {}, depth: {}", 
                    savedOpinion.getId(), savedOpinion.getPath(), savedOpinion.getDepth());
            return savedOpinion;
        }
    }

    /**
     * Get all root comments (top-level, no parent)
     * Time Complexity: O(n) where n is number of root comments
     */
    public List<Opinion> getRootCommentsByPostId(String postId) {
        log.info("Getting root comments for post: {}", postId);
        return opinionRepository.findByPostIdAndParentIdIsNull(postId);
    }
    
    /**
     * Get direct children of a comment
     * Time Complexity: O(n) where n is number of direct children
     */
    public List<Opinion> getChildComments(String parentId) {
        log.info("Getting child comments for parent: {}", parentId);
        return opinionRepository.findByParentId(parentId);
    }
    
    /**
     * Get all comments in hierarchical order (sorted by path)
     * This returns comments in tree-traversal order
     * Time Complexity: O(n log n) due to sorting
     */
    public List<Opinion> getOpinionsByPostIdHierarchical(String postId) {
        log.info("Getting hierarchical comments for post: {}", postId);
        return opinionRepository.findByPostIdOrderByPathAsc(postId);
    }
    
    /**
     * Get all descendants of a comment (including nested children)
     * Uses regex to match path pattern
     * Time Complexity: O(n) where n is total descendants
     */
    public List<Opinion> getAllDescendants(String commentId) {
        log.info("Getting all descendants for comment: {}", commentId);
        
        Opinion comment = opinionRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        // Find all comments whose path starts with this comment's path
        // e.g., if path is "1/2", find all paths starting with "1/2/" (children, grandchildren, etc.)
        Pattern pattern = Pattern.compile("^" + Pattern.quote(comment.getPath()) + "/.*");
        Query query = new Query(Criteria.where("path").regex(pattern));
        
        return mongoTemplate.find(query, Opinion.class);
    }

    /**
     * Delete a comment and all its descendants (cascade delete)
     * Time Complexity: O(log n) where n is depth of nesting
     * Uses materialized path pattern for efficient deletion
     */
    public void deleteOpinion(String id) {
        log.info("Deleting opinion and all descendants: {}", id);
        
        Opinion opinion = opinionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opinion not found with id: " + id));
        
        // Delete all descendants first (comments whose path starts with this comment's path)
        Pattern pattern = Pattern.compile("^" + Pattern.quote(opinion.getPath()) + "/.*");
        Query query = new Query(Criteria.where("path").regex(pattern));
        
        List<Opinion> descendants = mongoTemplate.find(query, Opinion.class);
        log.info("Found {} descendants to delete", descendants.size());
        
        // Delete all descendants
        mongoTemplate.remove(query, Opinion.class);
        
        // Delete the comment itself
        opinionRepository.deleteById(id);
        
        log.info("Deleted opinion {} and {} descendants", id, descendants.size());
    }

    public List<Opinion> getOpinionsByPostId(String postId) {
        return opinionRepository.findByPostId(postId);
    }

    public List<Opinion> getAllOpinions() {
        return opinionRepository.findAll();
    }
}