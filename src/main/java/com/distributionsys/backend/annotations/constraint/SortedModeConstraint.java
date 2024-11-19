package com.distributionsys.backend.annotations.constraint;

import com.distributionsys.backend.annotations.validator.SortedModeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { SortedModeValidator.class })
@Target({ FIELD })
@Retention(RUNTIME)
public @interface SortedModeConstraint {
    String message() default "Invalid sorted mode";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
