package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.model.dto.CheckoutSessionResponse;
import com.cryptotrading.Crypto.Trading.App.model.entity.PaymentTransaction;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.model.enums.PaymentStatus;
import com.cryptotrading.Crypto.Trading.App.repo.PaymentTransactionRepository;
import com.cryptotrading.Crypto.Trading.App.service.PaymentService;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("1.00");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000.00");
    private static final BigDecimal CENTS_PER_DOLLAR = BigDecimal.valueOf(100);

    private final UserService userService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public PaymentServiceImpl(UserService userService, PaymentTransactionRepository paymentTransactionRepository) {
        this.userService = userService;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Override
    public CheckoutSessionResponse createCheckoutSession(String email, BigDecimal amount) throws StripeException {
        BigDecimal validatedAmount = validateAndNormalizeAmount(amount);

        User user = userService.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        long amountInCents = validatedAmount.multiply(CENTS_PER_DOLLAR).longValueExact();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setClientReferenceId(String.valueOf(user.getId()))
                .putMetadata("userId", String.valueOf(user.getId()))
                .setSuccessUrl(frontendBaseUrl + "/add-funds/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendBaseUrl + "/add-funds?canceled=true")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Virtual balance top-up - $" + validatedAmount.toPlainString())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setUser(user);
        paymentTransaction.setStripeSessionId(session.getId());
        paymentTransaction.setAmount(validatedAmount);
        paymentTransaction.setStatus(PaymentStatus.PENDING);
        paymentTransaction.setCreatedAt(LocalDateTime.now());
        paymentTransactionRepository.save(paymentTransaction);

        return new CheckoutSessionResponse(session.getUrl());
    }

    private BigDecimal validateAndNormalizeAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Amount must have at most 2 decimal places");
        }

        BigDecimal normalized = amount.setScale(2, RoundingMode.UNNECESSARY);

        if (normalized.compareTo(MIN_AMOUNT) < 0) {
            throw new IllegalArgumentException("Amount must be at least $1.00");
        }
        if (normalized.compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("Amount must not exceed $10,000.00");
        }

        return normalized;
    }

    @Override
    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new IllegalArgumentException("Invalid Stripe webhook signature");
        }

        if (!"checkout.session.completed".equals(event.getType())) {
            return;
        }

        StripeObject stripeObject;
        try {
            stripeObject = event.getDataObjectDeserializer().deserializeUnsafe();
        } catch (EventDataObjectDeserializationException e) {
            log.warn("Stripe webhook: failed to deserialize checkout.session.completed data object (event id={})", event.getId());
            throw new IllegalStateException("Unable to deserialize checkout.session.completed payload", e);
        }

        if (!(stripeObject instanceof Session session)) {
            log.warn("Stripe webhook: checkout.session.completed data object was not a Checkout Session (event id={})", event.getId());
            throw new IllegalStateException("checkout.session.completed payload did not contain a Checkout Session");
        }

        PaymentTransaction paymentTransaction = paymentTransactionRepository
                .findByStripeSessionId(session.getId())
                .orElse(null);

        if (paymentTransaction == null || paymentTransaction.getStatus() == PaymentStatus.COMPLETED) {
            return;
        }

        User user = paymentTransaction.getUser();
        String expectedUserId = String.valueOf(user.getId());
        if (session.getClientReferenceId() != null && !expectedUserId.equals(session.getClientReferenceId())) {
            throw new IllegalStateException("Checkout session client_reference_id does not match stored payment transaction user");
        }

        paymentTransaction.setStatus(PaymentStatus.COMPLETED);
        paymentTransaction.setProcessedAt(LocalDateTime.now());
        paymentTransactionRepository.save(paymentTransaction);

        userService.creditBalance(user.getEmail(), paymentTransaction.getAmount());
    }
}
