package com.vikram.blogapp.mapper;

import com.vikram.blogapp.dto.CategoryDTO;
import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.dto.UserDTO;
import com.vikram.blogapp.entities.Category;
import com.vikram.blogapp.entities.Comment;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.entities.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModelMapperTest {
    private final ModelMapper modelMapper = new ModelMapper();

    @Test
    void testDtoToUserDAO_validUserDTO_returnsUser() {
        UserDTO userDTO = Instancio.create(UserDTO.class);

        User userDAO = modelMapper.dtoToUserDAO(userDTO);

        // Assert
        assertNotNull(userDAO);
        assertEquals(userDTO.getFirstName(), userDAO.getFirstName());
        assertEquals(userDTO.getLastName(), userDAO.getLastName());
        assertEquals(userDTO.getEmail(), userDAO.getEmail());
        assertEquals(userDTO.getPassword(), userDAO.getPassword());
        assertEquals(userDTO.getAbout(), userDAO.getAbout());
        assertEquals(userDTO.getRole(), userDAO.getRole());
    }

    @Test
    void testDaoToUserDTO_validUser_returnsUserDTO() {
        User userDAO = Instancio.create(User.class);

        UserDTO userDTO = modelMapper.daoToUserDTO(userDAO);

        assertNotNull(userDTO);
        assertEquals(userDAO.getId(), userDTO.getId());
        assertEquals(userDAO.getFirstName(), userDTO.getFirstName());
        assertEquals(userDAO.getLastName(), userDTO.getLastName());
        assertEquals(userDAO.getEmail(), userDTO.getEmail());
        assertEquals(userDAO.getAbout(), userDTO.getAbout());
        assertEquals(userDAO.getRole(), userDTO.getRole());
    }

    @Test
    void testDtoToCategoryDAO_validCategoryDTO_returnsCategory() {
        CategoryDTO categoryDTO = Instancio.create(CategoryDTO.class);

        Category categoryDAO = modelMapper.dtoToCategoryDAO(categoryDTO);

        assertNotNull(categoryDAO);
        assertEquals(categoryDTO.getCategoryTitle(), categoryDAO.getCategoryTitle());
        assertEquals(categoryDTO.getCategoryDescription(), categoryDAO.getCategoryDescription());
    }

    @Test
    void testDaoToCategoryDTO_validCategory_returnsCategoryDTO() {
        Category category = Instancio.create(Category.class);

        CategoryDTO categoryDTO = modelMapper.daoToCategoryDTO(category);

        assertNotNull(categoryDTO);
        assertEquals(category.getId(), categoryDTO.getId());
        assertEquals(category.getCategoryTitle(), categoryDTO.getCategoryTitle());
        assertEquals(category.getCategoryDescription(), categoryDTO.getCategoryDescription());
    }

    @Test
    void testDtoToPostDAO_validPostDTO_returnsPost() {
        PostDTO postDTO = Instancio.create(PostDTO.class);

        Post post = modelMapper.dtoTOPostDAO(postDTO);

        assertNotNull(post);
        assertEquals(postDTO.getTitle(), post.getTitle());
        assertEquals(postDTO.getContent(), post.getContent());
    }

    @Test
    void testDaoToPostDTO_validPost_returnsPostDTO() {
        Post post = Instancio.create(Post.class);

        PostDTO postDTO = modelMapper.daoTOPostDTO(post);

        assertNotNull(postDTO);
        assertEquals(post.getId(), postDTO.getId());
        assertEquals(post.getTitle(), postDTO.getTitle());
        assertEquals(post.getContent(), postDTO.getContent());
        assertEquals(post.getAddedDate(), postDTO.getAddedDate());
        assertEquals(post.getUser().getId(), postDTO.getUserId());
        assertEquals(post.getCategory().getId(), postDTO.getCategoryId());
    }

    @Test
    void testDtoToCommentDao_validCommentDTO_returnsComment() {
        CommentDTO commentDTO = Instancio.create(CommentDTO.class);

        Comment commentDao = modelMapper.dtoToCommentDao(commentDTO);

        assertNotNull(commentDao);
        assertEquals(commentDTO.getContent(), commentDao.getContent());
    }

    @Test
    void testDaoToCommentDTO_validComment_returnsCommentDTO() {
        Comment comment = Instancio.create(Comment.class);


        CommentDTO commentDTO = modelMapper.daoToCommentDTO(comment);

        assertNotNull(commentDTO);
        assertEquals(comment.getId(), commentDTO.getId());
        assertEquals(comment.getContent(), commentDTO.getContent());
        assertEquals(comment.getUser().getId(), commentDTO.getUserId());
        assertEquals(comment.getPost().getId(), commentDTO.getPostId());
    }
}
