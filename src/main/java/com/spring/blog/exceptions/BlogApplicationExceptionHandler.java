package com.spring.blog.exceptions;

import com.spring.blog.errors.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class BlogApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> resourceNotFoundExceptionHandler(ResourceNotFoundException resourceNotFoundException, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse(resourceNotFoundException.getMessage(), webRequest.getDescription(false), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCommentException.class)
    public ResponseEntity<Object> invalidCommentExceptionHandler(InvalidCommentException invalidCommentException, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse(invalidCommentException.getMessage(), webRequest.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> invalidCommentExceptionHandler(AccessDeniedException accessDeniedException, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse(accessDeniedException.getMessage(), webRequest.getDescription(false), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    //Global Generic Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> globalGenericExceptionHandler(Exception exception, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
