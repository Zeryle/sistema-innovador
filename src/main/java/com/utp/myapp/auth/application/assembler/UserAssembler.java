package com.utp.myapp.auth.application.assembler;

import com.utp.myapp.auth.application.dto.UserDto;
import com.utp.myapp.auth.domain.model.aggregates.User;
import org.springframework.stereotype.Component;

@Component
public class UserAssembler {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail().value())
                .role(user.getRole())
                .tenantId(user.getTenantId().value())
                .active(user.isActive())
                .build();
    }
}
