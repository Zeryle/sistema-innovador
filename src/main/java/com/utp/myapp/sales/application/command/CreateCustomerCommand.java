package com.utp.myapp.sales.application.command;

import com.utp.myapp.sales.application.dto.AddressDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CreateCustomerCommand {
    private final String name;
    private final String lastName;
    private final String dni;
    private final String email;
    private final String phone;
    private final String notes;
    private final AddressDto address;
}
