package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.entities.Comment;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.entities.User;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.CommentRepository;
import com.vikram.blogapp.repository.PostRepository;
import com.vikram.blogapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {
        // Get userId
        long userId = commentDTO.getUserId();
        // Find user
        User userDao = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));

        // Get postId
        long postId = commentDTO.getPostId();
        // Find post
        Post postDAO = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post","id",postId));

        // Convert commentDTO to commentDAO, we get only content in dao
        Comment commentDAO = modelMapper.dtoToCommentDao(commentDTO);

        // Add user and post
        commentDAO.setUser(userDao);
        commentDAO.setPost(postDAO);

        // Save comment in db
        Comment savedComment = commentRepository.save(commentDAO);

        // Update user and post with this comment
        userDao.addComment(savedComment);
        postDAO.addComment(savedComment);

        return modelMapper.daoToCommentDTO(savedComment);
    }

    @Override
    public CommentDTO updateComment(CommentDTO commentDTO, long commentId) {
        Comment commentDAO = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id",commentId));

        // Update comment
        commentDAO.setContent(commentDTO.getContent());

        // Find user
        User userDao = userRepository.findById(commentDTO.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User","id",commentDTO.getUserId()));

        // Find post
        Post postDao = postRepository.findById(commentDTO.getPostId()).orElseThrow(() -> new ResourceNotFoundException("Post","id",commentDTO.getPostId()));

        // Update both
        commentDAO.setUser(userDao);
        commentDAO.setPost(postDao);

        // Save in DB
        Comment updatedComment = commentRepository.save(commentDAO);

        return modelMapper.daoToCommentDTO(updatedComment);
    }

    @Override
    public CommentDTO deleteComment(long commentId) {
        Comment commentDAO = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id",commentId));

        // Get user and post
        User user = commentDAO.getUser();
        Post post = commentDAO.getPost();

        // Delete from user and post
        user.removeComment(commentDAO);
        post.removeComment(commentDAO);

        commentRepository.delete(commentDAO);

        return modelMapper.daoToCommentDTO(commentDAO);
    }

    @Override
    public CommentDTO getCommentById(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        return modelMapper.daoToCommentDTO(comment);
    }
}
