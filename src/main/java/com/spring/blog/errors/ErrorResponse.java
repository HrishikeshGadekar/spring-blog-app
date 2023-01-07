package com.spring.blog.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timeStamp;
    private HttpStatus status;
    private String message;
    private String details;

    public ErrorResponse(String message, String details, HttpStatus status) {
        timeStamp = LocalDateTime.now();
        this.message = message;
        this.status = status;
        this.details = details;
    }

}
