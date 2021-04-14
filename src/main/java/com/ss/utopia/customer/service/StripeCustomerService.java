package com.ss.utopia.customer.service;

import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;

public interface StripeCustomerService {

  String createStripeCustomer(CustomerCreateParams params);

  void updateStripeCustomer(String stripeId, CustomerUpdateParams params);

  void deleteStripeCustomer(String stripeId);

}
