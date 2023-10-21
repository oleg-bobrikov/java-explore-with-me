package ru.practicum.ewm.dto.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class LocalDateTimeIsAfter2HoursValidator implements ConstraintValidator<LocalDateTimeIsAfter2Hours, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext context) {
        if (localDateTime == null) {
            return true;
        }
        LocalDateTime twoHoursAhead = LocalDateTime.now().plusHours(2);

        return localDateTime.isEqual(twoHoursAhead) || localDateTime.isAfter(twoHoursAhead);
    }


}
