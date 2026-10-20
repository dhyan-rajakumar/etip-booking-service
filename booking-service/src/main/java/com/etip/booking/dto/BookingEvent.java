package com.etip.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@RequiredArgsConstructor

public class BookingEvent {
    private String bookingId;
    private String eventName;
    private String customerName;
    private String source;
    private String destination;

}
