package io.github.atsin.dashr.services;


import io.github.atsin.dashr.entities.Ride;
import io.github.atsin.dashr.entities.User;
import io.github.atsin.dashr.entities.Wallet;
import io.github.atsin.dashr.entities.enums.TransactionMethod;

public interface WalletService {

    Wallet addMoneyToWallet(User user, double amount, String transactionId, Ride ride, TransactionMethod transactionMethod);

    Wallet deductMoneyFromWallet(User user, double amount, String transactionId, Ride ride, TransactionMethod transactionMethod);

    void withdrawAllMoneyFromWallet(Long userId, Wallet wallet);

    Wallet getMyWallet(Long userId);

    Wallet createNewWallet(User user);

    Wallet findByUser(User user);
}
