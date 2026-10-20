package com.etip.booking.repository;
//import com.etip.booking.dto.BookingResponse;
import com.etip.booking.dto.BookingResponse;
import org.springframework.context.annotation.Bean;
//import com.etip.booking.entity.Booking;
//import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


public class BookingRepository_Map {
    private final Map<String, BookingResponse> bookings = new HashMap<>();
    private final AtomicLong bookingSequence = new AtomicLong(100000);

    public BookingResponse save(BookingResponse booking){
        String bookingId = "BK"+bookingSequence.incrementAndGet();
        booking.setBookingId(bookingId);
        bookings.put(bookingId, booking);
        return bookings.get(bookingId);
    }

    public BookingResponse getBookingByid(String bookingId){
        return bookings.get(bookingId);
    }

    public BookingResponse updateBookingById(String bookingId, BookingResponse response){
        response.setBookingId(bookingId);
        bookings.replace(bookingId, response);
        return bookings.get(bookingId);
    }

    public void deleteBookingById(String bookingId){
        bookings.remove(bookingId);
    }
}
