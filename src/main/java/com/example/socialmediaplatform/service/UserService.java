package com.example.socialmediaplatform.service;

import com.example.socialmediaplatform.dto.*;
import com.example.socialmediaplatform.model.Follow;
import com.example.socialmediaplatform.model.User;
import com.example.socialmediaplatform.repository.FollowRepository;
import com.example.socialmediaplatform.repository.UserRepository;
import com.example.socialmediaplatform.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 0. Login User
    public String login(LoginDto loginDto) {
        log.info("UserService - login");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(),
                loginDto.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    // 1. Create User
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        log.info("UserService- createUser() ");
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setRole(requestDTO.getRole());
        User savedUser = userRepository.save(user);
        return savedUser.toResponse();
    }

    // 2. Get User by ID
    public UserResponseDTO getUserById(Long userId) {
        log.info("UserService - getUserById()");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.toResponse();
    }

    // 3. Get All Users
    @Transactional
    public List<UserResponseDTO> getAllUsers() {
        log.info("UserService - getAllUsers()");
        return userRepository.findAll().stream().map(User::toResponse).toList();
    }

    public Page<User> findAll(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    // 4. Update User
    public UserResponseDTO updateUser(Long userId, UserRequestDTO requestDTO) {
        log.info("UserService - updateUser()");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword())); // Hashing recommended

        User updatedUser = userRepository.save(user);
        return updatedUser.toResponse();
    }

    // 5. Delete User
    public void deleteUser(Long userId) {
        log.info("UserService - deleteUser()");
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    // 6. Search
    public Page<User> search(String keyword, int page, int size){
        log.info("UserService - search()");
        log.info("Keyword - {}",keyword);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username"));
        return userRepository.findByUsernameContainingOrEmailContainingOrBioContaining(keyword,keyword,keyword,pageable);
    }

    //7. Follow User
    public FollowingResponseDTO followUser(Long targetUserId){

        // Get the authenticated user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByUsernameOrEmail(currentUsername,currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Follow follow = new Follow(null, authenticatedUser, targetUser);

        return followRepository.save(follow).FollowingToResponse();
    }

    //8. Get Followers
    @Transactional
    public List<FollowerResponseDTO> getFollowers(Long id){

        User foundUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return foundUser.getFollowers().stream().map(Follow::FollowerToResponse).toList();
    }

    //9. Get Following
    public List<FollowingResponseDTO> getFollowing(Long id){

        User foundUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return foundUser.getFollowing().stream().map(Follow::FollowingToResponse).toList();

    }

}
