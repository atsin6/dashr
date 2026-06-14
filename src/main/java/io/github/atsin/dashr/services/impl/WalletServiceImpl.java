package io.github.atsin.dashr.services.impl;

import io.github.atsin.dashr.dto.RideDto;
import io.github.atsin.dashr.dto.WalletDto;
import io.github.atsin.dashr.dto.WalletTransactionDto;
import io.github.atsin.dashr.entities.Ride;
import io.github.atsin.dashr.entities.User;
import io.github.atsin.dashr.entities.Wallet;
import io.github.atsin.dashr.entities.WalletTransaction;
import io.github.atsin.dashr.entities.enums.TransactionMethod;
import io.github.atsin.dashr.entities.enums.TransactionType;
import io.github.atsin.dashr.exceptions.ResourceNotFoundException;
import io.github.atsin.dashr.repositories.WalletRepository;
import io.github.atsin.dashr.services.WalletService;
import io.github.atsin.dashr.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl  implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, double amount, String transactionId, Ride ride, TransactionMethod transactionMethod) {
        //get user's wallet
        Wallet wallet = findByUser(user);

        //updating balance to the user's wallet
        wallet.setBalance(wallet.getBalance() + amount);

        //creating the walletTransaction entity. AND
        // Putting all necessary details in this entity
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .transactionType(TransactionType.CREDIT)
                .transactionMethod(transactionMethod)
                .amount(amount)
                .build();

        //creating a new wallet transaction using previously created entity
        walletTransactionService.createNewWalletTransaction(walletTransaction);

        //Saving the wallet with updated balance
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet deductMoneyFromWallet(User user, double amount, String transactionId, Ride ride, TransactionMethod transactionMethod) {
        //get user's wallet
        Wallet wallet = findByUser(user);

        //updating balance to the user's wallet
        wallet.setBalance(wallet.getBalance() - amount);

        //creating the walletTransaction entity. AND
        // Putting all necessary details in this entity
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .transactionType(TransactionType.DEBIT)
                .transactionMethod(transactionMethod)
                .amount(amount)
                .build();

        //creating a new wallet transaction using previously created entity
        walletTransactionService.createNewWalletTransaction(walletTransaction);
//        wallet.getTransactions().add(walletTransaction);

        //Saving the wallet with updated balance

        return walletRepository.save(wallet);
    }

    @Override
    public void withdrawAllMoneyFromWallet(Long userId, Wallet wallet) {

    }

    @Override
    public Wallet getMyWallet(Long walletId) {
        return walletRepository.findById(walletId).orElseThrow(
                () -> new ResourceNotFoundException("Wallet noy found with id: " + walletId)
        );
    }

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findByUser(User user) {
        //finding wallet by user
        //Wallet is in one-to-one relationship with User so wallet can be find by user
        //for that I create a method "findByUser(user)" in WalletRepository
        //to find wallet by giving user
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + user.getId()));
    }
}
