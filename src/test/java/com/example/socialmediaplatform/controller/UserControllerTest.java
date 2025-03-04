package com.example.socialmediaplatform.controller;

import com.example.socialmediaplatform.dto.*;
import com.example.socialmediaplatform.model.User;
import com.example.socialmediaplatform.repository.UserRepository;
import com.example.socialmediaplatform.security.SecurityConfig;
import com.example.socialmediaplatform.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(SecurityConfig.class) // Ensures security settings are applied
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private User user1;
    private User user2;

    private String accessToken;

    @BeforeEach
    void setup(){
        userRepository.deleteAll();
    }

    @Test
    public void registerUserTest() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO("Zeerak","zeerak@gmail.com","Zeerak","img.jpg","Likes car racing","USER");

        ResultActions response = mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // Extract the response body
        String responseBody = response.andReturn().getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);

        UserResponseDTO userResponse = objectMapper.readValue(responseBody, UserResponseDTO.class);

        Assertions.assertNotNull(userResponse);
        Assertions.assertEquals("Zeerak", userResponse.getUsername());
        Assertions.assertEquals("zeerak@gmail.com", userResponse.getEmail());
        Assertions.assertNull(userResponse.getComments());
        Assertions.assertNull(userResponse.getFollower());
        Assertions.assertNull(userResponse.getFollowing());
        Assertions.assertEquals(0, userResponse.getLikes());

    }

    public void registerUser(){
        user1 = userRepository.save(new User(null,"Zeerak","zeerak@gmail.com",passwordEncoder.encode("Zeerak"),"img.jpg","Likes Rock Climbing",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user1.getId());
        user2 = userRepository.save(new User(null,"Zayyan","zayyan@gmail.com",passwordEncoder.encode("Zayyan"),"img.jpg","Likes Eating Pizza",null,null,null,null,null,"USER"));
        Assertions.assertNotNull(user2.getId());
    }

    public void loginUser(){
        accessToken = userService.login(new LoginDto("Zeerak","Zeerak"));
        Assertions.assertNotNull(accessToken);
    }

    @Test
    public void loginUserTest() throws Exception{
        //First we register the user
        registerUser();

        LoginDto loginDto = new LoginDto("zeerak@gmail.com", "Zeerak");

        ResultActions response = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                ;

        String responseBody = response.andReturn().getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);

        JwtAuthResponse jwtAuthResponse = objectMapper.readValue(responseBody, JwtAuthResponse.class);


        Assertions.assertNotNull(jwtAuthResponse);
        Assertions.assertNotNull(jwtAuthResponse.getAccessToken());
        Assertions.assertFalse(jwtAuthResponse.getAccessToken().isEmpty());

    }

    @Test
    public void getUserByIdTest() throws Exception{
        registerUser(); //register users
        loginUser(); //login as Zeerak

        accessToken = "Bearer " + accessToken;

        ResultActions response = mockMvc.perform(get("/users/{id}", user2.getId()) // Pass path variable
                        .header(HttpHeaders.AUTHORIZATION, accessToken) // Attach Bearer token
                        .contentType(MediaType.APPLICATION_JSON)) // Specify content type
                ;

        String responseBody = response.andReturn().getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);

        UserResponseDTO userResponse = objectMapper.readValue(responseBody, UserResponseDTO.class);


        Assertions.assertNotNull(userResponse);
        Assertions.assertEquals("Zayyan", userResponse.getUsername());
        Assertions.assertEquals("zayyan@gmail.com", userResponse.getEmail());
        Assertions.assertNull(userResponse.getComments());
        Assertions.assertNull(userResponse.getFollower());
        Assertions.assertNull(userResponse.getFollowing());
        Assertions.assertEquals(0, userResponse.getLikes());
    }

    @Test
    public void getAllUsersTest() throws Exception{
        registerUser();
        loginUser();

        accessToken = "Bearer " + accessToken;

        ResultActions response = mockMvc.perform(get("/users") // Pass path variable
                        .header(HttpHeaders.AUTHORIZATION, accessToken) // Attach Bearer token
                        .contentType(MediaType.APPLICATION_JSON)) // Specify content type
        ;


        String responseBody = response.andReturn().getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);

        List<UserResponseDTO> userResponses = objectMapper.readValue(responseBody, new TypeReference<>() {});
        Assertions.assertNotNull(userResponses);
        Assertions.assertEquals(2,userResponses.size());
        Assertions.assertEquals("Zeerak",userResponses.get(0).getUsername());
        Assertions.assertEquals("Zayyan",userResponses.get(1).getUsername());
    }

    @Test
    public void updateUserTest() throws Exception{
        registerUser();
        loginUser();

        accessToken = "Bearer " + accessToken;

        UserRequestDTO requestDTO = new UserRequestDTO("Zubair","zubair@gmail.com","Zubair","img.jpg","Likes Racing","USER");

        ResultActions response = mockMvc.perform(put("/users/{id}", user1.getId())
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        String responseBody = response.andReturn().getResponse().getContentAsString();

        UserResponseDTO updatedUser = objectMapper.readValue(responseBody, UserResponseDTO.class);


        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals("Zubair", updatedUser.getUsername());
        Assertions.assertEquals("zubair@gmail.com", updatedUser.getEmail());
    }

    @Test
    public void followUserTes() throws Exception{
        registerUser();
        loginUser();

        ResultActions response = mockMvc.perform(post("/users/{id}/follow", user2.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // Attach Bearer token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String responseBody = response.andReturn().getResponse().getContentAsString();

        FollowingResponseDTO followingResponse = objectMapper.readValue(responseBody, FollowingResponseDTO.class);

        Assertions.assertNotNull(followingResponse);
        Assertions.assertEquals(user2.getUsername(), followingResponse.getUsername());
        Assertions.assertEquals(user2.getEmail(), followingResponse.getEmail());
    }

    public void followUser(){
        userService.followUser(user2.getId());
    }

    @Test
    public void getFollowersTest() throws Exception{
        registerUser();
        loginUser();
        followUser(); //Zeerak follows Zayyan

        ResultActions response = mockMvc.perform(get("/users/{id}/followers", user2.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        String responseBody = response.andReturn().getResponse().getContentAsString();

        List<FollowerResponseDTO> followers = objectMapper.readValue(responseBody, new TypeReference<>() {});

        Assertions.assertNotNull(followers);
        Assertions.assertFalse(followers.isEmpty());
    }

    @Test
    public void getFollowingTest() throws Exception{
        registerUser();
        loginUser();
        followUser(); //Zeerak follows Zayyan

        ResultActions response = mockMvc.perform(get("/users/{id}/following", user1.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        String responseBody = response.andReturn().getResponse().getContentAsString();

        List<FollowingResponseDTO> following = objectMapper.readValue(responseBody, new TypeReference<>() {});

        Assertions.assertNotNull(following);
        Assertions.assertFalse(following.isEmpty());
    }

}