package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PaginationResponseDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.entities.*;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.exception.UserNotAuthorizedException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.CategoryRepository;
import com.vikram.blogapp.repository.PostRepository;
import com.vikram.blogapp.repository.UserRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static com.vikram.blogapp.constants.Constants.MDC_ROLE_KEY;
import static com.vikram.blogapp.constants.Constants.MDC_USERNAME_KEY;

class PostServiceImplTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPostById_whenSameUser_returnsPostDTO() {
        Post post = generateRandomPost();
        PostDTO postDTO = generateRandomPostDTO();
        User user = generateRandomUser();

        post.setUser(user);

        Mockito.when(postRepository.findById(postDTO.getId())).thenReturn(java.util.Optional.of(post));
        Mockito.when(modelMapper.daoTOPostDTO(post)).thenReturn(postDTO);
        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        PostDTO actualPostDTO = postService.getPostById(postDTO.getId());

        // Assert
        Assertions.assertNotNull(postDTO);
        Mockito.verify(postRepository, Mockito.times(1)).findById(postDTO.getId());
        assertPostDTOEquals(postDTO, actualPostDTO);
    }

    @Test
    void testGetPostById_whenAdmin_returnsPostDTO() {
        Post post = generateRandomPost();
        PostDTO postDTO = generateRandomPostDTO();

        Mockito.when(postRepository.findById(postDTO.getId())).thenReturn(java.util.Optional.of(post));
        Mockito.when(modelMapper.daoTOPostDTO(post)).thenReturn(postDTO);
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());

        PostDTO actualPostDTO = postService.getPostById(postDTO.getId());

        // Assert
        Assertions.assertNotNull(postDTO);
        Mockito.verify(postRepository, Mockito.times(1)).findById(postDTO.getId());
        assertPostDTOEquals(postDTO, actualPostDTO);
    }

    @Test
    void testGetPostById_whenDifferentUserAndNotAdmin_ThrowsUserNotAuthorizedException() {
        Post post = generateRandomPost();

        Mockito.when(postRepository.findById(post.getId())).thenReturn(java.util.Optional.of(post));
        MDC.put(MDC_USERNAME_KEY, "randomEmail.com");
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.name());

        Assertions.assertThrows(UserNotAuthorizedException.class, () -> {
            postService.getPostById(post.getId());
        });
    }

    @Test
    void testGetPostById_whenPostNotFound_throwsResourceNotFoundException() {
        long postId = 1L;

        Mockito.when(postRepository.findById(postId)).thenReturn(java.util.Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            postService.getPostById(postId);
        });
    }

    @Test
    void testCreatePost_whenSameUser_returnsCreatedPostDTO() {
        PostDTO postDTO = generateRandomPostDTO();
        Post post = generateRandomPost();
        User user = generateRandomUser();
        Category category = generateRandomCategory();

        Mockito.when(userRepository.findById(postDTO.getUserId())).thenReturn(java.util.Optional.of(user));
        Mockito.when(categoryRepository.findById(postDTO.getCategoryId())).thenReturn(Optional.of(category));
        Mockito.when(modelMapper.dtoTOPostDAO(postDTO)).thenReturn(post);
        Mockito.when(postRepository.save(Mockito.any())).thenReturn(post);
        Mockito.when(modelMapper.daoTOPostDTO(post)).thenReturn(postDTO);
        MDC.put(MDC_USERNAME_KEY, user.getEmail());
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.name());

        PostDTO createdPostDTO = postService.createPost(postDTO);

        Assertions.assertNotNull(createdPostDTO);
        Mockito.verify(userRepository, Mockito.times(1)).findById(postDTO.getUserId());
        Mockito.verify(postRepository, Mockito.times(1)).save(Mockito.any());
        assertPostDTOEquals(postDTO, createdPostDTO);
    }

    @Test
    void testCreatePost_whenDifferentUser_throwsUserNotAuthorizedException() {
        PostDTO postDTO = generateRandomPostDTO();
        User user = generateRandomUser();

        Mockito.when(userRepository.findById(postDTO.getUserId())).thenReturn(java.util.Optional.of(user));
        MDC.put(MDC_USERNAME_KEY, "different_user@example.com");

        Assertions.assertThrows(UserNotAuthorizedException.class, () -> {
            postService.createPost(postDTO);
        });
        Mockito.verify(postRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testCreatePost_whenUserNotFound_throwsResourceNotFoundException() {
        PostDTO postDTO = generateRandomPostDTO();

        Mockito.when(userRepository.findById(postDTO.getUserId())).thenReturn(java.util.Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            postService.createPost(postDTO);
        });
        Mockito.verify(postRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testUpdatePost_whenSameUser_returnsUpdatedPostDTO() {
        PostDTO postDTO = generateRandomPostDTO();
        Post post = generateRandomPost();
        User user = generateRandomUser();
        Category category = generateRandomCategory();
        post.setUser(user);

        // Mock repository methods
        Mockito.when(postRepository.findById(postDTO.getId())).thenReturn(Optional.of(post));
        Mockito.when(userRepository.findById(postDTO.getUserId())).thenReturn(Optional.of(user));
        Mockito.when(categoryRepository.findById(postDTO.getCategoryId())).thenReturn(Optional.of(category));
        Mockito.when(modelMapper.dtoTOPostDAO(postDTO)).thenReturn(post);
        Mockito.when(postRepository.save(Mockito.any())).thenReturn(post);
        Mockito.when(modelMapper.daoTOPostDTO(post)).thenReturn(postDTO);
        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        PostDTO updatedPostDTO = postService.updatePost(postDTO, postDTO.getId());

        // Assert
        Assertions.assertNotNull(updatedPostDTO);
        Mockito.verify(postRepository, Mockito.times(1)).findById(postDTO.getId());
        Mockito.verify(postRepository, Mockito.times(1)).save(Mockito.any());
        assertPostDTOEquals(postDTO, updatedPostDTO);
    }

    @Test
    void testUpdatePost_whenDifferentUser_throwsUserNotAuthorizedException() {
        // Arrange
        PostDTO postDTO = generateRandomPostDTO();
        User user = generateRandomUser();
        Post post = generateRandomPost();
        post.setUser(user);

        Mockito.when(postRepository.findById(postDTO.getId())).thenReturn(java.util.Optional.of(post));
        MDC.put(MDC_USERNAME_KEY, "different_user@example.com");

        Assertions.assertThrows(UserNotAuthorizedException.class, () -> {
            postService.updatePost(postDTO, postDTO.getId());
        });
        Mockito.verify(postRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testUpdatePost_whenUserNotFound_throwsResourceNotFoundException() {
        PostDTO postDTO = generateRandomPostDTO();
        Post post = generateRandomPost();
        User user = generateRandomUser();
        post.setUser(user);

        Mockito.when(postRepository.findById(postDTO.getId())).thenReturn(java.util.Optional.of(post));
        Mockito.when(userRepository.findById(postDTO.getUserId())).thenReturn(java.util.Optional.empty());
        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost(postDTO, postDTO.getId());
        });
        Mockito.verify(postRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testUpdatePost_whenPostNotFound_throwsResourceNotFoundException() {
        PostDTO postDTO = generateRandomPostDTO();

        Mockito.when(postRepository.findById(postDTO.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost(postDTO, postDTO.getId());
        });
        Mockito.verify(postRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testDeletePost_whenSameUser_returnsDeletedPostDTO() {
        Post post = generateRandomPost();
        PostDTO postDTO = generateRandomPostDTO();
        User user = generateRandomUser();
        post.setUser(user);

        Mockito.when(postRepository.findById(postDTO.getId())).thenReturn(java.util.Optional.of(post));
        Mockito.when(modelMapper.daoTOPostDTO(post)).thenReturn(postDTO);
        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        PostDTO deletedPostDTO = postService.deletePost(postDTO.getId());

        // Assert
        Assertions.assertNotNull(deletedPostDTO);
        Mockito.verify(postRepository, Mockito.times(1)).delete(post);
        assertPostDTOEquals(postDTO, deletedPostDTO);
    }

    @Test
    void testDeletePost_whenAdmin_returnsDeletedPostDTO() {
        Post post = generateRandomPost();
        PostDTO postDTO = generateRandomPostDTO();

        Mockito.when(postRepository.findById(postDTO.getId())).thenReturn(java.util.Optional.of(post));
        Mockito.when(modelMapper.daoTOPostDTO(post)).thenReturn(postDTO);
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());

        PostDTO deletedPostDTO = postService.deletePost(postDTO.getId());

        // Assert
        Assertions.assertNotNull(deletedPostDTO);
        Mockito.verify(postRepository, Mockito.times(1)).delete(post);
        assertPostDTOEquals(postDTO, deletedPostDTO);
    }

    @Test
    void testDeletePost_whenDifferentUserAndNotAdmin_throwsUserNotAuthorizedException() {
        Post post = generateRandomPost();

        Mockito.when(postRepository.findById(post.getId())).thenReturn(java.util.Optional.of(post));
        MDC.put(MDC_USERNAME_KEY, "different_user@example.com");
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.name());

        Assertions.assertThrows(UserNotAuthorizedException.class, () -> {
            postService.deletePost(post.getId());
        });
        Mockito.verify(postRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void testDeletePost_whenPostNotFound_throwsResourceNotFoundException() {
        long postId = 1L;

        Mockito.when(postRepository.findById(postId)).thenReturn(java.util.Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            postService.deletePost(postId);
        });
        Mockito.verify(postRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void testGetAllPosts_admin_returnsAllPosts() {
        List<Post> posts = Instancio.ofList(Post.class).size(5).create();
        List<PostDTO> postDTOs = Instancio.ofList(PostDTO.class).size(5).create();

        int pageNumber = 1;
        int pageSize = 5;
        Sort asc = Sort.by("asc");
        String sortBy = "title";

        Pageable pageable = PageRequest.of(pageNumber, pageSize, asc);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        Mockito.when(postRepository.findAll(Mockito.any(Pageable.class))).thenReturn(postPage);
        Mockito.when(modelMapper.daoTOPostDTO(posts.get(0))).thenReturn(postDTOs.get(0));
        Mockito.when(modelMapper.daoTOPostDTO(posts.get(1))).thenReturn(postDTOs.get(1));
        Mockito.when(modelMapper.daoTOPostDTO(posts.get(2))).thenReturn(postDTOs.get(2));
        Mockito.when(modelMapper.daoTOPostDTO(posts.get(3))).thenReturn(postDTOs.get(3));
        Mockito.when(modelMapper.daoTOPostDTO(posts.get(4))).thenReturn(postDTOs.get(4));

        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());

        PaginationResponseDTO paginationResponseDTO = postService.getAllPosts(pageNumber, pageSize, sortBy, "asc");

        Assertions.assertNotNull(paginationResponseDTO);
        Assertions.assertEquals(pageSize, paginationResponseDTO.getTotalElements());
        Assertions.assertEquals(pageNumber, paginationResponseDTO.getPageNumber());
        Assertions.assertEquals(pageSize, paginationResponseDTO.getPageSize());
        Assertions.assertEquals(pageSize, paginationResponseDTO.getContent().size());
    }

    @Test
    void testGetAllPosts_notAdmin_throwsUserNotAuthorizedException() {
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.name());

        int pageNumber = 1;
        int pageSize = 5;
        Sort asc = Sort.by("asc");
        String sortBy = "title";

        Assertions.assertThrows(UserNotAuthorizedException.class, () -> {
            postService.getAllPosts(pageNumber, pageSize, sortBy, "asc");
        });
    }

    @Test
    void testSearchPosts_admin_returnsPostDTOsWithKeyword() {
        String keyword = "test";
        List<Post> posts = Instancio.ofList(Post.class).size(5).create();
        List<PostDTO> postDTOs = Instancio.ofList(PostDTO.class).size(5).create();

        Mockito.when(postRepository.findByTitleContaining(keyword)).thenReturn(posts);
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());

        List<PostDTO> foundPosts = postService.searchPosts(keyword);

        Assertions.assertNotNull(foundPosts);
        Assertions.assertEquals(postDTOs.size(), foundPosts.size());
    }

    @Test
    void testSearchPosts_notAdmin_throwsUserNotAuthorizedException() {
        String keyword = "test";
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.name());

        Assertions.assertThrows(UserNotAuthorizedException.class, () -> {
            postService.searchPosts(keyword);
        });
    }

    @Test
    void testGetAllComments_sameUser_returnsCommentDTOs() {
        long postId = 1L;
        Post post = generateRandomPost();
        List<Comment> comments = Instancio.ofList(Comment.class).size(5).create();
        List<CommentDTO> commentDTOs = Instancio.ofList(CommentDTO.class).size(5).create();
        User user = generateRandomUser();

        post.setComments(comments);
        post.setUser(user);

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        MDC.put(MDC_USERNAME_KEY, user.getEmail());
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.name());

        List<CommentDTO> foundComments = postService.getAllComments(postId);

        Assertions.assertNotNull(foundComments);
        Assertions.assertEquals(commentDTOs.size(), foundComments.size());
    }


    @Test
    void testGetAllComments_admin_returnsCommentDTOs() {
        long postId = 1L;
        Post post = generateRandomPost();
        List<Comment> comments = Instancio.ofList(Comment.class).size(5).create();
        List<CommentDTO> commentDTOs = Instancio.ofList(CommentDTO.class).size(5).create();

        post.setComments(comments);

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());

        List<CommentDTO> foundComments = postService.getAllComments(postId);

        Assertions.assertNotNull(foundComments);
        Assertions.assertEquals(commentDTOs.size(), foundComments.size());
    }


    @Test
    void testGetAllComments_differentUserAndNotAdmin_throwsUserNotAuthorizedException() {
        Post post = generateRandomPost();
        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));


        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.name());
        MDC.put(MDC_USERNAME_KEY, "random-email.com");

        Assertions.assertThrows(UserNotAuthorizedException.class, () -> {
            postService.getAllComments(post.getId());
        });
    }

    @Test
    void testGetAllComments_PostNotFound_ThrowsResourceNotFoundException() {
        long postId = 1L;
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            postService.getAllComments(postId);
        });
    }



    private void assertPostDTOEquals(PostDTO expectedPostDTO, PostDTO actualPostDTO) {
        Assertions.assertEquals(expectedPostDTO.getId(), actualPostDTO.getId());
        Assertions.assertEquals(expectedPostDTO.getUserId(), actualPostDTO.getUserId());
        Assertions.assertEquals(expectedPostDTO.getCategoryId(), actualPostDTO.getCategoryId());
        Assertions.assertEquals(expectedPostDTO.getTitle(), actualPostDTO.getTitle());
        Assertions.assertEquals(expectedPostDTO.getContent(), actualPostDTO.getContent());
        Assertions.assertEquals(expectedPostDTO.getAddedDate(), actualPostDTO.getAddedDate());
    }

    private Post generateRandomPost() {
        return Instancio.create(Post.class);
    }

    private User generateRandomUser() {
        return Instancio.create(User.class);
    }

    private Category generateRandomCategory() {
        return Instancio.create(Category.class);
    }

    private PostDTO generateRandomPostDTO() {
        return Instancio.create(PostDTO.class);
    }

}