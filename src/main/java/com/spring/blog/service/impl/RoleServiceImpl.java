package com.spring.blog.service.impl;

import com.spring.blog.dto.RoleDto;
import com.spring.blog.dto.UserDto;
import com.spring.blog.entity.Role;
import com.spring.blog.entity.User;
import com.spring.blog.exceptions.ResourceNotFoundException;
import com.spring.blog.repository.RoleRepository;
import com.spring.blog.repository.UserRepository;
import com.spring.blog.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    private UserRepository userRepository;

    private ModelMapper modelMapper;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RoleDto createRoll(RoleDto roleDto) {
        Role role = mapToEntity(roleDto);
        Role createdRole = roleRepository.save(role);
        return mapToDto(role);
    }

    @Override
    public UserDto assignRoleByUserId(long userId, RoleDto roleDto) {
        User user = checkAndGetUser(userId);
        Set<Role> roleSet = new HashSet<>();
        Role role = mapToEntity(roleDto);
        roleSet.add(role);
        user.setRoles(roleSet);
        User assignedUser = userRepository.save(user);
        return mapToUserDto(assignedUser);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream().map((role) -> mapToDto(role)).collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getUsersByRole(RoleDto roleDto) {
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(mapToEntity(roleDto));
        Optional<Role> role = roleRepository.findByName(roleDto.getName());
        return null;
//        return userRepository.findByRoles(roleSet).stream().map((user) -> mapToUserDto(user)).collect(Collectors.toList());
    }

    @Override
    public Set<RoleDto> getRolesByUserId(long userId) {
        User user = checkAndGetUser(userId);
        return user.getRoles().stream().map((role) -> mapToDto(role)).collect(Collectors.toSet());
    }

    private RoleDto mapToDto(Role role) {
        return modelMapper.map(role, RoleDto.class);
    }

    private Role mapToEntity(RoleDto roleDto) {
        return modelMapper.map(roleDto, Role.class);
    }

    private UserDto mapToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User mapToUserEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private User checkAndGetUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
        return user;
    }
}
