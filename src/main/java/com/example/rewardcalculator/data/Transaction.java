package com.example.rewardcalculator.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private Long id;
    private Long customerId;
    private LocalDate date;
    private BigDecimal amount;

    public Transaction(final Long id, final Long customerId, final LocalDate date, final BigDecimal amount) {
        this.id = id;
        this.customerId = customerId;
        this.date = date;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", date=" + date +
                ", amount=" + amount +
                '}';
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
