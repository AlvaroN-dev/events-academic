package com.codeup.riwi.tiqueteracatalogo.application.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation constraint for date range validation.
 * Ensures that startDate is before endDate when both are present.
 * 
 * This is a cross-field validation that validates at the class level.
 * 
 * Usage:
 * @ValidDateRange(startField = "startDate", endField = "endDate")
 * public class EventRequest { ... }
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {

    String message() default "{validation.date.range.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The name of the start date field
     */
    String startField();

    /**
     * The name of the end date field
     */
    String endField();

    /**
     * Whether to allow equal dates
     */
    boolean allowEqual() default true;

    /**
     * Defines several @ValidDateRange annotations on the same element.
     */
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        ValidDateRange[] value();
    }
}
