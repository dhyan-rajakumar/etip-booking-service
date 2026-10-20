package com.etip.booking.controller;

import com.etip.booking.dto.BookingEvent;
import com.etip.booking.dto.BookingRequest;
import com.etip.booking.dto.BookingResponse;
import com.etip.booking.service.BookingEventProducer;
import com.etip.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.web.bind.annotation.*;

@EnableKafka
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingEventProducer bookingEventProducer;

    public BookingController(BookingService bookingService, BookingEventProducer bookingEventProducer) {
        this.bookingService = bookingService;
        this.bookingEventProducer = bookingEventProducer;
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

    @PutMapping("/transaction-test/{bookingId}")
    public ResponseEntity<Void> transactionTest(@PathVariable String bookingId){
        bookingService.MethodA(bookingId);
        return ResponseEntity.ok().build();
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

    @PostMapping("/test-kafka-publish")
    public ResponseEntity testKafka(){

        BookingEvent bookingEvent = new BookingEvent();
        bookingEvent.setBookingId("Book-0001");
        bookingEvent.setCustomerName("Dhyaneswaran");
        bookingEvent.setSource("India");
        bookingEvent.setEventName("Booking-Created");
        bookingEvent.setDestination("Austria");
        bookingEventProducer.publishBookingCreated(bookingEvent);
        return ResponseEntity.ok("Kafka event posted successfully!");
    }
}
