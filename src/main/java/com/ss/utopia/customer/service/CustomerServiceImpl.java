package com.ss.utopia.customer.service;

import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.repository.CustomerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository repository;

  public CustomerServiceImpl(CustomerRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<Customer> getById(Long id) {
    return repository.findById(id);
  }

  @Override
  public List<Customer> getAll() {
    return repository.findAll();
  }

  @Override
  public Customer create(Customer customer) {
    if (customer.getId() != null) {
      customer.setId(null);
    }
    return repository.save(customer);
  }

  @Override
  public void removeById(Long id) {
    repository.findById(id)
        .ifPresent(repository::delete);
  }

  @Override
  public Customer update(Customer customer) {
    return repository.save(customer);
  }


}
