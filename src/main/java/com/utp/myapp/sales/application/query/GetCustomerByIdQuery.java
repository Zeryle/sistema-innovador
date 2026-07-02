package com.utp.myapp.sales.application.query;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GetCustomerByIdQuery {

    private final Integer id;

}
