package com.ferenc.reservation.exception;

public class CarNotAvailableException extends IllegalArgumentException {

    public CarNotAvailableException() {
    }

    public CarNotAvailableException(String message) {
        super(message);
    }

    public CarNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CarNotAvailableException(Throwable cause) {
        super(cause);
    }

}
