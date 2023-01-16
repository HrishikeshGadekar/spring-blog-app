package com.spring.blog.repository;

import com.spring.blog.dto.PostDto;
import com.spring.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByCategoryId(long categoryId);
}
