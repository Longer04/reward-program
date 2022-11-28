package com.example.rewardcalculator.validator.exception;

import com.example.rewardcalculator.validator.ValidationContext;

import java.util.Objects;

public class InvalidTransactionsOnListException extends RuntimeException {

    private final ValidationContext validationContext;

    public InvalidTransactionsOnListException(final ValidationContext validationContext1) {
        this.validationContext = validationContext1;
    }

    public ValidationContext getValidationContext() {
        return validationContext;
    }

    @Override
    public String toString() {
        return Objects.toString(validationContext);
    }
}
