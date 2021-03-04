package com.ss.utopia.customer.security.permissions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Permissions for creating a new Customer entity.
 * <p>
 * ALLOW:
 * <ul>
 *   <li>ADMIN</li>
 *   <li>TRAVEL_AGENT</li>
 *   <li>EMPLOYEE</li>
 *   <li>UNAUTHENTICATED (ie for a customer creating a new account)</li>
 * </ul>
 * <p>
 * <p>
 * DENY:
 * <ul>
 *   <li>DEFAULT</li>
 *   <li>CUSTOMER</li>
 * </ul>
 * <p>
 * An example of a deny is a customer attempting to create a new record.
 * This should be denied because they already have an existing record.
 * They should be logged out to create a new, different record.
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','TRAVEL_AGENT') OR NOT isAuthenticated()")
public @interface CreateCustomerPermission {

}
