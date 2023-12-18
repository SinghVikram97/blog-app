package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CommentDTO;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    // Create comment
    CommentDTO createComment(CommentDTO commentDTO);

    // Update comment
    CommentDTO updateComment(CommentDTO commentDTO, long commentId);

    // Delete comment
    CommentDTO deleteComment(long commentId);

    // Get comment by id
    CommentDTO getCommentById(long commentId);

    // Get list of comments -> to be done in post and user
}
