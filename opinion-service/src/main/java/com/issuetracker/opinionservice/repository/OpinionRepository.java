package com.issuetracker.opinionservice.repository;

import com.issuetracker.opinionservice.model.Opinion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpinionRepository extends MongoRepository<Opinion, String> {
    // Original query
    List<Opinion> findByPostId(String postId);
    
    // Nested comments queries using Materialized Path pattern
    // Get all root comments (top-level comments with no parent)
    List<Opinion> findByPostIdAndParentIdIsNull(String postId);
    
    // Get direct children of a comment
    List<Opinion> findByParentId(String parentId);
    
    // Get all descendants of a comment (using path pattern matching)
    // In service layer, we'll use regex: path starts with parent's path
    List<Opinion> findByPostIdOrderByPathAsc(String postId);
    
    // Get comments by depth (useful for limiting nesting level)
    List<Opinion> findByPostIdAndDepth(String postId, Integer depth);
}