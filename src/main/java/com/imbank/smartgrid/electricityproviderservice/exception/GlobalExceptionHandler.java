package com.imbank.smartgrid.electricityproviderservice.exception;

import com.imbank.smartgrid.electricityproviderservice.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidReadingException.class)
    public ResponseEntity<ApiResponse<Void>>handleInvalidReadingException(InvalidReadingException ex){
        log.warn("Invalid reading: field = {}, rejectedValue = {}, reason = {}",
                ex.getField(),ex.getRejectedValue(),ex.getReason());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, ex.getMessage(), null));
    }
    @ExceptionHandler(ReadingProgressionException.class)
    public ResponseEntity<ApiResponse<Void>>handleReadingProgressionException(ReadingProgressionException ex){
        log.warn("Reading Progression violation: meterId = {}, currentReading= {}, previousReading = {}",
                ex.getMeterId(), ex.getCurrentReading(), ex.getPreviousReading());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, ex.getMessage(),null));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>>handleResourceNotFoundException(ResourceNotFoundException ex){
        log.warn("Resource not found: resourceName = {}, fieldName = {}, fieldValue = {}",
                ex.getResourceName(), ex.getFieldName(), ex.getFieldValue());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(404, ex.getMessage(), null));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>>handleGenericException(Exception ex){
        log.error("Unexpected Error Occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(500,"An Unexpected Error Occured", null));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, "Validation failed", errors));
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(
            NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(404, ex.getMessage(), null));
    }
}
