package com.ss.utopia.customer.service;

import com.ss.utopia.customer.dto.CreateCustomerDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.dto.UpdateCustomerDto;
import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.entity.PaymentMethod;
import java.util.List;
import java.util.UUID;

public interface CustomerService {

  List<Customer> getAllCustomers();

  Customer getCustomerById(UUID id);

  Customer getCustomerByEmail(String email);

  Customer createNewCustomer(CreateCustomerDto customerDto);

  void removeCustomerById(UUID id);

  Customer updateCustomer(UUID customerId, UpdateCustomerDto updateCustomerDto);

  Long addPaymentMethod(UUID id, PaymentMethodDto paymentMethodDto);

  void updatePaymentMethod(UUID customerId, Long paymentId, PaymentMethodDto paymentMethodDto);

  void removePaymentMethod(UUID customerId, Long paymentId);

  PaymentMethod getPaymentMethod(UUID customerId, Long paymentId);

  Integer getCustomerLoyaltyPoints(UUID id);
}
