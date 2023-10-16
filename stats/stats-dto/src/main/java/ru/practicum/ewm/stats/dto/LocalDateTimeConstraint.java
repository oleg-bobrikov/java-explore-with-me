package ru.practicum.ewm.stats.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

@Documented
@Constraint(validatedBy = LocalDateTimeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalDateTimeConstraint {
    String message() default "incorrect date and time format. Pattern is " + DATE_TIME_PATTERN;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}