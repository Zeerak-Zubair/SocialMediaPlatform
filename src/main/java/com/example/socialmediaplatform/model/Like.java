package com.example.socialmediaplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_likes")
public class Like {

    @EmbeddedId
    private PostLikeId id;

    @ManyToOne
    @MapsId("postId") // Maps postId from PostLikeId
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @MapsId("userId") // Maps userId from PostLikeId
    @JoinColumn(name = "user_id")
    private User user;


}
