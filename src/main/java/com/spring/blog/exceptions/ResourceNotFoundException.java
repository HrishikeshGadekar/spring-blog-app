package com.spring.blog.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private String entityName;
    private String fieldName;
    private long fieldValue;

    public ResourceNotFoundException(String entityName, String fieldName, long fieldValue) {
        super(String.format("%s not found with %s: %s", entityName, fieldName, fieldValue));
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }


}
