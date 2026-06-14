package io.github.atsin.dashr.dto;

import io.github.atsin.dashr.entities.User;
import io.github.atsin.dashr.entities.WalletTransaction;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class WalletDto {

    private Long Id;

    private UserDto userDto;

    private Double balance;

    private List<WalletTransactionDto> transactions;
}
