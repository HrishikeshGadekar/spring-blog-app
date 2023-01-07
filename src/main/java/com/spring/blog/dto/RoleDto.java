package com.spring.blog.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RoleDto {
    private long id;

    @NotEmpty
    private String name;
}
