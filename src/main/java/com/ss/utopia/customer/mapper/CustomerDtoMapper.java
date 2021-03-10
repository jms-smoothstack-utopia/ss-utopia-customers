package com.ss.utopia.customer.mapper;

import com.ss.utopia.customer.dto.CreateCustomerDto;
import com.ss.utopia.customer.dto.CreateUserAccountDto;
import com.ss.utopia.customer.dto.UpdateCustomerDto;
import com.ss.utopia.customer.entity.Address;
import com.ss.utopia.customer.entity.Customer;
import java.util.Collections;
import java.util.Set;

public class CustomerDtoMapper {

  public static Customer map(UpdateCustomerDto updateCustomerDto) {

    return Customer.builder()
        .firstName(updateCustomerDto.getFirstName())
        .lastName(updateCustomerDto.getLastName())
        .email(updateCustomerDto.getEmail())
        .phoneNumber(updateCustomerDto.getPhoneNumber())
        .addresses(Set.of(Address.builder()
                              .line1(updateCustomerDto.getAddrLine1())
                              .line2(updateCustomerDto.getAddrLine2())
                              .city(updateCustomerDto.getCity())
                              .state(updateCustomerDto.getState())
                              .zipcode(updateCustomerDto.getZipcode())
                              .build()))
        .paymentMethods(Collections.emptySet())
        .ticketEmails(updateCustomerDto.getTicketEmails())
        .flightEmails(updateCustomerDto.getFlightEmails())
        .build();
  }

  public static Customer map(CreateCustomerDto dto) {
    return Customer.builder()
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

  public static CreateUserAccountDto createUserAccountDto(CreateCustomerDto dto) {
    return CreateUserAccountDto.builder()
        .email(dto.getEmail())
        .password(dto.getPassword())
        .build();
  }
}
