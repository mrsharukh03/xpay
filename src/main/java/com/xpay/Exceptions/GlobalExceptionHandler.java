package com.xpay.Exceptions;
import com.xpay.Anotations.RateLimitAspect;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitAspect.RateLimitExceededException.class)
    public ResponseEntity<String> handleRateLimitExceeded(RateLimitAspect.RateLimitExceededException ex) {
        return ResponseEntity.status(429).body(ex.getMessage());
    }
}

