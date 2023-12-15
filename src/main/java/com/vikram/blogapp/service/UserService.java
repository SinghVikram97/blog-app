package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.UserDTO;
import com.vikram.blogapp.entities.User;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO, long userId);
    UserDTO getUserById(long userId);
    List<UserDTO> getAllUsers();
    UserDTO deleteUser(long userId);
}
