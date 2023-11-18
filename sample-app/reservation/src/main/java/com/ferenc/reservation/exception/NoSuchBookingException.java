package com.ferenc.reservation.exception;

public class NoSuchBookingException extends IllegalArgumentException {
    public NoSuchBookingException() {
    }

    public NoSuchBookingException(String s) {
        super(s);
    }

    public NoSuchBookingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchBookingException(Throwable cause) {
        super(cause);
    }
}
