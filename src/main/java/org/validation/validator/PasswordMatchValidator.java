package org.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.validation.annotation.PasswordMatch;
import org.validation.dto.UserRequestDto;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserRequestDto> {

  @Override
  public boolean isValid(UserRequestDto dto, ConstraintValidatorContext context) {
    if (dto == null) return true;

    String p1 = dto.getPassword();
    String p2 = dto.getConfirmPassword();

    // If missing, required checks will be handled by @NotBlank (on create group)
    if (p1 == null || p2 == null) return true;

    boolean ok = p1.equals(p2);
    if (!ok) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("confirmPassword must match password")
          .addPropertyNode("confirmPassword")
          .addConstraintViolation();
    }
    return ok;
  }
}