package com.spring.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    private int pageNo;
    private int pageSize;
    private long totalPosts;
    private int totalPages;
    private boolean lastPage;
    private List<PostDto> posts;

}
