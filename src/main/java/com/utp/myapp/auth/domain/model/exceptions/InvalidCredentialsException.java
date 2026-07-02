package com.utp.myapp.auth.domain.model.exceptions;

import com.utp.myapp.shared.domain.model.exceptions.DomainException;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Invalid email or password", "AUTH_INVALID_CREDENTIALS");
    }

    public InvalidCredentialsException(String message) {
        super(message, "AUTH_INVALID_CREDENTIALS");
    }
}
