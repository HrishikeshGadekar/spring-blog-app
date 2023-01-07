package com.spring.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.blog.entity.Post;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;

    @NotEmpty(message = "Name cannot be null or empty")
    private String name;

    @NotEmpty(message = "Email cannot be null or empty")
    @Email
    private String email;

    @NotEmpty(message = "Comment cannot be null or empty")
    @Size(min = 1, message = "Comment should have at least 1 character")
    private String body;
    private PostDto postDto;
}
