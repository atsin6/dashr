package io.github.atsin.dashr.services.impl;

import io.github.atsin.dashr.entities.Payment;
import io.github.atsin.dashr.entities.Ride;
import io.github.atsin.dashr.entities.enums.PaymentMethod;
import io.github.atsin.dashr.entities.enums.PaymentStatus;
import io.github.atsin.dashr.exceptions.ResourceNotFoundException;
import io.github.atsin.dashr.repositories.PaymentRepository;
import io.github.atsin.dashr.services.PaymentService;
import io.github.atsin.dashr.strategies.PaymentStrategyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyManager paymentStrategyManager;

    @Override
    public void processPayment(Ride ride) {
        Payment payment = paymentRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for Ride with id: " + ride.getId()));
        paymentStrategyManager
                .paymentStrategy(payment.getPaymentMethod()) //calling payment strategy acc. to payment method
                .processPayment(payment); // this method belongs to PaymentStrategy
    }

    @Override
    public Payment creatNewPayment(Ride ride) {
        Payment payment = Payment.builder()
                .paymentMethod(ride.getPaymentMethod())
                .ride(ride)
                .amount(ride.getFare())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    public void updatePaymentStatus(Payment payment, PaymentStatus paymentStatus) {
        payment.setPaymentStatus(paymentStatus);
    }
}
