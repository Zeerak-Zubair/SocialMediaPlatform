package com.example.socialmediaplatform.model;

import com.example.socialmediaplatform.dto.UserResponseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String profilePicture;

    private String bio;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follow> following; //users who one follows

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Follow> followers; //users who one is followed by

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> likes;

    private String role;

    public UserResponseDTO toResponse(){
        return new UserResponseDTO(
                this.id,
                this.username,
                this.email,
                (posts == null || posts.isEmpty()) ? null : posts.stream().map(Post::toResponse).toList(),
                (comments == null || comments.isEmpty()) ? null : comments.stream().map(Comment::toResponse).toList(),
                (following == null || following.isEmpty()) ? null : following.stream().map(Follow::FollowingToResponse).toList(),
                (followers == null || followers.isEmpty()) ? null : followers.stream().map(Follow::FollowerToResponse).toList(),
                (likes == null || likes.isEmpty()) ? 0 : likes.size()
        );
    }
}