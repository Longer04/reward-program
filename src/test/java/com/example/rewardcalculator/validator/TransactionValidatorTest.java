package com.example.rewardcalculator.validator;

import com.example.rewardcalculator.data.Transaction;
import com.example.rewardcalculator.validator.exception.InvalidTransactionsOnListException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionValidatorTest {

    private static final Long VALID_CUSTOMER_ID = 1L;
    private static final Long VALID_TRANSACTION_ID = 1L;
    private static final LocalDate VALID_DATE = LocalDate.of(2022, 11, 27);
    private static final BigDecimal VALID_VALUE = BigDecimal.TEN;

    private final static String MISSING_ID_FOR_TRANSACTION = "Missing id for transaction on position %s.";
    private final static String MISSING_FIELD_FOR_TRANSACTION = "Missing %s for transaction with id %s.";
    private final static String DATE = "Date";
    private final static String CUSTOMER_ID = "CustomerId";
    private final static String AMOUNT = "Amount";

    private TransactionValidator transactionValidator;

    @BeforeEach
    public void setUp() {
        transactionValidator = new TransactionValidator();
    }

    @Test
    public void shouldFailMissingTransactionId() {
        final List<Transaction> transactions = Lists.newArrayList();
        transactions.add(getDefaultValidTransactionWithPoints(null, VALID_CUSTOMER_ID, VALID_DATE, VALID_VALUE));

        InvalidTransactionsOnListException thrown = assertThrows(InvalidTransactionsOnListException.class, () -> {
            transactionValidator.validate(transactions);
        });

        final Optional<ValidationContext.ValidationError> first = thrown.getValidationContext().getValidationErrors().stream().findFirst();
        assertTrue(first.isPresent());
        assertThat(first.get().getMessage()).contains(format(MISSING_ID_FOR_TRANSACTION, 0));
    }

    @Test
    public void shouldFailMissingCustomerId() {
        final List<Transaction> transactions = Lists.newArrayList();
        transactions.add(getDefaultValidTransactionWithPoints(VALID_TRANSACTION_ID, null, VALID_DATE, VALID_VALUE));

        InvalidTransactionsOnListException thrown = assertThrows(InvalidTransactionsOnListException.class, () -> {
            transactionValidator.validate(transactions);
        });

        final Optional<ValidationContext.ValidationError> first = thrown.getValidationContext().getValidationErrors().stream().findFirst();
        assertTrue(first.isPresent());
        assertThat(first.get().getMessage()).contains(format(MISSING_FIELD_FOR_TRANSACTION, CUSTOMER_ID, VALID_TRANSACTION_ID));
    }

    @Test
    public void shouldFailMissingDate() {
        final List<Transaction> transactions = Lists.newArrayList();
        transactions.add(getDefaultValidTransactionWithPoints(VALID_TRANSACTION_ID, VALID_CUSTOMER_ID, null, VALID_VALUE));

        InvalidTransactionsOnListException thrown = assertThrows(InvalidTransactionsOnListException.class, () -> {
            transactionValidator.validate(transactions);
        });

        final Optional<ValidationContext.ValidationError> first = thrown.getValidationContext().getValidationErrors().stream().findFirst();
        assertTrue(first.isPresent());
        assertThat(first.get().getMessage()).contains(format(MISSING_FIELD_FOR_TRANSACTION, DATE, VALID_TRANSACTION_ID));
    }

    @Test
    public void shouldFailMissingAmount() {
        final List<Transaction> transactions = Lists.newArrayList();
        transactions.add(getDefaultValidTransactionWithPoints(VALID_TRANSACTION_ID, VALID_CUSTOMER_ID, VALID_DATE, null));

        InvalidTransactionsOnListException thrown = assertThrows(InvalidTransactionsOnListException.class, () -> {
            transactionValidator.validate(transactions);
        });

        final Optional<ValidationContext.ValidationError> first = thrown.getValidationContext().getValidationErrors().stream().findFirst();
        assertTrue(first.isPresent());
        assertThat(first.get().getMessage()).contains(format(MISSING_FIELD_FOR_TRANSACTION, AMOUNT, VALID_TRANSACTION_ID));
    }

    @Test
    public void shouldPassValidation() {
        final List<Transaction> transactions = Lists.newArrayList();
        transactions.add(getDefaultValidTransactionWithPoints(VALID_TRANSACTION_ID, VALID_CUSTOMER_ID, VALID_DATE, VALID_VALUE));

        transactionValidator.validate(transactions);
    }

    @Test
    public void shouldGenerateMultipleErrors() {
        final List<Transaction> transactions = Lists.newArrayList();
        transactions.add(getDefaultValidTransactionWithPoints(VALID_TRANSACTION_ID, VALID_CUSTOMER_ID, null, VALID_VALUE));
        transactions.add(getDefaultValidTransactionWithPoints(VALID_TRANSACTION_ID, VALID_CUSTOMER_ID, VALID_DATE, null));
        transactions.add(getDefaultValidTransactionWithPoints(null, VALID_CUSTOMER_ID, VALID_DATE, VALID_VALUE));
        transactions.add(getDefaultValidTransactionWithPoints(VALID_TRANSACTION_ID, null, VALID_DATE, VALID_VALUE));

        InvalidTransactionsOnListException thrown = assertThrows(InvalidTransactionsOnListException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertThat(thrown.getValidationContext().getValidationErrors().size()).isEqualTo(4);
    }

    private Transaction getDefaultValidTransactionWithPoints(final Long id, final Long customerId, final LocalDate date, final BigDecimal amount) {
        return new Transaction(id, customerId, date, amount);
    }
}