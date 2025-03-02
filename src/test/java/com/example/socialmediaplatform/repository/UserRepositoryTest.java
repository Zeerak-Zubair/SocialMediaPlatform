package com.example.socialmediaplatform.repository;

import com.example.socialmediaplatform.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        user = new User(null, "john", "email@example.com", "password", "profilePicture.jpg",
                "successful entrepreneur", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "USER");
        user = userRepository.save(user);
    }

    @DisplayName("Saving a User")
    @Test
    void saveUser(){
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals("john", user.getUsername());
        Assertions.assertEquals("email@example.com", user.getEmail());
        Assertions.assertEquals("password", user.getPassword());
        Assertions.assertEquals("profilePicture.jpg", user.getProfilePicture());
        Assertions.assertEquals("successful entrepreneur", user.getBio());
    }

    @DisplayName("Updating a User")
    @Test
    void updateUser(){
        // Given
        user.setUsername("john_doe");
        user.setEmail("updated@example.com");

        // When
        User updatedUser = userRepository.save(user);

        // Then
        Assertions.assertEquals("john_doe", updatedUser.getUsername());
        Assertions.assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @DisplayName("Finding a User")
    @Test
    void findUser(){
        // When
        Optional<User> foundUser = userRepository.findById(user.getId());

        // Then
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals(user.getUsername(), foundUser.get().getUsername());
    }


    @DisplayName("Find All Users")
    @Test
    void findAllUsers(){
        // Given
        User anotherUser = new User(null, "jane", "jane@example.com", "password123", null, "bio",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "USER");
        userRepository.save(anotherUser);

        // When
        List<User> users = userRepository.findAll();

        // Then
        Assertions.assertEquals(2, users.size());
    }

    @DisplayName("Delete A User")
    @Test
    void deleteUser(){
        userRepository.delete(user);
        Optional<User> deletedUser = userRepository.findById(user.getId());

        Assertions.assertFalse(deletedUser.isPresent());
    }

    @DisplayName("Delete All Users")
    @Test
    void deleteAllUsers(){
        userRepository.deleteAll();
        Assertions.assertEquals(0, userRepository.count());
    }

}