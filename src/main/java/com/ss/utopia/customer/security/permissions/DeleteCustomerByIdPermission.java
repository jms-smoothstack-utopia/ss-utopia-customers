package com.ss.utopia.customer.security.permissions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN')"
    + " OR @customerAuthenticationManager.customerIdMatches(authentication, #customerId)")
public @interface DeleteCustomerByIdPermission {

}
