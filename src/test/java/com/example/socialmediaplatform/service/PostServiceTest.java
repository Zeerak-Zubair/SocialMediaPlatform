package com.example.socialmediaplatform.service;

import com.example.socialmediaplatform.dto.CommentResponseDTO;
import com.example.socialmediaplatform.dto.PostRequestDTO;
import com.example.socialmediaplatform.dto.PostResponseDTO;
import com.example.socialmediaplatform.model.*;
import com.example.socialmediaplatform.repository.CommentRepository;
import com.example.socialmediaplatform.repository.LikeRepository;
import com.example.socialmediaplatform.repository.PostRepository;
import com.example.socialmediaplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;


    @Test
    void testCreatePost_Success() {
        // Given
        String currentUsername = "testUser";
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn(currentUsername);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setUsername(currentUsername);

        PostRequestDTO requestDTO = new PostRequestDTO();
        requestDTO.setContent("This is a test post");

        Post post = new Post();
        post.setContent(requestDTO.getContent());
        post.setUser(authenticatedUser);

        Post savedPost = mock(Post.class);
        savedPost.setId(1L);
        savedPost.setContent("This is a test post");
        savedPost.setUser(authenticatedUser);

        PostResponseDTO responseDTO = new PostResponseDTO( "This is a test post", LocalDateTime.now(),null, 0);

        // Mock Repository Calls
        when(userRepository.findByUsernameOrEmail(currentUsername, currentUsername)).thenReturn(Optional.of(authenticatedUser));
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(savedPost.toResponse()).thenReturn(responseDTO);

        // When
        PostResponseDTO result = postService.createPost(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("This is a test post", result.getContent());

        verify(userRepository, times(1)).findByUsernameOrEmail(currentUsername, currentUsername);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testCreatePost_UserNotFound() {
        // Given
        String currentUsername = "testUser";
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn(currentUsername);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsernameOrEmail(currentUsername, currentUsername)).thenReturn(Optional.empty());

        PostRequestDTO requestDTO = new PostRequestDTO();
        requestDTO.setContent("This is a test post");

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            postService.createPost(requestDTO);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsernameOrEmail(currentUsername, currentUsername);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testGetPostById_Success() {
        // Given
        Long postId = 1L;

        Post post = mock(Post.class);
        post.setId(postId);
        post.setContent("This is a test post");
        post.setUser(new User(1L, "testUser", "test@example.com", "password", "img.jpg" , "bio" ,null, null, null, null, null, "USER"));

        PostResponseDTO responseDTO = new PostResponseDTO("This is a test post", LocalDateTime.now(), null, 0);

        // Mock Repository Call
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(post.toResponse()).thenReturn(responseDTO);

        // When
        PostResponseDTO result = postService.getPostById(postId);

        // Then
        assertNotNull(result);
        assertEquals("This is a test post", result.getContent());

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testGetPostById_PostNotFound() {
        // Given
        Long postId = 1L;

        // Mock Repository Call
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            postService.getPostById(postId);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testGetAllPosts_Success() {
        // Given
        Post post1 = mock(Post.class);
        Post post2 = mock(Post.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);


        // Create a mock-friendly list
        List<Post> posts = List.of(post1, post2);
        List<PostResponseDTO> responseDTOs = List.of(
                new PostResponseDTO("First post", LocalDateTime.now(), null, 0),
                new PostResponseDTO( "Second post", LocalDateTime.now(), null, 0)
        );

        // Mock Repository Call
        when(postRepository.findAll()).thenReturn(posts);
        when(posts.get(0).toResponse()).thenReturn(responseDTOs.get(0));
        when(posts.get(1).toResponse()).thenReturn(responseDTOs.get(1));

        // When
        List<PostResponseDTO> result = postService.getAllPosts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("First post", result.get(0).getContent());

        assertEquals("Second post", result.get(1).getContent());

        verify(postRepository, times(1)).findAll();
    }

    @Test
    void testGetAllPosts_EmptyList() {
        // Given
        when(postRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<PostResponseDTO> result = postService.getAllPosts();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository, times(1)).findAll();
    }

    @Test
    void testUpdatePost_Success() {
        // Given
        Long postId = 1L;
        String currentUsername = "user1";
        PostRequestDTO requestDTO = new PostRequestDTO("Updated content");

        User postOwner = new User(1L, "user1", "user1@example.com", "password", "", "",null, null, null, null, null, "USER");
        Post post = new Post(postId, postOwner, "Original content",LocalDateTime.now(), null, null);

        // Mock SecurityContextHolder to return the authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(currentUsername);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock repository calls
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PostResponseDTO result = postService.updatePost(postId, requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Updated content", result.getContent());

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testUpdatePost_PostNotFound() {
        // Given
        Long postId = 1L;
        PostRequestDTO requestDTO = new PostRequestDTO("Updated content");

        // Mock security context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext); // Set the mocked security context

        // Mock repository behavior
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> postService.updatePost(1L, requestDTO));
        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    void testUpdatePost_AccessDenied() {
        // Given
        Long postId = 1L;
        String currentUsername = "user2"; // Different user
        PostRequestDTO requestDTO = new PostRequestDTO("Updated content");

        User postOwner = new User(1L, "user1", "user1@example.com", "password", "", "", null, null, null, null, null, "USER");
        Post post = new Post(postId, postOwner, "Original content", LocalDateTime.now(), null, null);

        // Mock SecurityContextHolder to return another user
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(currentUsername);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock repository call
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When & Then
        Exception exception = assertThrows(AccessDeniedException.class, () -> postService.updatePost(postId, requestDTO));
        assertEquals("You are not authorized to edit this post.", exception.getMessage());

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testDeletePost_Success() {
        // Given
        Long postId = 1L;
        String currentUsername = "user1";

        User postOwner = new User(1L, "user1", "user1@example.com", "password", "", "", null, null, null, null, null, "USER");
        Post post = new Post(postId, postOwner, "Original content", LocalDateTime.now(), null, null);

        // Mock SecurityContextHolder to return the authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(currentUsername);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock repository calls
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).deleteById(postId);

        // When
        assertDoesNotThrow(() -> postService.deletePost(postId));

        // Then
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    void testDeletePost_PostNotFound() {
        // Given
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Mock security context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext); // Set the mocked security context

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> postService.deletePost(postId));
        assertEquals("Post not found", exception.getMessage());

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeletePost_AccessDenied() {
        // Given
        Long postId = 1L;
        String currentUsername = "user2"; // Different user

        User postOwner = new User(1L, "user1", "user1@example.com", "password", "", "", null, null, null, null, null, "USER");
        Post post = new Post(postId, postOwner, "Post content", LocalDateTime.now(), null, null);

        // Mock SecurityContextHolder to return another user
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(currentUsername);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock repository call
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When & Then
        Exception exception = assertThrows(AccessDeniedException.class, () -> postService.deletePost(postId));
        assertEquals("You are not authorized to delete this post.", exception.getMessage());

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetPostsByUserId_Success() {
        // Given
        Long userId = 1L;
        User user = new User(1L, "user1", "user1@example.com", "password", "", "", null, null, null, null, null, "USER");

        List<Post> posts = List.of(
                new Post(1L, user, "First post", LocalDateTime.now(), null, null),
                new Post(2L, user,"Second post", LocalDateTime.now(), null, null)
        );

        when(postRepository.findByUserId(userId)).thenReturn(posts);

        // When
        List<PostResponseDTO> response = postService.getPostsByUserId(userId);

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("First post", response.get(0).getContent());
        assertEquals("Second post", response.get(1).getContent());

        verify(postRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetPostsByUserId_NoPostsFound() {
        // Given
        Long userId = 1L;

        when(postRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // When
        List<PostResponseDTO> response = postService.getPostsByUserId(userId);

        // Then
        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(postRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testSearch_Success() {
        // Given
        String keyword = "test";
        int page = 0, size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "content"));

        User user = new User(1L, "user1", "user1@example.com", "password", "", "", null, null, null, null, null, "USER");
        List<Post> posts = List.of(
                new Post(1L, user, "Test post content", LocalDateTime.now(), null, null),
                new Post(2L, user, "Another test content", LocalDateTime.now(), null, null)
        );

        Page<Post> mockPage = new PageImpl<>(posts, pageable, posts.size());
        when(postRepository.findByContentContaining(keyword, pageable)).thenReturn(mockPage);

        // When
        Page<PostResponseDTO> response = postService.search(keyword, page, size);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        assertEquals("Test post content", response.getContent().get(0).getContent());
        assertEquals("Another test content", response.getContent().get(1).getContent());

        verify(postRepository, times(1)).findByContentContaining(keyword, pageable);
    }

    @Test
    void testSearch_NoResults() {
        // Given
        String keyword = "unknown";
        int page = 0, size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "content"));

        Page<Post> emptyPage = Page.empty();
        when(postRepository.findByContentContaining(keyword, pageable)).thenReturn(emptyPage);

        // When
        Page<PostResponseDTO> response = postService.search(keyword, page, size);

        // Then
        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(postRepository, times(1)).findByContentContaining(keyword, pageable);
    }

    @Test
    void testAddCommentToPost_Success() {
        // Given
        Long postId = 1L;
        String currentUsername = "user1";
        User authenticatedUser = new User(1L, "user1", "user1@example.com", "password", "", "", null, null, null, null, null, "USER");
        Post post = new Post(1L, authenticatedUser, "Test Post Content", LocalDateTime.now(), null, null);
        Comment comment = new Comment(null, post, authenticatedUser, "Nice Post!", LocalDateTime.now());


        // Mock SecurityContextHolder to return the authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(currentUsername);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsernameOrEmail(currentUsername, currentUsername)).thenReturn(Optional.of(authenticatedUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        CommentResponseDTO response = postService.addCommentToPost(postId, comment);

        // Then
        assertNotNull(response);
        assertEquals("Nice Post!", response.getContent());

        verify(userRepository, times(1)).findByUsernameOrEmail(currentUsername, currentUsername);
        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testAddCommentToPost_UserNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Mock repository behavior
        User user = new User(1L, "user1", "user1@example.com", "password", "img.jpg", "bio", null, null, null, null, null, "USER");
        when(userRepository.findByUsernameOrEmail("user1", "user1")).thenReturn(Optional.of(user));

        Post post = new Post();
        when(postRepository.save(any())).thenReturn(post);

        // Call the method
        postService.createPost(new PostRequestDTO("Test post"));

        // Verify the repository method was called
        verify(userRepository, times(1)).findByUsernameOrEmail(eq("user1"), eq("user1"));
        verify(postRepository, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testAddCommentToPost_PostNotFound() {
        //Mock Security Context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Given
        Long postId = 1L;
        String currentUsername = "user1";
        User authenticatedUser = new User(1L, "user1", "user1@example.com", "password", "", "", null, null, null, null, null, "USER");
        Comment comment = new Comment(null, null, null, "Nice Post!", LocalDateTime.now());;

        when(authentication.getName()).thenReturn(currentUsername);
        when(userRepository.findByUsernameOrEmail(currentUsername, currentUsername)).thenReturn(Optional.of(authenticatedUser));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> postService.addCommentToPost(postId, comment));

        verify(userRepository, times(1)).findByUsernameOrEmail(currentUsername, currentUsername);
        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testLikePost_Success() {
        //Mock Security Context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Given
        Long postId = 1L;
        String currentUsername = "user1";
        User authenticatedUser = new User(1L, "user1", "user1@example.com", "password", "", "", null, null, null, null, null, "USER");
        Post post = new Post(1L, authenticatedUser, "Test Post Content", LocalDateTime.now(), null, null);
        PostLikeId likeId = new PostLikeId(postId, authenticatedUser.getId());
        Like like = new Like(likeId, post, authenticatedUser);

        when(authentication.getName()).thenReturn(currentUsername);
        when(userRepository.findByUsernameOrEmail(currentUsername, currentUsername)).thenReturn(Optional.of(authenticatedUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post)); // Fetch updated post

        // When
        PostResponseDTO response = postService.likePost(postId);

        // Then
        assertNotNull(response);
        assertEquals("Test Post Content", response.getContent());

        verify(userRepository, times(1)).findByUsernameOrEmail(currentUsername, currentUsername);
        verify(postRepository, times(2)).findById(postId); // Once before like, once after saving
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void testLikePost_UserNotFound() {
        //Mock Security Context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Given
        Long postId = 1L;
        String currentUsername = "user1";

        when(authentication.getName()).thenReturn(currentUsername);
        when(userRepository.findByUsernameOrEmail(currentUsername, currentUsername)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> postService.likePost(postId));

        verify(userRepository, times(1)).findByUsernameOrEmail(currentUsername, currentUsername);
        verify(postRepository, never()).findById(any());
        verify(likeRepository, never()).save(any());
    }

    @Test
    void testLikePost_PostNotFound() {
        //Mock Security Context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Given
        Long postId = 1L;
        String currentUsername = "user1";
        User authenticatedUser = new User(1L, "user1", "user1@example.com", "password", "", "", null, null, null, null, null, "USER");

        when(authentication.getName()).thenReturn(currentUsername);
        when(userRepository.findByUsernameOrEmail(currentUsername, currentUsername)).thenReturn(Optional.of(authenticatedUser));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> postService.likePost(postId));

        verify(userRepository, times(1)).findByUsernameOrEmail(currentUsername, currentUsername);
        verify(postRepository, times(1)).findById(postId);
        verify(likeRepository, never()).save(any());
    }
}