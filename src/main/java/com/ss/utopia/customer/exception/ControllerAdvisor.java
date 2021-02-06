package com.ss.utopia.customer.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {

  private static final Logger log = LoggerFactory.getLogger(ControllerAdvisor.class);

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Invalid field(s) in request.");
    response.put("status", 400);

    var errors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .collect(
            Collectors.toMap(error -> ((FieldError) error).getField(),
                             error -> getErrorMessageOrDefault((FieldError) error)));

    response.put("message", errors);
    return response;
  }

  private String getErrorMessageOrDefault(FieldError error) {

    var logMsg = "Validation exception - Message: '";

    String errorMsg;
    if (error.getDefaultMessage() == null) {
      errorMsg = "Unknown validation failure";
    } else {
      errorMsg = error.getDefaultMessage();
    }

    logMsg += errorMsg + "' Field: " + error.getField()
        + " Rejected Value: " + error.getRejectedValue();

    log.debug(logMsg);

    return errorMsg;
  }
}
