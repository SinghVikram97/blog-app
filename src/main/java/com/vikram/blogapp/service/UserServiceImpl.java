package com.vikram.blogapp.service;

import com.vikram.blogapp.dto.UserDTO;
import com.vikram.blogapp.entities.User;
import com.vikram.blogapp.exception.ResourceNotFoundException;
import com.vikram.blogapp.mapper.ModelMapper;
import com.vikram.blogapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User userDAO = modelMapper.dtoToUserDAO(userDTO);
        userRepository.save(userDAO);
        return null;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, long userId) {
        User userDAO = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));

        userDAO.setName(userDTO.getName());
        userDAO.setEmail(userDTO.getEmail());
        userDAO.setPassword(userDTO.getPassword());
        userDAO.setAbout(userDAO.getAbout());

        User updateUserDAO = userRepository.save(userDAO);
        return modelMapper.daoToUserDTO(updateUserDAO);
    }

    @Override
    public UserDTO getUserById(long userId) {
        User userDAO = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));
        return modelMapper.daoToUserDTO(userDAO);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> userDAOList = userRepository.findAll();
        return userDAOList.stream().map(modelMapper::daoToUserDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO deleteUser(long userId) {
        User userDAO = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));
        userRepository.deleteById(userDAO.getId());
        return modelMapper.daoToUserDTO(userDAO);
    }
}
