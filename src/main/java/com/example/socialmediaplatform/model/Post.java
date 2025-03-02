package com.example.socialmediaplatform.model;

import com.example.socialmediaplatform.dto.PostResponseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String content;

    @CreatedDate
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @OneToMany(mappedBy = "post")
    private List<Like> likes;

    public PostResponseDTO toResponse(){
        return new PostResponseDTO(
                this.content,
                this.timestamp,
                (comments == null || comments.isEmpty()) ? null : comments.stream().map(Comment::toResponse).toList(),
                (likes == null || likes.isEmpty()) ? 0 : likes.size()
        );
    }

}
