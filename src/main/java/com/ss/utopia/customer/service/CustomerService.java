package com.ss.utopia.customer.service;

import com.ss.utopia.customer.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {

  List<Customer> getAll();

  Optional<Customer> getById(Long id);

  Customer create(Customer customer);

  void removeById(Long id);

  Customer update(Customer customer);
}
