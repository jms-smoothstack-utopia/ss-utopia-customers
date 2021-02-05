package com.ss.utopia.customer.service;

import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.repository.CustomerRepository;
import java.util.List;
import java.util.NoSuchElementException;
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

  /**
   * Update an existing {@link Customer} account.
   *
   * @param toUpdate The {@link Customer} account to update.
   * @return the updated {@link Customer} from saving changes.
   * @throws IllegalStateException  if customer ID is null or less than 1.
   * @throws NoSuchElementException if no Customer found with the ID.
   */
  @Override
  public Customer update(Customer toUpdate) {
    var id = toUpdate.getId();

    if (id == null || id < 1) {
      throw new IllegalArgumentException("ID cannot be null or less than 1.");
    }

    var exists = repository.findById(id).isPresent();

    if (exists) {
      return repository.save(toUpdate);
    }

    throw new NoSuchElementException("No customer with id '" + id + "' found.");
  }


}
