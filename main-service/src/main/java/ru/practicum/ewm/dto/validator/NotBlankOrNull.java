package ru.practicum.ewm.dto.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotBlankOrNullValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankOrNull {
    String message() default "Title must contain at least one non-whitespace character if present";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
