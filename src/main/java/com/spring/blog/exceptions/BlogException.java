package com.spring.blog.exceptions;

import org.springframework.http.HttpStatus;

public class BlogException extends RuntimeException {

    private HttpStatus httpStatus;
    private String message;

    public BlogException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public BlogException(String message, HttpStatus httpStatus, String message1) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message1;
    }

    public BlogException(String message, Throwable cause, HttpStatus httpStatus, String message1) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.message = message1;
    }

    public BlogException(Throwable cause, HttpStatus httpStatus, String message) {
        super(cause);
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public BlogException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                         HttpStatus httpStatus, String message1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.httpStatus = httpStatus;
        this.message = message1;
    }
}
