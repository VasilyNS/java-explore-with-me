package ru.practicum.ewmservice.tools.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class FutureWithinTwoHoursValidator implements ConstraintValidator<FutureWithinTwoHours, LocalDateTime> {
    @Override
    public void initialize(FutureWithinTwoHours constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Поле может быть пустым, если это разрешено в логике
        }

        LocalDateTime twoHoursFromNow = LocalDateTime.now().plusHours(2);
        return value.isAfter(twoHoursFromNow);
    }
}