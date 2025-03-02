package com.example.socialmediaplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FollowingResponseDTO {
    private String username;
    private String email;
}
