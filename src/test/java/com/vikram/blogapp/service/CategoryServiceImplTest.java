package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CategoryDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.entities.Category;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.entities.Role;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.exception.UserNotAuthorizedException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.CategoryRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import java.util.List;

import static com.vikram.blogapp.constants.Constants.MDC_ROLE_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCategory_whenAdmin_returnsCreatedCategoryDTO() {
        CategoryDTO categoryDTO = generateRandomCategoryDTO();
        Category categoryDAO = generateRandomCategory();
        when(modelMapper.dtoToCategoryDAO(categoryDTO)).thenReturn(categoryDAO);
        when(categoryRepository.save(categoryDAO)).thenReturn(categoryDAO);
        when(modelMapper.daoToCategoryDTO(categoryDAO)).thenReturn(categoryDTO);
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.toString());

        CategoryDTO createdCategoryDTO= categoryService.createCategory(categoryDTO);

        assertNotNull(createdCategoryDTO);
        verify(categoryRepository, times(1)).save(categoryDAO);
        assertEqualsCategoryDTO(categoryDTO, createdCategoryDTO);
    }

    @Test
    void testCreateCategory_whenNotAdmin_throwsUserNotAuthorizedException() {
        when(categoryRepository.save(any())).thenThrow(UserNotAuthorizedException.class);

        assertThrows(UserNotAuthorizedException.class, () -> {
            categoryService.createCategory(generateRandomCategoryDTO());
        });

    }

    @Test
    void testUpdateCategory_whenAdmin_returnsUpdatedCategoryDTO() {
        long categoryId = 1L;
        CategoryDTO categoryDTO = generateRandomCategoryDTO();
        categoryDTO.setId(categoryId);
        Category categoryDAO = generateRandomCategory();
        categoryDAO.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(categoryDAO));
        when(categoryRepository.save(categoryDAO)).thenReturn(categoryDAO);
        when(modelMapper.daoToCategoryDTO(categoryDAO)).thenReturn(categoryDTO);
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.toString());

        // Act
        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);

        // Assert
        assertNotNull(updatedCategoryDTO);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(categoryDAO);
        assertEqualsCategoryDTO(categoryDTO, updatedCategoryDTO);
    }

    @Test
    void testUpdateCategory_whenNotAdmin_throwsUserNotAuthorizedException() {
        long categoryId = 1L;
        CategoryDTO categoryDTO = generateRandomCategoryDTO();
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.toString());

        // Act and Assert
        assertThrows(UserNotAuthorizedException.class, () -> {
            categoryService.updateCategory(categoryDTO, categoryId);
        });
    }

    @Test
    void testUpdateCategory_whenCategoryNotFound_throwsResourceNotFoundException() {
        // Arrange
        long categoryId = 1L;
        CategoryDTO categoryDTO = generateRandomCategoryDTO();
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.empty());
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.toString());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.updateCategory(categoryDTO, categoryId);
        });
    }

    @Test
    void testDeleteCategory_whenAdmin_returnsDeletedCategoryDTO() {
        long categoryId = 1L;
        Category categoryDAO = generateRandomCategory();
        categoryDAO.setId(categoryId);

        CategoryDTO categoryDTO = generateRandomCategoryDTO();
        categoryDTO.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(categoryDAO));
        when(modelMapper.daoToCategoryDTO(categoryDAO)).thenReturn(categoryDTO);
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.toString());

        CategoryDTO deletedCategoryDTO = categoryService.deleteCategory(categoryId);

        assertNotNull(deletedCategoryDTO);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
        assertEqualsCategoryDTO(categoryDTO, deletedCategoryDTO);
    }

    @Test
    void testDeleteCategory_whenNotAdmin_throwsUserNotAuthorizedException() {
        long categoryId = 1L;
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.toString());

        assertThrows(UserNotAuthorizedException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });
    }

    @Test
    void testDeleteCategory_whenCategoryNotFound_throwsResourceNotFoundException() {
        long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.empty());
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.toString());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });
    }

    @Test
    void testGetCategory_whenAdmin_returnsCategoryDTO() {
        long categoryId = 1L;
        Category categoryDAO = generateRandomCategory();
        categoryDAO.setId(categoryId);

        CategoryDTO categoryDTO = generateRandomCategoryDTO();
        categoryDTO.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(categoryDAO));
        when(modelMapper.daoToCategoryDTO(categoryDAO)).thenReturn(categoryDTO);
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.toString());

        CategoryDTO getCategoryDTO = categoryService.getCategory(categoryId);

        // Assert
        assertNotNull(getCategoryDTO);
        verify(categoryRepository, times(1)).findById(categoryId);
        assertEqualsCategoryDTO(categoryDTO, getCategoryDTO);
    }

    @Test
    void testGetCategory_whenNotAdmin_throwsUserNotAuthorizedException() {
        long categoryId = 1L;
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.toString());

        assertThrows(UserNotAuthorizedException.class, () -> {
            categoryService.getCategory(categoryId);
        });
    }

    @Test
    void testGetCategory_whenCategoryNotFound_throwsResourceNotFoundException() {
        long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.empty());
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.toString());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategory(categoryId);
        });
    }

    @Test
    void testGetAllCategories_whenAdmin_returnsListOfCategoryDTOs() {
        // Arrange
        List<Category> categoryDAOList = Instancio.ofList(Category.class).size(5).create();

        when(categoryRepository.findAll()).thenReturn(categoryDAOList);
        when(modelMapper.daoToCategoryDTO(any())).thenReturn(generateRandomCategoryDTO());
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.toString());

        List<CategoryDTO> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(categoryDAOList.size(), result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCategories_whenNotAdmin_throwsUserNotAuthorizedException() {
        // Arrange
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.toString());

        // Act and Assert
        assertThrows(UserNotAuthorizedException.class, () -> {
            categoryService.getAllCategories();
        });
    }

    @Test
    void testGetAllPostsByCategory_whenAdmin_returnsListOfPostDTOs() {
        long categoryId = 1L;
        Category categoryDAO = generateRandomCategory();
        List<Post> postsDAO = Instancio.ofList(Post.class).size(5).create();
        categoryDAO.setPosts(postsDAO);

        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(categoryDAO));
        when(modelMapper.daoTOPostDTO(any())).thenReturn(generateRandomPostDTO());
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.toString());

        List<PostDTO> postDTOList = categoryService.getAllPostsByCategory(categoryId);

        assertNotNull(postDTOList);
        assertEquals(postsDAO.size(), postDTOList.size());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetAllPostsByCategory_whenNotAdmin_throwsUserNotAuthorizedException() {
        long categoryId = 1L;
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.toString());

        assertThrows(UserNotAuthorizedException.class, () -> {
            categoryService.getAllPostsByCategory(categoryId);
        });
    }


    private PostDTO generateRandomPostDTO() {
        return Instancio.create(PostDTO.class);
    }


    public static CategoryDTO generateRandomCategoryDTO() {
        return Instancio.create(CategoryDTO.class);
    }

    public static Category generateRandomCategory() {
        return Instancio.create(Category.class);
    }

    public void assertEqualsCategoryDTO(CategoryDTO expected, CategoryDTO actual){
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCategoryTitle(), actual.getCategoryTitle());
        assertEquals(expected.getCategoryDescription(), actual.getCategoryDescription());
    }
}