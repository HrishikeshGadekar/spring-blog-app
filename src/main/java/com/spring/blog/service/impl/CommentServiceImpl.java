package com.spring.blog.service.impl;

import com.spring.blog.dto.CommentDto;
import com.spring.blog.dto.PostDto;
import com.spring.blog.entity.Comment;
import com.spring.blog.entity.Post;
import com.spring.blog.exceptions.InvalidCommentException;
import com.spring.blog.exceptions.ResourceNotFoundException;
import com.spring.blog.repository.CommentRepository;
import com.spring.blog.repository.PostRepository;
import com.spring.blog.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    private PostRepository postRepository;

    private ModelMapper modelMapper;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {
        Post fetchPost = checkAndGetPost(postId);
        Comment comment = mapToEntity(commentDto);
        comment.setPost(fetchPost);
        Comment createdComment = commentRepository.save(comment);
        return mapToDto(createdComment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        Post fetchPost = checkAndGetPost(postId);
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentByPostId(long postId, long commentId) {
        Post fetchPost = checkAndGetPost(postId);
        Comment comment = checkAndGetComment(commentId);
        checkIfCommentBelongsToPost(postId, commentId, fetchPost, comment);
        return mapToDto(comment);
    }

    @Override
    public CommentDto updateComment(long postId, long commentId, CommentDto commentDto) {
        Post fetchPost = checkAndGetPost(postId);
        Comment comment = checkAndGetComment(commentId);
        checkIfCommentBelongsToPost(postId, commentId, fetchPost, comment);
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());
        Comment updatedComment = commentRepository.save(comment);
        return mapToDto(updatedComment);
    }

    @Override
    public void deleteComment(long postId, long commentId) {
        Post fetchPost = checkAndGetPost(postId);
        Comment comment = checkAndGetComment(commentId);
        checkIfCommentBelongsToPost(postId, commentId, fetchPost, comment);
        commentRepository.delete(comment);
    }


    // Private Service Methods
    private Comment mapToEntity(CommentDto commentDto) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
//        comment.setName(commentDto.getName());
//        comment.setEmail(commentDto.getEmail());
//        comment.setBody(commentDto.getBody());
        return comment;
    }

    private CommentDto mapToDto(Comment createdComment) {
        CommentDto createdCommentDto = modelMapper.map(createdComment, CommentDto.class);
//        createdCommentDto.setId(createdComment.getId());
//        createdCommentDto.setName(createdComment.getName());
//        createdCommentDto.setEmail(createdComment.getEmail());
//        createdCommentDto.setBody(createdComment.getBody());
//        createdCommentDto.setPostDto(mapToPostDto(createdComment.getPost()));
        return createdCommentDto;
    }

    private Post checkAndGetPost(long postId) {
        Post fetchPost = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        return fetchPost;
    }

    private Comment checkAndGetComment(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        return comment;
    }

    private void checkIfCommentBelongsToPost(long postId, long commentId, Post fetchPost, Comment comment) {
        if (!(comment.getPost().getId().equals(fetchPost.getId()))) {
            throw new InvalidCommentException(commentId, postId, HttpStatus.BAD_REQUEST);
        }
    }


}
