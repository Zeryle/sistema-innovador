package com.utp.myapp.sales.domain.model.valueobjets;

public record Address(String street,String number,String city,String country) {

    public Address{

        if(street==null || street.trim().isEmpty()){
            throw  new IllegalArgumentException("street cannot be null or empty");
        }
        if(number==null || number.trim().isEmpty()){
            throw  new IllegalArgumentException("number cannot be  or empty");
        }
        if(city==null || city.trim().isEmpty()){
            throw  new IllegalArgumentException("street cannot be null or empty");
        }
        if(country==null || country.trim().isEmpty()){
            throw  new IllegalArgumentException("street cannot be null");
        }


















    }


}
