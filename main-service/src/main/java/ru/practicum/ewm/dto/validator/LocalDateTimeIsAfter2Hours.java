package ru.practicum.ewm.dto.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LocalDateTimeIsAfter2HoursValidator.class)
public @interface LocalDateTimeIsAfter2Hours  {
    String message() default "LocalDateTime must be at least 2 hours in the future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}