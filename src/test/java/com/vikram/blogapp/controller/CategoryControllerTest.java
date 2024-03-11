package com.vikram.blogapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.vikram.blogapp.dto.CategoryDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.service.CategoryService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

class CategoryControllerTest {
    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCategory_returnsCategoryDTO() {
        CategoryDTO categoryDTO = Instancio.create(CategoryDTO.class);
        when(categoryService.createCategory(categoryDTO)).thenReturn(categoryDTO);

        ResponseEntity<CategoryDTO> responseEntity = categoryController.createCategory(categoryDTO);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsCategoryDTO(categoryDTO, responseEntity.getBody());
        verify(categoryService, times(1)).createCategory(categoryDTO);
    }

    @Test
    void testUpdateCategory_returnsUpdatedCategoryDTO() {
        CategoryDTO categoryDTO = Instancio.create(CategoryDTO.class);
        when(categoryService.updateCategory(categoryDTO, categoryDTO.getId())).thenReturn(categoryDTO);

        ResponseEntity<CategoryDTO> responseEntity = categoryController.updateCategory(categoryDTO, categoryDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsCategoryDTO(categoryDTO, responseEntity.getBody());
        verify(categoryService, times(1)).updateCategory(categoryDTO, categoryDTO.getId());
    }

    @Test
    void testDeleteCategory_returnsDeletedCategoryDTO() {
        CategoryDTO deletedCategoryDTO = Instancio.create(CategoryDTO.class);
        when(categoryService.deleteCategory(deletedCategoryDTO.getId())).thenReturn(deletedCategoryDTO);

        ResponseEntity<CategoryDTO> responseEntity = categoryController.deleteCategory(deletedCategoryDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsCategoryDTO(deletedCategoryDTO, responseEntity.getBody());
        verify(categoryService, times(1)).deleteCategory(deletedCategoryDTO.getId());
    }

    @Test
    void testGetCategory_returnsCategoryDTO() {
        CategoryDTO categoryDTO = Instancio.create(CategoryDTO.class);
        when(categoryService.getCategory(categoryDTO.getId())).thenReturn(categoryDTO);

        ResponseEntity<CategoryDTO> responseEntity = categoryController.getCategory(categoryDTO.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEqualsCategoryDTO(categoryDTO, responseEntity.getBody());
        verify(categoryService, times(1)).getCategory(categoryDTO.getId());
    }

    @Test
    void testGetAllCategories_returnsListOfCategoryDTO() {
        List<CategoryDTO> categoryDTOList = Instancio.ofList(CategoryDTO.class).size(5).create();
        when(categoryService.getAllCategories()).thenReturn(categoryDTOList);

        ResponseEntity<List<CategoryDTO>> responseEntity = categoryController.getAllCategories();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(categoryDTOList, responseEntity.getBody());
        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void testGetAllPostsByCategory_returnsListOfPostDTO() {
        long categoryId = 1L;
        List<PostDTO> postDTOList = Instancio.ofList(PostDTO.class).size(5).create();
        when(categoryService.getAllPostsByCategory(categoryId)).thenReturn(postDTOList);

        ResponseEntity<List<PostDTO>> responseEntity = categoryController.getAllPostsByCategory(categoryId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(postDTOList, responseEntity.getBody());
        verify(categoryService, times(1)).getAllPostsByCategory(categoryId);
    }

    void assertEqualsCategoryDTO(CategoryDTO expectedCategoryDTO, CategoryDTO actualCategoryDTO) {
        assertEquals(expectedCategoryDTO.getId(), actualCategoryDTO.getId());
        assertEquals(expectedCategoryDTO.getCategoryTitle(), actualCategoryDTO.getCategoryTitle());
        assertEquals(expectedCategoryDTO.getCategoryDescription(), actualCategoryDTO.getCategoryDescription());
    }
}
