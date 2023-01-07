package com.spring.blog.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCommentException extends RuntimeException {

    private long postId;
    private long commentId;
    private HttpStatus status;

    public InvalidCommentException(long postId, long commentId, HttpStatus status) {
        super(String.format("Comment ID: %s does not belong to Post ID: %s", commentId, postId));
        this.postId = postId;
        this.commentId = commentId;
        this.status = status;
    }


}
