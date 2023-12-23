package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.CommentDTO;
import com.vikram.blogapp.dto.PostDTO;
import com.vikram.blogapp.dto.UserDTO;
import com.vikram.blogapp.entities.Comment;
import com.vikram.blogapp.entities.Post;
import com.vikram.blogapp.entities.User;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.exception.UserAlreadyExistsException;
import com.vikram.blogapp.exception.UserNotAuthorizedException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.vikram.blogapp.constants.Constants.MDC_ROLE_KEY;
import static com.vikram.blogapp.constants.Constants.MDC_USERNAME_KEY;
import static com.vikram.blogapp.util.AuthUtil.isAdmin;
import static com.vikram.blogapp.util.AuthUtil.isSameUser;
import static com.vikram.blogapp.util.AuthUtil.isSameUserOrAdmin;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDTO updateUser(UserDTO userDTO, long userId) {
        User userDAO = getUserDaoOrThrowException(userId);

        // Only user can update its own profile
        String emailFromJWT = MDC.get(MDC_USERNAME_KEY);
        String emailFromRequest = userDAO.getEmail();

        if(!isSameUser(emailFromJWT, emailFromRequest)) {
            throw new UserNotAuthorizedException();
        }

        userDAO.setFirstName(userDTO.getFirstName());
        userDAO.setLastName(userDTO.getLastName());
        userDAO.setEmail(userDTO.getEmail());
        userDAO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userDAO.setAbout(userDAO.getAbout());

        User updateUserDAO;
        try{
            updateUserDAO = userRepository.save(userDAO);
        }catch (DataIntegrityViolationException e){
            throw new UserAlreadyExistsException(userDTO.getEmail());
        }

        return modelMapper.daoToUserDTO(updateUserDAO);
    }

    @Override
    public UserDTO getUserById(long userId) {
        User userDAO = getUserDaoOrThrowException(userId);

        String emailFromJWT = MDC.get(MDC_USERNAME_KEY);
        String roleFromJWT = MDC.get(MDC_ROLE_KEY);

        String emailFromRequest = userDAO.getEmail();

        // Either owning user can access its account or an admin
        if(!isSameUserOrAdmin(emailFromJWT, roleFromJWT, emailFromRequest)) {
            throw new UserNotAuthorizedException();
        }

        return modelMapper.daoToUserDTO(userDAO);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        // Only admin can get all users
        String roleFromJWT = MDC.get(MDC_ROLE_KEY);

        if(!isAdmin(roleFromJWT)){
            throw new UserNotAuthorizedException();
        }

        List<User> userDAOList = userRepository.findAll();
        return userDAOList.stream().map(modelMapper::daoToUserDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO deleteUser(long userId) {
        User userDAO = getUserDaoOrThrowException(userId);

        String emailFromJWT = MDC.get(MDC_USERNAME_KEY);
        String roleFromJWT = MDC.get(MDC_ROLE_KEY);

        String emailFromRequest = userDAO.getEmail();

        if(!isSameUserOrAdmin(emailFromJWT, roleFromJWT, emailFromRequest)) {
            throw new UserNotAuthorizedException();
        }

        userRepository.deleteById(userDAO.getId());
        return modelMapper.daoToUserDTO(userDAO);
    }

    @Override
    public List<PostDTO> getAllPostsByUser(long userId) {
        // Public
        User userDAO = getUserDaoOrThrowException(userId);
        List<Post> postsDAO = userDAO.getPosts();

        String emailFromJWT = MDC.get(MDC_USERNAME_KEY);
        String roleFromJWT = MDC.get(MDC_ROLE_KEY);

        String emailFromRequest = userDAO.getEmail();

        if(!isSameUserOrAdmin(emailFromJWT, roleFromJWT, emailFromRequest)) {
            throw new UserNotAuthorizedException();
        }

        return postsDAO.stream().map(modelMapper::daoTOPostDTO).collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getAllCommentsByUser(long userId) {
        // Public
        User userDAO = getUserDaoOrThrowException(userId);

        String emailFromJWT = MDC.get(MDC_USERNAME_KEY);
        String roleFromJWT = MDC.get(MDC_ROLE_KEY);

        String emailFromRequest = userDAO.getEmail();

        if(!isSameUserOrAdmin(emailFromJWT, roleFromJWT, emailFromRequest)) {
            throw new UserNotAuthorizedException();
        }

        List<Comment> comments = userDAO.getComments();
        return comments.stream().map(modelMapper::daoToCommentDTO).collect(Collectors.toList());
    }

    public User getUserDaoOrThrowException(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));
    }
}
