package com.ss.utopia.customer.service;

import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;

public interface StripeCustomerService {

  String createStripeCustomer(CustomerCreateParams params);

  Customer retrieveStripeCustomer(String customerId);

  void updateStripeCustomer(String stripeId, CustomerUpdateParams params);

  void deleteStripeCustomer(String stripeId);

}
