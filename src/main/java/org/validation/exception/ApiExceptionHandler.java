package org.validation.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

  // Body validation errors (@RequestBody)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleBodyValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

    return ResponseEntity.badRequest().body(Map.of(
        "message", "Validation failed",
        "errors", errors
    ));
  }

  // Param validation errors (@PathVariable/@RequestParam)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleParamValidation(ConstraintViolationException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));

    return ResponseEntity.badRequest().body(Map.of(
        "message", "Validation failed",
        "errors", errors
    ));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
  }
}