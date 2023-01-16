package com.spring.blog.controller;

import com.spring.blog.dto.JwtAuthResponseDto;
import com.spring.blog.dto.LoginDto;
import com.spring.blog.dto.RegisterDto;
import com.spring.blog.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.spring.blog.utils.Constants.*;

@RestController
@RequestMapping(AUTH_BASE_URL)
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping(value = {LOGIN_URL, SIGN_IN_URL})
    public ResponseEntity<JwtAuthResponseDto> login(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        JwtAuthResponseDto jwtAuthResponseDto = new JwtAuthResponseDto();
        jwtAuthResponseDto.setAccessToken(token);
        return new ResponseEntity<>(jwtAuthResponseDto, HttpStatus.OK);
    }

    @PostMapping(value = {REGISTER_URL, SIGN_UP_URL})
    public ResponseEntity<Object> register(@RequestBody RegisterDto registerDto) throws Exception {
        return new ResponseEntity<>(authService.register(registerDto), HttpStatus.CREATED);
    }
}
