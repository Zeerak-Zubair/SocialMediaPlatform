package com.example.socialmediaplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CommentResponseDTO {
    private String content;
    private LocalDateTime createdAt;
}
