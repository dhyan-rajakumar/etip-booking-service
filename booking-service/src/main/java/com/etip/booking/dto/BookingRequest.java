package com.etip.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class BookingRequest {

    @NotBlank(message="Customer name is mandatory!!")
    @Size(min=3, max=100, message="Not a valid name!")
    private String customerName;
    @NotBlank(message="Mandatory fields missing - source")
    private String source;
    @NotBlank(message="Mandatory fields missing - destination")
    private String destination;
    @NotNull(message="Mandatory fields missing - travelDate")
    @Future(message="Travel date cannot be in past")
    private LocalDate travelDate;

    public BookingRequest() {
    }

    public String getCustomerName() {
        return customerName;
    }


    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }
}
