package com.utp.myapp.shared.domain.model.exceptions;

/**
 * Thrown when a business rule is violated.
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessRuleViolationException(String message, String ruleCode) {
        super(message, "BUSINESS_RULE_VIOLATION." + ruleCode);
    }
}
