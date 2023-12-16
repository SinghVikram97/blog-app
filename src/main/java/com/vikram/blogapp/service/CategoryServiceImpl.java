package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CategoryDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.entities.Category;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category categoryDAO = modelMapper.dtoToCategoryDAO(categoryDTO);
        Category savedCategoryDAO = categoryRepository.save(categoryDAO);
        return modelMapper.daoToCategoryDTO(savedCategoryDAO);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId) {
        Category categoryDAO = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category ID", categoryId));

        categoryDAO.setCategoryTitle(categoryDTO.getCategoryTitle());
        categoryDAO.setCategoryDescription(categoryDTO.getCategoryDescription());

        Category savedCategoryDAO = categoryRepository.save(categoryDAO);

        return modelMapper.daoToCategoryDTO(savedCategoryDAO);
    }

    @Override
    public CategoryDTO deleteCategory(long categoryId) {
        Category categoryDAO = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category ID", categoryId));
        categoryRepository.deleteById(categoryDAO.getId());
        return modelMapper.daoToCategoryDTO(categoryDAO);
    }

    @Override
    public CategoryDTO getCategory(long categoryId) {
        Category categoryDAO = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category ID", categoryId));
        return modelMapper.daoToCategoryDTO(categoryDAO);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categoryDAOList = categoryRepository.findAll();
        return categoryDAOList.stream().map(modelMapper::daoToCategoryDTO).collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getAllPostsByCategory(long categoryId) {
        Category categoryDAO = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category ID", categoryId));
        List<Post> postsDAO = categoryDAO.getPosts();
        return postsDAO.stream().map(modelMapper::daoTOPostDTO).collect(Collectors.toList());
    }
}
