package com.utp.myapp.auth.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class RefreshTokenCommand {
    private final String refreshToken;
}
