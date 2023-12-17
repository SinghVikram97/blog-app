package com.vikram.blogapp.controller;

import com.vikram.blogapp.dto.PaginationResponseDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // Create a Post
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody @Valid PostDTO postDTO) {
        PostDTO post = postService.createPost(postDTO);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    // Update a Post
    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(@RequestBody @Valid PostDTO postDTO, @PathVariable long postId){
        PostDTO updatedPost = postService.updatePost(postDTO, postId);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    // DELETE - Delete a Post
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDTO> deletePost(@PathVariable long postId){
        PostDTO deletedPost = postService.deletePost(postId);
        return new ResponseEntity<>(deletedPost, HttpStatus.OK);
    }

    // GET - Post by ID
    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(@PathVariable long postId){
        PostDTO postDTO = postService.getPostById(postId);
        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    // GET - Get all posts
    @GetMapping
    public ResponseEntity<PaginationResponseDTO> getAllPosts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        PaginationResponseDTO paginationResponseDTO = postService.getAllPosts(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(paginationResponseDTO, HttpStatus.OK);
    }

    // Search posts
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<PostDTO>> searchPostByTitle(
            @PathVariable("keyword") String keyword
    ) {
        List<PostDTO> result = postService.searchPosts(keyword);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
