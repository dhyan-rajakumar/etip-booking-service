package com.etip.booking.exception;

public class InvalidBookingException extends RuntimeException {
    public InvalidBookingException(String errorMessage) {
        super(errorMessage);
    }
}
