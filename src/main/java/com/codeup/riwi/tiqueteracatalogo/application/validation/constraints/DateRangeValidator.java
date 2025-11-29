package com.codeup.riwi.tiqueteracatalogo.application.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

/**
 * Validator for @ValidDateRange constraint.
 * Validates that a start date field is before an end date field.
 * 
 * Supports:
 * - LocalDateTime
 * - LocalDate
 * - Any Comparable temporal type
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startField;
    private String endField;
    private boolean allowEqual;
    private String message;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
        this.allowEqual = constraintAnnotation.allowEqual();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true; // Null objects are handled by @NotNull
        }

        try {
            BeanWrapper beanWrapper = new BeanWrapperImpl(object);
            Object startValue = beanWrapper.getPropertyValue(startField);
            Object endValue = beanWrapper.getPropertyValue(endField);

            // If either field is null, skip validation (handled by @NotNull)
            if (startValue == null || endValue == null) {
                return true;
            }

            // Validate that both are Comparable
            if (!(startValue instanceof Comparable) || !(endValue instanceof Comparable)) {
                return true;
            }

            @SuppressWarnings("unchecked")
            Comparable<Object> start = (Comparable<Object>) startValue;
            int comparison = start.compareTo(endValue);

            boolean isValid = allowEqual ? comparison <= 0 : comparison < 0;

            if (!isValid) {
                // Disable default constraint violation
                context.disableDefaultConstraintViolation();
                
                // Add custom constraint violation to the end field
                context.buildConstraintViolationWithTemplate(message)
                       .addPropertyNode(endField)
                       .addConstraintViolation();
            }

            return isValid;

        } catch (Exception e) {
            // If we can't access the fields, validation passes
            // (let other validators handle it)
            return true;
        }
    }
}
