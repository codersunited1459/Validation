package org.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.validation.validator.PasswordMatchValidator;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {
  String message() default "password and confirmPassword must match";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}