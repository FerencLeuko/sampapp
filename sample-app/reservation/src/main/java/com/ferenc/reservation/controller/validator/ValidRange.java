package com.ferenc.reservation.controller.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

    @Documented
    @Constraint(validatedBy = DateInputValidator.class)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidRange {

        String message() default "Start date must be present or future, and before end date";

        Class<?>[] groups() default { };

        Class<? extends Payload>[] payload() default { };
    }

