package com.myalc.fxrates.exception;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    private HttpStatus status;
    private String message; 
    private List<String> errors;

    public CustomException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public CustomException(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public CustomException(HttpStatus status, String message, String error) {
        this.status = status;
        this.message = message;
        this.errors = Arrays.asList(error);
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
