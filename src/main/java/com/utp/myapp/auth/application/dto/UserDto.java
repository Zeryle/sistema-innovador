package com.utp.myapp.auth.application.dto;

import com.utp.myapp.auth.domain.model.valueobjects.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String email;
    private Role role;
    private String tenantId;
    private boolean active;
}
