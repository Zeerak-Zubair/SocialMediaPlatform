package com.example.socialmediaplatform.controller;

import com.example.socialmediaplatform.dto.CommentRequestDTO;
import com.example.socialmediaplatform.dto.CommentResponseDTO;
import com.example.socialmediaplatform.dto.PostRequestDTO;
import com.example.socialmediaplatform.dto.PostResponseDTO;
import com.example.socialmediaplatform.model.Post;
import com.example.socialmediaplatform.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    //`POST /posts` - Create a new post
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostRequestDTO requestDTO) {
        log.info("PostController - createPost()");
        return ResponseEntity.ok(postService.createPost(requestDTO));
    }

    //`GET /posts/{id}` - Retrieve a post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        log.info("PostController - getPostById()");
        return ResponseEntity.ok(postService.getPostById(id));
    }

    //`GET /posts`
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        log.info("PostController - getAllPosts()");
       try {
            List<PostResponseDTO> posts;
            Pageable paging = PageRequest.of(page, size);

            Page<PostResponseDTO> pagePosts;
            if (keyword == null)
                pagePosts = postService.findAll(paging);
            else
                pagePosts = postService.findByContentContaining(keyword, paging);

            posts = pagePosts.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("currentPage", pagePosts.getNumber());
            response.put("totalItems", pagePosts.getTotalElements());
            response.put("totalPages", pagePosts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //`PUT /posts/{id}` - Update an existing post
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @RequestBody PostRequestDTO requestDTO) {
        log.info("PostController - updatePost()");
        return ResponseEntity.ok(postService.updatePost(id, requestDTO));
    }

    //`DELETE /posts/{id}` - Delete a post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        log.info("PostController - deletePost()");
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    //`GET` /posts/user/{userId} - Get Posts By User Id
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponseDTO>> getPostsByUserId(@PathVariable Long userId) {
        log.info("PostController - getPostsByUserId()");
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }

    //`POST /posts/{id}/comments` - Add a comment to a post
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDTO> addComment(@PathVariable Long id, @RequestBody CommentRequestDTO commentRequestDTO){
        return ResponseEntity.ok(postService.addCommentToPost(id,commentRequestDTO.toEntity()));
    }

    //`POST /posts/{id}/like` - Like a post
    @PostMapping("/{id}/like")
    public ResponseEntity<PostResponseDTO> likePost(@PathVariable Long id){
        return ResponseEntity.ok(postService.likePost(id));
    }

    //`POST /posts/search` - Search for posts based on keywords in the title or content with pagination.
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        log.info("PostController - search()");
        try {
            List<PostResponseDTO> posts;
            Pageable paging = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "content"));

            Page<PostResponseDTO> pagePosts;
            if (keyword == null)
                pagePosts = postService.findAll(paging);
            else
                pagePosts = postService.search(keyword, paging);

            posts = pagePosts.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("currentPage", pagePosts.getNumber());
            response.put("totalItems", pagePosts.getTotalElements());
            response.put("totalPages", pagePosts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
