package com.utp.myapp.auth.interfaces.rest;

import com.utp.myapp.auth.application.command.LoginCommand;
import com.utp.myapp.auth.application.command.RefreshTokenCommand;
import com.utp.myapp.auth.application.command.RegisterUserCommand;
import com.utp.myapp.auth.application.dto.LoginResponseDto;
import com.utp.myapp.auth.application.dto.RegisterRequestDto;
import com.utp.myapp.auth.application.dto.UserDto;
import com.utp.myapp.auth.application.handler.GetCurrentUserQueryHandler;
import com.utp.myapp.auth.application.handler.LoginCommandHandler;
import com.utp.myapp.auth.application.handler.RefreshTokenCommandHandler;
import com.utp.myapp.auth.application.handler.RegisterUserCommandHandler;
import com.utp.myapp.auth.application.query.GetCurrentUserQuery;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserCommandHandler registerHandler;
    private final LoginCommandHandler loginHandler;
    private final RefreshTokenCommandHandler refreshHandler;
    private final GetCurrentUserQueryHandler getCurrentUserHandler;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponseDto>> register(
            @Valid @RequestBody RegisterRequestDto request) {
        RegisterUserCommand command = RegisterUserCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .businessName(request.getBusinessName())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();

        LoginResponseDto response = registerHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginCommand command) {
        LoginResponseDto response = loginHandler.handle(command);
        return ResponseEntity.ok(ApiResponse.ok(response, "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refresh(
            @Valid @RequestBody RefreshTokenCommand command) {
        LoginResponseDto response = refreshHandler.handle(command);
        return ResponseEntity.ok(ApiResponse.ok(response, "Token refreshed"));
    }

    /**
     * Returns the currently authenticated user, resolved from the JWT subject
     * (email) injected by {@link com.utp.myapp.auth.infraestructure.security.JwtAuthenticationFilter}.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }
        UserDto user = getCurrentUserHandler.handle(
                GetCurrentUserQuery.builder().email(authentication.getName()).build());
        return ResponseEntity.ok(ApiResponse.ok(user, "Current user"));
    }
}
