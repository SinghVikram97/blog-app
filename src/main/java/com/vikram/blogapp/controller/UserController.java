package com.vikram.blogapp.controller;

import com.vikram.blogapp.constants.Constants;
import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.dto.UserDTO;
import com.vikram.blogapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.vikram.blogapp.constants.Constants.MDC_USERNAME_KEY;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // GET - User by ID
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<UserDTO> getUser(@PathVariable long userId){
        UserDTO user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // PUT - Update User
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid UserDTO userDTO, @PathVariable long userId){
        UserDTO updatedUser = userService.updateUser(userDTO, userId);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // DELETE - Delete User
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable long userId){
        UserDTO deletedUser = userService.deleteUser(userId);
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }

    // GET - Get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> allUsers = userService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    // GET - Get all posts by user
    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostDTO>> getAllPostsByUser(@PathVariable long userId){
        List<PostDTO> allPosts = userService.getAllPostsByUser(userId);
        return new ResponseEntity<>(allPosts, HttpStatus.OK);
    }

    // GET - Get all comments by user
    @GetMapping("/{userId}/comments")
    public ResponseEntity<List<CommentDTO>> getAllCommentsByUser(@PathVariable long userId) {
        List<CommentDTO> allCommentsByUser = userService.getAllCommentsByUser(userId);
        return new ResponseEntity<>(allCommentsByUser, HttpStatus.OK);
    }
}
