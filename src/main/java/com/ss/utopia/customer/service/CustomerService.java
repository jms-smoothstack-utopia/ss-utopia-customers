package com.ss.utopia.customer.service;

import com.ss.utopia.customer.dto.CustomerDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.model.PaymentMethod;
import java.util.List;
import java.util.Optional;

public interface CustomerService {

  List<Customer> getAllCustomers();

  Customer getCustomerById(Long id);

  Customer createNewCustomer(CustomerDto customerDto);

  void removeCustomerById(Long id);

  Customer updateCustomer(Long customerId, CustomerDto customerDto);

  Long addPaymentMethod(Long id, PaymentMethodDto paymentMethodDto);

  void updatePaymentMethod(Long customerId, Long paymentId, PaymentMethodDto paymentMethodDto);

  void removePaymentMethod(Long customerId, Long paymentId);

  PaymentMethod getPaymentMethod(Long customerId, Long paymentId);
}
