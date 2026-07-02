package com.utp.myapp.auth.application.query;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ListUsersByTenantQuery {
    private final String tenantId;
}
