package com.codeup.riwi.tiqueteracatalogo.application.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation constraint to ensure event capacity does not exceed venue capacity.
 * This is a cross-field validation at class level.
 * 
 * Note: Since the venue is referenced by ID, this validation should be done
 * at the service layer where we can fetch the venue capacity.
 * This annotation serves as documentation and can be extended for DTO-level checks.
 * 
 * Usage:
 * @ValidCapacity
 * public class EventoRequest { ... }
 */
@Documented
@Constraint(validatedBy = CapacityValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCapacity {

    String message() default "{validation.capacity.exceeded}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The field name for capacity
     */
    String capacityField() default "capacity";

    /**
     * The field name for maximum capacity (if present in DTO)
     */
    String maxCapacityField() default "maxCapacity";
}
