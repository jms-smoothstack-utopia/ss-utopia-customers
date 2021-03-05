package com.ss.utopia.customer.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.customer.dto.CreateCustomerDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.dto.UpdateCustomerDto;
import com.ss.utopia.customer.dto.UpdateCustomerLoyaltyDto;
import com.ss.utopia.customer.entity.Address;
import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.entity.PaymentMethod;
import com.ss.utopia.customer.security.SecurityConstants;
import com.ss.utopia.customer.service.CustomerService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class CustomerControllerSecurityTests {

  final Date expiresAt = Date.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC));
  @Autowired
  WebApplicationContext wac;
  @MockBean
  SecurityConstants securityConstants;

  @MockBean
  CustomerService customerService;
  MockMvc mvc;

  PaymentMethod mockPaymentMethod = PaymentMethod.builder()
      .id(1L)
      .ownerId(UUID.fromString("a4a9feca-bfe7-4c45-8319-7cb6cdd359db"))
      .accountNum("123456789")
      .notes("test test")
      .build();

  PaymentMethodDto mockPaymentDto = PaymentMethodDto.builder()
      .accountNum(mockPaymentMethod.getAccountNum())
      .notes(mockPaymentMethod.getNotes())
      .build();

  Customer mockCustomer = Customer.builder()
      .id(UUID.fromString("a4a9feca-bfe7-4c45-8319-7cb6cdd359db"))
      .firstName("Eddy")
      .lastName("Grant")
      .email("eddy_grant@test.com")
      .phoneNumber("420-420-6969")
      .addresses(Set.of(Address.builder()
                            .line1("2 Electric Ave.")
                            .line2("Suite HI-R")
                            .city("Las Vegas")
                            .state("NV")
                            .zipcode("69420")
                            .build()))
      .paymentMethods(new HashSet<>())
      .build();

  CreateCustomerDto mockCreateDto = CreateCustomerDto.builder()
      .id(mockCustomer.getId())
      .firstName(mockCustomer.getFirstName())
      .lastName(mockCustomer.getLastName())
      .email(mockCustomer.getEmail())
      .phoneNumber(mockCustomer.getPhoneNumber())
      .addrLine1("2 Electric Ave.")
      .addrLine2("Suite HI-R")
      .city("Las Vegas")
      .state("NV")
      .zipcode("69420")
      .build();

  UpdateCustomerDto mockUpdateDto = UpdateCustomerDto.builder()
      .firstName(mockCustomer.getFirstName())
      .lastName(mockCustomer.getLastName())
      .email(mockCustomer.getEmail())
      .phoneNumber(mockCustomer.getPhoneNumber())
      .addrLine1(mockCreateDto.getAddrLine1())
      .addrLine2(mockCreateDto.getAddrLine2())
      .city(mockCreateDto.getCity())
      .state(mockCreateDto.getState())
      .zipcode(mockCreateDto.getZipcode())
      .build();

  UpdateCustomerLoyaltyDto mockLoyaltyDto = UpdateCustomerLoyaltyDto.builder()
      .pointsToChange(5)
      .increment(true)
      .build();

  @BeforeEach
  void beforeEach() {
    if (mockCustomer.getPaymentMethods().isEmpty()) {
      mockCustomer.getPaymentMethods().add(mockPaymentMethod);
    }

    mvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .apply(springSecurity())
        .build();

    when(securityConstants.getEndpoint()).thenReturn("/authenticate");
    when(securityConstants.getJwtIssuer()).thenReturn("test-issuer");
    when(securityConstants.getExpiresAt()).thenReturn(expiresAt);
    when(securityConstants.getJwtSecret()).thenReturn("superSecret");
    when(securityConstants.getUserIdClaimKey()).thenReturn("userId");
    when(securityConstants.getAuthorityClaimKey()).thenReturn("Authorities");
    when(securityConstants.getJwtHeaderName()).thenReturn("Authorization");
    when(securityConstants.getJwtHeaderPrefix()).thenReturn("Bearer ");

    when(customerService.getCustomerById(mockCustomer.getId()))
        .thenReturn(mockCustomer);

    when(customerService.getAllCustomers())
        .thenReturn(List.of(mockCustomer));

    when(customerService.getCustomerByEmail(mockCustomer.getEmail()))
        .thenReturn(mockCustomer);

    when(customerService.getCustomerLoyaltyPoints(mockCustomer.getId()))
        .thenReturn(mockCustomer.getLoyaltyPoints());

    when((customerService.createNewCustomer(mockCreateDto)))
        .thenReturn(mockCustomer);

    when(customerService.updateCustomer(mockCustomer.getId(), mockUpdateDto))
        .thenReturn(mockCustomer);

    when(customerService.getPaymentMethod(mockCustomer.getId(), mockPaymentMethod.getId()))
        .thenReturn(mockPaymentMethod);

    when(customerService.addPaymentMethod(mockCustomer.getId(), mockPaymentDto))
        .thenReturn(mockPaymentMethod.getId());
  }

  String getJwt(MockUser mockUser) {
    var jwt = JWT.create()
        .withSubject(mockUser.email)
        .withIssuer(securityConstants.getJwtIssuer())
        .withClaim(securityConstants.getUserIdClaimKey(), mockUser.id)
        .withClaim(securityConstants.getAuthorityClaimKey(), List.of(mockUser.getAuthority()))
        .withExpiresAt(expiresAt)
        .sign(Algorithm.HMAC512(securityConstants.getJwtSecret()));
    return "Bearer " + jwt;
  }

  @Test
  void test_getAllCustomers_CanOnlyBePerformedByADMIN() throws Exception {
    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_CUSTOMERS)
                .header("Authorization", getJwt(MockUser.ADMIN)))
        .andExpect(status().isOk());

    var unauthed = List.of(MockUser.DEFAULT,
                           MockUser.MATCH_CUSTOMER,
                           MockUser.UNMATCH_CUSTOMER,
                           MockUser.EMPLOYEE,
                           MockUser.TRAVEL_AGENT);

    for (var user : unauthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_CUSTOMERS)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_CUSTOMERS))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_getCustomerById_CanBePerformedByAuthedUsersOrOwningCustomerOnly() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.TRAVEL_AGENT,
                               MockUser.EMPLOYEE,
                               MockUser.MATCH_CUSTOMER);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }

    var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);

    for (var user : unauthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_getCustomerByEmail_CanBePerformedByAuthedUsersOrOwningCustomerOnly() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.TRAVEL_AGENT,
                               MockUser.EMPLOYEE,
                               MockUser.MATCH_CUSTOMER);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_CUSTOMERS + "/email/" + mockCustomer.getEmail())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }

    var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);

    for (var user : unauthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_CUSTOMERS + "/email/" + mockCustomer.getEmail())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_CUSTOMERS + "/email/" + mockCustomer.getEmail()))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_getCustomerLoyaltyPoints_CanBePerformedByAuthedUserOrOwningCustomerOnly()
      throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.TRAVEL_AGENT,
                               MockUser.EMPLOYEE,
                               MockUser.MATCH_CUSTOMER);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_CUSTOMERS + "/loyalty/" + mockCustomer.getId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }

    var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);

    for (var user : unauthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_CUSTOMERS + "/loyalty/" + mockCustomer.getId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    // also check for not authenticated
    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_CUSTOMERS + "/loyalty/" + mockCustomer.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_updateCustomerLoyaltyPoints_CanBePerformedByAuthedUserWithRoles()
      throws Exception {
    var content = new ObjectMapper().writeValueAsString(mockLoyaltyDto);
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.TRAVEL_AGENT,
                               MockUser.EMPLOYEE);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              put(EndpointConstants.API_V_0_1_CUSTOMERS + "/loyalty/" + mockCustomer.getId())
                  .header("Authorization", getJwt(user))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(content))
          .andExpect(status().isOk());
    }

    var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER, MockUser.MATCH_CUSTOMER);

    for (var user : unauthed) {
      mvc
          .perform(
              put(EndpointConstants.API_V_0_1_CUSTOMERS + "/loyalty/" + mockCustomer.getId())
                  .header("Authorization", getJwt(user))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(content))
          .andExpect(status().isForbidden());
    }

