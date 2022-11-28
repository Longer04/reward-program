package com.example.rewardcalculator.validator;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ValidationContext implements Serializable {

    public static class ValidationError implements Serializable {
        private String message;
        private String field;

        public ValidationError(String message, String field) {
            this.message = message;
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return "ValidationError{" +
                    "message='" + message + '\'' +
                    ", field='" + field + '\'' +
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

    private static final long serialVersionUID = 1L;

    private final List<ValidationError> validationErrors;

    public ValidationContext() {
        this.validationErrors = Lists.newArrayList();
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void addValidationError(final ValidationError error) {
        validationErrors.add(error);
    }

    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (hasErrors()) {
            sb.append("Validation errors:").append(System.lineSeparator());
            validationErrors.forEach(error -> sb.append(error.getMessage()).append(System.lineSeparator()));
        }
        if (sb.length() == 0) {
            sb.append("No errors.");
        }
        return sb.toString();
    }
}
