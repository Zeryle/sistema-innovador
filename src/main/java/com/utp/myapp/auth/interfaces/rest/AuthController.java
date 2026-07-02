package com.utp.myapp.auth.interfaces.rest;

import com.utp.myapp.auth.application.command.LoginCommand;
import com.utp.myapp.auth.application.command.RegisterUserCommand;
import com.utp.myapp.auth.application.dto.LoginResponseDto;
import com.utp.myapp.auth.application.dto.RegisterRequestDto;
import com.utp.myapp.auth.application.handler.LoginCommandHandler;
import com.utp.myapp.auth.application.handler.RegisterUserCommandHandler;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserCommandHandler registerHandler;
    private final LoginCommandHandler loginHandler;

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
            @RequestBody LoginCommand command) {
        LoginResponseDto response = loginHandler.handle(command);
        return ResponseEntity.ok(ApiResponse.ok(response, "Login successful"));
    }
}
