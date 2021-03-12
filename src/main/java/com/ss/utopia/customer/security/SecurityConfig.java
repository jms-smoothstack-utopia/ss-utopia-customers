package com.ss.utopia.customer.security;

import com.ss.utopia.customer.controller.EndpointConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final SecurityConstants securityConstants;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .cors().and().csrf().disable()
        .authorizeRequests()
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        .requestMatchers(CorsUtils::isCorsRequest).permitAll()
        // permit all to allow unauthenticated creation
        .antMatchers(HttpMethod.POST, EndpointConstants.API_V_0_1_CUSTOMERS).permitAll()
        //todo does this need preauthorization?
        .antMatchers(HttpMethod.DELETE, EndpointConstants.API_V_0_1_CUSTOMERS + "/confirm/**").permitAll()
        .antMatchers(HttpMethod.GET, "/api-docs").permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilter(new JwtAuthenticationVerificationFilter(authenticationManagerBean(),
                                                           securityConstants))
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    ;
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}
