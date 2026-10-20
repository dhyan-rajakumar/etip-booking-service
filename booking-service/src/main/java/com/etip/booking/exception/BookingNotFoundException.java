package com.etip.booking.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String bookingId){
        super("Booking -"+bookingId+" Not Found.");
    }
}
