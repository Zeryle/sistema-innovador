package com.utp.myapp.auth.domain.model.exceptions;

import com.utp.myapp.shared.domain.model.exceptions.BusinessRuleViolationException;

public class UserAlreadyExistsException extends BusinessRuleViolationException {

    public UserAlreadyExistsException(String email) {
        super("A user with email '" + email + "' already exists", "USER_DUPLICATE");
    }
}
