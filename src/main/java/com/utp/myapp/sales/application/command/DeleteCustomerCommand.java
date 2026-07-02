package com.utp.myapp.sales.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class DeleteCustomerCommand {
    private final Integer id;
}
