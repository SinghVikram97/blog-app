package com.vikram.blogapp.mapper;

import com.vikram.blogapp.dto.CategoryDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.dto.UserDTO;
import com.vikram.blogapp.entities.Category;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.entities.User;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
public class ModelMapper {
    public User dtoToUserDAO(UserDTO user) {
        return User.builder()
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

    public Post dtoTOPostDAO(PostDTO post) {
        return Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public PostDTO daoTOPostDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageName(post.getImageName())
                .addedDate(post.getAddedDate())
                .userId(nonNull(post.getUser()) ? post.getUser().getId() : null )
                .categoryId(nonNull(post.getCategory()) ? post.getCategory().getId() : null)
                .build();
    }
}


