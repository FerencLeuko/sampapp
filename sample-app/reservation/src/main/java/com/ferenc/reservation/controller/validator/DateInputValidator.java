package com.ferenc.reservation.controller.validator;

import com.ferenc.reservation.controller.dto.DateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateInputValidator implements ConstraintValidator<ValidRange, DateRange> {
    @Override
    public boolean isValid(DateRange dateRange, ConstraintValidatorContext context) {
        if( dateRange.getStartDate().isBefore(LocalDate.now())){
            return false;
        }
        return !dateRange.getStartDate().isAfter(dateRange.getEndDate());
    }
}