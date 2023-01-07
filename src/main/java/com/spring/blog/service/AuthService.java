package com.spring.blog.service;

import com.spring.blog.dto.LoginDto;
import com.spring.blog.dto.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto) throws Exception;
}
