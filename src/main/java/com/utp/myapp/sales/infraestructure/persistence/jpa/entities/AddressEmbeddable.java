package com.utp.myapp.sales.infraestructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEmbeddable {
    @Column
    private String street;
    @Column
    private String number;
    @Column
    private String city;
    @Column
    private String country;
}
