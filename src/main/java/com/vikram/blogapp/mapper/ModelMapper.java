package com.vikram.blogapp.mapper;

import com.vikram.blogapp.dto.CategoryDTO;
import com.vikram.blogapp.dto.UserDTO;
import com.vikram.blogapp.entities.Category;
import com.vikram.blogapp.entities.User;
import org.springframework.stereotype.Service;

@Service
public class ModelMapper {
    public User dtoToUserDAO(UserDTO user) {
        return User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .about(user.getAbout())
                .build();
    }

    public UserDTO daoToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .about(user.getAbout())
                .build();
    }

    public Category dtoToCategoryDAO(CategoryDTO category) {
        return Category.builder()
                .id(category.getId())
                .categoryTitle(category.getCategoryTitle())
                .categoryDescription(category.getCategoryDescription())
                .build();
    }

    public CategoryDTO daoToCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .categoryTitle(category.getCategoryTitle())
                .categoryDescription(category.getCategoryDescription())
                .build();
    }
}


