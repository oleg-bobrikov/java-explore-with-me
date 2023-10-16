package ru.practicum.ewm.stats.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

public class LocalDateTimeValidator implements ConstraintValidator<LocalDateTimeConstraint, String> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Override
    public void initialize(LocalDateTimeConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            LocalDateTime.parse(value, formatter);
        } catch (DateTimeParseException exception) {
            return false;
        }
        return true;
    }
}
