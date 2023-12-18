package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO, long userId);
    UserDTO getUserById(long userId);
    List<UserDTO> getAllUsers();
    UserDTO deleteUser(long userId);

    // Get all posts by a user
    List<PostDTO> getAllPostsByUser(long userId);

    // Get all comments by a user
    List<CommentDTO> getAllCommentsByUser(long userId);
}
