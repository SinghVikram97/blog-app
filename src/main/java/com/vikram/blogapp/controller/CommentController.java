package com.vikram.blogapp.controller;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.ErrorResponse;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Create a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the comment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDTO.class)) }),
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
    public ResponseEntity<CommentDTO> createComment(@RequestBody @Valid CommentDTO commentDTO) {
        CommentDTO comment = commentService.createComment(commentDTO);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the comment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDTO.class)) }),
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
    public ResponseEntity<CommentDTO> updateComment(@RequestBody @Valid CommentDTO commentDTO, @PathVariable Long commentId) {
        CommentDTO updatedComment = commentService.updateComment(commentDTO, commentId);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted the comment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable Long commentId) {
        CommentDTO updatedComment = commentService.deleteComment(commentId);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Get a comment by commentId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the comment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))}
    )
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long commentId) {
        CommentDTO comment = commentService.getCommentById(commentId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
}
