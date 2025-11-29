package com.codeup.riwi.tiqueteracatalogo.application.validation;

/**
 * Validation groups for differentiating Create and Update scenarios.
 * 
 * These interfaces are used with Bean Validation's group feature to apply
 * different validation rules depending on the operation context.
 * 
 * Usage:
 * - @NotNull(groups = OnCreate.class) - Only validates on creation
 * - @NotNull(groups = OnUpdate.class) - Only validates on update
 * - @NotNull(groups = {OnCreate.class, OnUpdate.class}) - Validates on both
 * 
 * In controllers:
 * - @Validated(OnCreate.class) for POST operations
 * - @Validated(OnUpdate.class) for PUT operations
 */
public final class ValidationGroups {

    private ValidationGroups() {
        // Utility class - prevent instantiation
    }

    /**
     * Validation group for creation operations (POST).
     * Used when creating new resources where all required fields must be present.
     */
    public interface OnCreate {}

    /**
     * Validation group for update operations (PUT/PATCH).
     * Used when updating existing resources where some fields may be optional.
     */
    public interface OnUpdate {}

    /**
     * Default validation group that applies to all operations.
     * Equivalent to jakarta.validation.groups.Default
     */
    public interface Always {}
}
