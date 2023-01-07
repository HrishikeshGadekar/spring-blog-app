package com.spring.blog.service.impl;

import com.spring.blog.dto.LoginDto;
import com.spring.blog.dto.RegisterDto;
import com.spring.blog.dto.UserDto;
import com.spring.blog.entity.Role;
import com.spring.blog.entity.User;
import com.spring.blog.exceptions.ResourceNotFoundException;
import com.spring.blog.repository.RoleRepository;
import com.spring.blog.repository.UserRepository;
import com.spring.blog.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    private ModelMapper modelMapper;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "User logged in successfully!";
    }

    @Override
    public String register(RegisterDto registerDto) throws Exception {
        if (userRepository.existsByUsername(registerDto.getUsername()) || userRepository.existsByEmail(registerDto.getEmail())) {
            throw new Exception("Username or Email already exists!");
        }
        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder(registerDto.getPassword()));
        Set<Role> roleSet = new HashSet<>();
        Role userRole = roleRepository.findByName("USER").get();
        roleSet.add(userRole);
        user.setRoles(roleSet);
        User createdUser = userRepository.save(user);
        return "User registered successfully!";
    }

    private UserDto mapToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User mapToEntity(RegisterDto registerDto) {
        return modelMapper.map(registerDto, User.class);
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
