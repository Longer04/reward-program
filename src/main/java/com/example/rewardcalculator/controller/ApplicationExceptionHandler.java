package com.example.rewardcalculator.controller;

import com.example.rewardcalculator.validator.exception.EmptyTransactionListException;
import com.example.rewardcalculator.validator.exception.InvalidTransactionsOnListException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(InvalidTransactionsOnListException.class)
    public ResponseEntity<Map<String, String>> handleException(final InvalidTransactionsOnListException e) {
        final Map<String, String> errorResponse = new HashMap<>();
        final List<String> reasons = new ArrayList<>();
        e.getValidationContext().getValidationErrors().forEach(validationError -> reasons.add(validationError.getMessage()));

        errorResponse.put("message", reasons.toString());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyTransactionListException.class)
    public ResponseEntity<Map<String, String>> handleException(final EmptyTransactionListException e) {
        final Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("message", e.getMessage());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleException(final HttpMessageNotReadableException e) {
        final Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
