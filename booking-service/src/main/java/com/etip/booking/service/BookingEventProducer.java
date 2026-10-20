package com.etip.booking.service;

import com.etip.booking.dto.BookingEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class BookingEventProducer {
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;
    private static final String topic = "booking-events";

    public void publishBookingCreated(BookingEvent bookingEvent){
        kafkaTemplate.send(topic, bookingEvent.getBookingId(), bookingEvent);
    }
}
