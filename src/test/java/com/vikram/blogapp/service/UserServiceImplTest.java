package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.dto.UserDTO;
import com.vikram.blogapp.entities.Role;
import com.vikram.blogapp.entities.User;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.exception.UserAlreadyExistsException;
import com.vikram.blogapp.exception.UserNotAuthorizedException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.UserRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.vikram.blogapp.constants.Constants.MDC_ROLE_KEY;
import static com.vikram.blogapp.constants.Constants.MDC_USERNAME_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateUser_sameUser_returnsUpdatedUser() {
        long userId = 1L;

        User user = generateRandomUser();
        UserDTO userDTO = generateRandomUserDTO();

        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(modelMapper.daoToUserDTO(any())).thenReturn(userDTO);

        UserDTO updatedUserDTO = userService.updateUser(userDTO, userId);

        assertNotNull(updatedUserDTO);
        assertUserDTOEqual(userDTO, updatedUserDTO);
    }

    @Test
    void testUpdateUser_byDifferentUser_throwsUserNotAuthorizedException() {
        long userId = 1L;
        User user = generateRandomUser();
        UserDTO userDTO = generateRandomUserDTO();

        MDC.put(MDC_USERNAME_KEY, "differentUser@gmail.com");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        assertThrows(UserNotAuthorizedException.class, () -> {
            userService.updateUser(userDTO, userId);
        });
    }

    @Test
    void testUpdateUser_userNotFound_throwsResourceNotFoundException() {
        long userId = 1L;
        UserDTO userDTO = generateRandomUserDTO();

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Test & Verify
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(userDTO, userId);
        });
    }

    @Test
    void testUpdateUser_userAlreadyExistsWithSameEmail_throwsUserAlreadyExistsException() {
        long userId = 1L;
        User user = generateRandomUser();
        UserDTO userDTO = generateRandomUserDTO();
        MDC.put(MDC_USERNAME_KEY, user.getEmail());


        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.updateUser(userDTO, userId);
        });
    }

    @Test
    void testGetUserById_sameUser_returnsUserDTO() {
        User user = generateRandomUser();
        UserDTO userDTO = generateRandomUserDTO();
        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(modelMapper.daoToUserDTO(any())).thenReturn(userDTO);

        UserDTO getUserDTO = userService.getUserById(user.getId());

        assertNotNull(userDTO);
        assertUserDTOEqual(userDTO, getUserDTO);
    }

    @Test
    void testGetUserById_admin_returnsUserDTO() {
        User user = generateRandomUser();
        UserDTO userDTO = generateRandomUserDTO();
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());

        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(modelMapper.daoToUserDTO(any())).thenReturn(userDTO);

        UserDTO getUserDTO = userService.getUserById(user.getId());

        assertNotNull(userDTO);
        assertUserDTOEqual(userDTO, getUserDTO);
    }

    @Test
    void testGetUserById_differentUser_throwsUserNotAuthorizedException() {
        User user = generateRandomUser();
        MDC.put(MDC_USERNAME_KEY, "differentUser@gmail.com");

        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));

        assertThrows(UserNotAuthorizedException.class, () -> {
            userService.getUserById(user.getId());
        });
    }

    @Test
    void testGetUserById_userNotFound_throwsResourceNotFoundException() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    void testGetAllUsers_admin_returnsUserDTOList() {
        List<User> userList = Instancio.ofList(User.class).size(5).create();

        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());
        when(userRepository.findAll()).thenReturn(userList);
        when(modelMapper.daoToUserDTO(any())).thenReturn(generateRandomUserDTO());

        // Test
        List<UserDTO> userDTOList = userService.getAllUsers();

        assertNotNull(userDTOList);
        assertEquals(userList.size(), userDTOList.size());
    }

    @Test
    void testGetAllUsers_notAdmin_throwsUserNotAuthorizedException() {
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.name());
        assertThrows(UserNotAuthorizedException.class, () -> {
            userService.getAllUsers();
        });
    }

    @Test
    void testDeleteUser_sameUser_returnsDeletedUserDTO() {
        User user = generateRandomUser();
        UserDTO userDTO = generateRandomUserDTO();

        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(modelMapper.daoToUserDTO(any())).thenReturn(userDTO);

        UserDTO deletedUserDTO = userService.deleteUser(user.getId());

        assertNotNull(deletedUserDTO);
        verify(userRepository, times(1)).deleteById(user.getId());
        assertUserDTOEqual(userDTO, deletedUserDTO);
    }

    @Test
    void testDeleteUser_admin_returnsDeletedUserDTO() {
        User user = generateRandomUser();
        UserDTO userDTO = generateRandomUserDTO();

        MDC.put(MDC_USERNAME_KEY, "admin@test.com");
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());

        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(modelMapper.daoToUserDTO(any())).thenReturn(userDTO);

        UserDTO deletedUserDTO = userService.deleteUser(user.getId());

        assertNotNull(deletedUserDTO);
        verify(userRepository, times(1)).deleteById(user.getId());
        assertUserDTOEqual(userDTO, deletedUserDTO);
    }

    @Test
    void testDeleteUser_differentUserNotAdmin_throwsUserNotAuthorizedException() {
        User user = generateRandomUser();

        MDC.put(MDC_USERNAME_KEY, "differentUser@gmail.com");

        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));

        assertThrows(UserNotAuthorizedException.class, () -> {
            userService.deleteUser(user.getId());
        });

        verify(userRepository, never()).deleteById(user.getId());
    }

    @Test
    void testDeleteUser_userNotFound_ThrowsResourceNotFoundException() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void testGetAllPostsByUser_userNotFound_throwsResourceNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getAllPostsByUser(userId));
    }

    @Test
    void testGetAllPostsByUser_notAuthorized_throwsUserNotAuthorizedException() {
        User user = generateRandomUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        MDC.put(MDC_USERNAME_KEY, "differentuser@gmail.com");
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.name());

        assertThrows(UserNotAuthorizedException.class, () -> userService.getAllPostsByUser(user.getId()));
    }

    @Test
    void testGetAllPostsByUser_bySameUser_returnsPostDTOList() {
        User user = generateRandomUser();
        PostDTO postDTO = Instancio.create(PostDTO.class);

        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.daoTOPostDTO(any())).thenReturn(postDTO);

        List<PostDTO> postDTOs = userService.getAllPostsByUser(user.getId());

        assertNotNull(postDTOs);
        assertEquals(user.getPosts().size(), postDTOs.size());
    }

    @Test
    void testGetAllPostsByUser_byAdmin_returnsPostDTOList() {
        User user = generateRandomUser();
        PostDTO postDTO = Instancio.create(PostDTO.class);

        MDC.put(MDC_USERNAME_KEY, "differentuser@gmail.com");
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.daoTOPostDTO(any())).thenReturn(postDTO);

        List<PostDTO> postDTOs = userService.getAllPostsByUser(user.getId());

        assertNotNull(postDTOs);
        assertEquals(user.getPosts().size(), postDTOs.size());
    }

    @Test
    void testGetAllCommentsByUser_userNotFound_throwsResourceNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getAllCommentsByUser(userId));
    }

    @Test
    void testGetAllCommentsByUser_notAuthorized_throwsUserNotAuthorizedException() {
        User user = generateRandomUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        MDC.put(MDC_USERNAME_KEY, "differentuser@gmail.com");
        MDC.put(MDC_ROLE_KEY, Role.ROLE_USER.name());

        assertThrows(UserNotAuthorizedException.class, () -> userService.getAllCommentsByUser(user.getId()));
    }

    @Test
    void testGetAllCommentsByUser_bySameUser_returnsCommentDTOList() {
        User user = generateRandomUser();
        CommentDTO commentDTO = Instancio.create(CommentDTO.class);

        MDC.put(MDC_USERNAME_KEY, user.getEmail());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.daoToCommentDTO(any())).thenReturn(commentDTO);

        List<CommentDTO> commentDTOList = userService.getAllCommentsByUser(user.getId());

        assertNotNull(commentDTOList);
        assertEquals(user.getComments().size(), commentDTOList.size());
    }

    @Test
    void testGetAllCommentsByUser_byAdmin_returnsCommentDTOList() {
        User user = generateRandomUser();
        CommentDTO commentDTO = Instancio.create(CommentDTO.class);

        MDC.put(MDC_USERNAME_KEY, "differentuser@gmail.com");
        MDC.put(MDC_ROLE_KEY, Role.ROLE_ADMIN.name());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.daoToCommentDTO(any())).thenReturn(commentDTO);

        List<CommentDTO> commentDTOList = userService.getAllCommentsByUser(user.getId());

        assertNotNull(commentDTOList);
        assertEquals(user.getComments().size(), commentDTOList.size());
    }


    private void assertUserDTOEqual(UserDTO expectedUserDTO, UserDTO actualUserDTO) {
        assertEquals(expectedUserDTO.getId(), actualUserDTO.getId());
        assertEquals(expectedUserDTO.getFirstName(), actualUserDTO.getFirstName());
        assertEquals(expectedUserDTO.getLastName(), actualUserDTO.getLastName());
        assertEquals(expectedUserDTO.getPassword(), actualUserDTO.getPassword());
        assertEquals(expectedUserDTO.getAbout(), actualUserDTO.getAbout());
        assertEquals(expectedUserDTO.getEmail(), actualUserDTO.getEmail());
    }

    private User generateRandomUser(){
        return Instancio.create(User.class);
    }

    private UserDTO generateRandomUserDTO(){
        return Instancio.create(UserDTO.class);
    }

}