package org.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.validation.annotation.NoWhitespace;

public class NoWhitespaceValidator implements ConstraintValidator<NoWhitespace, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;     // let @NotBlank handle required
    return !value.matches(".*\\s+.*");  // no spaces/tabs/newlines
  }
}