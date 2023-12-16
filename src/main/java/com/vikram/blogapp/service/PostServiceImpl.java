package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.entities.Category;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.entities.User;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.CategoryRepository;
import com.vikram.blogapp.repository.PostRepository;
import com.vikram.blogapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public PostDTO createPost(PostDTO postDTO) {
       // Get userId and categoryId
       long userId = postDTO.getUserId();
       long categoryId = postDTO.getCategoryId();

       // Find user and category
       User userDao = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));
       Category categoryDAO = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category ID", categoryId));

       // Convert post dto to post we expect only title and content in dto
        Post postDAO = modelMapper.dtoTOPostDAO(postDTO);

        // Add user and category to postDAO
        postDAO.setUser(userDao);
        postDAO.setCategory(categoryDAO);

        // Set date and image url
        postDAO.setAddedDate(new Date());
        postDAO.setImageName("default.png");

        // Save post in db
        Post savedPostDAO = postRepository.save(postDAO);

        // Add this to user and category
        userDao.addPost(savedPostDAO);
        categoryDAO.addPost(savedPostDAO);

        return modelMapper.daoTOPostDTO(savedPostDAO);
    }

    @Override
    public PostDTO updatePost(PostDTO postDTO, long postId) {
        return null;
    }

    @Override
    public PostDTO deletePost(long postId) {
        return null;
    }

    @Override
    public List<PostDTO> getAllPosts() {
        return null;
    }

    @Override
    public PostDTO getPostById(long postId) {
        return null;
    }

    @Override
    public List<PostDTO> searchPosts(String keyword) {
        return null;
    }
}
