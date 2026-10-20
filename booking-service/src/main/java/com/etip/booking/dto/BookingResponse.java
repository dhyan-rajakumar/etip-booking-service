package com.etip.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private String bookingId;
    private String customerName;
    private String source;
    private String destination;
    private LocalDate travelDate;
    private String bookingStatus;
    private BigDecimal amount;
}
