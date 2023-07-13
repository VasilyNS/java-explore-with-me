package ru.practicum.ewmservice.tools.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureWithinTwoHoursValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureWithinTwoHours {
    String message() default "The date and time must be no earlier than two hours before the current time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}