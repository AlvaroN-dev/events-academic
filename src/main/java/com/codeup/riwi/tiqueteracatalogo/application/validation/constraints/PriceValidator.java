package com.codeup.riwi.tiqueteracatalogo.application.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for @ValidPrice constraint.
 * Validates that a price is within the specified range.
 */
public class PriceValidator implements ConstraintValidator<ValidPrice, Number> {

    private double min;
    private double max;
    private boolean allowZero;

    @Override
    public void initialize(ValidPrice constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.allowZero = constraintAnnotation.allowZero();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null is handled by @NotNull
        }

        double price = value.doubleValue();

        // Check for zero
        if (price == 0) {
            return allowZero;
        }

        // Check range
        return price >= min && price <= max;
    }
}
