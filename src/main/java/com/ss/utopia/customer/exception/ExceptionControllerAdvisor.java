package com.ss.utopia.customer.exception;

import feign.FeignException.FeignClientException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ControllerAdvice for exception handling.
 */
@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvisor {

  public static final String ERROR_KEY = "error";
  public static final String STATUS_KEY = "status";
  public static final String CLIENT_EXCEPTION_MESSAGE =
      "There was a problem creating the account. Please try again.";

  /**
   * Handles exceptions thrown on search returning no results.
   *
   * @param ex an exception thrown as the result of no element found matching the search condition.
   * @return a map of the error message and status code.
   */
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoSuchElementException.class)
  public Map<String, Object> handleNoSuchElementExceptions(NoSuchElementException ex) {
    log.error(ex.getMessage());
    return baseResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  /**
   * Handles duplicate email constraint validation exceptions.
   *
   * <p>Returns the offending email in the returned map object.
   *
   * @param ex an exception thrown as the result of a unique constraint violation for an email on
   *           creating a new customer.
   * @return a map of the error message, offending email, and status code.
   */
  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateEmailException.class)
  public Map<String, Object> handleDuplicateEmailException(DuplicateEmailException ex) {
    log.error(ex.getMessage());
    return baseResponse(ex.getMessage(), HttpStatus.CONFLICT);
  }

  /**
   * Handles validation exceptions on invalid DTO fields.
   *
   * <p>Creates a map of field name to error message for the return message.
   *
   * @param ex an exception thrown during validation of DTO properties.
   * @return a map of the error message, status code, and offending fields and cause.
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
    log.error(ex.getMessage());

    var response = baseResponse("Invalid field(s) in request.", HttpStatus.BAD_REQUEST);

    // get field name and error message as map
    var errors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .collect(
            Collectors.toMap(
                error -> ((FieldError) error).getField(),
                error -> getErrorMessageOrDefault((FieldError) error)));

    response.put("message", errors);
    return response;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalPointChangeException.class)
  public Map<String, Object> handleIllegalPointChangeExceptions(IllegalPointChangeException ex) {
    log.error(ex.getMessage());
    return baseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(AccountsClientException.class)
  public Map<String, Object> accountsClientException(AccountsClientException ex) {
    log.error(ex.getMessage());
    return baseResponse(CLIENT_EXCEPTION_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(FeignClientException.class)
  public Map<String, Object> feignClientException(FeignClientException ex) {
    log.error(ex.getMessage());
    return baseResponse(CLIENT_EXCEPTION_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(CaughtStripeException.class)
  public Map<String, Object> caughtStripeException(CaughtStripeException ex) {
    log.error(ex.getMessage());
    var response = baseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    response.put("Stripe code", ex.getStripeCode());
    response.put("Stripe type", ex.getStripeErrorType());

    return response;
  }

  /**
   * Helper function to get error message or provide a default if not present.
   */
  private String getErrorMessageOrDefault(FieldError error) {
    var msg = error.getDefaultMessage();
    msg = msg == null || msg.isBlank() ? "Unknown validation failure." : msg;

    log.debug("Field" + error.getField() + " Message: " + msg);
    return msg;
  }

  private Map<String, Object> baseResponse(String errorMsg, HttpStatus status) {
    var response = new HashMap<String, Object>();
    response.put(ERROR_KEY, errorMsg);
    response.put(STATUS_KEY, status.value());
    return response;
  }
}
