package com.vikram.blogapp.controller;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.dto.UserDTO;
import com.vikram.blogapp.service.UserService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUser_returnsUserDTO() {
        UserDTO userDTO = Instancio.create(UserDTO.class);
        when(userService.getUserById(userDTO.getId())).thenReturn(userDTO);

        ResponseEntity<UserDTO> responseEntity = userController.getUser(userDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsUserDTO(userDTO, responseEntity.getBody());
        verify(userService, times(1)).getUserById(userDTO.getId());
    }

    @Test
    void testUpdateUser_returnsUpdatedUserDTO() {
        UserDTO userDTO = Instancio.create(UserDTO.class);
        UserDTO updatedUserDTO = Instancio.create(UserDTO.class);
        when(userService.updateUser(userDTO, userDTO.getId())).thenReturn(updatedUserDTO);

        ResponseEntity<UserDTO> responseEntity = userController.updateUser(userDTO, userDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsUserDTO(updatedUserDTO, responseEntity.getBody());
        verify(userService, times(1)).updateUser(userDTO, userDTO.getId());
    }

    @Test
    void testDeleteUser_returnsDeletedUserDTO() {
        UserDTO deletedUserDTO = Instancio.create(UserDTO.class);
        when(userService.deleteUser(deletedUserDTO.getId())).thenReturn(deletedUserDTO);

        ResponseEntity<UserDTO> responseEntity = userController.deleteUser(deletedUserDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsUserDTO(deletedUserDTO, responseEntity.getBody());
        verify(userService, times(1)).deleteUser(deletedUserDTO.getId());
    }

    @Test
    void testGetAllUsers_returnsListOfUserDTOs() {
        List<UserDTO> allUsers = Instancio.ofList(UserDTO.class).size(5).create();
        when(userService.getAllUsers()).thenReturn(allUsers);

        ResponseEntity<List<UserDTO>> responseEntity = userController.getAllUsers();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(allUsers.size(), responseEntity.getBody().size());
        assertEquals(allUsers, responseEntity.getBody());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetAllPostsByUser_returnsListOfPostDTOs() {
        long userId = 123;
        List<PostDTO> allPosts = Instancio.ofList(PostDTO.class).size(5).create();
        when(userService.getAllPostsByUser(userId)).thenReturn(allPosts);

        ResponseEntity<List<PostDTO>> responseEntity = userController.getAllPostsByUser(userId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(allPosts.size(), responseEntity.getBody().size());
        assertEquals(allPosts, responseEntity.getBody());
        verify(userService, times(1)).getAllPostsByUser(userId);
    }

    @Test
    void testGetAllCommentsByUser_returnsListOfCommentDTOs() {
        long userId = 123;
        List<CommentDTO> allComments = Instancio.ofList(CommentDTO.class).size(5).create();
        when(userService.getAllCommentsByUser(userId)).thenReturn(allComments);

        ResponseEntity<List<CommentDTO>> responseEntity = userController.getAllCommentsByUser(userId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(allComments.size(), responseEntity.getBody().size());
        assertEquals(allComments, responseEntity.getBody());
        verify(userService, times(1)).getAllCommentsByUser(userId);
    }


    void assertEqualsUserDTO(UserDTO expectedUserDTO, UserDTO actualUserDTO) {
        assertEquals(expectedUserDTO.getId(), actualUserDTO.getId());
        assertEquals(expectedUserDTO.getFirstName(), actualUserDTO.getFirstName());
        assertEquals(expectedUserDTO.getLastName(), actualUserDTO.getLastName());
        assertEquals(expectedUserDTO.getEmail(), actualUserDTO.getEmail());
        assertEquals(expectedUserDTO.getPassword(), actualUserDTO.getPassword());
        assertEquals(expectedUserDTO.getAbout(), actualUserDTO.getAbout());
        assertEquals(expectedUserDTO.getRole(), actualUserDTO.getRole());
    }
}