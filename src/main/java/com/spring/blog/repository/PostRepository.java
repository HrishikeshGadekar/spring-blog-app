package com.spring.blog.repository;

import com.spring.blog.dto.PostDto;
import com.spring.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByCategoryId(long categoryId);

    List<Post> searchPostByTitle(String title);

    @Query("SELECT p FROM Post p WHERE " +
            "p.title LIKE CONCAT('%',:query,'%')" +
            "Or p.description LIKE CONCAT('%',:query,'%') ")
    List<Post> searchPost(String query);

    @Query(value = "SELECT * FROM Post p WHERE " +
            "p.title LIKE CONCAT('%',:query,'%')" +
            "Or p.description LIKE CONCAT('%',:query,'%') ", nativeQuery = true)
    List<Post> searchPostSQL(String query);
}
