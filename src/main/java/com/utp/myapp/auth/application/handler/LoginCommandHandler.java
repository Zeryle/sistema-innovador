package com.utp.myapp.auth.application.handler;

import com.utp.myapp.auth.application.assembler.UserAssembler;
import com.utp.myapp.auth.application.command.LoginCommand;
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
public class LoginCommandHandler {

    private final IUserRepository userRepository;
    private final ITenantRepository tenantRepository;
    private final UserAssembler userAssembler;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public LoginResponseDto handle(LoginCommand command) {
        Email email = Email.of(command.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new InvalidCredentialsException("Account is deactivated");
        }

        if (!user.verifyPassword(command.getPassword())) {
            throw new InvalidCredentialsException();
        }

        UserDto userDto = userAssembler.toDto(user);
        String token = jwtTokenProvider.generateToken(userDto);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDto);

        Tenant tenant = tenantRepository.findById(user.getTenantId())
                .orElse(null);

        TenantSummaryDto tenantSummary = null;
        if (tenant != null) {
            tenantSummary = new TenantSummaryDto(
                    tenant.getId().value(),
                    tenant.getBusinessName(),
                    tenant.getPlan().name()
            );
        }

        return LoginResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userDto)
                .tenant(tenantSummary)
                .build();
    }
}
