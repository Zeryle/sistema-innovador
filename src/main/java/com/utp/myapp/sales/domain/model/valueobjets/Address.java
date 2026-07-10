package com.utp.myapp.sales.domain.model.valueobjets;

/**
 * Domain value object representing a customer's address.
 *
 * All fields are optional — the workshop record may exist without a complete address.
 * The persistence layer (AddressEmbeddable) accepts null values accordingly.
 *
 * Field order matches the persistence columns (street, number, city, country).
 */
public record Address(String street, String number, String city, String country) {

    public Address {
        // Address is optional in this domain. We allow null/empty fields; callers
        // (validators in the application layer) are responsible for deciding when
        // an address is "complete enough" for a given business rule.
    }
}
