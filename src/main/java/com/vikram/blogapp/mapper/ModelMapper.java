package com.vikram.blogapp.mapper;

import com.vikram.blogapp.dto.CategoryDTO;
import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.dto.UserDTO;
import com.vikram.blogapp.entities.Category;
import com.vikram.blogapp.entities.Comment;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.entities.User;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
public class ModelMapper {
    public User dtoToUserDAO(UserDTO user) {
        return User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .about(user.getAbout())
                .role(user.getRole())
                .build();
    }

    public UserDTO daoToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .about(user.getAbout())
                .role(user.getRole())
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
                .addedDate(post.getAddedDate())
                .userId(nonNull(post.getUser()) ? post.getUser().getId() : null)
                .categoryId(nonNull(post.getCategory()) ? post.getCategory().getId() : null)
                .build();
    }

    public Comment dtoToCommentDao(CommentDTO commentDTO) {
        return Comment.builder()
                .content(commentDTO.getContent()) // take out only content from dto to dao
                // user and post we will add later
                .build();
    }

    public CommentDTO daoToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(nonNull(comment.getUser()) ? comment.getUser().getId() : null)
                .postId(nonNull(comment.getPost()) ? comment.getPost().getId() : null)
                .build();
    }
}


