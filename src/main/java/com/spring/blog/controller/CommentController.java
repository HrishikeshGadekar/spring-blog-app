package com.spring.blog.controller;

import com.spring.blog.dto.CommentDto;
import com.spring.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.spring.blog.utils.Constants.*;

@RestController()
@RequestMapping(BASE_URL)
public class CommentController {

    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(CREATE_COMMENT)
    public ResponseEntity<CommentDto> createComment(@PathVariable long postId,
                                                    @Valid @RequestBody CommentDto commentDto) {
        return new ResponseEntity<>(commentService.createComment(postId, commentDto), HttpStatus.CREATED);
    }

    @GetMapping(GET_COMMENTS)
    public ResponseEntity<List> getAllCommentsByPost(@PathVariable long postId) {
        return new ResponseEntity<>(commentService.getCommentsByPostId(postId), HttpStatus.OK);
    }

    @GetMapping(GET_COMMENT_BY_ID)
    public ResponseEntity<CommentDto> getCommentById(@PathVariable long postId, @PathVariable long commentId) {
        return new ResponseEntity<>(commentService.getCommentByPostId(postId, commentId), HttpStatus.OK);
    }

    @PutMapping(UPDATE_COMMENT)
    public ResponseEntity<CommentDto> updateComment(@PathVariable long postId, @PathVariable long commentId,
                                                    @Valid @RequestBody CommentDto commentDto) {
        return new ResponseEntity<>(commentService.updateComment(postId, commentId, commentDto), HttpStatus.ACCEPTED);
    }

    @DeleteMapping(DELETE_COMMENT)
    public ResponseEntity<String> deleteComment(@PathVariable long postId, @PathVariable long commentId) {
        commentService.deleteComment(postId, commentId);
        return new ResponseEntity<>("Comment Deleted Successfully!", HttpStatus.OK);
    }
}
