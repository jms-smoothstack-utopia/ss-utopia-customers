package com.ss.utopia.customer.service;

import com.ss.utopia.customer.exception.CaughtStripeException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "com.ss.utopia.customer.service")
public class StripeCustomerServiceImpl implements  StripeCustomerService{

  @Getter @Setter
  private String stripeKey;

  @Override
  public String createStripeCustomer(CustomerCreateParams params) {
   try {
      Customer stripeCustomer = Customer.create(params, makeRequestOptions());
      return stripeCustomer.getId();
    } catch (StripeException e) {
      throw new CaughtStripeException(e);
    }
  }

  @Override
  public void updateStripeCustomer(String stripeId, CustomerUpdateParams params) {
    try {
      Customer customer = Customer.retrieve(stripeId, makeRequestOptions());
      customer.update(params, makeRequestOptions());
    } catch (StripeException e) {
      throw new CaughtStripeException(e);
    }
  }

  @Override
  public void deleteStripeCustomer(String stripeId) {
    try {
      Customer customer = Customer.retrieve(stripeId, makeRequestOptions());
      customer.delete();
    } catch (StripeException e) {
      throw new CaughtStripeException(e);
    }
  }

  private RequestOptions makeRequestOptions() {
    return RequestOptions.builder().setApiKey(this.stripeKey).build();
  }
}
