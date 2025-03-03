package com.example.socialmediaplatform.service;

import com.example.socialmediaplatform.dto.CommentResponseDTO;
import com.example.socialmediaplatform.dto.PostRequestDTO;
import com.example.socialmediaplatform.dto.PostResponseDTO;
import com.example.socialmediaplatform.model.*;
import com.example.socialmediaplatform.repository.CommentRepository;
import com.example.socialmediaplatform.repository.LikeRepository;
import com.example.socialmediaplatform.repository.PostRepository;
import com.example.socialmediaplatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    // 1. Create Post
    public PostResponseDTO createPost(PostRequestDTO requestDTO) {
        log.info("PostService - createPost()");

        // Get the authenticated user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Current Username: {}",currentUsername);
        User authenticatedUser = userRepository.findByUsernameOrEmail(currentUsername, currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setContent(requestDTO.getContent());
        post.setUser(authenticatedUser);

        Post savedPost = postRepository.save(post);
        return savedPost.toResponse();
    }

    // 2. Get Post by ID
    @Transactional
    public PostResponseDTO getPostById(Long postId) {
        log.info("PostService - getPostById()");
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return post.toResponse();
    }

    @Transactional
    public List<PostResponseDTO> getAllPosts() {
        log.info("PostService - getAllPosts()");
        return postRepository.findAll().stream()
                .map(Post::toResponse).toList();
    }

    public Page<Post> findAll(Pageable pageable){
        return postRepository.findAll(pageable);
    }

    public Page<Post> findByContentContaining(String keyword, Pageable paging){
        return postRepository.findByContentContaining(keyword,paging);
    }

    // 4. Update Post
    @Transactional
    public PostResponseDTO updatePost(Long postId, PostRequestDTO requestDTO) {
        log.info("PostService - updatePost()");

        // Get the authenticated user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if the current user is the post owner
        if (!post.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You are not authorized to edit this post.");
        }

        post.setContent(requestDTO.getContent());

        Post updatedPost = postRepository.save(post);
        return updatedPost.toResponse();
    }

    // 5. Delete Post
    public void deletePost(Long postId) {
        log.info("PostService - deletePost()");

        // Get the authenticated user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if the current user is the post owner
        if (!post.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You are not authorized to delete this post.");
        }

        postRepository.deleteById(postId);
    }

    // 6. Get Posts by User ID
    @Transactional
    public List<PostResponseDTO> getPostsByUserId(Long userId) {
        log.info("PostService - getPostsByUserId()");

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(Post::toResponse)
                .toList();
    }

    // 6. Search
    public Page<Post> search(String keyword, Pageable pageable){
        log.info("PostService - search()");
        return postRepository.findByContentContaining(keyword, pageable);
    }

    //7. Add a Comment To a POst
    public CommentResponseDTO addCommentToPost(Long postId, Comment comment){

        // Get the authenticated user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByUsernameOrEmail(currentUsername,currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        log.info("Received comment {}",comment.getContent());

        comment.setUser(authenticatedUser);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);

        return savedComment.toResponse();
    }

    //8. Like a Post
    public PostResponseDTO likePost(Long postId){

        // Get the authenticated user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByUsernameOrEmail(currentUsername,currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostLikeId likeId = new PostLikeId(postId,authenticatedUser.getId());
        Like like = new Like(likeId, post, authenticatedUser);
        likeRepository.save(like);

        return postRepository.findById(postId).get().toResponse();
    }


}
