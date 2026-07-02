package com.utp.myapp.sales.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDto {
    private Integer id;
    private String name;
    private String lastName;
    private String dni;
    private String email;
    private String phone;
    private String tenantId;
    private String notes;
    private String profileImageUrl;
    private AddressDto address;
}
