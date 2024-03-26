package com.ferenc.reservation.controller.validator;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.ferenc.reservation.controller.dto.DateRange;

public class DateInputValidator implements ConstraintValidator<ValidRange, DateRange> {

    @Override
    public boolean isValid(DateRange dateRange, ConstraintValidatorContext context) {
        if (dateRange.getStartDate().isBefore(LocalDate.now())) {
            return false;
        }
        return !dateRange.getStartDate().isAfter(dateRange.getEndDate());
    }
}