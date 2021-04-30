package com.ss.utopia.customer.service;

import com.ss.utopia.customer.exception.CaughtStripeException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
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
public class StripeCustomerServiceImpl implements  StripeCustomerService {

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
  public Customer retrieveStripeCustomer(String customerId) {
    try {
      return Customer.retrieve(customerId, makeRequestOptions());
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
      customer.delete(makeRequestOptions());
    } catch (StripeException e) {
      throw new CaughtStripeException(e);
    }
  }

  @Override
  public String createPaymentMethod(PaymentMethodCreateParams stripeMethodParams) {
    try {
      PaymentMethod paymentMethod = PaymentMethod.create(stripeMethodParams, makeRequestOptions());
      return paymentMethod.getId();
    } catch (StripeException e) {
      throw new CaughtStripeException(e);
    }
  }

  @Override
  public PaymentMethod retrieveStripePaymentMethod(String paymentMethodId) {
    try {
      return PaymentMethod.retrieve(paymentMethodId, makeRequestOptions());
    } catch (StripeException e) {
      throw new CaughtStripeException(e);
    }
  }

  @Override
  public void attachStripePaymentMethod(String paymentMethodId, String customerId) {
    try {
      PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId, makeRequestOptions());
      PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
              .setCustomer(customerId)
              .build();
      paymentMethod.attach(params, makeRequestOptions());
    } catch (StripeException e) {
      throw new CaughtStripeException(e);
    }
  }

  @Override
  public void detachStripePaymentMethod(String paymentMethodId) {
    try {
      PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId, makeRequestOptions());
      paymentMethod.detach(makeRequestOptions());
    } catch (StripeException e) {
      throw new CaughtStripeException(e);
    }
  }

  private RequestOptions makeRequestOptions() {
    return RequestOptions.builder().setApiKey(this.stripeKey).build();
  }
}
