package com.vikram.blogapp.controller;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.service.CommentService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentControllerTest {
    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment_returnsCommentDTO() {
        CommentDTO commentDTO = Instancio.create(CommentDTO.class);
        when(commentService.createComment(commentDTO)).thenReturn(commentDTO);

        ResponseEntity<CommentDTO> responseEntity = commentController.createComment(commentDTO);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsCommentDTO(commentDTO, responseEntity.getBody());
        verify(commentService, times(1)).createComment(commentDTO);
    }

    @Test
    void testUpdateComment_returnsUpdatedCommentDTO() {
        CommentDTO commentDTO = Instancio.create(CommentDTO.class);
        when(commentService.updateComment(commentDTO, commentDTO.getId())).thenReturn(commentDTO);

        ResponseEntity<CommentDTO> responseEntity = commentController.updateComment(commentDTO, commentDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsCommentDTO(commentDTO, responseEntity.getBody());
        verify(commentService, times(1)).updateComment(commentDTO, commentDTO.getId());
    }

    @Test
    void testDeleteComment_returnsDeletedCommentDTO() {
        CommentDTO deletedCommentDTO = Instancio.create(CommentDTO.class);
        when(commentService.deleteComment(deletedCommentDTO.getId())).thenReturn(deletedCommentDTO);

        ResponseEntity<CommentDTO> responseEntity = commentController.deleteComment(deletedCommentDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsCommentDTO(deletedCommentDTO, responseEntity.getBody());
        verify(commentService, times(1)).deleteComment(deletedCommentDTO.getId());
    }

    @Test
    void testGetCommentById_returnsCommentDTO() {
        CommentDTO commentDTO = Instancio.create(CommentDTO.class);
        when(commentService.getCommentById(commentDTO.getId())).thenReturn(commentDTO);

        ResponseEntity<CommentDTO> responseEntity = commentController.getCommentById(commentDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsCommentDTO(commentDTO, responseEntity.getBody());
        verify(commentService, times(1)).getCommentById(commentDTO.getId());
    }

    void assertEqualsCommentDTO(CommentDTO expectedCommentDTO, CommentDTO actualCommentDTO) {
        assertEquals(expectedCommentDTO.getId(), actualCommentDTO.getId());
        assertEquals(expectedCommentDTO.getPostId(), actualCommentDTO.getPostId());
        assertEquals(expectedCommentDTO.getUserId(), actualCommentDTO.getUserId());
        assertEquals(expectedCommentDTO.getContent(), actualCommentDTO.getContent());
    }
}
