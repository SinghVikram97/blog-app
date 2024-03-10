package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.entities.Comment;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.entities.Role;
import com.vikram.blogapp.entities.User;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.exception.UserNotAuthorizedException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.CommentRepository;
import com.vikram.blogapp.repository.PostRepository;
import com.vikram.blogapp.repository.UserRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import static com.vikram.blogapp.constants.Constants.MDC_ROLE_KEY;
import static com.vikram.blogapp.constants.Constants.MDC_USERNAME_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentServiceImplTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment_whenSameUser_ReturnsCommentDTO(){
        Comment comment = generateRandomComment();
        CommentDTO commentDTO = generateRandomCommentDTO();
        User user = generateRandomUser();
        Post post = generateRandomPost();

        commentDTO.setUserId(user.getId());
        commentDTO.setPostId(post.getId());

        when(userRepository.findById(commentDTO.getUserId())).thenReturn(java.util.Optional.of(user));
        when(postRepository.findById(commentDTO.getPostId())).thenReturn(java.util.Optional.of(post));

        when(modelMapper.dtoToCommentDao(commentDTO)).thenReturn(comment);
        when(commentRepository.save(any())).thenReturn(comment);

        when(modelMapper.daoToCommentDTO(any())).thenReturn(commentDTO);
        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        CommentDTO createdCommentDTO = commentService.createComment(commentDTO);

        assertNotNull(createdCommentDTO);
        verify(userRepository, times(1)).findById(user.getId());
        verify(postRepository, times(1)).findById(post.getId());
        verify(commentRepository, times(1)).save(comment);

        assertCommentDTOEqual(commentDTO, createdCommentDTO);
    }

    @Test
    void testCreateComment_whenDifferentUser_throwsUserNotAuthorizedException() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        User user = generateRandomUser();
        commentDTO.setUserId(user.getId());

        when(userRepository.findById(commentDTO.getUserId())).thenReturn(java.util.Optional.of(user));
        MDC.put(MDC_USERNAME_KEY, "randomEmail.com");

        assertThrows(UserNotAuthorizedException.class, () -> {
            commentService.createComment(commentDTO);
        });
    }

    @Test
    void testCreateComment_whenUserNotFound_throwsResourceNotFoundException() {
        CommentDTO commentDTO = generateRandomCommentDTO();

        when(userRepository.findById(commentDTO.getUserId())).thenReturn(java.util.Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.createComment(commentDTO);
        });
    }

    @Test
    void testCreateComment_whenPostNotFound_throwsResourceNotFoundException() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        User user = generateRandomUser();

        when(userRepository.findById(commentDTO.getUserId())).thenReturn(java.util.Optional.of(user));
        when(postRepository.findById(commentDTO.getPostId())).thenReturn(java.util.Optional.empty());
        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.createComment(commentDTO);
        });
    }

    @Test
    void testUpdateComment_whenSameUser_returnsUpdatedCommentDTO() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        Comment existingComment = generateRandomComment();
        User user = generateRandomUser();
        Post post = generateRandomPost();
        existingComment.setUser(user);

        when(commentRepository.findById(commentDTO.getId())).thenReturn(java.util.Optional.of(existingComment));
        when(userRepository.findById(commentDTO.getUserId())).thenReturn(java.util.Optional.of(user));
        when(postRepository.findById(commentDTO.getPostId())).thenReturn(java.util.Optional.of(post));
        when(commentRepository.save(existingComment)).thenReturn(existingComment);
        when(modelMapper.daoToCommentDTO(existingComment)).thenReturn(commentDTO);

        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        // Act
        CommentDTO updatedCommentDTO = commentService.updateComment(commentDTO, commentDTO.getId());

        // Assert
        assertNotNull(updatedCommentDTO);
        verify(commentRepository, times(1)).findById(commentDTO.getId());
        verify(commentRepository, times(1)).save(existingComment);
        assertCommentDTOEqual(commentDTO, updatedCommentDTO);
    }

    @Test
    void testUpdateComment_whenDifferentUser_throwsUserNotAuthorizedException() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        Comment existingComment = generateRandomComment();
        User user = generateRandomUser();
        User differentUser = generateRandomUser();
        existingComment.setUser(user);

        when(commentRepository.findById(commentDTO.getId())).thenReturn(java.util.Optional.of(existingComment));

        MDC.put(MDC_USERNAME_KEY, differentUser.getEmail());

        assertThrows(UserNotAuthorizedException.class, () -> {
            commentService.updateComment(commentDTO, commentDTO.getId());
        });
    }

    @Test
    void testUpdateComment_whenUserNotFound_throwsResourceNotFoundException() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        Comment existingComment = generateRandomComment();
        User user = generateRandomUser();
        existingComment.setUser(user);

        when(commentRepository.findById(commentDTO.getId())).thenReturn(java.util.Optional.of(existingComment));
        when(userRepository.findById(commentDTO.getUserId())).thenReturn(java.util.Optional.empty());
        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(commentDTO, commentDTO.getId());
        });
    }

    @Test
    void testUpdateComment_whenPostNotFound_throwsResourceNotFoundException() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        Comment existingComment = generateRandomComment();
        User user = generateRandomUser();
        existingComment.setUser(user);

        when(commentRepository.findById(commentDTO.getId())).thenReturn(java.util.Optional.of(existingComment));
        when(userRepository.findById(commentDTO.getUserId())).thenReturn(java.util.Optional.of(user));
        when(postRepository.findById(any())).thenReturn(java.util.Optional.empty());
        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(commentDTO, commentDTO.getId());
        });
    }

    @Test
    void testUpdateComment_whenCommentNotFound_throwsResourceNotFoundException() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        when(commentRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(commentDTO, commentDTO.getId());
        });
    }

    @Test
    void testDeleteComment_whenSameUser_returnsDeletedCommentDTO() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        Comment comment = generateRandomComment();
        User user = generateRandomUser();
        comment.setUser(user);

        when(commentRepository.findById(commentDTO.getId())).thenReturn(java.util.Optional.of(comment));
        when(modelMapper.daoToCommentDTO(comment)).thenReturn(commentDTO);
        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        CommentDTO deletedCommentDTO = commentService.deleteComment(commentDTO.getId());

        // Assert
        assertNotNull(deletedCommentDTO);
        verify(commentRepository, times(1)).delete(comment);
        assertCommentDTOEqual(commentDTO, deletedCommentDTO);
    }

    @Test
    void testDeleteComment_whenAdmin_returnsDeletedCommentDTO() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        Comment comment = generateRandomComment();
        User user = generateRandomUser();
        comment.setUser(user);

        when(commentRepository.findById(commentDTO.getId())).thenReturn(java.util.Optional.of(comment));
        when(modelMapper.daoToCommentDTO(comment)).thenReturn(commentDTO);
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.toString());

        CommentDTO deletedCommentDTO = commentService.deleteComment(commentDTO.getId());

        // Assert
        assertNotNull(deletedCommentDTO);
        verify(commentRepository, times(1)).delete(comment);
        assertCommentDTOEqual(commentDTO, deletedCommentDTO);
    }

    @Test
    void testDeleteComment_whenDifferentUserAndNotAdmin_throwsUserNotAuthorizedException() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        Comment comment = generateRandomComment();
        User user = generateRandomUser();
        User differentUser = generateRandomUser();

        comment.setUser(user);

        when(commentRepository.findById(commentDTO.getId())).thenReturn(java.util.Optional.of(comment));
        MDC.put(MDC_USERNAME_KEY, differentUser.getEmail());

        assertThrows(UserNotAuthorizedException.class, () -> {
            commentService.deleteComment(commentDTO.getId());
        });
    }

    @Test
    void testDeleteComment_whenCommentNotFound_throwsResourceNotFoundException() {
        CommentDTO commentDTO = generateRandomCommentDTO();

        when(commentRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteComment(commentDTO.getId());
        });
    }

    @Test
    void testGetCommentById_whenSameUser_returnsCommentDTO() {
        CommentDTO commentDTO = generateRandomCommentDTO();
        Comment comment = generateRandomComment();
        User user = generateRandomUser();
        comment.setUser(user);

        when(commentRepository.findById(commentDTO.getId())).thenReturn(java.util.Optional.of(comment));
        when(modelMapper.daoToCommentDTO(comment)).thenReturn(commentDTO);

        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        CommentDTO actualCommentDTO = commentService.getCommentById(commentDTO.getId());

        assertNotNull(actualCommentDTO);
        assertCommentDTOEqual(commentDTO, actualCommentDTO);
    }

    @Test
    void testGetCommentById_whenDifferentUserButAdmin_returnsCommentDTO() {
        CommentDTO expectedCommentDTO = generateRandomCommentDTO();
        Comment comment = generateRandomComment();

        when(commentRepository.findById(expectedCommentDTO.getId())).thenReturn(java.util.Optional.of(comment));
        when(modelMapper.daoToCommentDTO(comment)).thenReturn(expectedCommentDTO);

        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());

        CommentDTO actualCommentDTO = commentService.getCommentById(expectedCommentDTO.getId());

        // Assert
        assertNotNull(actualCommentDTO);
        assertCommentDTOEqual(expectedCommentDTO, actualCommentDTO);
    }

    @Test
    void testGetCommentById_whenDifferentUserAndNotAdmin_throwsUserNotAuthorizedException() {
        CommentDTO expectedCommentDTO = generateRandomCommentDTO();
        Comment comment = generateRandomComment();
        User user = generateRandomUser();
        comment.setUser(user);

        when(commentRepository.findById(expectedCommentDTO.getId())).thenReturn(java.util.Optional.of(comment));

        MDC.put(MDC_USERNAME_KEY, "different_user@example.com");

        assertThrows(UserNotAuthorizedException.class, () -> {
            commentService.getCommentById(expectedCommentDTO.getId());
        });
    }

    @Test
    void testGetCommentById_whenCommentNotFound_throwsResourceNotFoundException() {
        long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getCommentById(commentId);
        });
    }


    private void assertCommentDTOEqual(CommentDTO expectedCommentDTO, CommentDTO actualCommentDTO) {
        assertEquals(expectedCommentDTO.getId(), actualCommentDTO.getId());
        assertEquals(expectedCommentDTO.getUserId(), actualCommentDTO.getUserId());
        assertEquals(expectedCommentDTO.getPostId(), actualCommentDTO.getPostId());
        assertEquals(expectedCommentDTO.getContent(), actualCommentDTO.getContent());
    }

    private CommentDTO generateRandomCommentDTO() {
        return Instancio.create(CommentDTO.class);
    }

    private Comment generateRandomComment() {
        return Instancio.create(Comment.class);
    }

    private User generateRandomUser() {
        return Instancio.create(User.class);
    }

    private Post generateRandomPost() {
        return Instancio.create(Post.class);
    }
}