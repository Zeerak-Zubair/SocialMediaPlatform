package com.example.socialmediaplatform.repository;

import com.example.socialmediaplatform.model.Like;
import com.example.socialmediaplatform.model.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, PostLikeId> {
}
