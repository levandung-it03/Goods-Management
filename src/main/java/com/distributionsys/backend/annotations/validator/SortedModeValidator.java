package com.distributionsys.backend.annotations.validator;

import com.distributionsys.backend.annotations.constraint.SortedModeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class SortedModeValidator implements ConstraintValidator<SortedModeConstraint, Integer> {
    @Override
    public void initialize(SortedModeConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (Objects.isNull(value))  return true;
        return value.equals(1) || value.equals(-1);
    }
}
