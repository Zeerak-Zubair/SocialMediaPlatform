package com.example.socialmediaplatform.repository;

import com.example.socialmediaplatform.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
