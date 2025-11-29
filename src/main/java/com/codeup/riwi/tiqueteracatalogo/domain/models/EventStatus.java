package com.codeup.riwi.tiqueteracatalogo.domain.models;

/**
 * Enum representing the possible statuses of an Event.
 * Used for filtering and managing event lifecycle.
 */
public enum EventStatus {
    
    /**
     * Event is scheduled and active - tickets can be sold
     */
    ACTIVE("Activo"),
    
    /**
     * Event has been cancelled - no tickets can be sold
     */
    CANCELLED("Cancelado"),
    
    /**
     * Event has been postponed - waiting for new date
     */
    POSTPONED("Pospuesto"),
    
    /**
     * Event has already taken place
     */
    COMPLETED("Completado"),
    
    /**
     * Event is in draft status - not yet published
     */
    DRAFT("Borrador"),
    
    /**
     * All tickets have been sold
     */
    SOLD_OUT("Agotado");

    private final String displayName;

    EventStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parse a string to EventStatus (case-insensitive)
     * @param value String value to parse
     * @return EventStatus or null if not found
     */
    public static EventStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (EventStatus status : values()) {
            if (status.name().equalsIgnoreCase(value) || 
                status.displayName.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
