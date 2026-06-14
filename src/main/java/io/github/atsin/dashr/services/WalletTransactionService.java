package io.github.atsin.dashr.services;

import io.github.atsin.dashr.entities.WalletTransaction;

public interface WalletTransactionService {

    void createNewWalletTransaction(WalletTransaction walletTransaction);
}
