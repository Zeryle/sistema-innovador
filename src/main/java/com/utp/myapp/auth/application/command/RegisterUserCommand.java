package com.utp.myapp.auth.application.command;

import com.utp.myapp.auth.domain.model.valueobjects.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class RegisterUserCommand {
    private final String email;
    private final String password;
    private final String businessName;  // Creates the tenant automatically
    private final String phone;
    private final Role role;
}
