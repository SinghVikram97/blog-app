package com.vikram.blogapp.controller;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.service.CommentService;
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

    // Post - Create comment
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody @Valid CommentDTO commentDTO) {
        CommentDTO comment = commentService.createComment(commentDTO);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    // PUT - Update comment
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@RequestBody @Valid CommentDTO commentDTO, @PathVariable Long commentId) {
        CommentDTO updatedComment = commentService.updateComment(commentDTO, commentId);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    // DELETE - Delete comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable Long commentId) {
        CommentDTO updatedComment = commentService.deleteComment(commentId);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    // GET - Get comment by id
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long commentId) {
        CommentDTO comment = commentService.getCommentById(commentId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
}
