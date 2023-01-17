package com.spring.blog.controller;

import com.spring.blog.dto.PostDto;
import com.spring.blog.dto.PostResponseDto;
import com.spring.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.spring.blog.utils.Constants.*;

@RestController
@RequestMapping(BASE_URL)
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto) {
        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PostResponseDto> getAllPosts(@RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER,
            required = false) int pageNo,
                                                       @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE,
                                                               required = false) int pageSize,
                                                       @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY,
                                                               required = false) String sortBy,
                                                       @RequestParam(value = "sortOrder", defaultValue = DEFAULT_SORT_DIRECTION
                                                               , required = false) String sortOrder) {
        return new ResponseEntity<>(postService.getAllPosts(pageNo, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping(MAP_TO_ID)
    public ResponseEntity<PostDto> getPostById(@PathVariable long postId) {
        return new ResponseEntity<>(postService.getPostById(postId), HttpStatus.OK);
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(MAP_TO_ID)
    public ResponseEntity<PostDto> updatePost(@Valid @PathVariable long postId, @RequestBody PostDto postDto) {
        return new ResponseEntity<>(postService.updatePost(postDto, postId), HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(MAP_TO_ID)
    public ResponseEntity<String> deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
        return new ResponseEntity<>("Post deleted successfully!", HttpStatus.OK);
    }

    @GetMapping(GET_POSTS_BY_CATEGORY)
    public ResponseEntity<List<PostDto>> getPostsByCategory(@RequestParam long categoryId) {
        return new ResponseEntity<>(postService.getPostsByCategory(categoryId), HttpStatus.OK);
    }

    @GetMapping(SEARCH_POST)
    public ResponseEntity<List<PostDto>> searchPost(@RequestParam String query) {
        return new ResponseEntity<>(postService.searchPost(query), HttpStatus.OK);
    }

    @GetMapping(SEARCH_POST_SQL)
    public ResponseEntity<List<PostDto>> searchPostSQL(@RequestParam String query) {
        return new ResponseEntity<>(postService.searchPostSQL(query), HttpStatus.OK);
    }

    @GetMapping(SEARCH_POST_BY_TITLE)
    public ResponseEntity<List<PostDto>> searchPostByTitle(@RequestParam String title) {
        return new ResponseEntity<>(postService.searchPostByTitle(title), HttpStatus.OK);
    }

}

