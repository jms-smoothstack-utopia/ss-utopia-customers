package com.ss.utopia.customer.service;

import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.exception.DuplicateEmailException;
import com.ss.utopia.customer.exception.NoSuchCustomerException;
import com.ss.utopia.customer.exception.NoSuchPaymentMethod;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.model.PaymentMethod;
import com.ss.utopia.customer.repository.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository repository;

  public CustomerServiceImpl(CustomerRepository repository) {
    this.repository = repository;
  }

  @Override
  public Customer getById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoSuchCustomerException(id));
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

    repository.findByEmail(customer.getEmail())
        .ifPresent(c -> {
          throw new DuplicateEmailException(c.getEmail());
        });

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
   * @throws IllegalStateException   if customer ID is null or less than 1.
   * @throws NoSuchCustomerException if no Customer found with the ID.
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
    } else {
      throw new NoSuchCustomerException(id);
    }
  }

  @Override
  public Long addPaymentMethod(Long customerId, PaymentMethodDto paymentMethodDto) {
    var customer = getById(customerId);

    var method = new PaymentMethod();
    method.setAccountNum(paymentMethodDto.getAccountNum());
    method.setNotes(paymentMethodDto.getNotes());
    customer.getPaymentMethods().add(method);

    repository.save(customer);

    // get the ID from the created payment method and return it
    return customer.getPaymentMethods()
        .stream()
        .filter(m -> m.getAccountNum().equals(method.getAccountNum()))
        .mapToLong(PaymentMethod::getId)
        .findFirst()
        .orElseThrow();
  }

  @Override
  public void updatePaymentMethod(Long customerId,
                                  Long paymentId,
                                  PaymentMethodDto paymentMethodDto) {
    var customer = getById(customerId);

    customer.getPaymentMethods()
        .stream()
        .filter(m -> m.getId().equals(paymentId))
        .findFirst()
        .ifPresentOrElse(method -> {
                           method.setAccountNum(paymentMethodDto.getAccountNum());
                           method.setNotes(paymentMethodDto.getNotes());
                           repository.save(customer);
                         },
                         () -> {
                           throw new NoSuchPaymentMethod(customerId, paymentId);
                         });
  }

  @Override
  public void removePaymentMethod(Long customerId, Long paymentId) {
    var customer = getById(customerId);
    customer.getPaymentMethods()
        .removeIf(paymentMethod -> paymentMethod.getId().equals(paymentId));
    repository.save(customer);
  }
}
