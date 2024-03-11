package com.vikram.blogapp.controller;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PaginationResponseDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.service.PostService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostControllerTest {
    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePost_returnsCreatedPost() {
        PostDTO postDTO = Instancio.create(PostDTO.class);
        when(postService.createPost(postDTO)).thenReturn(postDTO);

        ResponseEntity<PostDTO> responseEntity = postController.createPost(postDTO);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertPostDTOEqual(postDTO, responseEntity.getBody());
        verify(postService, times(1)).createPost(postDTO);
    }

    @Test
    void testUpdatePost_returnsUpdatedPost() {
        PostDTO postDTO = Instancio.create(PostDTO.class);
        when(postService.updatePost(postDTO, postDTO.getId())).thenReturn(postDTO);

        ResponseEntity<PostDTO> responseEntity = postController.updatePost(postDTO, postDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertPostDTOEqual(postDTO, responseEntity.getBody());
        verify(postService, times(1)).updatePost(postDTO, postDTO.getId());
    }

    @Test
    void testDeletePost_returnsDeletedPost() {
        PostDTO deletedPostDTO = Instancio.create(PostDTO.class);
        when(postService.deletePost(deletedPostDTO.getId())).thenReturn(deletedPostDTO);

        ResponseEntity<PostDTO> responseEntity = postController.deletePost(deletedPostDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertPostDTOEqual(deletedPostDTO, responseEntity.getBody());
        verify(postService, times(1)).deletePost(deletedPostDTO.getId());
    }

    @Test
    void testGetPost_returnsPostDTO() {
        PostDTO postDTO = Instancio.create(PostDTO.class);
        when(postService.getPostById(postDTO.getId())).thenReturn(postDTO);

        ResponseEntity<PostDTO> responseEntity = postController.getPost(postDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertPostDTOEqual(postDTO, responseEntity.getBody());
        verify(postService, times(1)).getPostById(postDTO.getId());
    }

    @Test
    void testGetAllPosts_returnsPaginationResponseDTO() {
        PaginationResponseDTO paginationResponseDTO = Instancio.create(PaginationResponseDTO.class);
        when(postService.getAllPosts(anyInt(), anyInt(), anyString(), anyString())).thenReturn(paginationResponseDTO);

        ResponseEntity<PaginationResponseDTO> responseEntity = postController.getAllPosts(1, 10, "title", "asc");

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(paginationResponseDTO, responseEntity.getBody());
        verify(postService, times(1)).getAllPosts(anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    void testSearchPostByTitle_returnsListOfPostDTOs() {
        String keyword = "keyword";
        List<PostDTO> result = Instancio.ofList(PostDTO.class).size(5).create();
        when(postService.searchPosts(keyword)).thenReturn(result);

        ResponseEntity<List<PostDTO>> responseEntity = postController.searchPostByTitle(keyword);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(result.size(), responseEntity.getBody().size());
        assertEquals(result, responseEntity.getBody());
        verify(postService, times(1)).searchPosts(keyword);
    }

    @Test
    void testGetAllComments_returnsListOfCommentDTOs() {
        long postId = 123;
        List<CommentDTO> allComments = Instancio.ofList(CommentDTO.class).size(5).create();
        when(postService.getAllComments(postId)).thenReturn(allComments);

        ResponseEntity<List<CommentDTO>> responseEntity = postController.getAllComments(postId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(allComments.size(), responseEntity.getBody().size());
        assertEquals(allComments, responseEntity.getBody());
        verify(postService, times(1)).getAllComments(postId);
    }

    void assertPostDTOEqual(PostDTO expectedPostDTO, PostDTO actualPostDTO) {
        assertEquals(expectedPostDTO.getId(), actualPostDTO.getId());
        assertEquals(expectedPostDTO.getUserId(), actualPostDTO.getUserId());
        assertEquals(expectedPostDTO.getCategoryId(), actualPostDTO.getCategoryId());
        assertEquals(expectedPostDTO.getContent(), actualPostDTO.getContent());
        assertEquals(expectedPostDTO.getTitle(), actualPostDTO.getTitle());
        assertEquals(expectedPostDTO.getAddedDate(), actualPostDTO.getAddedDate());
    }
}