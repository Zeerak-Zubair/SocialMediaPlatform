package com.example.socialmediaplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private List<PostResponseDTO> posts;
    private List<CommentResponseDTO> comments;
    private List<FollowingResponseDTO> following;
    private List<FollowerResponseDTO> follower;
    private int likes;
}
