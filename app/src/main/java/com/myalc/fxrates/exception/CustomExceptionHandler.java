package com.myalc.fxrates.exception;

import javax.validation.ValidationException;

import com.myalc.fxrates.dto.CustomAppError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class CustomExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomAppError> customExceptionHandler(CustomException ex, WebRequest request) {

        CustomAppError message = new CustomAppError(String.valueOf(ex.getStatus().value()), ex.getMessage(), ex.getErrors());
        logger.warn("CustomException: status: {}, message: {}, errors: {}, request: {}", ex.getStatus(), ex.getMessage(), ex.getErrors(), request.toString());
        return new ResponseEntity<CustomAppError>(message, ex.getStatus());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<CustomAppError> requestHandlingNoHandlerFound(NoHandlerFoundException ex, WebRequest request) {
        
        CustomAppError err = new CustomAppError(String.valueOf(HttpStatus.NOT_FOUND.value()), "Not Found");
        logger.warn("NoHandlerFoundException: ex: {}, request: {}", ex.toString(), request.toString());
        return new ResponseEntity<CustomAppError>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomAppError> methodNotSupportedHandler(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        
        CustomAppError err = new CustomAppError(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()), "Method Not Allowed");
        logger.warn("HttpRequestMethodNotSupportedException: ex: {}, request: ", ex.toString(), request.toString());
        return new ResponseEntity<CustomAppError>(err, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomAppError> globalExceptionHandler(Exception ex, WebRequest request) {

        logger.warn("Exception: ex: {}, request: {}", ex.toString(), request.toString());

        if (ex instanceof ServletRequestBindingException || ex instanceof ValidationException || ex instanceof BindException || ex instanceof HttpMessageConversionException) {
            CustomAppError err = new CustomAppError(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Bad Request", ex.toString());
            return new ResponseEntity<CustomAppError>(err, HttpStatus.BAD_REQUEST);
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            CustomAppError err = new CustomAppError(String.valueOf(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()), "Unsupported Media Type", ex.toString());
            return new ResponseEntity<CustomAppError>(err, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } else {
            ex.printStackTrace();
            CustomAppError err = new CustomAppError(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "Internal Server Error", ex.toString());
            return new ResponseEntity<CustomAppError>(err, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
