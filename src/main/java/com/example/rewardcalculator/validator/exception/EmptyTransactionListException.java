package com.example.rewardcalculator.validator.exception;

public class EmptyTransactionListException extends RuntimeException {

    private final String message;

    public EmptyTransactionListException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
