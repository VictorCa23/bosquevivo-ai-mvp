package com.solveria.core.shared.exceptions;

import java.util.Map;

public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String ruleKey) {
        super(
                "error.business.rule.violation",
                Map.of("rule", ruleKey),
                "Business rule violated: " + ruleKey);
    }
}
