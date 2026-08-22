package com.cryptotrading.Crypto.Trading.App.service;

import com.cryptotrading.Crypto.Trading.App.model.dto.CheckoutSessionResponse;
import com.stripe.exception.StripeException;

import java.math.BigDecimal;

public interface PaymentService {
    CheckoutSessionResponse createCheckoutSession(String email, BigDecimal amount) throws StripeException;

    void handleWebhookEvent(String payload, String sigHeader);
}
