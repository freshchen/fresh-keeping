package com.github.freshchen.custom;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * @author darcy
 * @since 2020/06/30
 **/
public class AgetValidator implements ConstraintValidator<Age, Integer> {

    private int line;

    @Override
    public void initialize(Age constraintAnnotation) {
        this.line = constraintAnnotation.line();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return false;
        }
        return value > line;
    }
}
