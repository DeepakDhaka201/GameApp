package com.example.ludo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceSummary {
    private Double balance;
    private Double onHold;
    private Double totalWinning;
    private Double totalLost;
    private Double referralEarning;
}