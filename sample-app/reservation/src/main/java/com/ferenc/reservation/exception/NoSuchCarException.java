package com.ferenc.reservation.exception;

public class NoSuchCarException extends IllegalArgumentException {

    public NoSuchCarException() {
    }

    public NoSuchCarException(String s) {
        super(s);
    }

    public NoSuchCarException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchCarException(Throwable cause) {
        super(cause);
    }
}
