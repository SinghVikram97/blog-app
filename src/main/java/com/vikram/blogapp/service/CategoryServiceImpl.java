package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CategoryDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.entities.Category;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.exception.UserNotAuthorizedException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.vikram.blogapp.constants.Constants.MDC_ROLE_KEY;
import static com.vikram.blogapp.util.AuthUtil.isAdmin;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if(!isAdmin(MDC.get(MDC_ROLE_KEY))){
            throw new UserNotAuthorizedException();
        }
        Category categoryDAO = modelMapper.dtoToCategoryDAO(categoryDTO);
        Category savedCategoryDAO = categoryRepository.save(categoryDAO);
        return modelMapper.daoToCategoryDTO(savedCategoryDAO);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId) {
        if(!isAdmin(MDC.get(MDC_ROLE_KEY))){
            throw new UserNotAuthorizedException();
        }
        Category categoryDAO = getCategoryDAOOrThrowException(categoryId);

        categoryDAO.setCategoryTitle(categoryDTO.getCategoryTitle());
        categoryDAO.setCategoryDescription(categoryDTO.getCategoryDescription());

        Category savedCategoryDAO = categoryRepository.save(categoryDAO);

        return modelMapper.daoToCategoryDTO(savedCategoryDAO);
    }

    @Override
    public CategoryDTO deleteCategory(long categoryId) {
        if(!isAdmin(MDC.get(MDC_ROLE_KEY))){
            throw new UserNotAuthorizedException();
        }
        Category categoryDAO = getCategoryDAOOrThrowException(categoryId);

        // Set category of all associated posts to null
        List<Post> posts = categoryDAO.getPosts();
        posts.forEach(post -> post.setCategory(null));

        // Now remove the category
        categoryRepository.deleteById(categoryDAO.getId());
        return modelMapper.daoToCategoryDTO(categoryDAO);
    }

    @Override
    public CategoryDTO getCategory(long categoryId) {
        if(!isAdmin(MDC.get(MDC_ROLE_KEY))){
            throw new UserNotAuthorizedException();
        }
        Category categoryDAO = getCategoryDAOOrThrowException(categoryId);
        return modelMapper.daoToCategoryDTO(categoryDAO);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        if(!isAdmin(MDC.get(MDC_ROLE_KEY))){
            throw new UserNotAuthorizedException();
        }
        List<Category> categoryDAOList = categoryRepository.findAll();
        return categoryDAOList.stream().map(modelMapper::daoToCategoryDTO).collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getAllPostsByCategory(long categoryId) {
        if(!isAdmin(MDC.get(MDC_ROLE_KEY))){
            throw new UserNotAuthorizedException();
        }
        Category categoryDAO = getCategoryDAOOrThrowException(categoryId);
        List<Post> postsDAO = categoryDAO.getPosts();
        return postsDAO.stream().map(modelMapper::daoTOPostDTO).collect(Collectors.toList());
    }

    private Category getCategoryDAOOrThrowException(long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category ID", categoryId));
    }
}
