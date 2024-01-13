package com.vikram.blogapp.controller;

import com.vikram.blogapp.dto.*;
import com.vikram.blogapp.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @PostMapping
    @Operation(summary = "Creates a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the post",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Request body is invalid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    public ResponseEntity<PostDTO> createPost(@RequestBody @Valid PostDTO postDTO) {
        PostDTO post = postService.createPost(postDTO);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Updates a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the post",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Request body is invalid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    public ResponseEntity<PostDTO> updatePost(@RequestBody @Valid PostDTO postDTO, @PathVariable long postId){
        PostDTO updatedPost = postService.updatePost(postDTO, postId);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @Operation(summary = "Deletes a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted the post",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDTO> deletePost(@PathVariable long postId){
        PostDTO deletedPost = postService.deletePost(postId);
        return new ResponseEntity<>(deletedPost, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get a post by postId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the post",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    public ResponseEntity<PostDTO> getPost(@PathVariable long postId){
        PostDTO postDTO = postService.getPostById(postId);
        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Updates a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns all the posts",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponseDTO.class)) }),
            @ApiResponse(responseCode = "403", description = "User not authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    public ResponseEntity<PaginationResponseDTO> getAllPosts(
            @RequestParam(value = "pageNumber", defaultValue = PAGINATION_DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = PAGINATION_DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = PAGINATION_DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = PAGINATION_DEFAULT_SORT_DIR, required = false) String sortDir
    ){
        PaginationResponseDTO paginationResponseDTO = postService.getAllPosts(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(paginationResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/search/{keyword}")
    @Operation(summary = "Search for a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the relevant posts",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "User not authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    public ResponseEntity<List<PostDTO>> searchPostByTitle(
            @PathVariable("keyword") String keyword
    ) {
        List<PostDTO> result = postService.searchPosts(keyword);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "Get all comments for a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the relevant posts",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommentDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    public ResponseEntity<List<CommentDTO> > getAllComments(@PathVariable long postId){
        List<CommentDTO> allComments = postService.getAllComments(postId);
        return new ResponseEntity<>(allComments, HttpStatus.OK);
    }
}
