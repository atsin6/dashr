package io.github.atsin.dashr.strategies;

import io.github.atsin.dashr.entities.Payment;

public interface PaymentStrategy {

    Double PLATFORM_COMMISSION = 0.3;

    void processPayment(Payment payment);
}
