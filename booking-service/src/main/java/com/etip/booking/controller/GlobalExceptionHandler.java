package com.etip.booking.controller;

import com.etip.booking.dto.ErrorInfo;
import com.etip.booking.dto.ValidationErrorResponse;
import com.etip.booking.exception.BookingNotFoundException;
import com.etip.booking.exception.InvalidBookingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.etip.booking.dto.ErrorResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFound(BookingNotFoundException ex, HttpServletRequest request){
        ErrorResponse error =  new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setErrorCode("BOOKING NOT FOUND");
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ErrorResponse> invalidBooking(InvalidBookingException ex, HttpServletRequest request){
        ErrorResponse error =  new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setErrorCode("BOOKING NOT ALLOWED");
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request){
        ValidationErrorResponse error =  new ValidationErrorResponse();
        List<ErrorInfo> errInfo = new ArrayList<>();
        List<FieldError> fieldErrors= ex.getBindingResult().getFieldErrors();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrorCode("VALIDATION_ERROR");

        for(FieldError fieldError:fieldErrors)  {
            ErrorInfo err = new ErrorInfo();
            err.setField(fieldError.getField());
            err.setMessage(fieldError.getDefaultMessage());
            errInfo.add(err);
        }

        error.setErrorInfos(errInfo);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
