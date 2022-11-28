package com.example.rewardcalculator.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

public class CustomerReward {

    private Long customerId;
    private Long totalPoints;
    private Map<StartEndDate, Long> pointsPerPeriod;

    public CustomerReward(final Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }

    public Long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(final Long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Map<StartEndDate, Long> getPointsPerPeriod() {
        return pointsPerPeriod;
    }

    public void setPointsPerPeriod(final Map<StartEndDate, Long> pointsPerPeriod) {
        this.pointsPerPeriod = pointsPerPeriod;
    }

    @Override
    public boolean equals(final Object object) {
        return EqualsBuilder.reflectionEquals(this, object, false);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }
}
