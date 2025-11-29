package com.codeup.riwi.tiqueteracatalogo.application.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation constraint to ensure a date is in the future.
 * More flexible than @Future as it allows for custom messages
 * and works with different temporal types.
 * 
 * <p>This annotation is repeatable, allowing different validation rules
 * for different validation groups.</p>
 * 
 * Usage:
 * @FutureDate(message = "{validation.event.date.future}")
 * private LocalDateTime eventDate;
 */
@Documented
@Constraint(validatedBy = FutureDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(FutureDate.List.class)
public @interface FutureDate {

    String message() default "{validation.date.future}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum hours in the future the date must be.
     * Useful for events that require advance booking.
     */
    int minHoursAhead() default 0;

    /**
     * Container annotation for repeatable @FutureDate annotations.
     */
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FutureDate[] value();
    }
}
