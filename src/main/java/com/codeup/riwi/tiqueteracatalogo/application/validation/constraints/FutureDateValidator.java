package com.codeup.riwi.tiqueteracatalogo.application.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

/**
 * Validator for @FutureDate constraint.
 * Validates that a LocalDateTime is in the future by at least the specified hours.
 */
public class FutureDateValidator implements ConstraintValidator<FutureDate, LocalDateTime> {

    private int minHoursAhead;

    @Override
    public void initialize(FutureDate constraintAnnotation) {
        this.minHoursAhead = constraintAnnotation.minHoursAhead();
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null is handled by @NotNull
        }

        LocalDateTime threshold = LocalDateTime.now().plusHours(minHoursAhead);
        return value.isAfter(threshold);
    }
}
