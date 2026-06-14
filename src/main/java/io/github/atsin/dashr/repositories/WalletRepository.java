package io.github.atsin.dashr.repositories;

import io.github.atsin.dashr.entities.User;
import io.github.atsin.dashr.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
}
