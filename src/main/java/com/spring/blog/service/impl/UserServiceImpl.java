package com.spring.blog.service.impl;

import com.spring.blog.dto.UserDto;
import com.spring.blog.entity.User;
import com.spring.blog.exceptions.ResourceNotFoundException;
import com.spring.blog.repository.UserRepository;
import com.spring.blog.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = checkAndGetUser(userId);
        return mapToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map((user) -> mapToDto(user)).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapToEntity(userDto);
        user.setPassword(passwordEncoder(userDto.getPassword()));
        User createdUser = userRepository.save(user);
        return mapToDto(createdUser);
    }

    private UserDto mapToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User mapToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private User checkAndGetUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
        return user;
    }

    private String passwordEncoder(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }


}
