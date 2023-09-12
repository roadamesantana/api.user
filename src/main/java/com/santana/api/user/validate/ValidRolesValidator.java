package com.santana.api.user.validate;

import com.google.common.collect.Lists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidRolesValidator implements ConstraintValidator<ValidRoles, String> {

    private List<String> validRoles = Lists.newArrayList("ADM", "USER");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validRoles.stream().anyMatch(r -> value.equals(r));
    }

}
