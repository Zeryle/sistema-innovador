package com.utp.myapp.auth.application.handler;

import com.utp.myapp.auth.application.assembler.UserAssembler;
import com.utp.myapp.auth.application.command.RefreshTokenCommand;
import com.utp.myapp.auth.application.dto.LoginResponseDto;
import com.utp.myapp.auth.application.dto.TenantSummaryDto;
import com.utp.myapp.auth.application.dto.UserDto;
import com.utp.myapp.auth.domain.model.aggregates.User;
import com.utp.myapp.auth.domain.model.exceptions.InvalidCredentialsException;
import com.utp.myapp.auth.domain.model.repository.IUserRepository;
import com.utp.myapp.auth.infraestructure.security.JwtTokenProvider;
import com.utp.myapp.shared.domain.model.valueobjects.Email;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenCommandHandler {

    private final IUserRepository userRepository;
    private final ITenantRepository tenantRepository;
    private final UserAssembler userAssembler;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public LoginResponseDto handle(RefreshTokenCommand command) {
        String refreshToken = command.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidCredentialsException("Refresh token is required");
        }
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("Refresh token is invalid or expired");
        }

        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"REFRESH".equals(tokenType)) {
            throw new InvalidCredentialsException("Provided token is not a refresh token");
        }

        String emailValue = jwtTokenProvider.getEmailFromToken(refreshToken);
        Email email = Email.of(emailValue);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User no longer exists"));

        if (!user.isActive()) {
            throw new InvalidCredentialsException("Account is deactivated");
        }

        UserDto userDto = userAssembler.toDto(user);
        String newToken = jwtTokenProvider.generateToken(userDto);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDto);

        Tenant tenant = tenantRepository.findById(user.getTenantId()).orElse(null);
        TenantSummaryDto tenantSummary = null;
        if (tenant != null) {
            tenantSummary = new TenantSummaryDto(
                    tenant.getId().value(),
                    tenant.getBusinessName(),
                    tenant.getPlan().name()
            );
        }

        return LoginResponseDto.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .user(userDto)
                .tenant(tenantSummary)
                .build();
    }
}
