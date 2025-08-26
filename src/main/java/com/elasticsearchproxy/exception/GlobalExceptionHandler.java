package com.elasticsearchproxy.exception;

import com.elasticsearchproxy.model.ElasticsearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        logger.warn("Validation error occurred: {}", ex.getMessage());
        
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessages = bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "VALIDATION_ERROR");
        errorResponse.put("message", "Request validation failed");
        errorResponse.put("details", errorMessages);
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ElasticsearchResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        logger.warn("Invalid argument provided: {}", ex.getMessage());
        
        ElasticsearchResponse response = ElasticsearchResponse.error(null, null, ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ElasticsearchResponse> handleRuntimeException(
            RuntimeException ex) {
        
        logger.error("Runtime exception occurred", ex);
        
        ElasticsearchResponse response = ElasticsearchResponse.error(
                null, null, "Internal server error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ElasticsearchResponse> handleGenericException(
            Exception ex) {
        
        logger.error("Unexpected exception occurred", ex);
        
        ElasticsearchResponse response = ElasticsearchResponse.error(
                null, null, "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}