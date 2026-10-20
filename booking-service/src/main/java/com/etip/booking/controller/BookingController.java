package com.etip.booking.controller;

import com.etip.booking.dto.BookingRequest;
import com.etip.booking.dto.BookingResponse;
import com.etip.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;


    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @PostMapping
    public ResponseEntity<BookingResponse> postBooking(@Valid @RequestBody BookingRequest request){
        BookingResponse booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> updateBooking(@RequestBody BookingResponse request, @PathVariable String bookingId){
        BookingResponse booking = bookingService.updateBooking(request, bookingId);
        return ResponseEntity.ok(booking);
    }


    @PutMapping("/{bookingId}/{destination}")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable String bookingId, @PathVariable String destination) throws Exception {
        BookingResponse booking = bookingService.updateDestination(bookingId, destination);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable String bookingId){
        BookingResponse booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity deleteBooking(@PathVariable String bookingId){
        bookingService.deleteBookingById(bookingId);
        return ResponseEntity.noContent().build();
    }

}
