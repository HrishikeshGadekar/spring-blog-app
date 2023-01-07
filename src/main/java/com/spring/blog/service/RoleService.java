package com.spring.blog.service;

import com.spring.blog.dto.RoleDto;
import com.spring.blog.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


public interface RoleService {

    RoleDto createRoll(RoleDto roleDto);

    UserDto assignRoleByUserId(long userId, RoleDto roleDto);

    List<RoleDto> getAllRoles();

    List<UserDto> getUsersByRole(RoleDto roleDto);

    Set<RoleDto> getRolesByUserId(long userId);
}
