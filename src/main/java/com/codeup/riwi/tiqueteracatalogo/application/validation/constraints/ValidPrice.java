package com.codeup.riwi.tiqueteracatalogo.application.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation constraint to ensure price is within a valid range.
 * More flexible than @DecimalMin/@DecimalMax as it provides
 * a single annotation for both bounds with custom messaging.
 * 
 * Usage:
 * @ValidPrice(min = 0.01, max = 999999.99)
 * private Double price;
 */
@Documented
@Constraint(validatedBy = PriceValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrice {

    String message() default "{validation.price.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum price value (inclusive)
     */
    double min() default 0.01;

    /**
     * Maximum price value (inclusive)
     */
    double max() default 999999999.99;

    /**
     * Allow zero price (for free events)
     */
    boolean allowZero() default false;
}
