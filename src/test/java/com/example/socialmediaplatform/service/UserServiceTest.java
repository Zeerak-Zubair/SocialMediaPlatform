package com.example.socialmediaplatform.service;

import com.example.socialmediaplatform.dto.*;
import com.example.socialmediaplatform.model.Follow;
import com.example.socialmediaplatform.model.User;
import com.example.socialmediaplatform.repository.FollowRepository;
import com.example.socialmediaplatform.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private User user1;

    private User user2;
    @Autowired
    private FollowRepository followRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testLogin() {
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        user2 = userRepository.save(new User(null,"Zayyan","zayyan@gmail.com",passwordEncoder.encode("Zayyan"),"img.jpg","Likes Chess",null,null,null,null,null,"USER"));

        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("Zeerak");
        loginDto.setPassword("Zeerak");

        String token = userService.login(loginDto);
        Assertions.assertNotNull(token);

        //Changing password to assert failure
        loginDto.setPassword("Zayyan");
        // Verify that BadCredentialsException is thrown
        Assertions.assertThrows(BadCredentialsException.class, () -> {
            userService.login(loginDto);
        });

    }



    @Test
    void testCreateUser_Success() {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setUsername("testuser");
        requestDTO.setEmail("testuser@example.com");
        requestDTO.setPassword("password123");

        UserResponseDTO userResponseDTO = userService.createUser(requestDTO);
        Assertions.assertNotNull(userResponseDTO);
        Assertions.assertNotNull(userResponseDTO.getId());
        Assertions.assertEquals("testuser", userResponseDTO.getUsername());
        Assertions.assertEquals("testuser@example.com", userResponseDTO.getEmail());
        assertNull(userResponseDTO.getComments());
        assertNull(userResponseDTO.getFollower());
        assertNull(userResponseDTO.getFollowing());

        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        user2 = userRepository.save(new User(null,"Zayyan","zayyan@gmail.com",passwordEncoder.encode("Zayyan"),"img.jpg","Likes Chess",null,null,null,null,null,"USER"));
        //Fails when user already exists
        requestDTO.setUsername("Zeerak");
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userService.createUser(requestDTO);
        });
    }

    @Test
    void testGetUserById() {

        //Verifying exception is thrown when the userId doesn't exist
        Long userId = 1L;

        // Expecting a RuntimeException with the message "User not found"
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        });

        // Verify the exception message
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetAllUsers_ReturnsUsers() {
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        user2 = userRepository.save(new User(null,"Zayyan","zayyan@gmail.com",passwordEncoder.encode("Zayyan"),"img.jpg","Likes Chess",null,null,null,null,null,"USER"));

        List<UserResponseDTO> userResponseDTOList = userService.getAllUsers();
        Assertions.assertNotNull(userResponseDTOList);
        Assertions.assertEquals(2, userResponseDTOList.size());

        userRepository.deleteAll();
        //Verifying empty list returned with no users
        userResponseDTOList = userService.getAllUsers();
        Assertions.assertTrue(userResponseDTOList.isEmpty());
    }

    @Test
    void search_ShouldReturnUsers_WhenKeywordMatches() {
        User user1 = userRepository.save(new User(null, "john_doe", "john@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER"));
        User user2 = userRepository.save(new User(null, "johnny", "johnny@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER"));

        String keyword = "john";
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username"));

        List<User> userList = List.of(user1, user2);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        Page<User> response = userService.search(keyword,page,size);
        Assertions.assertEquals(userPage.getTotalElements(), response.getTotalElements());
        Assertions.assertEquals(userPage.getTotalPages(), response.getTotalPages());
        Assertions.assertEquals(userPage.getNumber(), response.getNumber());
        Assertions.assertEquals(userPage.getSize(), response.getSize());
        Assertions.assertEquals(userPage.getSort(), response.getSort());
        //Assertions.assertEquals(userPage.getContent(), response.getContent());
    }

    @Test
    void getFollowing_ShouldReturnFollowing_WhenUserExists() {
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        user2 = userRepository.save(new User(null,"Zayyan","zayyan@gmail.com",passwordEncoder.encode("Zayyan"),"img.jpg","Likes Chess",null,null,null,null,null,"USER"));
        User following1 = userRepository.save(new User(null,"Areej","areej@gmail.com",passwordEncoder.encode("Areej"),"img.jpg","Likes Painter",null,null,null,null,null,"USER"));
        User following2 = userRepository.save(new User(null,"Zubair","zubair@gmail.com",passwordEncoder.encode("Zubair"),"img.jpg","Likes Boxing",null,null,null,null,null,"USER"));

        // Create follow relationships (followers list in User)
        Follow follow1 = new Follow(null, user1, following1);
        Follow follow2 = new Follow(null, user1, following2);

        followRepository.save(follow1);
        followRepository.save(follow2);

        List<FollowerResponseDTO> followerResponseDTOS = userService.getFollowers(user1.getId());
        Assertions.assertEquals(followerResponseDTOS.size(), 2);
    }

}
