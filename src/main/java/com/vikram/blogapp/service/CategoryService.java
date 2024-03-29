package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CategoryDTO;
import com.vikram.blogapp.dto.PostDTO;

import java.util.List;

public interface CategoryService {
    // Create category
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    // Update category
    CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId);

    // Delete category
    CategoryDTO deleteCategory(long categoryId);

    // Get category
    CategoryDTO getCategory(long categoryId);

    // Get all categories
    List<CategoryDTO> getAllCategories();

    // Get all posts in a category
    List<PostDTO> getAllPostsByCategory(long categoryId);
}
