package com.distributionsys.backend.annotations.validator;

import com.distributionsys.backend.annotations.constraint.ListTypeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Objects;

public class ListTypeValidator implements ConstraintValidator<ListTypeConstraint, List<?>> {
    Class<?> listType;

    @Override
    public void initialize(ListTypeConstraint constraintAnnotation) {
        this.listType = constraintAnnotation.listType();
    }

    @Override
    public boolean isValid(List<?> values, ConstraintValidatorContext context) {
        if (Objects.isNull(values) || values.isEmpty()) return true;
        return values.stream().allMatch(v -> listType.isInstance(v));
    }
}
