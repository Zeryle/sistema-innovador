package com.utp.myapp.vehicle.domain.model.exceptions;

import com.utp.myapp.shared.domain.model.exceptions.BusinessRuleViolationException;

public class DuplicatePlateException extends BusinessRuleViolationException {

    public DuplicatePlateException(String plate) {
        super("A vehicle with license plate '" + plate + "' already exists in this workshop", "DUPLICATE_PLATE");
    }
}
