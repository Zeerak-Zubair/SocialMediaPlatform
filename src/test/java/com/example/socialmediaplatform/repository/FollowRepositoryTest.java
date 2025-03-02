package com.example.socialmediaplatform.repository;

import com.example.socialmediaplatform.model.Follow;
import com.example.socialmediaplatform.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class FollowRepositoryTest {
    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    private User follower;
    private User following;
    private Follow follow;

    @BeforeEach
    void setup() {
        followRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save two users
        follower = new User(null, "followerUser", "follower@example.com", "password", "profile1.jpg",
                "Tech Enthusiast", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),"USER");
        follower = userRepository.save(follower);

        following = new User(null, "followingUser", "following@example.com", "password", "profile2.jpg",
                "Business Guru", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),"USER");
        following = userRepository.save(following);

        // Create and save a Follow entity
        follow = new Follow(null, follower, following);
        follow = followRepository.save(follow);
    }

    @DisplayName("Saving a Follow")
    @Test
    void saveFollow() {
        Assertions.assertNotNull(follow.getId());
        Assertions.assertEquals(follower.getId(), follow.getFollower().getId());
        Assertions.assertEquals(following.getId(), follow.getFollowing().getId());
    }

    @DisplayName("Finding a Follow")
    @Test
    void findFollow() {
        Optional<Follow> foundFollow = followRepository.findById(follow.getId());

        Assertions.assertTrue(foundFollow.isPresent());
        Assertions.assertEquals(follow.getId(), foundFollow.get().getId());
    }

    @DisplayName("Find All Follows")
    @Test
    void findAllFollows() {
        List<Follow> follows = followRepository.findAll();
        Assertions.assertEquals(1, follows.size());
    }

    @DisplayName("Delete A Follow")
    @Test
    void deleteFollow() {
        followRepository.delete(follow);
        Optional<Follow> deletedFollow = followRepository.findById(follow.getId());

        Assertions.assertFalse(deletedFollow.isPresent());
    }

    @DisplayName("Delete All Follows")
    @Test
    void deleteAllFollows() {
        followRepository.deleteAll();
        Assertions.assertEquals(0, followRepository.count());
    }
}