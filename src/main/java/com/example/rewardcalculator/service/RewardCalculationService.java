package com.example.rewardcalculator.service;

import com.example.rewardcalculator.data.Transaction;
import com.example.rewardcalculator.model.CustomerReward;
import com.example.rewardcalculator.model.StartEndDate;
import com.example.rewardcalculator.validator.TransactionValidator;
import com.example.rewardcalculator.validator.exception.EmptyTransactionListException;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.google.common.collect.Iterables.isEmpty;

@Service
public class RewardCalculationService {

    private static final Logger log = LoggerFactory.getLogger(RewardCalculationService.class);

    private static final String EMPTY_TRANSACTION_LIST_SENT_FOR_CALCULATION_ERROR = "Provided transaction list is empty.";

    private final TransactionValidator validator;

    @Value("${calculator.periods}")
    private long periods;
    @Value("${calculator.firstRewardThreshold}")
    private long firstRewardThreshold;
    @Value("${calculator.secondRewardThreshold}")
    private long secondRewardThreshold;
    @Value("${calculator.firstRewardThresholdPoints}")
    private long firstRewardThresholdPoints;
    @Value("${calculator.secondRewardThresholdPoints}")
    private long secondRewardThresholdPoints;

    public RewardCalculationService(@Autowired TransactionValidator validator) {
        this.validator = validator;
    }


    public Collection<CustomerReward> processRewardCalculation(final List<Transaction> transactions) {
        long outOfDefinedDatesCount = 0;
        LocalDate startDate = null;
        LocalDate endDate = null;

        final Map<Long, CustomerReward> rewardsByCustomer = new HashMap<>();

        if (isEmpty(transactions)) {
            log.error(EMPTY_TRANSACTION_LIST_SENT_FOR_CALCULATION_ERROR);
            throw new EmptyTransactionListException(EMPTY_TRANSACTION_LIST_SENT_FOR_CALCULATION_ERROR);
        }

        validator.validate(transactions);

        for (final Transaction transaction : transactions) {
            final LocalDate transactionDate = transaction.getDate();
            if (startDate == null || transactionDate.isAfter(startDate)) {
                startDate = transactionDate;
            }
            if (endDate == null || transactionDate.isBefore(endDate)) {
                endDate = transactionDate;
            }
        }
        final List<StartEndDate> definedPeriods = prepareDefinedPeriods(startDate);

        for (final Transaction transaction : transactions) {
            final Optional<StartEndDate> period = calculateStartEndDate(transaction.getDate(), definedPeriods);
            final long points = processRewardCalculation(transaction);

            if (!rewardsByCustomer.containsKey(transaction.getCustomerId())) {
                defineRewardMapForNewCustomer(rewardsByCustomer, definedPeriods, transaction.getCustomerId());
            }

            if (period.isPresent() &&
                    rewardsByCustomer.get(transaction.getCustomerId()).getPointsPerPeriod()
                            .containsKey(period.get())) {

                final Long currentTotalPoints = rewardsByCustomer.get(transaction.getCustomerId()).getTotalPoints();
                rewardsByCustomer.get(transaction.getCustomerId()).setTotalPoints(points + currentTotalPoints);
                rewardsByCustomer.get(transaction.getCustomerId()).getPointsPerPeriod()
                        .computeIfPresent(period.get(), (givenPeriod, currentPoints) -> currentPoints + points);
            } else {
                final Long currentTotalPoints = rewardsByCustomer.get(transaction.getCustomerId()).getTotalPoints();
                rewardsByCustomer.get(transaction.getCustomerId()).setTotalPoints(points + currentTotalPoints);
                if (period.isPresent()) {
                    rewardsByCustomer.get(transaction.getCustomerId()).getPointsPerPeriod()
                            .put(period.get(), points);
                } else {
                    outOfDefinedDatesCount++;
                }
            }
        }

        if (outOfDefinedDatesCount > 0) {
            log.info("{} transactions were out of defined dates.", outOfDefinedDatesCount);
        }
        return rewardsByCustomer.values();
    }

    private void defineRewardMapForNewCustomer(final Map<Long, CustomerReward> rewardsByCustomer,
                                               final List<StartEndDate> definedPeriods,
                                               final Long customerId) {
        final Map<StartEndDate, Long> pointsPerPeriod = Maps.newHashMap();
        definedPeriods.forEach((StartEndDate p) -> {
            pointsPerPeriod.put(p, 0L);
        });
        final CustomerReward cr = new CustomerReward(customerId);
        cr.setPointsPerPeriod(pointsPerPeriod);
        cr.setTotalPoints(0L);
        rewardsByCustomer.put(customerId, cr);
    }


    private long processRewardCalculation(final Transaction transaction) {
        final Long transactionAmount = transaction.getAmount().longValue();
        if (transactionAmount.compareTo(firstRewardThreshold) >= 0
                && transactionAmount.compareTo(secondRewardThreshold) <= 0) {
            return (transactionAmount - firstRewardThreshold) * firstRewardThresholdPoints;
        } else if (transactionAmount.compareTo(secondRewardThreshold) > 0) {
            return (transactionAmount - secondRewardThreshold)
                    * secondRewardThresholdPoints
                    + (secondRewardThreshold - firstRewardThreshold);
        } else
            return 0;
    }

    private Optional<StartEndDate> calculateStartEndDate(final LocalDate date, final List<StartEndDate> startEndDates) {
        return startEndDates.stream().filter(startEndDate -> startEndDate.contains(date)).findFirst();
    }

    private List<StartEndDate> prepareDefinedPeriods(final LocalDate startDate) {
        final List<StartEndDate> periods = new ArrayList<>();
        for (int i = 1; i <= this.periods; i++) {
            periods.add(new StartEndDate(startDate.minusMonths(i - 1), startDate.minusMonths(i).plusDays(1)));
        }
        return periods;
    }
}
