package io.github.atsin.dashr.strategies.impl;

import io.github.atsin.dashr.entities.Driver;
import io.github.atsin.dashr.entities.Payment;
import io.github.atsin.dashr.entities.Rider;
import io.github.atsin.dashr.entities.enums.PaymentStatus;
import io.github.atsin.dashr.entities.enums.TransactionMethod;
import io.github.atsin.dashr.repositories.PaymentRepository;
import io.github.atsin.dashr.services.PaymentService;
import io.github.atsin.dashr.services.WalletService;
import io.github.atsin.dashr.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//Drivers wallet = 123Rs (before payment)
//Rider wallet = 230Rs (before payment)

//Ride cost = 100Rs (deducted from Rider's wallet)

//Commission = 100*0.3 = 30Rs
//costAfterCommission = 100 - 30 = 70Rs

//Drivers wallet = 123+70 = 193Rs  (after payment)
//Riders wallet = 130Rs (after payment)

@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
//    private final PaymentService paymentService;
    // I commented above line because I face dependency cycle between beans
    //paymentServiceImpl -> paymentServiceImpl -> walletPaymentStrategy
    // ^                                                v
    // ^    <-  <-          <-  <-  <-          <-  <-  v
    // so there are 2 ways to encounter this
    // 1. First is to create another service because PaymentService is circular dependent it is updating Payment Status and also getting PaymentStrategy
    // 2. And the second is to use repository instead of Payment
    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(Payment payment) {

        //Getting driver and driver using the Ride
        Driver driver = payment.getRide().getDriver();
        Rider rider = payment.getRide().getRider();

        //deducting the balance from rider's wallet
        walletService.deductMoneyFromWallet(
                rider.getUser(),
                payment.getAmount(),
                null,
                payment.getRide(),
                TransactionMethod.RIDE);

        //calculating commission
        double commission = payment.getAmount() * PLATFORM_COMMISSION;

        //adding the money to the driver's wallet
        walletService.addMoneyToWallet(
                driver.getUser(),
                payment.getAmount()-commission,
                null,
                payment.getRide(),
                TransactionMethod.RIDE
        );

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}
