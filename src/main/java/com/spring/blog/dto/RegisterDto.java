package com.spring.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {

    @NotEmpty(message = "Name cannot be empty or null")
    private String name;

    @NotEmpty(message = "Email cannot be empty or null")
    @Email
    private String email;

    @NotEmpty(message = "Username cannot be empty or null")
    @Size(min = 3, message = "Username should be atleast 3 characters")
    private String username;

    @NotEmpty(message = "Password cannot be empty or null")
    private String password;
}
