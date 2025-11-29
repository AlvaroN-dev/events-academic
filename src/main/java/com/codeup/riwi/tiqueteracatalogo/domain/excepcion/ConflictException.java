package com.codeup.riwi.tiqueteracatalogo.domain.excepcion;

/**
 * Domain exception thrown when there's a conflict with the current state.
 * Pure domain exception - no framework dependencies.
 * 
 * <p>Examples of conflict scenarios:</p>
 * <ul>
 *   <li>Duplicate resource creation</li>
 *   <li>Concurrent modification conflicts</li>
 *   <li>State transition conflicts</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public class ConflictException extends RuntimeException {

    private final String resourceType;
    private final String conflictField;

    public ConflictException(String message) {
        super(message);
        this.resourceType = null;
        this.conflictField = null;
    }

    public ConflictException(String message, String resourceType, String conflictField) {
        super(message);
        this.resourceType = resourceType;
        this.conflictField = conflictField;
    }

    /**
     * Gets the type of resource that had the conflict.
     * @return the resource type, or null if not specified
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Gets the field that caused the conflict.
     * @return the conflict field, or null if not specified
     */
    public String getConflictField() {
        return conflictField;
    }
}