// also check for not authenticated
    mvc
        .perform(
            put(EndpointConstants.API_V_0_1_CUSTOMERS + "/loyalty/" + mockCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
        .andExpect(status().isForbidden());
  }

  /**
   * Should Allow:
   * <ul>
   *   <li>ADMIN</li>
   *   <li>TRAVEL_AGENT</li>
   *   <li>EMPLOYEE</li>
   * </ul>
   * <p>
   * CUSTOMER or DEFAULT should NOT be allowed
   * BUT any UNAUTHENTICATED usage is permitted (ie for a customer creating a new account).
   */
  @Test
  void test_createNewCustomer_CanBePerformedCorrectly() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.TRAVEL_AGENT,
                               MockUser.EMPLOYEE);
    var mockDtoAsJson = new ObjectMapper().writeValueAsString(mockCreateDto);
    for (var user : alwaysAuthed) {
      mvc.perform(
          post(EndpointConstants.API_V_0_1_CUSTOMERS)
              .header("Authorization", getJwt(user))
              .contentType(MediaType.APPLICATION_JSON)
              .content(mockDtoAsJson))
          .andExpect(status().isCreated());
    }

    var unauthed = List.of(MockUser.DEFAULT,
                           MockUser.MATCH_CUSTOMER,
                           MockUser.UNMATCH_CUSTOMER);

    for (var user : unauthed) {
      mvc.perform(
          post(EndpointConstants.API_V_0_1_CUSTOMERS)
              .header("Authorization", getJwt(user))
              .contentType(MediaType.APPLICATION_JSON)
              .content(mockDtoAsJson))
          .andExpect(status().isForbidden());
    }

    // also check for not authenticated
    mvc.perform(
        post(EndpointConstants.API_V_0_1_CUSTOMERS)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mockDtoAsJson))
        .andExpect(status().isCreated());
  }

  @Test
  void test_updateExistingCustomer_CanBePerformedByAuthedUserOrOwningCustomerOnly()
      throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.TRAVEL_AGENT,
                               MockUser.EMPLOYEE,
                               MockUser.MATCH_CUSTOMER);
    var mockDtoAsJson = new ObjectMapper().writeValueAsString(mockUpdateDto);

    for (var user : alwaysAuthed) {
      mvc.perform(
          put(EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId())
              .header("Authorization", getJwt(user))
              .contentType(MediaType.APPLICATION_JSON)
              .content(mockDtoAsJson))
          .andExpect(status().isNoContent());
    }

    var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);
    for (var user : unauthed) {
      mvc.perform(
          put(EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId())
              .header("Authorization", getJwt(user))
              .contentType(MediaType.APPLICATION_JSON)
              .content(mockDtoAsJson))
          .andExpect(status().isForbidden());
    }

    mvc.perform(
        put(EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mockDtoAsJson))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_deleteCustomer_CanOnlyBePerformedByADMINOrOwningCustomer() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN, MockUser.MATCH_CUSTOMER);

    for (var user : alwaysAuthed) {
      mvc.perform(
          delete(EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId())
              .header("Authorization", getJwt(user)))
          .andExpect(status().isNoContent());
    }

    var unauthed = List.of(MockUser.DEFAULT,
                           MockUser.UNMATCH_CUSTOMER,
                           MockUser.TRAVEL_AGENT,
                           MockUser.EMPLOYEE);

    for (var user : unauthed) {
      mvc.perform(
          delete(EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId())
              .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc.perform(
        delete(EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_getPaymentMethod_CanBePerformedByAuthedUsersOrOwningCustomerOnly() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.TRAVEL_AGENT,
                               MockUser.EMPLOYEE,
                               MockUser.MATCH_CUSTOMER);
    var url =
        EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId() + "/payment-method/1";
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(url)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }

    var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);

    for (var user : unauthed) {
      mvc
          .perform(
              get(url)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            get(url))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_addPaymentMethod_CanBePerformedByAuthedUsersOrOwningCustomerOnly() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.TRAVEL_AGENT,
                               MockUser.EMPLOYEE,
                               MockUser.MATCH_CUSTOMER);
    var url =
        EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId() + "/payment-method";
    var content = new ObjectMapper().writeValueAsString(mockPaymentDto);

    for (var user : alwaysAuthed) {
      mvc
          .perform(
              post(url)
                  .header("Authorization", getJwt(user))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(content))
          .andExpect(status().isCreated());
    }

    var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);

    for (var user : unauthed) {
      mvc
          .perform(
              post(url)
                  .header("Authorization", getJwt(user))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(content))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_updatePaymentMethod_CanBePerformedByAuthedUsersOrOwningCustomerOnly() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.TRAVEL_AGENT,
                               MockUser.EMPLOYEE,
                               MockUser.MATCH_CUSTOMER);
    var url =
        EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId() + "/payment-method/"
            + mockPaymentMethod.getId();
    var content = new ObjectMapper().writeValueAsString(mockPaymentDto);

    for (var user : alwaysAuthed) {
      mvc
          .perform(
              put(url)
                  .header("Authorization", getJwt(user))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(content))
          .andExpect(status().isNoContent());
    }

    var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);

    for (var user : unauthed) {
      mvc
          .perform(
              put(url)
                  .header("Authorization", getJwt(user))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(content))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_removePaymentMethod_CanOnlyBePerformedByADMINOrOwningCustomer() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.MATCH_CUSTOMER);
    var url =
        EndpointConstants.API_V_0_1_CUSTOMERS + "/" + mockCustomer.getId() + "/payment-method/"
            + mockPaymentMethod.getId();

    for (var user : alwaysAuthed) {
      mvc
          .perform(
              delete(url)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isNoContent());
    }

    var unauthed = List.of(MockUser.DEFAULT,
                           MockUser.TRAVEL_AGENT,
                           MockUser.EMPLOYEE,
                           MockUser.UNMATCH_CUSTOMER);

    for (var user : unauthed) {
      mvc
          .perform(
              delete(url)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            delete(url))
        .andExpect(status().isForbidden());
  }

  enum MockUser {
    DEFAULT("default@test.com", "ROLE_DEFAULT", UUID.randomUUID().toString()),
    MATCH_CUSTOMER("eddy_grant@test.com", "ROLE_CUSTOMER", "a4a9feca-bfe7-4c45-8319-7cb6cdd359db"),
    UNMATCH_CUSTOMER("someOtherCustomer@test.com", "ROLE_CUSTOMER", UUID.randomUUID().toString()),
    EMPLOYEE("employee@test.com", "ROLE_EMPLOYEE", UUID.randomUUID().toString()),
    TRAVEL_AGENT("travel_agent@test.com", "ROLE_TRAVEL_AGENT", UUID.randomUUID().toString()),
    ADMIN("admin@test.com", "ROLE_ADMIN", UUID.randomUUID().toString());


    final String email;
    final GrantedAuthority grantedAuthority;
    final String id;

    MockUser(String email, String grantedAuthority, String id) {
      this.email = email;
      this.grantedAuthority = new SimpleGrantedAuthority(grantedAuthority);
      this.id = id;
    }

    public String getAuthority() {
      return grantedAuthority.getAuthority();
    }
  }
}
