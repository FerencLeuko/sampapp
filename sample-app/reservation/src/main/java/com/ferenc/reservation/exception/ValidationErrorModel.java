package com.ferenc.reservation.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorModel {

    private String fieldName;
    private Object rejectedValue;
    private String messageError;
}