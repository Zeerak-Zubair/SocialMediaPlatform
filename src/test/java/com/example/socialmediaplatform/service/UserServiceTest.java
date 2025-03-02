package com.example.socialmediaplatform.service;

import com.example.socialmediaplatform.dto.*;
import com.example.socialmediaplatform.model.Follow;
import com.example.socialmediaplatform.model.User;
import com.example.socialmediaplatform.repository.UserRepository;
import com.example.socialmediaplatform.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        //userRepository.deleteAll();
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testLogin_Success() {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("Zeerak");
        loginDto.setPassword("Zeerak");

        String expectedToken = "mock-jwt-token";

        // Mock behavior
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(expectedToken);

        // When
        String actualToken = userService.login(loginDto);

        // Then
        assertNotNull(actualToken);
        assertEquals(expectedToken, actualToken);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(authentication);
    }



    @Test
    void testCreateUser_Success() {
        // Given
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setUsername("testuser");
        requestDTO.setEmail("testuser@example.com");
        requestDTO.setPassword("password123");

        User mockUser = mock(User.class); // Create a mock object instead of a real object

        when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("encoded-password");

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponseDTO responseDTO = new UserResponseDTO(1L, "testuser", "testuser@example.com", null, null, null, null, 0);
        when(mockUser.toResponse()).thenReturn(responseDTO); // Now, we can stub toResponse()

        // When
        UserResponseDTO actualResponse = userService.createUser(requestDTO);

        // Then
        assertNotNull(actualResponse);
        assertEquals("testuser", actualResponse.getUsername());
        assertEquals("testuser@example.com", actualResponse.getEmail());

        verify(passwordEncoder, times(1)).encode(requestDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetUserById_UserExists() {
        // Given
        Long userId = 1L;
        User mockUser = mock(User.class); // Create a mock object instead of a real object
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setEmail("testuser@example.com");

        UserResponseDTO expectedResponse = new UserResponseDTO(userId, "testuser", "testuser@example.com",null, null, null, null, 0);


        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.toResponse()).thenReturn(expectedResponse);

        // When
        UserResponseDTO actualResponse = userService.getUserById(userId);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getUsername(), actualResponse.getUsername());
        assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_UserNotFound() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserById(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetAllUsers_ReturnsUsers() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        List<User> mockUsers = List.of(user1, user2);

        UserResponseDTO response1 = new UserResponseDTO(1L, "user1", "user1@example.com",null, null, null, null, 0);
        UserResponseDTO response2 = new UserResponseDTO(2L, "user2", "user2@example.com",null, null, null, null, 0);

        when(userRepository.findAll()).thenReturn(mockUsers);

        // When
        List<UserResponseDTO> actualUsers = userService.getAllUsers();

        // Then
        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertEquals("user1", actualUsers.get(0).getUsername());
        assertEquals("user2", actualUsers.get(1).getUsername());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<UserResponseDTO> actualUsers = userService.getAllUsers();

        // Then
        assertNotNull(actualUsers);
        assertTrue(actualUsers.isEmpty());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldEncodedPassword");

        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setUsername("newUsername");
        requestDTO.setEmail("new@example.com");
        requestDTO.setPassword("newPassword");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername(requestDTO.getUsername());
        updatedUser.setEmail(requestDTO.getEmail());
        updatedUser.setPassword("newEncodedPassword"); // Mocking encoded password

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserResponseDTO responseDTO = userService.updateUser(userId, requestDTO);

        // Then
        assertNotNull(responseDTO);
        assertEquals("newUsername", responseDTO.getUsername());
        assertEquals("new@example.com", responseDTO.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode(requestDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        Long userId = 1L;
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setUsername("newUsername");
        requestDTO.setEmail("new@example.com");
        requestDTO.setPassword("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, requestDTO);
        });

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Given
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void search_ShouldReturnUsers_WhenKeywordMatches() {
        // Given
        String keyword = "john";
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username"));

        User user1 = new User(1L, "john_doe", "john@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER");
        User user2 = new User(2L, "johnny", "johnny@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER");

        List<User> userList = List.of(user1, user2);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        // Mock repository call
        when(userRepository.findByUsernameContainingAndEmailContainingAndBioContaining(keyword, keyword, keyword,pageable)).thenReturn(userPage);

        // When
        Page<User> result = userService.search(keyword, page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("john_doe", result.getContent().get(0).getUsername());

        // Verify repository
        verify(userRepository, times(1)).findByUsernameContainingAndEmailContainingAndBioContaining(keyword, keyword, keyword, pageable);
    }

    @Test
    void getFollowers_ShouldReturnFollowers_WhenUserExists() {
        // Given
        Long userId = 1L;
        User user = new User(userId, "john_doe", "john@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER");

        User follower1 = new User(2L, "follower1", "follower1@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER");
        User follower2 = new User(3L, "follower2", "follower2@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER");

        Follow follow1 = new Follow(null,follower1, user);
        Follow follow2 = new Follow(null, follower2, user);
        List<Follow> followers = List.of(follow1, follow2);

        user.setFollowers(followers); // Assuming User has a `setFollowers` method

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        List<FollowerResponseDTO> result = userService.getFollowers(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("follower1", result.get(0).getUsername());
        assertEquals("follower2", result.get(1).getUsername());

        // Verify that repository was called
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getFollowers_ShouldThrowException_WhenUserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getFollowers(userId));
        assertEquals("User not found", exception.getMessage());

        // Verify that repository was called
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getFollowing_ShouldReturnFollowing_WhenUserExists() {
        // Given
        Long userId = 1L;
        User user = new User(userId, "john_doe", "john@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER");

        User following1 = new User(2L, "following1", "following1@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER");
        User following2 = new User(3L, "following2", "following2@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER");

        // Create follow relationships (followers list in User)
        Follow follow1 = new Follow(null, user, following1);
        Follow follow2 = new Follow(null, user, following2);
        List<Follow> followingList = List.of(follow1, follow2);

        // Mock the repository call to return the user with followers
        user.setFollowers(followingList); // Assuming User has a `getFollowers()` method that returns `List<Follow>`
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        List<FollowingResponseDTO> result = userService.getFollowing(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("following1", result.get(0).getUsername());
        assertEquals("following2", result.get(1).getUsername());

        // Verify interactions
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getFollowing_ShouldThrowException_WhenUserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getFollowing(userId));
        assertEquals("User not found", exception.getMessage());

        // Verify that repository was called
        verify(userRepository, times(1)).findById(userId);
    }
}
