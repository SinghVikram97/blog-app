package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PaginationResponseDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.entities.Category;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.entities.User;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.exception.UserNotAuthorizedException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.CategoryRepository;
import com.vikram.blogapp.repository.PostRepository;
import com.vikram.blogapp.repository.UserRepository;
import com.vikram.blogapp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.vikram.blogapp.constants.Constants.MDC_ROLE_KEY;
import static com.vikram.blogapp.constants.Constants.MDC_USERNAME_KEY;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public PostDTO getPostById(long postId) {
        Post postDao = getPostDAOOrThrowException(postId);

        if(!AuthUtil.isSameUserOrAdmin(MDC.get(MDC_USERNAME_KEY), MDC.get(MDC_ROLE_KEY), postDao.getUser().getEmail())) {
            throw new UserNotAuthorizedException();
        }

        return modelMapper.daoTOPostDTO(postDao);
    }

    @Override
    public PostDTO createPost(PostDTO postDTO) {
        // Get userId
        long userId = postDTO.getUserId();

        // Find user
        User userDao = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));

        // Check if same user as present in JWT
        if(!AuthUtil.isSameUser(MDC.get(MDC_USERNAME_KEY),userDao.getEmail())) {
            throw new UserNotAuthorizedException();
        }

        // Get categoryId - it can be null
        Category categoryDAO = null;
        if(nonNull(postDTO.getCategoryId())){
            long categoryId = postDTO.getCategoryId();
            // Find category
            categoryDAO = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category ID", categoryId));
        }


       // Convert post dto to post, take out only title and content from dto
        Post postDAO = modelMapper.dtoTOPostDAO(postDTO);

        // Add user and category to postDAO
        postDAO.setUser(userDao);
        postDAO.setCategory(categoryDAO);

        // Set date
        postDAO.setAddedDate(new Date());

        // Save post in db
        Post savedPostDAO = postRepository.save(postDAO);

        // Add this post to user and category
        userDao.addPost(savedPostDAO);

        // check if category not null
        if(nonNull(categoryDAO)) {
            categoryDAO.addPost(savedPostDAO);
        }
        return modelMapper.daoTOPostDTO(savedPostDAO);
    }

    @Override
    public PostDTO updatePost(PostDTO postDTO, long postId) {
        // We can update title, content, user, category
        Post postDao = getPostDAOOrThrowException(postId);

        // Check if user owns this post
        if(!AuthUtil.isSameUser(MDC.get(MDC_USERNAME_KEY),postDao.getUser().getEmail())) {
            throw new UserNotAuthorizedException();
        }

        postDao.setTitle(postDTO.getTitle());
        postDao.setContent(postDTO.getContent());

        // Update category
        if(nonNull(postDTO.getCategoryId())){
            Category categoryDAO = categoryRepository.findById(postDTO.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category", "Category ID", postDTO.getCategoryId()));
            postDao.setCategory(categoryDAO);
        }

        // Update user
        User userDao = userRepository.findById(postDTO.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User","id",postDTO.getUserId()));
        postDao.setUser(userDao);

        // Save in db
        Post savedPost = postRepository.save(postDao);

        return modelMapper.daoTOPostDTO(savedPost);
    }

    @Override
    public PostDTO deletePost(long postId) {
        Post postDao = getPostDAOOrThrowException(postId);

        if(!AuthUtil.isSameUserOrAdmin(MDC.get(MDC_USERNAME_KEY), MDC.get(MDC_ROLE_KEY), postDao.getUser().getEmail())) {
            throw new UserNotAuthorizedException();
        }

        postRepository.delete(postDao);

        // Remove this post from user and category as well
        User user = postDao.getUser();
        Category category = postDao.getCategory();

        user.removePost(postDao);

        // category can be null
        if(nonNull(category)) {
            category.removePost(postDao);
        }
        return modelMapper.daoTOPostDTO(postDao);
    }

    @Override
    public PaginationResponseDTO getAllPosts(int pageNumber, int pageSize, String sortBy, String sortDir) {

        if(!AuthUtil.isAdmin(MDC.get(MDC_ROLE_KEY))) {
            throw new UserNotAuthorizedException();
        }

        Sort sort;
        if(sortDir.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Post> page = postRepository.findAll(pageable);
        List<Post> postDaoList =page.getContent();
        List<PostDTO> content = postDaoList.stream().map(modelMapper::daoTOPostDTO).toList();

        return PaginationResponseDTO
                .builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getNumberOfElements())
                .isLastPage(page.isLast())
                .build();
    }

    @Override
    public List<PostDTO> searchPosts(String keyword) {
        if(!AuthUtil.isAdmin(MDC.get(MDC_ROLE_KEY))) {
            throw new UserNotAuthorizedException();
        }

        List<Post> postDAOList = postRepository.findByTitleContaining(keyword);
        return postDAOList.stream().map(modelMapper::daoTOPostDTO).collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getAllComments(long postId) {
        Post postDao = getPostDAOOrThrowException(postId);

        if(!AuthUtil.isSameUserOrAdmin(MDC.get(MDC_USERNAME_KEY), MDC.get(MDC_ROLE_KEY), postDao.getUser().getEmail())) {
            throw new UserNotAuthorizedException();
        }

        return postDao.getComments().stream().map(modelMapper::daoToCommentDTO).collect(Collectors.toList());
    }

    public Post getPostDAOOrThrowException(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id",postId));
    }
}
