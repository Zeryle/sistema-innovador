package com.utp.myapp.sales.application.query;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class SearchCustomersQuery {
    private final String query;
    private final int page;
    private final int size;
}
