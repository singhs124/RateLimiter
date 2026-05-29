package com.sushant.RateLimiter.application.auth.exception;

import com.sushant.RateLimiter.common.dto.ErrorMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMap> handleGeneralException(Exception ex){
        return new ResponseEntity<>(new ErrorMap(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorMap> handleInvalidEmailException(Exception ex){
        return new ResponseEntity<>(new ErrorMap(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(InvalidOTPException.class)
    public ResponseEntity<ErrorMap> handleInvalidOtpException(Exception ex){
        return new ResponseEntity<>(new ErrorMap(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMap> handleUserNotFoundException(UserNotFoundException ex){
        return new ResponseEntity<>(new ErrorMap(ex.getMessage(),HttpStatus.NOT_FOUND),HttpStatus.NOT_FOUND);
    }
}
