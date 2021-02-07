package com.ss.utopia.customer.service;

import com.ss.utopia.customer.dto.CustomerDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.exception.DuplicateEmailException;
import com.ss.utopia.customer.exception.NoSuchCustomerException;
import com.ss.utopia.customer.exception.NoSuchPaymentMethod;
import com.ss.utopia.customer.mapper.CustomerDtoMapper;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.model.PaymentMethod;
import com.ss.utopia.customer.repository.CustomerRepository;
import java.util.List;
import javax.validation.Valid;
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
  public Customer create(@Valid CustomerDto customerDto) {
    var customer = CustomerDtoMapper.map(customerDto);

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
   * <p>
   * TODO: Update to allow multiple addr fields
   *
   * @param customerDto The {@link Customer} account to update.
   * @return the updated {@link Customer} from saving changes.
   * @throws IllegalStateException   if customer ID is null or less than 1.
   * @throws NoSuchCustomerException if no Customer found with the ID.
   */
  @Override
  public Customer update(Long customerId, @Valid CustomerDto customerDto) {
    var oldValue = getById(customerId);
    var newValue = CustomerDtoMapper.map(customerDto);
    // set from old payment methods or it'll be erased
    newValue.setPaymentMethods(oldValue.getPaymentMethods());
    newValue.setId(customerId);
    return repository.save(newValue);
  }

  @Override
  public PaymentMethod getPaymentMethod(Long customerId, Long paymentId) {
    return repository.findById(customerId)
        .map(customer -> customer.getPaymentMethods()
            .stream()
            .filter(paymentMethod -> paymentMethod.getId().equals(paymentId))
            // sanity check, don't allow updates if not owner
            .filter(paymentMethod -> paymentMethod.getOwnerId().equals(customerId))
            .findFirst()
            .orElseThrow(() -> new NoSuchPaymentMethod(customerId, paymentId)))
        .orElseThrow(() -> new NoSuchCustomerException(customerId));
  }

  @Override
  public Long addPaymentMethod(Long customerId, PaymentMethodDto paymentMethodDto) {
    var customer = getById(customerId);

    var method = new PaymentMethod();
    method.setOwnerId(customer.getId());
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
        .ifPresentOrElse(method -> {  // update method if present
                           method.setAccountNum(paymentMethodDto.getAccountNum());
                           method.setNotes(paymentMethodDto.getNotes());
                           repository.save(customer);
                         },
                         () -> { // else throw ex
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
