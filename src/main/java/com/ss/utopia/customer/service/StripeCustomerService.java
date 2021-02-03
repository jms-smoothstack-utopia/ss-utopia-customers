package com.ss.utopia.customer.service;

import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodCreateParams;

public interface StripeCustomerService {

  String createStripeCustomer(CustomerCreateParams params);

  Customer retrieveStripeCustomer(String customerId);

  void updateStripeCustomer(String stripeId, CustomerUpdateParams params);

  void deleteStripeCustomer(String stripeId);

  String createPaymentMethod(PaymentMethodCreateParams stripeMethodParams);

  PaymentMethod retrieveStripePaymentMethod(String paymentMethodId);

  void detachStripePaymentMethod(String paymentMethodId);
}
