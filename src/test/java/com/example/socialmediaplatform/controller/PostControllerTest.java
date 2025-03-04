package com.example.socialmediaplatform.controller;

import com.example.socialmediaplatform.dto.*;
import com.example.socialmediaplatform.model.Post;
import com.example.socialmediaplatform.model.User;
import com.example.socialmediaplatform.repository.PostRepository;
import com.example.socialmediaplatform.repository.UserRepository;
import com.example.socialmediaplatform.security.SecurityConfig;
import com.example.socialmediaplatform.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.parser.Authorization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(SecurityConfig.class) // Ensures security settings are applied
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private User user1;
    //private User user2;
    private Post post1;
    private Post post2;
    private String accessToken;

    @BeforeEach
    void setup(){
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    public void registerUser(){
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());
    }

    public void loginUser(){
        accessToken = userService.login(new LoginDto("Zeerak","Zeerak"));
        Assertions.assertNotNull(accessToken);
    }

    @Test
    public void createPostTest() throws Exception{
        registerUser(); //register users
        loginUser(); //login as Zeerak

        PostRequestDTO postRequestDTO = new PostRequestDTO("This is my first post!");
        accessToken = "Bearer " + accessToken;

        ResultActions response = mockMvc.perform(post("/posts")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        String responseBody = response.andReturn().getResponse().getContentAsString();

        PostResponseDTO postResponseDTO = objectMapper.readValue(responseBody, PostResponseDTO.class);

        Assertions.assertNotNull(postResponseDTO);
        Assertions.assertEquals("This is my first post!", postResponseDTO.getContent());
    }

    public void createPost(){
        post1 = postRepository.save(new Post(null,user1,"This is my first post!",null,null,null));
        post2 = postRepository.save(new Post(null,user1,"This is my second post!",null,null,null));
    }

    @Test
    public void getPostByIdTest() throws Exception{
        registerUser(); //register users
        loginUser(); //login as Zeerak
        createPost();

        accessToken = "Bearer " + accessToken;

        ResultActions response = mockMvc.perform(get("/posts/{id}", post1.getId()) // Pass path variable
                .header(HttpHeaders.AUTHORIZATION, accessToken) // Attach Bearer token
                .contentType(MediaType.APPLICATION_JSON)) // Specify content type
                ;

        String responseBody = response.andReturn().getResponse().getContentAsString();

        PostResponseDTO postResponseDTO = objectMapper.readValue(responseBody, PostResponseDTO.class);
        Assertions.assertNotNull(postResponseDTO);
        Assertions.assertEquals("This is my first post!", postResponseDTO.getContent());
        Assertions.assertNotNull(postResponseDTO.getCreatedAt());
    }

    @Test
    public void getAllPostsTest() throws Exception{
        registerUser(); //register users
        loginUser(); //login as Zeerak
        createPost();

        ResultActions response = mockMvc.perform(get("/posts")
                        .param("page", "0")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Extract Response
        String responseBody = response.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<>() {});

        // Assertions
        Assertions.assertNotNull(responseMap);
        Assertions.assertTrue(responseMap.containsKey("posts"));
        Assertions.assertTrue(responseMap.containsKey("currentPage"));
        Assertions.assertTrue(responseMap.containsKey("totalItems"));
        Assertions.assertTrue(responseMap.containsKey("totalPages"));

        List<PostResponseDTO> posts = objectMapper.convertValue(responseMap.get("posts"), new TypeReference<>() {});
        Assertions.assertEquals(2, posts.size());
    }

    @Test
    public void updatePost() throws Exception{
        registerUser(); //register users
        loginUser(); //login as Zeerak
        createPost();

        accessToken = "Bearer " + accessToken;

        PostRequestDTO postRequestDTO = new PostRequestDTO("Updated Post");
        ResultActions response = mockMvc.perform(put("/posts/{id}", post1.getId())
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        String responseBody = response.andReturn().getResponse().getContentAsString();
        PostResponseDTO postResponseDTO = objectMapper.readValue(responseBody, PostResponseDTO.class);

        Assertions.assertNotNull(postResponseDTO);
        Assertions.assertEquals("Updated Post", postResponseDTO.getContent());
    }

    @Test
    public void deletePostTest() throws Exception{
        registerUser(); //register users
        loginUser(); //login as Zeerak
        createPost();

        accessToken = "Bearer " + accessToken;
        ResultActions response = mockMvc.perform(delete("/posts/{id}",post1.getId())
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        Boolean exists = postRepository.existsById(post1.getId());
        Assertions.assertFalse(exists);
    }

    @Test
    public void getPostsByUserIdTest() throws Exception{
        registerUser(); //register users
        loginUser(); //login as Zeerak
        createPost();

        accessToken = "Bearer " + accessToken;
        ResultActions response = mockMvc.perform(get("/posts/user/{userId}", user1.getId())
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        String responseBody = response.andReturn().getResponse().getContentAsString();
        List<PostResponseDTO> postResponses = objectMapper.readValue(responseBody, new TypeReference<>() {});
        Assertions.assertNotNull(postResponses);
        Assertions.assertEquals(2,postResponses.size());
        Assertions.assertEquals("This is my first post!",postResponses.get(0).getContent());
        Assertions.assertEquals("This is my second post!",postResponses.get(1).getContent());
    }

    @Test
    public void addCommentTest() throws Exception{
        registerUser();
        loginUser(); // Logs in as Zeerak

        createPost();
        accessToken = "Bearer " + accessToken;

        Long postId = post1.getId();
        CommentRequestDTO commentRequestDTO = new CommentRequestDTO("This is a test comment");

        ResultActions response = mockMvc.perform(post("/posts/{id}/comments", postId)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        String responseBody = response.andReturn().getResponse().getContentAsString();
        CommentResponseDTO returnedComment = objectMapper.readValue(responseBody, CommentResponseDTO.class);

        Assertions.assertNotNull(returnedComment);
        Assertions.assertEquals("This is a test comment", returnedComment.getContent());
        Assertions.assertNotNull(returnedComment.getCreatedAt());
    }

    @Test
    public void likePostTest() throws Exception{
        registerUser();
        loginUser(); // Logs in as Zeerak
        createPost();

        accessToken = "Bearer " + accessToken;


        ResultActions response = mockMvc.perform(post("/posts/{id}/like", post1.getId())
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        String responseBody = response.andReturn().getResponse().getContentAsString();
        PostResponseDTO returnedPost = objectMapper.readValue(responseBody, PostResponseDTO.class);

        Assertions.assertNotNull(returnedPost);
        Assertions.assertEquals("This is my first post!", returnedPost.getContent());
        Assertions.assertEquals(1, returnedPost.getLikes()); // Assuming it's the first like
    }

    @Test
    public void searchTest() throws Exception{
        registerUser(); //register users
        loginUser(); //login as Zeerak
        createPost();
        accessToken = "Bearer " + accessToken;

        ResultActions response = mockMvc.perform(post("/posts/search")
                        .param("keyword", "post!")
                        .param("page", "0")
                        .param("size", "3")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        String responseBody = response.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<>() {});

        Assertions.assertNotNull(responseMap);
        Assertions.assertTrue(responseMap.containsKey("posts"));
        Assertions.assertTrue(responseMap.containsKey("currentPage"));
        Assertions.assertTrue(responseMap.containsKey("totalItems"));
        Assertions.assertTrue(responseMap.containsKey("totalPages"));

        List<PostResponseDTO> posts = objectMapper.convertValue(responseMap.get("posts"), new TypeReference<>() {});
        Assertions.assertEquals(2, posts.size());

    }
}