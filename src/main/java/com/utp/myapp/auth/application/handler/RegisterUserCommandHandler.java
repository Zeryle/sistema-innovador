package com.utp.myapp.auth.application.handler;

import com.utp.myapp.auth.application.assembler.UserAssembler;
import com.utp.myapp.auth.application.command.RegisterUserCommand;
import com.utp.myapp.auth.application.dto.LoginResponseDto;
import com.utp.myapp.auth.application.dto.TenantSummaryDto;
import com.utp.myapp.auth.application.dto.UserDto;
import com.utp.myapp.auth.domain.model.aggregates.User;
import com.utp.myapp.auth.domain.model.exceptions.UserAlreadyExistsException;
import com.utp.myapp.auth.domain.model.repository.IUserRepository;
import com.utp.myapp.auth.domain.model.valueobjects.Role;
import com.utp.myapp.auth.infraestructure.security.JwtTokenProvider;
import com.utp.myapp.shared.domain.model.valueobjects.Email;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUserCommandHandler {

    private final IUserRepository userRepository;
    private final ITenantRepository tenantRepository;
    private final UserAssembler userAssembler;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponseDto handle(RegisterUserCommand command) {
        Email email = Email.of(command.getEmail());

        // Check uniqueness
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(command.getEmail());
        }

        // Create tenant first
        TenantId tenantId = TenantId.generate();
        Tenant tenant = Tenant.create(
                tenantId,
                command.getBusinessName(),
                command.getPhone(),
                SubscriptionPlan.FREE
        );
        tenant = tenantRepository.save(tenant);

        // Create user
        Role role = command.getRole() != null ? command.getRole() : Role.OWNER;
        User user = User.register(email, command.getPassword(), role, tenantId);
        user = userRepository.save(user);

        // Generate JWT
        UserDto userDto = userAssembler.toDto(user);
        String token = jwtTokenProvider.generateToken(userDto);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDto);

        TenantSummaryDto tenantSummary = new TenantSummaryDto(
                tenant.getId().value(),
                tenant.getBusinessName(),
                tenant.getPlan().name()
        );

        return LoginResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userDto)
                .tenant(tenantSummary)
                .build();
    }
}
