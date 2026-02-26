package org.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.validation.validator.NoWhitespaceValidator;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NoWhitespaceValidator.class)
public @interface NoWhitespace {
  String message() default "must not contain whitespace";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}