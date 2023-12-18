package com.vikram.blogapp.controller;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PaginationResponseDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.vikram.blogapp.constants.Constants.PAGINATION_DEFAULT_PAGE_NUMBER;
import static com.vikram.blogapp.constants.Constants.PAGINATION_DEFAULT_PAGE_SIZE;
import static com.vikram.blogapp.constants.Constants.PAGINATION_DEFAULT_SORT_BY;
import static com.vikram.blogapp.constants.Constants.PAGINATION_DEFAULT_SORT_DIR;

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
            @RequestParam(value = "pageNumber", defaultValue = PAGINATION_DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = PAGINATION_DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = PAGINATION_DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = PAGINATION_DEFAULT_SORT_DIR, required = false) String sortDir
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

    // Get all comments for a post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO> > getAllComments(@PathVariable long postId){
        List<CommentDTO> allComments = postService.getAllComments(postId);
        return new ResponseEntity<>(allComments, HttpStatus.OK);
    }
}
