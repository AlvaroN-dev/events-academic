package com.codeup.riwi.tiqueteracatalogo.application.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Validator for @ValidCapacity constraint.
 * Validates that capacity does not exceed max capacity when both are present in DTO.
 */
public class CapacityValidator implements ConstraintValidator<ValidCapacity, Object> {

    private String capacityField;
    private String maxCapacityField;
    private String message;

    @Override
    public void initialize(ValidCapacity constraintAnnotation) {
        this.capacityField = constraintAnnotation.capacityField();
        this.maxCapacityField = constraintAnnotation.maxCapacityField();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true;
        }

        try {
            BeanWrapper beanWrapper = new BeanWrapperImpl(object);
            
            // Check if maxCapacityField exists in the DTO
            if (!beanWrapper.isReadableProperty(maxCapacityField)) {
                return true; // Field doesn't exist, skip validation
            }

            Object capacityValue = beanWrapper.getPropertyValue(capacityField);
            Object maxCapacityValue = beanWrapper.getPropertyValue(maxCapacityField);

            // If either is null, skip validation
            if (capacityValue == null || maxCapacityValue == null) {
                return true;
            }

            Integer capacity = (Integer) capacityValue;
            Integer maxCapacity = (Integer) maxCapacityValue;

            boolean isValid = capacity <= maxCapacity;

            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                       .addPropertyNode(capacityField)
                       .addConstraintViolation();
            }

            return isValid;

        } catch (Exception e) {
            return true;
        }
    }
}
