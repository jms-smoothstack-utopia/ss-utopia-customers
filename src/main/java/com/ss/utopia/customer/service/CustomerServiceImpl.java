package com.ss.utopia.customer.service;

import com.ss.utopia.customer.client.AccountsClient;
import com.ss.utopia.customer.client.authentication.ServiceAuthenticationProvider;
import com.ss.utopia.customer.dto.CreateCustomerDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.dto.UpdateCustomerDto;
import com.ss.utopia.customer.dto.UpdateCustomerLoyaltyDto;
import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.entity.PaymentMethod;
import com.ss.utopia.customer.exception.AccountsClientException;
import com.ss.utopia.customer.exception.DuplicateEmailException;
import com.ss.utopia.customer.exception.IllegalPointChangeException;
import com.ss.utopia.customer.exception.NoSuchCustomerException;
import com.ss.utopia.customer.exception.NoSuchPaymentMethod;
import com.ss.utopia.customer.mapper.CustomerDtoMapper;
import com.ss.utopia.customer.repository.CustomerRepository;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final AccountsClient accountsClient;
  private final ServiceAuthenticationProvider serviceAuthenticationProvider;

  /**
   * Gets all {@link Customer} records.
   *
   * @return a list of all customers.
   */
  @Override
  public List<Customer> getAllCustomers() {
    return customerRepository.findAll();
  }

  /**
   * Gets a {@link Customer} record given an ID.
   *
   * @param id the ID of the customer.
   * @return the record with the given ID.
   * @throws IllegalArgumentException if id is null.
   * @throws NoSuchCustomerException  if a customer with the ID cannot be found.
   */
  @Override
  public Customer getCustomerById(UUID id) {
    notNull(id);
    return customerRepository.findById(id)
            .orElseThrow(() -> new NoSuchCustomerException(id));
  }

  @Override
  public Customer getCustomerByEmail(String email) {
    notNull(email);
    return customerRepository.findByEmail(email)
            .orElseThrow(() -> new NoSuchCustomerException(email));
  }

  /**
   * Creates a new {@link Customer} record.
   *
   * <p>Does not allow creation if a record with an existing email is present.
   *
   * @param customerDto a valid {@link UpdateCustomerDto}.
   * @return the created customer record.
   * @throws DuplicateEmailException if a record already exists with the given email.
   */
  @Override
  public Customer createNewCustomer(CreateCustomerDto customerDto) {
    var customer = CustomerDtoMapper.map(customerDto);

    customerRepository.findByEmail(customer.getEmail())
            .ifPresent(c -> {
              throw new DuplicateEmailException(c.getEmail());
            });

    var accountDto = CustomerDtoMapper.createUserAccountDto(customerDto);

    var response = accountsClient.createNewAccount(accountDto);
    var uuid = response.getBody();

    if (uuid == null) {
      throw new AccountsClientException(response);
    }
    customer.setId(uuid);

    return customerRepository.save(customer);
  }

  /**
   * Updates an existing {@link Customer} account.
   *
   * @param updateCustomerDto The {@link Customer} account to update.
   * @return the updated {@link Customer} from saving changes.
   * @throws NoSuchCustomerException if no Customer found with the ID.
   * @throws DuplicateEmailException if a different record exists with the same email as the update
   *                                 information.
   */
  @Override
  public Customer updateCustomer(UUID customerId, @Valid UpdateCustomerDto updateCustomerDto) {
    notNull(customerId);

    var duplicateEmail = customerRepository.findByEmail(updateCustomerDto.getEmail())
            .stream()
            .anyMatch(customer -> !customer.getId().equals(customerId));

    if (duplicateEmail) {
      throw new DuplicateEmailException(updateCustomerDto.getEmail());
    }

    var oldValue = getCustomerById(customerId);
    var newValue = CustomerDtoMapper.map(updateCustomerDto);

    if (!oldValue.getEmail().equals(newValue.getEmail())) {
      var header = serviceAuthenticationProvider.getAuthorizationHeader();
      accountsClient.updateCustomerEmail(header, oldValue.getId(), newValue.getEmail());
    }

    // set from old payment methods or it'll be erased
    newValue.setPaymentMethods(oldValue.getPaymentMethods());
    newValue.setId(customerId);
    return customerRepository.save(newValue);
  }

  /**
   * Removes a {@link Customer} record given an ID.
   *
   * @param id the ID of the customer to remove.
   */
  @Override
  public void removeCustomerById(UUID id) {
    notNull(id);

    customerRepository.findById(id)
            .ifPresent(customerRepository::delete);
  }

  /**
   * Gets a {@link PaymentMethod} for a {@link Customer} given the customer and payment ID.
   *
   * @param customerId the customer ID.
   * @param paymentId  the payment ID.
   * @return the found payment method record.
   * @throws NoSuchCustomerException if no customer record found with the given ID.
   * @throws NoSuchPaymentMethod     if no payment method record found with the given ID or if
   *                                 payment method ID does not belong to the given customer
   *                                 record.
   */
  @Override
  public PaymentMethod getPaymentMethod(UUID customerId, Long paymentId) {
    notNull(customerId, paymentId);

    return customerRepository.findById(customerId)
            .map(customer -> customer.getPaymentMethods()
                    .stream()
                    .filter(paymentMethod -> paymentMethod.getId().equals(paymentId))
                    // sanity check, don't allow updates if not owner
                    .filter(paymentMethod -> paymentMethod.getOwnerId().equals(customerId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchPaymentMethod(customerId, paymentId)))
            .orElseThrow(() -> new NoSuchCustomerException(customerId));
  }

  /**
   * Creates a new {@link PaymentMethod} record for a {@link Customer}.
   *
   * @param customerId       the customer ID for which the payment method belongs.
   * @param paymentMethodDto a valid {@link PaymentMethodDto}.
   * @return the created record.
   * @throws NoSuchCustomerException if no customer record found with the given ID.
   */
  @Override
  public Long addPaymentMethod(UUID customerId, PaymentMethodDto paymentMethodDto) {
    notNull(customerId);

    final var customer = getCustomerById(customerId);
    var method = new PaymentMethod();
    method.setOwnerId(customerId);
    method.setAccountNum(paymentMethodDto.getAccountNum());
    method.setNotes(paymentMethodDto.getNotes());
    customer.getPaymentMethods().add(method);

    customerRepository.save(customer);

    // get the ID from the created payment method and return it
    return customer.getPaymentMethods()
            .stream()
            .filter(m -> m.getAccountNum().equals(method.getAccountNum()))
            .mapToLong(PaymentMethod::getId)
            .findFirst()
            .orElseThrow();
  }

  /**
   * Updates a {@link PaymentMethod} record for a {@link Customer}.
   *
   * @param customerId       the customer ID for which the payment method belongs.
   * @param paymentId        the payment method ID.
   * @param paymentMethodDto a valid {@link PaymentMethodDto}.
   * @throws NoSuchCustomerException if no customer record found with the given ID.
   * @throws NoSuchPaymentMethod     if no payment method record found with the given ID or if
   *                                 payment method ID does not belong to the given customer
   *                                 record.
   */
  @Override
  public void updatePaymentMethod(UUID customerId,
                                  Long paymentId,
                                  PaymentMethodDto paymentMethodDto) {
    notNull(customerId, paymentId);

    var customer = getCustomerById(customerId);

    customer.getPaymentMethods()
            .stream()
            .filter(m -> m.getId().equals(paymentId))
            .findFirst()
            .ifPresentOrElse(method -> {  // update method if present
              method.setAccountNum(paymentMethodDto.getAccountNum());
              method.setNotes(paymentMethodDto.getNotes());
              customerRepository.save(customer);
            },
              () -> { // else throw ex
                throw new NoSuchPaymentMethod(customerId, paymentId);
              });
  }

  /**
   * Deletes an existing {@link PaymentMethod} from a {@link Customer} if present.
   *
   * @param customerId the customer ID for which the payment method belongs.
   * @param paymentId  the payment method ID.
   * @throws NoSuchCustomerException if no customer record found with the given ID.
   */
  @Override
  public void removePaymentMethod(UUID customerId, Long paymentId) {
    notNull(customerId, paymentId);

    var customer = getCustomerById(customerId);
    customer.getPaymentMethods()
            .removeIf(paymentMethod -> paymentMethod.getId().equals(paymentId));
    customerRepository.save(customer);
  }

  @Override
  public Integer getCustomerLoyaltyPoints(UUID id) {
    return getCustomerById(id).getLoyaltyPoints();
  }

  @Override
  public void updateCustomerLoyaltyPoints(UUID id, UpdateCustomerLoyaltyDto customerLoyaltyDto) {
    var customer = getCustomerById(id);

    var points = customer.getLoyaltyPoints();
    if (customerLoyaltyDto.getIncrement()) {
      //If loyalty point maximum is ever added, throw IllegalPointChange here
      points += customerLoyaltyDto.getPointsToChange();
    } else {
      points -= customerLoyaltyDto.getPointsToChange();
      if (points < 0) {
        throw new IllegalPointChangeException(id,
                customer.getLoyaltyPoints(),
                customerLoyaltyDto.getPointsToChange());
      }
    }
    customer.setLoyaltyPoints(points);
    customerRepository.save(customer);
  }

  /**
   * Util method to check for null ID values.
   *
   * @param ids vararg ids to check.
   */
  private void notNull(Object... ids) {
    for (var i : ids) {
      if (i == null) {
        throw new IllegalArgumentException("ID cannot be null.");
      }
    }
  }

  /**
   * Util method to check for null ID values.
   *
   * @param customerId UUID to check.
   * @param paymentId  long payment ID to check.
   */
  private void notNull(UUID customerId, Long paymentId) {
    if ((customerId == null) || (paymentId == null)) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
  }

}
