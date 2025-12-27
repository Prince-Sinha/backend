


package com.issuetracker.postservice.repository;

import com.issuetracker.postservice.model.Department;
import com.issuetracker.postservice.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByDept(Department dept);
    List<Post> findByUserId(String userId);
}


