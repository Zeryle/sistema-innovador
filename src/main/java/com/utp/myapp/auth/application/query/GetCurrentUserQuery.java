package com.utp.myapp.auth.application.query;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GetCurrentUserQuery {
    private final String email;
}
