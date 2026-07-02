package com.utp.myapp.sales.application.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto {
    private String street;
    private String number;
    private String city;
    private String country;

}
