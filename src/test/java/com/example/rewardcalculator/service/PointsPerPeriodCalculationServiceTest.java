package com.example.rewardcalculator.service;

import com.example.rewardcalculator.data.Transaction;
import com.example.rewardcalculator.model.CustomerReward;
import com.example.rewardcalculator.validator.exception.EmptyTransactionListException;
import com.example.rewardcalculator.validator.exception.InvalidTransactionsOnListException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
class PointsPerPeriodCalculationServiceTest {

    private static final Long CUSTOMER_ID_1 = 1L;
    private static final Long CUSTOMER_ID_2 = 2L;
    private static final LocalDate STANDARD_DATE = LocalDate.of(2022, 11, 27);

    private static final String EMPTY_TRANSACTION_LIST_SENT_FOR_CALCULATION_ERROR = "Provided transaction list is empty.";

    @Autowired
    private RewardCalculationService rewardCalculationService;


    @Test
    void shouldThrowVEmptyTransactionListException() {
        final List<Transaction> transactions = new ArrayList<>();

        final EmptyTransactionListException thrown = Assertions.assertThrows(EmptyTransactionListException.class, () -> {
            rewardCalculationService.processRewardCalculation(transactions);
        });
        assertThat(thrown.getMessage()).isEqualTo(EMPTY_TRANSACTION_LIST_SENT_FOR_CALCULATION_ERROR);
    }

    @Test
    void shouldThrowInvalidTransactionsOnListExceptionWithFourErrors() {
        final List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(null, 2L, LocalDate.of(2022, 11, 27), BigDecimal.TEN));
        transactions.add(new Transaction(1L, null, LocalDate.of(2022, 11, 27), BigDecimal.TEN));
        transactions.add(new Transaction(2L, 2L, null, BigDecimal.TEN));
        transactions.add(new Transaction(2L, 2L, LocalDate.of(2022, 11, 27), null));

