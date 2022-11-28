package com.example.rewardcalculator.validator;

import com.example.rewardcalculator.data.Transaction;
import com.example.rewardcalculator.validator.exception.InvalidTransactionsOnListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
public class TransactionValidator {

    private static final Logger log = LoggerFactory.getLogger(TransactionValidator.class);

    private final static String MISSING_ID_FOR_TRANSACTION = "Missing id for transaction on position %s.";
    private final static String MISSING_FIELD_FOR_TRANSACTION = "Missing %s for transaction with id %s.";
    private final static String DATE = "Date";
    private final static String CUSTOMER_ID = "CustomerId";
    private final static String AMOUNT = "Amount";
    private final static String ID = "id";


    public void validate(final List<Transaction> transactionList) {
        final ValidationContext validationContext = new ValidationContext();

        final long start = System.currentTimeMillis();
        for (int i = 0; i < transactionList.size(); i++) {
            this.validate(i, transactionList.get(i), validationContext);
        }
        log.info("Validated {} transactions in {} ms.", transactionList.size(), System.currentTimeMillis() - start);

        if (validationContext.hasErrors()) {
            log.error("Provided list of transactions did not pass validation.");
            throw new InvalidTransactionsOnListException(validationContext);
        }
    }

    private void validate(final long position, final Transaction data, final ValidationContext context) {
        final Long transactionId = data.getId();
        if (isNull(transactionId)) {
            context.addValidationError(new ValidationContext.ValidationError(String.format(MISSING_ID_FOR_TRANSACTION, position), ID));
        }
        if (isNull(data.getDate())) {
            context.addValidationError(new ValidationContext.ValidationError(String.format(MISSING_FIELD_FOR_TRANSACTION, DATE, transactionId), DATE));
        }
        if (isNull(data.getAmount())) {
            context.addValidationError(new ValidationContext.ValidationError(String.format(MISSING_FIELD_FOR_TRANSACTION, AMOUNT, transactionId), AMOUNT));
        }
        if (isNull(data.getCustomerId())) {
            context.addValidationError(new ValidationContext.ValidationError(String.format(MISSING_FIELD_FOR_TRANSACTION, CUSTOMER_ID, transactionId), CUSTOMER_ID));
        }
    }
}
