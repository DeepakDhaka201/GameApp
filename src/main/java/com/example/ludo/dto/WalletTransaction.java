package com.example.ludo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class WalletTransaction {
    private BigDecimal amount;
    private Long timestamp;
    private String id;
    private String status;
    private BigDecimal closingBalance;
    private WalletTransactionType type;
    private String mode;
}