package com.utp.myapp.sales.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerSummaryDto {
    private Integer id;
    private String fullName;
    private String dni;
    private String phone;
    private String email;
}
