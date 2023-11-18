package com.ferenc.reservation.exception;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonRootName("error")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ErrorResponse {
    private String message;
}
