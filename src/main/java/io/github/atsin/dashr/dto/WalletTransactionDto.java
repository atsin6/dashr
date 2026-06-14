package io.github.atsin.dashr.dto;

import io.github.atsin.dashr.entities.Ride;
import io.github.atsin.dashr.entities.Wallet;
import io.github.atsin.dashr.entities.enums.TransactionMethod;
import io.github.atsin.dashr.entities.enums.TransactionType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Data
@Builder
public class WalletTransactionDto {

    private Long Id;

    private Double amount;

    private TransactionType transactionType;

    private TransactionMethod transactionMethod;

    private RideDto ride;

    private String transactionId;

    private WalletDto wallet;

    private LocalDateTime timeStamp;
}
