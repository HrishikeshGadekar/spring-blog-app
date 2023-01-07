package com.spring.blog.service;


import com.spring.blog.dto.CommentDto;
import com.spring.blog.entity.Comment;

import java.util.List;

public interface CommentService {

    public CommentDto createComment(long postId, CommentDto commentDto);

    public List<CommentDto> getCommentsByPostId(long postId);

    public CommentDto getCommentByPostId(long postId, long commentId);

    public CommentDto updateComment(long postId, long commentId, CommentDto commentDto);

    public void deleteComment(long postId, long commentId);
}
