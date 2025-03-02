package com.example.socialmediaplatform.repository;

import com.example.socialmediaplatform.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByContentContaining(String content, Pageable pageable);
    List<Post> findByUserId(Long userId);
}
