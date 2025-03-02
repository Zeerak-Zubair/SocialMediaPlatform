package com.example.socialmediaplatform.repository;

import com.example.socialmediaplatform.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
