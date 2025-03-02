package com.example.socialmediaplatform.repository;

import com.example.socialmediaplatform.model.Like;
import com.example.socialmediaplatform.model.Post;
import com.example.socialmediaplatform.model.PostLikeId;
import com.example.socialmediaplatform.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post;
    private Like like;

    @BeforeEach
    void setup() {
        likeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        user = new User(null, "john", "email@example.com", "password", "profilePicture.jpg",
                "successful entrepreneur", List.of(), List.of(), List.of(), List.of(), List.of(), "USER");
        user = userRepository.save(user);

        post = new Post(null, user, "This is my first post!", LocalDateTime.now(), List.of(), List.of());
        post = postRepository.save(post);

        PostLikeId likeId = new PostLikeId(post.getId(), user.getId());
        like = new Like(likeId, post, user);
        like = likeRepository.save(like);
    }

    @DisplayName("Saving a Like")
    @Test
    void saveLike() {
        Assertions.assertNotNull(like.getId());
        Assertions.assertEquals(post.getId(), like.getPost().getId());
        Assertions.assertEquals(user.getId(), like.getUser().getId());
    }

    @DisplayName("Finding a Like")
    @Test
    void findLike() {
        Optional<Like> foundLike = likeRepository.findById(like.getId());

        Assertions.assertTrue(foundLike.isPresent());
        Assertions.assertEquals(like.getId(), foundLike.get().getId());
    }

    @DisplayName("Find All Likes")
    @Test
    void findAllLikes() {
        List<Like> likes = likeRepository.findAll();
        Assertions.assertEquals(1, likes.size());
    }

    @DisplayName("Delete A Like")
    @Test
    void deleteLike() {
        likeRepository.delete(like);
        Optional<Like> deletedLike = likeRepository.findById(like.getId());

        Assertions.assertFalse(deletedLike.isPresent());
    }

    @DisplayName("Delete All Likes")
    @Test
    void deleteAllLikes() {
        likeRepository.deleteAll();
        Assertions.assertEquals(0, likeRepository.count());
    }

}