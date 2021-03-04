package com.ss.utopia.customer.security.permissions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ADMIN', 'TRAVEL_AGENT', 'EMPLOYEE') "
    + "OR hasRole('CUSTOMER') "
    + "AND @customerAuthenticationManager.customerEmailMatches(authentication, #email)")
public @interface GetCustomerByEmailPermission {
}
