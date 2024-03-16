package com.ferenc.reservation.exception;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonRootName("validation error")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ValidationErrorResponse {

    private List<ValidationErrorModel> errorList;

}
