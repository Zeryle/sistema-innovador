package com.utp.myapp.sales.application.assembler;

import com.utp.myapp.sales.application.dto.AddressDto;
import com.utp.myapp.sales.domain.model.valueobjets.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressAssembler {

    public AddressDto toDto(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDto(
                address.street(),
                address.city(),
                address.number(),
                address.country()
        );
    }

    public Address toDomain(AddressDto dto){
        if (dto == null) {
            return null;
        }
        return new Address(dto.getStreet(),dto.getNumber(),
                dto.getCity(),dto.getCountry());
        }
}
