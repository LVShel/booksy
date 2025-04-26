package com.shelest.booksy.service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsbnValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIsbn {
    String message() default "Invalid ISBN format (min. 10 characters)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
