package com.example.socialmediaplatform.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeId {
    private Long postId;
    private Long userId;
}
