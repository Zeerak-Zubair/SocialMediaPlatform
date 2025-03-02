package com.example.socialmediaplatform.dto;

import com.example.socialmediaplatform.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class PostResponseDTO {
    private String content;
    private LocalDateTime createdAt;
    private List<CommentResponseDTO> comments;
    private int likes;
}
