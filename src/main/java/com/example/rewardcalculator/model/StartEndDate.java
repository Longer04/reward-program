package com.example.rewardcalculator.model;

import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

public class StartEndDate {

    private LocalDate start;
    private LocalDate end;

    public StartEndDate(final LocalDate start, final LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(final LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(final LocalDate end) {
        this.end = end;
    }

    public boolean contains(final LocalDate date) {
        return date.isEqual(start) || date.isEqual(end) ||
                (date.isAfter(end) && date.isBefore(start));
    }

    @Override
    public String toString() {
        return "StartEndDate{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartEndDate that = (StartEndDate) o;
        return Objects.equal(start, that.start) && Objects.equal(end, that.end);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }
}
