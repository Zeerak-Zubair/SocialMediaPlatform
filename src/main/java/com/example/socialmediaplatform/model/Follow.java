package com.example.socialmediaplatform.model;

import com.example.socialmediaplatform.dto.FollowerResponseDTO;
import com.example.socialmediaplatform.dto.FollowingResponseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity(name = "follows")
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private User following;

    public FollowerResponseDTO FollowerToResponse(){
        return new FollowerResponseDTO(
            this.follower.getUsername(),
                this.follower.getEmail()
        );
    }

    public FollowingResponseDTO FollowingToResponse(){
        return new FollowingResponseDTO(
            this.following.getUsername(),
                this.following.getEmail()
        );
    }
}


