package com.example.socialmediaplatform.repository;

import com.example.socialmediaplatform.model.Comment;
import com.example.socialmediaplatform.model.Post;
import com.example.socialmediaplatform.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        postRepository.deleteAll();
        commentRepository.deleteAll();

        user = new User(null, "john", "email@example.com", "password", "profilePicture.jpg",
                "successful entrepreneur", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),"USER");
        user = userRepository.save(user);

        post = new Post(null, user, "This is my first post!", LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        post = postRepository.save(post);

        comment = new Comment(null, post, user, "Nice post!", LocalDateTime.now());
        comment = commentRepository.save(comment);
    }

    @DisplayName("Saving a Comment")
    @Test
    void saveComment() {
        Assertions.assertNotNull(comment.getId());
        Assertions.assertEquals("Nice post!", comment.getContent());
        Assertions.assertEquals(user.getId(), comment.getUser().getId());
        Assertions.assertEquals(post.getId(), comment.getPost().getId());
    }

    @DisplayName("Updating a Comment")
    @Test
    void updateComment() {
        // Given
        comment.setContent("Updated comment content");

        // When
        Comment updatedComment = commentRepository.save(comment);

        // Then
        Assertions.assertEquals("Updated comment content", updatedComment.getContent());
    }

    @DisplayName("Finding a Comment")
    @Test
    void findComment() {
        // When
        Optional<Comment> foundComment = commentRepository.findById(comment.getId());

        // Then
        Assertions.assertTrue(foundComment.isPresent());
        Assertions.assertEquals(comment.getContent(), foundComment.get().getContent());
    }

    @DisplayName("Find All Comments")
    @Test
    void findAllComments() {
        // Given
        Comment anotherComment = new Comment(null, post, user, "Another comment!", LocalDateTime.now());
        commentRepository.save(anotherComment);

        // When
        List<Comment> comments = commentRepository.findAll();

        // Then
        Assertions.assertEquals(2, comments.size());
    }

    @DisplayName("Delete A Comment")
    @Test
    void deleteComment() {
        // When
        commentRepository.delete(comment);
        Optional<Comment> deletedComment = commentRepository.findById(comment.getId());

        // Then
        Assertions.assertFalse(deletedComment.isPresent());
    }

    @DisplayName("Delete All Comments")
    @Test
    void deleteAllComments() {
        // When
        commentRepository.deleteAll();

        // Then
        Assertions.assertEquals(0, commentRepository.count());
    }
}