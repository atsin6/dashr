package io.github.atsin.dashr.strategies.impl;

import io.github.atsin.dashr.entities.Driver;
import io.github.atsin.dashr.entities.Payment;
import io.github.atsin.dashr.entities.enums.PaymentStatus;
import io.github.atsin.dashr.entities.enums.TransactionMethod;
import io.github.atsin.dashr.repositories.PaymentRepository;
import io.github.atsin.dashr.services.WalletService;
import io.github.atsin.dashr.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
// IF
// Rider is charged 100 Rs for any ride
// The driver will get only 70Rs
// 30Rs is my commission (Paisa hi Paisa hoga babu bahiya)

@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
//    private final PaymentService paymentService;
// I commented above line because I face dependency cycle between beans
//paymentServiceImpl -> paymentServiceImpl -> walletPaymentStrategy
// ^                                                v
// ^    <-  <-          <-  <-  <-          <-  <-  v
// so there are 2 ways to encounter this
// 1. First is to create another service because PaymentService is circular dependent it is updating Payment Status and also getting PaymentStrategy
// 2. And the second is to use repository instead of Payment (It is not a good practice)
    private final PaymentRepository paymentRepository;
    @Override
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();
        double platformCommission = payment.getAmount() * PLATFORM_COMMISSION;

        walletService.deductMoneyFromWallet(driver.getUser(),
                platformCommission,
                null,
                payment.getRide(),
                TransactionMethod.RIDE);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}
