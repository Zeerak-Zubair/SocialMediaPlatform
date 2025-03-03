package com.example.socialmediaplatform.service;

import com.example.socialmediaplatform.dto.CommentResponseDTO;
import com.example.socialmediaplatform.dto.PostRequestDTO;
import com.example.socialmediaplatform.dto.PostResponseDTO;
import com.example.socialmediaplatform.model.*;
import com.example.socialmediaplatform.repository.CommentRepository;
import com.example.socialmediaplatform.repository.LikeRepository;
import com.example.socialmediaplatform.repository.PostRepository;
import com.example.socialmediaplatform.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;


    private SecurityContext securityContext;


    private Authentication authentication;

    private User user1;

    @BeforeEach
    void setUp(){
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreatePost(){
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());

        String role = "ROLE_" + user1.getRole(); // If roles are stored without prefix

        // Create an authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));

        // Set authentication in security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zeerak", currentUsername);

        PostRequestDTO post1 = new PostRequestDTO("My first Post!");
        PostResponseDTO postResponseDTO = postService.createPost(post1);
        Assertions.assertNotNull(postResponseDTO);
        Assertions.assertEquals("My first Post!", postResponseDTO.getContent());
        Assertions.assertNotNull(postResponseDTO.getCreatedAt());
        Assertions.assertEquals(0,postResponseDTO.getLikes());
        Assertions.assertNull(postResponseDTO.getComments());

        //Asserting the failure when the user is incorrect.
        SecurityContextHolder.clearContext();
        // Create an authentication token
        authentication = new UsernamePasswordAuthenticationToken("Alex", null, List.of(new SimpleGrantedAuthority(role)));

        // Set authentication in security context
        context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Alex", currentUsername);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            post1.setContent("Alex's First Post");
            postService.createPost(post1);
        });

        Assertions.assertEquals("User not found",exception.getMessage());
    }


    @Test
    void testGetPostById() {
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());

        String role = "ROLE_" + user1.getRole(); // If roles are stored without prefix

        // Create an authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));

        // Set authentication in security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zeerak", currentUsername);

        PostRequestDTO post1 = new PostRequestDTO("My first Post!");
        PostResponseDTO postResponseDTO = postService.createPost(post1);
        Assertions.assertNotNull(postResponseDTO);

        postResponseDTO = postService.getPostById(postResponseDTO.getId());
        Assertions.assertNotNull(postResponseDTO);

        //Error received when we send an invalid id
        Long invalidId = postResponseDTO.getId() +1;
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            postService.getPostById(invalidId);
        });

        Assertions.assertEquals("Post not found",exception.getMessage());
    }


    @Test
    void testGetAllPosts() {
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());

        String role = "ROLE_" + user1.getRole(); // If roles are stored without prefix

        // Create an authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));

        // Set authentication in security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zeerak", currentUsername);

        PostRequestDTO post1 = new PostRequestDTO("My first Post!");
        PostResponseDTO postResponseDTO1 = postService.createPost(post1);
        Assertions.assertNotNull(postResponseDTO1);

        PostRequestDTO post2 = new PostRequestDTO("My second Post!");
        PostResponseDTO postResponseDTO2 = postService.createPost(post2);
        Assertions.assertNotNull(postResponseDTO1);

        //Asserting that the posts are not null
        List<PostResponseDTO> posts = postService.getAllPosts();
        Assertions.assertNotNull(posts);
        Assertions.assertEquals(2,posts.size());

        //Asserting that the posts are empty when no posts exist
        postRepository.deleteAll();
        posts = postService.getAllPosts();
        Assertions.assertTrue(posts.isEmpty());
    }

    @Test
    void testUpdatePost() {
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());

        String role = "ROLE_" + user1.getRole(); // If roles are stored without prefix

        // Create an authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));

        // Set authentication in security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zeerak", currentUsername);

        Post post = postRepository.save(new Post(null,user1,"My first post!",null,null,null));
        Assertions.assertNotNull(post);
        Assertions.assertNotNull(post.getId());

        PostRequestDTO updatedPost = new PostRequestDTO("My updated post!");
        PostResponseDTO response = postService.updatePost(post.getId(), updatedPost);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getId(), post.getId());
        Assertions.assertNotEquals(response.getContent(),post.getContent());
        Assertions.assertEquals("My updated post!", response.getContent());

        //clear the context and log in with a different user.
        SecurityContextHolder.clearContext();
        User user2 = userRepository.save(new User(null,"Zayyan","zayyan@gmail.com",passwordEncoder.encode("Zayyan"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user2.getId());

        authentication = new UsernamePasswordAuthenticationToken(user2.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zayyan", currentUsername);

        updatedPost.setContent("My second updated post!");

        // It will fail because Zayyan is not the owner of the post.
        RuntimeException exception = Assertions.assertThrows(AccessDeniedException.class , () -> {
           postService.updatePost(post.getId(),updatedPost);
        });
        Assertions.assertEquals("You are not authorized to edit this post.",exception.getMessage());

    }

    @Test
    void testDeletePost() {
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());

        String role = "ROLE_" + user1.getRole(); // If roles are stored without prefix

        // Create an authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));

        // Set authentication in security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zeerak", currentUsername);

        Post post = postRepository.save(new Post(null,user1,"My first post!",null,null,null));
        Assertions.assertNotNull(post);
        Assertions.assertNotNull(post.getId());

        postService.deletePost(post.getId());
        Assertions.assertFalse(postRepository.existsById(post.getId()));

        //Creating a new post with user1
        post = postRepository.save(new Post(null,user1,"My second post!",null,null,null));
        Assertions.assertNotNull(post);
        Assertions.assertNotNull(post.getId());

        //clear the context and log in with a different user.
        SecurityContextHolder.clearContext();
        User user2 = userRepository.save(new User(null,"Zayyan","zayyan@gmail.com",passwordEncoder.encode("Zayyan"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user2.getId());

        authentication = new UsernamePasswordAuthenticationToken(user2.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zayyan", currentUsername);

        Long postId = post.getId();
        // It will fail because Zayyan is not the owner of the post.
        RuntimeException exception = Assertions.assertThrows(AccessDeniedException.class , () -> {
            postService.deletePost(postId);
        });
        Assertions.assertEquals("You are not authorized to edit this post.",exception.getMessage());

    }

    @Test
    void testGetPostsByUserId() {
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());

        String role = "ROLE_" + user1.getRole(); // If roles are stored without prefix

        // Create an authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));

        // Set authentication in security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zeerak", currentUsername);

        Post post = postRepository.save(new Post(null,user1,"My first post!",null,null,null));
        Assertions.assertNotNull(post);
        Assertions.assertNotNull(post.getId());

        post = postRepository.save(new Post(null,user1,"My second post!",null,null,null));
        Assertions.assertNotNull(post);
        Assertions.assertNotNull(post.getId());

        List<PostResponseDTO> posts = postService.getPostsByUserId(user1.getId());
        Assertions.assertNotNull(posts);
        Assertions.assertEquals(2, posts.size());

        //Empty List should be returned when posts does not exist
        postRepository.deleteAll();

        posts = postService.getPostsByUserId(user1.getId());
        Assertions.assertNotNull(posts);
        Assertions.assertTrue(posts.isEmpty());

        Long invalidId = user1.getId() + 1;
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, ()->{
            postService.getPostsByUserId(invalidId);
        });
        Assertions.assertEquals(exception.getMessage(), "User not found");
    }

    @Test
    void testSearch() {
        //Creating User and Posts
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());

        String role = "ROLE_" + user1.getRole(); // If roles are stored without prefix

        // Create an authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));

        // Set authentication in security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zeerak", currentUsername);

        Post post = postRepository.save(new Post(null,user1,"My first post!",null,null,null));
        Assertions.assertNotNull(post);
        Assertions.assertNotNull(post.getId());

        post = postRepository.save(new Post(null,user1,"My second post!",null,null,null));
        Assertions.assertNotNull(post);
        Assertions.assertNotNull(post.getId());

        //
        int page = 0;
        int size = 10;
        Pageable paging = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "content"));
        Page<Post> posts = postService.search("post",paging);

        Assertions.assertNotNull(posts); // Ensure the page object is not null
        Assertions.assertFalse(posts.isEmpty()); // Ensure that the result contains posts
        Assertions.assertEquals(2, posts.getTotalElements()); // Ensure the total elements match expected value
        Assertions.assertEquals(1, posts.getTotalPages()); // Ensure total pages match expected pages

        List<Post> postList = posts.getContent();
        Assertions.assertEquals(2, postList.size()); // Ensure the page contains the expected number of posts
        Assertions.assertEquals("My first post!", postList.get(0).getContent()); // Verify content of the first post
        Assertions.assertEquals("My second post!", postList.get(1).getContent()); // Verify content of the second post

    }


    @Test
    void testAddCommentToPost() {
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());

        String role = "ROLE_" + user1.getRole(); // If roles are stored without prefix

        // Create an authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));

        // Set authentication in security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zeerak", currentUsername);

        Post post = postRepository.save(new Post(null,user1,"My first post!",null,null,null));
        Assertions.assertNotNull(post);
        Assertions.assertNotNull(post.getId());

        Comment comment = new Comment(null, post, user1, "Nice comment!",null);
        CommentResponseDTO commentResponseDTO = postService.addCommentToPost(post.getId(), comment);
        Assertions.assertNotNull(commentResponseDTO);
        Assertions.assertEquals("Nice comment!",comment.getContent());
        Assertions.assertNotNull(comment.getTimestamp());

        //Assert that the Comment is not added when the user does not exist.
        SecurityContextHolder.clearContext();
        authentication = new UsernamePasswordAuthenticationToken("Zayyan", null, List.of(new SimpleGrantedAuthority(role)));
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);


        // Now, when you call getAuthentication().getName(), it should return the correct username

        RuntimeException exception1 = Assertions.assertThrows(RuntimeException.class, ()->{
            postService.addCommentToPost(post.getId(), comment);
        });
        Assertions.assertEquals("User not found",exception1.getMessage());

        //Assert that the Comment is not added when the post does not exist.
        SecurityContextHolder.clearContext();
        authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        Long invalidId = post.getId() + 1;
        RuntimeException exception2 = Assertions.assertThrows(RuntimeException.class, ()->{
            postService.addCommentToPost(invalidId, comment);
        });
        Assertions.assertEquals("Post not found",exception2.getMessage());
    }


    @Test
    void testLikePost() {
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());

        String role = "ROLE_" + user1.getRole(); // If roles are stored without prefix

        // Create an authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));

        // Set authentication in security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Now, when you call getAuthentication().getName(), it should return the correct username
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Assertions.assertEquals("Zeerak", currentUsername);

        Post post = postRepository.save(new Post(null,user1,"My first post!",null,null,null));
        Assertions.assertNotNull(post);
        Assertions.assertNotNull(post.getId());

        PostResponseDTO postResponseDTO = postService.likePost(post.getId());
        Assertions.assertTrue(postResponseDTO.getLikes() > 0);

        //Assert that the Comment is not added when the user does not exist.
        SecurityContextHolder.clearContext();
        authentication = new UsernamePasswordAuthenticationToken("Zayyan", null, List.of(new SimpleGrantedAuthority(role)));
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);


        RuntimeException exception1 = Assertions.assertThrows(RuntimeException.class, ()->{
            postService.likePost(post.getId());
        });
        Assertions.assertEquals("User not found",exception1.getMessage());

        //Assert that the Comment is not added when the post does not exist.
        SecurityContextHolder.clearContext();
        authentication = new UsernamePasswordAuthenticationToken(user1.getUsername(), null, List.of(new SimpleGrantedAuthority(role)));
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        Long invalidId = post.getId() + 1;
        RuntimeException exception2 = Assertions.assertThrows(RuntimeException.class, ()->{
            postService.likePost(invalidId);
        });
        Assertions.assertEquals("Post not found",exception2.getMessage());
    }

}