package com.etip.booking.service;

import com.etip.booking.dto.BookingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingEventConsumer {

    @KafkaListener(
            topics = "booking-events",
            groupId = "booking-service-group"
    )

    public void consume(BookingEvent event){
        log.info("Received Kafka Booking event for bookingId={} and CustomerName={}",
                event.getBookingId(), event.getCustomerName());

        throw new RuntimeException("Simulating consumer processing failure");
    }
}
