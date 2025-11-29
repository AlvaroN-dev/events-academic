package com.codeup.riwi.tiqueteracatalogo.domain.excepcion;

/**
 * Domain exception thrown when a business rule is violated.
 * Pure domain exception - no framework dependencies.
 * 
 * <p>Examples of business rule violations:</p>
 * <ul>
 *   <li>Event capacity exceeds venue capacity</li>
 *   <li>Attempting to modify a cancelled event</li>
 *   <li>Deleting a venue with active events</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public class BusinessRuleViolationException extends RuntimeException {

    private final String ruleCode;

    public BusinessRuleViolationException(String message) {
        super(message);
        this.ruleCode = null;
    }

    public BusinessRuleViolationException(String message, String ruleCode) {
        super(message);
        this.ruleCode = ruleCode;
    }

    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
        this.ruleCode = null;
    }

    /**
     * Gets the business rule code that was violated.
     * @return the rule code, or null if not specified
     */
    public String getRuleCode() {
        return ruleCode;
    }
}
