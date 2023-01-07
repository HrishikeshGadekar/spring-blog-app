package com.spring.blog.service;

import com.spring.blog.dto.UserDto;
import com.spring.blog.entity.User;
import com.spring.blog.repository.UserRepository;

import java.util.List;

public interface UserService {

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

}
