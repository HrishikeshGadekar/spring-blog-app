package com.spring.blog.controller;

import com.spring.blog.dto.RoleDto;
import com.spring.blog.dto.UserDto;
import com.spring.blog.service.RoleService;
import com.spring.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static com.spring.blog.utils.Constants.*;

@RestController
@RequestMapping(BASE_USER_URL)
public class RoleController {

    private RoleService roleService;

    private UserService userService;

    public RoleController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping(GET_ROLES)
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

    @GetMapping(GET_ROLES_BY_USER_ID)
    public ResponseEntity<Set<RoleDto>> getRolesByUserId(@PathVariable long userId) {
        return new ResponseEntity<>(roleService.getRolesByUserId(userId), HttpStatus.OK);
    }

    @PostMapping(CREATE_ROLE)
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        return new ResponseEntity<>(roleService.createRoll(roleDto), HttpStatus.CREATED);
    }

    @PostMapping(ASSIGN_ROLE_BY_USER_ID)
    public ResponseEntity<UserDto> assignRoleByUserId(@PathVariable long userId, @Valid @RequestBody RoleDto roleDto) {
        return new ResponseEntity<>(roleService.assignRoleByUserId(userId, roleDto), HttpStatus.ACCEPTED);
    }
}
