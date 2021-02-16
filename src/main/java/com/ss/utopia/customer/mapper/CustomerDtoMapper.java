package com.ss.utopia.customer.mapper;

import com.ss.utopia.customer.entity.Address;
import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.dto.CustomerDto;
import java.util.Collections;
import java.util.Set;

public class CustomerDtoMapper {

  public static Customer map(CustomerDto customerDto) {

    return Customer.builder()
        .firstName(customerDto.getFirstName())
        .lastName(customerDto.getLastName())
        .email(customerDto.getEmail())
        .addresses(Set.of(Address.builder()
                              .line1(customerDto.getAddrLine1())
                              .line2(customerDto.getAddrLine2())
                              .city(customerDto.getCity())
                              .state(customerDto.getState())
                              .zipcode(customerDto.getZipcode())
                              .build()))
        .paymentMethods(Collections.emptySet())
        .build();
  }
}
