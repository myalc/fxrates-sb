package com.myalc.fxrates.dto;

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CustomAppError {

    @NotBlank
    private String code; 

    @NotBlank
    private String message; 

    private List<String> errors;

    public CustomAppError() {
    }

    public CustomAppError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public CustomAppError(String code, String message, List<String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public CustomAppError(String code, String message, String error) {
        this.code = code;
        this.message = message;
        this.errors = Arrays.asList(error);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
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
