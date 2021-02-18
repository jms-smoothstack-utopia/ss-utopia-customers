package com.ss.utopia.customer.mapper;

import com.ss.utopia.customer.dto.CreateCustomerRecordDto;
import com.ss.utopia.customer.dto.CustomerDto;
import com.ss.utopia.customer.entity.Address;
import com.ss.utopia.customer.entity.Customer;
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

  public static Customer map(CreateCustomerRecordDto dto) {
    return Customer.builder()
        .id(dto.getId())
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .email(dto.getEmail())
        .phoneNumber(dto.getPhoneNumber())
        .loyaltyPoints(0)
        .addresses(Set.of(Address.builder()
                              .cardinality(1)
                              .line1(dto.getAddrLine1())
                              .line2(dto.getAddrLine2())
                              .city(dto.getCity())
                              .state(dto.getState())
                              .zipcode(dto.getZipcode())
                              .build()))
        .paymentMethods(Collections.emptySet())
        .build();
  }
}
