package com.etip.booking.service;

import com.etip.booking.dto.PaymentRequest;
import com.etip.booking.dto.PaymentResponse;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class PaymentClient {
    private final RestClient restClient;

    public PaymentResponse makePayment(PaymentRequest paymentRequest){
        return restClient.post().uri("http://localhost:8081/payments/v1/processPayment").body(paymentRequest).retrieve().body(PaymentResponse.class);
    }
    public PaymentResponse refundPayment(String paymentId, String idempotencyKey){
        return restClient.post().uri("http://localhost:8081/payments/v1/{paymentId}/refund", paymentId).header("Idempotency-key", idempotencyKey).retrieve().body(PaymentResponse.class);
    }
}
