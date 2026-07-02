package com.utp.myapp.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {
    private String token;
    private String refreshToken;
    private UserDto user;
    private TenantSummaryDto tenant;
}
