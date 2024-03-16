package com.ferenc.reservation.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ferenc.reservation.exception.CarNotAvailableException;
import com.ferenc.reservation.exception.ErrorResponse;
import com.ferenc.reservation.exception.NoSuchBookingException;
import com.ferenc.reservation.exception.NoSuchCarException;
import com.ferenc.reservation.exception.ValidationErrorModel;
import com.ferenc.reservation.exception.ValidationErrorResponse;

@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        List<ValidationErrorModel> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ValidationErrorModel(err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
                .distinct()
                .collect(Collectors.toList());
        return new ResponseEntity<>(ValidationErrorResponse.builder().errorList(errorMessages).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CarNotAvailableException.class)
    protected ResponseEntity<ErrorResponse> handleCarNotAvailableExceptions(
            CarNotAvailableException ex) {
        return new ResponseEntity<ErrorResponse>(ErrorResponse.builder().message(ex.getMessage()).build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = NoSuchCarException.class)
    protected ResponseEntity<ErrorResponse> handleNoSuchCarExceptions(
            NoSuchCarException ex) {
        return new ResponseEntity<ErrorResponse>(ErrorResponse.builder().message("Not authorized.").build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = NoSuchBookingException.class)
    protected ResponseEntity<ErrorResponse> handleNoSuchBookingExceptions(
            NoSuchBookingException ex) {
        return new ResponseEntity<ErrorResponse>(ErrorResponse.builder().message("Not authorized.").build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedExceptions(
            AccessDeniedException ex) {
        logger.warn(ex.getMessage());
        return new ResponseEntity<ErrorResponse>(ErrorResponse.builder().message("Not authorized.").build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleRuntimeExceptions(RuntimeException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<ErrorResponse>(ErrorResponse.builder().message("Something went wrong, please try again later.").build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
