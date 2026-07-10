package com.utp.myapp.billing.application.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Returns the public-facing details of a single checkout session, used by
 * the SPA to render the mock payment page.
 */
@Getter
@RequiredArgsConstructor
public class GetCheckoutQuery {
    private final String checkoutSessionId;
}
