package io.github.atsin.dashr.services;

import io.github.atsin.dashr.entities.Payment;
import io.github.atsin.dashr.entities.Ride;
import io.github.atsin.dashr.entities.enums.PaymentStatus;

public interface PaymentService {
    void processPayment(Ride ride);

    Payment creatNewPayment(Ride ride);

    void updatePaymentStatus(Payment payment, PaymentStatus paymentStatus);
}