        final InvalidTransactionsOnListException thrown = Assertions.assertThrows(InvalidTransactionsOnListException.class, () -> {
            rewardCalculationService.processRewardCalculation(transactions);
        });
        assertThat(thrown.getValidationContext().getValidationErrors().size()).isEqualTo(4);
    }

    @Test
    void bothCustomersShouldHavePointsAssigned() {
        final List<Transaction> transactions = Lists.newArrayList();
        transactions.add(getDefaultValidTransactionWithPoints(1,CUSTOMER_ID_1, STANDARD_DATE));
        transactions.add(getDefaultValidTransactionWithPoints(1,CUSTOMER_ID_2, STANDARD_DATE.minusMonths(1)));

        final Collection<CustomerReward> result = rewardCalculationService.processRewardCalculation(transactions);
        final Optional<CustomerReward> customerRewardForCustomer1 = result.stream()
                .filter(reward -> reward.getCustomerId().equals(CUSTOMER_ID_1)).findFirst();
        final Optional<CustomerReward> customerRewardForCustomer2 = result.stream()
                .filter(reward -> reward.getCustomerId().equals(CUSTOMER_ID_2)).findFirst();

        assertTrue(customerRewardForCustomer1.isPresent());
        assertTrue(customerRewardForCustomer2.isPresent());
        assertThat(customerRewardForCustomer1.get().getTotalPoints()).isEqualTo(25);
        assertThat(customerRewardForCustomer2.get().getTotalPoints()).isEqualTo(25);
        customerRewardForCustomer1.get().getPointsPerPeriod().forEach((key, value) -> assertThat(value).isEqualTo(25L));
    }


    @Test
    void allPeriodsShouldHaveZeroPoints() {
        final List<Transaction> transactions = Lists.newArrayList();
        transactions.add(getDefaultValidTransactionWithoutPoints(1,CUSTOMER_ID_2, STANDARD_DATE));
        transactions.add(getDefaultValidTransactionWithPoints(1,CUSTOMER_ID_2, STANDARD_DATE.minusMonths(4)));
        transactions.add(getDefaultValidTransactionWithPoints(1,CUSTOMER_ID_2, STANDARD_DATE.minusMonths(5)));

        final Collection<CustomerReward> result = rewardCalculationService.processRewardCalculation(transactions);
        final Optional<CustomerReward> customerReward = result.stream()
                .filter(reward -> reward.getCustomerId().equals(CUSTOMER_ID_2)).findFirst();

        assertTrue(customerReward.isPresent());
        assertThat(customerReward.get().getTotalPoints()).isEqualTo(50);
        assertThat(customerReward.get().getPointsPerPeriod().entrySet().size()).isEqualTo(3);
    }

    @Test
    void shouldNotIncludePointsForRoundedAmount() {
        final List<Transaction> transactions = Lists.newArrayList();
        final Transaction validTransaction1 = getDefaultValidTransactionWithPoints(1,CUSTOMER_ID_2, STANDARD_DATE);
        final Transaction validTransaction2 = getDefaultValidTransactionWithPoints(1,CUSTOMER_ID_2, STANDARD_DATE.minusMonths(1));
        validTransaction1.setAmount(BigDecimal.valueOf(99.99));
        validTransaction2.setAmount(BigDecimal.valueOf(100.99));
        transactions.add(validTransaction1);
        transactions.add(validTransaction2);

        final Collection<CustomerReward> result = rewardCalculationService.processRewardCalculation(transactions);
        final Optional<CustomerReward> customerReward = result.stream()
                .filter(reward -> reward.getCustomerId().equals(CUSTOMER_ID_2)).findFirst();

        assertTrue(customerReward.isPresent());
        assertThat(customerReward.get().getTotalPoints()).isEqualTo(99);
        assertThat(customerReward.get().getPointsPerPeriod().entrySet().size()).isEqualTo(3);
    }

    @Test
    void shouldAssignPointsWhenValueOnThreshold() {
        final List<Transaction> transactions = Lists.newArrayList();
        final Transaction validTransaction1 = getDefaultValidTransactionWithPoints(1,CUSTOMER_ID_2, STANDARD_DATE);
        validTransaction1.setAmount(BigDecimal.valueOf(100));
        transactions.add(validTransaction1);

        final Collection<CustomerReward> result = rewardCalculationService.processRewardCalculation(transactions);
        final Optional<CustomerReward> customerReward = result.stream()
                .filter(reward -> reward.getCustomerId().equals(CUSTOMER_ID_2)).findFirst();

        assertTrue(customerReward.isPresent());
        assertThat(customerReward.get().getTotalPoints()).isEqualTo(50);
        assertThat(customerReward.get().getPointsPerPeriod().entrySet().size()).isEqualTo(3);
    }

    @Test
    void everyPeriodShouldHaveEqualNumberOfPoints() {
        final List<Transaction> transactions = Lists.newArrayList();
        transactions.add(getDefaultValidTransactionWithPoints(1,CUSTOMER_ID_2, STANDARD_DATE));
        transactions.add(getDefaultValidTransactionWithPoints(1,CUSTOMER_ID_2, STANDARD_DATE.minusMonths(1)));
        transactions.add(getDefaultValidTransactionWithPoints(1,CUSTOMER_ID_2, STANDARD_DATE.minusMonths(2)));

        final Collection<CustomerReward> result = rewardCalculationService.processRewardCalculation(transactions);
        final Optional<CustomerReward> customerReward = result.stream()
                .filter(reward -> reward.getCustomerId().equals(CUSTOMER_ID_2)).findFirst();

        assertTrue(customerReward.isPresent());
        assertThat(customerReward.get().getTotalPoints()).isEqualTo(75L);
        assertThat(customerReward.get().getPointsPerPeriod().entrySet().size()).isEqualTo(3);
        customerReward.get().getPointsPerPeriod().forEach((startEndDate, points) -> assertThat(points).isEqualTo(25L));
    }

    private Transaction getDefaultValidTransactionWithoutPoints(final long id, final long customerId, final LocalDate date){
        return new Transaction(id, customerId, date, BigDecimal.TEN);
    }

    private Transaction getDefaultValidTransactionWithPoints(final long id, final long customerId, final LocalDate date){
        return new Transaction(id, customerId, date, BigDecimal.valueOf(75));
    }

}