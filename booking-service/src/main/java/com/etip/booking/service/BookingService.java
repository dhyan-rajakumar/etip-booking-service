package com.etip.booking.service;

import com.etip.booking.aspect.Audit;
import com.etip.booking.dto.BookingRequest;
import com.etip.booking.dto.BookingResponse;
import com.etip.booking.dto.PaymentRequest;
import com.etip.booking.dto.PaymentResponse;
import com.etip.booking.entity.Booking;
import com.etip.booking.exception.BookingNotFoundException;
import com.etip.booking.exception.InvalidBookingException;
import com.etip.booking.repository.BookingRepository;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class BookingService {
    private static final Logger log =
            LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final PaymentClient paymentClient;
    private final RedisTemplate<String, BookingResponse> redisTemplate;
    private static final String BOOKING_CACHE_PREFIX = "booking:";


    @Audit(operation = "CREATE_BOOKING", logRequest = true, logResponse = true)
    public BookingResponse createBooking(BookingRequest request){
        log.info("Creating booking for customer={}, source={}, destination={}",
                request.getCustomerName(), request.getSource(), request.getDestination());

        if(request.getSource().equalsIgnoreCase(request.getDestination())){
            throw new InvalidBookingException("Source and destination are same!!");
        }

        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        PaymentRequest paymentRequest = new PaymentRequest();
        PaymentResponse paymentResponse = new PaymentResponse();

        booking.setBookingId("TC-"+System.currentTimeMillis());
        booking.setCustomerName(request.getCustomerName());
        booking.setSource(request.getSource());
        booking.setDestination(request.getDestination());
        booking.setTravelDate(request.getTravelDate());
        booking.setBookingStatus("PENDING");
        booking.setAmount(BigDecimal.valueOf(26000));

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created - bookingID={}",savedBooking.getBookingId());

        paymentRequest.setBookingId(booking.getBookingId());
        paymentRequest.setPaymentMode("CARD");
        paymentRequest.setAmount(booking.getAmount());

        paymentResponse = paymentClient.makePayment(paymentRequest);

        log.info("Payment response, paymentId = {}, status={}", paymentResponse.getPaymentId(), paymentResponse.getPaymentStatus());

//        throw new RuntimeException("Simulating booking error after payment succeeds");
        paymentResponse = paymentClient.refundPayment(paymentResponse.getPaymentId(), booking.getBookingId());

        if("success".equalsIgnoreCase(paymentResponse.getPaymentStatus())){
            booking.setBookingStatus("COMPLETED");
            bookingRepository.save(booking);
        }else{
            booking.setBookingStatus("FAILED");
            bookingRepository.save(booking);
        }


        bookingResponse.setBookingId(savedBooking.getBookingId());
        bookingResponse.setCustomerName(savedBooking.getCustomerName());
        bookingResponse.setSource(savedBooking.getSource());
        bookingResponse.setDestination(savedBooking.getDestination());
        bookingResponse.setTravelDate(savedBooking.getTravelDate());
        bookingResponse.setBookingStatus(savedBooking.getBookingStatus());
        bookingResponse.setAmount(savedBooking.getAmount());

//        throw new RuntimeException("Simulating booking error after payment succeeds");

        return bookingResponse;
    }
    @Audit(operation = "UPDATE_BOOKING", logRequest = true, logResponse = true)
    public BookingResponse updateBooking(BookingResponse request, String bookingId){
        log.info("Update booking for customer={}, source={}, destination={}",
                request.getCustomerName(), request.getSource(), request.getDestination());

        redisTemplate.opsForValue().getAndDelete(BOOKING_CACHE_PREFIX+bookingId);

        if(request.getSource().equalsIgnoreCase(request.getDestination())){
            throw new InvalidBookingException("Source and destination are same!!");
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new BookingNotFoundException("Booking not found "+request.getBookingId()));
        BookingResponse bookingResponse = new BookingResponse();

        booking.setBookingId(bookingId);
        booking.setCustomerName(request.getCustomerName());
        booking.setSource(request.getSource());
        booking.setDestination(request.getDestination());
        booking.setTravelDate(request.getTravelDate());
        booking.setBookingStatus("CNF");
        booking.setAmount(BigDecimal.valueOf(26000));

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking updated - bookingID={}",bookingId);

        bookingResponse.setBookingId(bookingId);
        bookingResponse.setCustomerName(savedBooking.getCustomerName());
        bookingResponse.setSource(savedBooking.getSource());
        bookingResponse.setDestination(savedBooking.getDestination());
        bookingResponse.setTravelDate(savedBooking.getTravelDate());
        bookingResponse.setBookingStatus(savedBooking.getBookingStatus());
        bookingResponse.setAmount(savedBooking.getAmount());

        return bookingResponse;
    }

    @Audit(operation = "UPDATE_DESTINATION", logRequest = true, logResponse = true)
    @Transactional(rollbackFor = Exception.class)
    public BookingResponse updateDestination(String bookingId, String destination) throws Exception {
        log.info("Update destination for booking id = {}",bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new BookingNotFoundException("Booking not found "+bookingId));

        if(booking.getSource().equalsIgnoreCase(destination)){
            throw new InvalidBookingException("Source and destination are same!!");
        }
        booking.setDestination(destination);
        BookingResponse bookingResponse = new BookingResponse();

        bookingResponse.setBookingId(bookingId);
        bookingResponse.setCustomerName(booking.getCustomerName());
        bookingResponse.setSource(booking.getSource());
        bookingResponse.setDestination(booking.getDestination());
        bookingResponse.setTravelDate(booking.getTravelDate());
        bookingResponse.setBookingStatus(booking.getBookingStatus());
        bookingResponse.setAmount(booking.getAmount());


        return bookingResponse;
    }

    @Audit(operation = "GET-BOOKING_BY_ID", logRequest = true, logResponse = true)
    public BookingResponse getBookingById(String bookingId){
        log.info("get booking for bookingId={}",bookingId);

        String cacheKey = BOOKING_CACHE_PREFIX+bookingId;
        BookingResponse bookingResponse = new BookingResponse();

        BookingResponse cachedBooking = redisTemplate.opsForValue().get(cacheKey);
        if(cachedBooking != null){
            return cachedBooking;
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new BookingNotFoundException("Booking not found "+bookingId));

        bookingResponse.setBookingId(bookingId);
        bookingResponse.setCustomerName(booking.getCustomerName());
        bookingResponse.setSource(booking.getSource());
        bookingResponse.setDestination(booking.getDestination());
        bookingResponse.setTravelDate(booking.getTravelDate());
        bookingResponse.setBookingStatus(booking.getBookingStatus());
        bookingResponse.setAmount(booking.getAmount());

        redisTemplate.opsForValue().set(cacheKey, bookingResponse, Duration.ofMinutes(10));

        return bookingResponse;
    }
    @Audit(operation = "DELETE_BOOKING_BY_ID", logRequest = true, logResponse = true)
    public void deleteBookingById(String bookingId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new BookingNotFoundException("Booking not found "+bookingId));
        bookingRepository.deleteById(bookingId);
        redisTemplate.opsForValue().getAndDelete(BOOKING_CACHE_PREFIX+bookingId);
    }
    @Transactional
    public BookingResponse MethodA(String bookingId){
        log.info("Inside Method A");
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new BookingNotFoundException("Booking not found "+ bookingId));
        booking.setDestination("METHOD-A");
        MethodB(bookingId);
        return null;
    }
    @Transactional
    public void MethodB(String bookingId){
        log.info("Inside Method B");
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new BookingNotFoundException("Booking not found "+ bookingId));
        booking.setDestination("METHOD-B");
        throw new RuntimeException("test Exception in Method B");
    }
}
