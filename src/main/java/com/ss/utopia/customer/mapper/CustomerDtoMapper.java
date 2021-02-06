package com.ss.utopia.customer.mapper;

import com.ss.utopia.customer.model.Address;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.dto.CustomerDto;
import java.util.Collections;
import java.util.Set;

public class CustomerDtoMapper {

  public static Customer map(CustomerDto customerDto) {
    var customer = new Customer();

    customer.setFirstName(customerDto.getFirstName());
    customer.setLastName(customerDto.getLastName());
    customer.setEmail(customerDto.getEmail());

    var address = new Address();
    address.setCardinality(1);
    address.setLine1(customerDto.getAddrLine1());
    address.setLine2(customerDto.getAddrLine2());
    address.setCity(customerDto.getCity());
    address.setState(customerDto.getState());
    address.setZipcode(customerDto.getZipcode());

    customer.setAddresses(Set.of(address));
    customer.setPaymentMethods(Collections.emptySet());

    return customer;
  }
}
