package com.utp.myapp.auth.application.command;

import com.utp.myapp.auth.domain.model.valueobjects.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class UpdateUserRoleCommand {
    private final Long userId;
    private final Role newRole;
}
