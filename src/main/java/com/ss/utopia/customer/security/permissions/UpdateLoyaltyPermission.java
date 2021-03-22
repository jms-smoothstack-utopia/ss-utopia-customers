package com.ss.utopia.customer.security.permissions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','TRAVEL_AGENT')")
public @interface UpdateLoyaltyPermission {
}
