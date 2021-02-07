package com.ss.utopia.customer.service;

import com.ss.utopia.customer.dto.CustomerDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.model.PaymentMethod;
import java.util.List;
import java.util.Optional;

public interface CustomerService {

  List<Customer> getAll();

  Customer getById(Long id);

  Customer create(CustomerDto customerDto);

  void removeById(Long id);

  Customer update(Long customerId, CustomerDto customerDto);

  Long addPaymentMethod(Long id, PaymentMethodDto paymentMethodDto);

  void updatePaymentMethod(Long customerId, Long paymentId, PaymentMethodDto paymentMethodDto);

  void removePaymentMethod(Long customerId, Long paymentId);

  PaymentMethod getPaymentMethod(Long customerId, Long paymentId);
}
