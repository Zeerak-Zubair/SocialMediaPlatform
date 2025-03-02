package com.example.socialmediaplatform.controller;

import com.example.socialmediaplatform.dto.*;
import com.example.socialmediaplatform.model.Post;
import com.example.socialmediaplatform.model.User;
import com.example.socialmediaplatform.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    //`POST /users/login` - Authenticate a user and generate a JWT
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto){
        log.info("User Controller - login()");
        String token = userService.login(loginDto);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }

    //`POST /users/register` - Register a new user
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO requestDTO) {
        log.info("User Controller - createUser()");
        return ResponseEntity.ok(userService.createUser(requestDTO));
    }

    //`GET /users/{id}` - Retrieve a user profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("User Controller - getUserById()");
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("User Controller - getAllUsers()");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO requestDTO) {
        log.info("User Controller - updateUser()");
        return ResponseEntity.ok(userService.updateUser(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("User Controller - deleteUser()");
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    //`POST /users/{id}/follow` - Follow a user
    @PostMapping("{id}/follow")
    public FollowingResponseDTO followUser(@PathVariable Long id){
        log.info("User Controller - followUser()");
        return userService.followUser(id);
    }

    //`GET /users/{id}/followers` - Retrieve a user's followers
    @GetMapping("{id}/followers")
    public List<FollowerResponseDTO> getFollowers(@PathVariable Long id){
        log.info("User Controller - getFollowers()");
        return userService.getFollowers(id);
    }

    //`GET /users/{id}/following` - Retrieve users followed by a user
    @GetMapping("{id}/following")
    public List<FollowingResponseDTO> getFollowing(@PathVariable Long id){
        log.info("User Controller - getFollowing()");
        return userService.getFollowing(id);
    }

    //`POST /users/search` - Search for users based on keywords in the username or email or bio with pagination.
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        log.info("User Controller - search()");
        try {
            List<User> users;
            Pageable paging = PageRequest.of(page, size);

            Page<User> pageUsers;
            if (keyword == null)
                pageUsers = userService.findAll(paging);
            else
                pageUsers = userService.search(keyword, page, size);

            users = pageUsers.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            response.put("currentPage", pageUsers.getNumber());
            response.put("totalItems", pageUsers.getTotalElements());
            response.put("totalPages", pageUsers.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
