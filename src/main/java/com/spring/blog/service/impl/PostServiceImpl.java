package com.spring.blog.service.impl;

import com.spring.blog.dto.PostDto;
import com.spring.blog.dto.PostResponseDto;
import com.spring.blog.entity.Post;
import com.spring.blog.exceptions.BlogException;
import com.spring.blog.exceptions.ResourceNotFoundException;
import com.spring.blog.repository.CategoryRepository;
import com.spring.blog.repository.PostRepository;
import com.spring.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    private CategoryRepository categoryRepository;

    private ModelMapper modelMapper;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        Post post = mapToEntity(postDto);
        try {
            post.setCategory(categoryRepository.findByName(postDto.getCategoryName()));
        } catch (Exception e) {
            throw new BlogException(HttpStatus.BAD_REQUEST, "Category does not exist");
        }
        Post createdPost = postRepository.save(post);
        PostDto postDtoResponse = mapToDto(createdPost);
        return postDtoResponse;
    }


    @Override
    public PostResponseDto getAllPosts(int pageNo, int pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy)
                                                                                .ascending() : Sort.by(sortBy)
                                                                                                   .descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Post> page = postRepository.findAll(pageable);
        List<Post> posts = page.getContent();
        List<PostDto> content = posts.stream()
                                     .map((post) -> mapToDto(post))
                                     .collect(Collectors.toList());
        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setPageNo(page.getNumber());
        postResponseDto.setPageSize(page.getSize());
        postResponseDto.setTotalPosts(page.getTotalElements());
        postResponseDto.setTotalPages(page.getTotalPages());
        postResponseDto.setLastPage(page.isLast());
        postResponseDto.setPosts(content);

        return postResponseDto;
    }

    @Override
    public PostDto getPostById(long id) {
        Post fetchPost = postRepository.findById(id)
                                       .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return mapToDto(fetchPost);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        Post fetchPost = postRepository.findById(id)
                                       .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        fetchPost.setTitle(postDto.getTitle());
        fetchPost.setDescription(postDto.getDescription());
        fetchPost.setContent(postDto.getContent());
        try {
            fetchPost.setCategory(categoryRepository.findByName(postDto.getCategoryName()));
        } catch (Exception e) {
            throw new BlogException(HttpStatus.BAD_REQUEST, "Category does not exist");
        }
        return mapToDto(postRepository.save(fetchPost));
    }

    @Override
    public void deletePost(long id) {
        Post fetchPost = postRepository.findById(id)
                                       .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        postRepository.delete(fetchPost);
    }

    @Override
    public List<PostDto> getPostsByCategory(long categoryId) {
        categoryRepository.findById(categoryId)
                          .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", categoryId));
        List<Post> posts = postRepository.findByCategoryId(categoryId);
        return posts.stream()
                    .map((post) -> mapToDto(post))
                    .collect(Collectors.toList());
    }

    private Post mapToEntity(PostDto postDto) {
        Post post = modelMapper.map(postDto, Post.class);
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());
        return post;
    }

    private PostDto mapToDto(Post createdPost) {
        PostDto postDtoResponse = modelMapper.map(createdPost, PostDto.class);
//        postDtoResponse.setId(createdPost.getId());
//        postDtoResponse.setTitle(createdPost.getTitle());
//        postDtoResponse.setDescription(createdPost.getDescription());
//        postDtoResponse.setContent(createdPost.getContent());
        return postDtoResponse;
    }
}
