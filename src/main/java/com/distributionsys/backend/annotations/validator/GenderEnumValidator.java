package com.distributionsys.backend.annotations.validator;

import com.distributionsys.backend.annotations.constraint.GenderEnumConstraint;
import com.distributionsys.backend.enums.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class GenderEnumValidator implements ConstraintValidator<GenderEnumConstraint, Integer> {
    @Override
    public void initialize(GenderEnumConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (Objects.isNull(value))  return true;
        return value.equals(Gender.MALE.getGenderId()) || value.equals(Gender.FEMALE.getGenderId());
    }
}
