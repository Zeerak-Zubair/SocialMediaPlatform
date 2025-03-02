package com.example.socialmediaplatform.repository;

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

@SpringBootTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setup() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        user = new User(null, "john", "email@example.com", "password", "profilePicture.jpg",
                "successful entrepreneur", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "USER");
        user = userRepository.save(user);

        post = new Post(null, user, "This is my first post!", LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        post = postRepository.save(post);
    }

    @DisplayName("Saving a Post")
    @Test
    void savePost() {
        Assertions.assertNotNull(post.getId());
        Assertions.assertEquals("This is my first post!", post.getContent());
        Assertions.assertEquals(user.getId(), post.getUser().getId());
    }

    @DisplayName("Updating a Post")
    @Test
    void updatePost() {
        // Given
        post.setContent("Updated content");

        // When
        Post updatedPost = postRepository.save(post);

        // Then
        Assertions.assertEquals("Updated content", updatedPost.getContent());
    }

    @DisplayName("Finding a Post")
    @Test
    void findPost() {
        // When
        Optional<Post> foundPost = postRepository.findById(post.getId());

        // Then
        Assertions.assertTrue(foundPost.isPresent());
        Assertions.assertEquals(post.getContent(), foundPost.get().getContent());
    }

    @DisplayName("Find All Posts")
    @Test
    void findAllPosts() {
        // Given
        Post anotherPost = new Post(null, user, "Another post", LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        postRepository.save(anotherPost);

        // When
        List<Post> posts = postRepository.findAll();

        // Then
        Assertions.assertEquals(2, posts.size());
    }

    @DisplayName("Delete A Post")
    @Test
    void deletePost() {
        // When
        postRepository.delete(post);
        Optional<Post> deletedPost = postRepository.findById(post.getId());

        // Then
        Assertions.assertFalse(deletedPost.isPresent());
    }

    @DisplayName("Delete All Posts")
    @Test
    void deleteAllPosts() {
        // When
        postRepository.deleteAll();

        // Then
        Assertions.assertEquals(0, postRepository.count());
    }


}