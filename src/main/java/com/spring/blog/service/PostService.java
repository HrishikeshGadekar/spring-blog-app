package com.spring.blog.service;

import com.spring.blog.dto.PostDto;
import com.spring.blog.dto.PostResponseDto;

public interface PostService {

    public PostDto createPost(PostDto postDto);

    public PostResponseDto getAllPosts(int pageNo, int pageSize, String sortBy, String sortOrder);

    public PostDto getPostById(long id);

    public PostDto updatePost(PostDto postDto, long id);

    public void deletePost(long id);
}
